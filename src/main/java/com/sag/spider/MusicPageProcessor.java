package com.sag.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.sag.entity.Comment;
import com.sag.entity.Song;
import com.sag.service.MusicService;
import com.sag.util.Constants;
import com.sag.util.SpiderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫解析类
 */
@Slf4j
@Component
public class MusicPageProcessor implements PageProcessor {

	private Site site = Site.me()
			.setDomain(Constants.BASE_URL)
			.setSleepTime(1000)
			.setRetryTimes(30)
			.setCharset("utf-8")
			.setTimeOut(30000)
			.addHeader("Proxy-Authorization", SpiderUtil.authHeader())
			.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

	@Resource
	MusicService musicService;

	@Override
	public Site getSite() {
		return site;
	}

	/**
	 * 解析页面获取歌曲信息
	 * @param page  爬取的页面信息
	 */
	@Override
	public void process(Page page) {
		if (page.getUrl().regex(Constants.ALBUM_URL).match()) {
			//歌单页面 => 爬取歌曲URL添加到解析队列
			log.info("歌曲总数----->" + page.getHtml().xpath("//span[@id='playlist-track-count']/text()").toString());
			page.addTargetRequests(page.getHtml().xpath("//div[@id=\"song-list-pre-cache\"]").links().regex(Constants.MUSIC_URL).all());

		} else {
			//歌曲页面 => 爬取歌曲及评论信息
			String nowTime = SpiderUtil.stampToDate(System.currentTimeMillis());
			String url = page.getUrl().toString();
			Song song = new Song();
			song.setUrl(url);
			song.setGetTime(nowTime);
			song.setSongId(url.substring(url.indexOf("id=") + 3));
			song.setName(page.getHtml().xpath("//em[@class='f-ff2']/text()").toString());
			song.setSinger(page.getHtml().xpath("//p[@class='des s-fc4']/span/a/text()").toString());
			song.setAlbum(page.getHtml().xpath("//p[@class='des s-fc4']/a/text()").toString());
			//爬取评论
			log.info("歌曲【{}】开始爬取...",song.getName());
			int commentCount = this.getComment(song);
			if (commentCount > 0){
				//page.putField("song", song);
				song.setCommentCount(commentCount);
				musicService.addSong(song);
				log.info("歌曲【{}】爬取完成！",song.getName());
			}
		}
	}

	/**
	 * 获取评论并入库（使用JSONPath解析结果）
	 * @param song  要爬取的歌曲信息
	 * @return      爬取成功的评论条数
	 */
	private int getComment(Song song) {
		String songId = song.getSongId();
		int commentCount = 0;
		//获取评论总数
		String resTemp = SpiderUtil.httpPost(songId, 0);
		if (validateRes(resTemp)) {
			commentCount = (Integer) JSONPath.eval(JSONObject.parseObject(resTemp),"$.total");
		}
		for (int offset = 0; offset < commentCount; offset += Constants.ONE_PAGE) {
			log.info("{}/{} {}",offset,commentCount,song.getName());
			String res = SpiderUtil.httpPost(songId, offset);
			if (!validateRes(res)) {
				log.error("SongName:" + song.getName() + "，offset:" + (offset - Constants.ONE_PAGE));
				break;
			}
			JSONObject resJson = JSON.parseObject(res);
			List<Integer> commentIds = SpiderUtil.cast(JSONPath.eval(resJson, "$.comments.commentId"));
			List<String> contents = SpiderUtil.cast(JSONPath.eval(resJson, "$.comments.content"));
			List<Integer> userIds = SpiderUtil.cast(JSONPath.eval(resJson, "$.comments.user.userId"));
			List<Integer> likedCounts = SpiderUtil.cast(JSONPath.eval(resJson, "$.comments.likedCount"));
			List<String> nicknames = SpiderUtil.cast(JSONPath.eval(resJson, "$.comments.user.nickname"));
			List<Long> times = SpiderUtil.cast(JSONPath.eval(resJson, "$.comments.time"));
			List<Comment> comments = new ArrayList<>();
			for (int i = 0; i < contents.size(); i++) {
				//评论入库
				Comment comment = new Comment();
				comment.setCommentId(commentIds.get(i));
				comment.setSongId(songId);
				comment.setSongName(song.getName());
				comment.setContent(SpiderUtil.filterEmoji(contents.get(i)));
				comment.setLikeCount(likedCounts.get(i));
				comment.setNickname(nicknames.get(i));
				comment.setTime(SpiderUtil.stampToDate(times.get(i)));
				comment.setUserId(String.valueOf(userIds.get(i)));
				comments.add(comment);
			}
			musicService.addComments(comments);
			try {
				//防反爬策略：随机间隔请求
				Thread.sleep((long)(Math.random()*1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return commentCount;
	}

	private boolean validateRes(String response){
		if (response == null || response.contains("503 Service Temporarily Unavailable") || response.equals("{\"code\":-460,\"msg\":\"Cheating\"}")) {
			log.error("爬取失败！ Res:{}",response);
			return false;
		}
		return true;
	}

}
