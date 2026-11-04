package cn.zqat.qbittorrenttorainyunos.service.impl;

import cn.zqat.qbittorrenttorainyunos.mapper.UserMapper;
import cn.zqat.qbittorrenttorainyunos.pojo.LoginRequest;
import cn.zqat.qbittorrenttorainyunos.pojo.LoginResult;
import cn.zqat.qbittorrenttorainyunos.pojo.Result;
import cn.zqat.qbittorrenttorainyunos.pojo.User;
import cn.zqat.qbittorrenttorainyunos.service.UserService;
import cn.zqat.qbittorrenttorainyunos.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTUtils jwtu;

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
                return Result.suc(new LoginResult(u.getId(), u.getUsername(), u.getAvatar(), token));
            }
        }
        return Result.err("用户名或者密码错误");
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }
}
