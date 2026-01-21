package com.yuangu.ai.entity.dto.request;

import com.yuangu.ai.enums.ListSortEnum;
import com.yuangu.ai.enums.PriceCompareEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProductDTO {

    @ToolParam(description = "商品的编号", required = false)
    private String productId;

    @ToolParam(description = "商品的名称", required = false)
    private String productName;

    @ToolParam(description = "商品的品牌", required = false)
    private String brand;

    @ToolParam(description = "具体商品价格大小", required = false)
    private Integer price;

    @ToolParam(description = "商品的状态（上架状态的值为1/下架状态的值为0/预售状态的值为2）", required = false)
    private Integer status;

    @ToolParam(description = "查询列表的排序", required = false)
    private ListSortEnum sortEnum;

    @ToolParam(description = "比较价格的大小", required = false)
    private PriceCompareEnum priceCompareEnum;

}
