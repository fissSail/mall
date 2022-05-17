package com.yff.mall.auth.feign;

import com.yff.common.to.feign.GitUserTo;
import com.yff.common.utils.R;
import com.yff.mall.auth.vo.UserLoginVo;
import com.yff.mall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author yanfeifan
 * @Package com.yff.mall.auth.feign
 * @Description
 * @date 2022/1/29 19:36
 */
@FeignClient("mall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo userRegisterVo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth/gitee/login")
    R oauthGiteeLogin(@RequestBody GitUserTo to);

    @PostMapping("/member/member/oauth/github/login")
    R oauthGithubLogin(@RequestBody GitUserTo to);
}
