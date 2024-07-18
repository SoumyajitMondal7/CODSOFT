package com.grade.calculator;
import java.util.Scanner;
import java.util.InputMismatchException;

class GradeCalculator {
    private double[] gradeThresholds;
    private String[] gradeLetters;

    public GradeCalculator(double[] gradeThresholds, String[] gradeLetters) {
        this.gradeThresholds = gradeThresholds;
        this.gradeLetters = gradeLetters;
    }

    public String calculateGrade(double averagePercentage) {
        for (int i = 0; i < gradeThresholds.length; i++) {
            if (averagePercentage >= gradeThresholds[i]) {
                return gradeLetters[i];
            }
        }
        return "F";
    }
}

public class StudentGrades {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Define grade thresholds and letters
        System.out.println("Enter the number of grade levels:");
        int gradeLevels = scanner.nextInt();
        double[] gradeThresholds = new double[gradeLevels];
        String[] gradeLetters = new String[gradeLevels];

        System.out.println("Enter the grade levels and their corresponding minimum percentage thresholds:");
        for (int i = 0; i < gradeLevels; i++) {
            System.out.print("Grade letter: ");
            gradeLetters[i] = scanner.next();
            System.out.print("Minimum percentage for " + gradeLetters[i] + ": ");
            gradeThresholds[i] = scanner.nextDouble();
        }

        GradeCalculator gradeCalculator = new GradeCalculator(gradeThresholds, gradeLetters);

        boolean exit = false;
        while (!exit) {
            System.out.println("\nMenu:");
            System.out.println("1. Calculate grades for a single student");
            System.out.println("2. Calculate grades for multiple students");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    calculateGradesForSingleStudent(scanner, gradeCalculator);
                    break;
                case 2:
                    calculateGradesForMultipleStudents(scanner, gradeCalculator);
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }

    private static void calculateGradesForSingleStudent(Scanner scanner, GradeCalculator gradeCalculator) {
        try {
            System.out.print("Enter the number of subjects: ");
            int numSubjects = scanner.nextInt();

            int[] marks = new int[numSubjects];
            int totalMarks = 0;

            for (int i = 0; i < numSubjects; i++) {
                System.out.print("Enter marks for subject " + (i + 1) + ": ");
                marks[i] = scanner.nextInt();
                totalMarks += marks[i];
            }

            double averagePercentage = (double) totalMarks / numSubjects;
            String grade = gradeCalculator.calculateGrade(averagePercentage);

            displayResults(totalMarks, averagePercentage, grade);

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
            scanner.next(); // clear the invalid input
        }
    }

    private static void calculateGradesForMultipleStudents(Scanner scanner, GradeCalculator gradeCalculator) {
        try {
            System.out.print("Enter the number of students: ");
            int numStudents = scanner.nextInt();

            for (int i = 0; i < numStudents; i++) {
                System.out.println("\nStudent " + (i + 1) + ":");
                calculateGradesForSingleStudent(scanner, gradeCalculator);
            }

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
            scanner.next(); // clear the invalid input
        }
    }

    private static void displayResults(int totalMarks, double averagePercentage, String grade) {
        System.out.println("\nResults:");
        System.out.printf("Total Marks: %d\n", totalMarks);
        System.out.printf("Average Percentage: %.2f\n", averagePercentage);
        System.out.printf("Grade: %s\n", grade);
        System.out.println("=======================================");
    }
}
