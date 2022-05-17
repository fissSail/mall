package com.yff.common.to.feign;

import lombok.Data;

/**
 * @author yanfeifan
 * @Package com.yff.common.to.feign
 * @Description
 * @date 2022/1/31 15:53
 */
@Data
public class GitUserTo {

    private Long id;
    private String login;
    private String name;
    private String avatarUrl;
    private String email;
}
