package org.example.filter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.JwtService;
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

    public AuthFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //Проверяем, есть ли токен в заголовках
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            //Если токена нет, то пусть разбираются дальше
            chain.doFilter(request, response);
            return;
        }

        //Получаем чистую строку с токеном
        String accessToken = authHeader.substring(7);

        try {
            // Получаем userID из токена, если токен подписан не мной и он истек, то выдаем ошибку
            Claims claims = jwtService.getClaimsFromToken(accessToken);

            Long userId = Long.parseLong(claims.getSubject());

            //Авторизуем
            var authentication = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e){
            sendError(response, 401, "TOKEN_EXPIRED");
            return;
        } catch (JwtException e) {
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
