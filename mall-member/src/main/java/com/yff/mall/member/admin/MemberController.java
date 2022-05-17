package com.yff.mall.member.admin;

import com.yff.common.to.feign.GitUserTo;
import com.yff.common.to.feign.MemberTo;
import com.yff.common.utils.PageUtils;
import com.yff.common.utils.R;
import com.yff.mall.member.entity.MemberEntity;
import com.yff.mall.member.exception.MobileExistException;
import com.yff.mall.member.exception.UserNameExistException;
import com.yff.mall.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 会员
 *
 * @author yff
 * @email 1335799488@qq.com
 * @date 2021-12-08 21:18:23
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/register")
    public R register(@RequestBody MemberTo to){
        try {
            memberService.register(to);
        } catch (MobileExistException e) {
            e.printStackTrace();
            return R.error("手机号已存在");
        } catch (UserNameExistException e) {
            e.printStackTrace();
            return R.error("用户名已存在");
        }

        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberTo to){
        MemberEntity member = memberService.login(to);
        if(!ObjectUtils.isEmpty(member)){
            return R.ok().setData(member);
        }else{
            return R.error("登入失败");
        }
    }

    @PostMapping("/oauth/gitee/login")
    public R oauthGiteeLogin(@RequestBody GitUserTo to){
        MemberEntity member = memberService.oauthGiteeLogin(to);
        return R.ok().setData(member);
    }

    @PostMapping("/oauth/github/login")
    public R oauthGithubLogin(@RequestBody GitUserTo to){
        MemberEntity member = memberService.oauthGithubLogin(to);
        return R.ok().setData(member);
    }
}
