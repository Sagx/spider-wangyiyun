package com.sag;

import com.sag.spider.MusicPageProcessor;
import com.sag.spider.MusicPipeline;
import com.sag.util.Constants;
import com.sag.util.SpiderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import javax.annotation.Resource;

/**
 * 爬取网易云音乐评论
 * Created by Sag on 2019/4/27.
 */
@Slf4j
@SpringBootApplication
public class SpiderApplication implements ApplicationListener<ApplicationStartedEvent> {

    @Resource
    private MusicPageProcessor pageProcessor;

    @Resource
    private MusicPipeline pipeline;

    //初始爬取地址（可以是歌单也可以是歌曲）
    //private static final String START_URL = "http://music.163.com/playlist?id=2771669813";
    private static final String START_URL = "http://music.163.com/song?id=27207492";

    public static void main( String[] args ) {
        SpringApplication.run(SpiderApplication.class, args);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        log.info("爬取开始...");
        long start = System.currentTimeMillis();
        //代理设置
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy(Constants.PROXY_HOST,Constants.PROXY_PORT)));
        Spider.create(pageProcessor)
                .addUrl(START_URL)
                .setDownloader(httpClientDownloader)
                //.addPipeline(pipeline)
                .thread(3)
                .run();
        long end = System.currentTimeMillis();
        log.info("爬取结束,耗时--->" + SpiderUtil.parseMillisecond(end - start));
    }

}
