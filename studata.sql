
---

## 🧑‍💻 How to Run

### ✅ 1. Setup MySQL Database

1. Open MySQL Workbench or terminal.
2. Create the database and table:
   ```sql
   CREATE DATABASE studata;

   USE studata;

   CREATE TABLE sdata (
     student_id VARCHAR(10) PRIMARY KEY,
     first_name VARCHAR(50),
     last_name VARCHAR(50),
     major VARCHAR(50),
     phone VARCHAR(15),
     gpa FLOAT,
     date_of_birth DATE
   );
