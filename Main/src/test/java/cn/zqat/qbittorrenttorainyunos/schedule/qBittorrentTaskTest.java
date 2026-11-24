package cn.zqat.qbittorrenttorainyunos.schedule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class qBittorrentTaskTest {

    @Test
    void usesTorrentDirectoryNameWithoutNestedDirectories(@TempDir Path tempDir) throws IOException {
        Path contentRoot = Files.createDirectories(tempDir.resolve("Show"));
        Path nestedDirectory = Files.createDirectories(contentRoot.resolve("Season 1").resolve("Disc 1"));
        Files.writeString(nestedDirectory.resolve("01.mp4"), "video");

        assertEquals("Show", qBittorrentTask.getObjectKeyPrefix(contentRoot));
    }

    @Test
    void usesEmptyPrefixForSingleFileTorrent(@TempDir Path tempDir) throws IOException {
        Path contentRoot = Files.writeString(tempDir.resolve("movie.mp4"), "video");

        assertEquals("", qBittorrentTask.getObjectKeyPrefix(contentRoot));
    }

    @Test
    void usesEmptyPrefixWhenContentPathDoesNotExist(@TempDir Path tempDir) {
        assertEquals("", qBittorrentTask.getObjectKeyPrefix(tempDir.resolve("missing")));
    }

    @Test
    void usesDirectoryNameEvenWhenPathHasTrailingSeparator(@TempDir Path tempDir) throws IOException {
        Path contentRoot = Files.createDirectories(tempDir.resolve("Show"));

        assertEquals("Show", qBittorrentTask.getObjectKeyPrefix(Path.of(contentRoot + "/")));
    }
}
