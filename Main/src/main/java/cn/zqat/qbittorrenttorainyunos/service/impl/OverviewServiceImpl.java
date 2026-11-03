package cn.zqat.qbittorrenttorainyunos.service.impl;

import cn.zqat.qbittorrenttorainyunos.pojo.Overview;
import cn.zqat.qbittorrenttorainyunos.service.OverviewService;
import cn.zqat.qbittorrenttorainyunos.service.TorrentService;
import cn.zqat.qbittorrenttorainyunos.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OverviewServiceImpl implements OverviewService {

    @Autowired
    private TorrentService torrentService;

    @Autowired
    private UploadService uploadService;

    public Overview getOverview() {
        return new Overview(torrentService.getTorrentCount(), uploadService.getUploadCount(),uploadService.getUploadList());
    }
}
