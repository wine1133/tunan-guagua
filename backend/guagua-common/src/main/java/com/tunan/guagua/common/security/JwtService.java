package com.tunan.guagua.common.security;

import cn.hutool.core.convert.Convert;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.tunan.guagua.common.model.LoginUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {
    private final byte[] key;
    private final long expireMillis;

    public JwtService(@Value("${guagua.jwt.secret:tn-guagua-2026-please-change-this-long-secret-key}") String secret,
                      @Value("${guagua.jwt.expire-hours:168}") long expireHours) {
        this.key = secret.getBytes(StandardCharsets.UTF_8);
        this.expireMillis = expireHours * 60 * 60 * 1000;
    }

    public String createToken(LoginUser user) {
        Date now = new Date();
        return JWT.create()
                .setPayload("userId", user.id())
                .setPayload("username", user.username())
                .setPayload("nickname", user.nickname())
                .setPayload("role", user.role())
                .setIssuedAt(now)
                .setExpiresAt(new Date(now.getTime() + expireMillis))
                .setKey(key)
                .sign();
    }

    public LoginUser parse(String token) {
        try {
            if (token == null || token.isBlank() || !JWTUtil.verify(token, key)) {
                return null;
            }
            JWT jwt = JWTUtil.parseToken(token);
            Date expiresAt = jwt.getPayloads().getDate("exp");
            if (expiresAt != null && expiresAt.before(new Date())) {
                return null;
            }
            Long userId = Convert.toLong(jwt.getPayload("userId"));
            if (userId == null) {
                return null;
            }
            return new LoginUser(
                    userId,
                    Convert.toStr(jwt.getPayload("username")),
                    Convert.toStr(jwt.getPayload("nickname")),
                    Convert.toStr(jwt.getPayload("role"), "USER")
            );
        } catch (Exception ignored) {
            return null;
        }
    }
}

