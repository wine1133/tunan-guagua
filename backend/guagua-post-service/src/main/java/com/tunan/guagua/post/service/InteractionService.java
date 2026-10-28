package com.tunan.guagua.post.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.exception.BusinessException;
import com.tunan.guagua.post.client.UserBrief;
import com.tunan.guagua.post.dto.InteractionVO;
import com.tunan.guagua.post.dto.PostVO;
import com.tunan.guagua.post.entity.Post;
import com.tunan.guagua.post.entity.PostFavorite;
import com.tunan.guagua.post.entity.PostLike;
import com.tunan.guagua.post.mapper.PostFavoriteMapper;
import com.tunan.guagua.post.mapper.PostLikeMapper;
import com.tunan.guagua.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InteractionService {
    private final PostMapper postMapper;
    private final PostLikeMapper postLikeMapper;
    private final PostFavoriteMapper postFavoriteMapper;
    private final PostService postService;
    private final ReferenceDataService referenceDataService;

    @Transactional
    public void like(Long postId, Long userId) {
        requirePost(postId);
        Long exists = postLikeMapper.selectCount(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getPostId, postId).eq(PostLike::getUserId, userId));
        if (exists > 0) {
            return;
        }
        PostLike like = new PostLike();
        like.setPostId(postId);
        like.setUserId(userId);
        postLikeMapper.insert(like);
        postMapper.update(null, new LambdaUpdateWrapper<Post>()
                .eq(Post::getId, postId).setSql("like_count = like_count + 1"));
    }

    @Transactional
    public void unlike(Long postId, Long userId) {
        int deleted = postLikeMapper.delete(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getPostId, postId).eq(PostLike::getUserId, userId));
        if (deleted > 0) {
            postMapper.update(null, new LambdaUpdateWrapper<Post>()
                    .eq(Post::getId, postId).setSql("like_count = GREATEST(like_count - 1, 0)"));
        }
    }

    @Transactional
    public void favorite(Long postId, Long userId) {
        requirePost(postId);
        Long exists = postFavoriteMapper.selectCount(new LambdaQueryWrapper<PostFavorite>()
                .eq(PostFavorite::getPostId, postId).eq(PostFavorite::getUserId, userId));
        if (exists > 0) {
            return;
        }
        PostFavorite favorite = new PostFavorite();
        favorite.setPostId(postId);
        favorite.setUserId(userId);
        postFavoriteMapper.insert(favorite);
        postMapper.update(null, new LambdaUpdateWrapper<Post>()
                .eq(Post::getId, postId).setSql("favorite_count = favorite_count + 1"));
    }

    @Transactional
    public void unfavorite(Long postId, Long userId) {
        int deleted = postFavoriteMapper.delete(new LambdaQueryWrapper<PostFavorite>()
                .eq(PostFavorite::getPostId, postId).eq(PostFavorite::getUserId, userId));
        if (deleted > 0) {
            postMapper.update(null, new LambdaUpdateWrapper<Post>()
                    .eq(Post::getId, postId).setSql("favorite_count = GREATEST(favorite_count - 1, 0)"));
        }
    }

    public PageResult<PostVO> myLikes(Long userId, long page, long size) {
        Page<PostLike> result = postLikeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<PostLike>().eq(PostLike::getUserId, userId)
                        .orderByDesc(PostLike::getCreatedAt));
        return buildPostPage(result, userId);
    }

    public PageResult<PostVO> myFavorites(Long userId, long page, long size) {
        Page<PostFavorite> result = postFavoriteMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<PostFavorite>().eq(PostFavorite::getUserId, userId)
                        .orderByDesc(PostFavorite::getCreatedAt));
        List<Post> posts = result.getRecords().isEmpty() ? List.of()
                : postMapper.selectBatchIds(result.getRecords().stream().map(PostFavorite::getPostId).toList());
        Map<Long, Post> postMap = posts.stream().collect(Collectors.toMap(Post::getId, Function.identity()));
        List<Post> ordered = result.getRecords().stream().map(item -> postMap.get(item.getPostId()))
                .filter(java.util.Objects::nonNull).toList();
        return PageResult.of(result.getTotal(), postService.mapToVO(ordered, userId));
    }

    public PageResult<InteractionVO> adminLikes(long page, long size) {
        Page<PostLike> result = postLikeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<PostLike>().orderByDesc(PostLike::getCreatedAt));
        List<InteractionVO> records = toInteractions(result.getRecords().stream()
                .map(item -> new InteractionRecord(item.getId(), item.getPostId(), item.getUserId(), item.getCreatedAt()))
                .toList());
        return PageResult.of(result.getTotal(), records);
    }

    public PageResult<InteractionVO> adminFavorites(long page, long size) {
        Page<PostFavorite> result = postFavoriteMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<PostFavorite>().orderByDesc(PostFavorite::getCreatedAt));
        List<InteractionVO> records = toInteractions(result.getRecords().stream()
                .map(item -> new InteractionRecord(item.getId(), item.getPostId(), item.getUserId(), item.getCreatedAt()))
                .toList());
        return PageResult.of(result.getTotal(), records);
    }

    private PageResult<PostVO> buildPostPage(Page<PostLike> result, Long userId) {
        List<Post> posts = result.getRecords().isEmpty() ? List.of()
                : postMapper.selectBatchIds(result.getRecords().stream().map(PostLike::getPostId).toList());
        Map<Long, Post> postMap = posts.stream().collect(Collectors.toMap(Post::getId, Function.identity()));
        List<Post> ordered = result.getRecords().stream().map(item -> postMap.get(item.getPostId()))
                .filter(java.util.Objects::nonNull).toList();
        return PageResult.of(result.getTotal(), postService.mapToVO(ordered, userId));
    }

    private List<InteractionVO> toInteractions(List<InteractionRecord> records) {
        if (records.isEmpty()) {
            return List.of();
        }
        Set<Long> postIds = records.stream().map(InteractionRecord::postId).collect(Collectors.toSet());
        Set<Long> userIds = records.stream().map(InteractionRecord::userId).collect(Collectors.toSet());
        Map<Long, String> titles = postMapper.selectBatchIds(postIds).stream()
                .collect(Collectors.toMap(Post::getId, Post::getTitle));
        Map<Long, UserBrief> users = referenceDataService.users(userIds);
        List<InteractionVO> result = new ArrayList<>();
        for (InteractionRecord record : records) {
            UserBrief user = users.get(record.userId());
            result.add(new InteractionVO(record.id(), record.postId(),
                    titles.getOrDefault(record.postId(), "帖子"),
                    record.userId(), user == null ? "兔南用户" : user.nickname(), record.createdAt()));
        }
        return result;
    }

    private Post requirePost(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }
        return post;
    }

    private record InteractionRecord(Long id, Long postId, Long userId, java.time.LocalDateTime createdAt) {
    }
}
