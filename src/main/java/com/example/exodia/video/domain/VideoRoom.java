package com.example.exodia.video.domain;

import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VideoRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomName;

    @Column
    private String password;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Column(nullable = false)
    private Boolean isActive = true;

    // 방에 입장한 참가자 수
    @Column(nullable = false)
    private int participantCount = 0;

    public void increaseParticipantCount() {
        this.participantCount += 1;
    }

    public void decreaseParticipantCount() {
        this.participantCount -= 1;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
