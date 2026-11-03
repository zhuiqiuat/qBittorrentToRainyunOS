package cn.zqat.qbittorrenttorainyunos.controller;

import cn.zqat.qbittorrenttorainyunos.pojo.Result;
import cn.zqat.qbittorrenttorainyunos.service.TorrentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/torrent")
public class TorrentController {

    @Autowired
    private TorrentService tors;

    @GetMapping
    public Result getTorrents(){
        return Result.suc(tors.getTorrents());
    }

}
