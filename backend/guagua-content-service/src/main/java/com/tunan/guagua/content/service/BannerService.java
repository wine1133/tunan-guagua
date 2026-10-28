package com.tunan.guagua.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tunan.guagua.common.api.PageResult;
import com.tunan.guagua.common.exception.BusinessException;
import com.tunan.guagua.content.dto.BannerRequest;
import com.tunan.guagua.content.entity.Banner;
import com.tunan.guagua.content.mapper.BannerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {
    private final BannerMapper bannerMapper;

    public List<Banner> enabledList() {
        return bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort));
    }

    public PageResult<Banner> adminList(long page, long size) {
        Page<Banner> result = bannerMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Banner>().orderByAsc(Banner::getSort).orderByDesc(Banner::getId));
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Transactional
    public Banner save(Long id, BannerRequest request) {
        Banner banner = id == null ? new Banner() : require(id);
        banner.setTitle(request.title().trim());
        banner.setImageUrl(request.imageUrl().trim());
        banner.setLinkUrl(request.linkUrl() == null ? "" : request.linkUrl().trim());
        banner.setSort(request.sort());
        banner.setStatus(request.status());
        if (id == null) {
            bannerMapper.insert(banner);
        } else {
            bannerMapper.updateById(banner);
        }
        return banner;
    }

    @Transactional
    public void delete(Long id) {
        require(id);
        bannerMapper.deleteById(id);
    }

    private Banner require(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException(404, "轮播图不存在");
        }
        return banner;
    }
}
