package com.glinboy.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EmailCriteriaTest {

    @Test
    void newEmailCriteriaHasAllFiltersNullTest() {
        var emailCriteria = new EmailCriteria();
        assertThat(emailCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void emailCriteriaFluentMethodsCreatesFiltersTest() {
        var emailCriteria = new EmailCriteria();

        setAllFilters(emailCriteria);

        assertThat(emailCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void emailCriteriaCopyCreatesNullFilterTest() {
        var emailCriteria = new EmailCriteria();
        var copy = emailCriteria.copy();

        assertThat(emailCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(emailCriteria)
        );
    }

    @Test
    void emailCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var emailCriteria = new EmailCriteria();
        setAllFilters(emailCriteria);

        var copy = emailCriteria.copy();

        assertThat(emailCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(emailCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var emailCriteria = new EmailCriteria();

        assertThat(emailCriteria).hasToString("EmailCriteria{}");
    }

    private static void setAllFilters(EmailCriteria emailCriteria) {
        emailCriteria.id();
        emailCriteria.receiver();
        emailCriteria.subject();
        emailCriteria.status();
        emailCriteria.emailType();
        emailCriteria.createdAt();
        emailCriteria.createdBy();
        emailCriteria.distinct();
    }

    private static Condition<EmailCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReceiver()) &&
                condition.apply(criteria.getSubject()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getEmailType()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EmailCriteria> copyFiltersAre(EmailCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReceiver(), copy.getReceiver()) &&
                condition.apply(criteria.getSubject(), copy.getSubject()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getEmailType(), copy.getEmailType()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
