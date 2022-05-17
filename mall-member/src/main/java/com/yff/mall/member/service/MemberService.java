package com.yff.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yff.common.to.feign.GitUserTo;
import com.yff.common.to.feign.MemberTo;
import com.yff.common.utils.PageUtils;
import com.yff.mall.member.entity.MemberEntity;
import com.yff.mall.member.exception.MobileExistException;
import com.yff.mall.member.exception.UserNameExistException;

import java.util.Map;

/**
 * 会员
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:18:23
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberTo to);

    void checkUserName(String username) throws UserNameExistException;

    void checkMobile(String mobile) throws MobileExistException;

    MemberEntity login(MemberTo to);

    MemberEntity oauthGiteeLogin(GitUserTo to);

    MemberEntity oauthGithubLogin(GitUserTo to);
}

