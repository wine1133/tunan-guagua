<template>
  <section class="admin-panel">
    <div class="admin-panel-header">
      <h2>话题审核与维护</h2>
      <div class="admin-filters">
        <el-input v-model="keyword" clearable placeholder="搜索话题" style="width: 220px" @keyup.enter="load(1)" @clear="load(1)" />
        <el-select v-model="auditStatus" clearable placeholder="审核状态" style="width: 130px" @change="load(1)">
          <el-option label="待审核" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已驳回" :value="2" />
        </el-select>
        <el-button type="primary" @click="load(1)">查询</el-button>
      </div>
    </div>
    <el-table v-loading="loading" :data="rows" class="admin-table">
      <el-table-column prop="id" label="ID" width="65" />
      <el-table-column label="话题" min-width="200"><template #default="{ row }"><strong class="topic-name"># {{ row.name }}</strong></template></el-table-column>
      <el-table-column prop="description" label="简介" min-width="260" show-overflow-tooltip />
      <el-table-column label="热度" width="110"><template #default="{ row }">🔥 {{ row.heat }}</template></el-table-column>
      <el-table-column label="审核" width="100"><template #default="{ row }"><el-tag :type="auditType(row.auditStatus)" size="small">{{ auditText(row.auditStatus) }}</el-tag></template></el-table-column>
      <el-table-column label="创建时间" width="130"><template #default="{ row }">{{ formatTime(row.createdAt) }}</template></el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button text size="small" @click="router.push(`/topics/${row.id}`)">查看</el-button>
          <el-button v-if="row.auditStatus !== 1" text type="success" size="small" @click="audit(row, 1)">通过</el-button>
          <el-button v-if="row.auditStatus !== 2" text type="danger" size="small" @click="audit(row, 2)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="admin-pagination"><el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="load" /></div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { topicApi } from '../../api'
import { auditText, auditType, formatTime } from '../../utils/format'

const router = useRouter()
const rows = ref([])
const keyword = ref('')
const auditStatus = ref(null)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

async function load(target = page.value) {
  loading.value = true
  page.value = target
  try {
    const data = await topicApi.adminList({ page: page.value, size: pageSize, keyword: keyword.value || undefined, auditStatus: auditStatus.value ?? undefined })
    rows.value = data.records || []
    total.value = data.total || 0
  } finally { loading.value = false }
}

async function audit(row, status) {
  await ElMessageBox.confirm(`确定${status === 1 ? '通过' : '驳回'}话题「${row.name}」吗？`, '内容审核', { type: 'warning' })
  await topicApi.audit(row.id, status)
  row.auditStatus = status
  ElMessage.success('审核状态已更新')
}

onMounted(() => load(1))
</script>

<style scoped>
.topic-name { color: var(--teal); font-size: 13px; }
</style>
