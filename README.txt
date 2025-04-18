#  Task Manager Web App

A full-stack task management application that allows users to create, edit, and delete tasks with user authentication. Designed for efficiency, security, and ease of use.

##  Features

- User registration and login system
- Create, update, delete tasks
- Task status: Pending / In Progress / Completed
- Task descriptions and timestamps
- Password encryption using bcrypt
- SQL injection protection
- XSS, Clickjacking, and MITM defense

##  Tech Stack

Frontend: HTML, CSS, JavaScript  
Backend: Node.js, Express.js  
Database: MySQL  
Deployment: InfinityFree (42web.io) with Nginx + custom domain

##  Demo
 Live Demo: http://project0-task-manager.42web.io  
 GitHub Repo: https://github.com/jingtianW1ng/task-manager

##  Security Features

- Password hashing via bcrypt
- Input sanitization to prevent SQL Injection
- Basic protection against XSS and MITM attacks
- HTTP headers set to prevent clickjacking

##  File Structure (simplified)

/public          → Frontend HTML/CSS/JS  
/routes          → Express routes  
/db              → MySQL connection & queries  
/security        → CSRF, validation, rate limit  
/views           → Templates (if any)

##  How to Run Locally

1. Clone the repo:
   git clone https://github.com/jingtianW1ng/task-manager.git

2. Install dependencies:
   npm install

3. Set up .env file:
   DB_HOST=localhost  
   DB_USER=root  
   DB_PASS=yourpassword  
   DB_NAME=taskdb

4. Start the server:
   node app.js

##  About Me

Jingtian Wang  
Adelaide, South Australia  
ajssjaus@gmail.com  
GitHub: https://github.com/jingtianW1ng

---

Feel free to fork, contribute, or give feedback!
