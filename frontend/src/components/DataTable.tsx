interface Column<T> {
  key: string
  label: string
  render?: (item: T) => React.ReactNode
  width?: string
}

interface DataTableProps<T> {
  columns: Column<T>[]
  data: T[]
  loading?: boolean
  emptyMessage?: string
}

export default function DataTable<T extends Record<string, any>>({
  columns, data, loading, emptyMessage = 'No data'
}: DataTableProps<T>) {
  if (loading) {
    return <div style={{ color: '#64748b', padding: '2rem', textAlign: 'center' }}>Loading...</div>
  }

  if (data.length === 0) {
    return <div style={{ color: '#64748b', padding: '2rem', textAlign: 'center' }}>{emptyMessage}</div>
  }

  return (
    <div style={{ overflowX: 'auto' }}>
      <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
        <thead>
          <tr style={{ borderBottom: '2px solid #334155' }}>
            {columns.map(col => (
              <th key={col.key} style={{
                textAlign: 'left', padding: '0.75rem 0.5rem',
                color: '#94a3b8', fontWeight: 600, width: col.width
              }}>
                {col.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((item, i) => (
            <tr key={item.id || i} style={{ borderBottom: '1px solid #1e293b' }}>
              {columns.map(col => (
                <td key={col.key} style={{ padding: '0.6rem 0.5rem' }}>
                  {col.render ? col.render(item) : item[col.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
