package cn.zqat.qbittorrenttorainyunos.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SizeUtilsTest {

    private final SizeUtils sizeUtils = new SizeUtils();

    @ParameterizedTest
    @CsvSource({
            "0, 0 B",
            "1023, 1023 B",
            "1024, 1.00 KB",
            "1536, 1.50 KB",
            "1048576, 1.00 MB",
            "1073741824, 1.00 GB",
            "1099511627776, 1.00 TB"
    })
    void formatsBytesUsingTheExpectedUnit(long bytes, String expected) {
        assertEquals(expected, sizeUtils.getSize(bytes));
    }
}
