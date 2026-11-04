import request from '@/utils/request'
export const listMyRepairs = params => request({ url: '/bike/repair/my/list', method: 'get', params })
export const getMyRepair = id => request({ url: '/bike/repair/my/' + id, method: 'get' })
export const submitRepair = data => request({ url: '/bike/repair', method: 'post', data })
export const listRepairs = params => request({ url: '/bike/repair/list', method: 'get', params })
export const getRepair = id => request({ url: '/bike/repair/' + id, method: 'get' })
export const handleRepair = (id, data) => request({ url: '/bike/repair/' + id + '/handle', method: 'put', data })
