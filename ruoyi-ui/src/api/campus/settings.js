import request from '@/utils/request'
export const currentBilling = () => request({url:'/campus/settings/billing/current',method:'get'})
export const listBillingRules = params => request({url:'/campus/settings/billing',method:'get',params})
export const saveBillingRule = data => request({url:'/campus/settings/billing',method:'post',data})
export const listBackups = params => request({url:'/campus/settings/backups',method:'get',params})
export const createBackup = () => request({url:'/campus/settings/backups',method:'post',timeout:130000})
