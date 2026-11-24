package cn.zqat.qbittorrenttorainyunos.schedule;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class qBittorrentTaskTest {

    @Test
    void usesThirdLevelFolderForSingleFileTorrent() {
        Path file = Path.of("/downloads/xxx/movie.mp4");

        assertEquals("xxx", qBittorrentTask.getObjectKeyPrefix(file));
    }

    @Test
    void usesThirdLevelFolderWithoutAddingNestedDirectories() {
        Path file = Path.of("/downloads/xxx/Season 1/Disc 1/01.mp4");

        assertEquals("xxx", qBittorrentTask.getObjectKeyPrefix(file));
    }

    @Test
    void usesEmptyPrefixWhenThirdLevelFolderIsUnavailable() {
        assertEquals("", qBittorrentTask.getObjectKeyPrefix(Path.of("/downloads/movie.mp4")));
    }

    @Test
    void normalizesPathBeforeSelectingThirdLevelFolder() {
        Path file = Path.of("/downloads/xxx/Season 1/../movie.mp4");

        assertEquals("xxx", qBittorrentTask.getObjectKeyPrefix(file));
    }

    @Test
    void supportsWindowsPathSeparators() {
        assertEquals("xxx", qBittorrentTask.getObjectKeyPrefix("D:\\downloads\\xxx"));
    }
}
