<template>
  <div class="auth-page">
    <section class="auth-visual">
      <router-link to="/" class="auth-brand">
        <span>兔</span><strong>兔南呱呱同城网</strong>
      </router-link>
      <div>
        <div class="eyebrow">WELCOME TO TUNAN</div>
        <h1>在这座城，<br />遇见同频的人。</h1>
        <p>逛圈子、聊话题、记录生活。兔南呱呱把同城的新鲜事和有趣灵魂，都装进一个温暖的社区。</p>
      </div>
      <div class="auth-quote">“城市很大，但因为同频，我们离得很近。”</div>
    </section>
    <section class="auth-form-wrap">
      <div class="auth-form">
        <div class="auth-mobile-brand"><span>兔</span> 兔南呱呱</div>
        <h2>{{ isRegister ? '加入兔南' : '欢迎回来' }}</h2>
        <p>{{ isRegister ? '创建账号，开启你的同城生活。' : '登录后继续发现兔南的新鲜事。' }}</p>
        <el-form label-position="top" @submit.prevent="submit">
          <el-form-item label="账号">
            <el-input v-model="form.username" size="large" placeholder="4-20 位字母、数字或下划线" prefix-icon="User" />
          </el-form-item>
          <el-form-item v-if="isRegister" label="昵称">
            <el-input v-model="form.nickname" size="large" placeholder="大家怎么称呼你" prefix-icon="Avatar" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" size="large" type="password" show-password placeholder="至少 6 位" prefix-icon="Lock" @keyup.enter="submit" />
          </el-form-item>
          <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click.prevent="submit">
            {{ isRegister ? '注册并登录' : '登录' }}
          </el-button>
        </el-form>
        <div class="auth-switch">
          {{ isRegister ? '已经有账号？' : '还没有账号？' }}
          <button type="button" @click="switchMode">{{ isRegister ? '直接登录' : '立即注册' }}</button>
          <span class="separator">·</span>
          <router-link to="/">先逛逛</router-link>
        </div>
        <div class="demo-account">
          <strong>体验账号</strong><br />
          普通用户：xiaonan / 123456<br />
          管理员：admin / 123456
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const isRegister = ref(false)
const loading = ref(false)
const form = reactive({ username: '', nickname: '', password: '' })

function switchMode() {
  isRegister.value = !isRegister.value
}

async function submit() {
  if (!form.username.trim() || !form.password) {
    ElMessage.warning('请填写账号和密码')
    return
  }
  loading.value = true
  try {
    const result = isRegister.value
      ? await register({ username: form.username.trim(), nickname: form.nickname.trim() || form.username.trim(), password: form.password })
      : await login({ username: form.username.trim(), password: form.password })
    ElMessage.success(isRegister.value ? '注册成功，欢迎加入兔南' : '登录成功')
    const target = route.query.redirect || (result.user?.role === 'ADMIN' ? '/admin' : '/')
    router.replace(String(target))
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-brand { position: relative; display: flex; align-items: center; gap: 10px; color: white; text-decoration: none; font-size: 15px; }
.auth-brand > span, .auth-mobile-brand > span { width: 38px; height: 38px; display: inline-grid; place-items: center; border-radius: 12px 12px 12px 4px; background: var(--coral); font-weight: 900; }
.auth-mobile-brand { display: none; align-items: center; gap: 9px; font-weight: 800; margin-bottom: 32px; }
.submit-btn { width: 100%; height: 48px; margin-top: 6px; }
.separator { margin: 0 6px; color: #bbb; }
.auth-switch a { color: var(--teal); text-decoration: none; font-weight: 700; }
@media (max-width: 1000px) {
  .auth-mobile-brand { display: flex; }
  .auth-visual { min-height: 260px; }
  .auth-visual > div:nth-child(2) p { display: none; }
  .auth-visual h1 { font-size: 38px; }
  .auth-quote { display: none; }
}
</style>
