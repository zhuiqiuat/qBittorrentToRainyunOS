package cn.zqat.qbittorrenttorainyunos.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SsePara {
    private SseEmitter sseEmitter;
    private List<Object> para;
}
