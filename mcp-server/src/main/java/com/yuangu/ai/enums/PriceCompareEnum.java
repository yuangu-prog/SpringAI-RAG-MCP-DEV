package com.yuangu.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PriceCompareEnum {

    GREATER_THAN(">", "大于"),
    LESS_THAN("<", "小于"),
    GREATER_THAN_OR_EQUAL_TO(">=", "大于等于"),
    LESS_THAN_OR_EQUAL_TO("<=", "小于等于"),

    HIGHER_THAN(">", "高于"),
    LOWER_THAN("<", "低于"),
    NOT_HIGHER_THAN("<=", "不高于"),
    NOT_LOWER_THAN(">=", "不低于"),

    EQUAL_TO("=", "等于");

    ;

    private final String type;
    private final String value;
}
