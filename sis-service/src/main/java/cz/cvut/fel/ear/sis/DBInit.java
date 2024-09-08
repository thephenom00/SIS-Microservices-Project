package cz.cvut.fel.ear.sis;

import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Course;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.model.Teacher;
import cz.cvut.fel.ear.sis.repository.ClassroomRepository;
import cz.cvut.fel.ear.sis.repository.CourseRepository;
import cz.cvut.fel.ear.sis.repository.ParallelRepository;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.repository.SemesterRepository;
import cz.cvut.fel.ear.sis.service.interfaces.AdminService;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;

import static cz.cvut.fel.ear.sis.utils.enums.DayOfWeek.FRI;
import static cz.cvut.fel.ear.sis.utils.enums.DayOfWeek.MON;
import static cz.cvut.fel.ear.sis.utils.enums.DayOfWeek.THU;
import static cz.cvut.fel.ear.sis.utils.enums.DayOfWeek.TUE;
import static cz.cvut.fel.ear.sis.utils.enums.DayOfWeek.WED;
import static cz.cvut.fel.ear.sis.utils.enums.TimeSlot.SLOT1;
import static cz.cvut.fel.ear.sis.utils.enums.TimeSlot.SLOT2;
import static cz.cvut.fel.ear.sis.utils.enums.TimeSlot.SLOT3;
import static cz.cvut.fel.ear.sis.utils.enums.TimeSlot.SLOT4;
import static cz.cvut.fel.ear.sis.utils.enums.TimeSlot.SLOT5;
import static cz.cvut.fel.ear.sis.utils.enums.TimeSlot.SLOT7;

@Service
public class DBInit {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ParallelRepository parallelRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminService adminService;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            clearDB();
            Teacher sebekji1 = personRepository.save(new Teacher("Jiří", "Šebek", "sebekji1@fel.cvut.cz", "987654321", LocalDate.of(1999, 1, 1), "sebekji1", passwordEncoder.encode("123")));
            Teacher novakjan = personRepository.save(new Teacher("Jan", "Novák", "novajkan@fel.cvut.cz", "12345678", LocalDate.of(1999, 1, 1), "novakjan", passwordEncoder.encode("123")));
            Teacher pabloesc = personRepository.save(new Teacher("Pablo", "Escobar", "pabloesc@fel.cvut.cz", "098765432", LocalDate.of(1999, 1, 1), "pabloesc", passwordEncoder.encode("123")));

            Semester nextSemester = adminService.createSemester(2025, SemesterType.FALL);
            Semester currentSemester = adminService.createSemester(2025, SemesterType.SPRING);
            currentSemester.setIsActive(true);
            semesterRepository.save(currentSemester);
            semesterRepository.save(nextSemester);

            Classroom classroom = classroomRepository.save(Classroom.builder().code("KN:E-127").capacity(50).build());

            Course nss = courseRepository.save(Course.builder().teacher(sebekji1).name("Návrh Softwarových Systémů").code("B6B36NSS").ECTS(5).language(Locale.forLanguageTag("cz")).build());
            Course pjv = courseRepository.save(Course.builder().teacher(novakjan).name("Programování v Jazyce Java").code("B6B36PJV").ECTS(5).language(Locale.forLanguageTag("cz")).build());
            Course mob = courseRepository.save(Course.builder().teacher(pabloesc).name("Mezinárodní obchod").code("B1B63MOB").ECTS(5).language(Locale.forLanguageTag("cz")).build());

            Parallel nss_parallel1 = parallelRepository.save(Parallel.builder().capacity(20).timeSlot(SLOT1).dayOfWeek(MON).semester(nextSemester).classroom(classroom).course(nss).build());
            Parallel nss_parallel2 = parallelRepository.save(Parallel.builder().capacity(30).timeSlot(SLOT2).dayOfWeek(TUE).semester(nextSemester).classroom(classroom).course(nss).build());
            Parallel nss_parallel3 = parallelRepository.save(Parallel.builder().capacity(50).timeSlot(SLOT3).dayOfWeek(WED).semester(nextSemester).classroom(classroom).course(nss).build());
            Parallel pjv_parallel1 = parallelRepository.save(Parallel.builder().capacity(60).timeSlot(SLOT4).dayOfWeek(THU).semester(nextSemester).classroom(classroom).course(pjv).build());
            Parallel pjv_parallel2 = parallelRepository.save(Parallel.builder().capacity(20).timeSlot(SLOT5).dayOfWeek(FRI).semester(nextSemester).classroom(classroom).course(pjv).build());
            Parallel pjv_parallel3 = parallelRepository.save(Parallel.builder().capacity(30).timeSlot(SLOT1).dayOfWeek(MON).semester(nextSemester).classroom(classroom).course(pjv).build());
            Parallel mob_parallel1 = parallelRepository.save(Parallel.builder().capacity(40).timeSlot(SLOT2).dayOfWeek(FRI).semester(nextSemester).classroom(classroom).course(mob).build());
            Parallel mob_parallel2 = parallelRepository.save(Parallel.builder().capacity(50).timeSlot(SLOT7).dayOfWeek(WED).semester(nextSemester).classroom(classroom).course(mob).build());
            Parallel mob_parallel3 = parallelRepository.save(Parallel.builder().capacity(60).timeSlot(SLOT3).dayOfWeek(THU).semester(nextSemester).classroom(classroom).course(mob).build());


            nss.addParallel(nss_parallel1);
            nss.addParallel(nss_parallel2);
            nss.addParallel(nss_parallel3);
            pjv.addParallel(pjv_parallel1);
            pjv.addParallel(pjv_parallel2);
            pjv.addParallel(pjv_parallel3);
            mob.addParallel(mob_parallel1);
            mob.addParallel(mob_parallel2);
            mob.addParallel(mob_parallel3);

            courseRepository.save(nss);
            courseRepository.save(pjv);
            courseRepository.save(mob);
        };
    }

    public void clearDB() {
        parallelRepository.deleteAll();
        courseRepository.deleteAll();
        classroomRepository.deleteAll();
        semesterRepository.deleteAll();
        personRepository.deleteAll();
    }

}
