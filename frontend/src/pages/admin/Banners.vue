<template>
  <section class="admin-panel">
    <div class="admin-panel-header"><h2>首页轮播图</h2><el-button type="primary" @click="openCreate"><el-icon><Plus /></el-icon> 新增轮播图</el-button></div>
    <el-table v-loading="loading" :data="rows" class="admin-table">
      <el-table-column label="预览" width="150"><template #default="{ row }"><img :src="row.imageUrl" class="banner-thumb" /></template></el-table-column>
      <el-table-column prop="title" label="标题" min-width="220" />
      <el-table-column prop="linkUrl" label="跳转地址" min-width="180" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="显示" width="100"><template #default="{ row }"><el-switch :model-value="row.status === 1" @change="(value) => toggleStatus(row, value)" /></template></el-table-column>
      <el-table-column label="操作" width="160" fixed="right"><template #default="{ row }"><el-button text @click="openEdit(row)">编辑</el-button><el-button text type="danger" @click="remove(row)">删除</el-button></template></el-table-column>
    </el-table>
    <div class="admin-pagination"><el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="load" /></div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑轮播图' : '新增轮播图'" width="560px">
      <el-form label-position="top">
        <el-form-item label="标题"><el-input v-model="form.title" maxlength="100" /></el-form-item>
        <el-form-item label="图片地址"><el-input v-model="form.imageUrl" placeholder="https://..." /></el-form-item>
        <el-form-item label="跳转地址"><el-input v-model="form.linkUrl" placeholder="/circles 或 https://..." /></el-form-item>
        <div class="form-row"><el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item><el-form-item label="显示状态"><el-switch v-model="form.enabled" /></el-form-item></div>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { bannerApi } from '../../api'

const rows = ref([])
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const form = reactive({ id: null, title: '', imageUrl: '', linkUrl: '', sort: 0, enabled: true })

async function load(target = page.value) {
  loading.value = true
  page.value = target
  try {
    const data = await bannerApi.adminList({ page: page.value, size: pageSize })
    rows.value = data.records || []
    total.value = data.total || 0
  } finally { loading.value = false }
}

function resetForm() { Object.assign(form, { id: null, title: '', imageUrl: '', linkUrl: '', sort: 0, enabled: true }) }
function openCreate() { resetForm(); dialogVisible.value = true }
function openEdit(row) { Object.assign(form, { ...row, enabled: row.status === 1 }); dialogVisible.value = true }

async function save() {
  if (!form.title.trim() || !form.imageUrl.trim()) return ElMessage.warning('请填写标题和图片地址')
  saving.value = true
  const payload = { title: form.title.trim(), imageUrl: form.imageUrl.trim(), linkUrl: form.linkUrl, sort: form.sort, status: form.enabled ? 1 : 0 }
  try {
    if (form.id) await bannerApi.update(form.id, payload)
    else await bannerApi.create(payload)
    ElMessage.success('轮播图已保存')
    dialogVisible.value = false
    load()
  } finally { saving.value = false }
}

async function toggleStatus(row, enabled) {
  const payload = { title: row.title, imageUrl: row.imageUrl, linkUrl: row.linkUrl, sort: row.sort, status: enabled ? 1 : 0 }
  await bannerApi.update(row.id, payload)
  row.status = payload.status
  ElMessage.success('显示状态已更新')
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除轮播图「${row.title}」吗？`, '删除轮播图', { type: 'warning' })
  await bannerApi.remove(row.id)
  ElMessage.success('轮播图已删除')
  load()
}

onMounted(() => load(1))
</script>

<style scoped>
.banner-thumb { width: 120px; height: 60px; object-fit: cover; border-radius: 8px; background: #eee; }
.form-row { display: flex; gap: 30px; align-items: center; }
</style>
