package com.epam.mentoring.sql;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class DataGenerator {

    public static void main(String[] args) throws IOException {
        generateStudents(100000);
        generateExams(100000, 100000);
    }

    private static void generateStudents(int numOfStudents) throws IOException {
        File file = new File("module-3-advanced-sql/src/main/resources/students" + numOfStudents + ".sql");
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(
                "INSERT INTO student (id, name, surname, dob, primary_skill, created_datetime, updated_datetime)");
        printWriter.println("VALUES");
        for (int i = 1; i <= numOfStudents; i++) {
            printWriter.printf("('%d', '%s', '%s', '%s', '%s', '%s', '%s'),%n", i, "name" + i, "surname" + i,
                    LocalDate.now(), "primary skill" + i, LocalDateTime.now(), LocalDateTime.now());
        }
        printWriter.close();
    }

    private static void generateExams(int numOfExams, int numOfStudents) throws IOException {
        File file = new File("module-3-advanced-sql/src/main/resources/exams" + numOfExams + ".sql");
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        Random random = new Random();
        printWriter.println(
                "INSERT INTO exam_result (id, student_id, subject_id, mark)");
        printWriter.println("VALUES");
        for (int i = 1; i <= numOfExams; i++) {
            int studentId = random.nextInt(1, numOfStudents + 1);
            int subjectId = random.nextInt(1, 13);
            int mark = random.nextInt(1, 11);
            printWriter.printf("('%d', '%d', '%d', '%d'),%n", i, studentId, subjectId, mark);
        }
        printWriter.close();
    }

}
