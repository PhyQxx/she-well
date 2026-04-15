<template>
  <div class="settings-page">
    <el-card shadow="hover"><template #header>👤 个人信息</template>
      <el-form label-width="80px">
        <el-form-item label="昵称"><el-input v-model="profile.nickname" /></el-form-item>
        <el-form-item label="生日"><el-date-picker v-model="profile.birthday" type="date" /></el-form-item>
        <el-form-item label="身高(cm)"><el-input v-model="profile.height" type="number" /></el-form-item>
        <el-form-item label="体重(kg)"><el-input v-model="profile.weight" type="number" /></el-form-item>
        <el-form-item label="血型"><el-select v-model="profile.bloodType"><el-option v-for="b in ['A','B','O','AB','未知']" :key="b" :label="b" :value="b" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="saveProfile">保存</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="hover" style="margin-top:16px"><template #header>⚙️ 应用设置</template>
      <el-form label-width="100px">
        <el-form-item label="经期提醒">
          <el-switch v-model="settings.reminderEnabled" />
          <el-input v-if="settings.reminderEnabled" v-model="settings.reminderTime" style="width:80px;margin-left:8px" placeholder="09:00" />
        </el-form-item>
        <el-form-item label="排卵日提醒"><el-switch v-model="settings.ovulReminder" /></el-form-item>
        <el-form-item label="推送通知"><el-switch v-model="settings.pushEnabled" /></el-form-item>
        <el-form-item label="主题">
          <el-radio-group v-model="settings.theme">
            <el-radio label="light">浅色</el-radio><el-radio label="dark">深色</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="saveSettings">保存设置</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const profile = ref<any>({})
const settings = ref<any>({})

function fmt(d: any) { return d ? new Date(d).toISOString().slice(0,10) : '' }

async function saveProfile() {
  await api.put('/user/profile', { ...profile.value, birthday: fmt(profile.value.birthday) })
  ElMessage.success('已保存')
}

async function saveSettings() {
  await api.put('/user/settings', settings.value)
  ElMessage.success('已保存')
}

onMounted(async () => {
  const [p, s] = await Promise.all([api.get('/user/profile'), api.get('/user/settings')])
  profile.value = p.data || {}
  settings.value = s.data || {}
})
</script>

<style scoped>
.settings-page { padding: 8px; max-width: 600px }
</style>
