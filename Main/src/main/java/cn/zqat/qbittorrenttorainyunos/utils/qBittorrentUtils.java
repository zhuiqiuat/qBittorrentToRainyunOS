package cn.zqat.qbittorrenttorainyunos.utils;

import cn.zqat.qbittorrenttorainyunos.config.qBittorrentConfig;
import cn.zqat.qbittorrenttorainyunos.pojo.Torrent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class qBittorrentUtils {

    @Autowired
    private qBittorrentConfig config;

    public String login() throws IOException {
        String nowurl = config.getUrl() + "/auth/login";
        log.debug("正在登录 qBittorrent...");

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            List<BasicNameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("username",config.getUsername()));
            list.add(new BasicNameValuePair("password",config.getPassword()));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
            ClassicHttpRequest getR = ClassicRequestBuilder.post(nowurl).setEntity(entity).build();
            return client.execute(getR, response -> {
                String cookie = response.getHeader("set-cookie").getValue();
                if(response.getCode() == 204){
                    log.debug("本轮 qBittorrent 登录成功");
                }else{
                    log.error("本轮 qBittorrent 登录失败");
                }
                return cookie.split(";")[0];
            });
        }
    }

    public void logout(String cookie) throws IOException {
        String nowurl = config.getUrl() + "/auth/logout";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ClassicHttpRequest getR = ClassicRequestBuilder.post(nowurl).build();
            getR.addHeader("Cookie",cookie);
            client.execute(getR, response -> {
                int code = response.getCode();
                if (code == 200){
                    log.debug("本轮 qBittorrent 登出成功");
                }else{
                    log.debug("本轮 qBittorrent 登出失败");
                }
                return null;
            });
        }
    }

    public List<Torrent> getTorrents(String filter) {
        try {
            String cookie = login();
            String nowurl = config.getUrl() + "/torrents/info?filter=";
            List<Torrent> torrentList;
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                ClassicHttpRequest getReq = ClassicRequestBuilder.get(nowurl + filter).build();
                getReq.addHeader("Cookie",cookie);
                    torrentList = client.execute(getReq, response -> {
                    String json = EntityUtils.toString(response.getEntity());
                    Gson gson = new Gson();
                    return gson.fromJson(json, new TypeToken<List<Torrent>>() {}.getType());
                });
            }
            logout(cookie);
            return torrentList;
        } catch (IOException e) {
            log.error("获取类型为 {} 的种子列表时出错",filter);
        }
        return new ArrayList<>();
    }

    public void deleteTorrents(String cookie,List<String> hashes) throws IOException {
        String nowurl = config.getUrl() + "/torrents/delete";
        StringBuilder h = new StringBuilder();
        for(int i = 0;i < hashes.size();i++){
            if(i > 0){
                h.append("|");
            }
            h.append(hashes.get(i));
        }
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            List<BasicNameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("hashes",h.toString()));
            list.add(new BasicNameValuePair("deleteFiles",String.valueOf(false)));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list);
            ClassicHttpRequest getR = ClassicRequestBuilder.post(nowurl).setEntity(entity).build();
            getR.addHeader("Cookie",cookie);
            client.execute(getR, response -> {
                int code = response.getCode();
                if (code == 200){
                    log.debug("qBittorrent 种子删除成功");
                }else{
                    log.error("qBittorrent 种子删除失败");
                }
                return null;
            });
        }
    }
}
