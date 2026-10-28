package com.tunan.guagua.user.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tunan.guagua.common.exception.BusinessException;
import com.tunan.guagua.common.model.LoginUser;
import com.tunan.guagua.common.security.JwtService;
import com.tunan.guagua.user.dto.LoginRequest;
import com.tunan.guagua.user.dto.LoginResponse;
import com.tunan.guagua.user.dto.RegisterRequest;
import com.tunan.guagua.user.dto.UserVO;
import com.tunan.guagua.user.entity.User;
import com.tunan.guagua.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.username()));
        if (user == null || !BCrypt.checkpw(request.password(), user.getPasswordHash())) {
            throw new BusinessException(400, "账号或密码不正确");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被停用");
        }
        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getNickname(), user.getRole());
        return new LoginResponse(jwtService.createToken(loginUser), UserVO.from(user));
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.username()));
        if (count > 0) {
            throw new BusinessException(409, "该账号已被注册");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setNickname(StrUtil.blankToDefault(request.nickname(), request.username()));
        user.setPasswordHash(BCrypt.hashpw(request.password(), BCrypt.gensalt()));
        user.setAvatar("");
        user.setBio("这个用户还没有写下个人简介。");
        user.setCity("兔南");
        user.setRole("USER");
        user.setStatus(1);
        userMapper.insert(user);
        LoginUser loginUser = new LoginUser(user.getId(), user.getUsername(), user.getNickname(), user.getRole());
        return new LoginResponse(jwtService.createToken(loginUser), UserVO.from(user));
    }
}
