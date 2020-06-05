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
                //.antMatchers("/admin", "/admin/**").hasAnyAuthority("admin")
                //.antMatchers("/gestor", "/gestor/**").hasAnyAuthority("gestor")
                //.antMatchers("/u", "/u/**").hasAnyAuthority("user")
                .anyRequest().permitAll()
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
                .usersByUsernameQuery("SELECT correo, password, cuentaactivada FROM Usuarios WHERE correo = ?")
                .authoritiesByUsernameQuery("SELECT u.correo, r.nombrerol, u.cuentaactivada FROM Usuarios u INNER JOIN " +
                        " Roles r ON (u.rol = r.idroles) WHERE u.correo = ? and u.cuentaactivada = 1");

    }

}
