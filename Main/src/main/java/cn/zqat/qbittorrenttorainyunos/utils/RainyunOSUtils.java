package cn.zqat.qbittorrenttorainyunos.utils;

import cn.zqat.qbittorrenttorainyunos.config.RainyunConfig;
import cn.zqat.qbittorrenttorainyunos.pojo.MyFile;
import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.progress.TransferListener;
import software.amazon.awssdk.transfer.s3.progress.TransferProgressSnapshot;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RainyunOSUtils {

    @Autowired
    private S3TransferManager s3TransferManager;

    @Autowired
    private RainyunConfig config;

    private final AtomicInteger cnt = new AtomicInteger(0);

    public void uploadFiles(List<Torrent> torrents){
        for(Torrent torrent : torrents){
            for(MyFile file : torrent.getFiles()){
                if(file.getStatus() != 4 && file.getStatus() != 8){
                    continue;
                }
                if(cnt.get() < 5){
                    upload(file,torrent);
                }
            }
        }
    }

    public void upload(MyFile file,Torrent torrent){
        String path = file.getPrefix();
        String name = file.getPath().getFileName().toString();
        String key = "";
        if(path != null && !path.isEmpty()){
            key += path + "/";
        }
        key += name;
        String finalKey = key;
        cnt.incrementAndGet();
        FileUpload fileUpload = s3TransferManager.uploadFile(b -> b.putObjectRequest(r -> r.bucket(config.getBucketname())
                .key(finalKey)).source(file.getPath()).addTransferListener(new TransferListener() {
            @Override
            public void transferInitiated(Context.TransferInitiated context) {
                log.info("{} 开始上传",finalKey);
                file.setStartTime(System.currentTimeMillis());
                torrent.setStatus(5);
                if(file.getStatus() != 5){
                    file.setStatus(5);
                }
            }

            @Override
            public void bytesTransferred(Context.BytesTransferred context) {
                TransferProgressSnapshot snapshot = context.progressSnapshot();
                long transferredBytes = snapshot.transferredBytes();
                if(file.getSentBytes() > transferredBytes){
                    file.setStartTime(System.currentTimeMillis());
                }
                torrent.setUploaded(torrent.getUploaded() + transferredBytes - file.getSentBytes());
                file.setSentBytes(transferredBytes);
                file.setProgress((int) (snapshot.ratioTransferred().orElse(0) * 100));
            }

            @Override
            public void transferComplete(Context.TransferComplete context) {
                log.info("{} 上传成功",finalKey);
                torrent.getUnCompletedCnt().decrementAndGet();
                file.setStatus(6);
                cnt.decrementAndGet();
            }

            @Override
            public void transferFailed(Context.TransferFailed context) {
                cnt.decrementAndGet();
                torrent.setUploaded(torrent.getUploaded() - file.getSentBytes());
                file.setStatus(8);
                torrent.setStatus(8);
                file.setProgress(0);
                file.setSentBytes(0L);
                log.error("{} 上传失败,将在 1 秒内重试",finalKey);
                Throwable e = context.exception();
                log.error("失败原因: {}",e.getMessage());
            }
        }));
        fileUpload.completionFuture();
    }
}
