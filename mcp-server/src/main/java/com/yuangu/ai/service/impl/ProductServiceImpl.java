package com.yuangu.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuangu.ai.entity.Product;
import com.yuangu.ai.mapper.ProductMapper;
import com.yuangu.ai.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ProductServiceImpl
 *
 * @author ckliu
 * @since 2026-01-20 21:50:23
 */
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {
}
