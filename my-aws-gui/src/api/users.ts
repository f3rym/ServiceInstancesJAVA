import client from './client'
import type { UserDto } from '@/types'

const BASE = '/users'

export const usersApi = {
  getAll: () => client.get<UserDto[]>(BASE).then((r) => r.data),
  getById: (id: number) => client.get<UserDto>(`${BASE}/${id}`).then((r) => r.data),
  create: (dto: Pick<UserDto, 'username'>) =>
    client.post<UserDto>(BASE, dto).then((r) => r.data),
  update: (id: number, dto: Pick<UserDto, 'username'>) =>
    client.put<UserDto>(`${BASE}/${id}`, dto).then((r) => r.data),
  delete: (id: number) => client.delete(`${BASE}/${id}`),
}
