package cn.zqat.qbittorrenttorainyunos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MainApplication {

    static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
