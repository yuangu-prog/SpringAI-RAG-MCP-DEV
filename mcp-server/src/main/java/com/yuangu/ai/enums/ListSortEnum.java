package com.yuangu.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ListSortEnum {

    ASC("asc", "正序"),
    DESC("desc", "倒序");

    public final String type;
    public final String value;
}
