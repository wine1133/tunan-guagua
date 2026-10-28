<template>
  <section class="admin-panel">
    <div class="admin-panel-header">
      <h2>帖子审核与维护</h2>
      <div class="admin-filters">
        <el-input v-model="keyword" clearable placeholder="搜索帖子标题或正文" style="width: 230px" @keyup.enter="load(1)" @clear="load(1)" />
        <el-select v-model="auditStatus" clearable placeholder="审核状态" style="width: 130px" @change="load(1)"><el-option label="待审核" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已驳回" :value="2" /></el-select>
        <el-button type="primary" @click="load(1)">查询</el-button>
      </div>
    </div>
    <el-table v-loading="loading" :data="rows" class="admin-table">
      <el-table-column prop="id" label="ID" width="65" />
      <el-table-column label="帖子" min-width="250"><template #default="{ row }"><strong class="post-title" @click="preview(row)">{{ row.title }}</strong><div class="post-sub">{{ row.circleName || '未关联圈子' }} <span v-if="row.topicName">· #{{ row.topicName }}</span></div></template></el-table-column>
      <el-table-column prop="authorName" label="作者" width="100" />
      <el-table-column label="互动" width="170"><template #default="{ row }">👍 {{ row.likeCount }} · ⭐ {{ row.favoriteCount }} · 💬 {{ row.commentCount }}</template></el-table-column>
      <el-table-column label="审核" width="100"><template #default="{ row }"><el-tag :type="auditType(row.auditStatus)" size="small">{{ auditText(row.auditStatus) }}</el-tag></template></el-table-column>
      <el-table-column label="发布时间" width="130"><template #default="{ row }">{{ formatTime(row.createdAt) }}</template></el-table-column>
      <el-table-column label="操作" width="245" fixed="right">
        <template #default="{ row }">
          <el-button text size="small" @click="preview(row)">预览</el-button>
          <el-button v-if="row.auditStatus !== 1" text type="success" size="small" @click="audit(row, 1)">通过</el-button>
          <el-button v-if="row.auditStatus !== 2" text type="danger" size="small" @click="audit(row, 2)">驳回</el-button>
          <el-button text type="danger" size="small" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="admin-pagination"><el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="load" /></div>

    <el-dialog v-model="previewVisible" title="帖子预览" width="700px">
      <div v-if="current" class="preview-content">
        <el-tag :type="auditType(current.auditStatus)" size="small">{{ auditText(current.auditStatus) }}</el-tag>
        <h1>{{ current.title }}</h1>
        <div class="preview-meta">{{ current.authorName }} · {{ formatTime(current.createdAt) }} · {{ current.viewCount }} 阅读</div>
        <div class="preview-text">{{ current.content }}</div>
        <img v-for="image in current.images" :key="image" :src="image" class="preview-image" />
      </div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
        <el-button type="danger" plain @click="audit(current, 2)">驳回</el-button>
        <el-button type="success" @click="audit(current, 1)">审核通过</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { postApi } from '../../api'
import { auditText, auditType, formatTime } from '../../utils/format'

const rows = ref([])
const keyword = ref('')
const auditStatus = ref(null)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)
const previewVisible = ref(false)
const current = ref(null)

async function load(target = page.value) {
  loading.value = true
  page.value = target
  try {
    const data = await postApi.adminList({ page: page.value, size: pageSize, keyword: keyword.value || undefined, auditStatus: auditStatus.value ?? undefined })
    rows.value = data.records || []
    total.value = data.total || 0
  } finally { loading.value = false }
}

async function preview(row) {
  current.value = await postApi.adminDetail(row.id)
  previewVisible.value = true
}

async function audit(row, status) {
  if (!row) return
  await ElMessageBox.confirm(`确定${status === 1 ? '通过' : '驳回'}帖子「${row.title}」吗？`, '帖子审核', { type: 'warning' })
  await postApi.audit(row.id, status)
  row.auditStatus = status
  previewVisible.value = false
  ElMessage.success('审核状态已更新')
  load()
}

async function remove(row) {
  await ElMessageBox.confirm(`删除帖子「${row.title}」后不可恢复，确定继续吗？`, '删除帖子', { type: 'warning' })
  await postApi.adminRemove(row.id)
  ElMessage.success('帖子已删除')
  load()
}

onMounted(() => load(1))
</script>

<style scoped>
.post-title { cursor: pointer; font-size: 13px; }
.post-title:hover { color: var(--coral); }
.post-sub { color: var(--muted); font-size: 10px; margin-top: 4px; }
.preview-content h1 { font-size: 27px; margin: 14px 0 8px; }
.preview-meta { color: var(--muted); font-size: 11px; margin-bottom: 22px; }
.preview-text { white-space: pre-wrap; line-height: 1.9; color: #3f4c46; font-size: 14px; }
.preview-image { width: 100%; border-radius: 12px; margin-top: 16px; }
</style>
