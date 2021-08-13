package com.aline.underwritermicroservice.service;

import com.aline.core.exception.BadRequestException;
import com.aline.core.exception.NotFoundException;
import com.aline.core.model.Applicant;
import com.aline.core.model.Branch;
import com.aline.core.model.Member;
import com.aline.core.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
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
@Slf4j(topic = "Member Service")
public class MemberService {

    private final MemberRepository repository;
    private final BranchService branchService;

    /**
     * Create a member from an applicant.
     * @param applicant Applicant to attach to the member.
     * @return Saved member.
     */
    @Transactional(rollbackOn = {NotFoundException.class, BadRequestException.class})
    public Member createMember(Applicant applicant) {
        Member member = new Member();
        member.setApplicant(applicant);
        member.setBranch(getBranch(applicant));
        return saveMember(member);
    }

    public Member saveMember(@Valid Member member) {
        return repository.save(member);
    }

    /**
     * Used to find a branch by the applicant's address.
     * @param applicant Applicant to assign branch to.
     * @return A branch closest to the applicant.
     */
    public Branch getBranch(Applicant applicant) {
        log.info("(To be implemented) Finding branch closest to applicant's zipcode: {}", applicant.getZipcode());
        return branchService.getBranchById(1);
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
