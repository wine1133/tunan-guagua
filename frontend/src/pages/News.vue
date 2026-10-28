<template>
  <div class="page-container">
    <section class="page-hero news-hero">
      <div class="eyebrow">TUNAN LOCAL NEWS</div>
      <h1>读懂江南，<br />从身边的新鲜事开始。</h1>
      <p>城市更新、交通变化、周末活动和生活指南，为你筛选真正有用的本地资讯。</p>
    </section>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="keyword" style="width: 320px" size="large" clearable placeholder="搜索资讯" prefix-icon="Search" @keyup.enter="load(1)" @clear="load(1)" />
        <el-button size="large" @click="load(1)">搜索</el-button>
      </div>
      <span class="text-muted news-count">共 {{ total }} 条本地资讯</span>
    </div>
    <div v-loading="loading" class="news-list">
      <article v-for="item in news" :key="item.id" class="news-card" @click="router.push(`/news/${item.id}`)">
        <img :src="item.cover" :alt="item.title" loading="lazy" />
        <div class="news-card-body">
          <div class="news-meta"><span>{{ item.city }}资讯</span><span>{{ formatTime(item.createdAt) }}</span><span>{{ item.viewCount }} 阅读</span></div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.summary }}</p>
          <span class="read-more">阅读全文 →</span>
        </div>
      </article>
    </div>
    <EmptyState v-if="!loading && !news.length" icon="📰" title="还没有相关资讯" description="试试其他关键词。" />
    <div v-if="total > pageSize" class="page-pagination"><el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="load" /></div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { newsApi } from '../api'
import { formatTime } from '../utils/format'
import EmptyState from '../components/EmptyState.vue'

const router = useRouter()
const news = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

async function load(target = page.value) {
  loading.value = true
  page.value = target
  try {
    const data = await newsApi.list({ page: page.value, size: pageSize, keyword: keyword.value || undefined })
    news.value = data.records || []
    total.value = data.total || 0
  } finally { loading.value = false }
}

onMounted(() => load(1))
</script>

<style scoped>
.news-hero { overflow: hidden; background-image: linear-gradient(100deg, rgba(28, 32, 28, .86), rgba(28, 32, 28, .16)), url('/images/jiangnan/suzhou-canal.jpg'); background-size: cover; background-position: center; }
.news-hero::after { content: ''; position: absolute; inset: 0; background: radial-gradient(circle at 78% 8%, rgba(101, 169, 158, .22), transparent 34%); pointer-events: none; }
.news-list { display: grid; gap: 16px; }
.news-list .news-card { grid-template-columns: 260px 1fr; padding: 16px; }
.news-list .news-card img { height: 170px; }
.news-card-body { display: flex; flex-direction: column; justify-content: center; }
.news-card-body h3 { font-size: 23px; margin: 10px 0; }
.news-card-body p { font-size: 13px; line-height: 1.7; }
.read-more { color: var(--coral); font-size: 12px; font-weight: 700; margin-top: auto; }
.news-count { font-size: 13px; }
.page-pagination { display: flex; justify-content: center; margin-top: 28px; }
@media (max-width: 700px) { .news-list .news-card { grid-template-columns: 110px 1fr; } .news-list .news-card img { height: 110px; } .news-card-body h3 { font-size: 16px; } .news-card-body p, .read-more { display: none; } }
</style>
