package org.example.lebonachat.config;

import org.example.lebonachat.ModuleUser.CustomUserDetailsService;
import org.example.lebonachat.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    // 🔑 Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔑 AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 🔒 Security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Autoriser certaines pages pour tous
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/register", "/accueil",
                                "/css/**", "/js/**", "/images/**", "/uploads/**", "/error"
                        ).permitAll()
                        .requestMatchers("/profil", "/profil/**").authenticated()
                        .requestMatchers("/annonces/**").authenticated()
                        .requestMatchers("/category/new", "/category/save", "/category/delete/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/category/list").permitAll()
                        .requestMatchers("/api/**").permitAll() // Flutter API
                        .anyRequest().authenticated()
                )

                // Login web
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/accueil", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                // Logout web
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/accueil")
                        .permitAll()
                )

                // CSRF
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // Ignore CSRF pour API Flutter
                )

                // JWT Filter pour API
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // UserDetailsService
                .userDetailsService(userDetailsService);

        return http.build();
    }
}
