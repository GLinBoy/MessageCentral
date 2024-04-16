package com.glinboy.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ShortMessageCriteriaTest {

    @Test
    void newShortMessageCriteriaHasAllFiltersNullTest() {
        var shortMessageCriteria = new ShortMessageCriteria();
        assertThat(shortMessageCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void shortMessageCriteriaFluentMethodsCreatesFiltersTest() {
        var shortMessageCriteria = new ShortMessageCriteria();

        setAllFilters(shortMessageCriteria);

        assertThat(shortMessageCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void shortMessageCriteriaCopyCreatesNullFilterTest() {
        var shortMessageCriteria = new ShortMessageCriteria();
        var copy = shortMessageCriteria.copy();

        assertThat(shortMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(shortMessageCriteria)
        );
    }

    @Test
    void shortMessageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var shortMessageCriteria = new ShortMessageCriteria();
        setAllFilters(shortMessageCriteria);

        var copy = shortMessageCriteria.copy();

        assertThat(shortMessageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(shortMessageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var shortMessageCriteria = new ShortMessageCriteria();

        assertThat(shortMessageCriteria).hasToString("ShortMessageCriteria{}");
    }

    private static void setAllFilters(ShortMessageCriteria shortMessageCriteria) {
        shortMessageCriteria.id();
        shortMessageCriteria.phoneNumber();
        shortMessageCriteria.content();
        shortMessageCriteria.status();
        shortMessageCriteria.createdAt();
        shortMessageCriteria.createdBy();
        shortMessageCriteria.distinct();
    }

    private static Condition<ShortMessageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getContent()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getCreatedBy()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ShortMessageCriteria> copyFiltersAre(
        ShortMessageCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getContent(), copy.getContent()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getCreatedBy(), copy.getCreatedBy()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
