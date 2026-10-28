import http from './http'

export const authApi = {
  login: (data) => http.post('/auth/login', data),
  register: (data) => http.post('/auth/register', data)
}

export const userApi = {
  me: () => http.get('/users/me'),
  detail: (id) => http.get(`/users/${id}`),
  updateMe: (data) => http.put('/users/me', data),
  adminList: (params) => http.get('/admin/users', { params }),
  updateStatus: (id, status) => http.put(`/admin/users/${id}/status`, { status }),
  stats: () => http.get('/admin/users/stats')
}

export const followApi = {
  follow: (id) => http.post(`/follows/${id}`),
  unfollow: (id) => http.delete(`/follows/${id}`),
  status: (id) => http.get(`/follows/${id}/status`),
  following: () => http.get('/follows/me/following'),
  followers: () => http.get('/follows/me/followers')
}

export const circleApi = {
  list: (params) => http.get('/circles', { params }),
  detail: (id) => http.get(`/circles/${id}`),
  mine: () => http.get('/circles/mine'),
  create: (data) => http.post('/circles', data),
  update: (id, data) => http.put(`/circles/${id}`, data),
  remove: (id) => http.delete(`/circles/${id}`),
  adminList: (params) => http.get('/admin/circles', { params }),
  audit: (id, auditStatus) => http.put(`/admin/circles/${id}/audit`, { auditStatus })
}

export const topicApi = {
  list: (params) => http.get('/topics', { params }),
  detail: (id) => http.get(`/topics/${id}`),
  mine: () => http.get('/topics/mine'),
  create: (data) => http.post('/topics', data),
  update: (id, data) => http.put(`/topics/${id}`, data),
  remove: (id) => http.delete(`/topics/${id}`),
  adminList: (params) => http.get('/admin/topics', { params }),
  audit: (id, auditStatus) => http.put(`/admin/topics/${id}/audit`, { auditStatus })
}

export const bannerApi = {
  list: () => http.get('/banners'),
  adminList: (params) => http.get('/admin/banners', { params }),
  create: (data) => http.post('/admin/banners', data),
  update: (id, data) => http.put(`/admin/banners/${id}`, data),
  remove: (id) => http.delete(`/admin/banners/${id}`)
}

export const newsApi = {
  list: (params) => http.get('/news', { params }),
  detail: (id) => http.get(`/news/${id}`),
  adminList: (params) => http.get('/admin/news', { params }),
  create: (data) => http.post('/admin/news', data),
  update: (id, data) => http.put(`/admin/news/${id}`, data),
  remove: (id) => http.delete(`/admin/news/${id}`)
}

export const postApi = {
  list: (params) => http.get('/posts', { params }),
  mine: (params) => http.get('/posts/mine', { params }),
  detail: (id) => http.get(`/posts/${id}`),
  create: (data) => http.post('/posts', data),
  update: (id, data) => http.put(`/posts/${id}`, data),
  remove: (id) => http.delete(`/posts/${id}`),
  adminList: (params) => http.get('/admin/posts', { params }),
  adminDetail: (id) => http.get(`/admin/posts/${id}`),
  audit: (id, auditStatus) => http.put(`/admin/posts/${id}/audit`, { auditStatus }),
  adminRemove: (id) => http.delete(`/admin/posts/${id}`)
}

export const commentApi = {
  byPost: (postId) => http.get(`/comments/post/${postId}`),
  create: (data) => http.post('/comments', data),
  remove: (id) => http.delete(`/comments/${id}`),
  mine: (params) => http.get('/comments/mine', { params }),
  adminList: (params) => http.get('/admin/comments', { params }),
  adminRemove: (id) => http.delete(`/admin/comments/${id}`)
}

export const interactionApi = {
  like: (postId) => http.post(`/likes/${postId}`),
  unlike: (postId) => http.delete(`/likes/${postId}`),
  myLikes: (params) => http.get('/likes/mine', { params }),
  favorite: (postId) => http.post(`/favorites/${postId}`),
  unfavorite: (postId) => http.delete(`/favorites/${postId}`),
  myFavorites: (params) => http.get('/favorites/mine', { params }),
  adminLikes: (params) => http.get('/admin/likes', { params }),
  adminFavorites: (params) => http.get('/admin/favorites', { params })
}

export const statsApi = {
  content: () => http.get('/admin/content/stats'),
  posts: () => http.get('/admin/stats/overview')
}
