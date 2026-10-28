<template>
  <div class="page-container">
    <section v-if="user" class="profile-hero">
      <UserAvatar :name="user.nickname" :src="user.avatar" :size="92" />
      <div class="profile-main">
        <div class="eyebrow">MY TUNAN SPACE</div>
        <h1>{{ user.nickname }}</h1>
        <div class="profile-handle">@{{ user.username }} · {{ user.city }}</div>
        <p>{{ user.bio || '这个用户还没有写下个人简介。' }}</p>
      </div>
      <el-button round @click="openEdit">编辑资料</el-button>
      <div class="profile-stats">
        <div><strong>{{ summary.posts }}</strong><span>我的帖子</span></div>
        <div><strong>{{ summary.comments }}</strong><span>我的评论</span></div>
        <div><strong>{{ summary.favorites }}</strong><span>我的收藏</span></div>
        <div><strong>{{ followingList.length }}</strong><span>关注的人</span></div>
      </div>
    </section>

    <section class="profile-content">
      <el-tabs v-model="activeTab" class="profile-tabs" @tab-change="loadTab">
        <el-tab-pane label="我的帖子" name="posts">
          <div v-loading="loading.posts" class="post-grid"><PostCard v-for="item in data.posts" :key="item.id" :post="item" /></div>
          <EmptyState v-if="!loading.posts && !data.posts.length" icon="✍️" title="还没有发布帖子" description="分享你的第一个兔南瞬间吧。" />
        </el-tab-pane>
        <el-tab-pane label="我的评论" name="comments">
          <div v-loading="loading.comments" class="manage-list">
            <article v-for="item in data.comments" :key="item.id" class="manage-item">
              <div class="manage-main">
                <div class="manage-meta"><span>{{ formatTime(item.createdAt) }}</span><span>评论于《{{ item.postTitle }}》</span></div>
                <p>{{ item.content }}</p>
              </div>
              <div class="manage-actions"><el-button text type="primary" @click="router.push(`/posts/${item.postId}`)">查看</el-button><el-button text type="danger" @click="removeComment(item)">删除</el-button></div>
            </article>
          </div>
          <EmptyState v-if="!loading.comments && !data.comments.length" icon="💬" title="还没有发表过评论" description="去帖子里聊两句吧。" />
        </el-tab-pane>
        <el-tab-pane label="我的收藏" name="favorites">
          <div v-loading="loading.favorites" class="post-grid"><PostCard v-for="item in data.favorites" :key="item.id" :post="item" /></div>
          <EmptyState v-if="!loading.favorites && !data.favorites.length" icon="⭐" title="收藏夹还是空的" description="遇到喜欢的帖子，点个收藏以后慢慢看。" />
        </el-tab-pane>
        <el-tab-pane label="我的点赞" name="likes">
          <div v-loading="loading.likes" class="post-grid"><PostCard v-for="item in data.likes" :key="item.id" :post="item" /></div>
          <EmptyState v-if="!loading.likes && !data.likes.length" icon="👏" title="还没有点赞记录" description="给喜欢的同城分享一点鼓励。" />
        </el-tab-pane>
        <el-tab-pane label="我的圈子" name="circles">
          <div v-loading="loading.circles" class="manage-list">
            <article v-for="item in data.circles" :key="item.id" class="manage-item">
              <div class="manage-main"><div class="manage-meta"><el-tag size="small" :type="auditType(item.auditStatus)">{{ auditText(item.auditStatus) }}</el-tag><span>{{ item.memberCount }} 成员</span></div><h3>{{ item.name }}</h3><p>{{ item.description }}</p></div>
              <div class="manage-actions"><el-button text @click="router.push(`/circles/${item.id}`)">查看</el-button><el-button text type="danger" @click="removeCircle(item)">删除</el-button></div>
            </article>
          </div>
          <EmptyState v-if="!loading.circles && !data.circles.length" icon="🧭" title="还没有创建圈子" description="创建一个圈子，召集同频的人。" />
        </el-tab-pane>
        <el-tab-pane label="我的话题" name="topics">
          <div v-loading="loading.topics" class="manage-list">
            <article v-for="item in data.topics" :key="item.id" class="manage-item">
              <div class="manage-main"><div class="manage-meta"><el-tag size="small" :type="auditType(item.auditStatus)">{{ auditText(item.auditStatus) }}</el-tag><span>🔥 {{ item.heat }} 热度</span></div><h3># {{ item.name }}</h3><p>{{ item.description }}</p></div>
              <div class="manage-actions"><el-button text @click="router.push(`/topics/${item.id}`)">查看</el-button><el-button text type="danger" @click="removeTopic(item)">删除</el-button></div>
            </article>
          </div>
          <EmptyState v-if="!loading.topics && !data.topics.length" icon="💭" title="还没有发起话题" description="发起一个值得讨论的话题吧。" />
        </el-tab-pane>
        <el-tab-pane label="关注列表" name="following">
          <div v-loading="loading.following" class="following-grid">
            <article v-for="item in followingList" :key="item.id" class="following-card">
              <UserAvatar :name="item.nickname" :src="item.avatar" :size="48" />
              <div><strong>{{ item.nickname }}</strong><span>{{ item.city }} · {{ item.bio || '兔南用户' }}</span></div>
              <el-button round size="small" @click="unfollow(item)">取消关注</el-button>
            </article>
          </div>
          <EmptyState v-if="!loading.following && !followingList.length" icon="🤝" title="还没有关注任何人" description="在帖子详情里关注感兴趣的作者。" />
        </el-tab-pane>
      </el-tabs>
    </section>

    <el-dialog v-model="editVisible" title="编辑个人资料" width="520px">
      <el-form label-position="top">
        <el-form-item label="昵称"><el-input v-model="editForm.nickname" maxlength="20" /></el-form-item>
        <el-form-item label="城市"><el-input v-model="editForm.city" maxlength="60" /></el-form-item>
        <el-form-item label="头像地址"><el-input v-model="editForm.avatar" placeholder="https://..." /></el-form-item>
        <el-form-item label="个人简介"><el-input v-model="editForm.bio" type="textarea" :rows="4" maxlength="300" show-word-limit /></el-form-item>
      </el-form>
      <template #footer><el-button @click="editVisible = false">取消</el-button><el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { circleApi, commentApi, followApi, interactionApi, postApi, topicApi, userApi } from '../api'
