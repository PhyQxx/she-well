<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">系统配置</span>
    </template>
    <el-form label-width="160px" style="max-width:640px">
      <el-divider content-position="left">AI 配置</el-divider>
      <el-form-item label="默认 AI 模型">
        <el-select v-model="config['ai.default_provider']" style="width:100%">
          <el-option label="DeepSeek" value="deepseek" />
          <el-option label="智谱 GLM" value="glm" />
          <el-option label="MiniMax" value="minimax" />
        </el-select>
      </el-form-item>

      <!-- DeepSeek -->
      <el-divider content-position="left">DeepSeek</el-divider>
      <el-form-item label="API URL">
        <el-input v-model="config['ai.deepseek.url']" placeholder="https://api.deepseek.com/chat/completions" />
      </el-form-item>
      <el-form-item label="API Key">
        <el-input v-model="config['ai.deepseek.api_key']" type="password" show-password placeholder="sk-..." />
      </el-form-item>
      <el-form-item label="模型名">
        <el-input v-model="config['ai.deepseek.model']" placeholder="deepseek-chat" />
      </el-form-item>
      <el-form-item label="Temperature">
        <el-input-number v-model="deepseekTemp" :min="0" :max="2" :step="0.1" :precision="1" />
      </el-form-item>
      <el-form-item label="Max Tokens">
        <el-input-number v-model="deepseekTokens" :min="100" :max="8000" :step="100" />
      </el-form-item>

      <!-- GLM -->
      <el-divider content-position="left">智谱 GLM</el-divider>
      <el-form-item label="API URL">
        <el-input v-model="config['ai.glm.url']" placeholder="https://open.bigmodel.cn/api/paas/v4/chat/completions" />
      </el-form-item>
      <el-form-item label="API Key">
        <el-input v-model="config['ai.glm.api_key']" type="password" show-password />
      </el-form-item>
      <el-form-item label="模型名">
        <el-input v-model="config['ai.glm.model']" placeholder="glm-4" />
      </el-form-item>
      <el-form-item label="Temperature">
        <el-input-number v-model="glmTemp" :min="0" :max="2" :step="0.1" :precision="1" />
      </el-form-item>
      <el-form-item label="Max Tokens">
        <el-input-number v-model="glmTokens" :min="100" :max="8000" :step="100" />
      </el-form-item>

      <!-- MiniMax -->
      <el-divider content-position="left">MiniMax</el-divider>
      <el-form-item label="API URL">
        <el-input v-model="config['ai.minimax.url']" placeholder="https://api.minimax.chat/v1/text/chatcompletion_v2" />
      </el-form-item>
      <el-form-item label="API Key">
        <el-input v-model="config['ai.minimax.api_key']" type="password" show-password />
      </el-form-item>
      <el-form-item label="模型名">
        <el-input v-model="config['ai.minimax.model']" placeholder="MiniMax-Text-01" />
      </el-form-item>
      <el-form-item label="Temperature">
        <el-input-number v-model="minimaxTemp" :min="0" :max="2" :step="0.1" :precision="1" />
      </el-form-item>
      <el-form-item label="Max Tokens">
        <el-input-number v-model="minimaxTokens" :min="100" :max="8000" :step="100" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="saving" @click="saveConfig">保存配置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getConfig, saveConfig as saveConfigApi } from '../api'

const config = ref({
  'ai.default_provider': 'deepseek',
  'ai.deepseek.url': '',
  'ai.deepseek.api_key': '',
  'ai.deepseek.model': '',
  'ai.deepseek.temperature': '0.7',
  'ai.deepseek.max_tokens': '2000',
  'ai.glm.url': '',
  'ai.glm.api_key': '',
  'ai.glm.model': '',
  'ai.glm.temperature': '0.7',
  'ai.glm.max_tokens': '2000',
  'ai.minimax.url': '',
  'ai.minimax.api_key': '',
  'ai.minimax.model': '',
  'ai.minimax.temperature': '0.7',
  'ai.minimax.max_tokens': '2000',
})

const saving = ref(false)

// 数值类型的双向绑定辅助
const toNum = (v) => v == null ? null : Number(v)
const deepseekTemp = computed({ get: () => toNum(config.value['ai.deepseek.temperature']), set: v => config.value['ai.deepseek.temperature'] = String(v) })
const deepseekTokens = computed({ get: () => toNum(config.value['ai.deepseek.max_tokens']), set: v => config.value['ai.deepseek.max_tokens'] = String(v) })
const glmTemp = computed({ get: () => toNum(config.value['ai.glm.temperature']), set: v => config.value['ai.glm.temperature'] = String(v) })
const glmTokens = computed({ get: () => toNum(config.value['ai.glm.max_tokens']), set: v => config.value['ai.glm.max_tokens'] = String(v) })
const minimaxTemp = computed({ get: () => toNum(config.value['ai.minimax.temperature']), set: v => config.value['ai.minimax.temperature'] = String(v) })
const minimaxTokens = computed({ get: () => toNum(config.value['ai.minimax.max_tokens']), set: v => config.value['ai.minimax.max_tokens'] = String(v) })

async function loadConfig() {
  try {
    const res = await getConfig()
    if (res.data) {
      Object.keys(config.value).forEach(key => {
        if (res.data[key] !== undefined) {
          config.value[key] = res.data[key]
        }
      })
    }
  } catch (e) {
    console.error('加载配置失败', e)
  }
}

async function saveConfig() {
  saving.value = true
  try {
    await saveConfigApi(config.value)
    ElMessage.success('配置已保存')
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(loadConfig)
</script>
