package com.yzh.core;

import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

/**
 @author 杨子浩
 @since 2019/11/28 */

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataPermission implements Interceptor {

    protected final Log logger = LogFactory.getLog(getClass());

    /* 封装了definitionMap */
    private InterfaceDefinition interfaceDefinition;

    /* 作为配置存在,保存需要做数据权限的sql信息 */
    private Map<String, Xx> definitionMap;


    public DataPermission(InterfaceDefinition interfaceDefinition) {
        this.interfaceDefinition = interfaceDefinition;
    }

    /**
     拦截到对象后所进行操作的位置，也就是我们之后编写逻辑代码的位置
     @param invocation
     @return
     @throws Throwable intercept方法中的Invocation类的属性
     target         //所拦截到的目标的代理
     method         //所拦截目标的具体方法
     args           //方法的参数
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        //获取拦截到的执行器
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        //反射获取
        MetaObject statementHandler = SystemMetaObject.forObject(handler);
        MappedStatement mappedStatement = (MappedStatement) statementHandler.getValue("delegate.mappedStatement");

        //初始化配置map
        if (definitionMap == null) {
            RandomUtil.loggerout(RandomUtil.TO_THE_LOAD_TYPE,1);
            definitionMap = interfaceDefinition.getDefinitionMap();
            RandomUtil.loggerout(RandomUtil.TO_THE_LOAD_TYPE,3);
            if (definitionMap == null || definitionMap.size() == 0) {
                RandomUtil.loggerout(RandomUtil.TO_THE_LOAD_TYPE,2);
                return invocation.proceed();
            }

            RandomUtil.loggerout(RandomUtil.TO_THE_LOAD_TYPE,4);
        }

        //获取方法id
        String methodId = mappedStatement.getId();
        logger.info("==>  MethodId: "+methodId);

        //如果当前方法id存在配置map中，此条方法sql是需要增权的
        Xx xx = definitionMap.get(methodId);
        if (xx != null) {
            String flagIds = interfaceDefinition.getFlag();

            //获取原始sql
            BoundSql boundSql = handler.getBoundSql();
            String oldSQL = boundSql.getSql();
            logger.info("==> BeforeSQL: "+oldSQL);

            //增权后SQL
            String newSQL = null;

            //自动增权（待优化）
            if (xx.getAuto()) {
                newSQL = this.isAuto(oldSQL,xx,flagIds);
            } else { //手动增权-回调函数（待优化）
                newSQL = this.isNotAuto(oldSQL,xx,flagIds);
            }
            //将sql放回
            logger.info("<==  AfterSQL: "+newSQL+"\r\n");
            statementHandler.setValue("delegate.boundSql.sql", newSQL);
        }
        //返回执行器中执行SQL
        return invocation.proceed();
    }


    /**
     * 自动增权
     * 目前只支持一层where查询条件
     * 待优化(如果存在多重WHERE该怎么办呢?)
     * @param oldSQL
     * @param xx
     * @param flagIds
     * @return
     */
    private String isAuto(String oldSQL, Xx xx, String flagIds){
        String newSQL = null;
            if (sqlIsWhere(oldSQL)) {
                newSQL = xx.getAuthorizationSQL(oldSQL, flagIds, true);
            } else {
                newSQL = xx.getAuthorizationSQL(oldSQL, flagIds, false);
            }
        return newSQL;
    }

    /**
     * 手动增权
     * @param oldSQL
     * @param xx
     * @param flagIds
     * @return
     */
    private String isNotAuto(String oldSQL, Xx xx, String flagIds){
        String newSQL = null;
        if (sqlIsWhere(oldSQL)) {
            newSQL = xx.getAuthorization(oldSQL,flagIds,true);
        }else {
            newSQL = xx.getAuthorization(oldSQL,flagIds,false);
        }
        return newSQL;
    }

    /**
     拦截我们需要拦截到的对象。
     @param o
     @return
     */
    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }


    /**
     通过配置文件中进行properties配置,然后在该方法中读取到配置
     @param properties
     */
    @Override
    public void setProperties(Properties properties) {
    }

    /**
     SQL中是否存在条件查询
     @param oldSQL 增权前的sql串
     */
    private boolean sqlIsWhere(String oldSQL) {
        //mysql传输器解析
        MySqlStatementParser mySqlStatementParser = new MySqlStatementParser(oldSQL);
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) mySqlStatementParser.parseSelect();
        //查询对象
        SQLSelect sqlSelect = sqlSelectStatement.getSelect();
        SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
        if (sqlSelectQuery instanceof MySqlSelectQueryBlock) {
            MySqlSelectQueryBlock mySqlSelectQueryBlock = (MySqlSelectQueryBlock) sqlSelectQuery;
            //判断是否存在where条件
            if (mySqlSelectQueryBlock.getWhere() != null) return true;
        }
        return false;
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException {
        String old = "select id,dep_id,dep_name,dept_flag,user_name,personnel_code,department_code,mechanism_code," + "sex,birth_date,nation,place_type,home_address,health_type,document_type,idcard,education," + "isolation_start_date,isolation_end_date,deciding_organ,ringgiving_unit,whereto_receive," + "entry_date,narcotics_type,abusingroute,drugyears,disability,threefalse,threeno,drug_state," + "periodization,level_id,employment,marital_status,criminal_record,drug_type, " + "words_type,martial_type,peasant,managementstyle,account_card,out_date,file_number," + "policestation,contact_name,contact_number,contact_relationship,facepictures_url," + "enabled,remarks FROM sys_user u,sys_xx x WHERE dept_flag in " + "(select dept_flag from sys_role_dept_flag where role_id in ( 8) ) " + "and   idcard LIKE '%%' AND user_type = '0' LIMIT 1,10 ";
        MySqlStatementParser mySqlStatementParser = new MySqlStatementParser(old);
        SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) mySqlStatementParser.parseSelect();

        SQLSelect sqlSelect = sqlSelectStatement.getSelect();
        SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
        if (sqlSelectQuery instanceof MySqlSelectQueryBlock) {
            MySqlSelectQueryBlock mySqlSelectQueryBlock = (MySqlSelectQueryBlock) sqlSelectQuery;
            MySqlOutputVisitor where = new MySqlOutputVisitor(new StringBuilder());
            // 获取where 条件
            System.out.println(mySqlSelectQueryBlock.getWhere() == null);
            mySqlSelectQueryBlock.getWhere().accept(where);
            System.out.println("##########where（条件查询）###############");
            System.out.println(where.getAppender());
            // 获取表名
            System.out.println("############table_name（表名）##############");
            MySqlOutputVisitor tableName = new MySqlOutputVisitor(new StringBuilder());
            mySqlSelectQueryBlock.getFrom().accept(tableName);
            System.out.println(tableName.getAppender());

            //   获取查询字段
            System.out.println("############（查询字段）##############");
            System.out.println(mySqlSelectQueryBlock.getSelectList());
        }


    }

}
