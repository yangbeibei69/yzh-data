/**
 * History:
 * <author>         <time>           <version>
 * 杨子浩      2020/1/7 15:37     1.？.?
 */
package com.yzh.core;


/**
 * Copyright (C),2020
 * FileName: Xx
 *
 * @author 杨子浩
 * @version 1.0.0
 * @Date 2020/1/7 15:37
 * 描述: 数据权限SQL元数据
 * @since 1.0
 */
public class Xx {



    private final String IN_SQL = " in ( ";

    private final String STOP_SQL = " ) ";

    private final String AND_SQL = " ) and ";

    /* 数据权限的最终SQL段 */
    private String EMPOWERMENT_SQL;

    private String FLAG;

    //是否自动拼接(简单SQL采用自动拼接)
    private Boolean isAuto = false;



    public String getEMPOWERMENT_SQL() {
        return EMPOWERMENT_SQL;
    }

    public void setEMPOWERMENT_SQL(String EMPOWERMENT_SQL) {
        this.EMPOWERMENT_SQL = EMPOWERMENT_SQL;
    }

    public String getFLAG() {
        return FLAG;
    }

    public void setFLAG(String FLAG) {
        this.FLAG = FLAG;
    }

    public Boolean getAuto() {
        return isAuto;
    }

    public void setAuto(Boolean auto) {
        isAuto = auto;
    }

    public Boolean getWhere() {
        return isWhere;
    }

    public void setWhere(Boolean where) {
        isWhere = where;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    //是否存在条件
    private Boolean isWhere = false;

    //表别名(注意:此表别名代表的是查询数据权限时的主表别名)
    private String tableAlias;

    //关键字 非常复杂的SQL建议采用后置关键字
    private String keyword;






    /**
     * 自动且无别名
     * @param flagName 查询主表的数据权限flag字段名
     */
    public Xx(String flagName) {
        this.isAuto = true;
        this.FLAG = flagName;
        // WHERE FLAG_NAME IN (
        this.EMPOWERMENT_SQL = " WHERE "+ FLAG +this.IN_SQL;
    }

    /**
     * 自动且存在别名
     * @param flagName 查询主表的数据权限flag字段名
     * @param tableAlias 查询数据权限flag的主表别名
     */
    public Xx(String flagName,String tableAlias) {
        this.isAuto = true;
        this.FLAG = flagName;
        this.tableAlias = tableAlias;
        // WHERE tableAlias.FLAG_NAME IN (
        this.EMPOWERMENT_SQL = " WHERE "+ tableAlias+"."+FLAG +this.IN_SQL;
    }

    /**
     * 复杂SQL且无别名
     * @param flagName  查询主表的数据权限flag字段名
     * @param isAuto    采用此构造函数时,默认此字段使用false
     * @param keyword   后置关键字
     */
    public Xx(String flagName,Boolean isAuto,String keyword) {
        this.FLAG = flagName;
        this.isAuto = false;
        this.keyword = keyword;
        // WHERE FLAG_NAME IN (
        this.EMPOWERMENT_SQL = " WHERE "+ FLAG +this.IN_SQL;
    }

    /**
     * 复杂SQL且有别名
     * @param flagName      查询主表的数据权限flag字段名
     * @param isAuto        采用此构造函数时,默认此字段使用false
     * @param keyword       后置关键字
     * @param tableAlias    查询数据权限flag的主表别名
     */
    public Xx(String flagName,Boolean isAuto, String keyword, String tableAlias) {
        this.FLAG = flagName;
        this.isAuto = false;
        this.keyword = keyword;
        this.tableAlias = tableAlias;
        // WHERE tableAlias.FLAG_NAME IN (
        this.EMPOWERMENT_SQL = " WHERE "+ tableAlias+"."+FLAG +this.IN_SQL;
    }

    /**
     * 自动
     * 针对如下构造函数创建的Xx对象
     * public Xx(Integer type)
     * public Xx(String tableName, String tableAlias,Integer type)
     * @param oldSQL
     * @param flagIds
     * @param isWhere
     * @return
     */
    public String getAuthorizationSQL(String oldSQL, String flagIds, boolean isWhere) {
        String newSQL = null;
        if (isWhere) {
            newSQL = oldSQL.replace("WHERE", this.EMPOWERMENT_SQL + flagIds + this.AND_SQL);
        } else {
            newSQL = oldSQL.concat(this.EMPOWERMENT_SQL + flagIds + this.STOP_SQL);
        }
        return newSQL;
    }

    /**
     * 手动
     * 针对如下构造函数创建的Xx对象
     * public Xx(Boolean isAuto, String keyword, String tableAlias)
     * public Xx(Boolean isAuto,String keyword,Integer type)
     * @param oldSQL
     * @param flagIds
     * @param isWhere
     * @return
     */
    public String getAuthorization(String oldSQL, String flagIds, boolean isWhere) {
        String newSQL = null;
            if (isWhere) {
                newSQL = oldSQL.replace("WHERE", this.EMPOWERMENT_SQL + flagIds + AND_SQL);
            } else {
                newSQL = oldSQL.replace(this.keyword, this.EMPOWERMENT_SQL + flagIds + STOP_SQL + this.keyword + " ");
            }
        return newSQL;
    }




}
