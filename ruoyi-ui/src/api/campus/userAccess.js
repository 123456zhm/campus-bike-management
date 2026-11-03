import request from '@/utils/request'

export function getBlacklist(userId) {
  return request({ url: `/campus/user/blacklist/${userId}`, method: 'get' })
}

export function updateBlacklist(data) {
  return request({ url: '/campus/user/blacklist', method: 'put', data })
}
