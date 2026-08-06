package cn.zqat.qbittorrenttorainyunos.service;

import cn.zqat.qbittorrenttorainyunos.pojo.TorrentResult;

import java.io.IOException;
import java.util.List;

public interface TorrentService {

    Integer getTorrentCount() throws IOException;

    List<TorrentResult> getTorrents() throws IOException;

}
