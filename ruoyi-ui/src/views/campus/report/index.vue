<template>
  <div class="report-page" v-loading="loading">
    <header class="report-hero">
      <div><p class="eyebrow">CAMPUS MOBILITY / INSIGHTS</p><h1>运营统计</h1><p class="intro">从车辆、订单与账单了解校园骑行情况。</p></div>
      <div class="range"><label for="report-range">订单与金额统计时间</label><div class="range-controls"><el-date-picker id="report-range" v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" :disabled="loading" /><el-button type="primary" :loading="loading" @click="load">查询</el-button></div><span>不选择日期时统计全部历史；车辆为当前快照。</span></div>
    </header>
    <div v-if="report" class="report-content">
      <section aria-label="关键指标" class="metrics">
        <article><span>当前车辆总数 <em>实时快照</em></span><strong>{{ report.vehicles.total }}</strong><small>含空闲、借出、维修、报废</small></article>
        <article><span>发起订单 <em>按借车时间</em></span><strong>{{ report.orders.total }}</strong><small>其中 {{ report.orders.completed }} 笔当前已完成</small></article>
        <article><span>应收金额 <em>按账单创建时间</em></span><strong>¥{{ money(report.bills.receivable) }}</strong><small>共 {{ report.bills.billCount }} 张账单</small></article>
        <article class="accent"><span>期间实际到账 <em>按支付时间</em></span><strong>¥{{ money(report.bills.cashReceived) }}</strong><small>含期间内支付的历史账单</small></article>
      </section>
      <section class="report-section ledger">
        <div><p class="eyebrow dark">BILL POSITION</p><h2>账单结清情况</h2><p>按账单创建日期筛选；已缴和欠费显示这些账单截至当前的状态。</p></div>
        <div class="ledger-values"><div><span>已缴金额</span><strong>¥{{ money(report.bills.received) }}</strong></div><div><span>未缴金额</span><strong>¥{{ money(report.bills.outstanding) }}</strong></div></div>
      </section>
      <section class="chart-grid">
        <article class="report-section chart-panel"><div class="section-title"><div><p class="eyebrow dark">FLEET STATUS</p><h2>车辆状态</h2></div><span>当前快照</span></div><div ref="statusChartRef" class="chart" role="img" aria-label="车辆空闲、借出、维修、报废状态分布图"></div><div v-if="report.vehicles.total === 0" class="empty-chart">暂无车辆数据</div><div class="legend-text">空闲 {{ report.vehicles.idle }} · 借出 {{ report.vehicles.rented }} · 维修 {{ report.vehicles.repair }} · 报废 {{ report.vehicles.scrapped }}</div></article>
        <article class="report-section chart-panel"><div class="section-title"><div><p class="eyebrow dark">STATION DISTRIBUTION</p><h2>站点车辆分布</h2></div><span>当前归属</span></div><div ref="stationChartRef" class="chart" role="img" aria-label="各站点车辆状态数量分布图"></div><div v-if="report.stations.length === 0" class="empty-chart">暂无站点数据</div><div v-else-if="report.stations.length > 7" class="legend-text">滚动图表查看其他站点，完整数据见下表</div></article>
      </section>
      <section class="report-section table-panel"><div class="section-title"><div><p class="eyebrow dark">STATION DETAILS</p><h2>站点明细</h2></div><span>含已停用站点</span></div><p class="table-note">借出车辆按借车前的最后归属站点统计，不表示车辆当前在场。</p>
        <el-table :data="report.stations" row-key="stationId" empty-text="暂无站点数据"><el-table-column prop="stationName" label="站点" min-width="140" show-overflow-tooltip/><el-table-column label="状态" width="100"><template #default="{row}">{{ row.stationStatus === 'enabled' ? '启用' : '停用' }}</template></el-table-column><el-table-column prop="total" label="合计" min-width="80" align="right"/><el-table-column prop="idle" label="空闲" min-width="80" align="right"/><el-table-column prop="rented" label="借出" min-width="80" align="right"/><el-table-column prop="repair" label="维修" min-width="80" align="right"/><el-table-column prop="scrapped" label="报废" min-width="80" align="right"/></el-table>
      </section>
      <p class="footnote">订单按借车开始时间筛选，完成次数取当前状态；账单按创建时间筛选，已缴和欠费取当前状态；期间实际到账按缴费时间筛选。日期区间包含起止两天。</p>
    </div>
    <el-empty v-else-if="!loading" description="统计数据暂不可用"><el-button type="primary" @click="load">重新加载</el-button></el-empty>
  </div>
