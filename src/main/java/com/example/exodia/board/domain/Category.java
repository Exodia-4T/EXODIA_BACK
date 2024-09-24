package com.example.exodia.board.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    NOTICE("CATEGORY_NOTICE","공지사항"),
    ANONYMOUS("CATEGORY_ANONYMOUS","익명"),
    FAMILY_EVENT("CATEGORY_FAMILY_EVENT","경조사");

    private final String key;
    private final String string;
}
