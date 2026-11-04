import request from '@/utils/request'
export function getMyRentals(params) { return request({url:'/campus/rental/mine',method:'get',params}) }
export function getAllRentals(params) { return request({url:'/campus/rental/list',method:'get',params}) }
export function borrowVehicle(vehicleId) { return request({url:'/campus/rental/borrow',method:'post',data:{vehicleId}}) }
export function returnVehicle(orderId,stationId) { return request({url:'/campus/rental/return',method:'post',data:{orderId,stationId}}) }
export function markException(orderId,reason) { return request({url:'/campus/rental/exception',method:'post',data:{orderId,reason}}) }
export function resolveException(orderId,stationId,reason) { return request({url:'/campus/rental/exception/resolve',method:'post',data:{orderId,stationId,reason}}) }
