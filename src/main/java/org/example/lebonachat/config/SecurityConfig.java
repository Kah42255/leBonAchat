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
                        // Pages publiques
                        .requestMatchers("/", "/login", "/register", "/accueil", "/css/**", "/js/**", "/images/**", "/uploads/**", "/error").permitAll()
                        .requestMatchers("/annonces/**", "/annonce/*", "/search", "/filter/category", "/category/list", "/api/**").permitAll()

                        // Pages utilisateurs connectés
                        .requestMatchers("/profil/**", "/panier/**", "/notification/**", "/commande/details/**").authenticated()

                        // Pages admin
                        .requestMatchers("/category/new", "/category/save", "/category/delete/**").hasAuthority("ROLE_ADMIN")

                        // Toute autre requête nécessite authentification
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
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService);

        return http.build();
    }
}
