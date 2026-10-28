const now = Date.now()
const daysAgo = (days) => new Date(now - days * 86400000).toISOString()

const state = {
  users: [
    { id: 1, username: 'admin', nickname: '兔南管理员', avatar: '', bio: '守护每一份同城热爱', city: '江南', role: 'ADMIN', status: 1, createdAt: daysAgo(90) },
    { id: 2, username: 'xiaonan', nickname: '小南同学', avatar: '', bio: '喜欢逛吃、拍照，也喜欢认识新朋友。', city: '江南', role: 'USER', status: 1, createdAt: daysAgo(42) },
    { id: 3, username: 'agua', nickname: '阿瓜', avatar: '', bio: '城市观察员，记录街角的小美好。', city: '江南', role: 'USER', status: 1, createdAt: daysAgo(23) },
    { id: 4, username: 'chengzi', nickname: '橙子汽水', avatar: '', bio: '周末骑行，工作日认真生活。', city: '江南', role: 'USER', status: 1, createdAt: daysAgo(11) }
  ],
  follows: [
    { id: 1, followerId: 2, followingId: 3, createdAt: daysAgo(8) },
    { id: 2, followerId: 3, followingId: 2, createdAt: daysAgo(7) },
    { id: 3, followerId: 4, followingId: 2, createdAt: daysAgo(4) }
  ],
  circles: [
    { id: 1, name: '兔南美食研究所', cover: '/images/jiangnan/zhouzhuang-boat.jpg', description: '从街边小馆到宝藏甜品店，一起把江南吃明白。', auditStatus: 1, memberCount: 328, postCount: 12, createdBy: 2, createdAt: daysAgo(60) },
    { id: 2, name: '周末去哪儿', cover: '/images/jiangnan/wuzhen-river.jpg', description: '露营、徒步、展览、骑行，承包你的每个周末。', auditStatus: 1, memberCount: 216, postCount: 8, createdBy: 3, createdAt: daysAgo(48) },
    { id: 3, name: '萌宠碰头会', cover: '/images/jiangnan/zhouzhuang-canal.jpg', description: '分享毛孩子的日常，不定期组织线下遛宠。', auditStatus: 1, memberCount: 154, postCount: 5, createdBy: 4, createdAt: daysAgo(35) },
    { id: 4, name: '新晋摄影联盟', cover: '/images/jiangnan/jiangnan-garden.jpg', description: '用镜头记录水乡，每周一个拍摄主题。', auditStatus: 0, memberCount: 0, postCount: 0, createdBy: 2, createdAt: daysAgo(2) }
  ],
  topics: [
    { id: 1, name: '江南的秋天', cover: '/images/jiangnan/xitang-dusk-1.jpg', description: '晒出你镜头下最有秋日氛围的江南瞬间。', auditStatus: 1, heat: 9821, postCount: 16, createdBy: 2, createdAt: daysAgo(50) },
    { id: 2, name: '今天吃什么', cover: '/images/jiangnan/tuisi-garden.jpg', description: '拒绝选择困难，分享你的今日菜单。', auditStatus: 1, heat: 7632, postCount: 24, createdBy: 3, createdAt: daysAgo(40) },
    { id: 3, name: '城市夜跑搭子', cover: '/images/jiangnan/suzhou-canal.jpg', description: '安全约跑，互相监督，一起变强。', auditStatus: 1, heat: 4510, postCount: 9, createdBy: 4, createdAt: daysAgo(26) },
    { id: 4, name: '旧物交换计划', cover: '/images/jiangnan/qibao-canal.jpg', description: '让闲置找到新主人。', auditStatus: 0, heat: 120, postCount: 1, createdBy: 2, createdAt: daysAgo(3) }
  ],
  banners: [
    { id: 1, title: '烟雨江南 · 发现兔南的 100 种生活方式', imageUrl: '/images/jiangnan/qibao-canal-hero.jpg', linkUrl: '/circles', sort: 1, status: 1, createdAt: daysAgo(10) },
    { id: 2, title: '一城水巷 · 周末去吹江南的风', imageUrl: '/images/jiangnan/wuzhen-aerial-hero.jpg', linkUrl: '/topics', sort: 2, status: 1, createdAt: daysAgo(9) },
    { id: 3, title: '小桥流水 · 把江南故事讲给更多人听', imageUrl: '/images/jiangnan/zhouzhuang-lanterns-hero.jpg', linkUrl: '/news', sort: 3, status: 1, createdAt: daysAgo(8) }
  ],
  news: [
    { id: 1, title: '江南水巷步道全新开放，夜游地图请收好', summary: '全长 8.6 公里的水巷步道完成焕新，新增饮水点和夜间照明。', content: '这个周末，江南水巷步道正式向市民开放。\n\n此次升级新增了 4 处直饮水点、3 个便民驿站和全线夜间照明。步道分为慢行段、跑步段和亲子段，沿途还设置了多处观景平台。\n\n建议大家错峰前往，骑行请减速礼让行人，共同维护舒适的公共空间。', cover: '/images/jiangnan/xitang-dusk-2.jpg', city: '江南', status: 1, viewCount: 2381, createdAt: daysAgo(6) },
    { id: 2, title: '本周末江南市集指南：咖啡、手作与现场音乐', summary: '两天限定的秋日市集将在水乡老街举行，60 余家摊主参与。', content: '本周末 10:00-20:00，江南秋日市集将在水乡老街举行。\n\n现场包含精品咖啡、独立手作、旧物交换和本地音乐人演出。活动免费入场，建议乘坐公共交通前往。', cover: '/images/jiangnan/wuzhen-river.jpg', city: '江南', status: 1, viewCount: 1642, createdAt: daysAgo(4) },
    { id: 3, title: '水乡公交 17 路优化调整，串联三个热门生活圈', summary: '自下周一起，17 路新增两处站点，运营时间同步延长。', content: '为方便市民出行，自下周一起，公交 17 路将进行线路优化。\n\n新增“滨河公园东门”和“创意园北门”两站，末班车时间延长至 22:30。', cover: '/images/jiangnan/suzhou-canal.jpg', city: '江南', status: 1, viewCount: 905, createdAt: daysAgo(2) }
  ],
  posts: [
    { id: 1, title: '在江南水巷发现了这家藏在巷子里的面包店', content: '早起散步时被黄油香气带进了一条小巷。店面不大，玻璃柜里摆着刚出炉的可颂和盐面包，老板娘会记得每位熟客的口味。\n\n推荐：海盐可颂、桂花乳酪贝果。下午两点后通常会售罄，想去的朋友记得早点。', images: ['/images/jiangnan/zhouzhuang-lanterns.jpg'], circleId: 1, topicId: 2, authorId: 2, auditStatus: 1, likeCount: 36, favoriteCount: 18, commentCount: 3, viewCount: 428, createdAt: daysAgo(5) },
    { id: 2, title: '水乡的落日，值得专门跑一趟', content: '傍晚六点半从东门进去，沿着河走十五分钟就能看到最好的角度。今天云层很薄，天空从橘色慢慢变成蓝紫色。\n\n建议带一件薄外套，河边风很大。', images: ['/images/jiangnan/xitang-dusk-1.jpg'], circleId: 2, topicId: 1, authorId: 3, auditStatus: 1, likeCount: 52, favoriteCount: 24, commentCount: 2, viewCount: 691, createdAt: daysAgo(4) },
    { id: 3, title: '周三夜跑 5 公里，有没有一起的？', content: '路线：体育中心北门集合，沿滨河步道到白桥折返，配速 6分30左右。\n\n安全第一，不竞速，结束后可以一起吃碗热汤面。', images: ['/images/jiangnan/wuzhen-river.jpg'], circleId: 2, topicId: 3, authorId: 4, auditStatus: 1, likeCount: 19, favoriteCount: 6, commentCount: 2, viewCount: 203, createdAt: daysAgo(3) },
    { id: 4, title: '我家猫主子的新纸箱城堡', content: '快递箱不要扔，剪几个洞就是猫猫快乐屋。它已经在里面住了一下午，连饭都不肯出来吃。', images: ['/images/jiangnan/zhouzhuang-canal.jpg'], circleId: 3, topicId: 1, authorId: 4, auditStatus: 1, likeCount: 28, favoriteCount: 11, commentCount: 1, viewCount: 335, createdAt: daysAgo(2) },
    { id: 5, title: '旧相机交换：一台九成新的胶片机', content: '想换一台轻便的数码相机，也可以直接出。相机功能正常，送两卷胶卷。仅限同城面交。', images: ['/images/jiangnan/suzhou-canal.jpg'], circleId: 4, topicId: 4, authorId: 2, auditStatus: 0, likeCount: 2, favoriteCount: 1, commentCount: 0, viewCount: 17, createdAt: daysAgo(1) }
  ],
  comments: [
    { id: 1, postId: 1, userId: 3, parentId: null, content: '这家我也去过！桂花贝果真的香。', status: 1, createdAt: daysAgo(4) },
    { id: 2, postId: 1, userId: 4, parentId: null, content: '求一个具体地址，周末想去打卡。', status: 1, createdAt: daysAgo(3) },
    { id: 3, postId: 1, userId: 2, parentId: 2, content: '老槐树巷往里走 50 米，门口有块绿色小招牌。', status: 1, createdAt: daysAgo(3) },
    { id: 4, postId: 2, userId: 2, parentId: null, content: '这个配色也太美了，明天就去。', status: 1, createdAt: daysAgo(2) },
    { id: 5, postId: 2, userId: 4, parentId: null, content: '请问骑车能进去吗？', status: 1, createdAt: daysAgo(2) },
    { id: 6, postId: 3, userId: 2, parentId: null, content: '想参加，不过我只能跑 7 分配速可以吗？', status: 1, createdAt: daysAgo(1) },
    { id: 7, postId: 3, userId: 4, parentId: 6, content: '当然可以，我们按最舒服的节奏跑。', status: 1, createdAt: daysAgo(1) },
    { id: 8, postId: 4, userId: 3, parentId: null, content: '猫猫：这个家终于是我的了。', status: 1, createdAt: daysAgo(1) }
  ],
  likes: [
    { id: 1, postId: 1, userId: 3, createdAt: daysAgo(3) }, { id: 2, postId: 1, userId: 4, createdAt: daysAgo(2) },
    { id: 3, postId: 2, userId: 2, createdAt: daysAgo(2) }, { id: 4, postId: 3, userId: 2, createdAt: daysAgo(1) },
    { id: 5, postId: 4, userId: 2, createdAt: daysAgo(1) }, { id: 6, postId: 4, userId: 3, createdAt: daysAgo(1) }
  ],
  favorites: [
    { id: 1, postId: 1, userId: 3, createdAt: daysAgo(2) }, { id: 2, postId: 2, userId: 2, createdAt: daysAgo(2) },
    { id: 3, postId: 2, userId: 4, createdAt: daysAgo(1) }, { id: 4, postId: 4, userId: 2, createdAt: daysAgo(1) }
  ]
}

