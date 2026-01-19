package com.yuangu.ai.client.controller;

import com.yuangu.ai.client.entity.common.ApiResponse;
import com.yuangu.ai.client.entity.searxng.SearchResult;
import com.yuangu.ai.client.service.ISearxngService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * SearxngController
 *
 * @author ckliu
 * @since 2026-01-19 16:56:14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/searxng")
public class SearxngController {


    private final ISearxngService searxngService;

    @GetMapping("/search")
    public ApiResponse<List<SearchResult>> search(@RequestParam(value = "question") String question) {

        List<SearchResult> search = searxngService.search(question);
        return ApiResponse.success(search);
    }

}
