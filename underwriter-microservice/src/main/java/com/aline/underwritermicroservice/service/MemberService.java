package com.aline.underwritermicroservice.service;

import com.aline.core.model.Applicant;
import com.aline.core.model.Member;
import com.aline.core.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;
    private ModelMapper mapper;

    @Autowired
    public void setMapper(@Qualifier("defaultModelMapper") ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Member createMember(Applicant applicant) {
        Member member = mapper.map(applicant, Member.class);
        return repository.save(member);
    }

    public List<Member> saveAll(Iterable<Member> members) {
        return repository.saveAll(members);
    }

}
