package com.tunan.guagua.common.security;

import com.tunan.guagua.common.exception.BusinessException;
import com.tunan.guagua.common.model.LoginUser;

public final class UserContext {
    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        LoginUser user = HOLDER.get();
        return user == null ? null : user.id();
    }

    public static Long requireUserId() {
        LoginUser user = HOLDER.get();
        if (user == null) {
            throw new BusinessException(401, "请先登录");
        }
        return user.id();
    }

    public static LoginUser requireAdmin() {
        LoginUser user = HOLDER.get();
        if (user == null) {
            throw new BusinessException(401, "请先登录");
        }
        if (!user.isAdmin()) {
            throw new BusinessException(403, "无管理权限");
        }
        return user;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
