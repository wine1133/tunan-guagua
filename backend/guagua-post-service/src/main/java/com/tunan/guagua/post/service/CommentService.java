package com.tunan.guagua.post.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.exception.BusinessException;
import com.tunan.guagua.common.model.LoginUser;
import com.tunan.guagua.post.client.UserBrief;
import com.tunan.guagua.post.dto.CommentRequest;
import com.tunan.guagua.post.dto.CommentVO;
import com.tunan.guagua.post.entity.Comment;
import com.tunan.guagua.post.entity.Post;
import com.tunan.guagua.post.mapper.CommentMapper;
import com.tunan.guagua.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final ReferenceDataService referenceDataService;

    public List<CommentVO> listByPost(Long postId) {
        Post post = requirePost(postId);
        if (post.getAuditStatus() != 1) {
            throw new BusinessException(404, "帖子不存在或仍在审核中");
        }
        List<Comment> comments = commentMapper.selectList(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getPostId, postId)
                .eq(Comment::getStatus, 1)
                .orderByAsc(Comment::getCreatedAt));
        return enrich(comments, Map.of(postId, post.getTitle()));
    }

    @Transactional
    public CommentVO create(LoginUser user, CommentRequest request) {
        Post post = requirePost(request.postId());
        if (post.getAuditStatus() != 1) {
            throw new BusinessException(400, "帖子审核通过后才能评论");
        }
        if (request.parentId() != null) {
            Comment parent = commentMapper.selectById(request.parentId());
            if (parent == null || !Objects.equals(parent.getPostId(), request.postId())) {
                throw new BusinessException(400, "回复的评论不存在");
            }
        }
        Comment comment = new Comment();
        comment.setPostId(request.postId());
        comment.setUserId(user.id());
        comment.setParentId(request.parentId());
        comment.setContent(request.content().trim());
        comment.setStatus(1);
        commentMapper.insert(comment);
        postMapper.update(null, new LambdaUpdateWrapper<Post>()
                .eq(Post::getId, request.postId())
                .setSql("comment_count = comment_count + 1"));
        return enrich(List.of(comment), Map.of(post.getId(), post.getTitle())).get(0);
    }

    @Transactional
    public void delete(Long id, LoginUser user) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        if (!user.isAdmin() && !Objects.equals(comment.getUserId(), user.id())) {
            throw new BusinessException(403, "只能删除自己的评论");
        }
        commentMapper.deleteById(id);
        postMapper.update(null, new LambdaUpdateWrapper<Post>()
                .eq(Post::getId, comment.getPostId())
                .setSql("comment_count = GREATEST(comment_count - 1, 0)"));
    }

    public PageResult<CommentVO> mine(Long userId, long page, long size) {
        Page<Comment> result = commentMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getUserId, userId)
                        .orderByDesc(Comment::getCreatedAt));
        return PageResult.of(result.getTotal(), enrichWithPostTitle(result.getRecords()));
    }

    public PageResult<CommentVO> adminList(long page, long size, String keyword) {
        Page<Comment> result = commentMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Comment>()
                        .like(StrUtil.isNotBlank(keyword), Comment::getContent, keyword)
                        .orderByDesc(Comment::getCreatedAt));
        return PageResult.of(result.getTotal(), enrichWithPostTitle(result.getRecords()));
    }

    private List<CommentVO> enrichWithPostTitle(List<Comment> comments) {
        if (comments.isEmpty()) {
            return List.of();
        }
        Set<Long> postIds = comments.stream().map(Comment::getPostId).collect(Collectors.toSet());
        Map<Long, String> titles = postMapper.selectBatchIds(postIds).stream()
                .collect(Collectors.toMap(Post::getId, Post::getTitle));
        return enrich(comments, titles);
    }

    private List<CommentVO> enrich(List<Comment> comments, Map<Long, String> postTitles) {
        if (comments.isEmpty()) {
            return List.of();
        }
        Set<Long> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());
        Map<Long, UserBrief> users = referenceDataService.users(userIds);
        return comments.stream().map(comment -> {
            UserBrief user = users.get(comment.getUserId());
            return CommentVO.builder()
                    .id(comment.getId())
                    .postId(comment.getPostId())
                    .postTitle(postTitles.getOrDefault(comment.getPostId(), "帖子"))
                    .userId(comment.getUserId())
                    .userName(user == null ? "兔南用户" : user.nickname())
                    .userAvatar(user == null ? "" : user.avatar())
                    .parentId(comment.getParentId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }).toList();
    }

    private Post requirePost(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }
        return post;
    }
}
