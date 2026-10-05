package org.arqui.tpe_3.dtos;

public record DegreeEnrollmentsDTO  (
    String name,
    Long enrollmentsAmount
){

    @Override
    public String toString() {
        return """
           {
                "name": %s,
                "enrollments": %d,
           }
           """.formatted(name, enrollmentsAmount);
    }
}
