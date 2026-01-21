package com.yuangu.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Product
 *
 * @author ckliu
 * @since 2026-01-20 21:46:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "product")
public class Product {

    private Long id;

    private String productName;

    private String brand;
    private Double price;
    private Integer stock;
    private String description;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
