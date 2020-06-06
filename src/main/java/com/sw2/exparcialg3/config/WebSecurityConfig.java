package com.sw2.exparcialg3.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http.csrf().disable();

        http.formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/processLogin")
                .defaultSuccessUrl("/redirectByRole", true);
        http.authorizeRequests()
                .antMatchers("/admin", "/admin/**").hasAnyAuthority("admin")
                .antMatchers("/gestor", "/gestor/**").hasAnyAuthority("gestor")
                .antMatchers("/u", "/u/**").hasAnyAuthority("registrado")
                .anyRequest().permitAll()
                .and()
                .rememberMe().key("uniqueAndSecret")
                .tokenValiditySeconds(172800);
                ;

        http.logout()
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("SELECT correo, password, enable FROM usuario WHERE correo = ?")
                .authoritiesByUsernameQuery("SELECT u.correo, r.rol, u.enable FROM usuario u INNER JOIN " +
                        " rol r ON (u.rol = r.idrol) WHERE u.correo = ? and u.enable = 1");

    }

}
