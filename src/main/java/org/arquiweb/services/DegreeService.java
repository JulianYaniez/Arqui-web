package org.arquiweb.services;

import org.arquiweb.dto.queries.DegreeReportDTO;
import org.arquiweb.dto.queries.ListDTO;
import org.arquiweb.entities.Degree;
import org.arquiweb.repositories.interfaces.DegreeRepository;
import org.arquiweb.util.RepositoryProvider;

import java.util.List;
import java.util.UUID;

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
    public UUID save(Degree degree) { return degreeRepository.save(degree); }
    public void saveAll(List<Degree> degrees) { degreeRepository.saveAll(degrees); }
}
