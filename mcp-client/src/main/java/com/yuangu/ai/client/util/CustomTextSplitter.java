package com.yuangu.ai.client.util;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.Arrays;
import java.util.List;

public class CustomTextSplitter extends TextSplitter {

    @Override
    protected List<String> splitText(String text) {

        // 空格换行，本质就是按段落切分
        return List.of(text.split("\\s*\\R\\s*\\R\\s*"));
    }
}
