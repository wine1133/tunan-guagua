package com.tunan.guagua.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tunan.guagua.common.exception.BusinessException;
import com.tunan.guagua.user.dto.FollowUserVO;
import com.tunan.guagua.user.entity.Follow;
import com.tunan.guagua.user.entity.User;
import com.tunan.guagua.user.mapper.FollowMapper;
import com.tunan.guagua.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowMapper followMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BusinessException(400, "不能关注自己");
        }
        userService.requireUser(followingId);
        Long count = followMapper.selectCount(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFollowingId, followingId));
        if (count > 0) {
            return;
        }
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        followMapper.insert(follow);
    }

    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        followMapper.delete(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFollowingId, followingId));
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        if (followerId == null) {
            return false;
        }
        return followMapper.selectCount(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFollowingId, followingId)) > 0;
    }

    public List<FollowUserVO> following(Long userId) {
        List<Follow> follows = followMapper.selectList(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, userId)
                .orderByDesc(Follow::getCreatedAt));
        return toViews(follows, true);
    }

    public List<FollowUserVO> followers(Long userId) {
        List<Follow> follows = followMapper.selectList(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowingId, userId)
                .orderByDesc(Follow::getCreatedAt));
        return toViews(follows, false);
    }

    private List<FollowUserVO> toViews(List<Follow> follows, boolean followingDirection) {
        if (follows.isEmpty()) {
            return List.of();
        }
        List<Long> ids = follows.stream()
                .map(follow -> followingDirection ? follow.getFollowingId() : follow.getFollowerId())
                .distinct()
                .toList();
        Map<Long, User> users = userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        List<FollowUserVO> result = new ArrayList<>();
        for (Follow follow : follows) {
            Long id = followingDirection ? follow.getFollowingId() : follow.getFollowerId();
            User user = users.get(id);
            if (user != null && user.getStatus() == 1) {
                result.add(FollowUserVO.from(user, follow.getCreatedAt()));
            }
        }
        return result;
    }
}
