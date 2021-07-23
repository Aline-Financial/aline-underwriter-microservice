package com.aline.underwritermicroservice.util;

import com.aline.core.model.Applicant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import java.util.Arrays;

@RequiredArgsConstructor
public class ApplicantSpecification implements Specification<Applicant> {

    @NonNull
    private final String search;

    @Override
    public Predicate toPredicate(Root<Applicant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        String[] searchTerms = search.split("(\\s|,)");

        Predicate[] predicates = Arrays.stream(searchTerms)
                .map(String::toLowerCase)
                .flatMap(searchTerm -> root.getModel().getAttributes().stream()
                        .filter(attribute -> attribute.getJavaType() == String.class)
                        .map(Attribute::getName)
                        .map(attributeName -> cb.like(cb.lower(root.get(attributeName)), "%" + searchTerm + "%")))
                .toArray(Predicate[]::new);

        return cb.or(predicates);
    }
}
