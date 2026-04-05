-- Delete Rishi user from database
-- Run this in your PostgreSQL database client or pgAdmin

-- First, check if Rishi user exists
SELECT * FROM users WHERE email LIKE '%rishi%' OR full_name LIKE '%Rishi%';

-- Delete Rishi user(s)
-- This will delete all users with 'rishi' in their email or 'Rishi' in their name
DELETE FROM users WHERE email LIKE '%rishi%' OR full_name LIKE '%Rishi%';

-- Verify deletion
SELECT * FROM users;
