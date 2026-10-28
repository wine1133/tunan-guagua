<template>
  <el-dialog v-model="visible" title="分享兔南此刻" width="620px" class="publish-dialog" destroy-on-close>
    <div class="publish-tip">真实、友善地分享，帖子将在管理员审核后公开。</div>
    <el-form :model="form" label-position="top">
      <el-form-item label="标题">
        <el-input v-model="form.title" maxlength="160" show-word-limit placeholder="用一句话概括你的分享" />
      </el-form-item>
      <el-form-item label="正文">
        <el-input v-model="form.content" type="textarea" :rows="6" maxlength="5000" show-word-limit placeholder="记录一段经历、一个问题，或一份同城好心情..." />
      </el-form-item>
      <div class="publish-grid">
        <el-form-item label="关联圈子">
          <el-select v-model="form.circleId" clearable placeholder="选择一个圈子">
            <el-option v-for="item in circles" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联话题">
          <el-select v-model="form.topicId" clearable placeholder="选择一个话题">
            <el-option v-for="item in topics" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </div>
      <el-form-item label="图片地址（每行一个，可选）">
        <el-input v-model="form.imagesText" type="textarea" :rows="3" placeholder="https://example.com/photo.jpg" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit">发布帖子</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { circleApi, postApi, topicApi } from '../api'
import { authState } from '../stores/auth'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  defaultCircleId: { type: [Number, String], default: null },
  defaultTopicId: { type: [Number, String], default: null }
})
const emit = defineEmits(['update:modelValue', 'success'])
const router = useRouter()
const visible = ref(props.modelValue)
const submitting = ref(false)
const circles = ref([])
const topics = ref([])
const form = reactive({ title: '', content: '', circleId: null, topicId: null, imagesText: '' })

watch(() => props.modelValue, (value) => {
  visible.value = value
  if (value) {
    form.circleId = props.defaultCircleId ? Number(props.defaultCircleId) : null
    form.topicId = props.defaultTopicId ? Number(props.defaultTopicId) : null
    loadOptions()
  }
})
watch(visible, (value) => emit('update:modelValue', value))
watch(() => props.defaultCircleId, (value) => { if (value) form.circleId = Number(value) })
watch(() => props.defaultTopicId, (value) => { if (value) form.topicId = Number(value) })

async function loadOptions() {
  if (circles.value.length && topics.value.length) return
  try {
    const [circlePage, topicPage] = await Promise.all([
      circleApi.list({ page: 1, size: 100 }),
      topicApi.list({ page: 1, size: 100 })
    ])
    circles.value = circlePage.records || []
    topics.value = topicPage.records || []
  } catch {
    // 错误提示由全局拦截器处理
  }
}

async function submit() {
  if (!authState.token) {
    ElMessage.warning('请先登录')
    router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
    return
  }
  if (!form.title.trim() || !form.content.trim()) {
    ElMessage.warning('请填写标题和正文')
    return
  }
  submitting.value = true
  try {
    const post = await postApi.create({
      title: form.title.trim(),
      content: form.content.trim(),
      circleId: form.circleId || null,
      topicId: form.topicId || null,
      images: form.imagesText.split('\n').map((item) => item.trim()).filter(Boolean)
    })
    ElMessage.success('发布成功，等待管理员审核')
    Object.assign(form, { title: '', content: '', circleId: null, topicId: null, imagesText: '' })
    visible.value = false
    emit('success', post)
    router.push(`/posts/${post.id}`)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.publish-tip { margin: -4px 0 18px; padding: 10px 14px; border-radius: 10px; background: #f5f1e8; color: #6e746f; font-size: 13px; }
.publish-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.publish-grid :deep(.el-select) { width: 100%; }
:deep(.el-dialog) { border-radius: 22px; }
@media (max-width: 640px) { .publish-grid { grid-template-columns: 1fr; gap: 0; } }
</style>
