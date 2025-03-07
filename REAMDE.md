Student Course Management System

Overview

This Java project is a student course management system that allows adding students, managing courses, reading grades, processing student preferences, and assigning students to courses based on their preferences and grades.

Features

Add students to the system (Bachelor's and Master's levels).

Add courses with a maximum capacity.

Read student grades from input files.

Post student grades in an output file.

Handle grade contests.

Assign students to courses based on their preferences and grades.

Output student and course information to files.

Project Structure

org.example/
    ├── Student.java
    ├── StudentLicenta.java
    ├── StudentMaster.java
    ├── Curs.java
    ├── Secretariat.java
    ├── Main.java

Input Format

The system reads commands from an input file formatted as follows:

adauga_student licenta nume_student
adauga_curs licenta nume_curs capacitate
citeste_mediile
posteaza_mediile
contestatie nume_student noua_medie
adauga_preferinte nume_student curs1 curs2 ...
repartizeaza
posteaza_curs nume_curs
posteaza_student nume_student

Output

The system writes results to an output file named test_case_folder.out.

The output includes sorted student grades, assigned courses, and contestation results.

Error Handling

Detects duplicate students.

Checks for unknown study programs.

Handles missing input files gracefully.

Example Usage

Input file:
adauga_student licenta Alice
adauga_student master Bob
adauga_curs licenta Math 2
adauga_curs master Physics 1
citeste_mediile
repartizeaza
posteaza_curs Math
posteaza_student Alice
