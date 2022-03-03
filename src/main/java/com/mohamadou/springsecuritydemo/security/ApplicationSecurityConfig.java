package com.mohamadou.springsecuritydemo.security;

import com.mohamadou.springsecuritydemo.auth.ApplicationUserService;
import com.mohamadou.springsecuritydemo.jwt.JwtTokenVerifier;
import com.mohamadou.springsecuritydemo.jwt.JwtUsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

import static com.mohamadou.springsecuritydemo.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, ApplicationUserService applicationUserService){
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
    }


    @Override
    //JWT configuration
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernamePasswordAuthenticationFilter(authenticationManager()))
                .addFilterAfter(new JwtTokenVerifier(), JwtUsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "index", "/students/test", "/css/*", "/js/*").permitAll()
                .antMatchers("/student/students/**").hasRole(STUDENT.name())
                .anyRequest()
                .authenticated();
    }


    /* @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
//              .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//              .and()
              .csrf().disable()
              .authorizeRequests()
              .antMatchers("/", "index", "/students/test", "/css/*", "/js/*").permitAll()
              .antMatchers("/student/students/**").hasRole(STUDENT.name())
   *//*           .antMatchers(HttpMethod.POST,"/management/students/**").hasAuthority(COURSE_WRITE.getPermission())
              .antMatchers(HttpMethod.PUT, "/management/students/**").hasAuthority(COURSE_WRITE.getPermission())
              .antMatchers(HttpMethod.DELETE, "/management/students/**").hasAuthority(COURSE_WRITE.getPermission())
              .antMatchers(HttpMethod.GET,"/management/students/**").hasAnyRole(ADMIN.name(), ADMINTRAINEE.name())*//*
              .anyRequest()
              .authenticated()
              .and()
              .formLogin()
                  .loginPage("/login").permitAll()
                  .defaultSuccessUrl("/courses", true)
              .and()
              .rememberMe()
                    .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21)) //Default expiration time is 2 weeks
                    .key("verysecureappdemo") //Custom Hash key
              .and()
              .logout()
                    .logoutUrl("/logout")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .logoutSuccessUrl("/login");
    }
*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /*auth.userDetailsService(this.applicationUserService)
                .passwordEncoder(passwordEncoder);*/
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
                provider.setUserDetailsService(applicationUserService);
                provider.setPasswordEncoder(passwordEncoder);

                return provider;
    }



    /* @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails john = User.builder()
                .username("john")
                .password(passwordEncoder.encode("password"))
//                .roles(STUDENT.name()) //ROLE_STUDENT
                .authorities(STUDENT.getGrantedAuthorities())
                .build();

        UserDetails lynda = User.builder()
                .username("lynda")
                .password(passwordEncoder.encode("password123"))
//                .roles(ADMIN.name()) //ROLE_ADMIN
                .authorities(ADMIN.getGrantedAuthorities())
                .build();

        UserDetails tom = User.builder()
                .username("tom")
                .password(passwordEncoder.encode("password123"))
//                .roles(ADMINTRAINEE.name()) //ROLE_ADMINTRAINEE
                .authorities(ADMINTRAINEE.getGrantedAuthorities())
                .build();

        return new InMemoryUserDetailsManager(
                john,
                lynda,
                tom
        );
    }*/
}
