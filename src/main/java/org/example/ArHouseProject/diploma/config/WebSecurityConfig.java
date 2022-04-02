package org.example.ArHouseProject.diploma.config;

import org.example.ArHouseProject.diploma.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                    //Доступ только для не зарегистрированных пользователей
                    //Доступ разрешен всем пользователям
                    .antMatchers("/static/**", "/templates/site/**").permitAll()

//                    .antMatchers("/admin/addwork").permitAll() /**/

                    .antMatchers("/arhouse.arh/admin/**").hasRole("Администратор")
                    .antMatchers("/arhouse.arh/modeler/**").hasRole("Модельер")
                    .antMatchers("/arhouse.arh/designer/**").hasRole("Дизайнер")

                    //Все остальные страницы требуют аутентификации
                    //.anyRequest().authenticated()
                .and()
                    //Настройка для входа в систему
                    .formLogin()
                    .loginPage("/arhouse.arh/authorization")
                    .loginProcessingUrl("/arhouse.arh/authorization")
                    .usernameParameter("username").passwordParameter("password")
                    .defaultSuccessUrl("/successauth")
                    .permitAll()
                .and()
                    .logout()
                    .permitAll();
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder authenticationMB) throws Exception {
        authenticationMB.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }
}