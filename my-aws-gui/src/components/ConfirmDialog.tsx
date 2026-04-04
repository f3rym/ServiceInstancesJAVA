import { Modal } from './Modal'

interface Props {
  message: string
  onConfirm: () => void
  onCancel: () => void
  loading?: boolean
}

export function ConfirmDialog({ message, onConfirm, onCancel, loading }: Props) {
  return (
    <Modal
      title="Confirm action"
      onClose={onCancel}
      onConfirm={onConfirm}
      confirmLabel="Delete"
      confirmDanger
      loading={loading}
    >
      <p style={{ color: 'var(--text2)', fontSize: 13 }}>{message}</p>
    </Modal>
  )
}
