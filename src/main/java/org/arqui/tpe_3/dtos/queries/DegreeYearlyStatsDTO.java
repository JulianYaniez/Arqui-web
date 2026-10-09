package org.arqui.tpe_3.dtos.queries;

public record DegreeYearlyStatsDTO(
        Integer year,
        Long enrollments,
        Long graduates
) {}
