<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">📖 文章管理</span>
      <el-button type="primary" size="small" style="float:right" @click="showDialog = true">+ 新建文章</el-button>
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
      <el-form-item label="状态">
        <el-select v-model="filters.status" placeholder="全部" clearable style="width:100px">
          <el-option label="已发布" :value="1" />
          <el-option label="草稿" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button @click="loadData">搜索</el-button></el-form-item>
    </el-form>

    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="author" label="作者" width="100" />
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
          <el-button size="small" @click="editRow(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteRow(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      layout="total, prev, pager, next"
      :total="total"
      :page-size="20"
      v-model:current-page="page"
      style="margin-top:16px;justify-content:center"
    />

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
        <el-form-item label="作者"><el-input v-model="form.author" /></el-form-item>
        <el-form-item label="封面">
          <el-upload action="/api/file/upload/post-image" :show-file-list="false" :on-success="f => form.coverUrl = f.data">
            <img v-if="form.coverUrl" :src="form.coverUrl" style="width:120px;height:60px;object-fit:cover" />
            <el-button v-else>选择封面</el-button>
          </el-upload>
        </el-form-item>
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
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const showDialog = ref(false)
const page = ref(1)
const total = ref(0)
const filters = ref({ categoryId: null, status: null })
const form = ref({ id: null, title: '', categoryId: null, summary: '', author: '', coverUrl: '', content: '', status: 1 })

function loadData() {
  list.value = [
    { id: 1, title: '经期疼痛缓解指南：科学方法与日常护理', categoryName: '经期护理', author: '运营团队', viewCount: 2341, likeCount: 186, status: 1, createTime: '2026-03-01 10:00' },
    { id: 2, title: '排卵试纸使用全攻略，备孕必看', categoryName: '备孕指南', author: '运营团队', viewCount: 1892, likeCount: 142, status: 1, createTime: '2026-03-03 14:00' },
    { id: 3, title: '孕早期注意事项与营养补充建议', categoryName: '孕期保健', author: '专家王医生', viewCount: 3210, likeCount: 256, status: 1, createTime: '2026-03-05 09:00' },
  ]
  total.value = list.value.length
}

function editRow(row) { form.value = { ...row, id: row.id }; showDialog.value = true }
async function deleteRow(row) {
  await ElMessageBox.confirm('确认删除该文章？', '提示')
  list.value = list.value.filter(i => i.id !== row.id)
  ElMessage.success('已删除')
}

function submitForm() {
  if (!form.value.title) { ElMessage.warning('请填写标题'); return }
  if (!form.value.id) {
    list.value.unshift({ ...form.value, id: Date.now(), createTime: new Date().toISOString().slice(0, 16), viewCount: 0, likeCount: 0 })
  }
  showDialog.value = false
  ElMessage.success('保存成功')
}

loadData()
</script>
