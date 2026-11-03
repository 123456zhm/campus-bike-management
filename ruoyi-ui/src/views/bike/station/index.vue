<template>
  <div class="station-page app-container">
    <header class="station-heading">
      <div><span class="eyebrow">CAMPUS MOBILITY / STATIONS</span><h1>站点管理</h1><p>维护校园站点，掌握车辆归属与可用情况。</p></div>
      <el-button type="primary" icon="Plus" @click="openAdd" v-hasPermi="['bike:station:add']">新增站点</el-button>
    </header>
    <section class="workspace">
      <el-form ref="queryRef" :model="query" inline class="filters" @submit.prevent>
        <el-form-item label="站点编号" prop="stationNo"><el-input v-model="query.stationNo" placeholder="输入站点编号" clearable @keyup.enter="search" /></el-form-item>
        <el-form-item label="站点名称" prop="name"><el-input v-model="query.name" placeholder="输入站点名称" clearable @keyup.enter="search" /></el-form-item>
        <el-form-item label="状态" prop="status"><el-select v-model="query.status" placeholder="全部状态" clearable style="width: 130px"><el-option label="启用" value="enabled" /><el-option label="停用" value="disabled" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" icon="Search" @click="search">查询</el-button><el-button icon="Refresh" @click="resetSearch">重置</el-button></el-form-item>
      </el-form>
      <div class="table-title"><div><strong>站点列表</strong><span> · {{ total }} 个结果</span></div><small>借出车辆按最后所属站点计入归属；可借车辆排除未完成订单和报修</small></div>
      <el-alert v-if="loadError" title="站点列表加载失败，请检查网络后重试" type="error" :closable="false" show-icon class="load-error" />
      <el-table v-loading="loading" :data="stations" empty-text="暂无站点，新增站点后即可录入车辆">
        <el-table-column prop="stationNo" label="站点编号" min-width="120" />
        <el-table-column prop="name" label="站点名称" min-width="150" />
        <el-table-column prop="locationText" label="位置" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="90"><template #default="{ row }"><el-tag :type="row.status === 'enabled' ? 'success' : 'info'" effect="light">{{ row.status === 'enabled' ? '启用' : '停用' }}</el-tag></template></el-table-column>
        <el-table-column prop="totalCount" label="归属总数" width="95" align="center" />
        <el-table-column prop="idleCount" label="空闲" width="72" align="center" />
        <el-table-column prop="availableCount" label="可借" width="72" align="center" />
        <el-table-column prop="rentedCount" label="借出" width="72" align="center" />
        <el-table-column prop="repairCount" label="维修" width="72" align="center" />
        <el-table-column prop="scrappedCount" label="报废" width="72" align="center" />
        <el-table-column label="操作" width="140" fixed="right"><template #default="{ row }"><el-button link type="primary" @click="openEdit(row)" v-hasPermi="['bike:station:edit']">编辑</el-button><el-button link type="danger" @click="remove(row)" v-hasPermi="['bike:station:remove']">删除</el-button></template></el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />
    </section>
    <el-dialog v-model="dialog" :title="form.id ? '编辑站点' : '新增站点'" width="520px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="站点编号" prop="stationNo"><el-input v-model="form.stationNo" maxlength="32" placeholder="例如 ST001" /></el-form-item>
        <el-form-item label="站点名称" prop="name"><el-input v-model="form.name" maxlength="100" placeholder="例如 教学楼站" /></el-form-item>
        <el-form-item label="位置描述" prop="locationText"><el-input v-model="form.locationText" maxlength="255" placeholder="填写师生容易找到的位置" /></el-form-item>
        <el-form-item label="站点状态" prop="status"><el-radio-group v-model="form.status"><el-radio value="enabled">启用</el-radio><el-radio value="disabled">停用</el-radio></el-radio-group></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialog = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存站点</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup name="BikeStationAdmin">
import { listStations, getStation, addStation, updateStation, deleteStation } from '@/api/bike/station'
const { proxy } = getCurrentInstance()
const stations = ref([]), total = ref(0), loading = ref(false), loadError = ref(false), dialog = ref(false), saving = ref(false)
const query = reactive({ pageNum: 1, pageSize: 10, stationNo: '', name: '', status: '' })
const form = ref({})
const rules = { stationNo: [{ required: true, whitespace: true, message: '请输入站点编号', trigger: 'blur' }], name: [{ required: true, whitespace: true, message: '请输入站点名称', trigger: 'blur' }], locationText: [{ required: true, whitespace: true, message: '请输入位置描述', trigger: 'blur' }], status: [{ required: true, message: '请选择状态', trigger: 'change' }] }
async function load() { loading.value = true; loadError.value = false; try { const data = await listStations(query); stations.value = data.rows; total.value = data.total } catch { loadError.value = true; stations.value = []; total.value = 0 } finally { loading.value = false } }
function search() { query.pageNum = 1; load() }
function resetSearch() { proxy.resetForm('queryRef'); search() }
function openAdd() { form.value = { stationNo: '', name: '', locationText: '', status: 'enabled' }; dialog.value = true }
async function openEdit(row) { const res = await getStation(row.id); form.value = { ...res.data }; dialog.value = true }
async function save() { if (saving.value) return; const valid = await proxy.$refs.formRef.validate().catch(() => false); if (!valid) return; saving.value = true; try { await (form.value.id ? updateStation(form.value) : addStation(form.value)); proxy.$modal.msgSuccess('保存成功'); dialog.value = false; await load() } finally { saving.value = false } }
async function remove(row) { try { await proxy.$modal.confirm('确定删除站点“' + row.name + '”？有车辆或历史订单的站点只能停用。'); await deleteStation(row.id); proxy.$modal.msgSuccess('删除成功'); await load() } catch { /* 取消或错误提示由公共组件处理 */ } }
load()
</script>
<style scoped>
.station-page { --ink:#173631; --leaf:#168273; color:var(--ink); background:#f3f7f4; min-height:calc(100vh - 84px); }
.station-heading { display:flex; align-items:flex-end; justify-content:space-between; gap:20px; padding:12px 2px 24px; }
.eyebrow { color:var(--leaf); font-size:11px; font-weight:700; letter-spacing:.16em; }
h1 { margin:8px 0 6px; font-size:27px; line-height:1.2; font-weight:700; letter-spacing:.02em; }
.station-heading p { margin:0; color:#637a73; font-size:14px; }
.workspace { padding:22px; background:#fff; border:1px solid #e0ebe5; border-radius:12px; box-shadow:0 8px 26px rgba(28,70,51,.04); }
.filters { padding-bottom:5px; border-bottom:1px solid #edf2ef; }
.table-title { display:flex; align-items:center; justify-content:space-between; gap:12px; padding:22px 0 14px; }
.table-title strong { font-size:16px; }.table-title span,.table-title small { color:#82948b; }.load-error { margin-bottom:12px; }
@media(max-width:768px) { .station-heading,.table-title { align-items:flex-start; flex-direction:column; } .workspace { padding:16px; } }
</style>
