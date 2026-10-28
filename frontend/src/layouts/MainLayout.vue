<template>
  <div class="site-shell">
    <header class="site-header">
      <div class="header-inner">
        <router-link to="/" class="brand">
          <span class="brand-mark">兔</span>
          <span class="brand-copy">
            <strong>兔南呱呱</strong>
            <small>同城生活，一起呱唧</small>
          </span>
        </router-link>
        <nav class="main-nav">
          <router-link to="/" class="nav-item" :class="{ active: route.name === 'home' }">首页</router-link>
          <router-link to="/circles" class="nav-item" :class="{ active: route.name?.startsWith('circle') }">圈子广场</router-link>
          <router-link to="/topics" class="nav-item" :class="{ active: route.name?.startsWith('topic') }">话题广场</router-link>
          <router-link to="/posts" class="nav-item" :class="{ active: route.name === 'posts' || route.name === 'post-detail' }">同城动态</router-link>
          <router-link to="/news" class="nav-item" :class="{ active: route.name?.startsWith('news') }">同城资讯</router-link>
        </nav>
        <div class="header-actions">
          <span v-if="demoMode" class="demo-badge">在线演示</span>
          <el-button class="publish-btn" round @click="openPublish"><el-icon><Plus /></el-icon> 发布</el-button>
          <template v-if="authState.user">
            <el-dropdown trigger="click">
              <button class="user-trigger">
                <UserAvatar :name="authState.user.nickname" :src="authState.user.avatar" :size="36" />
                <span>{{ authState.user.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item v-if="authState.user.role === 'ADMIN'" @click="router.push('/admin')">管理后台</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <el-button v-else text @click="goLogin">登录 / 注册</el-button>
        </div>
      </div>
    </header>

    <main class="main-content">
      <router-view />
    </main>

    <footer class="site-footer">
      <div class="footer-inner">
        <div>
          <div class="footer-brand">兔南呱呱同城网</div>
          <p>让每一份同城热爱，都有被看见的地方。</p>
        </div>
        <div class="footer-links">
          <router-link to="/circles">圈子</router-link>
          <router-link to="/topics">话题</router-link>
          <router-link to="/news">资讯</router-link>
          <span>© 2026 兔南呱呱</span>
        </div>
      </div>
    </footer>
    <PublishDialog v-model="publishVisible" />
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import UserAvatar from '../components/UserAvatar.vue'
import PublishDialog from '../components/PublishDialog.vue'
import { authState, logout, refreshUser } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const publishVisible = ref(false)
const demoMode = import.meta.env.VITE_DEMO_MODE === 'true'

const openPublish = () => {
  if (!authState.token) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  publishVisible.value = true
}

const goLogin = () => router.push({ name: 'login', query: { redirect: route.fullPath } })

async function handleLogout() {
  await ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' })
  logout()
  router.push('/')
}

const expireHandler = () => router.push({ name: 'login', query: { redirect: route.fullPath } })
onMounted(() => {
  refreshUser()
  window.addEventListener('guagua-auth-expired', expireHandler)
})
onBeforeUnmount(() => window.removeEventListener('guagua-auth-expired', expireHandler))
</script>

<style scoped>
.site-shell { min-height: 100vh; display: flex; flex-direction: column; }
.site-header { position: sticky; top: 0; z-index: 50; background: rgba(247, 243, 235, .92); backdrop-filter: blur(18px); border-bottom: 1px solid rgba(31, 55, 49, .08); }
.header-inner { max-width: 1240px; margin: 0 auto; height: 76px; padding: 0 24px; display: flex; align-items: center; gap: 36px; }
.brand { display: flex; align-items: center; gap: 11px; color: var(--ink); text-decoration: none; flex: none; }
.brand-mark { width: 44px; height: 44px; display: grid; place-items: center; border-radius: 15px 15px 15px 5px; background: var(--coral); color: white; font-weight: 900; font-size: 22px; transform: rotate(-4deg); box-shadow: 0 8px 20px rgba(245, 102, 79, .25); }
.brand-copy { display: flex; flex-direction: column; line-height: 1.15; }
.brand-copy strong { font-size: 19px; letter-spacing: .5px; }
.brand-copy small { color: var(--muted); font-size: 11px; margin-top: 4px; }
.main-nav { display: flex; align-items: center; gap: 26px; height: 100%; }
.nav-item { position: relative; color: #5e6864; text-decoration: none; font-size: 14px; font-weight: 600; padding: 27px 0; white-space: nowrap; }
.nav-item::after { content: ''; position: absolute; left: 50%; bottom: 19px; width: 0; height: 3px; border-radius: 3px; background: var(--coral); transform: translateX(-50%); transition: width .2s; }
.nav-item.active, .nav-item:hover { color: var(--ink); }
.nav-item.active::after { width: 18px; }
.header-actions { margin-left: auto; display: flex; align-items: center; gap: 12px; }
.demo-badge { padding: 5px 9px; border-radius: 999px; background: #e5f1ed; color: var(--teal); font-size: 10px; font-weight: 700; white-space: nowrap; }
.publish-btn { background: var(--ink); color: white; border-color: var(--ink); padding: 10px 18px; }
.publish-btn:hover { background: var(--coral); border-color: var(--coral); color: white; }
.user-trigger { display: flex; align-items: center; gap: 8px; border: 0; background: transparent; color: var(--ink); cursor: pointer; font-size: 14px; font-weight: 600; }
.main-content { flex: 1; }
.site-footer { background: #172622; color: #d8e0dc; margin-top: 80px; }
.footer-inner { max-width: 1240px; margin: 0 auto; padding: 44px 24px; display: flex; justify-content: space-between; gap: 30px; }
.footer-brand { font-size: 22px; font-weight: 800; color: #fff; margin-bottom: 8px; }
.site-footer p { color: #91a29b; margin: 0; }
.footer-links { display: flex; align-items: center; gap: 22px; color: #91a29b; font-size: 13px; }
.footer-links a { color: #d8e0dc; text-decoration: none; }
@media (max-width: 900px) {
  .header-inner { gap: 18px; padding: 0 16px; }
  .brand-copy small { display: none; }
  .main-nav { gap: 16px; overflow-x: auto; }
  .user-trigger > span { display: none; }
  .publish-btn { padding: 8px 13px; }
}
@media (max-width: 640px) {
  .header-inner { height: auto; min-height: 66px; flex-wrap: wrap; gap: 10px; padding-bottom: 8px; }
  .main-nav { order: 3; width: 100%; height: 38px; }
  .nav-item { padding: 8px 0; }
  .nav-item::after { bottom: 2px; }
  .brand-copy strong { font-size: 16px; }
  .brand-mark { width: 38px; height: 38px; }
  .publish-btn { font-size: 13px; }
  .header-actions { gap: 4px; }
  .footer-inner { flex-direction: column; }
  .footer-links { flex-wrap: wrap; }
}
</style>
