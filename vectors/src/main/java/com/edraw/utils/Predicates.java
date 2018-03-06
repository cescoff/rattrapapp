package com.edraw.utils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;

public class Predicates {

    public static <T> Predicate<T> FullTextFilter(final String textContains, final Function<T, String> toText) {
        return new com.google.common.base.Predicate<T>() {
            @Override
            public boolean apply(T t) {
                return StringUtils.containsIgnoreCase(toText.apply(t), textContains);
            }
        };
    }

    public static <T> Predicate<T> FullTextFilterAnd(final Iterable<String> textContains, final Function<T, String> toText) {
        return com.google.common.base.Predicates.and(Iterables.transform(textContains, new Function<String, Predicate<T>>() {

            @Override
            public Predicate<T> apply(String s) {
                return FullTextFilter(s, toText);
            }

        }));
    }

    public static <T> Predicate<T> FullTextFilterOr(final Iterable<String> textContains, final Function<T, String> toText) {
        return com.google.common.base.Predicates.or(Iterables.transform(textContains, new Function<String, Predicate<T>>() {

            @Override
            public Predicate<T> apply(String s) {
                return FullTextFilter(s, toText);
            }

        }));
    }

}
