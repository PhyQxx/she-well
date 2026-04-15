<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">🎯 Banner管理</span>
      <el-button type="primary" size="small" style="float:right" @click="showDialog = true">+ 添加Banner</el-button>
    </template>
    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="imageUrl" label="图片" width="120">
        <template #default="{ row }">
          <el-image v-if="row.imageUrl" :src="row.imageUrl" style="width:80px;height:40px;border-radius:4px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="editRow(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteRow(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" :title="form.id ? '编辑Banner' : '添加Banner'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="图片">
          <el-upload action="/api/file/upload/post-image" :show-file-list="false" :on-success="onImgSuccess">
            <img v-if="form.imageUrl" :src="form.imageUrl" style="width:120px;height:60px;object-fit:cover;border-radius:4px" />
            <el-button v-else>选择图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="跳转链接"><el-input v-model="form.linkValue" placeholder="如：/pages/index 或 https://..." /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const showDialog = ref(false)
const form = ref({ id: null, title: '', imageUrl: '', linkValue: '', sortOrder: 0, status: 1 })

function editRow(row) { form.value = { ...row }; showDialog.value = true }
async function deleteRow(row) {
  await ElMessageBox.confirm('确认删除该Banner？', '提示')
  list.value = list.value.filter(i => i.id !== row.id)
  ElMessage.success('已删除')
}
function onImgSuccess(res) { form.value.imageUrl = res.data }
function submitForm() {
  if (!form.value.title) { ElMessage.warning('请填写标题'); return }
  if (form.value.id) {
    const idx = list.value.findIndex(i => i.id === form.value.id)
    if (idx > -1) list.value[idx] = { ...form.value }
  } else {
    list.value.unshift({ ...form.value, id: Date.now(), createTime: new Date().toISOString().slice(0, 16) })
  }
  showDialog.value = false
  ElMessage.success('保存成功')
}

// 演示数据
list.value = [
  { id: 1, title: '妇女节活动banner', imageUrl: '', linkValue: '/activity/1', sortOrder: 1, status: 1, createTime: '2026-03-01 10:00' },
  { id: 2, title: '经期健康知识专题', imageUrl: '', linkValue: '/knowledge/1', sortOrder: 2, status: 1, createTime: '2026-03-05 10:00' },
]
</script>
