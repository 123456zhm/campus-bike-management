<template>
  <div class="bill-page">
    <header class="bill-header"><div><p>FINANCE / CAMPUS OPERATIONS</p><h1>全部账单</h1><span>按订单查看应收和缴费状态。</span></div><div class="mark">06<small>费用运营</small></div></header>
    <section class="bill-content">
      <el-form :model="query" :inline="true" class="filters" @submit.prevent>
        <el-form-item label="账单编号"><el-input v-model="query.billNo" clearable placeholder="输入账单编号" @keyup.enter="search" /></el-form-item>
        <el-form-item label="订单编号"><el-input v-model="query.orderNo" clearable placeholder="输入订单编号" @keyup.enter="search" /></el-form-item>
        <el-form-item label="用户账号"><el-input v-model="query.userName" clearable placeholder="输入用户账号" @keyup.enter="search" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="query.status" clearable placeholder="全部状态"><el-option label="待缴" value="unpaid"/><el-option label="已缴" value="paid"/></el-select></el-form-item>
        <el-form-item><el-button type="primary" icon="Search" @click="search">查询</el-button><el-button icon="Refresh" @click="reset">重置</el-button></el-form-item>
      </el-form>
      <div class="table-heading"><strong>账单清单</strong><span>共 {{ total }} 条</span></div>
      <el-table v-loading="loading" :data="rows" row-key="id" empty-text="暂无符合条件的账单">
        <el-table-column prop="billNo" label="账单编号" min-width="170" show-overflow-tooltip />
        <el-table-column prop="orderNo" label="订单编号" min-width="170" show-overflow-tooltip />
        <el-table-column prop="userName" label="用户" min-width="110" show-overflow-tooltip />
        <el-table-column prop="vehicleNo" label="车辆" min-width="115" show-overflow-tooltip />
        <el-table-column label="应缴" width="110" align="right"><template #default="{row}">¥{{ money(row.payableAmount) }}</template></el-table-column>
        <el-table-column label="已缴" width="110" align="right"><template #default="{row}">¥{{ money(row.paidAmount) }}</template></el-table-column>
        <el-table-column label="状态" width="100"><template #default="{row}"><span :class="['status', row.status]">{{ row.status === 'paid' ? '已缴' : '待缴' }}</span></template></el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />
    </section>
  </div>
</template>
<script setup name="CampusBillAdmin">
import { onMounted, reactive, ref } from 'vue'
import { listAllBills } from '@/api/campus/bill'
const query = reactive({ pageNum: 1, pageSize: 10, billNo: '', orderNo: '', userName: '', status: undefined })
const rows = ref([]), total = ref(0), loading = ref(false)
const money = value => Number(value || 0).toFixed(2)
async function load() { loading.value = true; try { const response = await listAllBills(query); rows.value = response.rows; total.value = response.total } finally { loading.value = false } }
function search() { query.pageNum = 1; load() }
function reset() { Object.assign(query, { pageNum: 1, billNo: '', orderNo: '', userName: '', status: undefined }); load() }
onMounted(load)
</script>
<style scoped>
.bill-page { padding:22px; color:#182f3d; }.bill-header { display:flex; justify-content:space-between; align-items:center; padding:28px 32px; color:#f5faf8; background:#123b49; border-radius:8px 8px 0 0; }.bill-header p { color:#79dac0; letter-spacing:.18em; font-size:10px; font-weight:700; margin:0; }.bill-header h1 { font-size:25px; margin:10px 0; }.bill-header span { font-size:13px; color:#bfd4d3; }.mark { border-left:1px solid #4c727b; padding-left:22px; color:#7bdcca; font-size:32px; font-weight:700; }.mark small { display:block; font-size:11px; font-weight:400; color:#c2d4d4; }.bill-content { padding:24px; background:#fff; border:1px solid #e0e9e9; border-top:0; }.filters :deep(.el-input),.filters :deep(.el-select) { width:150px; }.table-heading { display:flex; align-items:center; gap:12px; border-top:1px solid #edf1f1; padding:20px 0 16px; }.table-heading span { font-size:12px; color:#899aa0; }.status { display:inline-block; min-width:50px; text-align:center; padding:4px 8px; border-radius:3px; font-size:12px; }.status.paid { background:#e1f5ed; color:#187a59; }.status.unpaid { background:#fff1d9; color:#986516; }
@media (max-width:650px) { .bill-page { padding:10px; }.bill-header { padding:22px; }.mark { display:none; }.bill-content { padding:16px; } }
</style>
