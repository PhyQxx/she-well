<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">⚙️ 系统配置</span>
    </template>
    <el-form label-width="140px" style="max-width:600px">
      <el-divider content-position="left">AI 配置</el-divider>
      <el-form-item label="默认 AI 模型">
        <el-select v-model="config.aiProvider" style="width:100%">
          <el-option label="DeepSeek" value="deepseek" />
          <el-option label="智谱 GLM" value="glm" />
          <el-option label="MiniMax" value="minimax" />
        </el-select>
      </el-form-item>
      <el-form-item label="DeepSeek API URL">
        <el-input v-model="config.deepseekUrl" placeholder="https://api.deepseek.com/chat/completions" />
      </el-form-item>
      <el-form-item label="DeepSeek API Key">
        <el-input v-model="config.deepseekKey" type="password" show-password placeholder="sk-..." />
      </el-form-item>
      <el-form-item label="GLM API URL">
        <el-input v-model="config.glmUrl" placeholder="https://open.bigmodel.cn/api/paas/v4/chat/completions" />
      </el-form-item>
      <el-form-item label="GLM API Key">
        <el-input v-model="config.glmKey" type="password" show-password />
      </el-form-item>

      <el-divider content-position="left">FTP 配置</el-divider>
      <el-form-item label="FTP 服务器地址">
        <el-input v-model="config.ftpHost" placeholder="127.0.0.1" />
      </el-form-item>
      <el-form-item label="FTP 端口">
        <el-input v-model="config.ftpPort" placeholder="21" />
      </el-form-item>
      <el-form-item label="FTP 用户名">
        <el-input v-model="config.ftpUsername" />
      </el-form-item>
      <el-form-item label="FTP 密码">
        <el-input v-model="config.ftpPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="FTP 基础路径">
        <el-input v-model="config.ftpBasePath" placeholder="/" />
      </el-form-item>

      <el-divider content-position="left">其他配置</el-divider>
      <el-form-item label="是否开启注册">
        <el-switch v-model="config.enableRegister" />
      </el-form-item>
      <el-form-item label="强制实名认证">
        <el-switch v-model="config.forceRealAuth" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="saveConfig">保存配置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const config = ref({
  aiProvider: 'deepseek',
  deepseekUrl: 'https://api.deepseek.com/chat/completions',
  deepseekKey: '',
  glmUrl: 'https://open.bigmodel.cn/api/paas/v4/chat/completions',
  glmKey: '',
  ftpHost: '127.0.0.1',
  ftpPort: '21',
  ftpUsername: 'anonymous',
  ftpPassword: '',
  ftpBasePath: '/',
  enableRegister: true,
  forceRealAuth: false,
})

function saveConfig() {
  ElMessage.success('配置已保存')
}
</script>
