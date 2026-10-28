<template>
  <div>
    <div class="dashboard-grid">
      <div class="metric-card coral"><div class="metric-top"><span>注册用户</span><el-icon><User /></el-icon></div><strong>{{ users.totalUsers || 0 }}</strong><small>今日新增 {{ users.todayNew || 0 }} 人</small></div>
      <div class="metric-card teal"><div class="metric-top"><span>同城帖子</span><el-icon><Document /></el-icon></div><strong>{{ posts.totalPosts || 0 }}</strong><small>{{ posts.publishedPosts || 0 }} 篇已公开</small></div>
      <div class="metric-card yellow"><div class="metric-top"><span>待审核内容</span><el-icon><Bell /></el-icon></div><strong>{{ pendingTotal }}</strong><small>圈子 {{ content.pendingCircles || 0 }} · 话题 {{ content.pendingTopics || 0 }} · 帖子 {{ posts.pendingPosts || 0 }}</small></div>
      <div class="metric-card dark"><div class="metric-top"><span>社区互动</span><el-icon><Pointer /></el-icon></div><strong>{{ interactionTotal }}</strong><small>点赞 {{ posts.totalLikes || 0 }} · 收藏 {{ posts.totalFavorites || 0 }}</small></div>
    </div>

    <div class="dashboard-main">
      <section class="admin-panel chart-panel">
        <div class="admin-panel-header"><div><h2>内容健康度</h2><small>平台核心数据一览</small></div><el-tag type="success" effect="plain" round>运行中</el-tag></div>
        <div class="progress-row"><span>用户活跃率</span><div class="progress"><i :style="{ width: `${activeRate}%` }" /></div><strong>{{ activeRate }}%</strong></div>
        <div class="progress-row"><span>帖子公开率</span><div class="progress"><i :style="{ width: `${publishRate}%` }" /></div><strong>{{ publishRate }}%</strong></div>
        <div class="progress-row"><span>审核处理完成度</span><div class="progress"><i :style="{ width: `${auditRate}%` }" /></div><strong>{{ auditRate }}%</strong></div>
        <div class="progress-row"><span>内容互动率</span><div class="progress"><i :style="{ width: `${interactionRate}%` }" /></div><strong>{{ interactionRate }}%</strong></div>
      </section>

      <section class="admin-panel">
        <div class="admin-panel-header"><h2>待办事项</h2><router-link to="/admin/posts" class="panel-link">进入审核 →</router-link></div>
        <div class="todo-item"><span class="todo-icon">◎</span><div><strong>{{ content.pendingCircles || 0 }} 个圈子待审核</strong><small>及时处理用户创建的圈子</small></div><router-link to="/admin/circles">处理</router-link></div>
        <div class="todo-item"><span class="todo-icon">#</span><div><strong>{{ content.pendingTopics || 0 }} 个话题待审核</strong><small>让有价值的话题尽快上线</small></div><router-link to="/admin/topics">处理</router-link></div>
        <div class="todo-item"><span class="todo-icon">✎</span><div><strong>{{ posts.pendingPosts || 0 }} 篇帖子待审核</strong><small>审核通过后将在社区公开展示</small></div><router-link to="/admin/posts">处理</router-link></div>
      </section>
    </div>

    <section class="admin-panel quick-panel">
      <div class="admin-panel-header"><h2>快捷管理</h2></div>
      <div class="quick-grid">
        <div @click="router.push('/admin/users')"><el-icon><User /></el-icon><strong>用户管理</strong><span>{{ users.activeUsers || 0 }} 个正常账号</span></div>
        <div @click="router.push('/admin/comments')"><el-icon><ChatDotRound /></el-icon><strong>评论管理</strong><span>{{ posts.totalComments || 0 }} 条社区评论</span></div>
        <div @click="router.push('/admin/interactions')"><el-icon><Star /></el-icon><strong>互动记录</strong><span>{{ interactionTotal }} 次点赞收藏</span></div>
        <div @click="router.push('/admin/news')"><el-icon><Notebook /></el-icon><strong>资讯发布</strong><span>{{ content.totalNews || 0 }} 篇本地资讯</span></div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { statsApi, userApi } from '../../api'

