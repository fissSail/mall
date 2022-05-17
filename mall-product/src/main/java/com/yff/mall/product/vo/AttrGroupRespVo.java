package com.yff.mall.product.vo;

import com.yff.mall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.vo
 * @Description
 * @date 2021/12/21 16:13
 */
@Data
public class AttrGroupRespVo {

   /**
    * 分组id
    */
   private Long attrGroupId;
   /**
    * 组名
    */
   private String attrGroupName;
   /**
    * 排序
    */
   private Integer sort;
   /**
    * 描述
    */
   private String descript;
   /**
    * 组图标
    */
   private String icon;
   /**
    * 所属分类id
    */
   private Long catelogId;

   /**
    * 属性list
    */
   private List<AttrEntity> attrs;
}
