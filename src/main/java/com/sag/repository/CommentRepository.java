package com.sag.repository;

import com.sag.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	int countByCommentId(int commentId);
}
