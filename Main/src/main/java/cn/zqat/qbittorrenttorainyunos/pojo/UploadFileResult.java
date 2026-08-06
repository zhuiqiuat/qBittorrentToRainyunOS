package cn.zqat.qbittorrenttorainyunos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UploadFileResult {

    private String name;
    private String usize;
    private String size;
    private Integer progress;
    private String speed;
    private String bucket;
    private Integer status;

}