import { authState, updateLocalUser } from '../stores/auth'
import { auditText, auditType, formatTime } from '../utils/format'
import UserAvatar from '../components/UserAvatar.vue'
import PostCard from '../components/PostCard.vue'
import EmptyState from '../components/EmptyState.vue'

const router = useRouter()
const user = ref(authState.user)
const activeTab = ref('posts')
const editVisible = ref(false)
const saving = ref(false)
const editForm = reactive({ nickname: '', city: '', avatar: '', bio: '' })
const summary = reactive({ posts: 0, comments: 0, favorites: 0 })
const followingList = ref([])
const data = reactive({ posts: [], comments: [], favorites: [], likes: [], circles: [], topics: [] })
const loading = reactive({ posts: false, comments: false, favorites: false, likes: false, circles: false, topics: false, following: false })
const loaded = reactive({ posts: false, comments: false, favorites: false, likes: false, circles: false, topics: false, following: false })

async function loadSummary() {
  const [posts, comments, favorites, following] = await Promise.all([
    postApi.mine({ page: 1, size: 1 }),
    commentApi.mine({ page: 1, size: 1 }),
    interactionApi.myFavorites({ page: 1, size: 1 }),
    followApi.following()
  ])
  summary.posts = posts.total || 0
  summary.comments = comments.total || 0
  summary.favorites = favorites.total || 0
  followingList.value = following || []
  loaded.following = true
}

async function loadTab(name) {
  if (loaded[name]) return
  loading[name] = true
  try {
    if (name === 'posts') data.posts = (await postApi.mine({ page: 1, size: 50 })).records || []
    if (name === 'comments') data.comments = (await commentApi.mine({ page: 1, size: 50 })).records || []
    if (name === 'favorites') data.favorites = (await interactionApi.myFavorites({ page: 1, size: 50 })).records || []
    if (name === 'likes') data.likes = (await interactionApi.myLikes({ page: 1, size: 50 })).records || []
    if (name === 'circles') data.circles = (await circleApi.mine()) || []
    if (name === 'topics') data.topics = (await topicApi.mine()) || []
    if (name === 'following') followingList.value = (await followApi.following()) || []
    loaded[name] = true
  } finally { loading[name] = false }
}

function openEdit() {
  Object.assign(editForm, { nickname: user.value.nickname, city: user.value.city, avatar: user.value.avatar || '', bio: user.value.bio || '' })
  editVisible.value = true
}

