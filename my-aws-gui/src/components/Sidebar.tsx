import { NavLink } from 'react-router-dom'

interface NavItem {
  to: string
  label: string
  dotColor: string
}

const NAV: { group: string; items: NavItem[] }[] = [
  {
    group: 'Compute',
    items: [
      { to: '/instances',   label: 'Instances',    dotColor: '#58a6ff' },
      { to: '/datacenters', label: 'Datacenters',  dotColor: '#3fb950' },
    ],
  },
  {
    group: 'Catalog',
    items: [
      { to: '/software', label: 'Software',  dotColor: '#bc8cff' },
    ],
  },
  {
    group: 'Users & Orders',
    items: [
      { to: '/users',  label: 'Users',  dotColor: '#ff9f43' },
      { to: '/orders', label: 'Orders', dotColor: '#39d353' },
    ],
  },
  {
    group: 'System',
    items: [
      { to: '/statistics', label: 'Statistics', dotColor: '#f78166' },
    ],
  },
]

export function Sidebar() {
  return (
    <aside className="sidebar">
      {NAV.map(({ group, items }) => (
        <div key={group}>
          <div className="sidebar-group-label">{group}</div>
          {items.map(({ to, label, dotColor }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) => 'sidebar-link' + (isActive ? ' active' : '')}
            >
              <span className="sidebar-dot" style={{ background: dotColor }} />
              {label}
            </NavLink>
          ))}
        </div>
      ))}
    </aside>
  )
}
