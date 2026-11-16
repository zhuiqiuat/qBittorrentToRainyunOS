package cn.zqat.qbittorrenttorainyunos.service;

import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;

import java.util.List;

public interface TorrentService {

    Integer getActiveDownloadCount();

    List<Torrent> getDownloadList();

    Integer getActiveUploadCount();

    List<Torrent> getUploadList();

}
