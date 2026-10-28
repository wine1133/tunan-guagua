<template>
  <section class="admin-panel">
    <div class="admin-panel-header"><h2>点赞与收藏记录</h2><el-tag effect="plain" round>实时社区互动</el-tag></div>
    <el-tabs v-model="activeTab" @tab-change="load">
      <el-tab-pane label="点赞记录" name="likes" />
      <el-tab-pane label="收藏记录" name="favorites" />
    </el-tabs>
    <el-table v-loading="loading" :data="rows" class="admin-table">
      <el-table-column prop="id" label="记录 ID" width="100" />
      <el-table-column prop="userName" label="用户" width="150" />
      <el-table-column prop="postTitle" label="帖子" min-width="280" show-overflow-tooltip />
      <el-table-column label="行为" width="110"><template #default><el-tag :type="activeTab === 'likes' ? 'danger' : 'warning'" effect="plain" size="small">{{ activeTab === 'likes' ? '点赞' : '收藏' }}</el-tag></template></el-table-column>
      <el-table-column label="时间" width="150"><template #default="{ row }">{{ formatTime(row.createdAt) }}</template></el-table-column>
    </el-table>
    <div class="admin-pagination"><el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page="page" @current-change="load" /></div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { interactionApi } from '../../api'
import { formatTime } from '../../utils/format'

const activeTab = ref('likes')
const rows = ref([])
const page = ref(1)
const pageSize = 10
const total = ref(0)
const loading = ref(false)

async function load(target = 1) {
  loading.value = true
  page.value = target
  try {
    const api = activeTab.value === 'likes' ? interactionApi.adminLikes : interactionApi.adminFavorites
    const data = await api({ page: page.value, size: pageSize })
    rows.value = data.records || []
    total.value = data.total || 0
  } finally { loading.value = false }
}

onMounted(() => load(1))
</script>