let nextUserId = 10
let nextCircleId = 10
let nextTopicId = 10
let nextBannerId = 10
let nextNewsId = 10
let nextPostId = 10
let nextCommentId = 20
let nextInteractionId = 20

const ok = (data = null) => ({ code: 200, message: 'ok', data })
const fail = (code, message) => ({ code, message, data: null })
const clone = (value) => JSON.parse(JSON.stringify(value))
const pageOf = (items, params = {}) => {
  const page = Math.max(1, Number(params.page || 1))
  const size = Math.max(1, Number(params.size || 10))
  return { total: items.length, records: clone(items.slice((page - 1) * size, page * size)) }
}
const includes = (value, keyword) => !keyword || String(value || '').toLowerCase().includes(String(keyword).toLowerCase())
const userById = (id) => state.users.find((item) => item.id === Number(id))
const circleById = (id) => state.circles.find((item) => item.id === Number(id))
const topicById = (id) => state.topics.find((item) => item.id === Number(id))
const postById = (id) => state.posts.find((item) => item.id === Number(id))

function currentUser(config) {
  const raw = config.headers?.Authorization || config.headers?.get?.('Authorization') || ''
  const match = String(raw).match(/demo-(\d+)/)
  return match ? userById(match[1]) : null
}

function postVO(post, viewer) {
  const author = userById(post.authorId)
  const circle = circleById(post.circleId)
  const topic = topicById(post.topicId)
  return {
    ...clone(post),
    circleName: circle?.name || '',
    topicName: topic?.name || '',
    authorName: author?.nickname || '江南用户',
    authorAvatar: author?.avatar || '',
    liked: Boolean(viewer && state.likes.some((item) => item.postId === post.id && item.userId === viewer.id)),
    favorited: Boolean(viewer && state.favorites.some((item) => item.postId === post.id && item.userId === viewer.id))
  }
}

