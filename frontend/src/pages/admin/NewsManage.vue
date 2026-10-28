<template>
  <section class="admin-panel">
    <div class="admin-panel-header">
      <h2>同城资讯管理</h2>
      <div class="admin-filters"><el-input v-model="keyword" clearable placeholder="搜索资讯标题" style="width: 250px" @keyup.enter="load(1)" @clear="load(1)" /><el-button type="primary" @click="openCreate"><el-icon><Plus /></el-icon> 发布资讯</el-button></div>
    </div>
    <el-table v-loading="loading" :data="rows" class="admin-table">
      <el-table-column label="封面" width="120"><template #default="{ row }"><img :src="row.cover" class="news-thumb" /></template></el-table-column>
      <el-table-column prop="title" label="标题" min-width="300" />
      <el-table-column prop="city" label="城市" width="90" />
      <el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag></template></el-table-column>
      <el-table-column prop="viewCount" label="阅读" width="90" />
      <el-table-column label="发布时间" width="130"><template #default="{ row }">{{ formatTime(row.createdAt) }}</template></el-table-column>
      <el-table-column label="操作" width="160" fixed="right"><template #default="{ row }"><el-button text @click="openEdit(row)">编辑</el-button><el-button text type="danger" @click="remove(row)">删除</el-button></template></el-table-column>
    </el-table>
    <div class="admin-pagination"><el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="load" /></div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑资讯' : '发布资讯'" width="720px">
      <el-form label-position="top">
        <el-form-item label="标题"><el-input v-model="form.title" maxlength="200" show-word-limit /></el-form-item>
        <div class="form-row"><el-form-item label="城市"><el-input v-model="form.city" /></el-form-item><el-form-item label="状态"><el-select v-model="form.status"><el-option label="草稿" :value="0" /><el-option label="发布" :value="1" /></el-select></el-form-item></div>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" maxlength="500" show-word-limit /></el-form-item>
        <el-form-item label="封面图片地址"><el-input v-model="form.cover" placeholder="https://..." /></el-form-item>
        <el-form-item label="正文"><el-input v-model="form.content" type="textarea" :rows="10" placeholder="支持换行输入" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存资讯</el-button></template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { newsApi } from '../../api'
import { formatTime } from '../../utils/format'

const rows = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const form = reactive({ id: null, title: '', summary: '', content: '', cover: '', city: '兔南', status: 1 })

async function load(target = page.value) {
  loading.value = true
  page.value = target
  try {
    const data = await newsApi.adminList({ page: page.value, size: pageSize, keyword: keyword.value || undefined })
    rows.value = data.records || []
    total.value = data.total || 0
  } finally { loading.value = false }
}

function resetForm() { Object.assign(form, { id: null, title: '', summary: '', content: '', cover: '', city: '兔南', status: 1 }) }
function openCreate() { resetForm(); dialogVisible.value = true }
function openEdit(row) { Object.assign(form, row); dialogVisible.value = true }

async function save() {
  if (!form.title.trim() || !form.content.trim()) return ElMessage.warning('请填写标题和正文')
  saving.value = true
  const payload = { title: form.title.trim(), summary: form.summary, content: form.content, cover: form.cover, city: form.city, status: form.status }
  try {
    if (form.id) await newsApi.update(form.id, payload)
    else await newsApi.create(payload)
    ElMessage.success('资讯已保存')
    dialogVisible.value = false
    load()
  } finally { saving.value = false }
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除资讯「${row.title}」吗？`, '删除资讯', { type: 'warning' })
  await newsApi.remove(row.id)
  ElMessage.success('资讯已删除')
  load()
}

onMounted(() => load(1))
</script>

<style scoped>
.news-thumb { width: 88px; height: 54px; object-fit: cover; border-radius: 7px; background: #eee; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 18px; }
</style>
