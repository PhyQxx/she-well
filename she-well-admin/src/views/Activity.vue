<template>
  <el-card shadow="hover">
    <template #header>
      <span style="font-weight:600">🎁 活动管理</span>
      <el-button type="primary" size="small" style="float:right" @click="showDialog = true">+ 创建活动</el-button>
    </template>
    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="活动名称" min-width="150" />
      <el-table-column prop="type" label="类型" width="100" />
      <el-table-column prop="startDate" label="开始日期" width="120" />
      <el-table-column prop="endDate" label="结束日期" width="120" />
      <el-table-column prop="participants" label="参与人数" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="{ ongoing: 'success', upcoming: 'warning', ended: 'info' }[row.status]" size="small">
            {{ { ongoing: '进行中', upcoming: '未开始', ended: '已结束' }[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="editRow(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteRow(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" :title="form.id ? '编辑活动' : '创建活动'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="活动名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="活动类型">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="打卡挑战" value="打卡挑战" />
            <el-option label="健康知识赛" value="健康知识赛" />
            <el-option label="社区互动" value="社区互动" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
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

const list = ref([
  { id: 1, name: '21天打卡挑战', type: '打卡挑战', startDate: '2026-04-01', endDate: '2026-04-21', participants: 1234, status: 'ongoing' },
  { id: 2, name: '女性健康知识竞赛', type: '健康知识赛', startDate: '2026-05-01', endDate: '2026-05-07', participants: 0, status: 'upcoming' },
])
const showDialog = ref(false)
const form = ref({ id: null, name: '', type: '', startDate: '', endDate: '' })

function editRow(row) { form.value = { ...row }; showDialog.value = true }
async function deleteRow(row) {
  await ElMessageBox.confirm('确认删除？', '提示')
  list.value = list.value.filter(i => i.id !== row.id)
  ElMessage.success('已删除')
}
function submitForm() {
  if (!form.value.name) { ElMessage.warning('请填写活动名称'); return }
  if (!form.value.id) list.value.unshift({ ...form.value, id: Date.now(), participants: 0, status: 'upcoming' })
  showDialog.value = false
  ElMessage.success('保存成功')
}
</script>
