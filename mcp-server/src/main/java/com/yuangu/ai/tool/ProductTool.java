package com.yuangu.ai.tool;

import com.alibaba.fastjson2.JSONObject;
import com.yuangu.ai.entity.Product;
import com.yuangu.ai.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 产品信息工具类
 *
 * @author ckliu
 * @since 2026-01-20 21:51:26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductTool {


    private final IProductService productService;


    @Tool(description = "查询产品列表")
    public List<Product> getAllProductList() {
        log.info("============ 调用MCP工具 getAllProductList ============");
        List<Product> list = productService.list();
        log.info("产品列表：{}", JSONObject.toJSONString(list));
        return list;
    }
}
