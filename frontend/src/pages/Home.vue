<template>
  <div>
    <section class="home-hero">
      <el-carousel v-if="banners.length" ref="carouselRef" height="470px" :autoplay="false" arrow="always" indicator-position="outside" @change="onCarouselChange">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div class="hero-slide" :style="{ backgroundImage: `url(${assetUrl(banner.imageUrl)})` }">
            <div class="hero-copy">
              <div class="eyebrow">TUNAN · LOCAL LIFE</div>
              <h1>{{ banner.title }}</h1>
              <p>发现同城圈子，参与热门话题，把普通日子过成值得分享的故事。</p>
              <div class="hero-actions">
                <el-button class="hero-primary" size="large" round @click="router.push(banner.linkUrl || '/circles')">现在去逛逛 <el-icon><Right /></el-icon></el-button>
                <el-button class="hero-ghost" size="large" round @click="openPublish">分享我的此刻</el-button>
              </div>
            </div>
          </div>
        </el-carousel-item>
        <el-carousel-item>
          <div class="hero-slide video-slide">
            <div class="hero-copy video-copy">
              <div class="eyebrow">JIANGNAN STORY · 30 SECONDS</div>
              <h1>一城水巷，<br />藏着江南的温柔。</h1>
              <p>橹声轻摇，白墙黛瓦倒映在水面。用 30 秒，走进一段属于江南的同城故事。</p>
              <div class="hero-actions">
                <el-button class="hero-primary" size="large" round @click="router.push('/news')">走进江南 <el-icon><Right /></el-icon></el-button>
                <el-button class="hero-ghost" size="large" round @click="openPublish">分享我的此刻</el-button>
              </div>
            </div>
            <div class="video-stage">
              <video
                ref="carouselVideo"
                class="carousel-video"
                :src="videoUrl"
                :poster="assetUrl('/images/jiangnan/tuisi-garden.jpg')"
                playsinline
                preload="metadata"
              />
              <div class="video-stage-shade" />
              <button v-if="videoMuted" class="video-sound-btn" @click.stop="enableVideoSound">🔊 开启声音</button>
              <div v-else class="video-sound-state">🔊 声音已开启</div>
              <div class="video-badge"><span class="video-dot" /> 江南影像 · 30″</div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>      <div v-else class="hero-fallback">
        <div class="hero-copy">
          <div class="eyebrow">TUNAN · LOCAL LIFE</div>
          <h1>发现江南的<br />100 种生活方式</h1>
          <p>从一条街巷、一顿晚餐，到一群同频的人。</p>
        </div>
      </div>
    </section>

    <div class="page-container">
      <section class="stats-strip">
        <div><strong>{{ stats.posts }}</strong><span>同城分享</span></div>
        <div><strong>{{ stats.circles }}</strong><span>兴趣圈子</span></div>
        <div><strong>{{ stats.topics }}</strong><span>热门话题</span></div>
        <div><strong>{{ stats.news }}</strong><span>本地资讯</span></div>
      </section>

      <section>
        <div class="section-heading">
          <div><div class="eyebrow">CIRCLES</div><h2>找到你的同频圈子</h2><p>因为共同热爱，陌生人也能很快熟络起来。</p></div>
          <router-link to="/circles">全部圈子 →</router-link>
        </div>
        <div v-loading="loading" class="card-grid home-circles">
          <article v-for="(circle, index) in circles" :key="circle.id" class="circle-card home-circle-card" @click="router.push(`/circles/${circle.id}`)">
            <div class="home-circle-cover">
              <img :src="assetUrl(circle.cover || '/images/jiangnan/qibao-canal.jpg')" :alt="circle.name" loading="lazy" />
              <div class="home-cover-shade" />
              <span class="circle-icon">{{ circleIcons[index % circleIcons.length] }}</span>
            </div>
            <div class="home-circle-body">
              <h3>{{ circle.name }}</h3>
              <p>{{ circle.description }}</p>
              <div class="card-meta"><span>{{ circle.memberCount }} 位成员</span><span>{{ circle.postCount }} 条分享</span></div>
            </div>
          </article>
        </div>
      </section>

      <section>
        <div class="section-heading">
          <div><div class="eyebrow">TOGETHER</div><h2>此刻，江南正在发生</h2><p>看看江南同城最近都在聊什么。</p></div>
          <router-link to="/posts">更多动态 →</router-link>
        </div>
        <div class="home-split">
          <div v-loading="loading" class="post-grid">
            <PostCard v-for="post in posts" :key="post.id" :post="post" />
          </div>
          <aside class="home-side">
            <div class="hot-topics side-card">
              <div class="side-title"><h3>热门话题</h3><router-link to="/topics">全部</router-link></div>
              <div v-for="(topic, index) in topics" :key="topic.id" class="hot-topic" @click="router.push(`/topics/${topic.id}`)">
                <span class="rank">0{{ index + 1 }}</span>
                <div><strong># {{ topic.name }}</strong><small>{{ topic.heat }} 热度</small></div>
              </div>
            </div>
            <div class="community-note">
              <div class="note-emoji">🐸</div>
              <h3>呱呱守则</h3>
              <p>真诚分享、友善交流、保护隐私。让每一次相遇，都舒服又温暖。</p>
            </div>
          </aside>
        </div>
      </section>

      <section>
        <div class="section-heading">
          <div><div class="eyebrow">LOCAL NEWS</div><h2>江南新鲜事</h2><p>城市里的新变化，第一时间知道。</p></div>
          <router-link to="/news">全部资讯 →</router-link>
        </div>
        <div v-loading="loading" class="news-grid">
          <article v-for="item in news" :key="item.id" class="news-card" @click="router.push(`/news/${item.id}`)">
            <img :src="assetUrl(item.cover)" :alt="item.title" />
            <div><h3>{{ item.title }}</h3><p>{{ item.summary }}</p><div class="news-meta"><span>{{ item.city }}</span><span>{{ item.viewCount }} 阅读</span></div></div>
          </article>
        </div>
      </section>
    </div>
    <PublishDialog v-model="publishVisible" />
  </div>
