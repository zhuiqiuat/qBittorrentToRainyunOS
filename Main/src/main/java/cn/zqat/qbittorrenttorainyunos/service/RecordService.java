package cn.zqat.qbittorrenttorainyunos.service;

import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;

import java.util.List;

public interface RecordService {

    void add(Torrent torrent);

    List<Torrent> getAll();

}
