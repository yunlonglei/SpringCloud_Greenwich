package com.lei.springcloud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @Author: leiyunlong
 * @Date: 2019/7/26 11:37
 * @Version 1.0
 */

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("12345678").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login").permitAll()
                .defaultSuccessUrl("/welcome").permitAll()
                .and().authorizeRequests()
                .anyRequest().authenticated()
                .antMatchers("/login.html", "/**/**.css", "/images/**", "**/**.js", "/index").permitAll()//解决静态资源被拦截的问题
                .and().logout().permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/login.html", "/**/**.css", "/**/images/**", "**/**.js", "/welcome");
    }

}
