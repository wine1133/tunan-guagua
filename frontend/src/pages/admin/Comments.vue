<template>
  <section class="admin-panel">
    <div class="admin-panel-header">
      <h2>评论管理</h2>
      <div class="admin-filters"><el-input v-model="keyword" clearable placeholder="搜索评论内容" style="width: 260px" @keyup.enter="load(1)" @clear="load(1)" /><el-button type="primary" @click="load(1)">查询</el-button></div>
    </div>
    <el-table v-loading="loading" :data="rows" class="admin-table">
      <el-table-column prop="id" label="ID" width="65" />
      <el-table-column label="评论用户" width="130"><template #default="{ row }">{{ row.userName }}</template></el-table-column>
      <el-table-column prop="content" label="评论内容" min-width="260" show-overflow-tooltip />
      <el-table-column prop="postTitle" label="所属帖子" min-width="220" show-overflow-tooltip />
      <el-table-column label="类型" width="90"><template #default="{ row }">{{ row.parentId ? '回复' : '评论' }}</template></el-table-column>
      <el-table-column label="时间" width="130"><template #default="{ row }">{{ formatTime(row.createdAt) }}</template></el-table-column>
      <el-table-column label="操作" width="100" fixed="right"><template #default="{ row }"><el-button text type="danger" size="small" @click="remove(row)">删除</el-button></template></el-table-column>
    </el-table>
    <div class="admin-pagination"><el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="load" /></div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { commentApi } from '../../api'
import { formatTime } from '../../utils/format'

const rows = ref([])
const keyword = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

async function load(target = page.value) {
  loading.value = true
  page.value = target
  try {
    const data = await commentApi.adminList({ page: page.value, size: pageSize, keyword: keyword.value || undefined })
    rows.value = data.records || []
    total.value = data.total || 0
  } finally { loading.value = false }
}

async function remove(row) {
  await ElMessageBox.confirm('确定删除这条评论吗？', '删除评论', { type: 'warning' })
  await commentApi.adminRemove(row.id)
  ElMessage.success('评论已删除')
  load()
}

onMounted(() => load(1))
</script>
