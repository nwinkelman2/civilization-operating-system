import { useQuery } from '@apollo/client'
import { GET_INCIDENTS, GET_PROJECTS } from '../graphql/queries'
import { Heart, AlertTriangle, FolderGit2, Loader2, AlertCircle, Inbox } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import DataTable from '../components/DataTable'
import StatusBadge from '../components/StatusBadge'

export default function Social() {
  const { data: incidentsData, loading: incidentsLoading, error: incidentsError } = useQuery(GET_INCIDENTS)
  const { data: projectsData, loading: projectsLoading, error: projectsError } = useQuery(GET_PROJECTS)

  if (incidentsLoading || projectsLoading) {
    return (
      <Layout icon={<Heart size={24} color="#ef4444" />} title="Social & Community" subtitle="Incidents, projects, and community health">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#94a3b8' }}>
          <Loader2 size={36} style={{ animation: 'spin 1s linear infinite' }} />
          <span>Loading social & community data...</span>
        </div>
      </Layout>
    )
  }

  if (incidentsError || projectsError) {
    const err = incidentsError || projectsError
    return (
      <Layout icon={<Heart size={24} color="#ef4444" />} title="Social & Community" subtitle="Incidents, projects, and community health">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '1rem', color: '#ef4444' }}>
          <AlertCircle size={36} />
          <span>Failed to load social data: {err?.message}</span>
        </div>
      </Layout>
    )
  }

  const incidents = incidentsData?.incidents || []
  const projects = projectsData?.projects || []

  return (
    <Layout
      icon={<Heart size={24} color="#ef4444" />}
      title="Social & Community"
      subtitle="Incidents, projects, and community health"
    >
      <div className="dashboard-grid" style={{ marginBottom: '1.5rem' }}>
        <StatCard title="Incidents Overview" icon={<AlertTriangle size={18} color="#f59e0b" />}>
          <div style={{ fontSize: '0.85rem', color: '#64748b' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <span>Total Incidents</span><span style={{ color: '#e2e8f0' }}>{incidents.length}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span>Active</span><span style={{ color: '#ef4444' }}>{incidents.filter((i: any) => i.status === 'OPEN' || i.status === 'ACTIVE').length}</span>
            </div>
          </div>
        </StatCard>

        <StatCard title="Projects Overview" icon={<FolderGit2 size={18} color="#0ea5e9" />}>
          <div style={{ fontSize: '0.85rem', color: '#64748b' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
              <span>Contribution Projects</span><span style={{ color: '#e2e8f0' }}>{projects.length}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span>In Progress</span><span style={{ color: '#22c55e' }}>{projects.filter((p: any) => p.status === 'IN_PROGRESS' || p.status === 'ACTIVE').length}</span>
            </div>
          </div>
        </StatCard>
      </div>

      <div style={{ marginBottom: '2rem' }}>
        <h2 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '1rem' }}>Active Incidents</h2>
        <DataTable
          columns={[
            { key: 'title', label: 'Title' },
            { key: 'type', label: 'Type', render: (i: any) => <span style={{ textTransform: 'capitalize' }}>{i.type || '-'}</span> },
            { key: 'description', label: 'Description' },
            { key: 'status', label: 'Status', render: (i: any) => <StatusBadge status={i.status} /> },
            { key: 'createdAt', label: 'Reported', render: (i: any) => i.createdAt ? new Date(i.createdAt).toLocaleDateString() : '-' }
          ]}
          data={incidents}
          emptyMessage="No incidents reported"
        />
      </div>

      <div>
        <h2 style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '1rem' }}>Contribution Projects</h2>
        {projects.length === 0 ? (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '2rem', background: '#1e293b', borderRadius: '0.75rem', color: '#94a3b8', gap: '0.5rem' }}>
            <Inbox size={28} />
            <span>No contribution projects active</span>
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '1rem' }}>
            {projects.map((p: any) => {
              const progress = p.targetContribution > 0 ? (p.currentContribution / p.targetContribution) * 100 : 0
              return (
                <StatCard key={p.id} title={p.name}>
                  <div style={{ fontSize: '0.85rem', color: '#94a3b8', marginBottom: '0.5rem' }}>
                    {p.description}
                  </div>
                  <div style={{ fontSize: '0.8rem', display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
                    <span>Category: {p.category}</span>
                    <span style={{ color: '#0ea5e9' }}>{progress.toFixed(0)}%</span>
                  </div>
                  <div className="resource-bar">
                    <div className="resource-bar-fill" style={{ width: `${Math.min(100, progress)}%`, background: '#22c55e' }} />
                  </div>
                </StatCard>
              )
            })}
          </div>
        )}
      </div>
    </Layout>
  )
}
