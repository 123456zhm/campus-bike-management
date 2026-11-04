<template>
  <el-dialog :model-value="modelValue" title="报修进度" width="560px" append-to-body @update:model-value="$emit('update:modelValue', $event)">
    <div v-if="detail.order" class="detail">
      <div class="detail-heading"><span class="code">{{ detail.order.repairNo }}</span><el-tag :type="statusType(detail.order.status)">{{ statusText(detail.order.status) }}</el-tag></div>
      <h3>车辆 {{ detail.order.vehicleNo }}</h3><p class="description">{{ detail.order.description }}</p>
      <h4>处理记录</h4>
      <el-timeline v-if="detail.records?.length"><el-timeline-item v-for="record in detail.records" :key="record.id" :timestamp="parseTime(record.createdAt)" placement="top" type="primary"><strong>{{ statusText(record.toStatus) }}</strong><div class="note">{{ record.note }}</div><small v-if="record.operatorName">处理人：{{ record.operatorName }}</small></el-timeline-item></el-timeline>
      <el-empty v-else description="暂无处理记录" :image-size="70" />
    </div>
  </el-dialog>
</template>
<script setup name="RepairDetail">
defineProps({ modelValue: Boolean, detail: { type: Object, default: () => ({}) } })
defineEmits(['update:modelValue'])
const { proxy } = getCurrentInstance()
const parseTime = proxy.parseTime
const statusText = status => ({ new: '提交前', pending: '待处理', processing: '处理中', completed: '已完成' }[status] || status)
const statusType = status => ({ pending: 'warning', processing: 'primary', completed: 'success' }[status] || 'info')
</script>
<style scoped>
.detail-heading{display:flex;align-items:center;justify-content:space-between;gap:12px}.code{color:#778b81;font-size:12px;letter-spacing:.04em}h3{margin:18px 0 8px;font-size:18px;color:#173631}.description{white-space:pre-wrap;overflow-wrap:anywhere;color:#5e7168;line-height:1.65;margin:0}h4{margin:24px 0 16px;color:#173631}.note{white-space:pre-wrap;overflow-wrap:anywhere;margin-top:6px;color:#5e7168}small{display:block;margin-top:5px;color:#91a099}
</style>
