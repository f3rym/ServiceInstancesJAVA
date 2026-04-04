import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Layout } from '@/components/Layout'
import { Dashboard }   from '@/pages/Dashboard'
import { Instances }   from '@/pages/Instances'
import { Datacenters } from '@/pages/Datacenters'
import { Software }    from '@/pages/Software'
import { Users }       from '@/pages/Users'
import { Orders }      from '@/pages/Orders'
import { Statistics }  from '@/pages/Statistics'
import { useToast }    from '@/hooks/useToast'

export default function App() {
  const { toasts, push, dismiss } = useToast()

  const toast = (msg: string, type: 'success' | 'error' | 'info' = 'success') =>
    push(msg, type)

  return (
    <BrowserRouter>
      <Layout toasts={toasts} dismissToast={dismiss}>
        <Routes>
          <Route path="/"            element={<Dashboard />} />
          <Route path="/instances"   element={<Instances   toast={toast} />} />
          <Route path="/datacenters" element={<Datacenters toast={toast} />} />
          <Route path="/software"    element={<Software    toast={toast} />} />
          <Route path="/users"       element={<Users       toast={toast} />} />
          <Route path="/orders"      element={<Orders      toast={toast} />} />
          <Route path="/statistics"  element={<Statistics  toast={toast} />} />
          <Route path="*"            element={<Navigate to="/" replace />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  )
}
