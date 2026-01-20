package com.yuangu.ai.tool;

import com.yuangu.ai.entity.Product;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductToolTest
 *
 * @author ckliu
 * @since 2026-01-20 21:58:33
 */
@SpringBootTest
class ProductToolTest {

    @Resource
    private ProductTool productTool;

    @Test
    void getAllProductList() {

        List<Product> allProductList = productTool.getAllProductList();
        allProductList.forEach(System.out::println);
    }
}