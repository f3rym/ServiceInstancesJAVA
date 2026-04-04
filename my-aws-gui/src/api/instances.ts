import client from './client'
import type { InstanceDto, InstanceFilterDto, Page } from '@/types'

const BASE = '/instances'

export const instancesApi = {
  getAll: () => client.get<InstanceDto[]>(BASE).then((r) => r.data),
  getById: (id: number) => client.get<InstanceDto>(`${BASE}/${id}`).then((r) => r.data),
  create: (dto: InstanceDto) => client.post<InstanceDto>(BASE, dto).then((r) => r.data),
  update: (id: number, dto: InstanceDto) =>
    client.put<InstanceDto>(`${BASE}/${id}`, dto).then((r) => r.data),
  delete: (id: number) => client.delete(`${BASE}/${id}`),
  search: (filter: InstanceFilterDto, page = 0, size = 10) =>
    client
      .get<Page<InstanceDto>>(`${BASE}/search`, {
        params: {
          price: filter.price || undefined,
          datacenterLocation: filter.datacenterLocation || undefined,
          useNative: filter.useNative,
          page,
          size,
        },
      })
      .then((r) => r.data),
}
