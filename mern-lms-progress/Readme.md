# Loan Management REST API Backend Documentation (MERN Stack + MongoDB)

This documentation describes how to implement the equivalent Loan Management APIs in a MERN Stack (MongoDB, Express.js, React.js, Node.js).

---

# Base URL

```text
http://localhost:5000/api/loans
```

---

# Authentication

All endpoints require JWT authentication.

**Header**

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# Roles

| Role     | Permissions                           |
| -------- | ------------------------------------- |
| CUSTOMER | Apply for loan, View own loans        |
| ADMIN    | View loans, Approve loan, Reject loan |

---

# 1. Apply Loan

### Endpoint

```http
POST /api/loans
```

### Authorization

```text
CUSTOMER
```

### Request Body

```json
{
  "loanAmount": 500000,
  "loanType": "HOME",
  "tenure": 120,
  "purpose": "Purchase Home",
  "annualIncome": 1200000
}
```

### Validation

| Field        | Type   | Required |
| ------------ | ------ | -------- |
| loanAmount   | Number | Yes      |
| loanType     | String | Yes      |
| tenure       | Number | Yes      |
| purpose      | String | Yes      |
| annualIncome | Number | Yes      |

---

### Success Response

```json
{
  "id": "66a4f38ef5d60bca64d9d901",
  "loanAmount": 500000,
  "loanType": "HOME",
  "tenure": 120,
  "purpose": "Purchase Home",
  "annualIncome": 1200000,
  "status": "PENDING",
  "customerId": "66a4f38ef5d60bca64d9d800",
  "createdAt": "2026-07-19T10:00:00Z"
}
```

---

# 2. Get User Loans

### Endpoint

```http
GET /api/loans
```

### Authorization

```text
CUSTOMER / ADMIN
```

Customer

Returns only logged-in customer's loans.

Admin

Returns all loans (optional implementation).

---

### Response

```json
[
  {
    "id": "66a4f38ef5d60bca64d9d901",
    "loanAmount": 500000,
    "loanType": "HOME",
    "status": "PENDING"
  },
  {
    "id": "66a4f38ef5d60bca64d9d902",
    "loanAmount": 300000,
    "loanType": "CAR",
    "status": "APPROVED"
  }
]
```

---

# 3. Approve Loan

### Endpoint

```http
PUT /api/loans/:loanId/approve
```

### Authorization

```text
ADMIN
```

### Request Body

```json
{
  "remarks": "Loan approved successfully"
}
```

Body is optional.

---

### Response

```json
{
  "id": "66a4f38ef5d60bca64d9d901",
  "status": "APPROVED",
  "approvedBy": "66a4f38ef5d60bca64d9d001",
  "remarks": "Loan approved successfully"
}
```

---

# 4. Reject Loan

### Endpoint

```http
PUT /api/loans/:loanId/reject
```

### Authorization

```text
ADMIN
```

### Request Body

```json
{
  "remarks": "Income verification failed"
}
```

---

### Response

```json
{
  "id": "66a4f38ef5d60bca64d9d901",
  "status": "REJECTED",
  "approvedBy": "66a4f38ef5d60bca64d9d001",
  "remarks": "Income verification failed"
}
```

---

# HTTP Status Codes

| Status | Description           |
| ------ | --------------------- |
| 200    | Success               |
| 201    | Loan Created          |
| 400    | Validation Error      |
| 401    | Unauthorized          |
| 403    | Forbidden             |
| 404    | Loan Not Found        |
| 500    | Internal Server Error |

---

# MongoDB Collection

Collection Name

```text
loans
```

---

# Loan Document Schema

```json
{
  "_id": "ObjectId",

  "customerId": "ObjectId",

  "loanAmount": 500000,

  "loanType": "HOME",

  "tenure": 120,

  "purpose": "Purchase Home",

  "annualIncome": 1200000,

  "status": "PENDING",

  "remarks": "",

  "approvedBy": "ObjectId",

  "createdAt": "2026-07-19T10:00:00Z",

  "updatedAt": "2026-07-19T10:00:00Z"
}
```

---

# Mongoose Model

