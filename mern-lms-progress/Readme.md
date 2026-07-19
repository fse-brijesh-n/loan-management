# Loan Management REST API Documentation (MERN Stack + MongoDB)

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

This design mirrors the behavior of your Spring Boot `LoanController`, while following common MERN conventions with Express routing, JWT-based authentication, Mongoose models, and role-based authorization.
