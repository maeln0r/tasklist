package ru.maelnor.tasks.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.maelnor.tasks.security.UserDetailsServiceImpl;

import java.io.IOException;

/**
 * Фильтр для проверки JWT токенов при каждом запросе.
 * Реализует логику аутентификации пользователей на основе токена, если он валидный.
 * Работает как фильтр, который выполняется один раз на каждый запрос {@link OncePerRequestFilter}.
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Основной метод фильтрации запросов, который извлекает JWT токен, проверяет его валидность и аутентифицирует пользователя.
     *
     * @param request  текущий запрос {@link HttpServletRequest}
     * @param response текущий ответ {@link HttpServletResponse}
     * @param filterChain цепочка фильтров для передачи управления следующему фильтру
     * @throws ServletException если возникает ошибка сервлета
     * @throws IOException если возникает ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = getToken(request);
            if (jwtToken != null && jwtUtils.validate(jwtToken)) {
                String username = jwtUtils.getUsername(jwtToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Устанавливает аутентификацию в SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Невозможно выполнить аутентификацию пользователя: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает JWT токен из заголовка Authorization.
     *
     * @param request текущий запрос {@link HttpServletRequest}
     * @return строка, содержащая JWT токен, или {@code null}, если токен отсутствует или не начинается с "Bearer "
     */
    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
