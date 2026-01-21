package com.yuangu.ai.tool;

import com.alibaba.fastjson2.JSONObject;
import com.yuangu.ai.entity.Product;
import com.yuangu.ai.enums.ListSortEnum;
import com.yuangu.ai.enums.PriceCompareEnum;
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

    @Tool(description = "把排序（正序/倒叙）转换为对应的枚举")
    public ListSortEnum getSortEnum(String sort) {

        log.info("============ 调用MCP工具 getSortEnum ============");
        log.info("******参数：{}", sort);

        if (ListSortEnum.ASC.value.equalsIgnoreCase(sort)) {
            return ListSortEnum.ASC;
        } else {
            return ListSortEnum.DESC;
        }
    }


    @Tool(description = "把商品价格的比较（大于/小于/大于等于/小于等于/高于/低于/不高于/不低于/等于）转换为对应的枚举")
    public PriceCompareEnum getPriceCompareEnum(String priceCompare) {

        log.info("========== 调用MCP工具：getPriceCompareEnum() ==========");
        log.info("| 参数 priceCompare 为： {}", priceCompare);
        log.info("========== End ==========");

        if (priceCompare.equalsIgnoreCase(PriceCompareEnum.GREATER_THAN.getValue())) {
            return PriceCompareEnum.GREATER_THAN;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.LESS_THAN.getValue())) {
            return PriceCompareEnum.LESS_THAN;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.GREATER_THAN_OR_EQUAL_TO.getValue())) {
            return PriceCompareEnum.GREATER_THAN_OR_EQUAL_TO;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.LESS_THAN_OR_EQUAL_TO.getValue())) {
            return PriceCompareEnum.LESS_THAN_OR_EQUAL_TO;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.HIGHER_THAN.getValue())) {
            return PriceCompareEnum.HIGHER_THAN;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.LOWER_THAN.getValue())) {
            return PriceCompareEnum.LOWER_THAN;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.NOT_HIGHER_THAN.getValue())) {
            return PriceCompareEnum.NOT_HIGHER_THAN;
        } else if (priceCompare.equalsIgnoreCase(PriceCompareEnum.NOT_LOWER_THAN.getValue())) {
            return PriceCompareEnum.NOT_LOWER_THAN;
        } else {
            return PriceCompareEnum.EQUAL_TO;
        }

    }



}
