package cn.zqat.qbittorrenttorainyunos.service.impl;

import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;
import cn.zqat.qbittorrenttorainyunos.schedule.qBittorrentTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TorrentServiceImplTest {

    private TorrentServiceImpl torrentService;

    @BeforeEach
    void setUp() {
        qBittorrentTask task = new qBittorrentTask();
        task.setHashTorrents(Map.of(
                "downloading-1", torrent("downloading-1", 2),
                "downloading-2", torrent("downloading-2", 2),
                "uploading", torrent("uploading", 5),
                "failed", torrent("failed", 8),
                "finished", torrent("finished", 6)
        ));

        torrentService = new TorrentServiceImpl();
        ReflectionTestUtils.setField(torrentService, "qbtt", task);
    }

    @Test
    void countsOnlyActiveDownloads() {
        assertEquals(2, torrentService.getActiveDownloadCount());
    }

    @Test
    void countsOnlyActiveUploads() {
        assertEquals(1, torrentService.getActiveUploadCount());
    }

    @Test
    void returnsUploadingAndFailedTorrentsInUploadList() {
        assertEquals(
                java.util.Set.of("uploading", "failed"),
                torrentService.getUploadList().stream().map(Torrent::getHash).collect(java.util.stream.Collectors.toSet())
        );
    }

    private Torrent torrent(String hash, int status) {
        Torrent torrent = new Torrent();
        torrent.setHash(hash);
        torrent.setStatus(status);
        return torrent;
    }
}
