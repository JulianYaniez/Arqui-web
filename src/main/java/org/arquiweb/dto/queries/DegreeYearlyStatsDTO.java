package org.arquiweb.dto.queries;

public record DegreeYearlyStatsDTO(
        Integer year,
        Long enrollments,
        Long graduates
) {

    @Override
    public String toString() {
        return """
        {
            "year":  %d,
            "enrollments":  %d,
            "graduates":  %d
        }""".formatted(year, enrollments, graduates);
    }
}
