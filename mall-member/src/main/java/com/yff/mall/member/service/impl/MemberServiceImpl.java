package com.yff.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yff.common.to.feign.GitUserTo;
import com.yff.common.to.feign.MemberTo;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.Query;
import com.yff.mall.member.dao.MemberDao;
import com.yff.mall.member.entity.MemberEntity;
import com.yff.mall.member.entity.MemberLevelEntity;
import com.yff.mall.member.exception.MobileExistException;
import com.yff.mall.member.exception.UserNameExistException;
import com.yff.mall.member.service.MemberLevelService;
import com.yff.mall.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberTo to) {

        this.checkUserName(to.getUserName());
        this.checkMobile(to.getPhone());
        //密码编译器编译密码，同样的密码编译后都不同，但可已拿加密后的数据和原文进行校验，返回为true
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(to.getPassword());

        MemberEntity memberEntity = new MemberEntity();
        MemberLevelEntity memberLevelEntity = memberLevelService.
                getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));

        memberEntity.setUsername(to.getUserName());
        memberEntity.setNickname(to.getUserName());
        memberEntity.setMobile(to.getPhone());
        memberEntity.setPassword(password);
        memberEntity.setLevelId(memberLevelEntity.getId());
        memberEntity.setStatus(1);
        memberEntity.setCreateTime(new Date());
        this.save(memberEntity);
    }

    @Override
    public void checkUserName(String username) throws UserNameExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0) {
            throw new UserNameExistException();
        }
    }

    @Override
    public void checkMobile(String mobile) throws MobileExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", mobile));
        if (count > 0) {
            throw new MobileExistException();
        }
    }

    @Override
    public MemberEntity login(MemberTo to) {
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", to.getUserName())
                .or().eq("mobile", to.getPhone()));
        return Optional.ofNullable(memberEntity).map(data->{
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean matches = encoder.matches(to.getPassword(), data.getPassword());
            if(matches){
                return data;
            }
            return null;
        }).get();
    }

    @Override
    public MemberEntity oauthGiteeLogin(GitUserTo to) {
        MemberEntity giteeMember = this.getOne(new QueryWrapper<MemberEntity>().eq("gitee_uid", to.getId()));
        if(giteeMember != null){
            //登录
            return giteeMember;
        }else{
            //注册
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setUsername(to.getName());
            memberEntity.setNickname(to.getLogin());
            memberEntity.setGiteeUid(to.getId());
            this.save(memberEntity);
            return memberEntity;
        }
    }


    @Override
    public MemberEntity oauthGithubLogin(GitUserTo to) {
        MemberEntity githubMember = this.getOne(new QueryWrapper<MemberEntity>().eq("github_uid", to.getId()));
        if(githubMember != null){
            //登录
            return githubMember;
        }else{
            //注册
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setUsername(to.getName());
            memberEntity.setNickname(to.getLogin());
            memberEntity.setGithubUid(to.getId());
            this.save(memberEntity);
            return memberEntity;
        }
    }

}
