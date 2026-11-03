<template>
  <div class="vehicle-page">
    <header class="vehicle-header">
      <div><p class="eyebrow">CAMPUS MOBILITY / OPERATIONS</p><h1>车辆管理</h1><p>维护校园车辆、站点归属与可用状态。</p></div>
      <div class="header-mark" aria-hidden="true">02<span>车辆运营</span></div>
    </header>
    <section class="vehicle-content">
      <el-form :model="query" :inline="true" class="filters" @submit.prevent>
        <el-form-item label="车辆编号"><el-input v-model="query.vehicleNo" placeholder="输入车辆编号" clearable @keyup.enter="search" /></el-form-item>
        <el-form-item label="所属站点"><el-select v-model="query.stationId" placeholder="全部站点" clearable><el-option v-for="item in stations" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="query.status" placeholder="全部状态" clearable><el-option v-for="item in states" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" icon="Search" @click="search">查询</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
      </el-form>
      <div class="table-heading"><div><strong>车辆清单</strong><span>共 {{ total }} 辆</span></div><el-button v-hasPermi="['campus:vehicle:add']" type="primary" icon="Plus" @click="openAdd">录入车辆</el-button></div>
      <el-table v-loading="loading" :data="rows" row-key="id" empty-text="暂无符合条件的车辆">
        <el-table-column label="车辆编号" prop="vehicleNo" min-width="150" show-overflow-tooltip><template #default="scope"><span class="vehicle-number">{{ scope.row.vehicleNo }}</span></template></el-table-column>
        <el-table-column label="所属站点" prop="stationName" min-width="140" show-overflow-tooltip />
        <el-table-column label="状态" min-width="110"><template #default="scope"><span :class="['status', scope.row.status]">{{ label(scope.row.status) }}</span></template></el-table-column>
        <el-table-column label="备注" prop="description" min-width="180" show-overflow-tooltip />
        <el-table-column label="录入时间" prop="createdAt" min-width="170" />
        <el-table-column label="操作" width="110" fixed="right"><template #default="scope"><el-button v-hasPermi="['campus:vehicle:edit']" link type="primary" :disabled="scope.row.status === 'rented'" @click="openEdit(scope.row)">修改</el-button></template></el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />
    </section>
    <el-dialog v-model="dialog" :title="form.id ? '修改车辆' : '录入车辆'" width="min(520px, 94vw)" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="车辆编号" prop="vehicleNo"><el-input v-model="form.vehicleNo" maxlength="32" show-word-limit placeholder="例如 BIKE001" /></el-form-item>
        <el-form-item label="所属站点" prop="stationId"><el-select v-model="form.stationId" placeholder="请选择启用的站点" style="width:100%"><el-option v-for="item in stations" :key="item.id" :label="item.name" :value="item.id" /></el-select></el-form-item>
        <el-form-item label="车辆状态" prop="status"><el-select v-model="form.status" style="width:100%"><el-option v-for="item in editableStates" :key="item.value" :label="item.label" :value="item.value" :disabled="wasScrapped && item.value !== 'scrapped'" /></el-select></el-form-item>
        <el-form-item label="备注" prop="description"><el-input v-model="form.description" type="textarea" :rows="3" maxlength="255" show-word-limit placeholder="填写便于运营人员识别的备注" /></el-form-item>
        <p class="form-note">借出状态由租借流程更新；有未完成报修的车辆不能设为空闲。</p>
      </el-form>
      <template #footer><el-button @click="dialog = false">取消</el-button><el-button type="primary" :loading="saving" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup name="CampusVehicle">
