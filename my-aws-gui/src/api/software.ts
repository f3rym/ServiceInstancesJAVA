import client from './client'
import type { SoftwareDto } from '@/types'

const BASE = '/software'

export const softwareApi = {
  getAll: () => client.get<SoftwareDto[]>(BASE).then((r) => r.data),
  getById: (id: number) => client.get<SoftwareDto>(`${BASE}/${id}`).then((r) => r.data),
  create: (dto: SoftwareDto) => client.post<SoftwareDto>(BASE, dto).then((r) => r.data),
  update: (id: number, dto: SoftwareDto) =>
    client.put<SoftwareDto>(`${BASE}/${id}`, dto).then((r) => r.data),
  delete: (id: number) => client.delete(`${BASE}/${id}`),
}
