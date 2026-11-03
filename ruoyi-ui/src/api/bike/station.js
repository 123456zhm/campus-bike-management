import request from '@/utils/request'

export const listStations = params => request({ url: '/bike/station/list', method: 'get', params })
export const browseStations = params => request({ url: '/bike/station/browse', method: 'get', params })
export const getStation = id => request({ url: '/bike/station/' + id, method: 'get' })
export const addStation = data => request({ url: '/bike/station', method: 'post', data })
export const updateStation = data => request({ url: '/bike/station', method: 'put', data })
export const deleteStation = ids => request({ url: '/bike/station/' + ids, method: 'delete' })
