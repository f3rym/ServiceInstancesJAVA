import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { datacentersApi } from '@/api/datacenters'
import type { DatacenterDto } from '@/types'
import { Modal } from '@/components/Modal'
import { ConfirmDialog } from '@/components/ConfirmDialog'

const EMPTY = { name: '', location: '' }

interface Props {
  toast: (msg: string, type?: 'success' | 'error' | 'info') => void
}

export function Datacenters({ toast }: Props) {
  const [rows, setRows]                     = useState<DatacenterDto[]>([])
  const [loading, setLoading]               = useState(true)
  const [error, setError]                   = useState('')
  const [search, setSearch]                 = useState('')
  const [modal, setModal]                   = useState<'create' | 'edit' | null>(null)
  const [form, setForm]                     = useState(EMPTY)
  const [editId, setEditId]                 = useState<number | undefined>()
  const [saving, setSaving]                 = useState(false)
  const [deleteTarget, setDeleteTarget]     = useState<DatacenterDto | null>(null)
  const [deleting, setDeleting]             = useState(false)

  const load = () => {
    setLoading(true)
    datacentersApi.getAll()
      .then(setRows)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const filtered = rows.filter(
    (r) =>
      r.name.toLowerCase().includes(search.toLowerCase()) ||
      r.location.toLowerCase().includes(search.toLowerCase())
  )

  const openCreate = () => { setForm(EMPTY); setEditId(undefined); setModal('create') }
  const openEdit   = (r: DatacenterDto) => {
    setForm({ name: r.name, location: r.location })
    setEditId(r.id)
    setModal('edit')
  }

  const handleSave = async () => {
    setSaving(true)
    try {
      if (modal === 'create') {
        await datacentersApi.create(form)
        toast('Дата-центр создан')
      } else {
        await datacentersApi.update(editId!, { id: editId, ...form })
        toast('Дата-центр обновлён')
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
      await datacentersApi.delete(deleteTarget.id)
      toast('Дата-центр удалён')
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
          <div className="page-title">Дата-центры</div>
          <div className="page-sub">Локации и ресурсы ЦОД · ManyToMany ↔ Instances</div>
        </div>
        <div className="page-actions">
          <button className="btn btn-primary" onClick={openCreate}>+ Новый дата-центр</button>
        </div>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <div className="table-wrap">
        <div className="table-toolbar">
          <span className="table-toolbar-title">
            Дата-центры <span className="c-text2">({filtered.length})</span>
          </span>
          <input
            className="table-search"
            placeholder="Поиск по названию, локации…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <button className="btn btn-sm" onClick={load}>↺</button>
        </div>

        {loading ? (
          <div className="loading"><div className="spinner" /> Загрузка…</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <span className="empty-icon">🏢</span>
            <span className="empty-text">Дата-центры не найдены</span>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Название</th>
                <th>Локация</th>
                <th>Инстансов</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((r) => (
                <tr key={r.id}>
                  <td><span className="mono c-text2">#{r.id}</span></td>
                  <td style={{ fontWeight: 500 }}>{r.name}</td>
                  <td>{r.location}</td>
                  <td>
                    {r.instanceIds && r.instanceIds.length > 0 ? (
                      <Link
                        to={`/instances`}
                        style={{ color: 'var(--blue)', fontSize: 12 }}
                      >
                        {r.instanceIds.length} инстанс{r.instanceIds.length === 1 ? '' : 'ов'} →
                      </Link>
                    ) : (
                      <span style={{ color: 'var(--text3)', fontSize: 12 }}>—</span>
                    )}
                  </td>
                  <td>
                    <div className="row-actions">
                      <button className="btn btn-sm btn-ghost" onClick={() => openEdit(r)}>Изменить</button>
                      <button className="btn btn-sm btn-danger" onClick={() => setDeleteTarget(r)}>Удалить</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {modal && (
        <Modal
          title={modal === 'create' ? 'Новый дата-центр' : 'Изменить дата-центр'}
          onClose={() => setModal(null)}
          onConfirm={handleSave}
          loading={saving}
        >
          <div className="form-grid">
            <div className="form-group">
              <label>Название *</label>
              <input
                type="text"
                value={form.name}
                onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                placeholder="MSK-01"
              />
            </div>
            <div className="form-group">
              <label>Локация *</label>
              <input
                type="text"
                value={form.location}
                onChange={(e) => setForm((f) => ({ ...f, location: e.target.value }))}
                placeholder="Москва, ул. Льва Толстого"
              />
            </div>
          </div>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          message={`Удалить дата-центр "${deleteTarget.name}"? Связанные инстансы будут отвязаны.`}
          onConfirm={handleDelete}
          onCancel={() => setDeleteTarget(null)}
          loading={deleting}
        />
      )}
    </div>
  )
}
