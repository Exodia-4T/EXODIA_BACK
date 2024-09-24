package com.example.exodia.board.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "board_file")
public class BoardFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(nullable = false)
    private String filePath; // 파일 경로

    @Column(nullable = false)
    private String fileType; // MIME 타입

    @Column(nullable = false)
    private String fileName; // 파일 이름

    @Column(nullable = false)
    private Long fileSize; // 파일 크기

    @Column(nullable = true)
    private String fileDownloadUrl; // 다운로드 URL
}