```javascript
import mongoose from "mongoose";

const loanSchema = new mongoose.Schema(
  {
    customerId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true
    },

    loanAmount: {
      type: Number,
      required: true
    },

    loanType: {
      type: String,
      enum: ["HOME", "PERSONAL", "CAR", "EDUCATION", "BUSINESS"],
      required: true
    },

    tenure: {
      type: Number,
      required: true
    },

    purpose: {
      type: String,
      required: true
    },

    annualIncome: {
      type: Number,
      required: true
    },

    status: {
      type: String,
      enum: ["PENDING", "APPROVED", "REJECTED"],
      default: "PENDING"
    },

    remarks: {
      type: String,
      default: ""
    },

    approvedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User"
    }
  },
  {
    timestamps: true
  }
);

export default mongoose.model("Loan", loanSchema);
```

---

# Suggested Project Structure (MERN)

```text
backend/
│
├── config/
│   └── db.js
│
├── controllers/
│   └── loanController.js
│
├── middleware/
│   ├── authMiddleware.js
│   └── roleMiddleware.js
│
├── models/
│   ├── User.js
│   └── Loan.js
│
├── routes/
│   └── loanRoutes.js
│
├── services/
│   └── loanService.js
│
├── utils/
│
├── app.js
└── server.js
```

---

# Express Routes

```javascript
import express from "express";
import {
  applyLoan,
  getLoans,
  approveLoan,
  rejectLoan
} from "../controllers/loanController.js";
import { verifyToken } from "../middleware/authMiddleware.js";
import { authorize } from "../middleware/roleMiddleware.js";

const router = express.Router();

router.post("/", verifyToken, authorize("CUSTOMER"), applyLoan);

router.get("/", verifyToken, getLoans);

router.put("/:loanId/approve", verifyToken, authorize("ADMIN"), approveLoan);

router.put("/:loanId/reject", verifyToken, authorize("ADMIN"), rejectLoan);

export default router;
```

# MERN Frontend Documentation (React.js)

This frontend documentation maps directly to the Loan Management REST APIs and describes the pages, API calls, components, state management, and folder structure needed in a React application.

---

# Technology Stack

| Technology                             | Purpose           |
| -------------------------------------- | ----------------- |
| React.js                               | UI Framework      |
| React Router DOM                       | Routing           |
| Axios                                  | API Communication |
| Redux Toolkit / Context API            | State Management  |
| Material UI / Bootstrap / Tailwind CSS | UI Components     |
| JWT                                    | Authentication    |
| React Hook Form                        | Form Handling     |
| Yup                                    | Form Validation   |

---

# Frontend Folder Structure

```text
src/
│
├── api/
│   ├── axios.js
│   ├── authApi.js
│   └── loanApi.js
│
├── components/
│   ├── Navbar.jsx
│   ├── Sidebar.jsx
│   ├── LoanCard.jsx
│   ├── LoanTable.jsx
│   ├── StatusBadge.jsx
│   └── ProtectedRoute.jsx
│
├── pages/
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── Dashboard.jsx
│   ├── ApplyLoan.jsx
│   ├── MyLoans.jsx
│   ├── AdminDashboard.jsx
│   ├── LoanDetails.jsx
│   └── NotFound.jsx
│
├── context/
│   └── AuthContext.jsx
│
├── hooks/
│   └── useAuth.js
│
├── layouts/
│   ├── CustomerLayout.jsx
│   └── AdminLayout.jsx
│
├── services/
│   └── tokenService.js
│
├── routes/
│   └── AppRoutes.jsx
│
├── utils/
│
├── App.jsx
└── main.jsx
```

---

# Pages

## 1. Login Page

### Purpose

Authenticate user.

### API

```http
POST /api/auth/login
```

### Fields

| Field    | Type     |
| -------- | -------- |
| email    | String   |
| password | Password |

### Success

Store

* JWT Token
* User
* Role

Navigate

```text
CUSTOMER → Dashboard

ADMIN → Admin Dashboard
```

---

# 2. Dashboard

### Customer Dashboard

Display

* Welcome User
* Loan Summary
* Pending Loans
* Approved Loans
* Rejected Loans

Cards

```
Total Loans

Pending

Approved

Rejected
```

Buttons

```
Apply Loan

View My Loans
```

---

# 3. Apply Loan Page

### API

```http
POST /api/loans
```

### Form Fields

| Field         | Component    |
| ------------- | ------------ |
| Loan Amount   | Number Input |
| Loan Type     | Dropdown     |
| Tenure        | Number       |
| Annual Income | Number       |
| Purpose       | TextArea     |

Loan Types

```
HOME

CAR

PERSONAL

BUSINESS

EDUCATION
```

### Validation

Loan Amount

```
Required

Greater than 0
```

Annual Income

```
Required
```

Purpose

```
Minimum 10 characters
```

### Submit

