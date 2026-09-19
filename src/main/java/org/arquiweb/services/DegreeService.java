package org.arquiweb.services;

import org.arquiweb.dto.DegreeReportDTO;
import org.arquiweb.dto.ListDTO;
import org.arquiweb.repositories.interfaces.DegreeRepository;
import org.arquiweb.util.RepositoryProvider;

import java.util.List;

public class DegreeService {

    private static DegreeService instance;
    public static DegreeService getInstance() {
        if (instance == null) {
            instance = new DegreeService(
                    RepositoryProvider.getDegreeRepository()
            );
        }
        return instance;
    }

    private DegreeRepository degreeRepository;

    private DegreeService() {}

    private DegreeService(DegreeRepository degreeRepository) {
        this.degreeRepository = degreeRepository;
    }

    public ListDTO<DegreeReportDTO> getReports() {
        return new ListDTO<>(degreeRepository.getReports());
    }
}
