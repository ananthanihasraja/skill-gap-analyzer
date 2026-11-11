# Resume Skill Gap Analyzer

A full-stack web app that compares a resume against the skills a job role needs, then gives a score, a week-by-week learning plan, resume tips and rewrites for weak bullet points.

## Problem
Students often do not know which skills companies expect, so their resumes miss key requirements and are filled with weak, number-free bullet points. This tool shows the gap and a plan to close it.

## Features
- User registration and login with JWT authentication (BCrypt password hashing)
- Upload a PDF resume (Apache PDFBox) or paste text
- Choose a target role (Java Developer, Web Developer, Backend Developer, Data Analyst)
- Skill matching with aliases and whole-word detection ("java" does not match "javascript")
- Score out of 100, matched and missing skills
- Week-by-week learning plan with tutorial links
- Weak bullet detection and AI rewriting (Claude API, with a template fallback)
- Per-user analysis history stored in MySQL

## Tech Stack
Java 25 (LTS), Spring Boot 3, Spring Security, Spring Data JPA, MySQL, JWT (jjwt), Apache PDFBox, HTML/CSS/JavaScript, Docker

## API
| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | /api/auth/register | No | Create account, returns JWT |
| POST | /api/auth/login | No | Login, returns JWT |
| GET | /api/roles | No | List roles |
| POST | /api/analyze | Yes | Analyze pasted text `{ "text", "role" }` |
| POST | /api/analyze-pdf | Yes | Analyze PDF (multipart: `file`, `role`) |
| POST | /api/improve | Yes | Rewrite one bullet `{ "bullet", "role" }` |
| GET | /api/history | Yes | Your past analyses |

## Run locally
1. Install Java 25 (LTS) or newer, Maven and MySQL.
2. In MySQL: `CREATE DATABASE skillgap;`
3. Set your MySQL password (edit `application.properties` or set `DB_PASS`).
4. Optional, for AI rewriting: set `ANTHROPIC_API_KEY`.
5. Run `mvn spring-boot:run`
6. Open http://localhost:8080

## Environment variables (production)
`DB_URL`, `DB_USER`, `DB_PASS`, `JWT_SECRET` (32+ random characters), `ANTHROPIC_API_KEY` (optional), `PORT`

## Deploy
A `Dockerfile` is included. Works on Railway or Render with a MySQL database attached and the variables above.

## Screenshots
_Add 2 or 3 screenshots here after you run it._

## Future Improvements
- Admin page to add roles and skills
- Job description paste, to match against a real posting
- Resume export with the suggested fixes applied
