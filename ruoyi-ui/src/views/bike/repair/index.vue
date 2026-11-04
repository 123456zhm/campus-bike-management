<template>
  <div class="repair-page app-container">
    <header class="heading"><div><span class="eyebrow">CAMPUS MOBILITY / OPERATIONS</span><h1>报修工单</h1><p>跟进车辆故障，让每条报修都有记录和反馈。</p></div></header>
    <section class="workspace">
      <el-form ref="queryRef" :model="query" inline @submit.prevent><el-form-item label="车辆编号" prop="vehicleNo"><el-input v-model="query.vehicleNo" clearable placeholder="搜索车辆编号" @keyup.enter="search" /></el-form-item><el-form-item label="工单状态" prop="status"><el-select v-model="query.status" clearable placeholder="全部状态" style="width:150px"><el-option v-for="item in statuses" :key="item.value" :label="item.label" :value="item.value" /></el-select></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="search">查询</el-button><el-button icon="Refresh" @click="resetSearch">重置</el-button></el-form-item></el-form>
      <div class="table-title"><strong>全部工单</strong><span>共 {{ total }} 条</span></div><el-alert v-if="error" title="工单加载失败，请稍后重试" type="error" :closable="false" show-icon />
      <el-table v-loading="loading" :data="rows" empty-text="暂无报修工单"><el-table-column prop="repairNo" label="工单编号" min-width="220" show-overflow-tooltip /><el-table-column prop="vehicleNo" label="车辆编号" min-width="110" /><el-table-column prop="reporterName" label="提交人" min-width="100" /><el-table-column prop="description" label="问题描述" min-width="180" show-overflow-tooltip /><el-table-column label="状态" width="100"><template #default="{ row }"><el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag></template></el-table-column><el-table-column label="提交时间" width="170"><template #default="{ row }">{{ parseTime(row.createdAt) }}</template></el-table-column><el-table-column label="操作" width="135" fixed="right"><template #default="{ row }"><el-button link type="primary" @click="openDetail(row)" v-hasPermi="['bike:repair:query']">详情</el-button><el-button v-if="row.status !== 'completed'" link type="primary" @click="openHandle(row)" v-hasPermi="['bike:repair:handle']">处理</el-button></template></el-table-column></el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />
    </section>
    <el-dialog v-model="dialog" title="处理报修工单" width="500px" append-to-body destroy-on-close><p class="dialog-hint">车辆 {{ selected?.vehicleNo }} · {{ selected?.repairNo }}</p><el-form ref="handleRef" :model="form" :rules="rules" label-position="top"><el-form-item label="更新状态" prop="status"><el-select v-model="form.status" placeholder="选择处理状态" style="width:100%"><el-option v-if="selected?.status === 'pending'" label="处理中" value="processing" /><el-option label="已完成" value="completed" /></el-select></el-form-item><el-form-item label="处理说明" prop="note"><el-input v-model="form.note" type="textarea" :rows="4" maxlength="1000" show-word-limit placeholder="说明处理进度或维修结果" /></el-form-item></el-form><template #footer><el-button @click="dialog=false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存处理结果</el-button></template></el-dialog>
    <repair-detail v-model="detailOpen" :detail="detail" />
  </div>
</template>
<script setup name="BikeRepairAdmin">
import { listRepairs, getRepair, handleRepair } from '@/api/bike/repair'
import RepairDetail from './RepairDetail.vue'
const { proxy } = getCurrentInstance(), parseTime = proxy.parseTime
const rows = ref([]), total = ref(0), loading = ref(false), error = ref(false), dialog = ref(false), saving = ref(false), detailOpen = ref(false), detail = ref({}), selected = ref(null)
const query = reactive({ pageNum: 1, pageSize: 10, vehicleNo: '', status: '' })
const form = reactive({ status: '', note: '' })
const rules = { status: [{ required: true, message: '请选择处理状态', trigger: 'change' }], note: [{ required: true, whitespace: true, message: '请填写处理说明', trigger: 'blur' }] }
const statuses = [{ label: '待处理', value: 'pending' }, { label: '处理中', value: 'processing' }, { label: '已完成', value: 'completed' }]
const statusText = s => statuses.find(x => x.value === s)?.label || s
const statusType = s => ({ pending: 'warning', processing: 'primary', completed: 'success' }[s] || 'info')
async function load() { loading.value = true; error.value = false; try { const r = await listRepairs(query); rows.value = r.rows; total.value = r.total } catch { error.value = true; rows.value = []; total.value = 0 } finally { loading.value = false } }
function search() { query.pageNum = 1; load() }
function resetSearch() { proxy.resetForm('queryRef'); search() }
function openHandle(row) { selected.value = row; Object.assign(form, { status: row.status === 'processing' ? 'completed' : 'processing', note: '' }); dialog.value = true }
async function save() { if (saving.value) return; const valid = await proxy.$refs.handleRef.validate().catch(() => false); if (!valid) return; saving.value = true; try { await handleRepair(selected.value.id, form); proxy.$modal.msgSuccess('处理结果已保存'); dialog.value = false; await load() } finally { saving.value = false } }
async function openDetail(row) { const r = await getRepair(row.id); detail.value = r.data; detailOpen.value = true }
load()
</script>
<style scoped>
.repair-page{--ink:#173631;--leaf:#168273;color:var(--ink);background:#f3f7f4;min-height:calc(100vh - 84px)}.heading{display:flex;justify-content:space-between;align-items:flex-end;gap:20px;padding:12px 2px 24px}.eyebrow{color:var(--leaf);font-size:11px;font-weight:700;letter-spacing:.16em}h1{margin:8px 0 6px;font-size:27px}.heading p,.dialog-hint{margin:0;color:#6c8178;font-size:14px}.workspace{padding:22px;background:#fff;border:1px solid #e0ebe5;border-radius:12px;box-shadow:0 8px 26px rgba(28,70,51,.04)}.table-title{display:flex;align-items:center;gap:10px;padding:10px 0 16px}.table-title span{font-size:13px;color:#81938a}.dialog-hint{margin-bottom:20px;line-height:1.6}@media(max-width:768px){.heading{align-items:flex-start;flex-direction:column}.workspace{padding:16px}}
</style>
