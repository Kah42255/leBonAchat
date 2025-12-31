package org.example.lebonachat.config;

import org.example.lebonachat.ModuleUser.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth


                        // 🔓 Pages publiques
                        .requestMatchers(
                                "/",
                                "/login",
                                "/accueil",
                                "/register",
                                "/annonces",
                                "/annonce/**",
                                "/search",
                                "/filter/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**",
                                "/error"
                        ).permitAll()

                        .requestMatchers(
                                "/profil",
                                "/profil/**"
                        ).authenticated()

                        .requestMatchers("/annonces/**").authenticated()

                        .requestMatchers(
                                "/category/new",
                                "/category/save",
                                "/category/delete/**"
                        ).hasAuthority("ROLE_ADMIN")

                        // 👀 Liste des catégories visible pour tous
                        .requestMatchers("/category/list").permitAll()

                        // 🔒 Tout le reste nécessite login
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/accueil", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/accueil")
                        .permitAll()
                )

                // CustomUserDetailsService
                .userDetailsService(userDetailsService);

        return http.build();
    }
}
