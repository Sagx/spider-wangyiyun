package com.sag.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "song")
public class Song {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer	id;
	private String songId;
	private String name;
	private String singer;
	private String album;
	private String url;
	private Integer commentCount;
	private String getTime;

}
