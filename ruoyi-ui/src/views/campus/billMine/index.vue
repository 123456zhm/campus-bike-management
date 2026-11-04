<template>
  <div class="bill-page">
    <header class="bill-header"><div><p>MY RIDES / MY EXPENSES</p><h1>我的账单</h1><span>查看骑行费用，及时完成模拟缴费。</span></div><div class="header-badge">¥<small>校园骑行</small></div></header>
    <section class="bill-content">
      <div class="notice"><strong>模拟缴费</strong><span>此功能用于课程演示，不会产生真实扣款。</span></div>
      <el-tabs v-model="activeTab" @tab-change="loadTab">
        <el-tab-pane label="账单记录" name="bills">
          <el-form :model="query" :inline="true" class="filters" @submit.prevent>
            <el-form-item label="账单编号"><el-input v-model="query.billNo" clearable placeholder="输入账单编号" @keyup.enter="search" /></el-form-item>
            <el-form-item label="状态"><el-select v-model="query.status" clearable placeholder="全部状态"><el-option label="待缴" value="unpaid"/><el-option label="已缴" value="paid"/></el-select></el-form-item>
            <el-form-item><el-button type="primary" icon="Search" @click="search">查询</el-button><el-button icon="Refresh" @click="reset">重置</el-button></el-form-item>
          </el-form>
          <div class="table-heading"><strong>我的账单</strong><span>共 {{ total }} 条</span></div>
          <el-table v-loading="loading" :data="rows" row-key="id" empty-text="暂无账单">
            <el-table-column prop="billNo" label="账单编号" min-width="170" show-overflow-tooltip />
            <el-table-column prop="orderNo" label="订单编号" min-width="170" show-overflow-tooltip />
            <el-table-column prop="vehicleNo" label="车辆" min-width="120" show-overflow-tooltip />
            <el-table-column label="应缴" width="110" align="right"><template #default="{row}">¥{{ money(row.payableAmount) }}</template></el-table-column>
            <el-table-column label="已缴" width="110" align="right"><template #default="{row}">¥{{ money(row.paidAmount) }}</template></el-table-column>
            <el-table-column label="状态" width="100"><template #default="{row}"><span :class="['status', row.status]">{{ row.status === 'paid' ? '已缴' : '待缴' }}</span></template></el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="170" />
            <el-table-column label="操作" fixed="right" width="120"><template #default="{row}"><el-button v-if="row.status === 'unpaid'" link type="primary" :disabled="payingId !== null" :loading="payingId === row.id" @click="pay(row)">模拟缴费</el-button><span v-else class="settled">已结清</span></template></el-table-column>
          </el-table>
          <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="loadBills" />
        </el-tab-pane>
        <el-tab-pane label="缴费记录" name="payments">
          <div class="table-heading"><strong>模拟缴费记录</strong><span>共 {{ paymentTotal }} 条</span></div>
          <el-table v-loading="paymentLoading" :data="payments" row-key="id" empty-text="暂无缴费记录">
            <el-table-column prop="paymentNo" label="流水编号" min-width="180" show-overflow-tooltip />
            <el-table-column prop="billNo" label="账单编号" min-width="180" show-overflow-tooltip />
            <el-table-column prop="orderNo" label="订单编号" min-width="180" show-overflow-tooltip />
            <el-table-column label="缴费金额" width="130" align="right"><template #default="{row}">¥{{ money(row.amount) }}</template></el-table-column>
            <el-table-column prop="paidAt" label="缴费时间" width="180" />
          </el-table>
          <pagination v-show="paymentTotal > 0" :total="paymentTotal" v-model:page="paymentQuery.pageNum" v-model:limit="paymentQuery.pageSize" @pagination="loadPayments" />
        </el-tab-pane>
      </el-tabs>
    </section>
  </div>