async function saveProfile() {
  saving.value = true
  try {
    user.value = await userApi.updateMe(editForm)
    updateLocalUser(user.value)
    editVisible.value = false
    ElMessage.success('资料已更新')
  } finally { saving.value = false }
}

async function removeComment(item) {
  await ElMessageBox.confirm('确定删除这条评论吗？', '提示', { type: 'warning' })
  await commentApi.remove(item.id)
  data.comments = data.comments.filter((comment) => comment.id !== item.id)
  summary.comments = Math.max(0, summary.comments - 1)
  ElMessage.success('评论已删除')
}

async function removeCircle(item) {
  await ElMessageBox.confirm(`确定删除圈子「${item.name}」吗？`, '删除圈子', { type: 'warning' })
  await circleApi.remove(item.id)
  data.circles = data.circles.filter((circle) => circle.id !== item.id)
  ElMessage.success('圈子已删除')
}

async function removeTopic(item) {
  await ElMessageBox.confirm(`确定删除话题「${item.name}」吗？`, '删除话题', { type: 'warning' })
  await topicApi.remove(item.id)
  data.topics = data.topics.filter((topic) => topic.id !== item.id)
  ElMessage.success('话题已删除')
}

async function unfollow(item) {
  await followApi.unfollow(item.id)
  followingList.value = followingList.value.filter((people) => people.id !== item.id)
  ElMessage.success('已取消关注')
}

onMounted(async () => {
  try { user.value = await userApi.me(); updateLocalUser(user.value) } catch {}
  await Promise.all([loadSummary(), loadTab('posts')])
})
</script>

<style scoped>
.profile-hero { position: relative; margin-top: 28px; padding: 38px; border-radius: 28px; background: linear-gradient(125deg, #172d27, #3d7065); color: white; display: flex; align-items: center; gap: 24px; overflow: hidden; }
.profile-hero::after { content: ''; position: absolute; width: 260px; height: 260px; border-radius: 50%; right: -80px; top: -110px; background: rgba(239,184,74,.16); }
.profile-main { flex: 1; position: relative; z-index: 1; }
.profile-main h1 { margin: 0 0 5px; font-size: 35px; }
.profile-handle { color: rgba(255,255,255,.5); font-size: 12px; }
.profile-main p { color: rgba(255,255,255,.7); margin: 13px 0 0; font-size: 14px; }
.profile-hero > :deep(.el-button) { position: relative; z-index: 1; background: rgba(255,255,255,.1); border-color: rgba(255,255,255,.25); color: white; }
.profile-stats { position: absolute; right: 38px; bottom: 30px; display: flex; gap: 28px; }
.profile-stats div { display: flex; flex-direction: column; text-align: right; }
.profile-stats strong { font-size: 22px; }
.profile-stats span { color: rgba(255,255,255,.48); font-size: 10px; margin-top: 2px; }
.profile-content { margin-top: 28px; background: var(--paper); border: 1px solid var(--line); border-radius: 22px; padding: 8px 25px 28px; }
.profile-tabs :deep(.el-tabs__header) { margin-bottom: 24px; }
.profile-tabs :deep(.el-tabs__item) { font-weight: 600; }
.manage-list { display: grid; gap: 12px; }
.manage-item { display: flex; align-items: center; gap: 18px; padding: 18px; border: 1px solid #eee8de; border-radius: 14px; }
.manage-main { flex: 1; min-width: 0; }
.manage-meta { display: flex; align-items: center; gap: 10px; color: var(--muted); font-size: 11px; }
.manage-main h3 { margin: 7px 0 4px; font-size: 16px; }
.manage-main p { margin: 5px 0 0; color: #5d6863; font-size: 13px; line-height: 1.6; }
.manage-actions { flex: none; }
.following-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 12px; }
.following-card { display: flex; align-items: center; gap: 12px; padding: 15px; border: 1px solid #eee8de; border-radius: 14px; }
.following-card > div:nth-child(2) { flex: 1; display: flex; flex-direction: column; gap: 4px; min-width: 0; }
.following-card strong { font-size: 13px; }
.following-card span { color: var(--muted); font-size: 10px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
@media (max-width: 760px) {
  .profile-hero { padding: 26px; align-items: flex-start; flex-wrap: wrap; }
  .profile-stats { position: static; width: 100%; justify-content: space-between; margin-top: 10px; }
  .profile-stats div { text-align: left; }
  .following-grid { grid-template-columns: 1fr; }
  .manage-item { align-items: flex-start; flex-direction: column; }
  .profile-content { padding: 8px 15px 20px; }
}
</style>
