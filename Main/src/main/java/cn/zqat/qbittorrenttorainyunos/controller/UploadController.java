package cn.zqat.qbittorrenttorainyunos.controller;

import cn.zqat.qbittorrenttorainyunos.pojo.Result;
import cn.zqat.qbittorrenttorainyunos.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private UploadService us;

    @GetMapping
    public Result getUploads(Integer page,Integer size) {
        return Result.suc(us.getUploadList(size));
    }

    @GetMapping("/count")
    public Result getUploadCount() {
        return Result.suc(us.getUploadCount());
    }

}
