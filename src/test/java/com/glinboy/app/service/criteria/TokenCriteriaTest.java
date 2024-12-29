package com.glinboy.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TokenCriteriaTest {

    @Test
    void newTokenCriteriaHasAllFiltersNullTest() {
        var tokenCriteria = new TokenCriteria();
        assertThat(tokenCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tokenCriteriaFluentMethodsCreatesFiltersTest() {
        var tokenCriteria = new TokenCriteria();

        setAllFilters(tokenCriteria);

        assertThat(tokenCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tokenCriteriaCopyCreatesNullFilterTest() {
        var tokenCriteria = new TokenCriteria();
        var copy = tokenCriteria.copy();

        assertThat(tokenCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tokenCriteria)
        );
    }

    @Test
    void tokenCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tokenCriteria = new TokenCriteria();
        setAllFilters(tokenCriteria);

        var copy = tokenCriteria.copy();

        assertThat(tokenCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tokenCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tokenCriteria = new TokenCriteria();

        assertThat(tokenCriteria).hasToString("TokenCriteria{}");
    }

    private static void setAllFilters(TokenCriteria tokenCriteria) {
        tokenCriteria.id();
        tokenCriteria.name();
        tokenCriteria.token();
        tokenCriteria.disable();
        tokenCriteria.deprecateAt();
        tokenCriteria.roles();
        tokenCriteria.createdAt();
        tokenCriteria.createdBy();
        tokenCriteria.updatedAt();
        tokenCriteria.updatedBy();
        tokenCriteria.distinct();
    }

    private static Condition<TokenCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getToken()) &&
                condition.apply(criteria.getDisable()) &&
                condition.apply(criteria.getDeprecateAt()) &&
                condition.apply(criteria.getRoles()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getUpdatedBy()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TokenCriteria> copyFiltersAre(TokenCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getToken(), copy.getToken()) &&
                condition.apply(criteria.getDisable(), copy.getDisable()) &&
                condition.apply(criteria.getDeprecateAt(), copy.getDeprecateAt()) &&
                condition.apply(criteria.getRoles(), copy.getRoles()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getUpdatedBy(), copy.getUpdatedBy()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
