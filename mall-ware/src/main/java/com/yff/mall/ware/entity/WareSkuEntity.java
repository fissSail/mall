package com.yff.mall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品库存
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:26:26
 */
@Data
@TableName("wms_ware_sku")
public class WareSkuEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * sku_id
	 */
	private Long skuId;
	/**
	 * 仓库id
	 */
	private Long wareId;
	/**
	 * 库存数
	 */
	private Integer stock;
	/**
	 * 商品名称
	 */
	private String skuName;
	/**
	 * 锁定库存
	 */
	private Integer stockLocked;
	/**
	 * 仓库名称
	 */
	private String wareName;


}
