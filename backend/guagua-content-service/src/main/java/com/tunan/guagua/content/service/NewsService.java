package com.tunan.guagua.content.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.exception.BusinessException;
import com.tunan.guagua.content.dto.NewsRequest;
import com.tunan.guagua.content.entity.News;
import com.tunan.guagua.content.mapper.NewsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsMapper newsMapper;

    public PageResult<News> publicList(long page, long size, String keyword) {
        Page<News> result = newsMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<News>()
                        .eq(News::getStatus, 1)
                        .like(StrUtil.isNotBlank(keyword), News::getTitle, keyword)
                        .orderByDesc(News::getCreatedAt));
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Transactional
    public News detail(Long id) {
        News news = require(id);
        news.setViewCount(news.getViewCount() + 1);
        newsMapper.updateById(news);
        return news;
    }

    public PageResult<News> adminList(long page, long size, String keyword) {
        Page<News> result = newsMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<News>()
                        .like(StrUtil.isNotBlank(keyword), News::getTitle, keyword)
                        .orderByDesc(News::getCreatedAt));
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Transactional
    public News save(Long id, NewsRequest request) {
        News news = id == null ? new News() : require(id);
        news.setTitle(request.title().trim());
        news.setSummary(StrUtil.blankToDefault(request.summary(), ""));
        news.setContent(request.content());
        news.setCover(StrUtil.blankToDefault(request.cover(), ""));
        news.setCity(StrUtil.blankToDefault(request.city(), "兔南"));
        news.setStatus(request.status());
        if (id == null) {
            news.setViewCount(0);
            newsMapper.insert(news);
        } else {
            newsMapper.updateById(news);
        }
        return news;
    }

    @Transactional
    public void delete(Long id) {
        require(id);
        newsMapper.deleteById(id);
    }

    private News require(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new BusinessException(404, "资讯不存在");
        }
        return news;
    }
}
