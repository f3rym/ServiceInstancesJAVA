import type { Toast, ToastType } from '@/hooks/useToast'

const ICONS: Record<ToastType, string> = {
  success: '✓',
  error: '✕',
  info: 'ℹ',
}

interface Props {
  toasts: Toast[]
  dismiss: (id: number) => void
}

export function ToastContainer({ toasts, dismiss }: Props) {
  return (
    <div className="toast-container">
      {toasts.map((t) => (
        <div key={t.id} className={`toast toast-${t.type}`} onClick={() => dismiss(t.id)}>
          <span>{ICONS[t.type]}</span>
          <span style={{ flex: 1 }}>{t.message}</span>
        </div>
      ))}
    </div>
  )
}
