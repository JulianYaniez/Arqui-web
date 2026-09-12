package org.arquiweb.repositories.impl;

import org.arquiweb.entities.Degree;
import org.arquiweb.repositories.interfaces.DegreeRepository;

import java.util.List;
import java.util.UUID;

public class JpaDegreeRepository extends JpaRepository implements DegreeRepository {

    public UUID save(Degree degree) {

        return degree.getId();
    }

    public void saveAll(List<Degree> degrees) {

    }
}
