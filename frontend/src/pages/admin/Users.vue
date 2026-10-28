<template>
  <section class="admin-panel">
    <div class="admin-panel-header">
      <h2>用户列表</h2>
      <div class="admin-filters">
        <el-input v-model="keyword" clearable placeholder="搜索账号或昵称" style="width: 230px" @keyup.enter="load(1)" @clear="load(1)" />
        <el-select v-model="status" clearable placeholder="账号状态" style="width: 130px" @change="load(1)">
          <el-option label="正常" :value="1" /><el-option label="已停用" :value="0" />
        </el-select>
        <el-button type="primary" @click="load(1)">查询</el-button>
      </div>
    </div>
    <el-table v-loading="loading" :data="rows" class="admin-table">
      <el-table-column label="用户" min-width="190">
        <template #default="{ row }"><div class="user-cell"><UserAvatar :name="row.nickname" :src="row.avatar" :size="36" /><div><strong>{{ row.nickname }}</strong><span>@{{ row.username }}</span></div></div></template>
      </el-table-column>
      <el-table-column prop="city" label="城市" width="100" />
      <el-table-column label="角色" width="100"><template #default="{ row }"><el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" effect="plain" size="small">{{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}</el-tag></template></el-table-column>
      <el-table-column prop="bio" label="简介" min-width="220" show-overflow-tooltip />
      <el-table-column label="注册时间" width="140"><template #default="{ row }">{{ formatTime(row.createdAt) }}</template></el-table-column>
      <el-table-column label="账号状态" width="130" fixed="right"><template #default="{ row }"><el-switch :model-value="row.status === 1" :disabled="row.role === 'ADMIN'" @change="(value) => changeStatus(row, value)" /></template></el-table-column>
    </el-table>
    <div class="admin-pagination"><el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="load" /></div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '../../api'
import { formatTime } from '../../utils/format'
import UserAvatar from '../../components/UserAvatar.vue'

const rows = ref([])
const keyword = ref('')
const status = ref(null)
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

async function load(target = page.value) {
  loading.value = true
  page.value = target
  try {
    const data = await userApi.adminList({ page: page.value, size: pageSize, keyword: keyword.value || undefined, status: status.value ?? undefined })
    rows.value = data.records || []
    total.value = data.total || 0
  } finally { loading.value = false }
}

async function changeStatus(row, enabled) {
  const nextStatus = enabled ? 1 : 0
  await ElMessageBox.confirm(`确定${enabled ? '启用' : '停用'}用户「${row.nickname}」吗？`, '账号状态', { type: 'warning' })
  await userApi.updateStatus(row.id, nextStatus)
  row.status = nextStatus
  ElMessage.success('账号状态已更新')
}

onMounted(() => load(1))
</script>

<style scoped>
.user-cell { display: flex; align-items: center; gap: 9px; }
.user-cell div { display: flex; flex-direction: column; }
.user-cell strong { font-size: 13px; }
.user-cell span { color: var(--muted); font-size: 10px; margin-top: 2px; }
</style>
