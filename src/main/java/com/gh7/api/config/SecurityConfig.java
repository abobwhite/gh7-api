package com.gh7.api.config;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  private String apiAudience;
  private String issuer;

  @Autowired
  public SecurityConfig(@Value(value = "${auth0.apiAudience}") String apiAudience, @Value(value = "${auth0.issuer}") String issuer) {
    this.apiAudience = apiAudience;
    this.issuer = issuer;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors();
    JwtWebSecurityConfigurer
        .forRS256(apiAudience, issuer)
        .configure(http)
        .authorizeRequests()
        .antMatchers("/actuator/**").permitAll()
        .antMatchers(HttpMethod.POST, "/twilio/**").permitAll()
        .anyRequest().authenticated();
  }

//  @Bean
//  CorsConfigurationSource corsConfigurationSource() {
//    CorsConfiguration configuration = new CorsConfiguration();
//    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//    configuration.setAllowedMethods(Arrays.asList("GET","POST"));
//    configuration.setAllowCredentials(true);
//    configuration.addAllowedHeader("Authorization");
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", configuration);
//    return source;
//  }
}
