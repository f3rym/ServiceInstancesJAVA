import client from './client'
import type { DatacenterDto } from '@/types'

const BASE = '/datacenters'

export const datacentersApi = {
  getAll: () => client.get<DatacenterDto[]>(BASE).then((r) => r.data),
  getById: (id: number) => client.get<DatacenterDto>(`${BASE}/${id}`).then((r) => r.data),
  create: (dto: Omit<DatacenterDto, 'id' | 'instanceIds'>) =>
    client.post<DatacenterDto>(BASE, dto).then((r) => r.data),
  update: (id: number, dto: Omit<DatacenterDto, 'instanceIds'>) =>
    client.put<DatacenterDto>(`${BASE}/${id}`, dto).then((r) => r.data),
  delete: (id: number) => client.delete(`${BASE}/${id}`),
}
