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

/**
 * Member Service
 * <p>
 *     Used to create members in the context of
 *     approving an application.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;
    private ModelMapper mapper;

    @Autowired
    public void setMapper(@Qualifier("defaultModelMapper") ModelMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Create a member from an applicant.
     * @param applicant Applicant to attach to the member.
     * @return Saved member.
     */
    public Member createMember(Applicant applicant) {
        Member member = new Member();
        member.setApplicant(applicant);
        return repository.save(member);
    }

    /**
     * Batch call to saving members
     * @param members Members to be saved.
     * @return A list of saved members.
     */
    public List<Member> saveAll(Iterable<Member> members) {
        return repository.saveAll(members);
    }

}
