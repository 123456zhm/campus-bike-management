<template>
  <div class="browse-page">
    <header class="browse-hero">
      <div class="hero-copy"><p class="eyebrow">CAMPUS RIDE / 轻松出发</p><h1>找一辆车，<br>去往校园的下一站。</h1><p class="hero-text">查看启用站点的空闲车辆，选择适合你的出发点。</p></div>
      <div class="hero-orbit" aria-hidden="true"><span>GO</span><i></i><b></b></div>
    </header>
    <section class="browse-content">
      <div class="section-heading"><div><p class="eyebrow dark">AVAILABLE NOW</p><h2>可借车辆 <span>{{ total }}</span></h2></div><el-input v-model="query.vehicleNo" placeholder="搜索车辆编号" clearable class="search" @keyup.enter="search" @clear="search"><template #append><el-button icon="Search" aria-label="搜索" @click="search" /></template></el-input></div>
      <div v-loading="loading" class="vehicle-grid">
        <article v-for="vehicle in rows" :key="vehicle.id" class="ride-card">
          <div class="card-top"><span class="availability"><i></i>可借用</span><span class="card-index">CAMPUS BIKE</span></div>
          <div class="bike-mark" aria-hidden="true"><div></div><div></div><span></span></div>
          <div class="card-detail"><div><small>车辆编号</small><strong>{{ vehicle.vehicleNo }}</strong></div><div class="station"><small>所在站点</small><strong>{{ vehicle.stationName }}</strong></div></div>
        </article>
        <el-empty v-if="!loading && rows.length === 0" description="暂无可借车辆，请稍后再来看看" />
      </div>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />
    </section>
  </div>
</template>
<script setup name="CampusVehicleBrowse">
import { onMounted, reactive, ref } from 'vue'
import { listAvailableVehicles } from '@/api/campus/vehicle'
const query = reactive({ pageNum: 1, pageSize: 12, vehicleNo: '' })
const rows = ref([]), total = ref(0), loading = ref(false)
async function load() { loading.value = true; try { const response = await listAvailableVehicles(query); rows.value = response.rows; total.value = response.total } finally { loading.value = false } }
function search() { query.pageNum = 1; load() }
onMounted(load)
</script>
<style scoped>
.browse-page { padding:22px; color:#173a46; }
.browse-hero { position:relative; min-height:235px; display:flex; align-items:center; overflow:hidden; padding:34px 42px; background:#123c49; color:#f6faf7; border-radius:8px 8px 0 0; }
.hero-copy { position:relative; z-index:1; }.eyebrow { margin:0; color:#76dec6; font-size:10px; font-weight:700; letter-spacing:.2em; }
h1 { font-size:30px; line-height:1.35; letter-spacing:.04em; margin:14px 0; }.hero-text { color:#bad5d5; font-size:13px; }
.hero-orbit { position:absolute; width:260px; height:260px; right:9%; top:-8px; border:1px solid #4f858b; border-radius:50%; display:grid; place-items:center; color:#7edfcc; font-size:60px; font-weight:800; letter-spacing:-.07em; }
.hero-orbit i,.hero-orbit b { position:absolute; border:1px solid #4f858b; border-radius:50%; }.hero-orbit i { inset:25px; }.hero-orbit b { width:12px; height:12px; background:#9ce8bd; border:none; top:54px; right:30px; }
.browse-content { padding:28px; background:#f5f9f7; border:1px solid #e0ece6; }.section-heading { display:flex; align-items:end; justify-content:space-between; gap:18px; margin-bottom:22px; }.eyebrow.dark { color:#509080; }.section-heading h2 { margin:7px 0 0; font-size:20px; }.section-heading h2 span { color:#5aa78c; font-size:14px; margin-left:6px; }.search { width:240px; }
.vehicle-grid { min-height:160px; display:grid; grid-template-columns:repeat(auto-fill,minmax(230px,1fr)); gap:16px; }.ride-card { background:#fff; border:1px solid #dcebe3; border-radius:6px; padding:18px; min-width:0; transition:transform .2s ease,border-color .2s ease; }.ride-card:hover { transform:translateY(-3px); border-color:#8dc8af; }.card-top { display:flex; justify-content:space-between; align-items:center; }.availability { color:#217e60; font-size:12px; font-weight:700; }.availability i { display:inline-block; width:7px; height:7px; border-radius:50%; background:#42b88c; margin-right:6px; }.card-index { color:#a6bcb8; font-size:9px; letter-spacing:.1em; }
.bike-mark { height:76px; display:flex; align-items:end; justify-content:center; gap:34px; padding-bottom:15px; position:relative; }.bike-mark div { width:38px; height:38px; border:3px solid #c8e8db; border-radius:50%; }.bike-mark span { position:absolute; width:56px; height:29px; bottom:26px; border-bottom:3px solid #79bca0; border-left:3px solid #79bca0; transform:skewX(-26deg); }
.card-detail { border-top:1px solid #eff4f1; display:flex; gap:12px; justify-content:space-between; padding-top:15px; }.card-detail small,.card-detail strong { display:block; }.card-detail small { color:#91a8a1; font-size:11px; margin-bottom:6px; }.card-detail strong { font-size:14px; overflow-wrap:anywhere; }.station { text-align:right; }
@media (prefers-reduced-motion:reduce) { .ride-card { transition:none; } }
@media (max-width:650px) { .browse-page { padding:10px; }.browse-hero { padding:28px 24px; min-height:210px; }.hero-orbit { opacity:.25; right:-85px; }.browse-content { padding:18px; }.section-heading { align-items:start; flex-direction:column; }.search { width:100%; }h1 { font-size:24px; } }
</style>
