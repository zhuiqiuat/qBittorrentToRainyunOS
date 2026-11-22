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
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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

    @Scheduled(fixedDelay = 1000)
    public void task(){
        try {
            String cookie = qbt.login();
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
                    AtomicInteger failed = new AtomicInteger();
                    try (Stream<Path> walk = Files.walk(Paths.get(torrent.getContent_path()))) {
                        walk.filter(Files::isRegularFile).forEach(path ->  {
                            String[] cpath = path.getParent().toString().split("\\\\");
                            String prefix = cpath.length >= 3 ? cpath[2] : "";
                            MyFile myFile = null;
                            try {
                                myFile = new MyFile(prefix,path,4, Files.size(path));
                            } catch (IOException e) {
                                failed.getAndIncrement();
                                log.error("文件遍历时出错");
                                log.error(e.getMessage());
                            }
                            if(myFile != null){
                                now.getFiles().add(myFile);
                            }
                        });
                    }catch (IOException | UncheckedIOException e){
                        log.error("种子 {} 文件读取出错，已跳过",now.getName());
                        log.error(e.getMessage());
                        now.getFiles().clear();
                        now.setStatus(3);
                        continue;
                    }
                    if(failed.get() > 0){
                        log.error("种子 {} 文件读取出错，已跳过",now.getName());
                        now.getFiles().clear();
                        now.setStatus(3);
                        continue;
                    }
                    if(now.getFiles().isEmpty()){
                        log.error("文件夹 {} 下没有文件，直接跳过",now.getContent_path());
                        now.setStatus(6);
                        rem.add(now.getHash());
                        hashTorrents.remove(now.getHash());
                        continue;
                    }
                    now.setUploaded(0L);
                    now.setStatus(4);
                    now.getUnCompletedCnt().set(now.getFiles().size());
                    torrents.add(now);
                }else if(now.getStatus() == 4 || now.getStatus() == 5 || now.getStatus() == 8){
                    if(now.getUnCompletedCnt().get() == 0){
                        rem.add(now.getHash());
                        now.setSize(now.getTotal_size());
                        now.setStatus(6);
                        now.setUpdateTime(LocalDateTime.now());
                        hashTorrents.remove(now.getHash());
                        recordService.add(now);
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
                qbt.deleteTorrents(cookie,rem,true);
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
