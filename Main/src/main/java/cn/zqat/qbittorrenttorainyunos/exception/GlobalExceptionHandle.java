package cn.zqat.qbittorrenttorainyunos.exception;

import cn.zqat.qbittorrenttorainyunos.pojo.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandle {

    @ExceptionHandler
    public Result handleException(Exception e, HttpServletResponse response){
        // 响应已提交说明客户端大概率已断开，写什么都没人收，直接放弃
        if (response.isCommitted()) {
            log.warn("响应已提交（客户端可能已断开），无法写回 Result: {}", e.getMessage());
            return null;
        }
        log.error(e.getMessage(), e);
        return Result.err(e.getMessage());
    }
}
