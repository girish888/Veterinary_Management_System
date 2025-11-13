-- Check all users and their email addresses
SELECT 
    u.id,
    u.username,
    u.email,
    u.full_name,
    u.role,
    o.id as owner_id,
    o.name as owner_name
FROM users u
LEFT JOIN owners o ON u.id = o.user_id
ORDER BY u.id;

-- Check specific user by email
SELECT 
    u.id,
    u.username,
    u.email,
    u.full_name,
    u.role,
    o.id as owner_id,
    o.name as owner_name
FROM users u
LEFT JOIN owners o ON u.id = o.user_id
WHERE u.email LIKE '%liyakhath%' OR u.email LIKE '%deva%' OR u.email LIKE '%mahi%';

-- Check appointments and their associated users
SELECT 
    a.id as appointment_id,
    a.owner_id,
    a.veterinarian_id,
    a.pet_id,
    a.date_time,
    a.status,
    owner_user.email as owner_email,
    owner_user.full_name as owner_name,
    vet_user.email as vet_email,
    vet_user.full_name as vet_name
FROM appointments a
LEFT JOIN users owner_user ON a.owner_id = owner_user.id
LEFT JOIN users vet_user ON a.veterinarian_id = vet_user.id
ORDER BY a.id DESC
LIMIT 10;
