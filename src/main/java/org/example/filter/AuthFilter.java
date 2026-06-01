package org.example.filter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthFilter extends OncePerRequestFilter{
    private JwtService jwtService;

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);
    public AuthFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/");
    }

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //Проверяем, есть ли токен в заголовках
        log.info("Проверка. Есть ли заголовок в запросе");
        String authHeader = request.getHeader("Authorization");
        log.info("Токен:" + authHeader);
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            //Если токена нет, то пусть разбираются дальше
            log.info("Токен не найден");
            chain.doFilter(request, response);
            return;
        }

        log.info("Получаем строку с токеном");
        //Получаем чистую строку с токеном
        String accessToken = authHeader.substring(7);
        log.info("Чистый токен:" + accessToken);

        try {
            log.info("Проверяем подписан ли мной токен");
            // Получаем userID из токена, если токен подписан не мной и он истек, то выдаем ошибку
            Claims claims = jwtService.getClaimsFromToken(accessToken);
            Long userId = Long.parseLong(claims.getSubject());

            log.info("Авторизуем");
            //Авторизуем
            var authentication = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e){
            log.info("TOKEN_EXPIRED");
            sendError(response, 401, "TOKEN_EXPIRED");
            return;
        } catch (JwtException e) {
            log.info("INVALID_TOKEN");
            sendError(response, 401, "INVALID_TOKEN");
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException{
        ObjectMapper mapper = new ObjectMapper();

        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("error ", message);

        //Переделываем мапу в json строку и записываем в ответ
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
