package com.sag.spider;

import com.sag.entity.Comment;
import com.sag.entity.Song;
import com.sag.repository.CommentRepository;
import com.sag.repository.SongRepository;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 爬虫结果处理类
 * 此类可定制持久化方式，若不想入库也可存到excel文件
 */
@Component
public class MusicPipeline implements Pipeline {

	@Resource
	public SongRepository songRepository;

	@Resource
	public CommentRepository commentRepository;

	@Override
	public void process(ResultItems resultItems, Task task) {
		for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
			if (entry.getKey().equals("song")) {
				Song song = (Song) entry.getValue();
				if (songRepository.countBySongId(song.getSongId()) == 0) {
					songRepository.save(song);
				}
			} else {
				commentRepository.save((Comment) entry.getValue());
			}
		}
	}

}
