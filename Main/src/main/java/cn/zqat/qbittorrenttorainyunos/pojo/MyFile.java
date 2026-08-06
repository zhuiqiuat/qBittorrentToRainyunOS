package cn.zqat.qbittorrenttorainyunos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyFile {
    private String prefix;
    private Path path;
    private Integer status;
    private Long startTime;
    private Long totalbytes;
    private Long sendedbytes;
    private Integer progress;
}
