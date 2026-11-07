package com.example.template.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import tools.jackson.databind.ObjectMapper;

/**
 * Application-wide security baseline: every request must authenticate with HTTP Basic
 * against a single in-memory user whose credentials are required configuration.
 * Rejected requests are reported as Problem Details, keeping the API's single error format.
 *
 * TEMPLATE: Decisions each derived project must revisit before deployment:
 * - Authentication mechanism: HTTP Basic with one shared user is a starting point, not a
 *   production choice. Replace it with the mechanism the project needs (JWT resource server,
 *   OAuth2 login, per-user credentials) and remove or replace the in-memory user.
 * - Authorization rules and roles: the baseline only requires authentication, so 403 is
 *   configured but unreachable. Add request rules or method security when roles exist.
 * - CSRF: disabled here because the API is stateless and clients send credentials explicitly
 *   per request. Re-enable it as soon as authentication relies on browser cookies or sessions.
 * - CORS: not enabled by default. Enable it through http.cors(...) with an explicit
 *   CorsConfigurationSource when a browser frontend from another origin must call this API.
 * - Security headers: framework defaults apply; review them for the deployment (for example
 *   HSTS behind TLS termination).
 * - Secret handling: credentials come from the environment or the local .env and are never
 *   committed; keep it that way in every environment.
 * - Operational endpoints: only /actuator/health/** is permitted without credentials, and only
 *   health is exposed by default. Update the matcher if the management base path changes. Review
 *   the exposed endpoints and consider a separate management port or network policy per deployment.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        var rejectedRequests = new ProblemDetailSecurityHandler(objectMapper);
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic
                        .authenticationEntryPoint(rejectedRequests))
                // Stateless: credentials travel with every request; no HTTP session is created.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(rejectedRequests)
                        .accessDeniedHandler(rejectedRequests));
        // Form login is deliberately not enabled: this is a credentials-in-request REST API.
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Single in-memory user built from required configuration. The plain password is encoded
     * at startup; only the BCrypt hash is kept in memory. Declaring this bean also suppresses
     * Spring Boot's auto-configured user with its generated, logged password.
     */
    @Bean
    public UserDetailsService userDetailsService(
            @Value("${app.security.username}") String username,
            @Value("${app.security.password}") String password,
            PasswordEncoder passwordEncoder) {
        requireConfiguredValue(username, "app.security.username");
        requireConfiguredValue(password, "app.security.password");
        UserDetails user = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                // No roles or business permissions in the baseline; see the TEMPLATE notes.
                .roles()
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    private static void requireConfiguredValue(String value, String property) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required security property '" + property
                    + "' is missing or blank; supply it through the environment or the local .env");
        }
    }
}
