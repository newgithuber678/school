package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.RecordNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final static Logger LOGGER = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        LOGGER.info("Student.create was invoked!");
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        LOGGER.info("Student.find was invoked!");
        return studentRepository.findById(id).orElseThrow(RecordNotFoundException::new);
    }

    public boolean deleteStudent(long id) {
        LOGGER.info("Student.delete was invoked!");
        return studentRepository.findById(id).map(entity -> {
            studentRepository.delete(entity);
            return true;
        }).orElse(false);
    }

    public Student editStudent(Student student) {
        LOGGER.info("Student.edit was invoked!");
        return studentRepository.findById(student.getId())
                .map(entity -> studentRepository.save(student))
                .orElse(null);
    }

    public Collection<Student> getByAgeStudents(int age) {
        LOGGER.info("Student.getByAgeStudents was invoked!");
        return studentRepository.findByAge(age);
    }

    public Collection<Student> getByAgeBetween(int min, int max) {
        LOGGER.info("Student.getByAgeBetween was invoked!");
        return studentRepository.findAllByAgeBetween(min, max);
    }

    public Collection<Student> getAllStudents() {
        LOGGER.info("Student.getAllStudents was invoked!");
        return studentRepository.findAll();
    }

    public Faculty getFacultyByStudent(Long id) {
        LOGGER.info("Student.getFacultyByStudent was invoked!");
        return findStudent(id).getFaculty();
    }

    public int getStudentCount() {
        LOGGER.info("Student.getStudentCount was invoked!");
        return studentRepository.countStudents();
    }

    public double getAvgAge() {
        LOGGER.info("Student.getAvgAge was invoked!");
        return studentRepository.avgAge();
    }

    public Collection<Student> getLastFive() {
        LOGGER.info("Student.getLastFive was invoked!");
        return studentRepository.getLastFive();
    }

    public Collection<String> getNameStartsWithA() {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .map(String::toUpperCase)
                .filter(name -> name.startsWith("A"))
                .sorted()
                .collect(Collectors.toList());
    }

    public double getAverageAge() {
        return studentRepository.findAll().stream()
                .mapToDouble(Student::getAge)
                .average()
                .orElse(0);
    }

    public void printParallel() {
        var students = studentRepository.findAll();

        LOGGER.info(students.get(0).toString());
        LOGGER.info(students.get(1).toString());

        new Thread(() -> {
            try {
                Thread.sleep(3000); // 3000 ms
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            LOGGER.info(students.get(2).toString());
            LOGGER.info(students.get(3).toString());
        }).start();

        new Thread(() -> {
            LOGGER.info(students.get(4).toString());
            LOGGER.info(students.get(5).toString());
        }).start();
    }

    public void printSynchronized(){
        var students = studentRepository.findAll();

        print(students.get(0));
        print(students.get(1));

        new Thread(() -> {
            try {
                Thread.sleep(3000); // 3000 ms
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            print(students.get(2));
            print(students.get(3));
        }).start();

        new Thread(() -> {
            print(students.get(4));
            print(students.get(5));
        }).start();
    }

    private synchronized void print(Object o){
        LOGGER.info(o.toString());
    }
}
