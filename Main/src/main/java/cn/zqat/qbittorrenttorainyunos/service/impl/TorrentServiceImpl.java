package cn.zqat.qbittorrenttorainyunos.service.impl;

import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;
import cn.zqat.qbittorrenttorainyunos.service.TorrentService;
import cn.zqat.qbittorrenttorainyunos.utils.qBittorrentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TorrentServiceImpl implements TorrentService {

    @Autowired
    private qBittorrentUtils qbu;

    public Integer getDownloadCount() {
        return qbu.getTorrents("all").size() - qbu.getTorrents("completed").size();
    }

    public List<Torrent> getDownloadList() {
        List<Torrent> list = qbu.getTorrents("all");
        for(Torrent torrent : list){
            torrent.setProgressPercent((int) (torrent.getProgress() * 100));
            switch (torrent.getState()) {
                case "stalledDL", "pausedDL", "queuedDL" -> torrent.setStatus(0);
                case "downloading", "forcedDL" -> torrent.setStatus(1);
                case "uploading", "stalledUP", "forcedUP" -> torrent.setStatus(2);
                case "error", "missingFiles", "unknown" -> torrent.setStatus(3);
                case "pausedUP", "checkingUP", "queuedUP" -> torrent.setStatus(5);
                default -> torrent.setStatus(4);
            }
        }
        return list;
    }
}
