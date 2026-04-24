# 📘 Internship Management System

## 🧠 Overview

The Internship Management System is a role-based desktop application developed using **Java (Swing)** and **MySQL**. It is designed to streamline the internship process by connecting **students, companies, and administrators** on a single platform. The system allows students to apply for internships, companies to evaluate candidates, and administrators to monitor overall system activity.

The project follows a structured architecture using **DAO (Data Access Object)**, **JDBC**, and **Object-Oriented Programming (OOP)** principles.

---

## 🏗️ System Architecture

The application follows a layered structure:

```
UI (Swing)
   ↓
DAO Layer (Business Logic)
   ↓
JDBC (Database Connectivity)
   ↓
MySQL Database
```

* **UI Layer**: Handles user interaction using Java Swing
* **DAO Layer**: Executes SQL queries and processes data
* **DB Layer**: Stores all data (users, internships, applications, etc.)

---

## 👥 User Roles

### 1. Student

* Manage profile (CGPA, college, graduation year)
* Add/remove skills with levels
* View internships
* Apply for internships
* View application status
* Get recommendations based on skills

---

### 2. Company

* Add internships (title, stipend, CGPA criteria)
* View applicants
* Shortlist/reject candidates manually
* Automatically evaluate candidates based on CGPA

---

### 3. Admin

* View system statistics
* Identify unplaced students
* Detect high-risk students (too many applications)
* View inactive companies
* View all applications in the system

---

## 🔐 Authentication System

### Signup

* Inserts user into `users` table
* Based on role:

  * Student → insert into `students`
  * Company → insert into `companies`
  * Admin → only users table

### Login

* Validates credentials using:

  ```sql
  SELECT user_id, role FROM users WHERE email = ? AND password = ?
  ```
* Redirects user based on role

---

## 🗄️ Database Design

### Core Tables

* **users** → stores login credentials and roles
* **students** → stores student-specific data
* **companies** → stores company details
* **internships** → internship postings
* **applications** → student applications
* **skills** → available skills
* **student_skills** → mapping of student skills
* **internship_skills** → required skills for internships

---

## 🔄 Core Features & Workflow

---

### 🧾 1. Profile Management

* Student profile is fetched using JOIN between `users` and `students`
* Updates are done using:

  ```sql
  UPDATE students SET cgpa=?, college=?, graduation_year=? WHERE student_id=?
  ```

---

### 🧠 2. Skill Management

* Add skill:

  * If exists → update level
  * Else → insert new record

* Remove skill:

  ```sql
  DELETE FROM student_skills WHERE student_id=? AND skill_id=?
  ```

---

### 💼 3. Internship Viewing

* Internships are fetched using JOIN:

  ```sql
  SELECT i.title, c.company_name, i.stipend
  FROM internships i
  JOIN companies c ON i.company_id = c.company_id
  ```

* Displayed using JTable in UI

---

### 🚀 4. Apply Internship (Core Logic)

Steps:

1. Check duplicate application
2. Check eligibility using DB function:

   ```sql
   SELECT check_eligibility(studentId, internshipId)
   ```
3. Apply using stored procedure:

   ```sql
   CALL apply_internship(studentId, internshipId)
   ```

---

### 🤖 5. Recommendation System

* Matches student skills with internship skills
* Calculates match score using:

  ```sql
  COUNT(*) AS match_score
  ```
* Orders internships by highest match

---

### 🏢 6. Company Operations

* Add internship:

  ```sql
  INSERT INTO internships(...)
  ```

* Auto evaluation:

  ```sql
  UPDATE applications
  SET status = 'shortlisted'
  WHERE cgpa >= min_cgpa
  ```

* Manual status update via stored procedure

---

### 🛠️ 7. Admin Monitoring

* Total stats using COUNT queries
* Unplaced students using NOT IN
* Risk detection using HAVING COUNT > 2
* Inactive companies using LEFT JOIN

---

## 🔌 JDBC Implementation

### DBConnection Class

* Loads MySQL driver
* Creates connection using:

  ```
  jdbc:mysql://localhost:3306/internship_db
  ```
* Returns `Connection` object for DAO usage

---

### JDBC Components Used

* **Connection** → database connection
* **PreparedStatement** → execute parameterized queries
* **ResultSet** → fetch data
* **CallableStatement** → execute stored procedures

---

## 🧠 OOP Concepts Used

### 🔹 Inheritance

* `User` → base class
* `Student`, `Company` → derived classes

### 🔹 Polymorphism

* Method overriding (`displayRole()`)
* Method overloading (`log()` methods)

### 🔹 Abstraction

* Abstract class `User`

### 🔹 Interface

* `Trackable` interface implemented by DAO classes

---

## ⚠️ Custom Exceptions

Custom exceptions improve error handling:

* `ApplicationException` → general errors
* `DataNotFoundException` → no data found
* `DuplicateUserException` → duplicate actions
* `InvalidInputException` → invalid input
* `UnauthorizedActionException` → invalid operations

---

## 🔄 CRUD Operations

| Operation | Example                       |
| --------- | ----------------------------- |
| INSERT    | Signup, Add Internship        |
| UPDATE    | Profile update, status update |
| DELETE    | Remove skill                  |
| SELECT    | Login, recommendations        |

---

## 🔥 Key Highlights

* Role-based system (Student, Company, Admin)
* Skill-based recommendation engine
* Automated candidate evaluation
* Use of stored procedures and SQL functions
* Clean architecture using DAO pattern
* Strong use of OOP principles

---

## 🎯 Conclusion

This project demonstrates a complete full-stack desktop application integrating UI, backend logic, and database operations. It emphasizes real-world features such as role-based access, recommendation systems, and automated decision-making, making it a strong project for internship and placement interviews.

---