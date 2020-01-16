package com.yzh.core;

import java.util.Map;

/**
 @author 杨子浩
 @since 2019/12/2
 mapper接口 权限定义类对象 */
public interface InterfaceDefinition {

    /*
     * 需要增权的SQL放入map中,map<SQL的命名空间+id,元数据>
     * com.yzh.controller.PoliceDao.selectList
     * */
    Map<String, Xx> getDefinitionMap();

    /**
     * @return 当前登陆系统用户的数据权限标识flag
     */
    String getFlag();

//    /**
//     * map<SQL的命名空间+id,字段名(,拼接)>
//     * @return 当前登陆系统用户的所能查看的字段
//     */
//    Map<String,String> getField();
}

