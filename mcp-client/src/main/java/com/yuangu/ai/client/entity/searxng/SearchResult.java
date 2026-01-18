package com.yuangu.ai.client.entity.searxng;

import lombok.Data;

@Data
public class SearchResult {
    private String title;
    private String url;
    private String content;
    private Double score;
}
