package cn.zqat.qbittorrenttorainyunos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Torrent {
    private String hash;
    private String content_path;
    private String name;
    private String root_path;
    private String state;
    private String save_path;
    private Integer dlspeed;
    private Double progress;
    private Long downloaded;
    private Long total_size;
    private Integer num_seeds;
    private Integer num_complete;
    private List<MyFile> files = new ArrayList<>();
    private AtomicInteger unCompletedCnt = new AtomicInteger(0);
    private LocalDateTime updateTime;
    private Integer status;
    private Long size;
}
