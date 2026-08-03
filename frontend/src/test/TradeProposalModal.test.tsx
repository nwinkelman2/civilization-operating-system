import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import TradeProposalModal from '../components/TradeProposalModal'

describe('TradeProposalModal', () => {
  it('does not render when isOpen is false', () => {
    render(<TradeProposalModal isOpen={false} onClose={() => {}} />)
    expect(screen.queryByText('Propose Trade Agreement')).not.toBeInTheDocument()
  })

  it('renders modal elements when isOpen is true', () => {
    render(<TradeProposalModal isOpen={true} onClose={() => {}} />)
    expect(screen.getByText('Propose Trade Agreement')).toBeInTheDocument()
    expect(screen.getByLabelText('Target Civilization')).toBeInTheDocument()
    expect(screen.getByLabelText('Resource Type')).toBeInTheDocument()
    expect(screen.getByLabelText('Quantity / Volume')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /Submit Proposal/i })).toBeInTheDocument()
  })

  it('calls onClose when Cancel button is clicked', () => {
    const handleClose = vi.fn()
    render(<TradeProposalModal isOpen={true} onClose={handleClose} />)
    const cancelBtn = screen.getByRole('button', { name: 'Cancel' })
    fireEvent.click(cancelBtn)
    expect(handleClose).toHaveBeenCalledOnce()
  })
})
