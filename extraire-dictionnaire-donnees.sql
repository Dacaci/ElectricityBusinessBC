-- Script SQL pour extraire le dictionnaire de données complet
-- Modèle Physique de Données (MPD) - Electricity Business

-- ============================================================
-- 1. LISTE DES TABLES
-- ============================================================
SELECT 
    '=== LISTE DES TABLES ===' as section;

SELECT 
    table_name as "Nom de la table",
    (SELECT COUNT(*) 
     FROM information_schema.columns 
     WHERE table_name = t.table_name 
     AND table_schema = 'public') as "Nombre de colonnes"
FROM information_schema.tables t
WHERE table_schema = 'public' 
AND table_type = 'BASE TABLE'
AND table_name != 'flyway_schema_history'
ORDER BY table_name;

-- ============================================================
-- 2. DICTIONNAIRE DE DONNÉES COMPLET
-- ============================================================
SELECT 
    '=== DICTIONNAIRE DE DONNÉES ===' as section;

SELECT 
    t.table_name as "Table",
    c.column_name as "Colonne",
    c.data_type as "Type de données",
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END as "Type complet",
    CASE WHEN c.is_nullable = 'NO' THEN 'NON NULL' ELSE 'NULL' END as "Nullable",
    COALESCE(c.column_default, '') as "Valeur par défaut",
    CASE 
        WHEN pk.column_name IS NOT NULL THEN 'PK'
        WHEN fk.column_name IS NOT NULL THEN 'FK → ' || fk.foreign_table_name || '(' || fk.foreign_column_name || ')'
        ELSE ''
    END as "Contraintes"
FROM information_schema.tables t
JOIN information_schema.columns c ON t.table_name = c.table_name
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku 
        ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY'
    AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT 
        ku.table_name,
        ku.column_name,
        ccu.table_name as foreign_table_name,
        ccu.column_name as foreign_column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku 
        ON tc.constraint_name = ku.constraint_name
    JOIN information_schema.constraint_column_usage ccu 
        ON tc.constraint_name = ccu.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY'
    AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE t.table_schema = 'public'
AND t.table_type = 'BASE TABLE'
AND t.table_name != 'flyway_schema_history'
ORDER BY t.table_name, c.ordinal_position;

-- ============================================================
-- 3. CONTRAINTES (CLÉS PRIMAIRES, ÉTRANGÈRES, UNIQUES)
-- ============================================================
SELECT 
    '=== CONTRAINTES ===' as section;

-- Clés primaires
SELECT 
    'CLÉS PRIMAIRES' as type_contrainte,
    tc.table_name as "Table",
    ku.column_name as "Colonne(s)"
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage ku 
    ON tc.constraint_name = ku.constraint_name
WHERE tc.constraint_type = 'PRIMARY KEY'
AND tc.table_schema = 'public'
AND tc.table_name != 'flyway_schema_history'
ORDER BY tc.table_name;

-- Clés étrangères
SELECT 
    'CLÉS ÉTRANGÈRES' as type_contrainte,
    tc.table_name as "Table",
    ku.column_name as "Colonne",
    ccu.table_name as "Table référencée",
    ccu.column_name as "Colonne référencée"
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage ku 
    ON tc.constraint_name = ku.constraint_name
JOIN information_schema.constraint_column_usage ccu 
    ON tc.constraint_name = ccu.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY'
AND tc.table_schema = 'public'
AND tc.table_name != 'flyway_schema_history'
ORDER BY tc.table_name, ku.column_name;

-- Contraintes UNIQUE
SELECT 
    'CONTRAINTES UNIQUE' as type_contrainte,
    tc.table_name as "Table",
    ku.column_name as "Colonne(s)"
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage ku 
    ON tc.constraint_name = ku.constraint_name
WHERE tc.constraint_type = 'UNIQUE'
AND tc.table_schema = 'public'
AND tc.table_name != 'flyway_schema_history'
ORDER BY tc.table_name;

-- ============================================================
-- 4. INDEX
-- ============================================================
SELECT 
    '=== INDEX ===' as section;

SELECT 
    schemaname as "Schéma",
    tablename as "Table",
    indexname as "Nom de l'index",
    indexdef as "Définition"
FROM pg_indexes
WHERE schemaname = 'public'
AND tablename != 'flyway_schema_history'
ORDER BY tablename, indexname;

-- ============================================================
-- 5. TRIGGERS
-- ============================================================
SELECT 
    '=== TRIGGERS ===' as section;

SELECT 
    trigger_name as "Nom du trigger",
    event_object_table as "Table",
    action_statement as "Action",
    action_timing as "Timing",
    event_manipulation as "Événement"
FROM information_schema.triggers
WHERE trigger_schema = 'public'
ORDER BY event_object_table, trigger_name;

-- ============================================================
-- 6. FONCTIONS
-- ============================================================
SELECT 
    '=== FONCTIONS ===' as section;

SELECT 
    routine_name as "Nom de la fonction",
    routine_type as "Type",
    data_type as "Type de retour"
FROM information_schema.routines
WHERE routine_schema = 'public'
ORDER BY routine_name;

-- ============================================================
-- 7. STATISTIQUES (Nombre d'enregistrements)
-- ============================================================
SELECT 
    '=== STATISTIQUES ===' as section;

SELECT 
    'users' as table_name, COUNT(*) as nombre_lignes FROM users
UNION ALL SELECT 'email_verification_codes', COUNT(*) FROM email_verification_codes
UNION ALL SELECT 'locations', COUNT(*) FROM locations
UNION ALL SELECT 'stations', COUNT(*) FROM stations
UNION ALL SELECT 'reservations', COUNT(*) FROM reservations
UNION ALL SELECT 'vehicles', COUNT(*) FROM vehicles
UNION ALL SELECT 'medias', COUNT(*) FROM medias
UNION ALL SELECT 'plug_types', COUNT(*) FROM plug_types
UNION ALL SELECT 'user_vehicle', COUNT(*) FROM user_vehicle
UNION ALL SELECT 'vehicle_plug_compatibility', COUNT(*) FROM vehicle_plug_compatibility
UNION ALL SELECT 'station_plug_type', COUNT(*) FROM station_plug_type
ORDER BY table_name;




