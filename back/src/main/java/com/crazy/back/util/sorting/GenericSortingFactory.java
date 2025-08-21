package com.crazy.back.util.sorting;

import com.crazy.back.exception.InvalidSortFieldException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.function.BiFunction;

@Slf4j
@Component
public class GenericSortingFactory {

    public <T> boolean isValidSortField(Class<T> entityClass, String fieldName) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            return field.isAnnotationPresent(Sortable.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public <T> Comparator<T> createComparator(Class<T> entityClass, Sort sort) {
        // Handle unsorted case
        if (sort.isUnsorted()) {
            return null;
        }

        Comparator<T> comparator = null;

        for (Sort.Order order : sort) {
            String fieldName = order.getProperty();

            if (!isValidSortField(entityClass, fieldName)) {
                throw new InvalidSortFieldException(
                        String.format("Field '%s' is not sortable for %s. Available sortable fields can be found in the API documentation.",
                                fieldName, entityClass.getSimpleName())
                );
            }

            Comparator<T> fieldComparator = createFieldComparator(entityClass, fieldName);

            if (order.getDirection() == Sort.Direction.DESC) {
                fieldComparator = fieldComparator.reversed();
            }

            if (comparator == null) {
                comparator = fieldComparator;
            } else {
                comparator = comparator.thenComparing(fieldComparator);
            }
        }

        return comparator;
    }

    private <T> Comparator<T> createFieldComparator(Class<T> entityClass, String fieldName) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Sortable sortable = field.getAnnotation(Sortable.class);

            return switch (sortable.type()) {
                case STRING -> (o1, o2) -> {
                    String val1 = getStringValue(field, o1);
                    String val2 = getStringValue(field, o2);
                    return compareNullSafe(val1, val2, String::compareToIgnoreCase);
                };

                case NUMBER -> (o1, o2) -> {
                    Double val1 = getNumericValue(field, o1);
                    Double val2 = getNumericValue(field, o2);
                    return compareNullSafe(val1, val2, Double::compareTo);
                };

                case DATE -> (o1, o2) -> {
                    Comparable<Object> val1 = getComparableValue(field, o1);
                    Comparable<Object> val2 = getComparableValue(field, o2);
                    return compareNullSafe(val1, val2, Comparable::compareTo);
                };

                case CUSTOM -> createCustomComparator();
            };

        } catch (NoSuchFieldException e) {
            throw new InvalidSortFieldException("Field not found: " + fieldName);
        }
    }

    private <T> String getStringValue(Field field, T obj) {
        try {
            Object value = field.get(obj);
            return value != null ? value.toString() : null;
        } catch (IllegalAccessException e) {
            log.warn("Cannot access field {}: {}", field.getName(), e.getMessage());
            return null;
        }
    }

    private <T> Double getNumericValue(Field field, T obj) {
        try {
            Object value = field.get(obj);
            if (value == null) return null;

            String stringValue = value.toString().replaceAll("[^0-9.-]", "");
            if (stringValue.isEmpty() || "unknown".equalsIgnoreCase(value.toString())) {
                return null;
            }

            return Double.parseDouble(stringValue);
        } catch (IllegalAccessException | NumberFormatException e) {
            log.warn("Cannot parse numeric value for field {}: {}", field.getName(), e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Comparable<Object> getComparableValue(Field field, T obj) {
        try {
            Object value = field.get(obj);
            return (Comparable<Object>) value;
        } catch (IllegalAccessException | ClassCastException e) {
            log.warn("Cannot get comparable value for field {}: {}", field.getName(), e.getMessage());
            return null;
        }
    }

    private <T, V> int compareNullSafe(V val1, V val2, BiFunction<V, V, Integer> comparator) {
        if (val1 == null && val2 == null) return 0;
        if (val1 == null) return -1;
        if (val2 == null) return 1;
        return comparator.apply(val1, val2);
    }

    private <T> Comparator<T> createCustomComparator() {
        throw new UnsupportedOperationException("Custom comparators not implemented yet");
    }
}