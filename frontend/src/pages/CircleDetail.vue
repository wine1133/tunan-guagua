<template>
  <div class="page-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/circles' }">圈子广场</el-breadcrumb-item>
      <el-breadcrumb-item>{{ circle?.name || '圈子详情' }}</el-breadcrumb-item>
    </el-breadcrumb>
    <section v-if="circle" class="circle-detail-hero" :style="{ backgroundImage: `linear-gradient(90deg, rgba(18, 35, 29, .92), rgba(18, 35, 29, .26)), url(${circle.cover || '/images/jiangnan/qibao-canal.jpg'})` }">
      <div class="circle-symbol">◎</div>
      <div class="circle-detail-copy">
        <div class="eyebrow">TUNAN CIRCLE</div>
        <h1>{{ circle.name }}</h1>
        <p>{{ circle.description }}</p>
        <div class="circle-stats"><span><strong>{{ circle.memberCount }}</strong> 成员</span><span><strong>{{ circle.postCount }}</strong> 分享</span><span><strong>{{ total }}</strong> 社区动态</span></div>
      </div>
      <el-button size="large" round type="primary" @click="openPublish">发布到圈子</el-button>
    </section>

    <section>
      <div class="section-heading">
        <div><div class="eyebrow">DISCUSSIONS</div><h2>圈内新鲜事</h2><p>只收录已通过审核的社区分享。</p></div>
      </div>
      <div v-loading="loading" class="post-grid">
        <PostCard v-for="post in posts" :key="post.id" :post="post" />
      </div>
      <EmptyState v-if="!loading && !posts.length" icon="🌤️" title="圈子里还很安静" description="来发布第一条动态，让这里热闹起来。" />
      <div v-if="total > pageSize" class="page-pagination"><el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="loadPosts" /></div>
    </section>
    <PublishDialog v-model="publishVisible" :default-circle-id="circleId" />
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { circleApi, postApi } from '../api'
import { authState } from '../stores/auth'
import PostCard from '../components/PostCard.vue'
import EmptyState from '../components/EmptyState.vue'
import PublishDialog from '../components/PublishDialog.vue'

const route = useRoute()
const router = useRouter()
const circleId = Number(route.params.id)
const circle = ref(null)
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
    const data = await postApi.list({ page: page.value, size: pageSize, circleId })
    posts.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function openPublish() {
  if (!authState.token) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  publishVisible.value = true
}

onMounted(async () => {
  try { circle.value = await circleApi.detail(circleId) } catch { router.replace('/circles'); return }
  await loadPosts(1)
})
watch(() => route.params.id, () => router.go(0))
</script>

<style scoped>
.breadcrumb { margin-top: 26px; }
.circle-detail-hero { position: relative; overflow: hidden; margin-top: 22px; padding: clamp(28px, 5vw, 48px); border-radius: 28px; display: flex; align-items: center; gap: 28px; background: linear-gradient(125deg, #203c34, #477d6f); background-size: cover; background-position: center; color: white; isolation: isolate; }
.circle-detail-hero::before { content: ''; position: absolute; inset: 0; background: radial-gradient(circle at 75% 40%, transparent, rgba(9, 23, 18, .22) 64%, rgba(9, 23, 18, .58)); z-index: -1; }
.circle-symbol { position: relative; z-index: 3; flex: none; width: 94px; height: 94px; display: grid; place-items: center; border-radius: 30px; font-size: 46px; background: rgba(255,255,255,.12); border: 1px solid rgba(255,255,255,.14); }
.circle-detail-hero > :deep(.el-button) { position: relative; z-index: 3; }
.circle-detail-copy { position: relative; z-index: 3; flex: 1; }
.circle-detail-copy h1 { margin: 0 0 10px; font-size: clamp(28px, 4vw, 42px); }
.circle-detail-copy p { color: rgba(255,255,255,.7); line-height: 1.7; margin: 0 0 14px; }
.circle-stats { display: flex; gap: 22px; color: rgba(255,255,255,.62); font-size: 12px; }
.circle-stats strong { color: white; font-size: 18px; margin-right: 3px; }
.page-pagination { display: flex; justify-content: center; margin-top: 26px; }
@media (max-width: 760px) { .circle-detail-hero { align-items: flex-start; flex-wrap: wrap; } .circle-symbol { position: relative; z-index: 3; width: 68px; height: 68px; font-size: 32px; } }
</style>
