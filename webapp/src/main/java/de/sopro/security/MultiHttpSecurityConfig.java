package de.sopro.security;

/**
 * This file is sponsored by
 *     https://docs.spring.io/spring-security/site/docs/5.0.8.RELEASE/reference/htmlsingle/#multiple-httpsecurity
 * with much love
 */

import de.sopro.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.hibernate.criterion.Restrictions.and;

@EnableWebSecurity
public class MultiHttpSecurityConfig {

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/app/**").httpBasic()
                    .and()
                    .antMatcher("/app/**")
                    .authorizeRequests()
                        .antMatchers("/app/combinations/public").permitAll()
                        .antMatchers("/app/combinations/own").authenticated()
                        .antMatchers("/app/combinations/shared").authenticated()
                        .antMatchers("/app/combinations/*").permitAll()
                        .antMatchers("/app/products/**").permitAll()
                        .antMatchers("/app/**").authenticated()
                    ;
        }
    }

    @Configuration
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .formLogin()
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                    .and()
                    .logout()
                        .permitAll()
                    .and()
                    .authorizeRequests()
                        .antMatchers("/").permitAll()
                        .antMatchers(HttpMethod.GET,"/combinations").permitAll()
                        .antMatchers(HttpMethod.POST,"/combinations").authenticated()
                        .antMatchers(HttpMethod.POST,"/combinations/new").authenticated()
                        .antMatchers(HttpMethod.GET, "/combinations/*").permitAll()
                        .antMatchers("/formats/**").hasAnyAuthority(User.ROLE_ADMIN)
                        .antMatchers("/login").permitAll()
                        .antMatchers("/product-list").permitAll()
                        .antMatchers("/products").permitAll()
                        .antMatchers("/products/alternatives/*/*").permitAll()
                        .antMatchers("/products/compatibility/*/*").permitAll()
                        .antMatchers("/products/new").hasAnyAuthority(User.ROLE_ADMIN)
                        .antMatchers("/products/upload").hasAnyAuthority(User.ROLE_ADMIN)
                        .antMatchers("/products/*").permitAll()
                        .antMatchers("/registration").anonymous()
                        .antMatchers("/users/**").hasAnyAuthority(User.ROLE_ADMIN)
            ;

            //Need to use h2-console
            http.csrf().disable();
            http.headers().frameOptions().disable();
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