</template>
<script setup name="CampusBillMine">
import { getCurrentInstance, onMounted, reactive, ref } from 'vue'
import { listMyBills, listMyPayments, simulateBillPayment } from '@/api/campus/bill'
const { proxy } = getCurrentInstance()
const activeTab = ref('bills'), rows = ref([]), payments = ref([]), total = ref(0), paymentTotal = ref(0), loading = ref(false), paymentLoading = ref(false), payingId = ref(null)
const query = reactive({ pageNum: 1, pageSize: 10, billNo: '', status: undefined })
const paymentQuery = reactive({ pageNum: 1, pageSize: 10 })
// 网络超时后保留同一请求号，安全重试。
const pendingKeys = new Map()
const money = value => Number(value || 0).toFixed(2)
function newRequestKey() {
  if (globalThis.crypto?.randomUUID) return globalThis.crypto.randomUUID()
  if (globalThis.crypto?.getRandomValues) { const a = new Uint8Array(16); globalThis.crypto.getRandomValues(a); return Array.from(a, n => n.toString(16).padStart(2, '0')).join('') }
  return Date.now().toString(36).padStart(12, '0') + Math.floor(Math.random() * 0x100000000).toString(16).padStart(8, '0') + Math.floor(Math.random() * 0x100000000).toString(16).padStart(8, '0')
}
async function loadBills() { loading.value = true; try { const r = await listMyBills(query); rows.value = r.rows; total.value = r.total } finally { loading.value = false } }
async function loadPayments() { paymentLoading.value = true; try { const r = await listMyPayments(paymentQuery); payments.value = r.rows; paymentTotal.value = r.total } finally { paymentLoading.value = false } }
function loadTab(tab) { if (tab === 'payments') loadPayments(); else loadBills() }
function search() { query.pageNum = 1; loadBills() }
function reset() { Object.assign(query, { pageNum: 1, billNo: '', status: undefined }); loadBills() }
async function pay(row) {
  if (payingId.value !== null) return
  const confirmed = await proxy.$modal.confirm('确认模拟缴费 ¥' + money(row.payableAmount) + '？此操作不会产生真实扣款。').then(() => true).catch(() => false)
  if (!confirmed || payingId.value !== null) return
  const key = pendingKeys.get(row.id) || newRequestKey()
  pendingKeys.set(row.id, key)
  payingId.value = row.id
  try { await simulateBillPayment(row.id, key); pendingKeys.delete(row.id); proxy.$modal.msgSuccess('模拟缴费成功'); await loadBills() } finally { payingId.value = null }
}
onMounted(loadBills)
</script>
<style scoped>
.bill-page { padding:22px; color:#183943; }.bill-header { display:flex; justify-content:space-between; align-items:center; padding:28px 32px; color:#f5faf8; background:#123b49; border-radius:8px 8px 0 0; }.bill-header p { color:#79dac0; letter-spacing:.18em; font-size:10px; font-weight:700; margin:0; }.bill-header h1 { font-size:25px; margin:10px 0; }.bill-header span { font-size:13px; color:#bfd4d3; }.header-badge { color:#7bdcca; font-size:38px; font-weight:700; border-left:1px solid #4c727b; padding-left:22px; }.header-badge small { display:block; font-size:11px; font-weight:400; color:#c2d4d4; }.bill-content { padding:24px; background:#fff; border:1px solid #e0e9e9; border-top:0; }.notice { display:flex; align-items:baseline; flex-wrap:wrap; gap:10px; color:#356f61; background:#eaf6f0; border-left:3px solid #68c59d; padding:12px 15px; margin-bottom:18px; font-size:12px; }.notice strong { font-size:13px; }.filters :deep(.el-input),.filters :deep(.el-select) { width:170px; }.table-heading { display:flex; align-items:center; gap:12px; border-top:1px solid #edf1f1; padding:20px 0 16px; }.table-heading span,.settled { font-size:12px; color:#899aa0; }.status { display:inline-block; min-width:50px; text-align:center; padding:4px 8px; border-radius:3px; font-size:12px; }.status.paid { background:#e1f5ed; color:#187a59; }.status.unpaid { background:#fff1d9; color:#986516; }
@media (max-width:650px) { .bill-page { padding:10px; }.bill-header { padding:22px; }.header-badge { display:none; }.bill-content { padding:16px; } }
</style>
