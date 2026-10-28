<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/topics' }">话题广场</el-breadcrumb-item>
      <el-breadcrumb-item>{{ topic?.name || '话题详情' }}</el-breadcrumb-item>
    </el-breadcrumb>
    <section v-if="topic" class="topic-detail-hero" :style="{ backgroundImage: `linear-gradient(90deg, rgba(18, 35, 29, .92), rgba(18, 35, 29, .26)), url(${assetUrl(topic.cover || '/images/jiangnan/tuisi-garden.jpg')})` }">
      <div class="topic-hash">#</div>
      <div class="topic-detail-copy">
        <div class="eyebrow">TRENDING NOW</div>
        <h1>{{ topic.name }}</h1>
        <p>{{ topic.description }}</p>
        <div class="topic-stats"><span>🔥 {{ topic.heat }} 热度</span><span>{{ topic.postCount }} 条分享</span><span>{{ total }} 条公开动态</span></div>
      </div>
      <el-button type="primary" size="large" round @click="openPublish">参与话题</el-button>
    </section>
    <section>
      <div class="section-heading"><div><div class="eyebrow">CONVERSATIONS</div><h2>大家正在聊</h2><p>加入讨论，或者分享你的独特视角。</p></div></div>
      <div v-loading="loading" class="post-grid"><PostCard v-for="post in posts" :key="post.id" :post="post" /></div>
      <EmptyState v-if="!loading && !posts.length" icon="💭" title="还没有相关动态" description="成为第一个参与这个话题的人。" />
      <div v-if="total > pageSize" class="page-pagination"><el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="loadPosts" /></div>
    </section>
    <PublishDialog v-model="publishVisible" :default-topic-id="topicId" />
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { postApi, topicApi } from '../api'
import { authState } from '../stores/auth'
import PostCard from '../components/PostCard.vue'
import EmptyState from '../components/EmptyState.vue'
import PublishDialog from '../components/PublishDialog.vue'
import { assetUrl } from '../utils/format'

const route = useRoute()
const router = useRouter()
const topicId = Number(route.params.id)
const topic = ref(null)
const posts = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 6
const loading = ref(false)
const publishVisible = ref(false)

async function loadPosts(target = 1) {
  page.value = target
  loading.value = true
  try {
    const data = await postApi.list({ page: page.value, size: pageSize, topicId })
    posts.value = data.records || []
    total.value = data.total || 0
  } finally { loading.value = false }
}

function openPublish() {
  if (!authState.token) { router.push({ name: 'login', query: { redirect: route.fullPath } }); return }
  publishVisible.value = true
}

onMounted(async () => {
  try { topic.value = await topicApi.detail(topicId) } catch { router.replace('/topics'); return }
  await loadPosts(1)
})
watch(() => route.params.id, () => router.go(0))
</script>

<style scoped>
.breadcrumb { margin-top: 26px; }
.topic-detail-hero { position: relative; overflow: hidden; margin-top: 22px; padding: clamp(28px, 5vw, 48px); border-radius: 28px; display: flex; align-items: center; gap: 28px; background: linear-gradient(125deg, #272f42, #596887); background-size: cover; background-position: center; color: white; isolation: isolate; }
.topic-detail-hero::before { content: ''; position: absolute; inset: 0; background: radial-gradient(circle at 75% 40%, transparent, rgba(13, 24, 33, .25) 64%, rgba(13, 24, 33, .64)); z-index: -1; }
.topic-hash { flex: none; width: 94px; height: 94px; display: grid; place-items: center; border-radius: 28px; font-size: 54px; font-weight: 900; color: #f4c266; background: rgba(255,255,255,.08); }
.topic-detail-hero > :deep(.el-button) { position: relative; z-index: 3; }
.topic-detail-copy { position: relative; z-index: 3; flex: 1; }
.topic-detail-copy h1 { margin: 0 0 10px; font-size: clamp(28px, 4vw, 42px); }
.topic-detail-copy p { color: rgba(255,255,255,.7); line-height: 1.7; margin: 0 0 14px; }
.topic-stats { display: flex; gap: 22px; color: rgba(255,255,255,.62); font-size: 12px; }
.page-pagination { display: flex; justify-content: center; margin-top: 26px; }
@media (max-width: 760px) { .topic-detail-hero { align-items: flex-start; flex-wrap: wrap; } .topic-hash { width: 68px; height: 68px; font-size: 36px; } }
</style>
