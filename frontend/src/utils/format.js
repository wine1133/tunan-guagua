export function formatTime(value) {
  if (!value) return ''
  const date = new Date(value)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  if (diff < 60 * 1000) return '刚刚'
  if (diff < 60 * 60 * 1000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 24 * 60 * 60 * 1000) return `${Math.floor(diff / 3600000)} 小时前`
  if (diff < 7 * 24 * 60 * 60 * 1000) return `${Math.floor(diff / 86400000)} 天前`
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

export function excerpt(text, length = 88) {
  if (!text) return ''
  const plain = text.replace(/\s+/g, ' ')
  return plain.length > length ? `${plain.slice(0, length)}...` : plain
}

export function auditText(status) {
  return { 0: '待审核', 1: '已通过', 2: '已驳回' }[status] || '未知'
}

export function auditType(status) {
  return { 0: 'warning', 1: 'success', 2: 'danger' }[status] || 'info'
}
