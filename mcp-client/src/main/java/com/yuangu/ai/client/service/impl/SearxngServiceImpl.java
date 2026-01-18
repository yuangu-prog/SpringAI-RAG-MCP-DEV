package com.yuangu.ai.client.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.yuangu.ai.client.entity.searxng.SearchResult;
import com.yuangu.ai.client.entity.searxng.SearxngResponse;
import com.yuangu.ai.client.service.ISearxngService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearxngServiceImpl implements ISearxngService {


    @Value("${internet.search.searxng.url}")
    private String searxngUrl;

    @Value("${internet.search.searxng.top-k:5}")
    private int topK;

    private final OkHttpClient okHttpClient;

    @Override
    public List<SearchResult> search(String question) {

        // 构建请求的 URL
        HttpUrl httpUrl = HttpUrl.get(searxngUrl).newBuilder().addQueryParameter("q", question).addQueryParameter("format", "json").build();
        log.info("searxng url = {}", httpUrl.url());


        Request request = new Request.Builder().url(httpUrl).build();
        try (Response response = okHttpClient.newCall(request).execute()) {

            log.info("response = {}", JSONObject.toJSONString(response));
            if (!response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("请求失败：url = " + httpUrl.url());
            }

            // 先将响应体内容读取到字符串，确保在 Response 关闭前完成读取
            return Optional.ofNullable(JSONObject.parseObject(response.body().string(), SearxngResponse.class).getResults()).orElse(Collections.emptyList())
                    .stream().limit(topK).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