```javascript
axios.post("/api/loans", formData)
```

### Success Message

```
Loan Applied Successfully
```

Redirect

```
My Loans
```

---

# 4. My Loans

### API

```http
GET /api/loans
```

Display

| Loan Type | Amount | Status | Created Date |
| --------- | ------ | ------ | ------------ |

Status Colors

| Status   | Color  |
| -------- | ------ |
| Pending  | Yellow |
| Approved | Green  |
| Rejected | Red    |

---

# 5. Loan Details Page

Shows

```
Loan Amount

Loan Type

Tenure

Income

Purpose

Status

Remarks

Created Date
```

---

# Admin Dashboard

### API

```
GET /api/loans
```

Display

All Loans

Table

| Customer | Loan Type | Amount | Status | Action |

Buttons

```
Approve

Reject

View
```

---

# Approve Loan

### API

```http
PUT /api/loans/{loanId}/approve
```

Popup

```
Remarks
```

Submit

```javascript
axios.put(`/api/loans/${loanId}/approve`, {
 remarks
});
```

---

# Reject Loan

### API

```http
PUT /api/loans/{loanId}/reject
```

Popup

```
Remarks
```

Submit

```javascript
axios.put(`/api/loans/${loanId}/reject`, {
 remarks
});
```

---

# Axios Configuration

```javascript
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:5000/api"
});

api.interceptors.request.use((config) => {

  const token = localStorage.getItem("token");

  if(token){
      config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default api;
```

---

# Loan API Service

```javascript
import api from "./axios";

export const applyLoan = (data) =>
    api.post("/loans", data);

export const getLoans = () =>
    api.get("/loans");

export const approveLoan = (id, remarks) =>
    api.put(`/loans/${id}/approve`, remarks);

export const rejectLoan = (id, remarks) =>
    api.put(`/loans/${id}/reject`, remarks);
```

---

# React Router

```javascript
<Route path="/dashboard" element={<Dashboard />} />

<Route path="/apply-loan" element={<ApplyLoan />} />

<Route path="/my-loans" element={<MyLoans />} />

<Route path="/admin" element={<AdminDashboard />} />

<Route path="/login" element={<Login />} />
```

---

# Suggested Components

```
Navbar

Sidebar

Footer

LoanCard

LoanTable

LoanStatus

Loader

ErrorMessage

ProtectedRoute

Pagination

SearchBar

ConfirmationDialog
```

---

# State Management

Auth State

```javascript
{
  token:"",
  user:{},
  role:"",
  isAuthenticated:false
}
```

Loan State

```javascript
{
  loans:[],
  loading:false,
  error:null
}
```

---

# Validation Rules

| Field         | Rule                        |
| ------------- | --------------------------- |
| Loan Amount   | Required, > 0               |
| Loan Type     | Required                    |
| Tenure        | Required                    |
| Annual Income | Required                    |
| Purpose       | Required, Min 10 Characters |

---

# User Flow

## Customer Flow

```
Login
      │
      ▼
Dashboard
      │
      ▼
Apply Loan
      │
      ▼
Loan Created
      │
      ▼
My Loans
      │
      ▼
View Loan Status
```

---

## Admin Flow

```
Login
      │
      ▼
Admin Dashboard
      │
      ▼
View Pending Loans
      │
      ├────────► Approve Loan
      │
      └────────► Reject Loan
```

---

# UI Layout Recommendation

### Customer Dashboard

```
----------------------------------------
Navbar
----------------------------------------

Welcome User

-----------------------------
Total Loans
Pending
Approved
Rejected
-----------------------------

[ Apply Loan ]

----------------------------------------

Recent Loans

----------------------------------------
```

---

### Admin Dashboard

```
-----------------------------------------------------

Admin Dashboard

-----------------------------------------------------

Search Loan

-----------------------------------------------------

Customer | Loan | Amount | Status | Action

-----------------------------------------------------

John      Home    5,00,000 Pending  Approve Reject

-----------------------------------------------------
```

---

# API Mapping

| React Page   | API Endpoint                  | Method |
| ------------ | ----------------------------- | ------ |
| Login        | `/api/auth/login`             | POST   |
| Register     | `/api/auth/register`          | POST   |
| Apply Loan   | `/api/loans`                  | POST   |
| My Loans     | `/api/loans`                  | GET    |
| Approve Loan | `/api/loans/{loanId}/approve` | PUT    |
| Reject Loan  | `/api/loans/{loanId}/reject`  | PUT    |



