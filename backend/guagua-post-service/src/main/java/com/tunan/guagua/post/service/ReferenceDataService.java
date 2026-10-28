package com.tunan.guagua.post.service;

import com.tunan.guagua.post.client.ContentBrief;
import com.tunan.guagua.post.client.ContentClient;
import com.tunan.guagua.post.client.UserBrief;
import com.tunan.guagua.post.client.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferenceDataService {
    private final UserClient userClient;
    private final ContentClient contentClient;

    public Map<Long, UserBrief> users(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        String values = ids.stream().filter(Objects::nonNull).distinct()
                .map(String::valueOf).collect(Collectors.joining(","));
        if (values.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            var response = userClient.batch(values);
            if (response != null && response.getCode() == 200 && response.getData() != null) {
                return response.getData().stream().collect(Collectors.toMap(UserBrief::id, Function.identity()));
            }
        } catch (Exception exception) {
            log.warn("Failed to load user references: {}", exception.getMessage());
        }
        return Collections.emptyMap();
    }

    public Map<Long, ContentBrief> circles(Collection<Long> ids) {
        return loadContent(ids, true);
    }

    public Map<Long, ContentBrief> topics(Collection<Long> ids) {
        return loadContent(ids, false);
    }

    private Map<Long, ContentBrief> loadContent(Collection<Long> ids, boolean circle) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        String values = ids.stream().filter(Objects::nonNull).distinct()
                .map(String::valueOf).collect(Collectors.joining(","));
        if (values.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            var response = circle ? contentClient.batchCircles(values) : contentClient.batchTopics(values);
            if (response != null && response.getCode() == 200 && response.getData() != null) {
                return response.getData().stream().collect(Collectors.toMap(ContentBrief::id, Function.identity()));
            }
        } catch (Exception exception) {
            log.warn("Failed to load content references: {}", exception.getMessage());
        }
        return Collections.emptyMap();
    }
}
