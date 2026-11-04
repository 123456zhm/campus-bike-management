import request from '@/utils/request'
export function getCampusReport(params) {
  return request({ url: '/campus/report/summary', method: 'get', params })
}
