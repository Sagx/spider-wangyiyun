package com.sag.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "comment")
public class Comment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer	id;
	private String songId;
	private String songName;
	private String userId;
	private String nickname;
	private String content;
	private Integer likeCount;
	private String time;
	private Integer commentId;

}
