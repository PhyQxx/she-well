<script setup>
import { ref, watch } from 'vue'

// 全局状态：当前模式（period/trying/pregnancy/nursing）
const currentMode = ref('period')
const tabBarItems = ref([
  { pagePath: 'pages/index/index', text: '首页', iconPath: 'static/tab-home.png', selectedIconPath: 'static/tab-home-active.png' },
  { pagePath: 'pages/period/index', text: '经期', iconPath: 'static/tab-cal.png', selectedIconPath: 'static/tab-cal-active.png' },
  { pagePath: 'pages/checkin/index', text: '打卡', iconPath: 'static/tab-check.png', selectedIconPath: 'static/tab-check-active.png' },
  { pagePath: 'pages/mine/mine', text: '我的', iconPath: 'static/tab-mine.png', selectedIconPath: 'static/tab-mine-active.png' },
])

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
  tabBarItems.value.forEach((item, index) => {
    const label = items[index]
    if (label) {
      try {
        uni.setTabBarItem({
          index,
          text: label.text,
          // 图标暂用文字替代（生产环境替换为真实图标路径）
          iconPath: 'static/tab-' + ['home', 'cal', 'check', 'mine'][index] + '.png',
          selectedIconPath: 'static/tab-' + ['home', 'cal', 'check', 'mine'][index] + '-active.png',
          success: () => console.log('tabBar updated:', label.text),
          fail: (e) => console.log('tabBar update failed', e),
        })
      } catch (e) {
        console.log('setTabBarItem not available in H5 mode')
      }
    }
  })
}

watch(currentMode, (newMode) => {
  uni.setStorageSync('sw_mode', newMode)
  updateTabBar(newMode)
})

// 监听模式切换事件（其他页面切换模式后通知首页）
uni.onAppRoute((res) => {
  // 空实现，供后续扩展
})
</script>

<script>
export default {
  onLaunch() {
    // 启动时读取保存的模式
    const savedMode = uni.getStorageSync('sw_mode')
    if (savedMode) {
      currentMode.mode = savedMode
      updateTabBar(savedMode)
    }
    console.log('SheWell App Launched')
  },
  onShow() {},
  onHide() {},
}
</script>

<style>
page { background: #faf8fb; }
</style>
