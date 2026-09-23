package org.arquiweb;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.arquiweb.dto.commands.SaveDegreeDTO;
import org.arquiweb.dto.commands.SaveStudentDTO;
import org.arquiweb.dto.queries.DegreeEnrollmentsDTO;
import org.arquiweb.dto.queries.DegreeReportDTO;
import org.arquiweb.dto.queries.ListDTO;
import org.arquiweb.dto.queries.StudentDTO;
import org.arquiweb.enums.Genre;
import org.arquiweb.services.DegreeService;
import org.arquiweb.services.StudentService;

import java.time.LocalDate;
import java.util.UUID;

public class Main {
    static void main() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("group-7");

        //Testing of Degree
        var degree = DegreeService.getInstance();

        ListDTO<DegreeEnrollmentsDTO> enrollments = degree.getEnrollments();
        ListDTO<DegreeReportDTO> reports = degree.getReports();
        UUID save = degree.save(new SaveDegreeDTO("Tudai"));

        System.out.println("------------ Testing of Degree ------------");

        System.out.println("Enrollments: \n" + enrollments + " \n " +
                            "Reports: \n" + reports + " \n " +
                            "Save: \n" + save);

        //Testing of Enrollment
        var student = StudentService.getInstance();

        student.enrollStudent(UUID.randomUUID(), UUID.randomUUID());

        //Testing of Student

        StudentDTO studentDTO = student.findByRecordNumber("0001");
        ListDTO<StudentDTO> students = student.getAll("name", "DESC");
        ListDTO<StudentDTO> studentsCity = student.getByDegreeAndCity(UUID.randomUUID(), "Tandil");
        ListDTO<StudentDTO> studentGenre = student.getByGenre(Genre.MALE);
        UUID studentId = student.save(new SaveStudentDTO(
                "Pepe",
                "12.345.678",
                "A01234",
                "Eslovaquia",
                Genre.X,
                LocalDate.now())
        );

        System.out.println("------------ Testing of Student ------------");

        System.out.println("RecordNumber: \n" + studentDTO + "\n" +
                            "getAll: \n" + students + "\n" +
                            "DegreeAndCity: \n" + studentsCity + "\n" +
                            "Genre: \n" + studentGenre + "\n" +
                            "Save: \n" + studentId);


        emf.close();
    }
}
