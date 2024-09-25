package com.example.exodia.board.domain;

import lombok.*;

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

    @Column(nullable = false, length = 2083)
    private String filePath; // 파일 경로

    @Column(nullable = false)
    private String fileType; // MIME 타입

    @Column(nullable = false)
    private String fileName; // 파일 이름

    @Column(nullable = false)
    private Long fileSize; // 파일 크기

    @Column(nullable = true, length = 2083)
    private String fileDownloadUrl; // 다운로드 URL

    // 정적 생성 메서드 (빌더를 활용)
    public static BoardFile createBoardFile(Board board, String filePath, String fileType, String fileName, Long fileSize, String fileDownloadUrl) {
        return BoardFile.builder()
                .board(board)
                .filePath(filePath)
                .fileType(fileType)
                .fileName(fileName)
                .fileSize(fileSize)
                .fileDownloadUrl(fileDownloadUrl)
                .build();
    }
}
