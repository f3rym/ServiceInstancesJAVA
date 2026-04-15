import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { ordersApi } from '@/api/orders'
import { usersApi } from '@/api/users'
import { instancesApi } from '@/api/instances'
import type { OrderDto, UserDto, InstanceDto } from '@/types'
import { Modal } from '@/components/Modal'
import { ConfirmDialog } from '@/components/ConfirmDialog'

interface Props {
  toast: (msg: string, type?: 'success' | 'error' | 'info') => void
}

export function Orders({ toast }: Props) {
  const [searchParams] = useSearchParams()
  const userIdFilter = searchParams.get('userId') ? Number(searchParams.get('userId')) : null

  const [rows, setRows]           = useState<OrderDto[]>([])
  const [loading, setLoading]     = useState(true)
  const [error, setError]         = useState('')
  const [users, setUsers]         = useState<UserDto[]>([])
  const [instances, setInstances] = useState<InstanceDto[]>([])

  const [modal, setModal]         = useState<'create' | 'transaction' | null>(null)
  const [saving, setSaving]       = useState(false)
  const [deleteTarget, setDeleteTarget] = useState<OrderDto | null>(null)
  const [deleting, setDeleting]   = useState(false)

  // Single order form
  const [form, setForm] = useState<{ userId: number; instanceId: number }>({
    userId: 0, instanceId: 0,
  })

  // Transaction form
  const [txUsername, setTxUsername]     = useState('')
  const [txInstanceId, setTxInstanceId] = useState(0)

  const load = () => {
    setLoading(true)
    ordersApi.getAll()
      .then((data) => {
        setRows(userIdFilter ? data.filter((o) => o.userId === userIdFilter) : data)
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [userIdFilter]) // eslint-disable-line

  useEffect(() => {
    usersApi.getAll().then(setUsers).catch(() => {})
    instancesApi.getAll().then(setInstances).catch(() => {})
  }, [])

  // lookup helpers
  const userName = (id: number) => users.find((u) => u.id === id)?.username ?? `User #${id}`
  const instName = (id: number) => instances.find((i) => i.id === id)?.name ?? `Instance #${id}`

  const handleCreate = async () => {
    if (!form.userId || !form.instanceId) {
      toast('Выберите пользователя и инстанс', 'error')
      return
    }
    setSaving(true)
    try {
      await ordersApi.createBulk([form as OrderDto])
      toast('Заказ создан')
      setModal(null)
      load()
    } catch (e: any) {
      toast(e.message, 'error')
    } finally {
      setSaving(false)
    }
  }

  const handleTransaction = async () => {
    if (!txUsername || !txInstanceId) {
      toast('Заполните все поля', 'error')
      return
    }
    setSaving(true)
    try {
      const result = await ordersApi.transaction(txUsername, txInstanceId)
      toast(result || 'Транзакция выполнена')
      setModal(null)
      load()
    } catch (e: any) {
      toast(e.message, 'error')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget?.id) return
    setDeleting(true)
    try {
      await ordersApi.delete(deleteTarget.id)
      toast('Заказ удалён')
      setDeleteTarget(null)
      load()
    } catch (e: any) {
      toast(e.message, 'error')
    } finally {
      setDeleting(false)
    }
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Заказы</div>
          <div className="page-sub">
            Аренда серверов
            {userIdFilter && (
              <> · <span style={{ color: 'var(--blue)' }}>Фильтр: User #{userIdFilter}</span></>
            )}
          </div>
        </div>
        <div className="page-actions">
          <button className="btn" onClick={() => { setTxUsername(''); setTxInstanceId(0); setModal('transaction') }}>
            ⚡ Транзакция
          </button>
          <button
            className="btn btn-primary"
            onClick={() => { setForm({ userId: 0, instanceId: 0 }); setModal('create') }}
          >
            + Новый заказ
          </button>
        </div>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <div className="table-wrap">
        <div className="table-toolbar">
          <span className="table-toolbar-title">
            Заказы <span className="c-text2">({rows.length})</span>
          </span>
          <button className="btn btn-sm" onClick={load}>↺</button>
        </div>

        {loading ? (
          <div className="loading"><div className="spinner" /> Загрузка…</div>
        ) : rows.length === 0 ? (
          <div className="empty-state">
            <span className="empty-icon">📋</span>
            <span className="empty-text">Заказы не найдены</span>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Пользователь</th>
                <th>Инстанс</th>
                <th>Тип / ОС</th>
                <th>Цена / ч</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r) => {
                const inst = instances.find((i) => i.id === r.instanceId)
                return (
                  <tr key={r.id}>
                    <td><span className="mono c-text2">#{r.id}</span></td>
                    <td>
                      <span style={{ fontWeight: 500 }}>{userName(r.userId)}</span>
                    </td>
                    <td>
                      <span style={{ fontWeight: 500 }}>{instName(r.instanceId)}</span>
                    </td>
                    <td>
                      {inst ? (
                        <div style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
                          <span className="pill">{inst.instanceType}</span>
                          <span style={{ fontSize: 11, color: 'var(--text3)' }}>{inst.os}</span>
                        </div>
                      ) : <span style={{ color: 'var(--text3)' }}>—</span>}
                    </td>
                    <td>
                      {inst ? (
                        <span className="mono" style={{ color: 'var(--accent2)' }}>
                          ${inst.price.toFixed(2)}
                        </span>
                      ) : '—'}
                    </td>
                    <td>
                      <button className="btn btn-sm btn-danger" onClick={() => setDeleteTarget(r)}>
                        Удалить
                      </button>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        )}
      </div>

      {/* New Order */}
      {modal === 'create' && (
        <Modal
          title="Новый заказ"
          onClose={() => setModal(null)}
          onConfirm={handleCreate}
          loading={saving}
        >
          <div className="form-grid">
            <div className="form-group">
              <label>Пользователь *</label>
              <select
                value={form.userId}
                onChange={(e) => setForm((f) => ({ ...f, userId: +e.target.value }))}
              >
                <option value={0}>— выберите пользователя —</option>
                {users.map((u) => <option key={u.id} value={u.id}>{u.username}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Инстанс *</label>
              <select
                value={form.instanceId}
                onChange={(e) => setForm((f) => ({ ...f, instanceId: +e.target.value }))}
              >
                <option value={0}>— выберите инстанс —</option>
                {instances.map((i) => (
                  <option key={i.id} value={i.id}>
                    {i.name} ({i.instanceType}, ${i.price}/ч)
                  </option>
                ))}
              </select>
            </div>
          </div>
        </Modal>
      )}

      {/* Transaction */}
      {modal === 'transaction' && (
        <Modal
          title="⚡ Транзакция: новый пользователь + заказ"
          onClose={() => setModal(null)}
          onConfirm={handleTransaction}
          confirmLabel="Выполнить"
          loading={saving}
        >
          <p style={{ fontSize: 12, color: 'var(--text3)', marginBottom: 16 }}>
            Создаёт нового пользователя (если не существует) и привязывает его к инстансу в рамках одной транзакции.
          </p>
          <div className="form-grid">
            <div className="form-group full">
              <label>Username *</label>
              <input
                type="text"
                value={txUsername}
                onChange={(e) => setTxUsername(e.target.value)}
                placeholder="new_user_123"
                autoFocus
              />
            </div>
            <div className="form-group full">
              <label>Инстанс *</label>
              <select value={txInstanceId} onChange={(e) => setTxInstanceId(+e.target.value)}>
                <option value={0}>— выберите инстанс —</option>
                {instances.map((i) => (
                  <option key={i.id} value={i.id}>
                    {i.name} ({i.instanceType}, ${i.price}/ч)
                  </option>
                ))}
              </select>
            </div>
          </div>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          message={`Удалить заказ #${deleteTarget.id}?`}
          onConfirm={handleDelete}
          onCancel={() => setDeleteTarget(null)}
          loading={deleting}
        />
      )}
    </div>
  )
}
