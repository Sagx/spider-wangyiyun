package com.sag.util;

/**
 * 常量
 */
public class Constants {

    // 主域名
    public static final String BASE_URL = "http://music.163.com/";

    // 匹配专辑URL，正则表达式\\. \\转义java中的\ \.转义正则中的.
    public static final String ALBUM_URL = "http://music\\.163\\.com/playlist\\?id=\\d+";

    // 匹配歌曲URL
    public static final String MUSIC_URL = "http://music\\.163\\.com/song\\?id=\\d+";

    //单次请求爬取评论条数
    public static final int ONE_PAGE = 20;

    //代理host
    public static final String PROXY_HOST = "forward.xdaili.cn";

    //代理port
    public static final int PROXY_PORT = 80;

    //代理订单号
    public static final String PROXY_ORDER_NO = "#######################";

    //代理secret
    public static final String PROXY_SECRET = "#######################";

}
