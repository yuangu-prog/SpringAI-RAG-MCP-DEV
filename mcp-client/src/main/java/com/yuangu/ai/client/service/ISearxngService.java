package com.yuangu.ai.client.service;

import com.yuangu.ai.client.entity.searxng.SearchResult;

import java.util.List;

public interface ISearxngService {


    /**
     * 联网搜索
     *
     * @param question 问题
     * @return 搜索结果
     */
    List<SearchResult> search(String question);
}
