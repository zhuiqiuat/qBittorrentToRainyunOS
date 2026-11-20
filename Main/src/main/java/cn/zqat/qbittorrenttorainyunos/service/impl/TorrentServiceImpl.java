package cn.zqat.qbittorrenttorainyunos.service.impl;

import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;
import cn.zqat.qbittorrenttorainyunos.schedule.qBittorrentTask;
import cn.zqat.qbittorrenttorainyunos.service.TorrentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class TorrentServiceImpl implements TorrentService {

    @Autowired
    private qBittorrentTask qbtt;

    public Integer getActiveDownloadCount() {
        int cnt = 0;
        for(Torrent torrent : qbtt.getHashTorrents().values()){
            if(torrent.getStatus() == 2){
                cnt++;
            }
        }
        return cnt;
    }

    public Collection<Torrent> getDownloadList() {
        return qbtt.getHashTorrents().values();
    }

    @Override
    public Integer getActiveUploadCount() {
        Integer cnt = 0;
        for(Torrent torrent : qbtt.getHashTorrents().values()){
            if(torrent.getStatus() == 5){
                cnt++;
            }
        }
        return cnt;
    }

    @Override
    public Collection<Torrent> getUploadList() {
        return qbtt.getHashTorrents().values().stream()
                .filter(torrent -> torrent.getStatus() == 5 || torrent.getStatus() == 8)
                .toList();
    }
}
