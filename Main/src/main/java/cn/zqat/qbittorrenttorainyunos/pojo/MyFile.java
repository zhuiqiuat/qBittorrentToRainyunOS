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
    private String name;
    private Path path;
    private Integer status;
    private Long startTime = 0L;
    private Long totalBytes;
    private Long sentBytes = 0L;
    private Integer progress = 0;
    private Long speed = 0L;
    private String bucket;

    public MyFile(String prefix, Path path, int status, long totalBytes) {
        this.prefix = prefix;
        this.path = path;
        this.status = status;
        this.totalBytes = totalBytes;
    }
}
