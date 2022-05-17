package com.yff.mall.cart.vo;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.mall.cart.vo
 * @Description
 * @date 2022/2/3 10:05
 */
@Data
public class UserInfoVo {
    private Long userId;
    private String userKey;

    private Boolean tempUser = false;
}
