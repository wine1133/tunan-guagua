<template>
  <div v-if="post" class="page-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/posts' }">同城动态</el-breadcrumb-item>
      <el-breadcrumb-item>{{ post.circleName || '社区分享' }}</el-breadcrumb-item>
      <el-breadcrumb-item>正文</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="detail-layout">
      <main>
        <article class="detail-card">
          <div v-if="post.auditStatus !== 1" class="audit-banner">
            当前帖子状态：{{ auditText(post.auditStatus) }}，仅自己和管理员可见。
          </div>
          <div class="eyebrow">TUNAN MOMENT · {{ post.circleName || '同城广场' }}</div>
          <h1>{{ post.title }}</h1>
          <div class="detail-meta">
            <UserAvatar :name="post.authorName" :src="post.authorAvatar" :size="46" />
            <div class="detail-author"><strong>{{ post.authorName }}</strong><span>{{ formatTime(post.createdAt) }} · {{ post.viewCount }} 次浏览</span></div>
            <el-button v-if="!isSelf" round size="small" :type="following ? 'default' : 'primary'" @click="toggleFollow">
              {{ following ? '已关注' : '关注作者' }}
            </el-button>
            <el-dropdown v-if="canManage" trigger="click">
              <el-button text><el-icon><MoreFilled /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="openEdit">编辑帖子</el-dropdown-item>
                  <el-dropdown-item divided @click="removePost">删除帖子</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div class="tag-row">
            <el-tag v-if="post.circleName" effect="plain">◎ {{ post.circleName }}</el-tag>
            <el-tag v-if="post.topicName" effect="plain" type="warning"># {{ post.topicName }}</el-tag>
          </div>
          <div class="prose">{{ post.content }}</div>
          <div v-if="post.images?.length" class="detail-images">
            <img v-for="(image, index) in post.images" :key="image" :src="image" :alt="`${post.title}-${index + 1}`" loading="lazy" />
          </div>
          <div class="action-row">
            <button class="action-btn" :class="{ active: post.liked }" @click="toggleLike"><el-icon><Pointer /></el-icon> {{ post.liked ? '已点赞' : '点赞' }} {{ post.likeCount }}</button>
            <button class="action-btn" :class="{ active: post.favorited }" @click="toggleFavorite"><el-icon><Star /></el-icon> {{ post.favorited ? '已收藏' : '收藏' }} {{ post.favoriteCount }}</button>
            <button class="action-btn" @click="focusComment"><el-icon><ChatDotRound /></el-icon> 评论 {{ post.commentCount }}</button>
          </div>
        </article>

        <section v-if="post.auditStatus === 1" class="comment-area">
          <h2>一起聊聊 <span class="comment-count">{{ comments.length }}</span></h2>
          <div class="comment-form">
            <UserAvatar :name="authState.user?.nickname || '访客'" :src="authState.user?.avatar" :size="38" />
            <div style="flex: 1">
              <div v-if="replyTarget" class="reply-hint">正在回复 {{ replyTarget.userName }} <button @click="replyTarget = null">取消</button></div>
              <el-input ref="commentInput" v-model="commentContent" type="textarea" :rows="3" maxlength="1000" show-word-limit :placeholder="authState.token ? '写下你的想法，友善交流…' : '登录后参与评论'" />
              <div class="comment-submit"><el-button type="primary" :loading="commentSubmitting" @click="submitComment">发表评论</el-button></div>
            </div>
          </div>
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <UserAvatar :name="comment.userName" :src="comment.userAvatar" :size="36" />
            <div class="comment-body">
              <header><strong>{{ comment.userName }}</strong><span>{{ formatTime(comment.createdAt) }}</span><span v-if="comment.parentId" class="reply-tag">回复评论</span></header>
              <p>{{ comment.content }}</p>
              <div class="comment-actions">
                <button @click="setReply(comment)">回复</button>
                <button v-if="canDeleteComment(comment)" @click="deleteComment(comment)">删除</button>
              </div>
            </div>
          </div>
          <EmptyState v-if="!comments.length" icon="💬" title="还没有评论" description="说点什么，开启这场同城对话。" />
        </section>
      </main>

      <aside>
        <div v-if="circle" class="side-card circle-side">
          <div class="circle-side-icon">◎</div>
          <h3>{{ circle.name }}</h3>
          <p>{{ circle.description }}</p>
          <el-button text type="primary" @click="router.push(`/circles/${circle.id}`)">进入圈子 →</el-button>
        </div>
        <div class="side-card">
          <h3>分享守则</h3>
          <p class="side-muted">尊重每一种生活方式，不发布隐私和攻击性内容。让兔南社区始终温暖、真实、有边界。</p>
        </div>
        <div class="side-card">
          <h3>相关动态</h3>
          <div v-for="item in related" :key="item.id" class="related-post" @click="openPost(item.id)">
            <strong>{{ item.title }}</strong><span>{{ item.authorName }} · {{ formatTime(item.createdAt) }}</span>
          </div>
        </div>
      </aside>
    </div>

    <el-dialog v-model="editVisible" title="编辑帖子" width="620px">
      <div class="edit-tip">更新操作由 Redisson 分布式锁保护，同一帖子同一时间只允许一个更新任务。</div>
      <el-form label-position="top">
        <el-form-item label="标题"><el-input v-model="editForm.title" maxlength="160" show-word-limit /></el-form-item>
        <el-form-item label="正文"><el-input v-model="editForm.content" type="textarea" :rows="7" maxlength="5000" show-word-limit /></el-form-item>
        <el-form-item label="图片地址（每行一个）"><el-input v-model="editForm.imagesText" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="editVisible = false">取消</el-button><el-button type="primary" :loading="editSubmitting" @click="submitEdit">保存修改</el-button></template>
    </el-dialog>
  </div>
  <div v-else v-loading="true" class="page-container loading-holder" />
