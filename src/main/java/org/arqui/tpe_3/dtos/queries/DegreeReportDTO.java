package org.arqui.tpe_3.dtos.queries;

import java.util.List;
import java.util.stream.Collectors;

public record DegreeReportDTO(
        String degreeName,
        List<DegreeYearlyStatsDTO> years
) {}
