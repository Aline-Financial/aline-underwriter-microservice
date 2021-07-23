package com.aline.underwritermicroservice.service;

import com.aline.core.dto.request.ApplyRequest;
import com.aline.core.dto.response.ApplicationResponse;
import com.aline.core.exception.notfound.ApplicationNotFoundException;
import com.aline.core.model.Application;
import com.aline.core.model.ApplicationStatus;
import com.aline.core.model.ApplicationType;
import com.aline.core.repository.ApplicationRepository;
import com.aline.core.service.CrudService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Application Service
 * <p>
 *     CRUD for applications.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ApplicationService implements CrudService<ApplicationResponse, Long, ApplyRequest, ApplyRequest> {

    private final ApplicationRepository repository;

    private ModelMapper mapper;

    @Autowired
    public void setMapper(@Qualifier("defaultModelMapper") ModelMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Get object by id.
     *
     * @param id Id of the retrieved object.
     * @return The retrieved object.
     */
    @Override
    public ApplicationResponse getById(Long id) {
        Application application = repository.findById(id).orElseThrow(ApplicationNotFoundException::new);
        return mapper.map(application, ApplicationResponse.class);
    }

    /**
     * Create an object using a dto.
     *
     * @param creatDto DTO used to create the application.
     * @return The created object.
     */
    @Override
    public ApplicationResponse create(ApplyRequest creatDto) {
        return null;
    }

    /**
     * Update an object by id using a dto.
     *
     * @param id        Id of the object to be updated.
     * @param newValues The new values to update the object with.
     */
    @Override
    public void update(Long id, ApplyRequest newValues) {

    }

    /**
     * Delete an object by id.
     *
     * @param id Id of object to be deleted.
     */
    @Override
    public void delete(Long id) {

    }
}
