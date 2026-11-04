<template>
  <div class="settings-page app-container">
    <header class="heading"><div><span>OPERATIONS / SETTINGS</span><h1>系统设置</h1><p>维护当前收费规则，并为业务数据创建备份。</p></div></header>
    <el-tabs v-model="tab" class="settings-tabs">
      <el-tab-pane label="收费规则" name="billing">
        <section class="panel"><div class="panel-title"><div><h2>当前规则</h2><p>新借出的订单使用此规则，历史订单保留原价格。</p></div></div>
          <div v-if="current" class="current-rule"><span>每 {{ current.unitMinutes }} 分钟</span><strong>¥{{ Number(current.unitPrice).toFixed(2) }}</strong><small>不足一个计费单位按一个单位计算</small></div>
          <el-empty v-else description="尚无生效规则" :image-size="70"/>
          <el-form :model="form" label-position="top" class="rule-form"><el-form-item label="规则名称"><el-input v-model="form.ruleName" maxlength="100" placeholder="例如：标准计费"/></el-form-item><el-form-item label="计费单位（分钟）"><el-input-number v-model="form.unitMinutes" :min="1" :max="1440" :step="1" :precision="0"/></el-form-item><el-form-item label="每单位价格（元）"><el-input-number v-model="form.unitPrice" :min="0" :max="99999999.99" :precision="2" :step="0.5"/></el-form-item><el-button type="primary" :loading="saving" @click="save">保存并启用</el-button></el-form>
        </section>
        <section class="panel history"><h2>历史规则</h2><el-table v-loading="loadingRules" :data="rules" empty-text="暂无规则"><el-table-column prop="ruleName" label="名称" min-width="160"/><el-table-column prop="unitMinutes" label="计费单位（分钟）" min-width="140"/><el-table-column label="价格（元）" width="130"><template #default="s">{{ Number(s.row.unitPrice).toFixed(2) }}</template></el-table-column><el-table-column label="状态" width="100"><template #default="s"><el-tag :type="s.row.isActive===1?'success':'info'">{{ s.row.isActive===1?'生效中':'历史' }}</el-tag></template></el-table-column><el-table-column label="创建时间" min-width="170"><template #default="s">{{ parseTime(s.row.createdAt) }}</template></el-table-column></el-table><pagination v-show="ruleTotal>0" :total="ruleTotal" v-model:page="ruleQuery.pageNum" v-model:limit="ruleQuery.pageSize" @pagination="loadRules"/></section>
      </el-tab-pane>
      <el-tab-pane label="数据库备份" name="backup">
        <section class="panel"><div class="panel-title"><div><h2>创建备份</h2><p>备份保存在服务器本机，仅管理员可以查看执行结果。</p></div><el-button type="primary" :loading="backingUp" @click="backup">立即备份</el-button></div><el-table v-loading="loadingBackups" :data="backups" empty-text="暂无备份记录"><el-table-column label="时间" min-width="170"><template #default="s">{{ parseTime(s.row.createdAt) }}</template></el-table-column><el-table-column label="状态" width="110"><template #default="s"><el-tag :type="s.row.status==='success'?'success':s.row.status==='failed'?'danger':'warning'">{{ ({success:'成功',failed:'失败',running:'进行中'})[s.row.status]||s.row.status }}</el-tag></template></el-table-column><el-table-column prop="filePath" label="文件位置" min-width="240" show-overflow-tooltip/><el-table-column prop="resultMessage" label="结果" min-width="160" show-overflow-tooltip/></el-table><pagination v-show="backupTotal>0" :total="backupTotal" v-model:page="backupQuery.pageNum" v-model:limit="backupQuery.pageSize" @pagination="loadBackups"/></section>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup name="CampusSettings">
import { currentBilling,listBillingRules,saveBillingRule,listBackups,createBackup } from '@/api/campus/settings'
const { proxy }=getCurrentInstance()
const tab=ref('billing'),current=ref(null),rules=ref([]),backups=ref([]),ruleTotal=ref(0),backupTotal=ref(0),loadingRules=ref(false),loadingBackups=ref(false),saving=ref(false),backingUp=ref(false)
const form=reactive({ruleName:'标准计费',unitMinutes:30,unitPrice:1}),ruleQuery=reactive({pageNum:1,pageSize:10}),backupQuery=reactive({pageNum:1,pageSize:10})
async function loadRules(){loadingRules.value=true;try{const [a,b]=await Promise.all([currentBilling(),listBillingRules(ruleQuery)]);current.value=a.data;rules.value=b.rows;ruleTotal.value=b.total}finally{loadingRules.value=false}}
async function loadBackups(){loadingBackups.value=true;try{const r=await listBackups(backupQuery);backups.value=r.rows;backupTotal.value=r.total}finally{loadingBackups.value=false}}
async function save(){if(!form.ruleName.trim()||!form.unitMinutes||form.unitPrice==null){proxy.$modal.msgWarning('请完整填写收费规则');return}try{await proxy.$modal.confirm('启用新规则后，新借车订单将使用新价格，确认继续？')}catch{return}saving.value=true;try{await saveBillingRule({...form,ruleName:form.ruleName.trim()});proxy.$modal.msgSuccess('收费规则已启用');await loadRules()}finally{saving.value=false}}
async function backup(){backingUp.value=true;try{const r=await createBackup();if(r.data.status==='success')proxy.$modal.msgSuccess('备份完成');else proxy.$modal.msgError(r.data.resultMessage||'备份失败');await loadBackups()}finally{backingUp.value=false}}
loadRules();loadBackups()
</script>
<style scoped>.settings-page{min-height:calc(100vh - 84px);background:#f3f8f6;color:#183d36}.heading{padding:20px 4px 22px}.heading span{font-size:10px;letter-spacing:.18em;color:#218b75;font-weight:700}.heading h1{margin:9px 0;font-size:28px}.heading p,.panel p{color:#718b83;font-size:13px}.settings-tabs{padding:0 20px 20px;background:#fff;border:1px solid #e1ebe5;border-radius:10px}.panel{padding:22px;border:1px solid #e1ebe5;border-radius:8px}.history{margin-top:14px}.panel h2{margin:0;font-size:17px}.panel-title{display:flex;justify-content:space-between;align-items:center;gap:20px}.current-rule{display:flex;align-items:baseline;gap:14px;margin:22px 0;padding:18px 20px;background:#e9f6ed;border-radius:8px;color:#28765f}.current-rule strong{font-size:28px}.current-rule small{color:#718b83}.rule-form{display:flex;align-items:end;flex-wrap:wrap;gap:14px}.rule-form :deep(.el-form-item){margin-bottom:0}.rule-form .el-button{margin-bottom:1px}@media(max-width:650px){.panel-title,.current-rule{align-items:flex-start;flex-direction:column}.rule-form{align-items:stretch;flex-direction:column}}</style>
