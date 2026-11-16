package cn.zqat.qbittorrenttorainyunos.service.impl;

import cn.zqat.qbittorrenttorainyunos.pojo.Overview;
import cn.zqat.qbittorrenttorainyunos.service.OverviewService;
import cn.zqat.qbittorrenttorainyunos.service.TorrentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OverviewServiceImpl implements OverviewService {

    @Autowired
    private TorrentService torrentService;

    public Overview getOverview() {
        return new Overview(torrentService.getActiveDownloadCount(), torrentService.getActiveUploadCount(),torrentService.getUploadList());
    }
}
