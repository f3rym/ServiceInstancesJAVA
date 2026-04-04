import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { usersApi } from '@/api/users'
import type { UserDto } from '@/types'
import { Modal } from '@/components/Modal'
import { ConfirmDialog } from '@/components/ConfirmDialog'

interface Props {
  toast: (msg: string, type?: 'success' | 'error' | 'info') => void
}

const AVATAR_COLORS = [
  ['#bc8cff', '#6e40c9'],
  ['#58a6ff', '#1f6feb'],
  ['#3fb950', '#238636'],
  ['#ff9f43', '#e8890a'],
  ['#f78166', '#da3633'],
]

export function Users({ toast }: Props) {
  const [rows, setRows]                     = useState<UserDto[]>([])
  const [loading, setLoading]               = useState(true)
  const [error, setError]                   = useState('')
  const [search, setSearch]                 = useState('')
  const [modal, setModal]                   = useState<'create' | 'edit' | null>(null)
  const [form, setForm]                     = useState({ username: '' })
  const [editId, setEditId]                 = useState<number | undefined>()
  const [saving, setSaving]                 = useState(false)
  const [deleteTarget, setDeleteTarget]     = useState<UserDto | null>(null)
  const [deleting, setDeleting]             = useState(false)

  const load = () => {
    setLoading(true)
    usersApi.getAll()
      .then(setRows)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const filtered = rows.filter((r) =>
    r.username.toLowerCase().includes(search.toLowerCase())
  )

  const openCreate = () => { setForm({ username: '' }); setEditId(undefined); setModal('create') }
  const openEdit   = (r: UserDto) => { setForm({ username: r.username }); setEditId(r.id); setModal('edit') }

  const handleSave = async () => {
    setSaving(true)
    try {
      if (modal === 'create') {
        await usersApi.create(form)
        toast('Пользователь создан')
      } else {
        await usersApi.update(editId!, form)
        toast('Пользователь обновлён')
      }
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
      await usersApi.delete(deleteTarget.id)
      toast('Пользователь удалён')
      setDeleteTarget(null)
      load()
    } catch (e: any) {
      toast(e.message, 'error')
    } finally {
      setDeleting(false)
    }
  }

  const initials = (username: string) =>
    username.slice(0, 2).toUpperCase()

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Пользователи</div>
          <div className="page-sub">Учётные записи · OneToMany → Заказы</div>
        </div>
        <div className="page-actions">
          <button className="btn btn-primary" onClick={openCreate}>+ Новый пользователь</button>
        </div>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <div className="table-wrap">
        <div className="table-toolbar">
          <span className="table-toolbar-title">
            Пользователи <span className="c-text2">({filtered.length})</span>
          </span>
          <input
            className="table-search"
            placeholder="Поиск по username…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <button className="btn btn-sm" onClick={load}>↺</button>
        </div>

        {loading ? (
          <div className="loading"><div className="spinner" /> Загрузка…</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <span className="empty-icon">👤</span>
            <span className="empty-text">Пользователи не найдены</span>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Пользователь</th>
                <th>Заказов</th>
                <th>Заказы</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((r, i) => {
                const [bg, accent] = AVATAR_COLORS[i % AVATAR_COLORS.length]
                return (
                  <tr key={r.id}>
                    <td><span className="mono c-text2">#{r.id}</span></td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                        <div style={{
                          width: 30, height: 30, borderRadius: '50%',
                          background: `linear-gradient(135deg, ${bg}, ${accent})`,
                          display: 'flex', alignItems: 'center', justifyContent: 'center',
                          fontSize: 11, fontWeight: 700, color: '#fff', flexShrink: 0,
                        }}>
                          {initials(r.username)}
                        </div>
                        <span style={{ fontWeight: 500 }}>{r.username}</span>
                      </div>
                    </td>
                    <td>
                      <span className="mono" style={{ color: 'var(--text2)' }}>
                        {r.orderIds?.length ?? 0}
                      </span>
                    </td>
                    <td>
                      <Link
                        to={`/orders?userId=${r.id}`}
                        style={{ fontSize: 12, color: 'var(--blue)' }}
                      >
                        Просмотр заказов →
                      </Link>
                    </td>
                    <td>
                      <div className="row-actions">
                        <button className="btn btn-sm btn-ghost" onClick={() => openEdit(r)}>Изменить</button>
                        <button className="btn btn-sm btn-danger" onClick={() => setDeleteTarget(r)}>✕</button>
                      </div>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        )}
      </div>

      {modal && (
        <Modal
          title={modal === 'create' ? 'Новый пользователь' : 'Изменить пользователя'}
          onClose={() => setModal(null)}
          onConfirm={handleSave}
          loading={saving}
        >
          <div className="form-grid">
            <div className="form-group full">
              <label>Username * (мин. 2 символа)</label>
              <input
                type="text"
                value={form.username}
                onChange={(e) => setForm({ username: e.target.value })}
                placeholder="john_doe"
                autoFocus
              />
            </div>
          </div>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          message={`Удалить пользователя "${deleteTarget.username}"? Его заказы останутся в системе.`}
          onConfirm={handleDelete}
          onCancel={() => setDeleteTarget(null)}
          loading={deleting}
        />
      )}
    </div>
  )
}
