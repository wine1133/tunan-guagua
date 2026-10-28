package com.tunan.guagua.content.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.exception.BusinessException;
import com.tunan.guagua.common.model.LoginUser;
import com.tunan.guagua.content.dto.TopicRequest;
import com.tunan.guagua.content.entity.Topic;
import com.tunan.guagua.content.mapper.TopicMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicMapper topicMapper;

    public PageResult<Topic> publicList(long page, long size, String keyword) {
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<Topic>()
                .eq(Topic::getAuditStatus, 1)
                .like(StrUtil.isNotBlank(keyword), Topic::getName, keyword)
                .orderByDesc(Topic::getHeat);
        Page<Topic> result = topicMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    public Topic detail(Long id) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null) {
            throw new BusinessException(404, "话题不存在");
        }
        return topic;
    }

    public List<Topic> mine(Long userId) {
        return topicMapper.selectList(new LambdaQueryWrapper<Topic>()
                .eq(Topic::getCreatedBy, userId)
                .orderByDesc(Topic::getCreatedAt));
    }

    @Transactional
    public Topic create(LoginUser user, TopicRequest request) {
        Topic topic = new Topic();
        topic.setName(request.name().trim());
        topic.setCover(StrUtil.blankToDefault(request.cover(), ""));
        topic.setDescription(StrUtil.blankToDefault(request.description(), ""));
        topic.setAuditStatus(0);
        topic.setHeat(0);
        topic.setPostCount(0);
        topic.setCreatedBy(user.id());
        topicMapper.insert(topic);
        return topic;
    }

    @Transactional
    public Topic update(Long id, LoginUser user, TopicRequest request) {
        Topic topic = require(id);
        requireOwnerOrAdmin(topic, user);
        topic.setName(request.name().trim());
        topic.setCover(StrUtil.blankToDefault(request.cover(), ""));
        topic.setDescription(StrUtil.blankToDefault(request.description(), ""));
        if (!user.isAdmin()) {
            topic.setAuditStatus(0);
        }
        topicMapper.updateById(topic);
        return topic;
    }

    @Transactional
    public void delete(Long id, LoginUser user) {
        Topic topic = require(id);
        requireOwnerOrAdmin(topic, user);
        topicMapper.deleteById(id);
    }

    public PageResult<Topic> adminList(long page, long size, String keyword, Integer auditStatus) {
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<Topic>()
                .like(StrUtil.isNotBlank(keyword), Topic::getName, keyword)
                .eq(auditStatus != null, Topic::getAuditStatus, auditStatus)
                .orderByAsc(Topic::getAuditStatus)
                .orderByDesc(Topic::getCreatedAt);
        Page<Topic> result = topicMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Transactional
    public void audit(Long id, Integer auditStatus) {
        Topic topic = require(id);
        topic.setAuditStatus(auditStatus);
        topicMapper.updateById(topic);
    }

    private Topic require(Long id) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null) {
            throw new BusinessException(404, "话题不存在");
        }
        return topic;
    }

    private void requireOwnerOrAdmin(Topic topic, LoginUser user) {
        if (!user.isAdmin() && !topic.getCreatedBy().equals(user.id())) {
            throw new BusinessException(403, "只能管理自己创建的话题");
        }
    }
}
