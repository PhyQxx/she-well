<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">📖 文章管理</span>
      <el-button type="primary" size="small" style="float:right" @click="openDialog()">+ 新建文章</el-button>
    </template>

    <el-form :inline="true" style="margin-bottom:12px">
      <el-form-item label="分类">
        <el-select v-model="filters.categoryId" placeholder="全部" clearable style="width:120px">
          <el-option label="经期护理" :value="1" />
          <el-option label="备孕指南" :value="2" />
          <el-option label="孕期保健" :value="3" />
          <el-option label="产后恢复" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button @click="loadData">搜索</el-button></el-form-item>
    </el-form>

    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="categoryId" label="分类" width="100" />
      <el-table-column prop="viewCount" label="阅读" width="80" />
      <el-table-column prop="likeCount" label="点赞" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteRow(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination layout="total, prev, pager, next" :total="total" :page-size="20" v-model:current-page="page" @current-change="loadData" style="margin-top:16px;justify-content:center" />

    <el-dialog v-model="showDialog" :title="form.id ? '编辑文章' : '新建文章'" width="700px" top="5vh">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" style="width:100%">
            <el-option label="经期护理" :value="1" />
            <el-option label="备孕指南" :value="2" />
            <el-option label="孕期保健" :value="3" />
            <el-option label="产后恢复" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="8" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">发布</el-radio>
            <el-radio :label="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getArticleList, createArticle, updateArticle, deleteArticle } from '@/api'

const list = ref([])
const loading = ref(false)
const showDialog = ref(false)
const page = ref(1)
const total = ref(0)
const filters = ref({ categoryId: null })
const form = ref({ id: null, title: '', categoryId: null, summary: '', content: '', status: 1 })

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, size: 20 }
    if (filters.value.categoryId) params.categoryId = filters.value.categoryId
    const res = await getArticleList(params)
    list.value = res.data || []
    total.value = list.value.length
  } catch { ElMessage.error('加载文章列表失败') }
  finally { loading.value = false }
}

function openDialog(row) {
  form.value = row ? { ...row } : { id: null, title: '', categoryId: null, summary: '', content: '', status: 1 }
  showDialog.value = true
}

async function submitForm() {
  if (!form.value.title) { ElMessage.warning('请填写标题'); return }
  try {
    if (form.value.id) {
      await updateArticle(form.value.id, form.value)
    } else {
      await createArticle(form.value)
    }
    showDialog.value = false
    ElMessage.success('保存成功')
    loadData()
  } catch { ElMessage.error('保存失败') }
}

async function deleteRow(row) {
  await ElMessageBox.confirm('确认删除该文章？', '提示')
  try {
    await deleteArticle(row.id)
    ElMessage.success('已删除')
    loadData()
  } catch { ElMessage.error('删除失败') }
}

onMounted(() => loadData())
</script>
