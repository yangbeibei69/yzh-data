/**
 * History:
 * <author>         <time>           <version>
 * 杨子浩      2020/1/3 15:48     1.0.1
 */
package com.yzh.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Copyright (C),2020
 * FileName: AuthorizationConfig
 * @author 杨子浩
 * @since 1.0
 * @version 1.0.1
 * @Date 2020/1/3 15:48
 * 描述: 数据权限配置类
 */
@ConfigurationProperties(prefix = "data-permission")
public class AuthorizationConfig {

    /* 是否开启数据权限 */
    private Boolean toDataPermission = false;

    /* 是否开启字段数据权限 */
    private Boolean toFieldPermission = false;


    public Boolean getToDataPermission() {
        return toDataPermission;
    }

    public void setToDataPermission(Boolean toDataPermission) {
        this.toDataPermission = toDataPermission;
    }

    public Boolean getToFieldPermission() {
        return toFieldPermission;
    }

    public void setToFieldPermission(Boolean toFieldPermission) {
        this.toFieldPermission = toFieldPermission;
    }
}