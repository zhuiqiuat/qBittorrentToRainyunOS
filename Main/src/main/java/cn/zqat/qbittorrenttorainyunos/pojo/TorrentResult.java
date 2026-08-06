package cn.zqat.qbittorrenttorainyunos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TorrentResult {

    private String name;
    private String dsize;
    private String size;
    private Integer progress;
    private String speed;
    private String peers;
    private Integer status;

}
