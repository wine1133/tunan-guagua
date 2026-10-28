<template>
  <div class="page-container">
    <section class="page-hero topics-hero">
      <div class="eyebrow">TRENDING TOPICS</div>
      <h1>每个江南话题，<br />都是一次同频相遇。</h1>
      <p>参与正在发生的讨论，让你的观点被更多人听见。热度来自真实互动，也来自每一份认真表达。</p>
    </section>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="keyword" style="width: 280px" size="large" clearable placeholder="搜索话题" prefix-icon="Search" @keyup.enter="load(1)" @clear="load(1)" />
        <el-button size="large" @click="load(1)">搜索</el-button>
      </div>
      <el-button type="primary" size="large" @click="openCreate"><el-icon><Plus /></el-icon> 发起话题</el-button>
    </div>
    <div v-loading="loading" class="topic-grid">
      <article v-for="(topic, index) in topics" :key="topic.id" class="topic-card jiangnan-card" @click="router.push(`/topics/${topic.id}`)">
        <div class="topic-cover">
          <img :src="topic.cover || '/images/jiangnan/tuisi-garden.jpg'" :alt="topic.name" loading="lazy" />
          <div class="topic-cover-shade" />
          <div class="topic-top"><span class="topic-rank">TOP {{ String(index + 1).padStart(2, '0') }}</span><span class="topic-heat">🔥 {{ topic.heat }}</span></div>
        </div>
        <div class="topic-body">
          <h3># {{ topic.name }}</h3>
          <p>{{ topic.description || '这个话题正在等待更多讨论。' }}</p>
          <div class="heat-bar"><span :style="{ width: `${Math.min(100, 22 + index * 16)}%` }" /></div>
          <div class="card-meta"><span>{{ topic.postCount }} 条相关分享</span><span>参与讨论 →</span></div>
        </div>
      </article>
    </div>
    <EmptyState v-if="!loading && !topics.length" icon="💬" title="这个话题还没被提起" description="来发起一个新话题，开启第一场讨论。" />
    <div v-if="total > pageSize" class="page-pagination"><el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="load" /></div>

    <el-dialog v-model="createVisible" title="发起一个新话题" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item label="话题名称"><el-input v-model="form.name" maxlength="100" placeholder="例如：兔南的秋天" /></el-form-item>
        <el-form-item label="话题介绍"><el-input v-model="form.description" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="希望大家围绕什么展开讨论？" /></el-form-item>
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
import { topicApi } from '../api'
import { authState } from '../stores/auth'
import EmptyState from '../components/EmptyState.vue'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const topics = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = 12
const total = ref(0)
const createVisible = ref(false)
const form = reactive({ name: '', description: '', cover: '' })

async function load(target = page.value) {
  loading.value = true
  page.value = target
  try {
    const data = await topicApi.list({ page: page.value, size: pageSize, keyword: keyword.value || undefined })
    topics.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function openCreate() {
  if (!authState.token) {
    router.push({ name: 'login', query: { redirect: '/topics' } })
    return
  }
  createVisible.value = true
}

async function submitCreate() {
  if (!form.name.trim()) { ElMessage.warning('请输入话题名称'); return }
  submitting.value = true
  try {
    await topicApi.create({ ...form, name: form.name.trim() })
    ElMessage.success('话题已提交审核，通过后即可参与讨论')
    createVisible.value = false
    Object.assign(form, { name: '', description: '', cover: '' })
  } finally {
    submitting.value = false
  }
}

onMounted(() => load(1))
</script>

<style scoped>
.topics-hero { overflow: hidden; background-image: linear-gradient(90deg, rgba(20, 31, 42, .88), rgba(20, 31, 42, .24)), url('/images/jiangnan/wuzhen-aerial.jpg'); background-size: cover; background-position: center; }
.topics-hero::after { content: ''; position: absolute; inset: 0; background: radial-gradient(circle at 72% 4%, rgba(120, 184, 176, .24), transparent 36%); pointer-events: none; }
.topic-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 18px; }
.topic-card { padding: 0; overflow: hidden; background: #fffdf9; border-radius: 22px; perspective: 900px; transform-style: preserve-3d; animation: topic-in .65s cubic-bezier(.2,.8,.2,1) both; }
.topic-card:nth-child(2n) { animation-delay: .08s; }
.topic-card:nth-child(3n) { animation-delay: .16s; }
.topic-card:hover { transform: perspective(900px) rotateX(1.4deg) rotateY(2deg) translateY(-7px); box-shadow: 0 24px 55px rgba(26, 45, 54, .2); }
.topic-cover { position: relative; height: 180px; overflow: hidden; }
.topic-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform .6s cubic-bezier(.2,.8,.2,1); }
.topic-card:hover .topic-cover img { transform: scale(1.1); }
.topic-cover-shade { position: absolute; inset: 0; background: linear-gradient(180deg, rgba(13, 26, 32, .12), rgba(13, 26, 32, .68)); }
.topic-top { position: absolute; inset: 14px 15px auto; display: flex; justify-content: space-between; align-items: center; }
.topic-rank { color: #ffd27d; text-shadow: 0 2px 10px rgba(0,0,0,.35); }
.topic-heat { color: rgba(255,255,255,.86); font-size: 11px; }
.topic-body { padding: 20px 22px 22px; }
.topic-card .card-meta { margin-top: 14px; justify-content: space-between; }
.page-pagination { display: flex; justify-content: center; margin-top: 28px; }
@keyframes topic-in { from { opacity: 0; transform: translateY(24px) scale(.97); } to { opacity: 1; transform: translateY(0) scale(1); } }
@media (max-width: 1000px) { .topic-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 680px) { .topic-grid { grid-template-columns: 1fr; } .page-hero-3d { display: block; width: 78%; height: 230px; right: -14%; top: auto; bottom: -22px; opacity: .48; } }
</style>


