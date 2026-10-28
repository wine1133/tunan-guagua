<template>
  <div class="page-container">
    <section class="page-hero circles-hero">
      <div class="eyebrow">CIRCLE SQUARE</div>
      <h1>找到属于你的<br />江南同城小宇宙。</h1>
      <p>美食、运动、摄影、萌宠……在真实的生活兴趣里，认识真正聊得来的人。</p>
    </section>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="keyword" style="width: 280px" size="large" clearable placeholder="搜索圈子名称" prefix-icon="Search" @keyup.enter="load(1)" @clear="load(1)" />
        <el-button size="large" @click="load(1)">搜索</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" size="large" @click="openCreate"><el-icon><Plus /></el-icon> 创建圈子</el-button>
      </div>
    </div>

    <div v-loading="loading" class="card-grid">
      <article v-for="(circle, index) in circles" :key="circle.id" class="circle-card jiangnan-card" @click="router.push(`/circles/${circle.id}`)">
        <div class="circle-cover">
          <img :src="circle.cover || '/images/jiangnan/qibao-canal.jpg'" :alt="circle.name" loading="lazy" />
          <div class="cover-shade" />
          <span class="circle-icon">{{ icons[index % icons.length] }}</span>
        </div>
        <div class="circle-card-body">
          <h3>{{ circle.name }}</h3>
          <p>{{ circle.description || '这个圈子正在等待第一条介绍。' }}</p>
          <div class="card-meta"><span>{{ circle.memberCount }} 位成员</span><span>{{ circle.postCount }} 条分享</span></div>
        </div>
      </article>
    </div>
    <EmptyState v-if="!loading && !circles.length" icon="🧭" title="还没有找到合适的圈子" description="换个关键词，或者亲手创建一个。" />
    <div v-if="total > pageSize" class="page-pagination">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="load" />
    </div>

    <el-dialog v-model="createVisible" title="创建一个新圈子" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item label="圈子名称"><el-input v-model="form.name" maxlength="80" placeholder="例如：兔南咖啡地图" /></el-form-item>
        <el-form-item label="圈子介绍"><el-input v-model="form.description" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="这个圈子适合谁？大家会在这里聊什么？" /></el-form-item>
        <el-form-item label="封面图片地址（可选）"><el-input v-model="form.cover" placeholder="https://..." /></el-form-item>
      </el-form>
      <template #footer><el-button @click="createVisible = false">取消</el-button><el-button type="primary" :loading="submitting" @click="submitCreate">提交审核</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { circleApi } from '../api'
import { authState } from '../stores/auth'
import EmptyState from '../components/EmptyState.vue'

const router = useRouter()
const icons = ['🍜', '🏕️', '🐾', '📷', '🎨', '🚴', '🎵', '📚']
const loading = ref(false)
const submitting = ref(false)
const circles = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = 12
const total = ref(0)
const createVisible = ref(false)
const form = reactive({ name: '', description: '', cover: '' })

async function load(targetPage = page.value) {
  loading.value = true
  page.value = targetPage
  try {
    const data = await circleApi.list({ page: page.value, size: pageSize, keyword: keyword.value || undefined })
    circles.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function openCreate() {
  if (!authState.token) {
    router.push({ name: 'login', query: { redirect: '/circles' } })
    return
  }
  createVisible.value = true
}

async function submitCreate() {
  if (!form.name.trim()) {
    ElMessage.warning('请输入圈子名称')
    return
  }
  submitting.value = true
  try {
    await circleApi.create({ ...form, name: form.name.trim() })
    ElMessage.success('圈子已提交审核，通过后会出现在圈子广场')
    createVisible.value = false
    Object.assign(form, { name: '', description: '', cover: '' })
  } finally {
    submitting.value = false
  }
}

onMounted(() => load(1))
</script>

<style scoped>
.circles-hero { overflow: hidden; background-image: linear-gradient(90deg, rgba(26, 34, 30, .84), rgba(26, 34, 30, .28)), url('/images/jiangnan/xitang-dusk-2.jpg'); background-size: cover; background-position: center; }
.circles-hero::after { content: ''; position: absolute; inset: 0; background: linear-gradient(110deg, transparent 46%, rgba(239, 184, 74, .13)); pointer-events: none; }
.circle-card { padding: 0; border-radius: 22px; perspective: 900px; transform-style: preserve-3d; box-shadow: 0 12px 32px rgba(31, 47, 40, .08); animation: card-in .65s cubic-bezier(.2,.8,.2,1) both; }
.circle-card:nth-child(2n) { animation-delay: .08s; }
.circle-card:nth-child(3n) { animation-delay: .16s; }
.circle-card:hover { transform: perspective(900px) rotateX(1.4deg) rotateY(-2deg) translateY(-7px); box-shadow: 0 24px 55px rgba(28, 48, 39, .2); }
.circle-cover { position: relative; height: 190px; overflow: hidden; }
.circle-cover img { width: 100%; height: 100%; object-fit: cover; transform: scale(1.02); transition: transform .6s cubic-bezier(.2,.8,.2,1); }
.circle-card:hover .circle-cover img { transform: scale(1.1) translateZ(8px); }
.cover-shade { position: absolute; inset: 0; background: linear-gradient(180deg, transparent 28%, rgba(19, 32, 27, .7)); }
.circle-icon { position: absolute; left: 18px; bottom: 15px; width: 48px; height: 48px; display: grid; place-items: center; border-radius: 15px; background: rgba(255, 253, 249, .88); backdrop-filter: blur(12px); font-size: 24px; box-shadow: 0 9px 24px rgba(12, 30, 24, .25); }
.circle-card-body { padding: 20px 22px 22px; }
.circle-card-body h3 { margin-bottom: 8px; }
.circle-card-body p { min-height: 40px; }
.page-pagination { display: flex; justify-content: center; margin-top: 28px; }
@keyframes card-in { from { opacity: 0; transform: translateY(24px) scale(.97); } to { opacity: 1; transform: translateY(0) scale(1); } }
@media (max-width: 760px) { .page-hero-3d { display: block; width: 78%; height: 240px; right: -14%; top: auto; bottom: -20px; opacity: .52; } }
</style>


