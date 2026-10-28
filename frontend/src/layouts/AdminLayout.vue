<template>
  <div class="admin-shell">
    <aside class="admin-aside">
      <router-link to="/" class="admin-brand">
        <span class="admin-mark">兔</span>
        <span><strong>呱呱管理台</strong><small>兔南同城运营中心</small></span>
      </router-link>
      <el-menu :default-active="route.path" router class="admin-menu">
        <el-menu-item index="/admin/dashboard"><el-icon><DataBoard /></el-icon><span>数据总览</span></el-menu-item>
        <el-menu-item index="/admin/users"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
        <el-menu-item index="/admin/circles"><el-icon><Connection /></el-icon><span>圈子审核</span></el-menu-item>
        <el-menu-item index="/admin/topics"><el-icon><ChatLineSquare /></el-icon><span>话题审核</span></el-menu-item>
        <el-menu-item index="/admin/posts"><el-icon><Document /></el-icon><span>帖子审核</span></el-menu-item>
        <el-menu-item index="/admin/comments"><el-icon><ChatDotRound /></el-icon><span>评论管理</span></el-menu-item>
        <el-menu-item index="/admin/interactions"><el-icon><Pointer /></el-icon><span>点赞收藏</span></el-menu-item>
        <el-menu-item index="/admin/banners"><el-icon><Picture /></el-icon><span>轮播图管理</span></el-menu-item>
        <el-menu-item index="/admin/news"><el-icon><Notebook /></el-icon><span>资讯管理</span></el-menu-item>
      </el-menu>
      <div class="aside-bottom">
        <router-link to="/" class="back-site"><el-icon><Back /></el-icon> 返回用户端</router-link>
      </div>
    </aside>
    <section class="admin-main">
      <header class="admin-header">
        <div>
          <div class="admin-eyebrow">TUNAN ADMIN</div>
          <h1>{{ pageTitle }}</h1>
        </div>
        <div class="admin-user">
          <UserAvatar :name="authState.user?.nickname" :src="authState.user?.avatar" :size="38" />
          <div><strong>{{ authState.user?.nickname }}</strong><span>平台管理员</span></div>
          <el-button text @click="handleLogout">退出</el-button>
        </div>
      </header>
      <div class="admin-content"><router-view /></div>
    </section>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import UserAvatar from '../components/UserAvatar.vue'
import { authState, logout } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const titles = {
  '/admin/dashboard': '数据总览',
  '/admin/users': '用户管理',
  '/admin/circles': '圈子审核',
  '/admin/topics': '话题审核',
  '/admin/posts': '帖子审核',
  '/admin/comments': '评论管理',
  '/admin/interactions': '点赞与收藏',
  '/admin/banners': '轮播图管理',
  '/admin/news': '资讯管理'
}
const pageTitle = computed(() => titles[route.path] || '管理后台')
const expireHandler = () => router.replace({ name: 'login', query: { redirect: route.fullPath } })
onMounted(() => window.addEventListener('guagua-auth-expired', expireHandler))
onBeforeUnmount(() => window.removeEventListener('guagua-auth-expired', expireHandler))

async function handleLogout() {
  await ElMessageBox.confirm('确定退出管理后台吗？', '提示', { type: 'warning' })
  logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-shell { display: flex; min-height: 100vh; background: #f3f5f2; }
.admin-aside { width: 248px; position: fixed; inset: 0 auto 0 0; background: #14231f; color: white; padding: 24px 16px; display: flex; flex-direction: column; }
.admin-brand { display: flex; align-items: center; gap: 12px; color: white; text-decoration: none; padding: 6px 10px 24px; }
.admin-mark { width: 42px; height: 42px; display: grid; place-items: center; border-radius: 14px 14px 14px 5px; background: var(--coral); font-weight: 900; font-size: 20px; }
.admin-brand span:last-child { display: flex; flex-direction: column; }
.admin-brand strong { font-size: 16px; }
.admin-brand small { color: #81948c; font-size: 10px; margin-top: 4px; }
.admin-menu { border: 0; background: transparent; --el-menu-text-color: #a8b8b1; --el-menu-hover-bg-color: rgba(255,255,255,.06); --el-menu-active-color: #fff; }
.admin-menu :deep(.el-menu-item) { border-radius: 10px; margin: 4px 0; height: 44px; }
.admin-menu :deep(.el-menu-item.is-active) { background: var(--coral); }
.aside-bottom { margin-top: auto; padding-top: 20px; border-top: 1px solid rgba(255,255,255,.08); }
.back-site { display: flex; align-items: center; gap: 8px; color: #a8b8b1; text-decoration: none; padding: 10px; font-size: 13px; }
.back-site:hover { color: white; }
.admin-main { margin-left: 248px; width: calc(100% - 248px); }
.admin-header { min-height: 104px; padding: 24px 34px; background: rgba(255,255,255,.7); border-bottom: 1px solid #e4e9e5; display: flex; align-items: center; justify-content: space-between; }
.admin-eyebrow { color: var(--coral); font-size: 10px; letter-spacing: 2px; font-weight: 800; margin-bottom: 5px; }
.admin-header h1 { margin: 0; font-size: 26px; color: var(--ink); }
.admin-user { display: flex; align-items: center; gap: 10px; }
.admin-user > div { display: flex; flex-direction: column; font-size: 13px; }
.admin-user span { color: var(--muted); font-size: 11px; margin-top: 2px; }
.admin-content { padding: 30px 34px 50px; }
@media (max-width: 900px) {
  .admin-aside { width: 78px; padding: 18px 10px; }
  .admin-brand span:last-child, .admin-menu :deep(.el-menu-item span), .back-site { display: none; }
  .admin-menu :deep(.el-menu-item) { justify-content: center; padding: 0 !important; }
  .admin-main { margin-left: 78px; width: calc(100% - 78px); }
  .admin-header, .admin-content { padding-left: 18px; padding-right: 18px; }
  .admin-user > div { display: none; }
}
</style>


