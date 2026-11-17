package cn.zqat.qbittorrenttorainyunos.utils;

import cn.zqat.qbittorrenttorainyunos.config.RainyunConfig;
import cn.zqat.qbittorrenttorainyunos.pojo.MyFile;
import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.progress.TransferListener;
import software.amazon.awssdk.transfer.s3.progress.TransferProgressSnapshot;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class RainyunOSUtils {

    @Autowired
    private RainyunConfig config;

    private int cnt = 0;

    public S3AsyncClient connect(){
        return S3AsyncClient.builder().region(Region.CN_NORTH_1).credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(config.getAccesskey(),config.getSecretkey()))).endpointOverride(URI.create(config.getEndpoint())).
                serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build()).build();
    }

    public void uploadFiles(List<Torrent> torrents){
        for(Torrent torrent : torrents){
            for(MyFile file : torrent.getFiles()){
                if(file.getStatus() != 4 && file.getStatus() != 7){
                    continue;
                }
                if(cnt < 5){
                    upload(file,torrent);
                }
            }
        }
    }

    public void upload(MyFile file,Torrent torrent){
        S3AsyncClient s3 = connect();
        ExecutorService executor = Executors.newFixedThreadPool(16);
        S3TransferManager s3tm = S3TransferManager.builder().s3Client(s3).executor(executor).build();
        String path = file.getPrefix();
        String name = file.getPath().getFileName().toString();
        String key = "";
        if(path != null && !path.isEmpty()){
            key += path + "/";
        }
        key += name;
        String finalKey = key;
        FileUpload fileUpload = s3tm.uploadFile(b -> b.putObjectRequest(r -> r.bucket(config.getBucketname())
                .key(finalKey)).source(file.getPath()).addTransferListener(new TransferListener() {
            @Override
            public void transferInitiated(Context.TransferInitiated context) {
                log.info("{} 开始上传",finalKey);
                file.setStartTime(System.currentTimeMillis());
                if(file.getStatus() != 5){
                    cnt++;
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
                cnt--;
                s3tm.close();
            }

            @Override
            public void transferFailed(Context.TransferFailed context) {
                cnt--;
                torrent.setUploaded(torrent.getUploaded() - file.getSentBytes());
                file.setStatus(7);
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