</template>

<script setup>
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { assetUrl } from '../utils/format'
import { bannerApi, circleApi, newsApi, postApi, topicApi } from '../api'
import { authState } from '../stores/auth'
import PostCard from '../components/PostCard.vue'
import PublishDialog from '../components/PublishDialog.vue'

const router = useRouter()
const loading = ref(true)
const publishVisible = ref(false)
const carouselRef = ref(null)
const carouselVideo = ref(null)
const videoUrl = assetUrl('/videos/jiangnan-carousel.mp4')
const videoMuted = ref(true)
let rotationTimer = null
let currentSlide = 0
const banners = ref([])
const circles = ref([])
const topics = ref([])
const posts = ref([])
const news = ref([])
const stats = reactive({ posts: 0, circles: 0, topics: 0, news: 0 })
const circleIcons = ['🍜', '🏕️', '🐾', '📷', '🎨', '🚴']

function pauseHeroVideo() {
  carouselVideo.value?.pause()
}

function startHeroVideo() {
  const video = carouselVideo.value
  if (!video) return
  video.currentTime = 0
  video.muted = false
  video.volume = 1
  video.play()
    .then(() => { videoMuted.value = false })
    .catch(() => {
      video.muted = true
      videoMuted.value = true
      video.play().catch(() => {})
    })
}

function enableVideoSound() {
  const video = carouselVideo.value
  if (!video) return
  video.muted = false
  video.volume = 1
  video.play()
    .then(() => { videoMuted.value = false })
    .catch(() => { videoMuted.value = true })
}

function scheduleRotation() {
  if (rotationTimer) clearTimeout(rotationTimer)
  const videoIndex = banners.value.length
  const isVideo = currentSlide === videoIndex
  if (isVideo) startHeroVideo()
  else pauseHeroVideo()
  rotationTimer = setTimeout(() => {
    const total = banners.value.length + 1
    carouselRef.value?.setActiveItem((currentSlide + 1) % total)
  }, isVideo ? 30000 : 6500)
}