</template>
<script setup name="CampusReport">
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { getCampusReport } from '@/api/campus/report'
const dateRange = ref([]), loading = ref(false), report = ref(null), statusChartRef = ref(null), stationChartRef = ref(null)
let statusChart, stationChart
const money = value => Number(value || 0).toFixed(2)
function disposeCharts() { statusChart?.dispose(); stationChart?.dispose(); statusChart = undefined; stationChart = undefined }
function resizeCharts() { statusChart?.resize(); stationChart?.resize() }
async function load() {
  if (loading.value) return
  loading.value = true
  disposeCharts()
  report.value = null
  try {
    const params = dateRange.value?.length === 2 ? { startDate: dateRange.value[0], endDate: dateRange.value[1] } : {}
    const response = await getCampusReport(params)
    report.value = response.data
    await nextTick()
    renderCharts()
  } finally { loading.value = false }
}
function renderCharts() {
  if (!report.value || !statusChartRef.value || !stationChartRef.value) return
  const colors = ['#3eae84', '#e2ae52', '#d97868', '#a4b2b4']
  const names = ['空闲', '借出', '维修', '报废']
  const keys = ['idle', 'rented', 'repair', 'scrapped']
  statusChart = echarts.init(statusChartRef.value)
  statusChart.setOption({ color: colors, tooltip: { trigger: 'item', renderMode: 'richText' }, series: [{ type:'pie', stillShowZeroSum:false, radius:['58%','78%'], center:['50%','51%'], avoidLabelOverlap:true, label:{ show:false }, data:keys.map((key,i) => ({ name:names[i], value:report.value.vehicles[key] })) }] })
  stationChart = echarts.init(stationChartRef.value)
  const stations = report.value.stations
  stationChart.setOption({ color: colors, tooltip: { trigger:'axis', axisPointer:{ type:'shadow' }, renderMode:'richText' }, grid:{ left:90, right:18, top:20, bottom:35 }, xAxis:{ type:'value', minInterval:1, splitLine:{ lineStyle:{ color:'#edf1ef' } } }, yAxis:{ type:'category', data:stations.map(s => s.stationName), axisLabel:{ width:75, overflow:'truncate' }, axisTick:{ show:false } }, dataZoom: stations.length > 7 ? [{ type:'inside', yAxisIndex:0, startValue:0, endValue:6 }] : [], series:keys.map((key,i) => ({ name:names[i], type:'bar', stack:'total', barMaxWidth:20, data:stations.map(s => s[key]) })) })
}
onMounted(() => { window.addEventListener('resize', resizeCharts); load() })
onBeforeUnmount(() => { window.removeEventListener('resize', resizeCharts); disposeCharts() })
</script>
<style scoped>
.report-page { padding:22px; color:#193a44; }.report-hero { display:flex; justify-content:space-between; align-items:end; gap:25px; padding:30px 34px; color:#f5faf8; background:#123b49; border-radius:8px 8px 0 0; }.eyebrow { margin:0; color:#79dac0; letter-spacing:.18em; font-size:10px; font-weight:700; }.report-hero h1 { font-size:25px; margin:10px 0; }.intro,.range span { font-size:12px; color:#bed2d2; }.range label { display:block; margin-bottom:8px; font-size:12px; color:#e7f2ef; }.range-controls { display:flex; align-items:center; gap:8px; }.range-controls :deep(.el-date-editor) { width:250px; }.range span { display:block; margin-top:8px; }.report-content { padding:24px; background:#f3f8f6; border:1px solid #e0ebe7; }.metrics { display:grid; grid-template-columns:repeat(4,minmax(0,1fr)); gap:14px; }.metrics article,.report-section { background:#fff; border:1px solid #deebe5; border-radius:6px; }.metrics article { min-width:0; padding:20px; }.metrics span,.ledger-values span { display:block; color:#6a8586; font-size:12px; }.metrics em { color:#94aca6; font-style:normal; font-size:10px; margin-left:4px; }.metrics strong { display:block; margin:13px 0 10px; color:#163c47; font-size:26px; overflow-wrap:anywhere; }.metrics small { color:#829995; font-size:11px; }.metrics .accent { background:#e6f4ea; border-color:#cee9d5; }.metrics .accent strong { color:#197452; }.report-section { padding:22px; }.ledger { display:flex; align-items:center; justify-content:space-between; gap:24px; margin-top:14px; }.dark { color:#4b907e; }.report-section h2 { font-size:16px; margin:7px 0; }.ledger p:not(.eyebrow),.table-note { color:#809692; font-size:12px; line-height:1.6; margin:7px 0 0; }.ledger-values { display:flex; gap:50px; }.ledger-values strong { display:block; margin-top:8px; font-size:20px; }.chart-grid { display:grid; grid-template-columns:1fr 1.5fr; gap:14px; margin-top:14px; }.chart-panel { position:relative; min-width:0; }.section-title { display:flex; justify-content:space-between; align-items:center; gap:15px; }.section-title>span { color:#8aa19d; font-size:11px; }.chart { width:100%; height:235px; }.legend-text { text-align:center; color:#6e8b83; font-size:12px; line-height:1.7; }.empty-chart { position:absolute; top:50%; left:50%; color:#8ca29c; font-size:12px; transform:translate(-50%,-50%); }.table-panel { margin-top:14px; }.table-note { margin-bottom:15px; }.footnote { margin:16px 2px 0; font-size:11px; line-height:1.7; color:#7b938e; }
@media (max-width:1050px) { .metrics { grid-template-columns:repeat(2,minmax(0,1fr)); }.report-hero { align-items:start; flex-direction:column; }.chart-grid { grid-template-columns:1fr; } }
@media (max-width:650px) { .report-page { padding:10px; }.report-hero { padding:24px; }.report-content { padding:14px; }.metrics { grid-template-columns:1fr 1fr; gap:8px; }.metrics article { padding:14px; }.metrics strong { font-size:20px; }.ledger { align-items:start; flex-direction:column; }.ledger-values { gap:30px; }.range-controls :deep(.el-date-editor) { width:220px; } }
</style>
