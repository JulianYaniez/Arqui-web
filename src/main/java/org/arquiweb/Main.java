package org.arquiweb;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.arquiweb.dto.commands.SaveDegreeDTO;
import org.arquiweb.dto.commands.SaveStudentDTO;
import org.arquiweb.dto.queries.DegreeEnrollmentsDTO;
import org.arquiweb.dto.queries.DegreeReportDTO;
import org.arquiweb.dto.queries.ListDTO;
import org.arquiweb.dto.queries.StudentDTO;
import org.arquiweb.entities.Student;
import org.arquiweb.enums.Genre;
import org.arquiweb.services.DegreeService;
import org.arquiweb.services.StudentService;

import java.time.LocalDate;
import java.util.UUID;

public class Main {
    static void main() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("group-7");

        var degreeService = DegreeService.getInstance();
        var studentService = StudentService.getInstance();

        System.out.println("Activity 2");

        // A - Save Student
        SaveStudentDTO std1 = new SaveStudentDTO(
               "Pedro Pablo Sanchez",
               "LO-20102",
               "45.223.241",
                "Tandil",
                Genre.MALE,
                LocalDate.of(2004, 12, 12)
        );
        UUID testStudentId = studentService.save(std1);


        // B - Enroll Student
        UUID testDegreeId = UUID.fromString("0191e4ab-7f12-7001-8a3b-2f4e8c1d5a90");
        studentService.enrollStudent(testStudentId, testDegreeId);


        // C - Get Students By Criteria
        ListDTO<StudentDTO> studentsByCriteria = studentService.getAll("city", "ASC");
        System.out.println("-- Students by criteria --");
        System.out.println(studentsByCriteria);
        System.out.println();


        // D - Get Student By Record Number
        StudentDTO testStudent = studentsByCriteria.list().getFirst();
        StudentDTO studentByRecordNumber = studentService.findByRecordNumber(testStudent.recordNumber());
        System.out.println("-- Student by Record Number --");
        System.out.println(studentByRecordNumber);
        System.out.println();


        // E - Get Students By Genre
        ListDTO<StudentDTO> studentsByGenre = studentService.getByGenre(Genre.MALE);
        System.out.println("-- Students by Genre --");
        System.out.println(studentsByGenre);
        System.out.println();

        // F - Get Degrees With Enrolled Students, Ordered By Amount
        ListDTO<DegreeEnrollmentsDTO> enrollments = degreeService.getEnrollments();
        System.out.println("-- Degrees by Enrollment Amount --");
        System.out.println(enrollments);
        System.out.println();

        // G - Get Students By Degree & City
        ListDTO<StudentDTO> studentsByDegreeAndCity = studentService.getByDegreeAndCity(testDegreeId, "Tandil");
        System.out.println("-- Students by Degree and City --");
        System.out.println(studentsByDegreeAndCity);
        System.out.println("\n\n");


        System.out.println("Activity 3");
        ListDTO<DegreeReportDTO> reports = degreeService.getReports();
        System.out.println("-- Degrees Reports --");
        System.out.println(reports);
        System.out.println();

        emf.close();
    }
}
