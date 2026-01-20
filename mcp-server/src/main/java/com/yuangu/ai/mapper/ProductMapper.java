package com.yuangu.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuangu.ai.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * ProductMapper
 *
 * @author ckliu
 * @since 2026-01-20 21:46:24
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
