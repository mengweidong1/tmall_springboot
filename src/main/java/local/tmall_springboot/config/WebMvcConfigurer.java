package local.tmall_springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import local.tmall_springboot.interceptor.LoginInterceptor;
import local.tmall_springboot.interceptor.OtherInterceptor;

/**
 * 
 * 类型(Types)注释标签（类的注释）：
 * 
 * @ClassName: WebMvcConfigurer
 * @Description: 配置拦截器
 * @author: NYM
 * @date: 2018年12月18日 上午9:12:07
 * 
 * @Copyright: 2018 www.tydic.com Inc. All rights reserved.
 */
@Configuration
class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    @Bean
    public OtherInterceptor getOtherIntercepter() {
        return new OtherInterceptor();
    }

    @Bean
    public LoginInterceptor getLoginIntercepter() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getOtherIntercepter()).addPathPatterns("/**");
        registry.addInterceptor(getLoginIntercepter()).addPathPatterns("/**");
    }
}