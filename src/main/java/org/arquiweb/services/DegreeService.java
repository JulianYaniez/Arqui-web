package org.arquiweb.services;

import org.arquiweb.dto.commands.SaveDegreeDTO;
import org.arquiweb.dto.queries.DegreeEnrollmentsDTO;
import org.arquiweb.dto.queries.DegreeReportDTO;
import org.arquiweb.dto.queries.ListDTO;
import org.arquiweb.entities.Degree;
import org.arquiweb.repositories.interfaces.DegreeRepository;
import org.arquiweb.util.RepositoryProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DegreeService {

    private static DegreeService instance;

    private DegreeRepository degreeRepository;

    public static DegreeService getInstance() {
        if (instance == null) {
            instance = new DegreeService(
                    RepositoryProvider.getDegreeRepository()
            );
        }
        return instance;
    }

    private DegreeService() {}
    private DegreeService(DegreeRepository degreeRepository) {
        this.degreeRepository = degreeRepository;
    }

    public UUID save(SaveDegreeDTO degree) {
        return degreeRepository.save(degree.toEntity());
    }

    public void saveAll(List<SaveDegreeDTO> degreesDTO) {
        List<Degree> degrees = degreesDTO.stream().map(SaveDegreeDTO::toEntity).toList();
        degreeRepository.saveAll(degrees);
    }

    public ListDTO<DegreeReportDTO> getReports() {
        return new ListDTO<>(degreeRepository.getReports());
    }

    public ListDTO<DegreeEnrollmentsDTO> getEnrollments() {
        return new ListDTO<>(degreeRepository.getEnrollments());
    }
}
