<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header><div class="heading"><div><strong>租借订单</strong><p>查看租借记录并处理异常订单</p></div><el-button icon="Refresh" @click="load">刷新</el-button></div></template>
      <el-table v-loading="loading" :data="rows" empty-text="暂无订单">
        <el-table-column label="订单编号" prop="orderNo" min-width="180" show-overflow-tooltip/>
        <el-table-column label="用户编号" prop="userId" width="100"/><el-table-column label="车辆编号" prop="vehicleNo" width="120"/>
        <el-table-column label="借车站点" prop="startStationName" min-width="130"/><el-table-column label="还车站点" prop="endStationName" min-width="130"/>
        <el-table-column label="借车时间" min-width="170"><template #default="s">{{ parseTime(s.row.startedAt) }}</template></el-table-column>
        <el-table-column label="还车时间" min-width="170"><template #default="s">{{ s.row.endedAt ? parseTime(s.row.endedAt) : '—' }}</template></el-table-column>
        <el-table-column label="应付" min-width="90"><template #default="s">{{ s.row.payableAmount == null ? '—' : '¥'+Number(s.row.payableAmount).toFixed(2) }}</template></el-table-column>
        <el-table-column label="状态" width="90"><template #default="s">{{ ({ongoing:'进行中',completed:'已完成',exception:'异常',closed:'已关闭'})[s.row.status]||s.row.status }}</template></el-table-column>
        <el-table-column label="处理" width="110" fixed="right"><template #default="s"><el-button v-if="s.row.status==='ongoing'" v-hasPermi="['campus:rental:manage']" link type="warning" @click="mark(s.row)">标记异常</el-button><el-button v-if="s.row.status==='exception'" v-hasPermi="['campus:rental:manage']" link type="primary" @click="openResolve(s.row)">异常结算</el-button></template></el-table-column>
      </el-table><pagination v-show="total>0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load"/>
    </el-card>
    <el-dialog v-model="resolveOpen" title="异常订单结算" width="480px" append-to-body>
      <p>订单 {{ selected?.orderNo }} 将按借车时的收费规则结算，操作会保留记录。</p>
      <el-form label-position="top"><el-form-item label="还车站点" required><el-select v-model="stationId" filterable placeholder="请选择站点" style="width:100%"><el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.id"/></el-select></el-form-item><el-form-item label="处理原因" required><el-input v-model="reason" type="textarea" maxlength="1000" show-word-limit /></el-form-item></el-form>
      <template #footer><el-button @click="resolveOpen=false">取消</el-button><el-button type="primary" :loading="submitting" @click="resolve">确认结算</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="CampusRentalAdmin">
import { getAllRentals, markException, resolveException } from '@/api/campus/rental'
import { browseStations } from '@/api/bike/station'
const { proxy }=getCurrentInstance()
const rows=ref([]),total=ref(0),loading=ref(false),query=reactive({pageNum:1,pageSize:10})
const resolveOpen=ref(false),selected=ref(),stations=ref([]),stationId=ref(),reason=ref(''),submitting=ref(false)
async function load(){loading.value=true;try{const r=await getAllRentals(query);rows.value=r.rows;total.value=r.total}finally{loading.value=false}}
async function mark(row){try{const {value}=await proxy.$prompt('请填写标记异常的原因','标记异常',{confirmButtonText:'确认',cancelButtonText:'取消',inputType:'textarea',inputValidator:v=>!!v?.trim()&&v.trim().length<=1000,inputErrorMessage:'请填写不超过1000字的原因'});await markException(row.id,value.trim());proxy.$modal.msgSuccess('已标记异常');await load()}catch(_){}}
async function openResolve(row){const r=await browseStations({pageNum:1,pageSize:1000});stations.value=r.rows;selected.value=row;stationId.value=undefined;reason.value='';resolveOpen.value=true}
async function resolve(){if(!stationId.value||!reason.value.trim()){proxy.$modal.msgWarning('请选择站点并填写原因');return}submitting.value=true;try{await resolveException(selected.value.id,stationId.value,reason.value.trim());resolveOpen.value=false;proxy.$modal.msgSuccess('异常订单已结算');await load()}finally{submitting.value=false}}
load()
</script>
<style scoped>.heading{display:flex;align-items:center;justify-content:space-between}.heading strong{font-size:19px}.heading p{margin:6px 0 0;color:#84918b;font-size:13px}</style>
