package com.example.exodia.chat.repository;

import com.example.exodia.chat.domain.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatFileERepository extends JpaRepository<ChatFile, Long> {
}
