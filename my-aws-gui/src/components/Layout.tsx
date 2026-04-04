import { Link } from 'react-router-dom'
import { Sidebar } from './Sidebar'
import { ToastContainer } from './Toast'
import type { Toast } from '@/hooks/useToast'
import type { ReactNode } from 'react'

interface Props {
  children: ReactNode
  toasts: Toast[]
  dismissToast: (id: number) => void
}

export function Layout({ children, toasts, dismissToast }: Props) {
  return (
    <div className="app">
      <header className="topbar">
        <Link to="/" className="topbar-logo">
          <div className="topbar-logo-icon">Fe</div>
          FeInstances
        </Link>
        <div className="topbar-right">
          <span style={{ fontSize: 12, color: 'var(--text3)', fontFamily: 'var(--mono)' }}>
            eu-west-1
          </span>
          <div className="avatar" title="Admin">AD</div>
        </div>
      </header>

      <div className="app-body">
        <Sidebar />
        <main className="main-content">{children}</main>
      </div>

      <ToastContainer toasts={toasts} dismiss={dismissToast} />
    </div>
  )
}
