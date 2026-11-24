package cn.zqat.qbittorrenttorainyunos.interceptor;

import cn.zqat.qbittorrenttorainyunos.service.UserService;
import cn.zqat.qbittorrenttorainyunos.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class GlobalInterceptor implements HandlerInterceptor {

    @Autowired
    private JWTUtils jwtu;

    @Autowired
    private UserService usrs;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String token = request.getHeader("token");
        if (token == null || token.isEmpty() || !jwtu.isValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        Claims claims = jwtu.extractAllClaims(token);
        String utoken = usrs.getTokenById(Math.toIntExact((Long) claims.get("id")));
        if(utoken == null || utoken.isEmpty() || !utoken.equals(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
