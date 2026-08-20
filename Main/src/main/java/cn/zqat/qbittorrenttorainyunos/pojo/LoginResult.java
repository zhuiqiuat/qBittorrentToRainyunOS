package cn.zqat.qbittorrenttorainyunos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {

    private Integer id;
    private String username;
    private String avatar;
    private String token;

}
