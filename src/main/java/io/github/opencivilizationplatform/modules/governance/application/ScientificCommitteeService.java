package io.github.opencivilizationplatform.modules.governance.application;

import io.github.opencivilizationplatform.modules.governance.domain.ScientificCommittee;
import io.github.opencivilizationplatform.modules.governance.infrastructure.ScientificCommitteeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ScientificCommitteeService {

    private final ScientificCommitteeRepository committeeRepository;

    public ScientificCommitteeService(ScientificCommitteeRepository committeeRepository) {
        this.committeeRepository = committeeRepository;
    }

    public Page<ScientificCommittee> getAllCommittees(Pageable pageable) {
        return committeeRepository.findAll(pageable);
    }

    public ScientificCommittee saveCommittee(ScientificCommittee committee) {
        return committeeRepository.save(committee);
    }
}
