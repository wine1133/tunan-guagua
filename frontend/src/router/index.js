import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import AdminLayout from '../layouts/AdminLayout.vue'

const routes = [
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', name: 'home', component: () => import('../pages/Home.vue') },
      { path: 'circles', name: 'circles', component: () => import('../pages/Circles.vue') },
      { path: 'circles/:id', name: 'circle-detail', component: () => import('../pages/CircleDetail.vue') },
      { path: 'topics', name: 'topics', component: () => import('../pages/Topics.vue') },
      { path: 'topics/:id', name: 'topic-detail', component: () => import('../pages/TopicDetail.vue') },
      { path: 'posts', name: 'posts', component: () => import('../pages/Posts.vue') },
      { path: 'posts/:id', name: 'post-detail', component: () => import('../pages/PostDetail.vue') },
      { path: 'news', name: 'news', component: () => import('../pages/News.vue') },
      { path: 'news/:id', name: 'news-detail', component: () => import('../pages/NewsDetail.vue') },
      { path: 'profile', name: 'profile', component: () => import('../pages/Profile.vue'), meta: { requiresAuth: true } }
    ]
  },
  { path: '/login', name: 'login', component: () => import('../pages/Login.vue') },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'admin-dashboard', component: () => import('../pages/admin/Dashboard.vue') },
      { path: 'users', name: 'admin-users', component: () => import('../pages/admin/Users.vue') },
      { path: 'circles', name: 'admin-circles', component: () => import('../pages/admin/Circles.vue') },
      { path: 'topics', name: 'admin-topics', component: () => import('../pages/admin/Topics.vue') },
      { path: 'posts', name: 'admin-posts', component: () => import('../pages/admin/Posts.vue') },
      { path: 'comments', name: 'admin-comments', component: () => import('../pages/admin/Comments.vue') },
      { path: 'interactions', name: 'admin-interactions', component: () => import('../pages/admin/Interactions.vue') },
      { path: 'banners', name: 'admin-banners', component: () => import('../pages/admin/Banners.vue') },
      { path: 'news', name: 'admin-news', component: () => import('../pages/admin/NewsManage.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to) => {
  const token = localStorage.getItem('guagua_token')
  let user = null
  try {
    user = JSON.parse(localStorage.getItem('guagua_user') || 'null')
  } catch {
    user = null
  }
  if (to.meta.requiresAuth && !token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && (!token || user?.role !== 'ADMIN')) {
    return { name: 'home' }
  }
  return true
})

export default router
