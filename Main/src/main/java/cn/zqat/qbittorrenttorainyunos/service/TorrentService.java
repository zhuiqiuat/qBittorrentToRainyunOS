package cn.zqat.qbittorrenttorainyunos.service;

import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;

import java.util.Collection;

public interface TorrentService {

    Integer getActiveDownloadCount();

    Collection<Torrent> getDownloadList();

    Integer getActiveUploadCount();

    Collection<Torrent> getUploadList();

}
