package cn.zqat.qbittorrenttorainyunos.service;

import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;

import java.util.List;

public interface TorrentService {

    Integer getDownloadCount();

    List<Torrent> getDownloadList();

}
