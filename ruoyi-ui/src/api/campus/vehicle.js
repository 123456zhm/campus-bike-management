import request from '@/utils/request'

export function listVehicles(params) {
  return request({ url: '/campus/vehicle/list', method: 'get', params })
}
export function listAvailableVehicles(params) {
  return request({ url: '/campus/vehicle/available', method: 'get', params })
}
export function getVehicle(id) {
  return request({ url: '/campus/vehicle/' + id, method: 'get' })
}
export function listVehicleStations() {
  return request({ url: '/campus/vehicle/stations', method: 'get' })
}
export function addVehicle(data) {
  return request({ url: '/campus/vehicle', method: 'post', data })
}
export function updateVehicle(data) {
  return request({ url: '/campus/vehicle', method: 'put', data })
}
