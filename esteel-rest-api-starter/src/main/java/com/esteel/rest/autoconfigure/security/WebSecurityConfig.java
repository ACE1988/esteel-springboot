package com.esteel.rest.autoconfigure.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.http.HttpServletResponse;

@Configuration
//@EnableConfigurationProperties(SecurityProperties.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  //  @Autowired
//  SecurityProperties properties;
//  @Bean
//  @Override
//  public AuthenticationManager authenticationManagerBean() throws Exception {
//    return super.authenticationManagerBean();
//  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .exceptionHandling()
        .authenticationEntryPoint((request, response, authException) -> response
            .sendError(HttpServletResponse.SC_UNAUTHORIZED))
        .and()
        .authorizeRequests()
        .antMatchers("/**")
        .authenticated()
        .and()
        .httpBasic();
  }
}

//class CustomPreAuthUserDetailsService implements
//    AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
//
//  @Override
//  public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
//    Collection authorities = Arrays.asList(new SimpleGrantedAuthority("USER"));
//    return new User(token.getName(), "N/A", true, true, true, true, authorities);
//  }
//}
