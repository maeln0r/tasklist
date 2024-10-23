package ru.maelnor.tasks.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import ru.maelnor.tasks.handler.CustomAuthenticationSuccessHandler;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс конфигурации безопасности для интеграции с Keycloak через OpenID Connect (OIDC).
 * Настраивает фильтры безопасности, маппинг ролей и обработку аутентификации и выхода.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "app.auth-type", havingValue = "keycloak")
public class KeycloakSecurityConfig {

    /**
     * Интерфейс для конвертации OIDC-претензий (claims) в коллекцию {@link GrantedAuthority}.
     * Используется для преобразования ролей из realm_access в GrantedAuthority.
     */
    interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>> {
    }

    /**
     * Создает бин {@link AuthoritiesConverter}, который преобразует претензии (claims) realm_access
     * в набор ролей пользователя.
     *
     * @return объект {@link AuthoritiesConverter}
     */
    @Bean
    AuthoritiesConverter realmRolesAuthoritiesConverter() {
        return claims -> {
            final var realmAccess = Optional.ofNullable((Map<String, Object>) claims.get("realm_access"));
            final var roles =
                    realmAccess.flatMap(map -> Optional.ofNullable((List<String>) map.get("roles")));
            return roles.map(List::stream).orElse(Stream.empty()).map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast).toList();
        };
    }

    /**
     * Создает бин для преобразования OIDC-авторитетов (Authorities) в роли, полученные из Keycloak.
     * Использует {@link OidcUserAuthority} для получения информации о ролях.
     *
     * @param realmRolesAuthoritiesConverter конвертер ролей из realm_access
     * @return {@link GrantedAuthoritiesMapper}, который преобразует роли
     */
    @Bean
    GrantedAuthoritiesMapper authenticationConverter(
            Converter<Map<String, Object>, Collection<GrantedAuthority>> realmRolesAuthoritiesConverter) {
        return (authorities) -> authorities.stream()
                .filter(authority -> authority instanceof OidcUserAuthority)
                .map(OidcUserAuthority.class::cast).map(OidcUserAuthority::getIdToken)
                .map(OidcIdToken::getClaims).map(realmRolesAuthoritiesConverter::convert)
                .flatMap(roles -> roles.stream()).collect(Collectors.toSet());
    }

    /**
     * Настраивает цепочку фильтров безопасности для обработки OAuth2 аутентификации и выхода.
     * Включает настройку успешной аутентификации и обработку успешного выхода из системы с помощью
     * {@link OidcClientInitiatedLogoutSuccessHandler}.
     *
     * @param http                     объект для настройки параметров безопасности
     * @param clientRegistrationRepository репозиторий для регистрации OAuth2 клиентов
     * @param successHandler           обработчик успешной аутентификации
     * @return объект {@link SecurityFilterChain}, который описывает конфигурацию фильтров безопасности
     * @throws Exception если возникает ошибка при настройке фильтров
     */
    @Bean
    SecurityFilterChain clientSecurityFilterChain(HttpSecurity http,
                                                  ClientRegistrationRepository clientRegistrationRepository,
                                                  CustomAuthenticationSuccessHandler successHandler) throws Exception {
        // Настройка входа через OAuth2 с успешной аутентификацией
        http.oauth2Login(oauth2 -> oauth2.successHandler(successHandler));

        // Отключение защиты от CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        // Настройка выхода с использованием OIDC и редиректа после выхода
        http.logout((logout) -> {
            final var logoutSuccessHandler =
                    new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
            logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
            logout.logoutSuccessHandler(logoutSuccessHandler);
        });

        // Разрешить доступ только аутентифицированным пользователям
        http.authorizeHttpRequests(requests -> {
            requests.anyRequest().authenticated();
        });

        return http.build();
    }
}
