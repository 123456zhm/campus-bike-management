<template>
  <div class="browse-page app-container">
    <header class="hero"><span class="eyebrow">CAMPUS MOBILITY</span><h1>找到附近的站点</h1><p>查看校园站点和可借车辆，选好出发地点。</p></header>
    <section class="content">
      <el-form :model="query" inline @submit.prevent><el-form-item label="站点名称"><el-input v-model="query.name" placeholder="搜索站点" clearable @keyup.enter="search" /></el-form-item><el-form-item><el-button type="primary" icon="Search" @click="search">查询</el-button></el-form-item></el-form>
      <el-alert v-if="error" title="站点加载失败，请稍后重试" type="error" :closable="false" show-icon />
      <div v-loading="loading" class="station-grid">
        <article v-for="station in stations" :key="station.id" class="card"><div class="card-top"><span class="card-code">{{ station.stationNo }}</span><span class="status"><i></i>开放中</span></div><h2>{{ station.name }}</h2><p>{{ station.locationText }}</p><div class="available"><span>当前可借</span><strong>{{ station.availableCount }}</strong><span>辆</span></div></article>
        <el-empty v-if="!loading && !error && !stations.length" description="没有找到站点" />
      </div>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />
    </section>
  </div>
</template>
<script setup name="BikeStationBrowse">
import { browseStations } from '@/api/bike/station'
const stations = ref([]), total = ref(0), loading = ref(false), error = ref(false)
const query = reactive({ pageNum: 1, pageSize: 12, name: '' })
async function load() { loading.value = true; error.value = false; try { const data = await browseStations(query); stations.value = data.rows; total.value = data.total } catch { error.value = true; stations.value = []; total.value = 0 } finally { loading.value = false } }
function search() { query.pageNum = 1; load() }
load()
</script>
<style scoped>
.browse-page { min-height:calc(100vh - 84px); color:#173631; background:#f3f7f4; }.hero { padding:22px 4px 27px; }.eyebrow { color:#168273; font-size:11px; font-weight:700; letter-spacing:.16em; }h1 { margin:8px 0; font-size:29px; } .hero p { margin:0; color:#71867c; }.content { padding:24px; border:1px solid #e1ebe5; border-radius:12px; background:#fff; }.station-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(235px,1fr)); gap:16px; min-height:100px; }.card { padding:20px; border:1px solid #dfeae3; border-radius:10px; background:#fbfdfb; transition:transform .18s ease,border-color .18s ease; }.card:hover { transform:translateY(-3px); border-color:#9dcaba; }.card-top { display:flex; justify-content:space-between; align-items:center; }.card-code { color:#7c8e84; font-size:12px; letter-spacing:.06em; }.status { color:#168273; font-size:12px; }.status i { display:inline-block; width:7px; height:7px; margin-right:5px; border-radius:50%; background:#168273; }.card h2 { margin:20px 0 8px; font-size:19px; }.card p { min-height:38px; margin:0; color:#71867c; font-size:13px; }.available { display:flex; align-items:baseline; gap:6px; margin-top:14px; padding-top:14px; border-top:1px solid #e7eee9; color:#71867c; font-size:13px; }.available strong { margin-left:auto; color:#168273; font-size:29px; line-height:1; }@media(prefers-reduced-motion:reduce){.card{transition:none}.card:hover{transform:none}}
</style>