import { computed, getCurrentInstance, onMounted, reactive, ref } from 'vue'
import { addVehicle, getVehicle, listVehicleStations, listVehicles, updateVehicle } from '@/api/campus/vehicle'
const { proxy } = getCurrentInstance()
const states = [{ value: 'idle', label: '空闲' }, { value: 'rented', label: '借出' }, { value: 'repair', label: '维修' }, { value: 'scrapped', label: '报废' }]
const editableStates = computed(() => states.filter(item => item.value !== 'rented'))
const query = reactive({ pageNum: 1, pageSize: 10, vehicleNo: '', stationId: undefined, status: undefined })
const rows = ref([]), stations = ref([]), total = ref(0), loading = ref(false), saving = ref(false), dialog = ref(false), wasScrapped = ref(false), formRef = ref()
const form = reactive({ id: undefined, vehicleNo: '', stationId: undefined, status: 'idle', description: '' })
const rules = { vehicleNo: [{ required: true, whitespace: true, message: '请输入车辆编号', trigger: 'blur' }], stationId: [{ required: true, message: '请选择站点', trigger: 'change' }], status: [{ required: true, message: '请选择状态', trigger: 'change' }] }
const label = status => states.find(item => item.value === status)?.label || status
async function load() { loading.value = true; try { const response = await listVehicles(query); rows.value = response.rows; total.value = response.total } finally { loading.value = false } }
async function loadStations() { const response = await listVehicleStations(); stations.value = response.data || [] }
function search() { query.pageNum = 1; load() }
function resetQuery() { Object.assign(query, { pageNum: 1, vehicleNo: '', stationId: undefined, status: undefined }); load() }
function resetForm() { Object.assign(form, { id: undefined, vehicleNo: '', stationId: undefined, status: 'idle', description: '' }); wasScrapped.value = false; formRef.value?.clearValidate() }
async function openAdd() { resetForm(); await loadStations(); dialog.value = true }
async function openEdit(row) { resetForm(); await loadStations(); const response = await getVehicle(row.id); Object.assign(form, response.data); wasScrapped.value = form.status === 'scrapped'; dialog.value = true }
async function submit() {
  if (!(await formRef.value.validate().catch(() => false))) return
  saving.value = true
  try { if (form.id) await updateVehicle(form); else await addVehicle(form); proxy.$modal.msgSuccess('保存成功'); dialog.value = false; await load() } finally { saving.value = false }
}
onMounted(async () => { await Promise.all([loadStations(), load()]) })
</script>

<style scoped>
.vehicle-page { padding: 22px; color: #182b38; }
.vehicle-header { display:flex; justify-content:space-between; gap:24px; align-items:center; padding:28px 32px; border-radius:8px 8px 0 0; background:#123446; color:#f5f9f8; }
.vehicle-header h1 { margin: 8px 0; font-size: 25px; letter-spacing: .04em; }
.vehicle-header p { margin:0; color:#bed1d5; font-size:13px; }
.vehicle-header .eyebrow { color:#70d8c6; font-size:10px; letter-spacing:.18em; font-weight:700; }
.header-mark { border-left:1px solid #527080; padding-left:22px; font-size:32px; line-height:1; font-weight:700; color:#7bdcca; }
.header-mark span { display:block; margin-top:7px; color:#c2d4d7; font-size:11px; letter-spacing:.1em; font-weight:400; }
.vehicle-content { background:#fff; border:1px solid #e0e9e9; border-top:0; padding:24px; }
.filters :deep(.el-input), .filters :deep(.el-select) { width:165px; }
.table-heading { display:flex; justify-content:space-between; align-items:center; gap:16px; border-top:1px solid #edf1f1; padding:19px 0 15px; }
.table-heading strong { font-size:16px; }.table-heading span { margin-left:12px; color:#81939b; font-size:12px; }
.vehicle-number { color:#153e50; font-weight:700; letter-spacing:.04em; }
.status { display:inline-block; min-width:50px; text-align:center; padding:4px 8px; border-radius:3px; font-size:12px; background:#edf1f2; color:#687a83; }
.status.idle { background:#e1f5ed; color:#187a59; }.status.rented { background:#fff1d9; color:#986516; }.status.repair { background:#fce9e4; color:#af5143; }
.form-note { color:#71858c; font-size:12px; line-height:1.6; }
@media (max-width: 650px) { .vehicle-page { padding:10px; }.vehicle-header { padding:22px; }.header-mark { display:none; }.vehicle-content { padding:16px; }.filters :deep(.el-input), .filters :deep(.el-select) { width:100%; } }
</style>
