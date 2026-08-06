package cn.zqat.qbittorrenttorainyunos.service;

import cn.zqat.qbittorrenttorainyunos.pojo.UploadTorrentResult;

import java.util.List;

public interface UploadService {
    public Integer getUploadCount();

    public List<UploadTorrentResult> getUploadList(Integer count);
}
