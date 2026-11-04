<template>
  <div class="app-container rental-page">
    <header class="rental-header"><div><span>YOUR RIDES</span><h1>我的租借</h1><p>查看借车记录，选择站点归还车辆。</p></div><el-button type="primary" plain icon="Refresh" @click="load">刷新</el-button></header>
    <section class="content">
      <el-table v-loading="loading" :data="rows" empty-text="还没有租借记录，去可借车辆页面选一辆吧">
        <el-table-column label="订单编号" prop="orderNo" min-width="180" show-overflow-tooltip />
        <el-table-column label="车辆" prop="vehicleNo" min-width="110" />
        <el-table-column label="借车站点" prop="startStationName" min-width="120" />
        <el-table-column label="还车站点" prop="endStationName" min-width="120"><template #default="s">{{ s.row.endStationName || '—' }}</template></el-table-column>
        <el-table-column label="借车时间" prop="startedAt" min-width="170"><template #default="s">{{ parseTime(s.row.startedAt) }}</template></el-table-column>
        <el-table-column label="应付" min-width="90"><template #default="s">{{ s.row.payableAmount == null ? '待结算' : '¥' + Number(s.row.payableAmount).toFixed(2) }}</template></el-table-column>
        <el-table-column label="状态" min-width="90"><template #default="s"><el-tag :type="s.row.status==='ongoing'?'success':s.row.status==='exception'?'danger':'info'">{{ statusLabel(s.row.status) }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="90" fixed="right"><template #default="s"><el-button v-if="s.row.status==='ongoing'" v-hasPermi="['campus:rental:return']" link type="primary" @click="openReturn(s.row)">还车</el-button></template></el-table-column>
      </el-table>
      <pagination v-show="total>0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />
    </section>
    <el-dialog v-model="returnOpen" title="选择还车站点" width="440px" append-to-body>
      <p>车辆 {{ selected?.vehicleNo }} 可归还到任意启用的校园站点。</p>
      <el-select v-model="stationId" filterable placeholder="请选择站点" style="width:100%"><el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.id" /></el-select>
      <template #footer><el-button @click="returnOpen=false">取消</el-button><el-button type="primary" :loading="submitting" @click="submitReturn">确认还车</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="CampusRentalMine">
import { getMyRentals, returnVehicle } from '@/api/campus/rental'
import { browseStations } from '@/api/bike/station'
const { proxy }=getCurrentInstance()
const rows=ref([]),total=ref(0),loading=ref(false),returnOpen=ref(false),submitting=ref(false),stations=ref([]),stationId=ref(),selected=ref()
const query=reactive({pageNum:1,pageSize:10})
const statusLabel=s=>({ongoing:'进行中',completed:'已完成',exception:'异常',closed:'已关闭'}[s]||s)
async function load(){ loading.value=true; try {const r=await getMyRentals(query); rows.value=r.rows; total.value=r.total} finally {loading.value=false} }
async function openReturn(row){const r=await browseStations({pageNum:1,pageSize:1000}); stations.value=r.rows; selected.value=row; stationId.value=undefined; returnOpen.value=true}
async function submitReturn(){if(!stationId.value){proxy.$modal.msgWarning('请选择还车站点');return} submitting.value=true;try{const r=await returnVehicle(selected.value.id,stationId.value);returnOpen.value=false;proxy.$modal.msgSuccess('还车成功，应付 ¥'+Number(r.data.payableAmount).toFixed(2));await load()}finally{submitting.value=false}}
load()
</script>
<style scoped>
.rental-page{min-height:calc(100vh - 84px);background:#f2f7f4;color:#183d36}.rental-header{display:flex;justify-content:space-between;align-items:center;padding:20px 4px 26px}.rental-header span{color:#168273;font-size:11px;font-weight:700;letter-spacing:.16em}.rental-header h1{margin:8px 0;font-size:28px}.rental-header p{margin:0;color:#71867c}.content{padding:20px;background:#fff;border:1px solid #e1ebe5;border-radius:12px}
</style>
