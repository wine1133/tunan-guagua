<template>
  <div class="page-container">
    <section class="page-hero posts-hero">
      <div class="eyebrow">TUNAN MOMENTS</div>
      <h1>把江南生活写成<br />一封同城来信。</h1>
      <p>一顿好吃的饭、一场说走就走的散步、一次真诚的求助，都值得被看见。</p>
    </section>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="keyword" style="width: 320px" size="large" clearable placeholder="搜索帖子标题或内容" prefix-icon="Search" @keyup.enter="load(1)" @clear="load(1)" />
        <el-button size="large" @click="load(1)">搜索</el-button>
      </div>
      <el-button type="primary" size="large" @click="openPublish"><el-icon><Plus /></el-icon> 发布动态</el-button>
    </div>
    <div v-loading="loading" class="post-grid"><PostCard v-for="post in posts" :key="post.id" :post="post" /></div>
    <EmptyState v-if="!loading && !posts.length" icon="✍️" title="还没有匹配的分享" description="换个关键词，或者发布你的第一条兔南动态。" />
    <div v-if="total > pageSize" class="page-pagination"><el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="load" /></div>
    <PublishDialog v-model="publishVisible" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { postApi } from '../api'
import { authState } from '../stores/auth'
import PostCard from '../components/PostCard.vue'
import EmptyState from '../components/EmptyState.vue'
import PublishDialog from '../components/PublishDialog.vue'

const route = useRoute()
const router = useRouter()
const posts = ref([])
const keyword = ref(String(route.query.keyword || ''))
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)
const publishVisible = ref(false)

async function load(target = page.value) {
  loading.value = true
  page.value = target
  try {
    const data = await postApi.list({ page: page.value, size: pageSize, keyword: keyword.value || undefined })
    posts.value = data.records || []
    total.value = data.total || 0
  } finally { loading.value = false }
}

function openPublish() {
  if (!authState.token) { router.push({ name: 'login', query: { redirect: route.fullPath } }); return }
  publishVisible.value = true
}

onMounted(() => load(1))
</script>

<style scoped>
.posts-hero { overflow: hidden; background-image: linear-gradient(100deg, rgba(29, 34, 29, .86), rgba(29, 34, 29, .2)), url('/images/jiangnan/zhouzhuang-boat.jpg'); background-size: cover; background-position: center; }
.posts-hero::after { content: ''; position: absolute; inset: 0; background: radial-gradient(circle at 78% 12%, rgba(255, 207, 120, .2), transparent 35%); pointer-events: none; }
.page-pagination { display: flex; justify-content: center; margin-top: 28px; }
</style>
