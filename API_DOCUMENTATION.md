# Bug Reproduction Recorder API Documentation

**Base URL:** `http://localhost:8080`

**Version:** 1.0  
**Server Port:** 8080

---

## Table of Contents
1. [Authentication APIs](#authentication-apis)
2. [Bug Report APIs](#bug-report-apis)
3. [Bug Step APIs](#bug-step-apis)
4. [Environment APIs](#environment-apis)
5. [Enums & Constants](#enums--constants)
6. [Data Models](#data-models)

---

## Authentication APIs

### 1. Register User

**Endpoint:** `POST /api/auth/register`

**Description:** Register a new user in the system.

**Authentication:** Not required

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "string",
  "password": "string",
  "fullName": "string",
  "role": "QA" | "DEVELOPER" | "ADMIN"
}
```

**Field Details:**
- `email` (string, required): User's email address (must be unique)
- `password` (string, required): User's password
- `fullName` (string, required): User's full name
- `role` (enum, required): User role - must be one of: `QA`, `DEVELOPER`, `ADMIN`

**Response (200 OK):**
```json
{
  "token": "string",
  "email": "string",
  "fullName": "string",
  "role": "string"
}
```

**Example Request:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "fullName": "John Doe",
  "role": "DEVELOPER"
}
```

**Example Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "role": "DEVELOPER"
}
```

---

### 2. Login

**Endpoint:** `POST /api/auth/login`

**Description:** Authenticate user and receive JWT token.

**Authentication:** Not required

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "string",
  "password": "string"
}
```

**Field Details:**
- `email` (string, required): User's email address
- `password` (string, required): User's password

**Response (200 OK):**
```json
{
  "token": "string",
  "email": "string",
  "fullName": "string",
  "role": "string"
}
```

**Example Request:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123!"
}
```

**Example Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "role": "DEVELOPER"
}
```

---

## Bug Report APIs

All bug report endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

### 3. Create Bug Report

**Endpoint:** `POST /api/bugs`

**Description:** Create a new bug report with reproduction steps.

**Authentication:** Required  
**Roles:** `QA`, `DEVELOPER`, `ADMIN`

**Request Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "priority": "string",
  "assignedTo": "string",
  "environment": "string",
  "reproductionSteps": [
    {
      "step": 1,
      "description": "string",
      "screenshot": "string",
      "expected": "string",
      "actual": "string"
    }
  ]
}
```

**Field Details:**
- `title` (string, required): Bug title
- `description` (string, required): Detailed description of the bug
- `priority` (string, required): Priority level - Valid values: `CRITICAL`, `HIGH`, `MEDIUM`, `LOW`
- `assignedTo` (string, optional): Email/username of the developer to assign
- `environment` (string, required): Environment name where bug occurred
- `reproductionSteps` (array, optional): List of steps to reproduce the bug
  - `step` (integer): Step number
  - `description` (string): Description of the step
  - `screenshot` (string): URL to screenshot
  - `expected` (string): Expected result
  - `actual` (string): Actual result

**Response (200 OK):**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "priority": "string",
  "status": "string",
  "assignedTo": "string",
  "environment": "string",
  "reproductionSteps": [
    {
      "step": 1,
      "description": "string",
      "screenshot": "string",
      "expected": "string",
      "actual": "string"
    }
  ],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Example Request:**
```json
{
  "title": "Login button not responding",
  "description": "The login button becomes unresponsive after entering credentials",
  "priority": "HIGH",
  "assignedTo": "developer@example.com",
  "environment": "Production",
  "reproductionSteps": [
    {
      "step": 1,
      "description": "Navigate to login page",
      "screenshot": "https://example.com/screenshots/step1.png",
      "expected": "Login page loads successfully",
      "actual": "Login page loads successfully"
    },
    {
      "step": 2,
      "description": "Enter valid credentials and click login",
      "screenshot": "https://example.com/screenshots/step2.png",
      "expected": "User is logged in and redirected to dashboard",
      "actual": "Button does not respond, no action occurs"
    }
  ]
}
```

---

### 4. Get All Bug Reports

**Endpoint:** `GET /api/bugs`

**Description:** Retrieve all bug reports.

**Authentication:** Required  
**Roles:** `QA`, `DEVELOPER`, `ADMIN`

**Request Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "priority": "string",
    "status": "string",
    "assignedTo": "string",
    "environment": "string",
    "reproductionSteps": [],
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

---

### 5. Get Bug Report by ID

**Endpoint:** `GET /api/bugs/{id}`

**Description:** Retrieve a specific bug report by its ID.

**Authentication:** Required  
**Roles:** `QA`, `DEVELOPER`, `ADMIN`

**Request Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (Long): Bug report ID

**Response (200 OK):**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "priority": "string",
  "status": "string",
  "assignedTo": "string",
  "environment": "string",
  "reproductionSteps": [
    {
      "step": 1,
      "description": "string",
      "screenshot": "string",
      "expected": "string",
      "actual": "string"
    }
  ],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Example:** `GET /api/bugs/1`

---

### 6. Get My Reported Bugs

**Endpoint:** `GET /api/bugs/my-reports`

**Description:** Retrieve all bugs reported by the currently authenticated user.

**Authentication:** Required  
**Roles:** `QA`, `DEVELOPER`, `ADMIN`

**Request Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "priority": "string",
    "status": "string",
    "assignedTo": "string",
    "environment": "string",
    "reproductionSteps": [],
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

---

### 7. Get My Assigned Bugs

**Endpoint:** `GET /api/bugs/my-assigned`

**Description:** Retrieve all bugs assigned to the currently authenticated developer.

**Authentication:** Required  
**Roles:** `DEVELOPER` only

**Request Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": "string",
    "title": "string",
    "description": "string",
    "priority": "string",
    "status": "string",
    "assignedTo": "string",
    "environment": "string",
    "reproductionSteps": [],
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

---

### 8. Update Bug Status

**Endpoint:** `PATCH /api/bugs/{id}/status`

**Description:** Update the status of a bug report.

**Authentication:** Required  
**Roles:** `DEVELOPER`, `ADMIN`

**Request Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (Long): Bug report ID

**Query Parameters:**
- `status` (enum): Bug status - Valid values: `NEW`, `IN_PROGRESS`, `ASSIGNED`, `RESOLVED`, `CLOSED`

**Response (200 OK):**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "priority": "string",
  "status": "string",
  "assignedTo": "string",
  "environment": "string",
  "reproductionSteps": [],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Example:** `PATCH /api/bugs/1/status?status=IN_PROGRESS`

---

### 9. Assign Bug to Developer

**Endpoint:** `PATCH /api/bugs/{bugId}/assign/{developerId}`

**Description:** Assign a bug to a specific developer.

**Authentication:** Required  
**Roles:** `QA`, `ADMIN`

**Request Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `bugId` (Long): Bug report ID
- `developerId` (Long): Developer user ID

**Response (200 OK):**
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "priority": "string",
  "status": "string",
  "assignedTo": "string",
  "environment": "string",
  "reproductionSteps": [],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Example:** `PATCH /api/bugs/1/assign/5`

---

## Bug Step APIs

All bug step endpoints require authentication and are nested under bug reports.

### 10. Add Bug Step

**Endpoint:** `POST /api/bugs/{bugReportId}/steps`

**Description:** Add a reproduction step to a bug report.

**Authentication:** Required  
**Roles:** `QA`, `DEVELOPER`, `ADMIN`

**Request Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**Path Parameters:**
- `bugReportId` (Long): Bug report ID

**Request Body:**
```json
{
  "stepNumber": 1,
  "description": "string",
  "actionType": "CLICK",
  "elementSelector": "string",
  "elementName": "string",
  "inputValue": "string",
  "screenshotUrl": "string",
  "expectedValue": "string",
  "actualValue": "string",
  "duration": 1000,
  "xCoordinate": 100.5,
  "yCoordinate": 200.5
}
```

**Field Details:**
- `stepNumber` (integer, required): Step sequence number
- `description` (string, required): Description of the step
- `actionType` (enum, required): Type of action - Valid values: `CLICK`, `INPUT`, `SCROLL`, `NAVIGATE`, `WAIT`, `SCREENSHOT`, `HOVER`, `DOUBLE_CLICK`, `RIGHT_CLICK`, `KEY_PRESS`, `FORM_SUBMIT`, `PAGE_REFRESH`, `BACK_NAVIGATION`
- `elementSelector` (string, optional): CSS selector or XPath for the element
- `elementName` (string, optional): Human-readable element name
- `inputValue` (string, optional): Value entered in input fields
- `screenshotUrl` (string, optional): URL to screenshot
- `expectedValue` (string, optional): Expected result
- `actualValue` (string, optional): Actual result
- `duration` (long, required): Duration in milliseconds
- `xCoordinate` (double, optional): X coordinate for click actions
- `yCoordinate` (double, optional): Y coordinate for click actions

**Response (200 OK):**
```json
{
  "id": 1,
  "stepNumber": 1,
  "description": "string",
  "actionType": "CLICK",
  "elementSelector": "string",
  "elementName": "string",
  "inputValue": "string",
  "screenshotUrl": "string",
  "expectedValue": "string",
  "actualValue": "string",
  "duration": 1000,
  "xCoordinate": 100.5,
  "yCoordinate": 200.5,
  "timestamp": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Example Request:**
```json
{
  "stepNumber": 1,
  "description": "Click on the login button",
  "actionType": "CLICK",
  "elementSelector": "#login-button",
  "elementName": "Login Button",
  "inputValue": null,
  "screenshotUrl": "https://example.com/screenshots/click-login.png",
  "expectedValue": "Navigate to dashboard",
  "actualValue": "Nothing happens",
  "duration": 1500,
  "xCoordinate": 150.5,
  "yCoordinate": 300.0
}
```

---

### 11. Get Bug Steps

**Endpoint:** `GET /api/bugs/{bugReportId}/steps`

**Description:** Retrieve all reproduction steps for a bug report.

**Authentication:** Required  
**Roles:** `QA`, `DEVELOPER`, `ADMIN`

**Request Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `bugReportId` (Long): Bug report ID

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "stepNumber": 1,
    "description": "string",
    "actionType": "CLICK",
    "elementSelector": "string",
    "elementName": "string",
    "inputValue": "string",
    "screenshotUrl": "string",
    "expectedValue": "string",
    "actualValue": "string",
    "duration": 1000,
    "xCoordinate": 100.5,
    "yCoordinate": 200.5,
    "timestamp": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

**Example:** `GET /api/bugs/1/steps`

---

### 12. Delete Bug Step

**Endpoint:** `DELETE /api/bugs/{bugReportId}/steps/{stepId}`

**Description:** Delete a specific reproduction step.

**Authentication:** Required  
**Roles:** `QA`, `ADMIN`

**Request Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `bugReportId` (Long): Bug report ID (not used in controller but part of path)
- `stepId` (Long): Bug step ID to delete

**Response (204 No Content):**
No response body

**Example:** `DELETE /api/bugs/1/steps/5`

---

## Environment APIs

All environment endpoints require authentication.

### 13. Create Environment

**Endpoint:** `POST /api/environments`

**Description:** Create a new testing environment configuration.

**Authentication:** Required  
**Roles:** `ADMIN` only

**Request Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "name": "string",
  "description": "string",
  "baseUrl": "string",
  "browserName": "string",
  "browserVersion": "string",
  "osName": "string",
  "osVersion": "string",
  "deviceType": "string"
}
```

**Field Details:**
- `name` (string, required): Environment name (must be unique) - e.g., "Development", "Staging", "Production", "QA"
- `description` (string, optional): Detailed description of the environment
- `baseUrl` (string, required): Base URL of the environment
- `browserName` (string, required): Browser name - e.g., "Chrome", "Firefox", "Safari", "Edge"
- `browserVersion` (string, required): Browser version
- `osName` (string, required): Operating system - e.g., "Windows", "macOS", "Linux"
- `osVersion` (string, required): OS version
- `deviceType` (string, required): Device type - e.g., "DESKTOP", "TABLET", "MOBILE"

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "baseUrl": "string",
  "browserName": "string",
  "browserVersion": "string",
  "osName": "string",
  "osVersion": "string",
  "deviceType": "string",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Example Request:**
```json
{
  "name": "Production",
  "description": "Production environment for live users",
  "baseUrl": "https://app.example.com",
  "browserName": "Chrome",
  "browserVersion": "120.0.0",
  "osName": "Windows",
  "osVersion": "11",
  "deviceType": "DESKTOP"
}
```

---

### 14. Get All Environments

**Endpoint:** `GET /api/environments`

**Description:** Retrieve all environment configurations.

**Authentication:** Required  
**Roles:** `QA`, `DEVELOPER`, `ADMIN`

**Request Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "string",
    "description": "string",
    "baseUrl": "string",
    "browserName": "string",
    "browserVersion": "string",
    "osName": "string",
    "osVersion": "string",
    "deviceType": "string",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

---

### 15. Get Active Environments

**Endpoint:** `GET /api/environments/active`

**Description:** Retrieve only active environment configurations.

**Authentication:** Required  
**Roles:** `QA`, `DEVELOPER`, `ADMIN`

**Request Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "string",
    "description": "string",
    "baseUrl": "string",
    "browserName": "string",
    "browserVersion": "string",
    "osName": "string",
    "osVersion": "string",
    "deviceType": "string",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

---

### 16. Get Environment by ID

**Endpoint:** `GET /api/environments/{id}`

**Description:** Retrieve a specific environment by its ID.

**Authentication:** Required  
**Roles:** `QA`, `DEVELOPER`, `ADMIN`

**Request Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (Long): Environment ID

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "baseUrl": "string",
  "browserName": "string",
  "browserVersion": "string",
  "osName": "string",
  "osVersion": "string",
  "deviceType": "string",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Example:** `GET /api/environments/1`

---

### 17. Update Environment

**Endpoint:** `PUT /api/environments/{id}`

**Description:** Update an existing environment configuration.

**Authentication:** Required  
**Roles:** `ADMIN` only

**Request Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (Long): Environment ID

**Request Body:**
```json
{
  "name": "string",
  "description": "string",
  "baseUrl": "string",
  "browserName": "string",
  "browserVersion": "string",
  "osName": "string",
  "osVersion": "string",
  "deviceType": "string"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "baseUrl": "string",
  "browserName": "string",
  "browserVersion": "string",
  "osName": "string",
  "osVersion": "string",
  "deviceType": "string",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Example:** `PUT /api/environments/1`

---

### 18. Deactivate Environment

**Endpoint:** `DELETE /api/environments/{id}`

**Description:** Deactivate an environment (soft delete).

**Authentication:** Required  
**Roles:** `ADMIN` only

**Request Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (Long): Environment ID

**Response (204 No Content):**
No response body

**Example:** `DELETE /api/environments/1`

---

## Enums & Constants

### User Roles
```java
enum UserRole {
    QA,
    DEVELOPER,
    ADMIN
}
```

### Bug Severity/Priority
```java
enum BugSeverity {
    CRITICAL,  // System-breaking issues
    HIGH,      // Major functionality issues
    MEDIUM,    // Moderate issues
    LOW        // Minor issues
}
```

### Bug Status
```java
enum BugStatus {
    NEW,          // Newly reported
    IN_PROGRESS,  // Being worked on
    ASSIGNED,     // Assigned to developer
    RESOLVED,     // Fixed
    CLOSED        // Verified and closed
}
```

### Action Types (Bug Steps)
```java
enum ActionType {
    CLICK,
    INPUT,
    SCROLL,
    NAVIGATE,
    WAIT,
    SCREENSHOT,
    HOVER,
    DOUBLE_CLICK,
    RIGHT_CLICK,
    KEY_PRESS,
    FORM_SUBMIT,
    PAGE_REFRESH,
    BACK_NAVIGATION
}
```

### Assignment Status
```java
enum AssignmentStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COMPLETED
}
```

---

## Data Models

### User Entity
```java
{
  "id": Long,
  "email": String (unique),
  "password": String (encrypted),
  "fullName": String,
  "role": UserRole,
  "isActive": Boolean,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### Bug Report Entity
```java
{
  "id": Long,
  "title": String,
  "description": String,
  "severity": BugSeverity,
  "status": BugStatus,
  "reportedBy": User,
  "assignedTo": User,
  "environment": Environment,
  "steps": List<BugStep>,
  "expectedResult": String,
  "actualResult": String,
  "screenshotUrl": String,
  "videoUrl": String,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime,
  "resolvedAt": LocalDateTime
}
```

### Bug Step Entity
```java
{
  "id": Long,
  "bugReport": BugReport,
  "stepNumber": Integer,
  "description": String,
  "actionType": ActionType,
  "elementSelector": String,
  "elementName": String,
  "inputValue": String,
  "screenshotUrl": String,
  "expectedValue": String,
  "actualValue": String,
  "duration": Long (milliseconds),
  "xCoordinate": Double,
  "yCoordinate": Double,
  "timestamp": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### Environment Entity
```java
{
  "id": Long,
  "name": String (unique),
  "description": String,
  "baseUrl": String,
  "browserName": String,
  "browserVersion": String,
  "osName": String,
  "osVersion": String,
  "deviceType": String,
  "isActive": Boolean,
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### Bug Assignment Entity
```java
{
  "id": Long,
  "bugReport": BugReport,
  "developer": User,
  "assignedBy": User,
  "notes": String,
  "status": AssignmentStatus,
  "createdAt": LocalDateTime,
  "acceptedAt": LocalDateTime,
  "rejectedAt": LocalDateTime,
  "completedAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

---

## Authentication Flow

1. **Register:** Create a new user account with role
2. **Login:** Authenticate and receive JWT token
3. **Protected Requests:** Include JWT token in Authorization header for all protected endpoints

**JWT Token Format:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**JWT Expiration:** 86400000 milliseconds (24 hours)

---

## Error Responses

All API endpoints may return the following error responses:

### 400 Bad Request
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error message",
  "path": "/api/bugs"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "path": "/api/bugs"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "path": "/api/bugs"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/bugs/999"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/bugs"
}
```

---

## Notes

1. **CORS:** All endpoints have CORS enabled with `@CrossOrigin(origins = "*")`
2. **Database:** PostgreSQL on localhost:5432 (database: bugtracker)
3. **Date Format:** ISO 8601 format (`yyyy-MM-dd'T'HH:mm:ss`)
4. **ID Types:** All entity IDs are Long integers
5. **Soft Deletes:** Environment deletion is a soft delete (sets isActive to false)
6. **Role-Based Access:** Most endpoints require specific roles as noted in each endpoint description

---

## Quick Reference - Endpoint Summary

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Bug Reports
- `POST /api/bugs` - Create bug report
- `GET /api/bugs` - Get all bugs
- `GET /api/bugs/{id}` - Get bug by ID
- `GET /api/bugs/my-reports` - Get my reported bugs
- `GET /api/bugs/my-assigned` - Get bugs assigned to me
- `PATCH /api/bugs/{id}/status` - Update bug status
- `PATCH /api/bugs/{bugId}/assign/{developerId}` - Assign bug

### Bug Steps
- `POST /api/bugs/{bugReportId}/steps` - Add step
- `GET /api/bugs/{bugReportId}/steps` - Get steps
- `DELETE /api/bugs/{bugReportId}/steps/{stepId}` - Delete step

### Environments
- `POST /api/environments` - Create environment
- `GET /api/environments` - Get all environments
- `GET /api/environments/active` - Get active environments
- `GET /api/environments/{id}` - Get environment by ID
- `PUT /api/environments/{id}` - Update environment
- `DELETE /api/environments/{id}` - Deactivate environment

---

**Document Version:** 1.0  
**Last Updated:** March 3, 2026  
**Contact:** Support Team
