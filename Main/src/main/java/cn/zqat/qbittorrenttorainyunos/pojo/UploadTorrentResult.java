package cn.zqat.qbittorrenttorainyunos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadTorrentResult {

    private String name;
    private String hash;
    private String tusize;
    private String tsize;
    private Integer tprogress;
    private String tspeed;
    private Integer tstatus;
    private List<UploadFileResult> children;


}
