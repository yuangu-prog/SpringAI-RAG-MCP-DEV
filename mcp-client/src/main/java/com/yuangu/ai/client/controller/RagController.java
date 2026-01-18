package com.yuangu.ai.client.controller;

import com.yuangu.ai.client.entity.common.ApiResponse;
import com.yuangu.ai.client.service.IDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagController {


    private final IDocumentService documentService;

    /**
     * 文件上传 & 文件分片 & 内容向量化
     *
     * @param file 文件信息
     * @return void
     */
    @PostMapping("/upload")
    public ApiResponse<Void> upload(@RequestPart MultipartFile file) {

        try {
            documentService.upload(file.getOriginalFilename(), file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ApiResponse.success();
    }

    /**
     * 知识库相似性查询
     *
     * @param question 查询内容
     * @return 查询结果
     */
    @GetMapping("/similaritySearch")
    public ApiResponse<List<Document>> similaritySearch(String question) {
        return ApiResponse.success(documentService.similaritySearch(question));
    }
}
