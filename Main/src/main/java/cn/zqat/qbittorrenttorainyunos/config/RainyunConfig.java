package cn.zqat.qbittorrenttorainyunos.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rainyun")
public class RainyunConfig {

    private String endpoint;
    private String bucketname;
    private String accesskey;
    private String secretkey;

}
