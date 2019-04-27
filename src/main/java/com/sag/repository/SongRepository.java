package com.sag.repository;

import com.sag.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Integer> {
	int countBySongId(String songId);
}
