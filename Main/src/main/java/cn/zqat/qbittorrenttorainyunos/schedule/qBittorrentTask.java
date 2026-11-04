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

    private Map<String,Torrent> mp = new ConcurrentHashMap<>();
    private Queue<MyFile> q = new ArrayDeque<>();

    @Scheduled(fixedDelay = 1000)
    public void task(){
        try {
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
            List<Torrent> tmp = qbt.getTorrents("completed");
            List<Torrent> torrents = new ArrayList<>();
            List<String> rem = new ArrayList<>();
            for(Torrent torrent : tmp){
                if(mp.containsKey(torrent.getHash())){
                    Torrent now = mp.get(torrent.getHash());
                    if(now.getUnCompletedCnt().get() == 0){
                        q.addAll(now.getFiles());
                        rem.add(now.getHash());
                        now.setSize(now.getTotal_size());
                        now.setStatus(5);
                        now.setUpdateTime(LocalDateTime.now());
                        recordService.add(now);
                        mp.remove(now.getHash());
                    }else{
                        torrents.add(now);
                    }
                }else{
                    try (Stream<Path> walk = Files.walk(Paths.get(torrent.getContent_path()))) {
                        walk.filter(Files::isRegularFile).forEach(path ->  {
                            String[] cpath = path.getParent().toString().split("\\\\");
                            String prefix = cpath.length >= 3 ? cpath[2] : "";
                            MyFile myFile = null;
                            try {
                                myFile = new MyFile(prefix,path,0,0L, Files.size(path),0L,0);
                            } catch (IOException e) {
                                log.error(e.getMessage());
                            }
                            torrent.getFiles().add(myFile);
                        });
                    }
                    torrent.getUnCompletedCnt().set(torrent.getFiles().size());
                    mp.put(torrent.getHash(),torrent);
                    torrents.add(torrent);
                }
            }
            if(!rem.isEmpty()){
                String cookie = qbt.login();
                qbt.deleteTorrents(cookie,rem);
                qbt.logout(cookie);
            }
            if(!torrents.isEmpty()){
                os.uploadFiles(torrents);
            }
        } catch (IOException e) {
            log.error("出现错误 {}",e.getMessage());
        }
    }

}
