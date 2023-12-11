package com.mykingdom.security;

import com.mykingdom.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurity {
    @Autowired
    private CustomAuthenticationEntryPoint unauthorizedHandler;
    private final AuthService authDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authManagerBuilder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET,"/bill/getAllBill").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/bill").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST,"/bill").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/brand").permitAll()
                .requestMatchers(HttpMethod.POST,"/brand").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET,"/category").permitAll()
                .requestMatchers(HttpMethod.POST,"/category").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET,"/product").permitAll()
                .requestMatchers(HttpMethod.GET,"/product/getBestSaleOff").permitAll()
                .requestMatchers(HttpMethod.GET,"/product/getByCategory").permitAll()
                .requestMatchers(HttpMethod.GET,"/product/getByBrand").permitAll()
                .requestMatchers(HttpMethod.GET,"/product/getById").permitAll()
                .requestMatchers(HttpMethod.GET,"/post").permitAll()
                .requestMatchers("/vnpay/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/product").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET,"/user/getAllUsers").hasRole(Role.ADMIN.name())
                                .anyRequest().authenticated()
                .and()
                .addFilter(getAuthenticationFilter())
                .addFilter(new AuthorizationFilter(authManagerBuilder.getOrBuild(), authDetailsService))
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authenticationProvider(daoAuthenticationProvider());

        return http.build();
    }


    public AuthenticationFilter getAuthenticationFilter() throws Exception {
        final AuthenticationFilter filter = new AuthenticationFilter(authManagerBuilder.getOrBuild());
        filter.setFilterProcessesUrl("/login");
        return filter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedOrigins(List.of("https://mykingdomfe.vercel.app"));
//        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));

        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(1800L);
        corsConfiguration.setAllowedMethods(List.of("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        return provider;
    }
}
