package cn.zqat.qbittorrenttorainyunos.controller;

import cn.zqat.qbittorrenttorainyunos.schedule.SSETask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Slf4j
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private SSETask sseTask;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter upload(Integer count){
        SseEmitter sseEmitter = new SseEmitter(0L);

        sseTask.addUploadList(sseEmitter,count);

        return sseEmitter;
    }

    @GetMapping(value = "/count", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getUploadCount(){
        SseEmitter sseEmitter = new SseEmitter(0L);

        sseTask.addUploadCnt(sseEmitter);

        return sseEmitter;
    }
}
