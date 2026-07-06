package com.obe.evaluation.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.obe.evaluation.system.entity.SysUser;
import com.obe.evaluation.system.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {
    public SysUser findByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username).eq(SysUser::getStatus, 1));
    }
    public boolean usernameExists(String username) {
        return count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)) > 0;
    }
}
