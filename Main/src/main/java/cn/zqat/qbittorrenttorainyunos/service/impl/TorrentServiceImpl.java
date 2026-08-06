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
    public Integer getTorrentCount() throws IOException {
        String cookie = qbu.login();
        int cnt = qbu.getTorrents(cookie,"downloading").size();
        qbu.logout(cookie);
        return cnt;
    }

    @Override
    public List<TorrentResult> getTorrents() throws IOException {
        List<TorrentResult> list = new ArrayList<>();
        String cookie = qbu.login();
        for(Torrent torrent : qbu.getTorrents(cookie,"all")){
            TorrentResult tor = new TorrentResult();
            tor.setName(torrent.getName());
            tor.setDsize(szu.getSize(torrent.getDownloaded()));
            tor.setSize(szu.getSize(torrent.getTotal_size()));
            tor.setProgress((int) (torrent.getProgress() * 100));
            tor.setSpeed(szu.getSize(torrent.getDlspeed()) + "/s");
            tor.setPeers(torrent.getNum_seeds() + " / " + torrent.getNum_complete());
            if(torrent.getState().equals("stalledDL") || torrent.getState().equals("pausedDL") || torrent.getState().equals("queuedDL")){
                tor.setStatus(0);
            }else if(torrent.getState().equals("downloading") || torrent.getState().equals("forcedDL")){
                tor.setStatus(1);
            }else if(torrent.getState().equals("uploading") || torrent.getState().equals("stalledUP") || torrent.getState().equals("forcedUP")){
                tor.setStatus(2);
            }else if(torrent.getState().equals("error") || torrent.getState().equals("missingFiles") || torrent.getState().equals("unknown")) {
                tor.setStatus(3);
            }else if(torrent.getState().equals("pausedUP") || torrent.getState().equals("checkingUP") || torrent.getState().equals("queuedUP")){
                tor.setStatus(5);
            }else{
                tor.setStatus(4);
            }
            list.add(tor);
        }
        qbu.logout(cookie);
        return list;
    }
}
