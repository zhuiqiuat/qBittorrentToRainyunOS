package cn.zqat.qbittorrenttorainyunos.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "qbittorrent")
public class qBittorrentConfig {

    private String url;
    private String username;
    private String password;

}
