package com.yuangu.ai.client.entity.searxng;

import lombok.Data;

import java.util.List;

@Data
public class SearxngResponse {

    private String query;

    private List<SearchResult> results;
}
