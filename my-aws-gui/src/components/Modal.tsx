import { useEffect, type ReactNode } from 'react'

interface Props {
  title: string
  onClose: () => void
  onConfirm?: () => void
  confirmLabel?: string
  confirmDanger?: boolean
  loading?: boolean
  children: ReactNode
}

export function Modal({
  title,
  onClose,
  onConfirm,
  confirmLabel = 'Save',
  confirmDanger = false,
  loading = false,
  children,
}: Props) {
  useEffect(() => {
    const handler = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose() }
    window.addEventListener('keydown', handler)
    return () => window.removeEventListener('keydown', handler)
  }, [onClose])

  return (
    <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <span className="modal-title">{title}</span>
          <button className="modal-close" onClick={onClose}>✕</button>
        </div>
        {children}
        <div className="modal-footer">
          <button className="btn" onClick={onClose} disabled={loading}>
            Cancel
          </button>
          {onConfirm && (
            <button
              className={`btn ${confirmDanger ? 'btn-danger' : 'btn-primary'}`}
              onClick={onConfirm}
              disabled={loading}
            >
              {loading ? <><span className="spinner" style={{ width: 14, height: 14 }} /> Saving…</> : confirmLabel}
            </button>
          )}
        </div>
      </div>
    </div>
  )
}
