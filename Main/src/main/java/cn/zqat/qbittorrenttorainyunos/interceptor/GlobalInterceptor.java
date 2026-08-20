package cn.zqat.qbittorrenttorainyunos.interceptor;

import cn.zqat.qbittorrenttorainyunos.pojo.User;
import cn.zqat.qbittorrenttorainyunos.service.UserService;
import cn.zqat.qbittorrenttorainyunos.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");
        if (token == null || token.isEmpty() || !jwtu.isValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        Claims claims = jwtu.extractAllClaims(token);
        User u = usrs.getUserById((Integer) claims.get("id"));
        if(u == null || !u.getToken().equals(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
