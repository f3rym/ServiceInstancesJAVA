import client from './client'
import type { OrderDto } from '@/types'

const BASE = '/orders'

export const ordersApi = {
  getAll: () => client.get<OrderDto[]>(BASE).then((r) => r.data),
  createBulk: (dtos: OrderDto[], trl = true) =>
    client.post<OrderDto[]>(`${BASE}/bulk`, dtos, { params: { trl } }).then((r) => r.data),
  transaction: (username: string, instanceId: number) =>
    client
      .post<string>(`${BASE}/transaction`, null, { params: { username, instanceId } })
      .then((r) => r.data),
  delete: (id: number) => client.delete(`${BASE}/${id}`),
}
