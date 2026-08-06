package cn.zqat.qbittorrenttorainyunos.config;

import cn.zqat.qbittorrenttorainyunos.pojo.Result;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 将 Result 序列化为 text/event-stream（SSE）事件，
 * 使全局异常处理器在 SSE 请求上仍能返回 Result。
 * 与 SseEmitter 推送格式保持一致：data:<json>\n\n
 */
@Component
public class SseResultMessageConverter extends AbstractHttpMessageConverter<Object> {

    private final ObjectMapper objectMapper;

    public SseResultMessageConverter(ObjectMapper objectMapper) {
        super(MediaType.TEXT_EVENT_STREAM);
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz == Result.class;
    }

    /**
     * 只在 mediaType 明确为 text/event-stream 时接管写入。
     * SseEmitter.send(Object) 内部传入的 mediaType 为 null，会走默认的 Jackson 转换器，
     * 避免干扰正常 SSE 推送（否则会出现重复的 data: 前缀）。
     */
    @Override
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return clazz == Result.class && mediaType != null && super.canWrite(clazz, mediaType);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) {
        return null;
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException {
        String sse = "data:" + objectMapper.writeValueAsString(o) + "\n\n";
        outputMessage.getBody().write(sse.getBytes(StandardCharsets.UTF_8));
    }
}
