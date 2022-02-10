package org.egov.service;

import org.egov.repository.ProjectRepository;
import org.egov.web.models.Project;
import org.egov.web.models.ProjectRequest;
import org.egov.web.models.ProjectSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {


    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectEnrichmentService projectEnrichmentService;

    /**
     * @param projectSearchRequest
     * @return
     */
    public List<Project> findAllByCriteria(ProjectSearchRequest projectSearchRequest) {
        return projectRepository.findAllByCriteria(projectSearchRequest.getCriteria());
    }

    /**
     * @param projectRequest
     * @return
     */
    public ProjectRequest createProject(ProjectRequest projectRequest) {
        projectEnrichmentService.enrichProjectData(projectRequest);
        projectRepository.save(projectRequest.getProject());

        return projectRequest;
    }
}
