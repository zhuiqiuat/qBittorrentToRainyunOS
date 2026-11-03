package cn.zqat.qbittorrenttorainyunos.service.impl;

import cn.zqat.qbittorrenttorainyunos.pojo.MyFile;
import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;
import cn.zqat.qbittorrenttorainyunos.pojo.UploadFileResult;
import cn.zqat.qbittorrenttorainyunos.pojo.UploadTorrentResult;
import cn.zqat.qbittorrenttorainyunos.schedule.qBittorrentTask;
import cn.zqat.qbittorrenttorainyunos.service.UploadService;
import cn.zqat.qbittorrenttorainyunos.utils.SizeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UploadServiceImpl implements UploadService {

    @Autowired
    private qBittorrentTask qbtt;

    @Autowired
    private SizeUtils szu;

    @Override
    public Integer getUploadCount() {
        Integer cnt = 0;
        for(Torrent torrent : qbtt.getMp().values()){
            for(MyFile file : torrent.getFiles()){
                if(file.getStatus() == 1){
                    cnt++;
                }
            }
        }
        return cnt;
    }

    @Override
    public List<UploadTorrentResult> getUploadList() {
        List<UploadTorrentResult> list = new ArrayList<>();
        for(Torrent torrent : qbtt.getMp().values()){
            UploadTorrentResult tor = new UploadTorrentResult();
            tor.setName(torrent.getName());
            tor.setHash(torrent.getHash());
            tor.setChildren(new ArrayList<>());
            long tusize = 0;
            long tsize = 0;
            double tspeed = 0;
            int watting = 0;
            int uploading = 0;
            int uploaded = 0;
            for(MyFile file : torrent.getFiles()){
                tusize += file.getSendedbytes();
                tsize += file.getTotalbytes();
                UploadFileResult fil = new UploadFileResult();
                fil.setName(file.getPath().getFileName().toString());
                fil.setUsize(szu.getSize(file.getSendedbytes()));
                fil.setSize(szu.getSize(file.getTotalbytes()));
                fil.setProgress(file.getProgress());
                long time = System.currentTimeMillis();
                if(file.getStatus() == 1){
                    double speed = (double) file.getSendedbytes() * 1000 / (time - file.getStartTime());
                    fil.setSpeed(szu.getSize((long) speed) + "/s"); // bytes/s
                    tspeed += speed;
                    uploading++;
                }else if(file.getStatus() == 2){
                    fil.setSpeed("已完成");
                    uploaded++;
                }else{
                    fil.setSpeed("");
                    watting++;
                }
                fil.setStatus(file.getStatus());
                tor.getChildren().add(fil);
            }
            tor.setTusize(szu.getSize(tusize));
            tor.setTsize(szu.getSize(tsize));
            tor.setTprogress((int) (tusize * 100 / tsize));
            tor.setTspeed("");
            if(uploaded == 0 && uploading == 0){
                tor.setTstatus(0);
            }else if(uploading == 0 && watting == 0){
                tor.setTstatus(2);
            }else{
                tor.setTspeed(szu.getSize((long) tspeed) + "/s");
                tor.setTstatus(1);
            }
            list.add(tor);
        }
        return list;
    }
}
