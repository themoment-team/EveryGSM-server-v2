package team.themoment.everygsm.server.v2.global.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.global.security.data.CorsEnvironment;
import team.themoment.everygsm.server.v2.global.security.filter.JwtAuthenticationFilter;
import team.themoment.everygsm.server.v2.global.security.handler.JwtAccessDeniedHandler;
import team.themoment.everygsm.server.v2.global.security.handler.JwtAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsEnvironment corsEnvironment;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable).cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))

                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/v2/auth/**").permitAll()
                                .requestMatchers("/api/v2/auth/signin").permitAll()
                                .requestMatchers("/api/v2/projects/registration",
                                        "/api/v2/projects/my",
                                        "/api/v2/projects/my/pending",
                                        "/api/v2/projects/my/rejected")
                                .hasAnyAuthority("USER", "ADMIN").requestMatchers("/api/v2/projects/like/**")
                                .hasAnyAuthority("USER", "ADMIN").requestMatchers(HttpMethod.GET, "/api/v2/projects")
                                .permitAll().requestMatchers("/api/v2/admin/**").hasAuthority("ADMIN")

                                .anyRequest().authenticated())

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(corsEnvironment.getAllowedOrigins());

        configuration.setAllowedMethods(List.of(HttpMethod.GET,
                HttpMethod.POST,
                HttpMethod.PUT,
                HttpMethod.PATCH,
                HttpMethod.DELETE,
                HttpMethod.OPTIONS).stream().map(HttpMethod::name).toList());

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
