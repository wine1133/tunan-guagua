<template>
  <el-avatar :size="size" :src="src || undefined" class="user-avatar" :style="fallbackStyle">
    {{ letter }}
  </el-avatar>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  name: { type: String, default: '兔友' },
  src: { type: String, default: '' },
  size: { type: Number, default: 40 }
})

const letter = computed(() => (props.name || '兔').slice(0, 1))
const fallbackStyle = computed(() => {
  const colors = ['#ff7a59', '#2a7d74', '#efb64c', '#d45d79', '#5978b8']
  const index = (props.name || '兔').charCodeAt(0) % colors.length
  return { background: colors[index], color: '#fff', fontWeight: 700 }
})
</script>
