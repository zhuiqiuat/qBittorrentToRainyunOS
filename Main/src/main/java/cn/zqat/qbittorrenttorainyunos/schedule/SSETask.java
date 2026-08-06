package cn.zqat.qbittorrenttorainyunos.schedule;

import cn.zqat.qbittorrenttorainyunos.pojo.Result;
import cn.zqat.qbittorrenttorainyunos.pojo.SsePara;
import cn.zqat.qbittorrenttorainyunos.service.TorrentService;
import cn.zqat.qbittorrenttorainyunos.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SSETask {

    @Autowired
    private UploadService uploadService;
    @Autowired
    private TorrentService torrentService;


    private Map<String, SsePara> uploadCnt = new ConcurrentHashMap<>();
    private Map<String, SsePara> downloadCnt = new ConcurrentHashMap<>();
    private Map<String, SsePara> uploadList = new ConcurrentHashMap<>();
    private Map<String, SsePara> downloadList = new ConcurrentHashMap<>();

    @Scheduled(fixedDelay = 1500)
    public void task(){
        uploadCnt.forEach((k,v)->{
            try {
                v.getSseEmitter().send(Result.suc(uploadService.getUploadCount()));
            } catch (IOException e) {
                uploadCnt.remove(k);
            }
        });
        downloadCnt.forEach((k,v)->{
            try {
                v.getSseEmitter().send(Result.suc(torrentService.getTorrentCount()));
            } catch (IOException e) {
                downloadCnt.remove(k);
            }
        });
        uploadList.forEach((k,v)->{
            try {
                v.getSseEmitter().send(Result.suc(uploadService.getUploadList((Integer) v.getPara().getFirst())));
            } catch (IOException e) {
                uploadList.remove(k);
            }
        });
        downloadList.forEach((k,v)->{
            try {
                v.getSseEmitter().send(Result.suc(torrentService.getTorrents()));
            } catch (IOException e) {
                downloadList.remove(k);
            }
        });
    }

    public void addUploadCnt(SseEmitter sseEmitter){
        String clientId = UUID.randomUUID().toString();
        sseEmitter.onCompletion(() -> uploadCnt.remove(clientId));
        sseEmitter.onTimeout(() -> uploadCnt.remove(clientId));
        sseEmitter.onError(e -> uploadCnt.remove(clientId));
        uploadCnt.put(clientId,new SsePara(sseEmitter,null));
    }

    public void addDownloadCnt(SseEmitter sseEmitter){
        String clientId = UUID.randomUUID().toString();
        sseEmitter.onCompletion(() -> uploadCnt.remove(clientId));
        sseEmitter.onTimeout(() -> uploadCnt.remove(clientId));
        sseEmitter.onError(e -> uploadCnt.remove(clientId));
        downloadCnt.put(clientId,new SsePara(sseEmitter,null));
    }

    public void addUploadList(SseEmitter sseEmitter,Integer cnt){
        String clientId = UUID.randomUUID().toString();
        sseEmitter.onCompletion(() -> uploadCnt.remove(clientId));
        sseEmitter.onTimeout(() -> uploadCnt.remove(clientId));
        sseEmitter.onError(e -> uploadCnt.remove(clientId));
        uploadList.put(clientId,new SsePara(sseEmitter, Collections.singletonList(cnt)));
    }

    public void addDownloadList(SseEmitter sseEmitter){
        String clientId = UUID.randomUUID().toString();
        sseEmitter.onCompletion(() -> uploadCnt.remove(clientId));
        sseEmitter.onTimeout(() -> uploadCnt.remove(clientId));
        sseEmitter.onError(e -> uploadCnt.remove(clientId));
        downloadList.put(clientId,new SsePara(sseEmitter,null));
    }
}
