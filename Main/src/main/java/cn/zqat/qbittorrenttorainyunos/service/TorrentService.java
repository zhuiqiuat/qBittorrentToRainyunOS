package cn.zqat.qbittorrenttorainyunos.service;

import cn.zqat.qbittorrenttorainyunos.pojo.TorrentResult;

import java.util.List;

public interface TorrentService {

    Integer getTorrentCount();

    List<TorrentResult> getTorrents();

}