const router = useRouter()
const users = reactive({})
const content = reactive({})
const posts = reactive({})

const pendingTotal = computed(() => (content.pendingCircles || 0) + (content.pendingTopics || 0) + (posts.pendingPosts || 0))
const interactionTotal = computed(() => (posts.totalLikes || 0) + (posts.totalFavorites || 0))
const activeRate = computed(() => users.totalUsers ? Math.round((users.activeUsers / users.totalUsers) * 100) : 0)
const publishRate = computed(() => posts.totalPosts ? Math.round((posts.publishedPosts / posts.totalPosts) * 100) : 0)
const auditRate = computed(() => posts.totalPosts ? Math.round(((posts.totalPosts - posts.pendingPosts) / posts.totalPosts) * 100) : 100)
const interactionRate = computed(() => posts.publishedPosts ? Math.min(100, Math.round((interactionTotal.value / posts.publishedPosts) * 4)) : 0)

onMounted(async () => {
  const [userStats, contentStats, postStats] = await Promise.all([userApi.stats(), statsApi.content(), statsApi.posts()])
  Object.assign(users, userStats)
  Object.assign(content, contentStats)
  Object.assign(posts, postStats)
})
</script>

<style scoped>
.dashboard-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 16px; }
.metric-card { padding: 21px; border-radius: 17px; color: white; min-height: 145px; display: flex; flex-direction: column; }
.metric-card.coral { background: linear-gradient(145deg, #e85d48, #f28b68); }
.metric-card.teal { background: linear-gradient(145deg, #246c63, #3a8b7e); }
.metric-card.yellow { background: linear-gradient(145deg, #c98a23, #e9b958); }
.metric-card.dark { background: linear-gradient(145deg, #1b2d28, #354b43); }
.metric-top { display: flex; justify-content: space-between; align-items: center; color: rgba(255,255,255,.7); font-size: 12px; }
.metric-card > strong { font-size: 34px; margin-top: auto; line-height: 1; }
.metric-card small { color: rgba(255,255,255,.65); margin-top: 8px; }
.dashboard-main { display: grid; grid-template-columns: 1.25fr .75fr; gap: 18px; margin-top: 18px; }
.chart-panel small, .panel-link { color: var(--muted); font-size: 11px; }
.panel-link { text-decoration: none; }
.progress-row { display: grid; grid-template-columns: 110px 1fr 44px; gap: 12px; align-items: center; margin-top: 21px; font-size: 12px; color: #606c66; }
.progress { height: 8px; background: #eef1ed; border-radius: 8px; overflow: hidden; }
.progress i { display: block; height: 100%; border-radius: inherit; background: linear-gradient(90deg, var(--coral), var(--yellow)); }
.progress-row strong { text-align: right; color: var(--ink); }
.todo-item { display: flex; align-items: center; gap: 12px; padding: 14px 0; border-top: 1px solid #eef1ed; }
.todo-icon { width: 34px; height: 34px; border-radius: 10px; display: grid; place-items: center; background: #f5eee4; color: var(--coral); font-weight: 800; }
.todo-item div { flex: 1; display: flex; flex-direction: column; gap: 4px; }
.todo-item strong { font-size: 12px; }
.todo-item small { color: var(--muted); font-size: 10px; }
.todo-item a { color: var(--teal); font-size: 11px; text-decoration: none; font-weight: 700; }
.quick-panel { margin-top: 18px; }
.quick-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.quick-grid > div { padding: 17px; border-radius: 12px; background: #f7f8f6; cursor: pointer; transition: .2s; display: flex; flex-direction: column; gap: 7px; }
.quick-grid > div:hover { background: #edf4f0; transform: translateY(-2px); }
.quick-grid .el-icon { color: var(--teal); font-size: 20px; }
.quick-grid strong { font-size: 13px; }
.quick-grid span { color: var(--muted); font-size: 10px; }
@media (max-width: 1100px) { .dashboard-grid { grid-template-columns: repeat(2, 1fr); } .dashboard-main { grid-template-columns: 1fr; } }
@media (max-width: 700px) { .dashboard-grid, .quick-grid { grid-template-columns: 1fr; } }
</style>
