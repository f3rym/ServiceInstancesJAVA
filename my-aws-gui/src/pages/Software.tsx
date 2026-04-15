import { useEffect, useState } from 'react'
import { softwareApi } from '@/api/software'
import type { SoftwareDto } from '@/types'
import { Modal } from '@/components/Modal'
import { ConfirmDialog } from '@/components/ConfirmDialog'

const EMPTY: SoftwareDto = { name: '', version: '' }

interface Props {
  toast: (msg: string, type?: 'success' | 'error' | 'info') => void
}

export function Software({ toast }: Props) {
  const [rows, setRows]                     = useState<SoftwareDto[]>([])
  const [loading, setLoading]               = useState(true)
  const [error, setError]                   = useState('')
  const [search, setSearch]                 = useState('')
  const [modal, setModal]                   = useState<'create' | 'edit' | null>(null)
  const [form, setForm]                     = useState<SoftwareDto>(EMPTY)
  const [saving, setSaving]                 = useState(false)
  const [deleteTarget, setDeleteTarget]     = useState<SoftwareDto | null>(null)
  const [deleting, setDeleting]             = useState(false)

  const load = () => {
    setLoading(true)
    softwareApi.getAll()
      .then(setRows)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const filtered = rows.filter(
    (r) =>
      r.name.toLowerCase().includes(search.toLowerCase()) ||
      r.version.includes(search)
  )

  const openCreate = () => { setForm(EMPTY); setModal('create') }
  const openEdit   = (r: SoftwareDto) => { setForm({ ...r }); setModal('edit') }

  const handleSave = async () => {
    setSaving(true)
    try {
      if (modal === 'create') {
        await softwareApi.create(form)
        toast('ПО добавлено')
      } else {
        await softwareApi.update(form.id!, form)
        toast('ПО обновлено')
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
      await softwareApi.delete(deleteTarget.id)
      toast('ПО удалено')
      setDeleteTarget(null)
      load()
    } catch (e: any) {
      toast(e.message, 'error')
    } finally {
      setDeleting(false)
    }
  }

  const set = (k: keyof SoftwareDto, v: string) => setForm((f) => ({ ...f, [k]: v }))

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Программное обеспечение</div>
          <div className="page-sub">Каталог доступного ПО для установки на инстансы</div>
        </div>
        <div className="page-actions">
          <button className="btn btn-primary" onClick={openCreate}>+ Добавить ПО</button>
        </div>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <div className="table-wrap">
        <div className="table-toolbar">
          <span className="table-toolbar-title">
            ПО <span className="c-text2">({filtered.length})</span>
          </span>
          <input
            className="table-search"
            placeholder="Поиск по названию, версии…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <button className="btn btn-sm" onClick={load}>↺</button>
        </div>

        {loading ? (
          <div className="loading"><div className="spinner" /> Загрузка…</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <span className="empty-icon">📦</span>
            <span className="empty-text">ПО не найдено</span>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Название</th>
                <th>Версия</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((r) => (
                <tr key={r.id}>
                  <td><span className="mono c-text2">#{r.id}</span></td>
                  <td style={{ fontWeight: 500 }}>{r.name}</td>
                  <td><span className="mono">{r.version}</span></td>
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
          title={modal === 'create' ? 'Добавить ПО' : 'Изменить ПО'}
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
                onChange={(e) => set('name', e.target.value)}
                placeholder="PostgreSQL"
              />
            </div>
            <div className="form-group">
              <label>Версия *</label>
              <input
                type="text"
                value={form.version}
                onChange={(e) => set('version', e.target.value)}
                placeholder="15.4"
              />
            </div>
          </div>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          message={`Удалить "${deleteTarget.name} ${deleteTarget.version}"? ПО будет отвязано от всех инстансов.`}
          onConfirm={handleDelete}
          onCancel={() => setDeleteTarget(null)}
          loading={deleting}
        />
      )}
    </div>
  )
}
