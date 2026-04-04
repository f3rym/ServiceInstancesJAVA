import client from './client'

const BASE = '/stats'

export const statisticsApi = {
  getStats: () => client.get<string>(BASE).then((r) => r.data),
  testRace: () => client.post<string>(`${BASE}/test-race-condition`).then((r) => r.data),
}
