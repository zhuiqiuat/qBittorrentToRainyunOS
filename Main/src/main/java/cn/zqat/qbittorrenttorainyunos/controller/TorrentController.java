package cn.zqat.qbittorrenttorainyunos.controller;

import cn.zqat.qbittorrenttorainyunos.schedule.SseTask;
import cn.zqat.qbittorrenttorainyunos.service.TorrentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@Slf4j
@RequestMapping("/torrent")
public class TorrentController {

    @Autowired
    private SseTask sseTask;

    @GetMapping(value = "/count",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getTorrentCount() {
        SseEmitter sseEmitter = new SseEmitter(0L);

        sseTask.addDownloadCnt(sseEmitter);

        return sseEmitter;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getTorrents() {
        SseEmitter sseEmitter = new SseEmitter(0L);

        sseTask.addDownloadList(sseEmitter);

        return sseEmitter;
    }
}
