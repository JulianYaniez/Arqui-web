package org.arquiweb.dto;

import java.util.List;
import java.util.stream.Collectors;

public record DegreeReportDTO(
        String degreeName,
        List<DegreeYearlyStatsDTO> years
) {

    @Override
    public String toString() {
        String yearsString =  years.stream()
                .map(DegreeYearlyStatsDTO::toString)
                .collect(Collectors.joining(",\n"));
        return """
               {
                    "degree": "%s",
                    "years": [
               %s
                    ]
               }""".formatted(degreeName, yearsString.indent(12).stripTrailing());
    }
}
