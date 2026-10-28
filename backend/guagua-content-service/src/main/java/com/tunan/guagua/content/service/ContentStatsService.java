package com.tunan.guagua.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tunan.guagua.content.dto.ContentStatsVO;
import com.tunan.guagua.content.entity.Circle;
import com.tunan.guagua.content.entity.News;
import com.tunan.guagua.content.entity.Topic;
import com.tunan.guagua.content.mapper.CircleMapper;
import com.tunan.guagua.content.mapper.NewsMapper;
import com.tunan.guagua.content.mapper.TopicMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentStatsService {
    private final CircleMapper circleMapper;
    private final TopicMapper topicMapper;
    private final NewsMapper newsMapper;

    public ContentStatsVO stats() {
        return new ContentStatsVO(
                circleMapper.selectCount(null),
                topicMapper.selectCount(null),
                circleMapper.selectCount(new LambdaQueryWrapper<Circle>().eq(Circle::getAuditStatus, 0)),
                topicMapper.selectCount(new LambdaQueryWrapper<Topic>().eq(Topic::getAuditStatus, 0)),
                newsMapper.selectCount(new LambdaQueryWrapper<News>().eq(News::getStatus, 1))
        );
    }
}
