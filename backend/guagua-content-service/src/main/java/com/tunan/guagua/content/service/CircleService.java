package com.tunan.guagua.content.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.exception.BusinessException;
import com.tunan.guagua.common.model.LoginUser;
import com.tunan.guagua.content.dto.CircleRequest;
import com.tunan.guagua.content.entity.Circle;
import com.tunan.guagua.content.mapper.CircleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CircleService {
    private final CircleMapper circleMapper;

    public PageResult<Circle> publicList(long page, long size, String keyword) {
        LambdaQueryWrapper<Circle> wrapper = new LambdaQueryWrapper<Circle>()
                .eq(Circle::getAuditStatus, 1)
                .like(StrUtil.isNotBlank(keyword), Circle::getName, keyword)
                .orderByDesc(Circle::getMemberCount);
        Page<Circle> result = circleMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    public Circle detail(Long id) {
        Circle circle = circleMapper.selectById(id);
        if (circle == null) {
            throw new BusinessException(404, "圈子不存在");
        }
        return circle;
    }

    public List<Circle> mine(Long userId) {
        return circleMapper.selectList(new LambdaQueryWrapper<Circle>()
                .eq(Circle::getCreatedBy, userId)
                .orderByDesc(Circle::getCreatedAt));
    }

    @Transactional
    public Circle create(LoginUser user, CircleRequest request) {
        Circle circle = new Circle();
        circle.setName(request.name().trim());
        circle.setCover(StrUtil.blankToDefault(request.cover(), ""));
        circle.setDescription(StrUtil.blankToDefault(request.description(), ""));
        circle.setAuditStatus(0);
        circle.setMemberCount(0);
        circle.setPostCount(0);
        circle.setCreatedBy(user.id());
        circleMapper.insert(circle);
        return circle;
    }

    @Transactional
    public Circle update(Long id, LoginUser user, CircleRequest request) {
        Circle circle = require(id);
        requireOwnerOrAdmin(circle, user);
        circle.setName(request.name().trim());
        circle.setCover(StrUtil.blankToDefault(request.cover(), ""));
        circle.setDescription(StrUtil.blankToDefault(request.description(), ""));
        if (!user.isAdmin()) {
            circle.setAuditStatus(0);
        }
        circleMapper.updateById(circle);
        return circle;
    }

    @Transactional
    public void delete(Long id, LoginUser user) {
        Circle circle = require(id);
        requireOwnerOrAdmin(circle, user);
        circleMapper.deleteById(id);
    }

    public PageResult<Circle> adminList(long page, long size, String keyword, Integer auditStatus) {
        LambdaQueryWrapper<Circle> wrapper = new LambdaQueryWrapper<Circle>()
                .like(StrUtil.isNotBlank(keyword), Circle::getName, keyword)
                .eq(auditStatus != null, Circle::getAuditStatus, auditStatus)
                .orderByAsc(Circle::getAuditStatus)
                .orderByDesc(Circle::getCreatedAt);
        Page<Circle> result = circleMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Transactional
    public void audit(Long id, Integer auditStatus) {
        Circle circle = require(id);
        circle.setAuditStatus(auditStatus);
        circleMapper.updateById(circle);
    }

    private Circle require(Long id) {
        Circle circle = circleMapper.selectById(id);
        if (circle == null) {
            throw new BusinessException(404, "圈子不存在");
        }
        return circle;
    }

    private void requireOwnerOrAdmin(Circle circle, LoginUser user) {
        if (!user.isAdmin() && !circle.getCreatedBy().equals(user.id())) {
            throw new BusinessException(403, "只能管理自己创建的圈子");
        }
    }
}
