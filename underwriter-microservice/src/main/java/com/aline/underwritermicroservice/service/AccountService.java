package com.aline.underwritermicroservice.service;

import com.aline.core.exception.BadRequestException;
import com.aline.core.model.ApplicationType;
import com.aline.core.model.Member;
import com.aline.core.model.account.Account;
import com.aline.core.model.account.CheckingAccount;
import com.aline.core.model.account.SavingsAccount;
import com.aline.core.repository.AccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    public Set<Account> createAccount(ApplicationType applicationType, Member primaryAccountHolder, Set<Member> members) {
        Set<Account> accounts = new HashSet<>();
        switch (applicationType) {

            case CHECKING:
                accounts.add(createCheckingAccount(primaryAccountHolder, members));
                break;
            case SAVINGS:
                accounts.add(createSavingsAccount(primaryAccountHolder, members));
                break;
            case CHECKING_AND_SAVINGS:
                accounts.add(createCheckingAccount(primaryAccountHolder, members));
                accounts.add(createSavingsAccount(primaryAccountHolder, members));
                break;
        }

        return accounts;
    }

    private Account createCheckingAccount(Member primaryAccountHolder, Set<Member> members) {
        CheckingAccount account = CheckingAccount.builder()
                .primaryAccountHolder(primaryAccountHolder)
                .balance(0)
                .availableBalance(0)
                .members(members)
                .build();
        return Optional.of(repository.save(account)).orElseThrow(() -> new BadRequestException("Account was not saved."));
    }

    private Account createSavingsAccount(Member primaryAccountHolder, Set<Member> members) {
        SavingsAccount account = SavingsAccount.builder()
                .primaryAccountHolder(primaryAccountHolder)
                .balance(0)
                .apy(0.01f)
                .members(members)
                .build();
        return Optional.of(repository.save(account)).orElseThrow(() -> new BadRequestException("Account was not saved."));
    }

}
