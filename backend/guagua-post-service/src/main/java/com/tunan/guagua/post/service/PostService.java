package com.tunan.guagua.post.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.exception.BusinessException;
import com.tunan.guagua.common.model.LoginUser;
import com.tunan.guagua.post.client.ContentBrief;
import com.tunan.guagua.post.client.UserBrief;
import com.tunan.guagua.post.dto.PostRequest;
import com.tunan.guagua.post.dto.PostStatsVO;
import com.tunan.guagua.post.dto.PostVO;
import com.tunan.guagua.post.entity.Post;
import com.tunan.guagua.post.entity.PostFavorite;
import com.tunan.guagua.post.entity.PostLike;
import com.tunan.guagua.post.mapper.CommentMapper;
import com.tunan.guagua.post.mapper.PostFavoriteMapper;
import com.tunan.guagua.post.mapper.PostLikeMapper;
import com.tunan.guagua.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final PostLikeMapper postLikeMapper;
    private final PostFavoriteMapper postFavoriteMapper;
    private final ReferenceDataService referenceDataService;
    private final RedissonClient redissonClient;

    @Value("${guagua.lock.post-update-wait-seconds:3}")
    private long lockWaitSeconds;

    @Value("${guagua.lock.post-update-lease-seconds:10}")
    private long lockLeaseSeconds;

    public PageResult<PostVO> publicList(long page, long size, String keyword, Long circleId, Long topicId, Long currentUserId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<Post>()
                .eq(Post::getAuditStatus, 1)
                .and(StrUtil.isNotBlank(keyword), query -> query
                        .like(Post::getTitle, keyword)
                        .or()
                        .like(Post::getContent, keyword))
                .eq(circleId != null, Post::getCircleId, circleId)
                .eq(topicId != null, Post::getTopicId, topicId)
                .orderByDesc(Post::getCreatedAt);
        Page<Post> result = postMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getTotal(), mapToVO(result.getRecords(), currentUserId));
    }

    public PageResult<PostVO> mine(Long userId, long page, long size) {
        Page<Post> result = postMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getAuthorId, userId)
                        .orderByDesc(Post::getCreatedAt));
        return PageResult.of(result.getTotal(), mapToVO(result.getRecords(), userId));
    }

    @Transactional
    public PostVO detail(Long id, Long currentUserId, boolean adminView) {
        Post post = require(id);
        if (!adminView && (post.getAuditStatus() == null || post.getAuditStatus() != 1)
                && !Objects.equals(post.getAuthorId(), currentUserId)) {
            throw new BusinessException(404, "帖子不存在或仍在审核中");
        }
        postMapper.update(null, new LambdaUpdateWrapper<Post>()
                .eq(Post::getId, id)
                .setSql("view_count = view_count + 1"));
        post.setViewCount(post.getViewCount() + 1);
        return mapToVO(List.of(post), currentUserId).get(0);
    }

    @Transactional
    public PostVO create(LoginUser user, PostRequest request) {
        Post post = new Post();
        applyRequest(post, request);
        post.setAuthorId(user.id());
        post.setAuditStatus(0);
        post.setLikeCount(0);
        post.setFavoriteCount(0);
        post.setCommentCount(0);
        post.setViewCount(0);
        postMapper.insert(post);
        return mapToVO(List.of(post), user.id()).get(0);
    }

    @Transactional
    public PostVO update(Long id, LoginUser user, PostRequest request) {
        RLock lock = redissonClient.getLock("guagua:lock:post:update:" + id);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(lockWaitSeconds, lockLeaseSeconds, TimeUnit.SECONDS);
            if (!acquired) {
                throw new BusinessException(409, "帖子正在被其他操作更新，请稍后重试");
            }
            Post post = require(id);
            requireOwnerOrAdmin(post, user);
            applyRequest(post, request);
            if (!user.isAdmin()) {
                post.setAuditStatus(0);
            }
            postMapper.updateById(post);
            return mapToVO(List.of(post), user.id()).get(0);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new BusinessException(503, "获取帖子更新锁失败");
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public void delete(Long id, LoginUser user) {
        Post post = require(id);
        requireOwnerOrAdmin(post, user);
        postMapper.deleteById(id);
        postLikeMapper.delete(new LambdaQueryWrapper<PostLike>().eq(PostLike::getPostId, id));
        postFavoriteMapper.delete(new LambdaQueryWrapper<PostFavorite>().eq(PostFavorite::getPostId, id));
        commentMapper.delete(new LambdaQueryWrapper<com.tunan.guagua.post.entity.Comment>().eq(com.tunan.guagua.post.entity.Comment::getPostId, id));
    }

    public PageResult<PostVO> adminList(long page, long size, String keyword, Integer auditStatus, Long currentUserId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<Post>()
                .and(StrUtil.isNotBlank(keyword), query -> query
                        .like(Post::getTitle, keyword)
                        .or()
                        .like(Post::getContent, keyword))
                .eq(auditStatus != null, Post::getAuditStatus, auditStatus)
                .orderByAsc(Post::getAuditStatus)
                .orderByDesc(Post::getCreatedAt);
        Page<Post> result = postMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getTotal(), mapToVO(result.getRecords(), currentUserId));
    }

    @Transactional
    public void audit(Long id, Integer auditStatus) {
        Post post = require(id);
        post.setAuditStatus(auditStatus);
        postMapper.updateById(post);
    }

    public PostStatsVO stats() {
        long total = postMapper.selectCount(null);
        long published = postMapper.selectCount(new LambdaQueryWrapper<Post>().eq(Post::getAuditStatus, 1));
        long pending = postMapper.selectCount(new LambdaQueryWrapper<Post>().eq(Post::getAuditStatus, 0));
        long comments = commentMapper.selectCount(null);
        long likes = postLikeMapper.selectCount(null);
        long favorites = postFavoriteMapper.selectCount(null);
        long today = postMapper.selectCount(new LambdaQueryWrapper<Post>()
                .ge(Post::getCreatedAt, LocalDate.now().atStartOfDay()));
        return new PostStatsVO(total, published, pending, comments, likes, favorites, today);
    }

    public List<PostVO> mapToVO(List<Post> posts, Long currentUserId) {
        if (posts == null || posts.isEmpty()) {
            return List.of();
        }
        Set<Long> userIds = posts.stream().map(Post::getAuthorId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> circleIds = posts.stream().map(Post::getCircleId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> topicIds = posts.stream().map(Post::getTopicId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, UserBrief> users = referenceDataService.users(userIds);
        Map<Long, ContentBrief> circles = referenceDataService.circles(circleIds);
        Map<Long, ContentBrief> topics = referenceDataService.topics(topicIds);

        Set<Long> likedPostIds = new HashSet<>();
        Set<Long> favoritedPostIds = new HashSet<>();
        if (currentUserId != null) {
            List<Long> postIds = posts.stream().map(Post::getId).toList();
            likedPostIds.addAll(postLikeMapper.selectList(new LambdaQueryWrapper<PostLike>()
                            .eq(PostLike::getUserId, currentUserId)
                            .in(PostLike::getPostId, postIds))
                    .stream().map(PostLike::getPostId).toList());
            favoritedPostIds.addAll(postFavoriteMapper.selectList(new LambdaQueryWrapper<PostFavorite>()
                            .eq(PostFavorite::getUserId, currentUserId)
                            .in(PostFavorite::getPostId, postIds))
                    .stream().map(PostFavorite::getPostId).toList());
        }

        return posts.stream().map(post -> toVO(post, users, circles, topics, likedPostIds, favoritedPostIds)).toList();
    }

    public Post require(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }
        return post;
    }

    private PostVO toVO(Post post, Map<Long, UserBrief> users, Map<Long, ContentBrief> circles,
                        Map<Long, ContentBrief> topics, Set<Long> liked, Set<Long> favorited) {
        UserBrief author = users.get(post.getAuthorId());
        ContentBrief circle = post.getCircleId() == null ? null : circles.get(post.getCircleId());
        ContentBrief topic = post.getTopicId() == null ? null : topics.get(post.getTopicId());
        return PostVO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .images(parseImages(post.getImages()))
                .circleId(post.getCircleId())
                .circleName(circle == null ? "" : circle.name())
                .topicId(post.getTopicId())
                .topicName(topic == null ? "" : topic.name())
                .authorId(post.getAuthorId())
                .authorName(author == null ? "兔南用户" : author.nickname())
                .authorAvatar(author == null ? "" : author.avatar())
                .auditStatus(post.getAuditStatus())
                .likeCount(post.getLikeCount())
                .favoriteCount(post.getFavoriteCount())
                .commentCount(post.getCommentCount())
                .viewCount(post.getViewCount())
                .liked(liked.contains(post.getId()))
                .favorited(favorited.contains(post.getId()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private List<String> parseImages(String images) {
        if (StrUtil.isBlank(images)) {
            return List.of();
        }
        try {
            return JSONUtil.parseArray(images).toList(String.class);
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private void applyRequest(Post post, PostRequest request) {
        post.setTitle(request.title().trim());
        post.setContent(request.content());
        post.setImages(JSONUtil.toJsonStr(request.images() == null ? List.of() : request.images()));
        post.setCircleId(request.circleId());
        post.setTopicId(request.topicId());
    }

    private void requireOwnerOrAdmin(Post post, LoginUser user) {
        if (!user.isAdmin() && !Objects.equals(post.getAuthorId(), user.id())) {
            throw new BusinessException(403, "只能管理自己发布的帖子");
        }
    }

}


