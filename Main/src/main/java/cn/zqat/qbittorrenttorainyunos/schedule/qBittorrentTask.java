package cn.zqat.qbittorrenttorainyunos.schedule;

import cn.zqat.qbittorrenttorainyunos.pojo.MyFile;
import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;
import cn.zqat.qbittorrenttorainyunos.service.RecordService;
import cn.zqat.qbittorrenttorainyunos.utils.RainyunOSUtils;
import cn.zqat.qbittorrenttorainyunos.utils.qBittorrentUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Slf4j
@Component
@Data
public class qBittorrentTask {

    @Autowired
    private qBittorrentUtils qbt;

    @Autowired
    private RainyunOSUtils os;

    @Autowired
    private RecordService recordService;

    private Map<String,Torrent> hashTorrents = new ConcurrentHashMap<>();
    private Queue<MyFile> q = new ArrayDeque<>();

    @Scheduled(fixedDelay = 1000)
    public void task(){
        try {
            String cookie = qbt.login();
            if(!q.isEmpty()){
                Queue<MyFile> fail = new ArrayDeque<>();
                for(MyFile file : q){
                    Files.deleteIfExists(file.getPath());
                    boolean deleted = Files.notExists(file.getPath());
                    if (deleted){
                        log.debug("本地文件 {} 删除成功",file.getPath().toAbsolutePath().toAbsolutePath());
                    }else{
                        log.error("本地文件 {} 删除失败",file.getPath().toAbsolutePath().toAbsolutePath());
                        log.error("失败原因 未知");
                        fail.add(file);
                    }
                }
                q.clear();
                q = fail;
            }
            List<Torrent> tmp = qbt.getTorrents("all",cookie);
            List<Torrent> torrents = new ArrayList<>();
            List<String> rem = new ArrayList<>();
            for(Torrent torrent : tmp){
                switch (torrent.getState()) {
                    case "stalledDL", "pausedDL", "queuedDL" -> torrent.setStatus(1);
                    case "downloading", "forcedDL" -> torrent.setStatus(2);
                    case "uploading", "stalledUP", "forcedUP", "queuedUP", "checkingUP", "stoppedUP" -> torrent.setStatus(3);
                    case "error", "missingFiles", "unknown" -> torrent.setStatus(7);
                    default -> torrent.setStatus(0);
                }
                torrent.setProgressPercent((int) (torrent.getProgress() * 100));
                if(torrent.getStatus() < 3){
                    hashTorrents.put(torrent.getHash(),torrent);
                    continue;
                }
                if(!hashTorrents.containsKey(torrent.getHash())){
                    hashTorrents.put(torrent.getHash(),torrent);
                }
                Torrent tmp2 = hashTorrents.get(torrent.getHash());
                if(tmp2.getStatus() < 3 && torrent.getStatus() == 3){
                    hashTorrents.put(torrent.getHash(),torrent);
                    tmp2 = torrent;
                }
                Torrent now = tmp2;
                if(now.getStatus() == 3){
                    now.setUploaded(0L);
                    now.setStatus(4);
                    try (Stream<Path> walk = Files.walk(Paths.get(torrent.getContent_path()))) {
                        walk.filter(Files::isRegularFile).forEach(path ->  {
                            String[] cpath = path.getParent().toString().split("\\\\");
                            String prefix = cpath.length >= 3 ? cpath[2] : "";
                            MyFile myFile = null;
                            try {
                                myFile = new MyFile(prefix,path,4, Files.size(path));
                            } catch (IOException e) {
                                log.error(e.getMessage());
                            }
                            now.getFiles().add(myFile);
                        });
                    }
                    now.getUnCompletedCnt().set(now.getFiles().size());
                    torrents.add(now);
                }else if(now.getStatus() == 4 || now.getStatus() == 5 || now.getStatus() == 8){
                    if(now.getUnCompletedCnt().get() == 0){
                        q.addAll(now.getFiles());
                        rem.add(now.getHash());
                        now.setSize(now.getTotal_size());
                        now.setStatus(6);
                        now.setUpdateTime(LocalDateTime.now());
                        recordService.add(now);
                        hashTorrents.remove(now.getHash());
                    }else{
                        double tspeed = 0;
                        int uploading = 0;
                        int failed = 0;
                        for(MyFile file : now.getFiles()){
                            file.setName(file.getPath().getFileName().toString());
                            long time = System.currentTimeMillis();
                            if(file.getStatus() == 4 || file.getStatus() == 8){
                                file.setSpeed(0L);
                                if(file.getStatus() == 8){
                                    failed++;
                                }
                            }else if(file.getStatus() == 5){
                                double speed = (double) file.getSentBytes() * 1000 / (time - file.getStartTime());
                                file.setSpeed((long) speed);
                                tspeed += speed;
                                uploading++;
                            }else if(file.getStatus() == 6){
                                file.setSpeed(0L);
                            }
                        }
                        now.setProgressPercent((int) (now.getUploaded() * 100 / now.getTotal_size()));
                        now.setUpspeed(-1L);
                        if(uploading == 0){
                            now.setStatus(failed > 0 ? 8 : 4);
                        }else{
                            now.setUpspeed((long) tspeed);
                            now.setStatus(5);
                        }
                        torrents.add(now);
                    }
                }
            }
            if(!rem.isEmpty()){
                qbt.deleteTorrents(cookie,rem);
            }
            qbt.logout(cookie);
            if(!torrents.isEmpty()){
                os.uploadFiles(torrents);
            }
        } catch (IOException e) {
            log.error("出现错误 {}",e.getMessage());
        }
    }

}
