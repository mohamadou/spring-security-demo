package com.mohamadou.springsecuritydemo.student;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/student/students")
public class StudentController {

    private static final List<Student> studentList = Arrays.asList(
            new Student(1, "John Doe"),
            new Student(2, "Xavier Lee"),
            new Student(3, "Miriam Niang")
    );

    @GetMapping(path = "/test")
    public String test() {
        return "Hello world";
    }

    @GetMapping(path = "{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Student getStudent(@PathVariable Integer studentId) {

        return studentList.stream()
                 .filter(student -> student.getStudentId().equals(studentId))
                 .findFirst()
                 .orElseThrow(() ->new IllegalArgumentException("Student with id = "+ studentId+ " is not found"));
    }
}
