package com.example.exodia.board.domain;

import com.example.exodia.board.dto.BoardDetailDto;
import com.example.exodia.board.dto.BoardListResDto;
import com.example.exodia.board.dto.BoardUpdateDto;
import com.example.exodia.comment.domain.Comment;
import com.example.exodia.common.domain.BaseTimeEntity;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.user.domain.User;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "board")
@Where(clause = "del_yn = 'N'")
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private int hits = 0;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "del_yn", nullable = false)
    private DelYN delYn = DelYN.N;  // 기본값을 N으로 설정

    // 파일 경로 추가 (이미지뿐만 아니라 여러 파일 허용)
    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardFile> files = new ArrayList<>();


    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Comment> comments;

    // 상단 고정 여부 추가
    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    // 게시물 리스트 DTO로 변환
    public BoardListResDto listFromEntity() {
        return BoardListResDto.builder()
                .id(this.id)
                .title(this.title)
                .category(category)
                .hits(this.hits)
                .user_num(this.user.getUserNum())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .isPinned(this.isPinned)
                .build();
    }

    // 게시물 상세 DTO로 변환
    public BoardDetailDto detailFromEntity(List<String> filePaths) {
        return BoardDetailDto.builder()
                .id(this.getId())
                .title(this.getTitle())
                .content(this.getContent())
                .user_num(this.user.getUserNum())
                .category(category)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .filePaths(filePaths)
                .build();
    }



}
