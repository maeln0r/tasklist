package ru.maelnor.tasks.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.maelnor.tasks.security.UserDetailsServiceImpl;
import ru.maelnor.tasks.security.jwt.JwtAccessDeniedHandler;
import ru.maelnor.tasks.security.jwt.JwtAuthenticationEntryPoint;
import ru.maelnor.tasks.security.jwt.JwtTokenFilter;

/**
 * Класс конфигурации безопасности с использованием JWT.
 * Настраивает фильтрацию запросов, провайдер аутентификации и другие
 * параметры безопасности для приложения.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth-type", havingValue = "jwt", matchIfMissing = true)
public class JwtSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtTokenFilter jwtTokenFilter;

    /**
     * Создает бин {@link PasswordEncoder}, который использует BCrypt для кодирования паролей.
     *
     * @return экземпляр {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создает и настраивает провайдер аутентификации с использованием пользовательских
     * данных и кодировщика паролей.
     *
     * @return настроенный {@link DaoAuthenticationProvider}
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Создает бин {@link AuthenticationManager}, который управляет аутентификацией в приложении.
     *
     * @param authenticationConfiguration конфигурация аутентификации
     * @return экземпляр {@link AuthenticationManager}
     * @throws Exception если возникает ошибка при получении менеджера аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Настраивает цепочку фильтров безопасности для обработки HTTP-запросов.
     * Включает JWT-аутентификацию, управление сессиями, исключение CSRF, а также обработку ошибок аутентификации и доступа.
     *
     * @param http объект {@link HttpSecurity} для настройки параметров безопасности
     * @return настроенный {@link SecurityFilterChain}
     * @throws Exception если возникает ошибка при настройке безопасности
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> auth.requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
