import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'

interface ResourceChartProps {
  data: Record<string, any>[]
  dataKeys: { key: string; color: string }[]
  xKey?: string
}

export default function ResourceChart({ data, dataKeys, xKey = 'name' }: ResourceChartProps) {
  return (
    <ResponsiveContainer width="100%" height={300}>
      <BarChart data={data} margin={{ top: 10, right: 10, left: -10, bottom: 0 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
        <XAxis dataKey={xKey} stroke="#64748b" fontSize={12} tick={{ fill: '#64748b' }} />
        <YAxis stroke="#64748b" fontSize={12} tick={{ fill: '#64748b' }} />
        <Tooltip
          contentStyle={{ background: '#1e293b', border: '1px solid #334155', borderRadius: '0.5rem' }}
          labelStyle={{ color: '#e2e8f0' }}
        />
        {dataKeys.map(dk => (
          <Bar key={dk.key} dataKey={dk.key} fill={dk.color} radius={[4, 4, 0, 0]} />
        ))}
      </BarChart>
    </ResponsiveContainer>
  )
}
