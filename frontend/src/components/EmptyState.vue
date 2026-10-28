<template>
  <div class="empty-state">
    <div class="empty-icon">{{ icon }}</div>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <slot />
  </div>
</template>

<script setup>
defineProps({
  icon: { type: String, default: '🌱' },
  title: { type: String, default: '这里还没有内容' },
  description: { type: String, default: '成为第一个留下足迹的人吧。' }
})
</script>
