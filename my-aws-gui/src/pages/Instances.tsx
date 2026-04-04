import { useEffect, useState, useCallback } from 'react'
import { instancesApi } from '@/api/instances'
import { datacentersApi } from '@/api/datacenters'
import { softwareApi } from '@/api/software'
import type { InstanceDto, InstanceFilterDto, DatacenterDto, SoftwareDto, Page } from '@/types'
import { Modal } from '@/components/Modal'
import { ConfirmDialog } from '@/components/ConfirmDialog'

const COMMON_TYPES = ['t3.micro', 't3.small', 't3.medium', 't3.large', 'm5.xlarge', 'c5.2xlarge']
const COMMON_OS    = ['Ubuntu 22.04', 'Ubuntu 20.04', 'Debian 12', 'CentOS 9', 'AlmaLinux 9', 'Windows Server 2022']

const EMPTY: InstanceDto = {
  name: '',
  instanceType: 't3.medium',
  os: 'Ubuntu 22.04',
  price: 1,
  datacenterIds: [],
  softwareIds: [],
}

interface Props {
  toast: (msg: string, type?: 'success' | 'error' | 'info') => void
}

export function Instances({ toast }: Props) {
  const [page, setPage]               = useState<Page<InstanceDto> | null>(null)
  const [allRows, setAllRows]         = useState<InstanceDto[]>([])
  const [loading, setLoading]         = useState(true)
  const [error, setError]             = useState('')
  const [apiDebug, setApiDebug]       = useState('')
  const [currentPage, setCurrentPage] = useState(0)
  const PAGE_SIZE = 10

  const [datacenters, setDatacenters] = useState<DatacenterDto[]>([])
  const [software, setSoftware]       = useState<SoftwareDto[]>([])

  const [filter, setFilter] = useState<InstanceFilterDto>({
    price: '',
    datacenterLocation: '',
    useNative: false,
  })

  const [modal, setModal]               = useState<'create' | 'edit' | null>(null)
  const [form, setForm]                 = useState<InstanceDto>(EMPTY)
  const [saving, setSaving]             = useState(false)
  const [deleteTarget, setDeleteTarget] = useState<InstanceDto | null>(null)
  const [deleting, setDeleting]         = useState(false)

  useEffect(() => {
    datacentersApi.getAll().then(setDatacenters).catch(() => {})
    softwareApi.getAll().then(setSoftware).catch(() => {})
  }, [])

  const loadAll = useCallback(() => {
    setLoading(true)
    setError('')
    setApiDebug('')

    // Try /search first, fall back to /instances (getAll)
    const doSearch = filter.price || filter.datacenterLocation || filter.useNative
    const request = doSearch
      ? instancesApi.search(filter, currentPage, PAGE_SIZE)
      : instancesApi.getAll().then((rows) => {
          const start = currentPage * PAGE_SIZE
          return {
            content: rows.slice(start, start + PAGE_SIZE),
            totalElements: rows.length,
            totalPages: Math.ceil(rows.length / PAGE_SIZE),
            number: currentPage,
            size: PAGE_SIZE,
          } as Page<InstanceDto>
        })

    request
      .then((p) => {
        setPage(p)
        setAllRows(p.content)
        if (p.totalElements === 0) {
          setApiDebug('API вернул 0 элементов. Проверьте: 1) данные в БД 2) @JsonIgnoreProperties на моделях 3) маппер installedSoftware→softwareIds')
        }
      })
      .catch((e) => {
        setError(e.message)
        setApiDebug(`Детали: ${e.message}. Возможные причины: циклические ссылки (добавьте @JsonIgnoreProperties), LazyInitializationException (добавьте @Transactional в сервис), пустая БД.`)
      })
      .finally(() => setLoading(false))
  }, [filter, currentPage])

  useEffect(() => { loadAll() }, [loadAll])

  const setF = (k: keyof InstanceFilterDto, v: any) => {
    setFilter((f) => ({ ...f, [k]: v }))
    setCurrentPage(0)
  }

  const openCreate = () => { setForm({ ...EMPTY }); setModal('create') }
  const openEdit   = (r: InstanceDto) => {
    setForm({
      ...r,
      datacenterIds: r.datacenterIds ?? [],
      softwareIds:   r.softwareIds   ?? [],
    })
    setModal('edit')
  }

  const toggleId = (list: number[], id: number): number[] =>
    list.includes(id) ? list.filter((x) => x !== id) : [...list, id]

  const handleSave = async () => {
    if (!form.name.trim() || form.name.length < 2) {
      toast('Имя должно быть не короче 2 символов', 'error'); return
    }
    if (!form.instanceType.trim()) {
      toast('Тип инстанса обязателен', 'error'); return
    }
    if (!form.os.trim()) {
      toast('ОС обязательна', 'error'); return
    }
    if (form.price < 1) {
      toast('Цена должна быть больше 0', 'error'); return
    }
    setSaving(true)
    try {
      const payload: InstanceDto = {
        ...form,
        // Поле в Java-сущности называется installedSoftware, но DTO — softwareIds
        softwareIds:   form.softwareIds   ?? [],
        datacenterIds: form.datacenterIds ?? [],
      }
      if (modal === 'create') {
        await instancesApi.create(payload)
        toast('Инстанс создан')
      } else {
        await instancesApi.update(form.id!, payload)
        toast('Инстанс обновлён')
      }
      setModal(null)
      loadAll()
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
      await instancesApi.delete(deleteTarget.id)
      toast('Инстанс удалён')
      setDeleteTarget(null)
      loadAll()
    } catch (e: any) {
      toast(e.message, 'error')
    } finally {
      setDeleting(false)
    }
  }

  const set = (k: keyof InstanceDto, v: any) => setForm((f) => ({ ...f, [k]: v }))

  const total      = page?.totalElements ?? 0
  const totalPages = page?.totalPages    ?? 0

  const dcNames = (ids: number[]) =>
    (ids ?? []).map((id) => datacenters.find((d) => d.id === id)?.name ?? `ЦОД #${id}`)

  const swNames = (ids: number[]) =>
    (ids ?? []).map((id) => software.find((s) => s.id === id)?.name ?? `ПО #${id}`)

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Инстансы</div>
          <div className="page-sub">
            Облачные серверы · ManyToMany ↔ Datacenter · ManyToMany ↔ Software
          </div>
        </div>
        <div className="page-actions">
          <button className="btn btn-primary" onClick={openCreate}>+ Создать инстанс</button>
        </div>
      </div>

      {/* ── Filters ── */}
      <div style={{
        background: 'var(--bg2)',
        border: '1px solid var(--border)',
        borderRadius: 8,
        padding: '14px 16px',
        marginBottom: 16,
        display: 'flex',
        gap: 12,
        alignItems: 'flex-end',
        flexWrap: 'wrap',
      }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
          <label style={{ fontSize: 11, color: 'var(--text3)', textTransform: 'uppercase', letterSpacing: '.5px' }}>
            Макс. цена ($/ч)
          </label>
          <input
            type="number"
            min={0}
            placeholder="любая"
            value={filter.price}
            onChange={(e) => setF('price', e.target.value === '' ? '' : +e.target.value)}
            style={{ width: 110 }}
          />
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
          <label style={{ fontSize: 11, color: 'var(--text3)', textTransform: 'uppercase', letterSpacing: '.5px' }}>
            Локация дата-центра
          </label>
          <input
            type="text"
            placeholder="Москва"
            value={filter.datacenterLocation}
            onChange={(e) => setF('datacenterLocation', e.target.value)}
            style={{ width: 180 }}
          />
        </div>
        <label style={{ display: 'flex', alignItems: 'center', gap: 8, paddingBottom: 2, cursor: 'pointer' }}>
          <input
            type="checkbox"
            checked={filter.useNative}
            onChange={(e) => setF('useNative', e.target.checked)}
            style={{ accentColor: 'var(--blue)', width: 14, height: 14 }}
          />
          <span style={{ fontSize: 13, color: 'var(--text2)' }}>Native SQL</span>
        </label>
        <button className="btn btn-sm" onClick={loadAll}>Применить</button>
        <button
          className="btn btn-sm btn-ghost"
          onClick={() => { setFilter({ price: '', datacenterLocation: '', useNative: false }); setCurrentPage(0) }}
        >
          Сбросить
        </button>
      </div>

      {/* ── Error ── */}
      {error && (
        <div className="error-msg" style={{ marginBottom: 12 }}>
          <strong>Ошибка загрузки:</strong> {error}
        </div>
      )}

      {/* ── Debug hint (shown when empty) ── */}
      {apiDebug && !error && (
        <div style={{
          background: 'rgba(210,153,34,.08)',
          border: '1px solid rgba(210,153,34,.25)',
          borderRadius: 8,
          padding: '12px 16px',
          marginBottom: 14,
          fontSize: 12,
          color: 'var(--yellow)',
          lineHeight: 1.7,
        }}>
          ⚠ {apiDebug}
        </div>
      )}

      {/* ── Table ── */}
      <div className="table-wrap">
        <div className="table-toolbar">
          <span className="table-toolbar-title">
            Инстансы <span className="c-text2">({total})</span>
          </span>
          <button className="btn btn-sm" onClick={loadAll}>↺ Обновить</button>
        </div>

        {loading ? (
          <div className="loading"><div className="spinner" /> Загрузка…</div>
        ) : allRows.length === 0 ? (
          <div className="empty-state" style={{ flexDirection: 'column', gap: 12 }}>
            <span className="empty-icon">⚙</span>
            <span className="empty-text">Инстансы не найдены</span>
            <div style={{ fontSize: 12, color: 'var(--text3)', textAlign: 'center', maxWidth: 400, lineHeight: 1.7 }}>
              Если данные в БД есть, проверьте:<br />
              1. Добавлены ли <code style={{ fontFamily: 'var(--mono)', background: 'var(--bg4)', padding: '1px 5px', borderRadius: 3 }}>@JsonIgnoreProperties</code> в модели<br />
              2. Маппер использует <code style={{ fontFamily: 'var(--mono)', background: 'var(--bg4)', padding: '1px 5px', borderRadius: 3 }}>getInstalledSoftware()</code>, а не <code style={{ fontFamily: 'var(--mono)', background: 'var(--bg4)', padding: '1px 5px', borderRadius: 3 }}>getSoftware()</code><br />
              3. Сервис помечен <code style={{ fontFamily: 'var(--mono)', background: 'var(--bg4)', padding: '1px 5px', borderRadius: 3 }}>@Transactional</code>
            </div>
            <a
              href="http://localhost:8080/api/v1/instances"
              target="_blank"
              rel="noreferrer"
              className="btn btn-sm"
              style={{ marginTop: 4 }}
            >
              Открыть API напрямую ↗
            </a>
          </div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Название</th>
                <th>Тип</th>
                <th>ОС</th>
                <th>Цена / ч</th>
                <th>Дата-центры</th>
                <th>ПО</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {allRows.map((r) => (
                <tr key={r.id}>
                  <td><span className="mono c-text2">#{r.id}</span></td>
                  <td style={{ fontWeight: 500 }}>{r.name}</td>
                  <td><span className="pill">{r.instanceType}</span></td>
                  <td style={{ fontSize: 12, color: 'var(--text2)' }}>{r.os}</td>
                  <td>
                    <span className="mono" style={{ color: 'var(--accent2)' }}>
                      ${Number(r.price ?? 0).toFixed(2)}
                    </span>
                  </td>
                  <td>
                    <div className="chip-list">
                      {dcNames(r.datacenterIds ?? []).length > 0
                        ? dcNames(r.datacenterIds).slice(0, 2).map((n) => (
                            <span key={n} className="chip">{n}</span>
                          ))
                        : <span style={{ color: 'var(--text3)', fontSize: 11 }}>—</span>}
                      {(r.datacenterIds ?? []).length > 2 && (
                        <span className="chip">+{r.datacenterIds.length - 2}</span>
                      )}
                    </div>
                  </td>
                  <td>
                    <div className="chip-list">
                      {swNames(r.softwareIds ?? []).length > 0
                        ? swNames(r.softwareIds).slice(0, 2).map((n) => (
                            <span key={n} className="chip" style={{ borderColor: 'rgba(188,140,255,.3)', color: 'var(--purple)' }}>{n}</span>
                          ))
                        : <span style={{ color: 'var(--text3)', fontSize: 11 }}>—</span>}
                      {(r.softwareIds ?? []).length > 2 && (
                        <span className="chip">+{r.softwareIds.length - 2}</span>
                      )}
                    </div>
                  </td>
                  <td>
                    <div className="row-actions">
                      <button className="btn btn-sm btn-ghost" onClick={() => openEdit(r)}>Изменить</button>
                      <button className="btn btn-sm btn-danger" onClick={() => setDeleteTarget(r)}>✕</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {totalPages > 1 && (
          <div className="pagination">
            <span>Страница {currentPage + 1} из {totalPages} · Всего: {total}</span>
            <div className="pagination-btns">
              <button className="btn btn-sm" disabled={currentPage === 0} onClick={() => setCurrentPage((p) => p - 1)}>← Пред</button>
              <button className="btn btn-sm" disabled={currentPage >= totalPages - 1} onClick={() => setCurrentPage((p) => p + 1)}>След →</button>
            </div>
          </div>
        )}
      </div>

      {/* ── Modal ── */}
      {modal && (
        <Modal
          title={modal === 'create' ? 'Создать инстанс' : 'Изменить инстанс'}
          onClose={() => setModal(null)}
          onConfirm={handleSave}
          loading={saving}
        >
          <div className="form-grid">
            <div className="form-group">
              <label>Название * (мин. 2 символа)</label>
              <input
                type="text"
                value={form.name}
                onChange={(e) => set('name', e.target.value)}
                placeholder="Production-DB-1"
                autoFocus
              />
            </div>
            <div className="form-group">
              <label>Тип инстанса *</label>
              <select value={form.instanceType} onChange={(e) => set('instanceType', e.target.value)}>
                {COMMON_TYPES.map((t) => <option key={t}>{t}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Операционная система *</label>
              <select value={form.os} onChange={(e) => set('os', e.target.value)}>
                {COMMON_OS.map((o) => <option key={o}>{o}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Цена ($/ч) * — мин. 1</label>
              <input
                type="number"
                min={1}
                step={0.01}
                value={form.price}
                onChange={(e) => set('price', +e.target.value)}
              />
            </div>

            {/* ManyToMany: Datacenters */}
            <div className="form-group full">
              <label style={{ marginBottom: 8, display: 'block' }}>
                Дата-центры
                <span style={{ fontSize: 11, color: 'var(--blue)', marginLeft: 8 }}>ManyToMany</span>
              </label>
              {datacenters.length === 0 ? (
                <span style={{ fontSize: 12, color: 'var(--text3)' }}>
                  Нет дата-центров. Сначала создайте их.
                </span>
              ) : (
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
                  {datacenters.map((d) => {
                    const active = (form.datacenterIds ?? []).includes(d.id!)
                    return (
                      <span
                        key={d.id}
                        onClick={() => set('datacenterIds', toggleId(form.datacenterIds ?? [], d.id!))}
                        style={{
                          padding: '4px 10px',
                          borderRadius: 4,
                          fontSize: 12,
                          cursor: 'pointer',
                          border: `1px solid ${active ? 'rgba(88,166,255,.6)' : 'var(--border)'}`,
                          background: active ? 'rgba(88,166,255,.15)' : 'var(--bg4)',
                          color: active ? 'var(--blue)' : 'var(--text2)',
                          fontFamily: 'var(--mono)',
                          transition: 'all .12s',
                          userSelect: 'none',
                        }}
                      >
                        {active ? '✓ ' : ''}{d.name}
                        <span style={{ fontSize: 10, opacity: .7, marginLeft: 4 }}>{d.location}</span>
                      </span>
                    )
                  })}
                </div>
              )}
            </div>

            {/* ManyToMany: Software */}
            <div className="form-group full">
              <label style={{ marginBottom: 8, display: 'block' }}>
                Программное обеспечение
                <span style={{ fontSize: 11, color: 'var(--purple)', marginLeft: 8 }}>ManyToMany</span>
              </label>
              {software.length === 0 ? (
                <span style={{ fontSize: 12, color: 'var(--text3)' }}>
                  Нет ПО. Сначала добавьте его в разделе Software.
                </span>
              ) : (
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: 6 }}>
                  {software.map((s) => {
                    const active = (form.softwareIds ?? []).includes(s.id!)
                    return (
                      <span
                        key={s.id}
                        onClick={() => set('softwareIds', toggleId(form.softwareIds ?? [], s.id!))}
                        style={{
                          padding: '4px 10px',
                          borderRadius: 4,
                          fontSize: 12,
                          cursor: 'pointer',
                          border: `1px solid ${active ? 'rgba(188,140,255,.6)' : 'var(--border)'}`,
                          background: active ? 'rgba(188,140,255,.15)' : 'var(--bg4)',
                          color: active ? 'var(--purple)' : 'var(--text2)',
                          fontFamily: 'var(--mono)',
                          transition: 'all .12s',
                          userSelect: 'none',
                        }}
                      >
                        {active ? '✓ ' : ''}{s.name}
                        <span style={{ fontSize: 10, opacity: .7, marginLeft: 4 }}>v{s.version}</span>
                      </span>
                    )
                  })}
                </div>
              )}
            </div>
          </div>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          message={`Удалить инстанс "${deleteTarget.name}"? Это действие необратимо.`}
          onConfirm={handleDelete}
          onCancel={() => setDeleteTarget(null)}
          loading={deleting}
        />
      )}
    </div>
  )
}
