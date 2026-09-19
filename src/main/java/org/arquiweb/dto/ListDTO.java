package org.arquiweb.dto;

import java.util.List;
import java.util.stream.Collectors;

public record ListDTO<T> (
    List<T> list
) {
    @Override
    public String toString() {
        return list.stream()
                .map(T::toString)
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }
}