</template>

<script setup>
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { circleApi, commentApi, followApi, interactionApi, postApi } from '../api'
import { authState } from '../stores/auth'
import { auditText, formatTime } from '../utils/format'
import UserAvatar from '../components/UserAvatar.vue'
import EmptyState from '../components/EmptyState.vue'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const comments = ref([])
const related = ref([])
const circle = ref(null)
const following = ref(false)
const commentContent = ref('')
const commentSubmitting = ref(false)
const replyTarget = ref(null)
const commentInput = ref(null)
const editVisible = ref(false)
const editSubmitting = ref(false)
const editForm = reactive({ title: '', content: '', imagesText: '' })

const isSelf = computed(() => authState.user?.id === post.value?.authorId)
const canManage = computed(() => isSelf.value || authState.user?.role === 'ADMIN')

function canDeleteComment(comment) {
  return authState.user?.role === 'ADMIN' || authState.user?.id === comment.userId
}

async function load() {
  try {
    post.value = await postApi.detail(route.params.id)
    if (post.value.auditStatus === 1) comments.value = await commentApi.byPost(post.value.id)
    if (post.value.circleId) circle.value = await circleApi.detail(post.value.circleId)
    const page = await postApi.list({ page: 1, size: 4, circleId: post.value.circleId || undefined })
    related.value = (page.records || []).filter((item) => item.id !== post.value.id).slice(0, 3)
    if (authState.token && !isSelf.value) {
      const status = await followApi.status(post.value.authorId)
      following.value = status.following
    }
  } catch {
    router.replace('/posts')
  }
}

async function toggleLike() {
  if (!authState.token) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  if (post.value.liked) { await interactionApi.unlike(post.value.id); post.value.liked = false; post.value.likeCount = Math.max(0, post.value.likeCount - 1) }
  else { await interactionApi.like(post.value.id); post.value.liked = true; post.value.likeCount += 1 }
}

async function toggleFavorite() {
  if (!authState.token) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  if (post.value.favorited) { await interactionApi.unfavorite(post.value.id); post.value.favorited = false; post.value.favoriteCount = Math.max(0, post.value.favoriteCount - 1) }
  else { await interactionApi.favorite(post.value.id); post.value.favorited = true; post.value.favoriteCount += 1 }
}

async function toggleFollow() {
  if (!authState.token) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  if (following.value) { await followApi.unfollow(post.value.authorId); following.value = false; ElMessage.success('已取消关注') }
  else { await followApi.follow(post.value.authorId); following.value = true; ElMessage.success('关注成功') }
}

function focusComment() {
  if (!authState.token) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  nextTick(() => commentInput.value?.focus())
}

