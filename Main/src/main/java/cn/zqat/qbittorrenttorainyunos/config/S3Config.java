package cn.zqat.qbittorrenttorainyunos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.net.URI;
import java.time.Duration;

@Configuration
public class S3Config {

    @Bean(destroyMethod = "close")
    public S3AsyncClient s3AsyncClient(RainyunConfig rainyunConfig) {
        return S3AsyncClient.builder().region(Region.CN_NORTH_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(rainyunConfig.getAccesskey(),rainyunConfig.getSecretkey())))
                .endpointOverride(URI.create(rainyunConfig.getEndpoint()))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .overrideConfiguration(o -> o
                        .apiCallTimeout(Duration.ofMinutes(30))
                        .apiCallAttemptTimeout(Duration.ofMinutes(5)))
                .build();
    }

    @Bean(destroyMethod = "close")
    public S3TransferManager s3TransferManager(S3AsyncClient client) {
        return S3TransferManager.builder().s3Client(client).build();
    }
}