function onCarouselChange(index) {
  currentSlide = index
  scheduleRotation()
}

function openPublish() {
  if (!authState.token) {
    router.push({ name: 'login', query: { redirect: '/' } })
    return
  }
  publishVisible.value = true
}

onMounted(async () => {
  try {
    const [bannerData, circlePage, topicPage, postPage, newsPage] = await Promise.all([
      bannerApi.list(),
      circleApi.list({ page: 1, size: 6 }),
      topicApi.list({ page: 1, size: 4 }),
      postApi.list({ page: 1, size: 4 }),
      newsApi.list({ page: 1, size: 3 })
    ])
    banners.value = bannerData || []
    circles.value = circlePage.records || []
    topics.value = topicPage.records || []
    posts.value = postPage.records || []
    news.value = newsPage.records || []
    stats.posts = postPage.total || 0
    stats.circles = circlePage.total || 0
    stats.topics = topicPage.total || 0
    stats.news = newsPage.total || 0
  } finally {
    loading.value = false
    await nextTick()
    currentSlide = 0
    scheduleRotation()
  }
})

onBeforeUnmount(() => {
  if (rotationTimer) clearTimeout(rotationTimer)
  pauseHeroVideo()
})
</script>

<style scoped>
.home-hero { position: relative; max-width: 1240px; margin: 22px auto 0; padding: 0 24px; }
.hero-slide { position: relative; height: 100%; border-radius: 30px; overflow: hidden; background-size: cover; background-position: center; display: flex; align-items: center; padding: 0 clamp(28px, 5vw, 64px); isolation: isolate; }
.hero-slide::before { content: ''; position: absolute; inset: 0; background: linear-gradient(90deg, rgba(8, 23, 19, .86) 0%, rgba(8, 23, 19, .6) 36%, rgba(8, 23, 19, .16) 66%, rgba(8, 23, 19, .02) 100%); z-index: -1; }
.hero-slide::after { content: ''; position: absolute; left: 12%; top: -20%; width: 180px; height: 140%; background: linear-gradient(90deg, transparent, rgba(255,255,255,.09), transparent); transform: rotate(14deg); animation: hero-sheen 7s ease-in-out infinite; pointer-events: none; }
.hero-copy { position: relative; z-index: 6; max-width: 54%; color: white; text-shadow: 0 2px 20px rgba(0,0,0,.22); }
.hero-copy h1 { font-size: clamp(38px, 5.7vw, 66px); line-height: 1.12; letter-spacing: -2px; margin-bottom: 18px; }
.hero-copy p { max-width: 560px; color: rgba(255,255,255,.76); line-height: 1.8; font-size: 15px; }
.hero-actions { display: flex; gap: 12px; margin-top: 28px; flex-wrap: wrap; }
.hero-primary { background: var(--coral); border-color: var(--coral); color: white; padding: 0 25px; }
.hero-ghost { background: rgba(255,255,255,.1); border-color: rgba(255,255,255,.35); color: white; backdrop-filter: blur(8px); }
.hero-fallback { min-height: 420px; border-radius: 30px; padding: 80px; display: flex; align-items: center; background: linear-gradient(130deg, #152f28, #34776c); }
:deep(.el-carousel) { position: relative; z-index: 2; }
:deep(.el-carousel__container) { border-radius: 30px; overflow: hidden; }
:deep(.el-carousel__arrow) { z-index: 8; }
:deep(.el-carousel__indicators--outside) { margin-top: 10px; }
:deep(.el-carousel__arrow) { background: rgba(255,255,255,.16); backdrop-filter: blur(8px); width: 42px; height: 42px; }
.stats-strip { display: grid; grid-template-columns: repeat(4, 1fr); background: var(--paper); border: 1px solid var(--line); border-radius: 20px; margin-top: 18px; padding: 22px 16px; box-shadow: 0 10px 30px rgba(37,52,46,.04); }
.stats-strip > div { text-align: center; border-right: 1px solid var(--line); display: flex; flex-direction: column; }
.stats-strip > div:last-child { border-right: 0; }
.stats-strip strong { font-size: 25px; color: var(--teal); }
.stats-strip span { color: var(--muted); font-size: 12px; margin-top: 4px; }
.home-split { display: grid; grid-template-columns: minmax(0, 1fr) 300px; gap: 20px; align-items: start; }
.home-side { position: sticky; top: 98px; }
.side-title { display: flex; justify-content: space-between; align-items: center; }
.side-title a { color: var(--teal); font-size: 12px; text-decoration: none; }
.hot-topic { display: flex; gap: 12px; padding: 13px 0; border-top: 1px solid #f0ebe3; cursor: pointer; }
.hot-topic:hover strong { color: var(--coral); }
.hot-topic .rank { color: #c4b9a9; font-weight: 900; font-size: 12px; padding-top: 2px; }
.hot-topic div { display: flex; flex-direction: column; gap: 4px; }
.hot-topic strong { font-size: 13px; transition: color .2s; }
.hot-topic small { color: var(--muted); font-size: 10px; }
.community-note { padding: 25px; border-radius: 20px; background: linear-gradient(140deg, #233e36, #376c61); color: white; }
.note-emoji { font-size: 34px; margin-bottom: 10px; }
.community-note h3 { margin-bottom: 8px; }
.community-note p { color: rgba(255,255,255,.65); font-size: 12px; line-height: 1.75; margin: 0; }
.news-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 18px; }
.news-grid .news-card { grid-template-columns: 1fr; }
.news-grid .news-card img { height: 145px; }
.home-circle-card { padding: 0; overflow: hidden; perspective: 900px; transform-style: preserve-3d; animation: card-rise .7s cubic-bezier(.2,.8,.2,1) both; }
.home-circle-card:nth-child(2) { animation-delay: .08s; }
.home-circle-card:nth-child(3) { animation-delay: .16s; }
.home-circle-cover { position: relative; height: 170px; overflow: hidden; }
.home-circle-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform .6s cubic-bezier(.2,.8,.2,1); }
.home-circle-card:hover .home-circle-cover img { transform: scale(1.1); }
.home-cover-shade { position: absolute; inset: 0; background: linear-gradient(180deg, transparent 25%, rgba(16, 31, 25, .72)); }
.home-circle-cover .circle-icon { position: absolute; bottom: 14px; left: 17px; margin: 0; }
.home-circle-body { padding: 20px 22px 22px; }
.home-circle-body h3 { margin-bottom: 8px; }
.home-circle-body p { min-height: 42px; }
@keyframes card-rise { from { opacity: 0; transform: translateY(26px) scale(.97); } to { opacity: 1; transform: translateY(0) scale(1); } }
@keyframes hero-sheen { 0%, 100% { transform: translateX(-140%) rotate(14deg); opacity: 0; } 45% { opacity: .75; } 70% { transform: translateX(580%) rotate(14deg); opacity: 0; } }

.video-slide { padding-right: 0; background: linear-gradient(90deg, rgba(11, 28, 23, .97), rgba(14, 33, 27, .84)), url('/images/jiangnan/zhouzhuang-lanterns-hero.jpg') center / cover; }
.video-slide::before { background: linear-gradient(90deg, rgba(8, 23, 19, .94) 0%, rgba(8, 23, 19, .82) 44%, rgba(8, 23, 19, .16) 58%, transparent 100%); }
.video-copy { max-width: 46%; }
.video-copy h1 { font-size: clamp(32px, 4.5vw, 54px); }
.video-stage { position: absolute; z-index: 5; top: 0; right: 0; width: 50%; height: 100%; overflow: hidden; background: #13231f; box-shadow: -24px 0 55px rgba(0,0,0,.28); }
.video-stage::before { content: ''; position: absolute; inset: 0 auto 0 0; width: 90px; z-index: 3; background: linear-gradient(90deg, rgba(11,28,23,.42), transparent); pointer-events: none; }
.carousel-video { width: 100%; height: 100%; object-fit: cover; object-position: center 44%; display: block; }
.video-stage-shade { position: absolute; inset: 0; pointer-events: none; background: linear-gradient(180deg, rgba(8, 20, 16, .02) 55%, rgba(8, 20, 16, .46) 100%); }
.video-badge { position: absolute; right: 16px; bottom: 15px; display: inline-flex; align-items: center; gap: 7px; padding: 7px 11px; border-radius: 999px; background: rgba(11, 27, 22, .63); border: 1px solid rgba(255,255,255,.2); color: rgba(255,255,255,.92); font-size: 11px; backdrop-filter: blur(14px); }
.video-sound-btn, .video-sound-state { position: absolute; left: 16px; bottom: 15px; z-index: 4; border: 1px solid rgba(255,255,255,.3); border-radius: 999px; padding: 8px 13px; background: rgba(8, 25, 20, .7); color: white; font-size: 11px; font-weight: 700; backdrop-filter: blur(14px); }
.video-sound-btn { cursor: pointer; animation: sound-breathe 1.8s ease-in-out infinite; }
.video-sound-btn:hover { background: var(--coral); border-color: var(--coral); }
.video-sound-state { opacity: .78; }
.video-dot { width: 7px; height: 7px; border-radius: 50%; background: #ff6e4a; box-shadow: 0 0 14px #ff6e4a; animation: video-pulse 1.8s ease-in-out infinite; }
@keyframes sound-breathe { 0%, 100% { transform: scale(1); box-shadow: 0 0 0 rgba(245,102,79,0); } 50% { transform: scale(1.04); box-shadow: 0 0 22px rgba(245,102,79,.35); } }
@keyframes video-pulse { 0%, 100% { opacity: .6; transform: scale(.82); } 50% { opacity: 1; transform: scale(1.15); } }
@media (max-width: 1000px) {
  .video-stage { right: 0; width: 50%; height: 100%; top: 0; }
  .video-copy { max-width: 44%; }
  .home-split { grid-template-columns: 1fr; }
  .home-side { position: static; display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
  .news-grid { grid-template-columns: 1fr; }
  .news-grid .news-card { grid-template-columns: 170px 1fr; }
  .news-grid .news-card img { height: 120px; }
}
@media (max-width: 760px) {
  .video-stage { top: 0; right: 0; width: 54%; height: 100%; opacity: 1; }
  .video-copy { max-width: 40%; }
  .video-copy h1 { font-size: 30px; }
  .video-copy p { font-size: 12px; }
  .hero-copy { max-width: 100%; }
  .home-hero { padding: 0 15px; margin-top: 15px; }
  .hero-slide, .hero-fallback { padding: 38px 26px; }
  .hero-copy h1 { font-size: 38px; }
  .stats-strip strong { font-size: 20px; }
  .stats-strip span { font-size: 10px; }
  .home-side { grid-template-columns: 1fr; }
  .news-grid .news-card { grid-template-columns: 115px 1fr; }
  .news-grid .news-card img { height: 105px; }
}
@media (max-width: 520px) {
  .video-stage { top: 0; right: 0; width: 55%; height: 100%; opacity: 1; }
  .video-copy { max-width: 42%; }
  .video-copy h1 { font-size: 26px; }
  .video-sound-btn, .video-sound-state { left: 9px; bottom: 9px; padding: 6px 9px; font-size: 9px; }
  .video-badge { right: 8px; bottom: 9px; padding: 5px 8px; font-size: 9px; }
  .hero-copy h1 { font-size: 32px; }
  .hero-copy p { font-size: 13px; }
  .stats-strip { padding: 16px 8px; }
  .stats-strip strong { font-size: 17px; }
}
</style>




