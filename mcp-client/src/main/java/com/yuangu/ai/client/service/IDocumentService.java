package com.yuangu.ai.client.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.List;

public interface IDocumentService {

    void upload(String fileName, InputStream is);

    List<Document> similaritySearch(String question);
}
