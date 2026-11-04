import request from '@/utils/request'

export function listAllBills(params) {
  return request({ url: '/campus/bill/list', method: 'get', params })
}
export function listMyBills(params) {
  return request({ url: '/campus/bill/mine', method: 'get', params })
}
export function listMyPayments(params) {
  return request({ url: '/campus/bill/mine/records', method: 'get', params })
}
export function simulateBillPayment(billId, requestKey) {
  return request({ url: '/campus/bill/mine/' + billId + '/pay', method: 'post', data: { requestKey } })
}