function setReply(comment) {
  if (!authState.token) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  replyTarget.value = comment
  nextTick(() => commentInput.value?.focus())
}

async function submitComment() {
  if (!authState.token) return router.push({ name: 'login', query: { redirect: route.fullPath } })
  if (!commentContent.value.trim()) return ElMessage.warning('请输入评论内容')
  commentSubmitting.value = true
  try {
    const created = await commentApi.create({ postId: post.value.id, parentId: replyTarget.value?.id || null, content: commentContent.value.trim() })
    comments.value.push(created)
    post.value.commentCount += 1
    commentContent.value = ''
    replyTarget.value = null
    ElMessage.success('评论成功')
  } finally { commentSubmitting.value = false }
}

async function deleteComment(comment) {
  await ElMessageBox.confirm('确定删除这条评论吗？', '删除评论', { type: 'warning' })
  await commentApi.remove(comment.id)
  comments.value = comments.value.filter((item) => item.id !== comment.id)
  post.value.commentCount = Math.max(0, post.value.commentCount - 1)
  ElMessage.success('评论已删除')
}

function openEdit() {
  editForm.title = post.value.title
  editForm.content = post.value.content
  editForm.imagesText = (post.value.images || []).join('\n')
  editVisible.value = true
}

async function submitEdit() {
  if (!editForm.title.trim() || !editForm.content.trim()) return ElMessage.warning('请填写标题和正文')
  editSubmitting.value = true
  try {
    post.value = await postApi.update(post.value.id, {
      title: editForm.title.trim(),
      content: editForm.content.trim(),
      images: editForm.imagesText.split('\n').map((item) => item.trim()).filter(Boolean),
      circleId: post.value.circleId,
      topicId: post.value.topicId
    })
    editVisible.value = false
    ElMessage.success('修改成功，帖子已重新进入审核')
  } finally { editSubmitting.value = false }
}

async function removePost() {
  await ElMessageBox.confirm('删除后无法恢复，确定继续吗？', '删除帖子', { type: 'warning' })
  await postApi.remove(post.value.id)
  ElMessage.success('帖子已删除')
  router.replace('/profile')
}

function openPost(id) { router.push(`/posts/${id}`) }
onMounted(load)
watch(() => route.params.id, load)
</script>

<style scoped>
.breadcrumb { margin-top: 26px; }
.audit-banner { padding: 12px 15px; border-radius: 10px; background: #fff5df; color: #90691e; font-size: 13px; margin-bottom: 20px; }
.tag-row { display: flex; gap: 8px; flex-wrap: wrap; margin: 0 0 20px; }
.comment-count { color: var(--coral); font-size: 14px; }
.reply-hint { display: flex; align-items: center; justify-content: space-between; padding: 7px 12px; margin-bottom: 8px; border-radius: 8px; background: var(--teal-soft); color: var(--teal); font-size: 12px; }
.reply-hint button { border: 0; background: none; color: var(--coral); cursor: pointer; }
.comment-submit { display: flex; justify-content: flex-end; margin-top: 9px; }
.comment-actions { display: flex; gap: 12px; margin-top: 7px; }
.comment-actions button { border: 0; background: none; color: #929a96; font-size: 11px; cursor: pointer; padding: 0; }
.comment-actions button:hover { color: var(--coral); }
.circle-side { background: linear-gradient(145deg, #203b34, #3f776a); color: white; }
.circle-side-icon { font-size: 28px; color: #f3c66e; margin-bottom: 10px; }
.circle-side p { color: rgba(255,255,255,.62); font-size: 12px; line-height: 1.7; }
.circle-side :deep(.el-button) { color: #f3c66e; padding-left: 0; }
.side-muted { color: var(--muted); font-size: 12px; line-height: 1.8; margin-bottom: 0; }
.related-post { display: flex; flex-direction: column; gap: 5px; padding: 12px 0; border-top: 1px solid #f0ebe3; cursor: pointer; }
.related-post:hover strong { color: var(--coral); }
.related-post strong { font-size: 12px; line-height: 1.5; }
.related-post span { color: var(--muted); font-size: 10px; }
.loading-holder { min-height: 70vh; }
.edit-tip { padding: 10px 13px; margin-bottom: 16px; border-radius: 10px; background: #eef5f2; color: var(--teal); font-size: 12px; line-height: 1.6; }
</style>
