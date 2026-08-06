package cn.zqat.qbittorrenttorainyunos.service;

import cn.zqat.qbittorrenttorainyunos.pojo.UploadTorrentResult;

import java.util.List;

public interface UploadService {
    Integer getUploadCount();

    List<UploadTorrentResult> getUploadList(Integer count);
}
