package cn.zqat.qbittorrenttorainyunos.config;

import cn.zqat.qbittorrenttorainyunos.interceptor.GlobalInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private GlobalInterceptor gi;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(gi).addPathPatterns("/**").excludePathPatterns("/login");
    }
}
