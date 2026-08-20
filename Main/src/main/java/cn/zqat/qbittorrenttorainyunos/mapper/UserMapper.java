package cn.zqat.qbittorrenttorainyunos.mapper;

import cn.zqat.qbittorrenttorainyunos.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User getUserById(Integer id);

    User getUserByUsername(String username);

    void updateToken(Integer id, String token);

}