function commentVO(comment) {
  const user = userById(comment.userId)
  return {
    ...clone(comment),
    postTitle: postById(comment.postId)?.title || '帖子',
    userName: user?.nickname || '江南用户',
    userAvatar: user?.avatar || ''
  }
}

function interactionVO(item) {
  const user = userById(item.userId)
  return {
    ...clone(item),
    postTitle: postById(item.postId)?.title || '帖子',
    userName: user?.nickname || '江南用户'
  }
}

function route(config) {
  const method = String(config.method || 'get').toLowerCase()
  const parsed = new URL(config.url || '/', 'https://demo.local')
  const path = parsed.pathname
  const params = config.params || {}
  const body = typeof config.data === 'string' ? JSON.parse(config.data || '{}') : (config.data || {})
  const viewer = currentUser(config)
  let match

  if (method === 'post' && path === '/auth/login') {
    const user = state.users.find((item) => item.username === body.username)
    if (!user || body.password !== '123456') return fail(400, '演示账号或密码不正确')
    return ok({ token: `demo-${user.id}`, user: clone(user) })
  }
  if (method === 'post' && path === '/auth/register') {
    if (state.users.some((item) => item.username === body.username)) return fail(409, '该账号已被注册')
    const user = { id: nextUserId++, username: body.username, nickname: body.nickname || body.username, avatar: '', bio: '这个用户还没有写下个人简介。', city: '江南', role: 'USER', status: 1, createdAt: new Date().toISOString() }
    state.users.push(user)
    return ok({ token: `demo-${user.id}`, user: clone(user) })
  }
  if (method === 'get' && path === '/users/me') return viewer ? ok(clone(viewer)) : fail(401, '请先登录')
  if (method === 'put' && path === '/users/me') {
    if (!viewer) return fail(401, '请先登录')
    Object.assign(viewer, { nickname: body.nickname || viewer.nickname, avatar: body.avatar ?? viewer.avatar, bio: body.bio ?? viewer.bio, city: body.city || viewer.city })
    return ok(clone(viewer))
  }
  if (method === 'get' && path === '/users/batch') {
    const ids = String(params.ids || '').split(',').map(Number)
    return ok(clone(state.users.filter((item) => ids.includes(item.id))))
  }
  if (method === 'get' && (match = path.match(/^\/users\/(\d+)$/))) {
    const user = userById(match[1])
    return user ? ok(clone(user)) : fail(404, '用户不存在')
  }
  if (method === 'get' && path === '/admin/users') {
    if (!viewer || viewer.role !== 'ADMIN') return fail(403, '无管理权限')
    let items = state.users.filter((item) => includes(item.username, params.keyword) || includes(item.nickname, params.keyword))
    if (params.status !== undefined && params.status !== null && params.status !== '') items = items.filter((item) => item.status === Number(params.status))
    return ok(pageOf(items, params))
  }
  if (method === 'put' && (match = path.match(/^\/admin\/users\/(\d+)\/status$/))) {
    if (!viewer || viewer.role !== 'ADMIN') return fail(403, '无管理权限')
    const user = userById(match[1])
    if (!user) return fail(404, '用户不存在')
    user.status = Number(body.status)
    return ok()
  }
  if (method === 'get' && path === '/admin/users/stats') return ok({ totalUsers: state.users.length, activeUsers: state.users.filter((item) => item.status === 1).length, todayNew: 1, totalFollows: state.follows.length })

  if (method === 'get' && (match = path.match(/^\/follows\/(\d+)\/status$/))) return ok({ following: Boolean(viewer && state.follows.some((item) => item.followerId === viewer.id && item.followingId === Number(match[1]))) })
  if (method === 'post' && (match = path.match(/^\/follows\/(\d+)$/))) {
    if (!viewer) return fail(401, '请先登录')
    const target = Number(match[1])
    if (!state.follows.some((item) => item.followerId === viewer.id && item.followingId === target)) state.follows.push({ id: nextInteractionId++, followerId: viewer.id, followingId: target, createdAt: new Date().toISOString() })
    return ok()
  }
  if (method === 'delete' && (match = path.match(/^\/follows\/(\d+)$/))) {
    if (!viewer) return fail(401, '请先登录')
    state.follows = state.follows.filter((item) => !(item.followerId === viewer.id && item.followingId === Number(match[1])))
    return ok()
  }
  if (method === 'get' && (path === '/follows/me/following' || path === '/follows/me/followers')) {
    if (!viewer) return fail(401, '请先登录')
    const following = path.endsWith('following')
    const records = state.follows.filter((item) => following ? item.followerId === viewer.id : item.followingId === viewer.id).map((item) => {
      const user = userById(following ? item.followingId : item.followerId)
      return { id: user.id, nickname: user.nickname, avatar: user.avatar, bio: user.bio, city: user.city, followedAt: item.createdAt }
    })
    return ok(clone(records))
  }

  if (method === 'get' && path === '/circles') return ok(pageOf(state.circles.filter((item) => item.auditStatus === 1 && includes(item.name, params.keyword)), params))
  if (method === 'get' && path === '/circles/mine') return viewer ? ok(clone(state.circles.filter((item) => item.createdBy === viewer.id))) : fail(401, '请先登录')
  if (method === 'post' && path === '/circles') {
    if (!viewer) return fail(401, '请先登录')
    const item = { id: nextCircleId++, name: body.name, cover: body.cover || '/images/jiangnan/qibao-canal.jpg', description: body.description || '', auditStatus: 0, memberCount: 0, postCount: 0, createdBy: viewer.id, createdAt: new Date().toISOString() }
    state.circles.push(item)
    return ok(clone(item))
  }
  if (method === 'get' && (match = path.match(/^\/circles\/(\d+)$/))) { const item = circleById(match[1]); return item ? ok(clone(item)) : fail(404, '圈子不存在') }
  if (method === 'put' && (match = path.match(/^\/circles\/(\d+)$/))) { const item = circleById(match[1]); if (!item || !viewer) return fail(404, '圈子不存在'); Object.assign(item, { name: body.name, cover: body.cover || item.cover, description: body.description || '', auditStatus: viewer.role === 'ADMIN' ? item.auditStatus : 0 }); return ok(clone(item)) }
  if (method === 'delete' && (match = path.match(/^\/circles\/(\d+)$/))) { state.circles = state.circles.filter((item) => item.id !== Number(match[1])); return ok() }
  if (method === 'get' && path === '/admin/circles') { let items = state.circles.filter((item) => includes(item.name, params.keyword)); if (params.auditStatus !== undefined && params.auditStatus !== null && params.auditStatus !== '') items = items.filter((item) => item.auditStatus === Number(params.auditStatus)); return ok(pageOf(items, params)) }
  if (method === 'put' && (match = path.match(/^\/admin\/circles\/(\d+)\/audit$/))) { const item = circleById(match[1]); if (item) item.auditStatus = Number(body.auditStatus); return ok() }

  if (method === 'get' && path === '/topics') return ok(pageOf(state.topics.filter((item) => item.auditStatus === 1 && includes(item.name, params.keyword)), params))
  if (method === 'get' && path === '/topics/mine') return viewer ? ok(clone(state.topics.filter((item) => item.createdBy === viewer.id))) : fail(401, '请先登录')
  if (method === 'post' && path === '/topics') { if (!viewer) return fail(401, '请先登录'); const item = { id: nextTopicId++, name: body.name, cover: body.cover || '/images/jiangnan/tuisi-garden.jpg', description: body.description || '', auditStatus: 0, heat: 0, postCount: 0, createdBy: viewer.id, createdAt: new Date().toISOString() }; state.topics.push(item); return ok(clone(item)) }
  if (method === 'get' && (match = path.match(/^\/topics\/(\d+)$/))) { const item = topicById(match[1]); return item ? ok(clone(item)) : fail(404, '话题不存在') }
  if (method === 'put' && (match = path.match(/^\/topics\/(\d+)$/))) { const item = topicById(match[1]); if (item) Object.assign(item, { name: body.name, cover: body.cover || item.cover, description: body.description || '', auditStatus: viewer?.role === 'ADMIN' ? item.auditStatus : 0 }); return ok(clone(item)) }
  if (method === 'delete' && (match = path.match(/^\/topics\/(\d+)$/))) { state.topics = state.topics.filter((item) => item.id !== Number(match[1])); return ok() }
  if (method === 'get' && path === '/admin/topics') { let items = state.topics.filter((item) => includes(item.name, params.keyword)); if (params.auditStatus !== undefined && params.auditStatus !== null && params.auditStatus !== '') items = items.filter((item) => item.auditStatus === Number(params.auditStatus)); return ok(pageOf(items, params)) }
  if (method === 'put' && (match = path.match(/^\/admin\/topics\/(\d+)\/audit$/))) { const item = topicById(match[1]); if (item) item.auditStatus = Number(body.auditStatus); return ok() }

  if (method === 'get' && path === '/banners') return ok(clone(state.banners.filter((item) => item.status === 1).sort((a, b) => a.sort - b.sort)))
  if (method === 'get' && path === '/admin/banners') return ok(pageOf(state.banners, params))
  if (method === 'post' && path === '/admin/banners') { const item = { id: nextBannerId++, ...body, createdAt: new Date().toISOString() }; state.banners.push(item); return ok(clone(item)) }
  if (method === 'put' && (match = path.match(/^\/admin\/banners\/(\d+)$/))) { const item = state.banners.find((row) => row.id === Number(match[1])); if (item) Object.assign(item, body); return ok(clone(item)) }
  if (method === 'delete' && (match = path.match(/^\/admin\/banners\/(\d+)$/))) { state.banners = state.banners.filter((item) => item.id !== Number(match[1])); return ok() }

  if (method === 'get' && path === '/news') return ok(pageOf(state.news.filter((item) => item.status === 1 && includes(item.title, params.keyword)), params))
  if (method === 'get' && (match = path.match(/^\/news\/(\d+)$/))) { const item = state.news.find((row) => row.id === Number(match[1])); if (item) item.viewCount += 1; return item ? ok(clone(item)) : fail(404, '资讯不存在') }
  if (method === 'get' && path === '/admin/news') return ok(pageOf(state.news.filter((item) => includes(item.title, params.keyword)), params))
  if (method === 'post' && path === '/admin/news') { const item = { id: nextNewsId++, ...body, viewCount: 0, createdAt: new Date().toISOString() }; state.news.push(item); return ok(clone(item)) }
  if (method === 'put' && (match = path.match(/^\/admin\/news\/(\d+)$/))) { const item = state.news.find((row) => row.id === Number(match[1])); if (item) Object.assign(item, body); return ok(clone(item)) }
  if (method === 'delete' && (match = path.match(/^\/admin\/news\/(\d+)$/))) { state.news = state.news.filter((item) => item.id !== Number(match[1])); return ok() }

  if (method === 'get' && path === '/posts') { let items = state.posts.filter((item) => item.auditStatus === 1 && (includes(item.title, params.keyword) || includes(item.content, params.keyword))); if (params.circleId) items = items.filter((item) => item.circleId === Number(params.circleId)); if (params.topicId) items = items.filter((item) => item.topicId === Number(params.topicId)); const page = pageOf(items, params); page.records = page.records.map((item) => postVO(item, viewer)); return ok(page) }
  if (method === 'get' && path === '/posts/mine') { if (!viewer) return fail(401, '请先登录'); const page = pageOf(state.posts.filter((item) => item.authorId === viewer.id), params); page.records = page.records.map((item) => postVO(item, viewer)); return ok(page) }
  if (method === 'post' && path === '/posts') { if (!viewer) return fail(401, '请先登录'); const item = { id: nextPostId++, title: body.title, content: body.content, images: body.images || [], circleId: body.circleId || null, topicId: body.topicId || null, authorId: viewer.id, auditStatus: 0, likeCount: 0, favoriteCount: 0, commentCount: 0, viewCount: 0, createdAt: new Date().toISOString() }; state.posts.push(item); return ok(postVO(item, viewer)) }
  if (method === 'get' && (match = path.match(/^\/admin\/posts\/(\d+)$/))) { const item = postById(match[1]); return item ? ok(postVO(item, viewer)) : fail(404, '帖子不存在') }
  if (method === 'get' && path === '/admin/posts') { let items = state.posts.filter((item) => includes(item.title, params.keyword) || includes(item.content, params.keyword)); if (params.auditStatus !== undefined && params.auditStatus !== null && params.auditStatus !== '') items = items.filter((item) => item.auditStatus === Number(params.auditStatus)); const page = pageOf(items, params); page.records = page.records.map((item) => postVO(item, viewer)); return ok(page) }
  if (method === 'put' && (match = path.match(/^\/admin\/posts\/(\d+)\/audit$/))) { const item = postById(match[1]); if (item) item.auditStatus = Number(body.auditStatus); return ok() }
  if (method === 'delete' && (match = path.match(/^\/admin\/posts\/(\d+)$/))) { state.posts = state.posts.filter((item) => item.id !== Number(match[1])); return ok() }
  if (method === 'get' && (match = path.match(/^\/posts\/(\d+)$/))) { const item = postById(match[1]); if (!item) return fail(404, '帖子不存在'); if (item.auditStatus !== 1 && item.authorId !== viewer?.id && viewer?.role !== 'ADMIN') return fail(404, '帖子不存在或仍在审核中'); item.viewCount += 1; return ok(postVO(item, viewer)) }
  if (method === 'put' && (match = path.match(/^\/posts\/(\d+)$/))) { const item = postById(match[1]); if (!item || !viewer) return fail(404, '帖子不存在'); Object.assign(item, { title: body.title, content: body.content, images: body.images || [], circleId: body.circleId || null, topicId: body.topicId || null, auditStatus: viewer.role === 'ADMIN' ? item.auditStatus : 0 }); return ok(postVO(item, viewer)) }
  if (method === 'delete' && (match = path.match(/^\/posts\/(\d+)$/))) { state.posts = state.posts.filter((item) => item.id !== Number(match[1])); return ok() }

  if (method === 'get' && (match = path.match(/^\/comments\/post\/(\d+)$/))) return ok(clone(state.comments.filter((item) => item.postId === Number(match[1]) && item.status === 1).map(commentVO)))
  if (method === 'post' && path === '/comments') { if (!viewer) return fail(401, '请先登录'); const item = { id: nextCommentId++, postId: Number(body.postId), userId: viewer.id, parentId: body.parentId || null, content: body.content, status: 1, createdAt: new Date().toISOString() }; state.comments.push(item); const post = postById(item.postId); if (post) post.commentCount += 1; return ok(commentVO(item)) }
  if (method === 'get' && path === '/comments/mine') { if (!viewer) return fail(401, '请先登录'); const page = pageOf(state.comments.filter((item) => item.userId === viewer.id), params); page.records = page.records.map(commentVO); return ok(page) }
  if (method === 'delete' && (match = path.match(/^\/comments\/(\d+)$/))) { const item = state.comments.find((row) => row.id === Number(match[1])); if (item) { state.comments = state.comments.filter((row) => row.id !== item.id); const post = postById(item.postId); if (post) post.commentCount = Math.max(0, post.commentCount - 1) } return ok() }
  if (method === 'get' && path === '/admin/comments') { const page = pageOf(state.comments.filter((item) => includes(item.content, params.keyword)), params); page.records = page.records.map(commentVO); return ok(page) }
  if (method === 'delete' && (match = path.match(/^\/admin\/comments\/(\d+)$/))) { state.comments = state.comments.filter((item) => item.id !== Number(match[1])); return ok() }

  if (method === 'post' && (match = path.match(/^\/likes\/(\d+)$/))) { if (!viewer) return fail(401, '请先登录'); const postId = Number(match[1]); if (!state.likes.some((item) => item.postId === postId && item.userId === viewer.id)) { state.likes.push({ id: nextInteractionId++, postId, userId: viewer.id, createdAt: new Date().toISOString() }); const post = postById(postId); if (post) post.likeCount += 1 } return ok() }
  if (method === 'delete' && (match = path.match(/^\/likes\/(\d+)$/))) { if (!viewer) return fail(401, '请先登录'); const postId = Number(match[1]); const before = state.likes.length; state.likes = state.likes.filter((item) => !(item.postId === postId && item.userId === viewer.id)); if (state.likes.length < before) { const post = postById(postId); if (post) post.likeCount = Math.max(0, post.likeCount - 1) } return ok() }
  if (method === 'get' && path === '/likes/mine') { if (!viewer) return fail(401, '请先登录'); const ids = state.likes.filter((item) => item.userId === viewer.id).map((item) => item.postId); const items = state.posts.filter((item) => ids.includes(item.id)); const page = pageOf(items, params); page.records = page.records.map((item) => postVO(item, viewer)); return ok(page) }
  if (method === 'post' && (match = path.match(/^\/favorites\/(\d+)$/))) { if (!viewer) return fail(401, '请先登录'); const postId = Number(match[1]); if (!state.favorites.some((item) => item.postId === postId && item.userId === viewer.id)) { state.favorites.push({ id: nextInteractionId++, postId, userId: viewer.id, createdAt: new Date().toISOString() }); const post = postById(postId); if (post) post.favoriteCount += 1 } return ok() }
  if (method === 'delete' && (match = path.match(/^\/favorites\/(\d+)$/))) { if (!viewer) return fail(401, '请先登录'); const postId = Number(match[1]); const before = state.favorites.length; state.favorites = state.favorites.filter((item) => !(item.postId === postId && item.userId === viewer.id)); if (state.favorites.length < before) { const post = postById(postId); if (post) post.favoriteCount = Math.max(0, post.favoriteCount - 1) } return ok() }
  if (method === 'get' && path === '/favorites/mine') { if (!viewer) return fail(401, '请先登录'); const ids = state.favorites.filter((item) => item.userId === viewer.id).map((item) => item.postId); const items = state.posts.filter((item) => ids.includes(item.id)); const page = pageOf(items, params); page.records = page.records.map((item) => postVO(item, viewer)); return ok(page) }
  if (method === 'get' && path === '/admin/likes') return ok(pageOf(state.likes.map(interactionVO), params))
  if (method === 'get' && path === '/admin/favorites') return ok(pageOf(state.favorites.map(interactionVO), params))

  if (method === 'get' && path === '/admin/content/stats') return ok({ totalCircles: state.circles.length, totalTopics: state.topics.length, pendingCircles: state.circles.filter((item) => item.auditStatus === 0).length, pendingTopics: state.topics.filter((item) => item.auditStatus === 0).length, totalNews: state.news.filter((item) => item.status === 1).length })
  if (method === 'get' && path === '/admin/stats/overview') return ok({ totalPosts: state.posts.length, publishedPosts: state.posts.filter((item) => item.auditStatus === 1).length, pendingPosts: state.posts.filter((item) => item.auditStatus === 0).length, totalComments: state.comments.length, totalLikes: state.likes.length, totalFavorites: state.favorites.length, todayPosts: 1 })

  return fail(404, `演示接口不存在：${method.toUpperCase()} ${path}`)
}

export function createDemoAdapter() {
  return async (config) => {
    await new Promise((resolve) => setTimeout(resolve, 90))
    return { data: route(config), status: 200, statusText: 'OK', headers: {}, config, request: {} }
  }
}
