package com.tunan.guagua.user.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.exception.BusinessException;
import com.tunan.guagua.user.dto.UpdateProfileRequest;
import com.tunan.guagua.user.dto.UserStatsVO;
import com.tunan.guagua.user.dto.UserVO;
import com.tunan.guagua.user.entity.Follow;
import com.tunan.guagua.user.entity.User;
import com.tunan.guagua.user.mapper.FollowMapper;
import com.tunan.guagua.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final FollowMapper followMapper;

    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return UserVO.from(user);
    }

    public List<UserVO> batch(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return userMapper.selectBatchIds(ids).stream().map(UserVO::from).toList();
    }

    @Transactional
    public UserVO updateProfile(Long userId, UpdateProfileRequest request) {
        User user = requireUser(userId);
        if (StrUtil.isNotBlank(request.nickname())) {
            user.setNickname(request.nickname().trim());
        }
        if (request.avatar() != null) {
            user.setAvatar(request.avatar().trim());
        }
        if (request.bio() != null) {
            user.setBio(request.bio().trim());
        }
        if (StrUtil.isNotBlank(request.city())) {
            user.setCity(request.city().trim());
        }
        userMapper.updateById(user);
        return UserVO.from(user);
    }

    public PageResult<UserVO> adminList(long page, long size, String keyword, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .and(StrUtil.isNotBlank(keyword), query -> query
                        .like(User::getUsername, keyword)
                        .or()
                        .like(User::getNickname, keyword))
                .eq(status != null, User::getStatus, status)
                .orderByDesc(User::getCreatedAt);
        Page<User> result = userMapper.selectPage(new Page<>(page, size), wrapper);
        List<UserVO> records = result.getRecords().stream().map(UserVO::from).toList();
        return PageResult.of(result.getTotal(), records);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        User user = requireUser(id);
        if ("ADMIN".equals(user.getRole()) && status == 0) {
            throw new BusinessException(400, "不能停用管理员账号");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    public UserStatsVO adminStats() {
        long total = userMapper.selectCount(null);
        long active = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getStatus, 1));
        LocalDateTime today = LocalDate.now().atStartOfDay();
        long todayNew = userMapper.selectCount(new LambdaQueryWrapper<User>().ge(User::getCreatedAt, today));
        long follows = followMapper.selectCount(null);
        return new UserStatsVO(total, active, todayNew, follows);
    }

    public User requireUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }
}
