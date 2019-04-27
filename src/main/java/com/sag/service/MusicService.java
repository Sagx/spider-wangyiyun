package com.sag.service;

import com.sag.entity.Comment;
import com.sag.entity.Song;
import com.sag.repository.CommentRepository;
import com.sag.repository.SongRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MusicService {
	@Resource
	private SongRepository songRepository;
	@Resource
	private CommentRepository commentRepository;

	public void addSong(Song song) {
		//判断数据是否存在
		if (songRepository.countBySongId(song.getSongId()) == 0) {
			songRepository.save(song);
		}
	}

	public void addComment(Comment comment) {
		//判断数据是否存在
		if (commentRepository.countByCommentId(comment.getCommentId()) == 0) {
			commentRepository.save(comment);
		}
	}

	//批量存储
	public void addComments(List<Comment> comments) {
		commentRepository.saveAll(comments);
	}
}
