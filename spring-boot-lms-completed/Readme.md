## Loan Management System

This workspace contains a Spring Boot backend and a React frontend for a loan management system where customers can apply for loans and administrators can approve or reject applications.

### Backend

Run the API from `backend/`:

```bash
mvn spring-boot:run
```

Backend details:

- API base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 console: `http://localhost:8080/h2-console`
- Seeded admin account: `admin@loan.com` / `Admin@123`

### Frontend

Install dependencies and start the React app from `frontend/`:

```bash
npm install
npm run dev
```

Frontend details:

- Default dev URL: `http://localhost:5173`
- Set `VITE_API_BASE_URL` if the backend runs somewhere else.

### Features

- JWT authentication and role-based authorization
- Customer loan application submission
- Admin approval and rejection workflow
- H2 in-memory database for local development
- Swagger API documentation
- React UI with icons from `react-icons`

