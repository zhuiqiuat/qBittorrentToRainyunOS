package cn.zqat.qbittorrenttorainyunos.service;

import cn.zqat.qbittorrenttorainyunos.pojo.LoginRequest;
import cn.zqat.qbittorrenttorainyunos.pojo.Result;
import cn.zqat.qbittorrenttorainyunos.pojo.User;

public interface UserService {
    Result login(LoginRequest loginRequest);

    User getUserById(Integer id);

    String getTokenById(Integer id);
}
