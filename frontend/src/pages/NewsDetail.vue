<template>
  <div v-if="article" class="page-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/news' }">同城资讯</el-breadcrumb-item>
      <el-breadcrumb-item>正文</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="detail-layout">
      <article class="detail-card news-detail">
        <div class="eyebrow">{{ article.city }} · LOCAL NEWS</div>
        <h1>{{ article.title }}</h1>
        <div class="news-meta detail-news-meta"><span>{{ formatTime(article.createdAt) }}</span><span>{{ article.viewCount }} 阅读</span><span>兔南呱呱编辑部</span></div>
        <img v-if="article.cover" :src="assetUrl(article.cover)" :alt="article.title" class="news-cover" />
        <p class="news-summary">{{ article.summary }}</p>
        <div class="prose">{{ article.content }}</div>
        <div class="article-end">— 完 —</div>
      </article>
      <aside>
        <div class="side-card">
          <h3>更多兔南资讯</h3>
          <div v-for="item in moreNews" :key="item.id" class="more-news" @click="openNews(item.id)">
            <strong>{{ item.title }}</strong><span>{{ formatTime(item.createdAt) }}</span>
          </div>
        </div>
        <div class="side-card guide-card">
          <h3>分享你的兔南故事</h3>
          <p>有想推荐的小店、值得记录的变化？欢迎来社区告诉我们。</p>
          <el-button type="primary" @click="router.push('/posts')">去发布动态</el-button>
        </div>
      </aside>
    </div>
  </div>
  <div v-else v-loading="true" class="page-container loading-holder" />
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { newsApi } from '../api'
import { assetUrl, formatTime } from '../utils/format'

const route = useRoute()
const router = useRouter()
const article = ref(null)
const moreNews = ref([])

async function load() {
  article.value = await newsApi.detail(route.params.id)
  const page = await newsApi.list({ page: 1, size: 5 })
  moreNews.value = (page.records || []).filter((item) => item.id !== article.value.id).slice(0, 4)
}
function openNews(id) { router.push(`/news/${id}`) }
onMounted(load)
watch(() => route.params.id, load)
</script>

<style scoped>
.breadcrumb { margin-top: 26px; }
.detail-news-meta { margin: 0 0 22px; padding-bottom: 18px; border-bottom: 1px solid #eee8de; }
.news-cover { width: 100%; max-height: 480px; object-fit: cover; border-radius: 17px; margin-bottom: 26px; }
.news-summary { padding: 18px 20px; border-left: 4px solid var(--coral); background: #faf4ec; color: #5a655f; font-size: 15px; line-height: 1.8; border-radius: 0 12px 12px 0; }
.article-end { text-align: center; color: #b2aaa0; margin-top: 45px; font-size: 12px; letter-spacing: 4px; }
.more-news { padding: 12px 0; border-top: 1px solid #f0ebe3; cursor: pointer; display: flex; flex-direction: column; gap: 5px; }
.more-news:hover strong { color: var(--coral); }
.more-news strong { font-size: 13px; line-height: 1.5; }
.more-news span { color: var(--muted); font-size: 10px; }
.guide-card { background: linear-gradient(140deg, #243f37, #3e7468); color: white; }
.guide-card p { color: rgba(255,255,255,.65); font-size: 12px; line-height: 1.7; }
.loading-holder { min-height: 70vh; }
</style>
