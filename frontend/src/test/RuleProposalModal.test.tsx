import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import RuleProposalModal from '../components/RuleProposalModal'

describe('RuleProposalModal', () => {
  it('does not render when isOpen is false', () => {
    render(<RuleProposalModal isOpen={false} onClose={() => {}} />)
    expect(screen.queryByText('Propose Constitutional Rule')).not.toBeInTheDocument()
  })

  it('renders modal with scientific validation meter when isOpen is true', () => {
    render(<RuleProposalModal isOpen={true} onClose={() => {}} />)
    expect(screen.getByText('Propose Constitutional Rule')).toBeInTheDocument()
    expect(screen.getByLabelText('Rule Title')).toBeInTheDocument()
    expect(screen.getByLabelText('Governance Sector / Category')).toBeInTheDocument()
    expect(screen.getByLabelText('Rule Description & Rationale')).toBeInTheDocument()
    expect(screen.getByText('Scientific Validation Meter')).toBeInTheDocument()
  })

  it('calls onClose when Cancel button is clicked', () => {
    const handleClose = vi.fn()
    render(<RuleProposalModal isOpen={true} onClose={handleClose} />)
    const cancelBtn = screen.getByRole('button', { name: 'Cancel' })
    fireEvent.click(cancelBtn)
    expect(handleClose).toHaveBeenCalledOnce()
  })
})
