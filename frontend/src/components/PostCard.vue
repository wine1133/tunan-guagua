<template>
  <div class="post-card" @click="open">
    <div class="post-card-head">
      <UserAvatar :name="post.authorName" :src="post.authorAvatar" :size="38" />
      <div class="post-author">
        <strong>{{ post.authorName }}</strong>
        <span>{{ formatTime(post.createdAt) }} · {{ post.circleName || '同城广场' }}</span>
      </div>
      <el-tag v-if="post.topicName" effect="plain" round size="small">{{ post.topicName }}</el-tag>
    </div>
    <h3>{{ post.title }}</h3>
    <p>{{ excerpt(post.content, 92) }}</p>
    <div v-if="post.images?.length" class="post-cover">
      <img :src="post.images[0]" :alt="post.title" loading="lazy" />
    </div>
    <div class="post-card-foot">
      <span><el-icon><View /></el-icon> {{ post.viewCount || 0 }}</span>
      <span><el-icon><ChatDotRound /></el-icon> {{ post.commentCount || 0 }}</span>
      <span :class="{ active: post.liked }"><el-icon><Pointer /></el-icon> {{ post.likeCount || 0 }}</span>
      <span :class="{ active: post.favorited }"><el-icon><Star /></el-icon> {{ post.favoriteCount || 0 }}</span>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import UserAvatar from './UserAvatar.vue'
import { excerpt, formatTime } from '../utils/format'

const props = defineProps({ post: { type: Object, required: true } })
const router = useRouter()
const open = () => router.push(`/posts/${props.post.id}`)
</script>
