package com.example.exodia.board.domain;

import com.example.exodia.board.dto.BoardDetailDto;
import com.example.exodia.board.dto.BoardListResDto;
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

    // 작성자 정보 (익명 게시글의 경우 null, 수정 필요)
    @ManyToOne
    @JoinColumn(name = "user_num", nullable = true)
    private User user;

    // 익명 여부 필드 추가
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "del_yn", nullable = false)
    private DelYN delYn = DelYN.N;

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false;

    // 게시물 목록 DTO로 변환
    public BoardListResDto listFromEntity() {
        return BoardListResDto.builder()
                .id(this.id)
                .title(this.title)
                .category(category)
                .hits(this.hits)
                .user_num(this.isAnonymous ? "익명" : this.user.getUserNum())
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
                .user_num(this.isAnonymous ? "익명" : this.user.getUserNum())
                .category(category)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .filePaths(filePaths)
                .build();
    }
}
