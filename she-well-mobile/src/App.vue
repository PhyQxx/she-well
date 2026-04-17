<script setup>
import { ref, watch } from 'vue'

const currentMode = ref('period')

// 模式对应的 TabBar 定制
const modeTabBars = {
  period: [
    { text: '首页', icon: '🏠' },
    { text: '经期', icon: '📅' },
    { text: '打卡', icon: '✅' },
    { text: '我的', icon: '👤' },
  ],
  trying: [
    { text: '首页', icon: '🏠' },
    { text: '备孕', icon: '🤰' },
    { text: '打卡', icon: '✅' },
    { text: '我的', icon: '👤' },
  ],
  pregnancy: [
    { text: '首页', icon: '🏠' },
    { text: '孕期', icon: '🤱' },
    { text: '打卡', icon: '✅' },
    { text: '我的', icon: '👤' },
  ],
  nursing: [
    { text: '首页', icon: '🏠' },
    { text: '宝宝', icon: '🍼' },
    { text: '打卡', icon: '✅' },
    { text: '我的', icon: '👤' },
  ],
}

function updateTabBar(mode) {
  const items = modeTabBars[mode] || modeTabBars['period']
  const tabNames = ['home', 'cal', 'check', 'mine']
  items.forEach((label, index) => {
    try {
      uni.setTabBarItem({
        index,
        text: label.text,
        iconPath: 'static/tab-' + tabNames[index] + '.png',
        selectedIconPath: 'static/tab-' + tabNames[index] + '-active.png',
      })
    } catch (e) {
      // H5 模式不支持 setTabBarItem
    }
  })
}

watch(currentMode, (newMode) => {
  uni.setStorageSync('sw_mode', newMode)
  updateTabBar(newMode)
})

// UniApp lifecycle - onLaunch
uni.onAppLaunch(() => {
  const savedMode = uni.getStorageSync('sw_mode')
  if (savedMode) {
    currentMode.value = savedMode
    updateTabBar(savedMode)
  }
  console.log('SheWell App Launched')
})
</script>

<style>
page { background: #FAF8FB; }
</style>
