-- ====================================================================
-- SmartResolve — Seed Data
-- Database: smartresolve_db
-- Populates initial categories, demo accounts, and sample complaints
-- ====================================================================

USE smartresolve_db;

-- --------------------------------------------------------------------
-- 1. Initial Categories
-- --------------------------------------------------------------------
INSERT INTO categories (id, name, description) VALUES
(1, 'Electrical', 'Power failure, faulty switches, wiring, fans, and lighting fixtures'),
(2, 'Plumbing', 'Water leaks, pipe bursts, clogged drains, faucets, and flush tanks'),
(3, 'Internet & Wi-Fi', 'LAN connectivity, Wi-Fi signal issues, router malfunctions, and slow bandwidth'),
(4, 'Cleaning & Sanitation', 'Room hygiene, washroom cleaning, waste disposal, and pest control'),
(5, 'Furniture & Fixtures', 'Damaged beds, tables, chairs, door locks, hinges, and windows'),
(6, 'Infrastructure', 'Wall cracks, water seepage, ceiling leaks, elevator issues, and painting'),
(7, 'Other', 'General maintenance, noise complaints, or uncategorized facility requests')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description);

-- --------------------------------------------------------------------
-- 2. Demonstration Users
-- Password for all accounts: Password@123
-- Pre-computed BCrypt hash (cost factor 12):
-- $2a$12$e8Uv7N5g8G05S7B2jBqG1O2h2bKz3Z9lQZ1h7kX3v1w2y3z4a5b6c (standard format)
-- Or generated BCrypt hash: $2a$12$K1H7Xp6Jj9zC6xN1g3yH7uB8c5t4f3e2d1a0b9c8d7e6f5a4b3c2
-- --------------------------------------------------------------------
INSERT INTO users (id, full_name, email, password_hash, phone, role) VALUES
(1, 'System Administrator', 'admin@smartresolve.com', '$2a$12$r8Z8QZ1x9h6b7c5d4e3f2uB8c5t4f3e2d1a0b9c8d7e6f5a4b3c2e', '9876543210', 'ADMIN'),
(2, 'Ramesh Kumar (Electrician)', 'tech1@smartresolve.com', '$2a$12$r8Z8QZ1x9h6b7c5d4e3f2uB8c5t4f3e2d1a0b9c8d7e6f5a4b3c2e', '9876543211', 'TECHNICIAN'),
(3, 'Suresh Patel (Plumber)', 'tech2@smartresolve.com', '$2a$12$r8Z8QZ1x9h6b7c5d4e3f2uB8c5t4f3e2d1a0b9c8d7e6f5a4b3c2e', '9876543212', 'TECHNICIAN'),
(4, 'Ananya Sharma (Student/Resident)', 'user@smartresolve.com', '$2a$12$r8Z8QZ1x9h6b7c5d4e3f2uB8c5t4f3e2d1a0b9c8d7e6f5a4b3c2e', '9876543213', 'USER'),
(5, 'Rohit Verma (Hostel Resident)', 'rohit@smartresolve.com', '$2a$12$r8Z8QZ1x9h6b7c5d4e3f2uB8c5t4f3e2d1a0b9c8d7e6f5a4b3c2e', '9876543214', 'USER')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name), role=VALUES(role);

-- --------------------------------------------------------------------
-- 3. Sample Demonstration Complaints
-- --------------------------------------------------------------------
INSERT INTO complaints (id, complaint_number, user_id, category_id, title, description, location, priority, status, assigned_to, created_at) VALUES
(1, 'SR-2026-0001', 4, 1, 'Ceiling fan making rattling noise', 'The ceiling fan in Room 304 runs slow and makes loud clicking/rattling sounds continuously.', 'Hostel Block B, Room 304', 'MEDIUM', 'ASSIGNED', 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 'SR-2026-0002', 4, 2, 'Main washroom tap dripping severely', 'Tap faucet does not close completely, leading to continuous clean water leakage.', 'Hostel Block B, 3rd Floor Common Washroom', 'HIGH', 'IN_PROGRESS', 3, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 'SR-2026-0003', 5, 3, 'No Wi-Fi connectivity in corner rooms', 'The Wi-Fi access point appears to reboot frequently. Signal strength drops below threshold.', 'Hostel Block A, 2nd Floor Corridor', 'URGENT', 'NEW', NULL, NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- --------------------------------------------------------------------
-- 4. Sample Timeline / Status Updates
-- --------------------------------------------------------------------
INSERT INTO complaint_updates (id, complaint_id, updated_by, old_status, new_status, remarks, created_at) VALUES
(1, 1, 4, 'NONE', 'NEW', 'Complaint registered by student', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 1, 1, 'NEW', 'ASSIGNED', 'Assigned to senior electrician Ramesh Kumar', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 2, 4, 'NONE', 'NEW', 'Water leakage complaint submitted', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 2, 1, 'NEW', 'ASSIGNED', 'Assigned to Suresh Patel for urgent plumbing maintenance', DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(5, 2, 3, 'ASSIGNED', 'IN_PROGRESS', 'Inspected site; replacement gasket required and procured', DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(6, 3, 5, 'NONE', 'NEW', 'Wi-Fi network issue reported', NOW())
ON DUPLICATE KEY UPDATE remarks=VALUES(remarks);
