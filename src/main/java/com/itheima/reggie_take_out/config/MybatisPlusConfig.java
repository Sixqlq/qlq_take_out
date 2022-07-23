package com.itheima.reggie_take_out.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置MP的分页插件
 * 配置类里面使用@Bean标注在方法上给容器注册组件，默认也是单实例的
 */
@Configuration  // 告诉springBoot这是一个配置类 == 配置文件
public class MybatisPlusConfig {

    @Bean // 给容器中添加组件，以方法名作为组件的id。返回类型为组件类型，返回的值，就是组件在容器中的实例
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }
}
