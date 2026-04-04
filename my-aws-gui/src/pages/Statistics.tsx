import { useEffect, useState } from 'react'
import { statisticsApi } from '@/api/statistics'

interface Props {
  toast: (msg: string, type?: 'success' | 'error' | 'info') => void
}

interface ParsedStats {
  safe: number | null
  unsafe: number | null
}

function parseStats(raw: string): ParsedStats {
  const safeMatch   = raw.match(/Safe[=:\s]+(\d+)/i)
  const unsafeMatch = raw.match(/Unsafe[=:\s]+(\d+)/i)
  return {
    safe:   safeMatch   ? parseInt(safeMatch[1])   : null,
    unsafe: unsafeMatch ? parseInt(unsafeMatch[1]) : null,
  }
}

export function Statistics({ toast }: Props) {
  const [raw, setRaw]       = useState<string>('')
  const [loading, setLoading] = useState(false)
  const [running, setRunning] = useState(false)
  const [result, setResult]   = useState<string>('')

  const load = () => {
    setLoading(true)
    statisticsApi.getStats()
      .then(setRaw)
      .catch((e) => toast(e.message, 'error'))
      .finally(() => setLoading(false))
  }

  useEffect(() => { load() }, []) // eslint-disable-line

  const runTest = async () => {
    setRunning(true)
    setResult('')
    toast('Running race condition test (60 threads × 1000 iter)…', 'info')
    try {
      const res = await statisticsApi.testRace()
      setResult(res)
      toast('Test completed', 'success')
      load()
    } catch (e: any) {
      toast(e.message, 'error')
    } finally {
      setRunning(false)
    }
  }

  const stats = parseStats(raw)
  const diff = stats.safe !== null && stats.unsafe !== null
    ? Math.abs(stats.safe - stats.unsafe)
    : null

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Statistics</div>
          <div className="page-sub">Concurrency testing · Safe (AtomicInteger) vs Unsafe counter</div>
        </div>
        <div className="page-actions">
          <button className="btn" onClick={load} disabled={loading}>↺ Refresh</button>
          <button className="btn btn-primary" onClick={runTest} disabled={running}>
            {running
              ? <><span className="spinner" style={{ width: 14, height: 14 }} /> Running…</>
              : '⚡ Run Race Test'
            }
          </button>
        </div>
      </div>

      <div className="stats-grid" style={{ maxWidth: 640 }}>
        <div className="stat-card">
          <div className="stat-label">Safe Counter</div>
          <div className="stat-value c-green">
            {loading ? '…' : stats.safe?.toLocaleString() ?? '—'}
          </div>
          <div className="stat-sub">AtomicInteger</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Unsafe Counter</div>
          <div className="stat-value c-red">
            {loading ? '…' : stats.unsafe?.toLocaleString() ?? '—'}
          </div>
          <div className="stat-sub">Non-synchronized int</div>
        </div>
        {diff !== null && (
          <div className="stat-card">
            <div className="stat-label">Race Drift</div>
            <div className="stat-value" style={{ color: diff > 0 ? 'var(--red)' : 'var(--green)' }}>
              {diff.toLocaleString()}
            </div>
            <div className="stat-sub">{diff === 0 ? 'No drift detected' : 'Lost updates'}</div>
          </div>
        )}
      </div>

      {/* Raw response */}
      <div
        style={{
          background: 'var(--bg2)',
          border: '1px solid var(--border)',
          borderRadius: 8,
          padding: 20,
          marginBottom: 20,
          maxWidth: 640,
        }}
      >
        <div style={{ fontSize: 12, color: 'var(--text3)', marginBottom: 8 }}>Current state</div>
        <code style={{ fontFamily: 'var(--mono)', fontSize: 13, color: 'var(--text2)' }}>
          {loading ? 'Loading…' : raw || 'No data'}
        </code>
      </div>

      {/* Test result */}
      {result && (
        <div
          style={{
            background: 'rgba(188,140,255,.08)',
            border: '1px solid rgba(188,140,255,.25)',
            borderRadius: 8,
            padding: 20,
            maxWidth: 640,
          }}
        >
          <div style={{ fontSize: 12, color: 'var(--purple)', marginBottom: 8 }}>Last test result</div>
          <code style={{ fontFamily: 'var(--mono)', fontSize: 13, color: 'var(--text)' }}>
            {result}
          </code>
        </div>
      )}

      <hr className="section-divider" style={{ maxWidth: 640 }} />

      {/* Explanation */}
      <div style={{ maxWidth: 640 }}>
        <div style={{ fontSize: 14, fontWeight: 500, marginBottom: 12 }}>How it works</div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {[
            {
              color: 'var(--green)',
              title: 'Safe counter (AtomicInteger)',
              desc: 'Uses java.util.concurrent.atomic.AtomicInteger. All 60,000 increments are guaranteed to land correctly via CAS operations.',
            },
            {
              color: 'var(--red)',
              title: 'Unsafe counter (int)',
              desc: 'A plain int field incremented with ++. Under 60 concurrent threads the result is typically lower than expected due to lost updates.',
            },
            {
              color: 'var(--blue)',
              title: 'Test setup',
              desc: '60 threads × 1,000 iterations each = 60,000 expected increments. The drift between Safe and Unsafe shows the number of lost updates.',
            },
          ].map(({ color, title, desc }) => (
            <div
              key={title}
              style={{
                background: 'var(--bg2)',
                border: '1px solid var(--border)',
                borderRadius: 8,
                padding: '12px 16px',
                borderLeft: `3px solid ${color}`,
              }}
            >
              <div style={{ fontSize: 13, fontWeight: 500, marginBottom: 4 }}>{title}</div>
              <div style={{ fontSize: 12, color: 'var(--text2)', lineHeight: 1.6 }}>{desc}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
