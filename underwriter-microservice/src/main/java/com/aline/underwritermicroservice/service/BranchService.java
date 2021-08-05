package com.aline.underwritermicroservice.service;

import com.aline.core.exception.notfound.BranchNotFoundException;
import com.aline.core.model.Branch;
import com.aline.core.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository repository;

    public Branch getBranchById(long id) {
        return repository.findById(id).orElseThrow(BranchNotFoundException::new);
    }

}
