import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { datacentersApi } from '@/api/datacenters'
import { instancesApi } from '@/api/instances'
import { usersApi } from '@/api/users'
import { ordersApi } from '@/api/orders'
import { softwareApi } from '@/api/software'

interface Counts {
  datacenters: number
  instances: number
  users: number
  orders: number
  software: number
  avgPrice: number
}

export function Dashboard() {
  const [counts, setCounts] = useState<Counts | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.allSettled([
      datacentersApi.getAll(),
      instancesApi.getAll(),
      usersApi.getAll(),
      ordersApi.getAll(),
      softwareApi.getAll(),
    ]).then(([dc, inst, usr, ord, sw]) => {
      const instances = inst.status === 'fulfilled' ? inst.value : []
      const avgPrice  = instances.length
        ? instances.reduce((s, i) => s + i.price, 0) / instances.length
        : 0
      setCounts({
        datacenters: dc.status  === 'fulfilled' ? dc.value.length  : 0,
        instances:   instances.length,
        users:       usr.status === 'fulfilled' ? usr.value.length : 0,
        orders:      ord.status === 'fulfilled' ? ord.value.length : 0,
        software:    sw.status  === 'fulfilled' ? sw.value.length  : 0,
        avgPrice,
      })
    }).finally(() => setLoading(false))
  }, [])

  const cards = [
    { to: '/instances',   label: 'Инстансы',    value: counts?.instances,   sub: `avg $${counts?.avgPrice.toFixed(2)}/ч`,   dot: '#58a6ff' },
    { to: '/datacenters', label: 'Дата-центры',  value: counts?.datacenters, sub: 'ManyToMany ↔ Instances',                   dot: '#3fb950' },
    { to: '/software',    label: 'ПО',           value: counts?.software,    sub: 'ManyToMany ↔ Instances',                   dot: '#bc8cff' },
    { to: '/users',       label: 'Пользователи', value: counts?.users,       sub: 'OneToMany → Orders',                       dot: '#ff9f43' },
    { to: '/orders',      label: 'Заказы',       value: counts?.orders,      sub: 'userId + instanceId',                      dot: '#39d353' },
  ]

  const relations = [
    {
      title: 'Datacenter ↔ Instance',
      type: 'ManyToMany',
      color: '#3fb950',
      desc: 'Один инстанс может размещаться в нескольких ЦОД. DatacenterDto содержит instanceIds, InstanceDto — datacenterIds.',
    },
    {
      title: 'Instance ↔ Software',
      type: 'ManyToMany',
      color: '#bc8cff',
      desc: 'Инстанс может иметь несколько предустановленных ПО. InstanceDto хранит список softwareIds.',
    },
    {
      title: 'User → Order',
      type: 'OneToMany',
      color: '#ff9f43',
      desc: 'Пользователь может иметь несколько заказов. UserDto содержит список orderIds (READ_ONLY).',
    },
  ]

  return (
    <div>
      <div style={{ marginBottom: 28 }}>
        <div className="page-title" style={{ fontSize: 24, marginBottom: 6 }}>
          FeInstances — обзор
        </div>
        <div className="page-sub">React + TypeScript SPA · Spring Boot API</div>
      </div>

      {loading ? (
        <div className="loading"><div className="spinner" /> Загрузка…</div>
      ) : (
        <div className="stats-grid" style={{ marginBottom: 32 }}>
          {cards.map(({ to, label, value, sub, dot }) => (
            <Link key={to} to={to} style={{ textDecoration: 'none' }}>
              <div
                className="stat-card"
                style={{ cursor: 'pointer' }}
                onMouseEnter={(e) => (e.currentTarget.style.borderColor = 'var(--border2)')}
                onMouseLeave={(e) => (e.currentTarget.style.borderColor = 'var(--border)')}
              >
                <div className="stat-label" style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                  <span style={{ width: 7, height: 7, borderRadius: '50%', background: dot, display: 'inline-block' }} />
                  {label}
                </div>
                <div className="stat-value c-blue" style={{ fontSize: 28 }}>{value ?? '—'}</div>
                <div className="stat-sub">{sub}</div>
              </div>
            </Link>
          ))}
        </div>
      )}

      {/* Relations */}
      <div style={{ marginBottom: 10, fontSize: 14, fontWeight: 500 }}>Связи между сущностями</div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: 12, marginBottom: 32 }}>
        {relations.map(({ title, type, color, desc }) => (
          <div
            key={title}
            style={{
              background: 'var(--bg2)',
              border: '1px solid var(--border)',
              borderLeft: `3px solid ${color}`,
              borderRadius: 8,
              padding: '14px 16px',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 6 }}>
              <span style={{ fontSize: 13, fontWeight: 600 }}>{title}</span>
              <span style={{
                background: `${color}22`, color, border: `1px solid ${color}44`,
                borderRadius: 10, padding: '1px 8px', fontSize: 11, fontWeight: 500,
              }}>
                {type}
              </span>
            </div>
            <p style={{ fontSize: 12, color: 'var(--text2)', lineHeight: 1.6 }}>{desc}</p>
          </div>
        ))}
      </div>

      {/* API info */}
      <div style={{
        background: 'var(--bg2)', border: '1px solid var(--border)', borderRadius: 8,
        padding: '16px 20px', maxWidth: 520,
      }}>
        <div style={{ fontSize: 12, color: 'var(--text3)', marginBottom: 8 }}>API базовый URL</div>
        <code style={{ fontFamily: 'var(--mono)', fontSize: 13, color: 'var(--blue)' }}>
          http://localhost:8080/api/v1
        </code>
        <div style={{ marginTop: 12, display: 'flex', gap: 8 }}>
          <a href="http://localhost:8080/swagger-ui.html" target="_blank" rel="noreferrer" className="btn btn-sm">
            Swagger UI ↗
          </a>
        </div>
      </div>
    </div>
  )
}
