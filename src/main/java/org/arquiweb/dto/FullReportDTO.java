package org.arquiweb.dto;

import java.util.List;
import java.util.stream.Collectors;

public record FullReportDTO (
    List<DegreeReportDTO> degrees
) {
    @Override
    public String toString() {
        return degrees.stream()
                .map(DegreeReportDTO::toString)
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }
}
