package org.arqui.tpe_3.config;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.arqui.tpe_3.entities.Degree;
import org.arqui.tpe_3.entities.Enrollment;
import org.arqui.tpe_3.entities.Student;
import org.arqui.tpe_3.entities.composites.EnrollmentId;
import org.arqui.tpe_3.enums.EnrollmentStatus;
import org.arqui.tpe_3.enums.Genre;
import org.arqui.tpe_3.repositories.jpa.DegreeJpaRepository;
import org.arqui.tpe_3.repositories.jpa.EnrollmentJpaRepository;
import org.arqui.tpe_3.repositories.jpa.StudentJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final StudentJpaRepository studentRepository;
    private final DegreeJpaRepository degreeRepository;
    private final EnrollmentJpaRepository enrollmentRepository;

    @Override
    public void run(String... args) throws Exception {

        if (enrollmentRepository.count() != 0) return;

        Faker faker = new Faker(Locale.of("es", "AR"));
        Random random = new Random();

        Set<Student> students = new HashSet<>();
        Set<Degree> degrees = new HashSet<>();

        for (String degreeName : degreeNames) {
            Degree degree = new Degree(degreeName);
            degrees.add(degree);
        }
        List<Degree> savedDegrees = degreeRepository.saveAll(degrees);

        for (int i = 0; i < 300; i++) {
            Student student = new Student(
                faker.name().firstName() + " " + faker.name().lastName(),
                faker.date().birthday().toLocalDateTime().toLocalDate(),
                Genre.values()[random.nextInt(Genre.values().length)],
                faker.idNumber().valid(),
                faker.address().city(),
                faker.bothify("LO-?#?#?#?")
            );
            students.add(student);
        }
        List<Student> savedStudents = studentRepository.saveAll(students);




        List<Enrollment> enrollments = new ArrayList<>();
        for (Student student : savedStudents) {
            Collections.shuffle(savedDegrees);

            int n = random.nextInt(1, 4);

            for (int i = 0; i <= n; i++) {

                LocalDate minStartDate = student.getDob().plusYears(18);
                long daysBetween = ChronoUnit.DAYS.between(minStartDate, LocalDate.now());
                LocalDate startedAt = minStartDate.plusDays(random.nextLong(daysBetween + 1));


                final int maxDegreeDuration = 5;
                boolean finished = (
                        startedAt.plusYears(maxDegreeDuration).isBefore(LocalDate.now()) &&
                        random.nextInt(1, 6) <= 2
                );

                EnrollmentStatus status;
                LocalDate finishedAt = null;

                if (finished) {
                    status = EnrollmentStatus.FINISHED;
                    finishedAt = startedAt.plusYears(random.nextInt(3, 10));

                    if (finishedAt.isAfter(LocalDate.now())) {
                        finishedAt = LocalDate.now();
                    }

                } else {
                    status = random.nextInt(0, 5) < 1 ? EnrollmentStatus.DROPPED : EnrollmentStatus.ONGOING;
                }

                Degree d = savedDegrees.get(i);
                EnrollmentId id = new EnrollmentId(student.getId(), d.getId());
                Enrollment enrollment = new Enrollment(
                        id,
                        student,
                        d,
                        startedAt,
                        finishedAt,
                        status
                );
                enrollments.add(enrollment);
            }
        }
        enrollments = enrollmentRepository.saveAll(enrollments);
        System.out.println("Database successfully seeded");
    }


    private final String[] degreeNames = {
    "Computer Science",
    "Mechanical Engineering",
    "Business Administration",
    "Nursing",
    "Psychology",
    "Economics",
    "Biology",
    "Civil Engineering",
    "Graphic Design",
    "Political Science",
    "English Literature",
    "Architecture",
    "Accounting",
    "Data Science",
    "Information Technology"
};
}
