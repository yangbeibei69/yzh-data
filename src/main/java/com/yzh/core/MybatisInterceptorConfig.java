/**
 * History:
 * <author>         <time>           <version>
 * 杨子浩      2020/1/7 11:29     1.？.?
 */
package com.yzh.core;

import com.yzh.core.DataPermission;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),2020
 * FileName: MybatisInterceptorConfig
 * @author 杨子浩
 * @since 1.0
 * @version 1.0.0
 * @Date 2020/1/7 11:29
 * 描述: mybatis 拦截器配置类
 */
@Configuration
public class MybatisInterceptorConfig {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired(required = false)
    private InterfaceDefinition interfaceDefinition;

    /**
     * 添加自定义拦截器
     * 只有当data-permission.to-data-permission配置为true时,才会添加拦截器
     * @return
     */
//    @ConditionalOnProperty(value = "data-permission.to-data-permission",matchIfMissing = true)
//    @ConditionalOnProperty(value = {"data-permission.to-data-permission","data-permission.to-field-permission"},havingValue = "true")
    @ConditionalOnExpression("${data-permission.to-data-permission:true} || ${data-permission.to-field-permission:true}")
    @Bean
    ConfigurationCustomizer mybatisConfigurationCustomizer(){
        RandomUtil.loggerout(RandomUtil.INITIALIZE_TYPE,1);

        return new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                RandomUtil.loggerout(RandomUtil.INITIALIZE_TYPE,2);
                configuration.addInterceptor(new DataPermission(interfaceDefinition));
                RandomUtil.loggerout(RandomUtil.INITIALIZE_TYPE,3);
            }
        };
    }


}