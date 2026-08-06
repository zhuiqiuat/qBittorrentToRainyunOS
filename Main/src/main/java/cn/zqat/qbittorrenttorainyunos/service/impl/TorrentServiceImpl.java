package cn.zqat.qbittorrenttorainyunos.service.impl;

import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;
import cn.zqat.qbittorrenttorainyunos.pojo.TorrentResult;
import cn.zqat.qbittorrenttorainyunos.service.TorrentService;
import cn.zqat.qbittorrenttorainyunos.utils.SizeUtils;
import cn.zqat.qbittorrenttorainyunos.utils.qBittorrentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TorrentServiceImpl implements TorrentService {

    @Autowired
    private qBittorrentUtils qbu;
    @Autowired
    private SizeUtils szu;

    @Override
    public Integer getTorrentCount() {
        return qbu.getTorrents("all").size() - qbu.getTorrents("completed").size();
    }

    @Override
    public List<TorrentResult> getTorrents() {
        List<TorrentResult> list = new ArrayList<>();
        for(Torrent torrent : qbu.getTorrents("all")){
            TorrentResult tor = new TorrentResult();
            tor.setName(torrent.getName());
            tor.setDsize(szu.getSize(torrent.getDownloaded()));
            tor.setSize(szu.getSize(torrent.getTotal_size()));
            tor.setProgress((int) (torrent.getProgress() * 100));
            tor.setSpeed(szu.getSize(torrent.getDlspeed()) + "/s");
            tor.setPeers(torrent.getNum_seeds() + " / " + torrent.getNum_complete());
            switch (torrent.getState()) {
                case "stalledDL", "pausedDL", "queuedDL" -> tor.setStatus(0);
                case "downloading", "forcedDL" -> tor.setStatus(1);
                case "uploading", "stalledUP", "forcedUP" -> tor.setStatus(2);
                case "error", "missingFiles", "unknown" -> tor.setStatus(3);
                case "pausedUP", "checkingUP", "queuedUP" -> tor.setStatus(5);
                default -> tor.setStatus(4);
            }
            list.add(tor);
        }
        return list;
    }
}
