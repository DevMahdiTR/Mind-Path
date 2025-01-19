package mindpath.security.config;

import mindpath.config.APIRouters;
import mindpath.config.AuthenticationRoles;
import mindpath.security.jwt.JWTAuthenticationFilter;
import mindpath.security.jwt.JwtAuthEntryPoint;
import jakarta.servlet.Filter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthEntryPoint authEntryPoint;
    private final LogoutHandler logoutHandler;

    @Autowired
    public SecurityConfig(JwtAuthEntryPoint authEntryPoint, LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .and()
                .authorizeHttpRequests()

                .requestMatchers(APIRouters.AUTH_ROUTER + "/**").permitAll()


                // Class routes security configuration
                .requestMatchers(HttpMethod.GET,APIRouters.GROUP_ROUTER + "/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN,
                        AuthenticationRoles.ROLE_SUPER_TEACHER,
                        AuthenticationRoles.ROLE_TEACHER,
                        AuthenticationRoles.ROLE_STUDENT
                )
                .requestMatchers(APIRouters.GROUP_ROUTER + "/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN,
                        AuthenticationRoles.ROLE_SUPER_TEACHER
                )


                // Offer routes security configuration
                .requestMatchers(HttpMethod.GET,APIRouters.OFFER_ROUTER + "/student/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN,
                        AuthenticationRoles.ROLE_STUDENT
                )
                .requestMatchers(HttpMethod.GET,APIRouters.OFFER_ROUTER + "/teacher/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN,
                        AuthenticationRoles.ROLE_SUPER_TEACHER
                )
                .requestMatchers(HttpMethod.POST,APIRouters.OFFER_ROUTER + "/student-request/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_STUDENT
                )
                .requestMatchers(HttpMethod.POST,APIRouters.OFFER_ROUTER + "/teacher-request/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_SUPER_TEACHER
                )
                .requestMatchers(APIRouters.OFFER_ROUTER + "/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN
                )


                .requestMatchers(APIRouters.SUPER_TEACHER_ROUTER + "/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN,
                        AuthenticationRoles.ROLE_SUPER_TEACHER
                )

                .requestMatchers(APIRouters.SUBJECT_ROUTER + "/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN,
                        AuthenticationRoles.ROLE_SUPER_TEACHER,
                        AuthenticationRoles.ROLE_TEACHER,
                        AuthenticationRoles.ROLE_STUDENT
                )

                .requestMatchers(APIRouters.PLAY_LIST_ROUTER + "/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN,
                        AuthenticationRoles.ROLE_SUPER_TEACHER,
                        AuthenticationRoles.ROLE_TEACHER,
                        AuthenticationRoles.ROLE_STUDENT
                )

                .requestMatchers(HttpMethod.GET , APIRouters.USER_ROUTER+"/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN,
                        AuthenticationRoles.ROLE_STUDENT,
                        AuthenticationRoles.ROLE_TEACHER,
                        AuthenticationRoles.ROLE_SUPER_TEACHER

                )

                .requestMatchers(APIRouters.CHAT_ROUTER + "/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN,
                        AuthenticationRoles.ROLE_SUPER_TEACHER,
                        AuthenticationRoles.ROLE_TEACHER,
                        AuthenticationRoles.ROLE_STUDENT
                )

                .requestMatchers(APIRouters.STATS_ROUTER + "/**").hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN
                )


                .requestMatchers("/ws/**").permitAll()


                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "configuration/ui",
                        "configuration/security"
                ).hasAnyAuthority(
                        AuthenticationRoles.ROLE_ADMIN
                )

                .requestMatchers(APIRouters.AZURE_STORAGE_ROUTER+"/**").permitAll()


                .anyRequest().authenticated()
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .and()
                .logout()
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(
                        (request, response, authentication) ->
                                SecurityContextHolder.getContext())
                .and()
                .httpBasic();
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", configuration);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(@NotNull AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public Filter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }
}