package cn.zqat.qbittorrenttorainyunos.service.impl;

import cn.zqat.qbittorrenttorainyunos.mapper.UserMapper;
import cn.zqat.qbittorrenttorainyunos.pojo.LoginRequest;
import cn.zqat.qbittorrenttorainyunos.pojo.LoginResult;
import cn.zqat.qbittorrenttorainyunos.pojo.Result;
import cn.zqat.qbittorrenttorainyunos.pojo.User;
import cn.zqat.qbittorrenttorainyunos.service.UserService;
import cn.zqat.qbittorrenttorainyunos.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisHashCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTUtils jwtu;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final Expiration expiration = Expiration.from(6, TimeUnit.HOURS);

    @Override
    public Result login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        if(username != null && !username.isEmpty()){
            User u = userMapper.getUserByUsername(username);
            if(password == null || password.isEmpty()){
                return Result.err("密码不能为空");
            }
            if(u != null && u.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
                String token = jwtu.getToken(u.getId(),username);
                userMapper.updateToken(u.getId(), token);
                updateCache(u.getId(), token);
                return Result.suc(new LoginResult(u.getId(), u.getUsername(), u.getAvatar(), token));
            }
        }
        return Result.err("用户名或者密码错误");
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }

    @Override
    public String getTokenById(Integer id) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String token = hashOperations.getAndExpire("tokens", expiration, List.of(String.valueOf(id))).getFirst();
        if(token != null && !token.isEmpty()){
            return token;
        }

        log.info("id {} 没有找到对应的缓存", id);
        User user = getUserById(id);
        if(user != null && user.getToken() != null && !user.getToken().isEmpty()){
            token = user.getToken();
            updateCache(id, token);
            return token;
        }
        return null;
    }

    private void updateCache(Integer id, String token){
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAndExpire(
                "tokens",
                Map.of(String.valueOf(id), token),
                RedisHashCommands.HashFieldSetOption.UPSERT,
                expiration
        );
    }
}
