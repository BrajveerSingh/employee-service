--create employee table
CREATE TABLE employee_db.employee (
  id BIGINT SIGNED PRIMARY KEY auto_increment,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL
);

--create database employee_db
CREATE DATABASE employee_db;
```

--Add column department_code to employee table
```sql
ALTER TABLE employee_db.employee
ADD COLUMN department_code VARCHAR(100) NOT NULL;
```