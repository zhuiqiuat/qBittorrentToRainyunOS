package cn.zqat.qbittorrenttorainyunos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Overview {
    private Integer torrentCount;
    private Integer uploadCount;
    private List<UploadTorrentResult> uploadList;
}
