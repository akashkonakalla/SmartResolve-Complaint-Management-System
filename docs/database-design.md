# SmartResolve — Database Design

## Database Name
`smartresolve_db`

## Entity-Relationship Summary

```
users (1) ──────< complaints (N)        [user submits complaints]
users (1) ──────< complaints (N)        [technician assigned_to]
categories (1) ─< complaints (N)        [complaint belongs to category]
complaints (1) ─< complaint_updates (N) [every status change logged]
complaints (1) ──< feedback (1)         [one feedback per complaint]
users (1) ──────< feedback (N)          [user submits feedback]
users (1) ──────< complaint_updates (N) [who made the update]
```

## Table Descriptions

### `users`
Stores all application users (USER, ADMIN, TECHNICIAN roles).
- `role` column is ENUM restricted to three values
- `email` has UNIQUE constraint
- `password_hash` stores BCrypt output (never plain text)

### `categories`
Lookup table for complaint categories.
- Seeded with: Electrical, Plumbing, Internet/Wi-Fi, Cleaning, Furniture, Infrastructure, Other
- Simple name + description; administrators can extend via direct DB insert

### `complaints`
Core table representing a service request.
- `complaint_number` — unique human-readable reference (e.g., SR-2026-0001)
- `priority` — LOW / MEDIUM / HIGH / URGENT
- `status` — NEW / ASSIGNED / IN_PROGRESS / RESOLVED / CLOSED
- `assigned_to` — FK to users (technician); NULL until assigned
- `image_path` — relative path to uploaded image (stored outside webroot)
- Timestamps: `created_at`, `updated_at`, `resolved_at`

### `complaint_updates`
Audit trail for every complaint status change.
- Records `old_status` and `new_status`
- Records who made the change (`updated_by` FK to users)
- Records free-text `remarks`
- Used to render the complaint timeline in the UI

### `feedback`
Post-resolution satisfaction ratings.
- One record per complaint (UNIQUE constraint on `complaint_id`)
- `rating` 1–5 (CHECK constraint)
- `comments` optional free text

## Indexes

| Table | Column | Type | Reason |
|-------|--------|------|--------|
| users | email | UNIQUE | Login lookup |
| complaints | user_id | INDEX | "My complaints" queries |
| complaints | assigned_to | INDEX | Technician dashboard |
| complaints | status | INDEX | Filter by status |
| complaints | complaint_number | UNIQUE | Reference number lookup |
| complaint_updates | complaint_id | INDEX | Timeline queries |

## Transactions

Multi-step operations that must succeed or fail atomically:
1. **Technician assignment** — UPDATE complaints + INSERT complaint_updates
2. **Status transitions** — UPDATE complaints + INSERT complaint_updates
3. **Complaint creation** — INSERT complaints (auto-generate complaint_number)

Pattern used:
```java
connection.setAutoCommit(false);
try {
    // step 1
    // step 2
    connection.commit();
} catch (SQLException e) {
    connection.rollback();
    throw e;
}
```

## Naming Conventions

- Tables: `snake_case`, plural
- Columns: `snake_case`
- Primary keys: always `id` (INT AUTO_INCREMENT)
- Foreign keys: `{referenced_table_singular}_id` (e.g., `user_id`, `category_id`)
- Timestamps: `created_at`, `updated_at`, `resolved_at` (DATETIME DEFAULT CURRENT_TIMESTAMP)
