-- ============================================================
-- SCRIPT SQL POUR GÉNÉRER LE DICTIONNAIRE DE DONNÉES (MPD)
-- Electricity Business - Base de données PostgreSQL
-- ============================================================
-- 
-- INSTRUCTIONS :
-- 1. Exécutez ce script dans VS Code (extension Database)
-- 2. Copiez tous les résultats
-- 3. Collez dans un fichier .md
-- OU
-- 3. Exportez les résultats au format CSV/Markdown
-- ============================================================

-- ============================================================
-- 1. EN-TÊTE DU DICTIONNAIRE
-- ============================================================
SELECT 
    '# 📊 Dictionnaire de Données - Modèle Physique de Données (MPD)' as ligne
UNION ALL
SELECT '## Electricity Business - Base de données PostgreSQL'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Date de génération** : ' || CURRENT_DATE::text || ' ' || CURRENT_TIME::text
UNION ALL
SELECT '**Base de données** : eb'
UNION ALL
SELECT '**Schéma** : public'
UNION ALL
SELECT ''
UNION ALL
SELECT '---'
UNION ALL
SELECT '';

-- ============================================================
-- 2. LISTE DES TABLES
-- ============================================================
SELECT 
    '## 📋 Liste des Tables' as ligne
UNION ALL
SELECT ''
UNION ALL
SELECT '| Table | Nombre de colonnes | Nombre d''enregistrements |'
UNION ALL
SELECT '|-------|-------------------|--------------------------|'
UNION ALL
SELECT 
    '| `' || t.table_name || '` | ' || 
    (SELECT COUNT(*) FROM information_schema.columns WHERE table_name = t.table_name AND table_schema = 'public') || ' | ' ||
    CASE t.table_name
        WHEN 'users' THEN (SELECT COUNT(*)::text FROM users)
        WHEN 'email_verification_codes' THEN (SELECT COUNT(*)::text FROM email_verification_codes)
        WHEN 'locations' THEN (SELECT COUNT(*)::text FROM locations)
        WHEN 'stations' THEN (SELECT COUNT(*)::text FROM stations)
        WHEN 'reservations' THEN (SELECT COUNT(*)::text FROM reservations)
        WHEN 'medias' THEN (SELECT COUNT(*)::text FROM medias)
        ELSE '0'
    END || ' |'
FROM information_schema.tables t
WHERE t.table_schema = 'public'
AND t.table_type = 'BASE TABLE'
AND t.table_name != 'flyway_schema_history'
ORDER BY t.table_name;

-- ============================================================
-- 3. DICTIONNAIRE DÉTAILLÉ PAR TABLE
-- ============================================================
-- Pour chaque table, générer la section détaillée

-- Table: users
SELECT 
    '' as ligne
UNION ALL
SELECT '---'
UNION ALL
SELECT ''
UNION ALL
SELECT '## 📋 Table : `users`'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Description** : Table des utilisateurs (conducteurs et propriétaires de bornes)'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Nombre d''enregistrements** : ' || (SELECT COUNT(*)::text FROM users)
UNION ALL
SELECT ''
UNION ALL
SELECT '| Colonne | Type | Nullable | Valeur par défaut | Contraintes |'
UNION ALL
SELECT '|---------|------|----------|-------------------|-------------|'
UNION ALL
SELECT 
    '| `' || c.column_name || '` | `' ||
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END || '` | ' ||
    CASE WHEN c.is_nullable = 'NO' THEN 'NON NULL' ELSE 'NULL' END || ' | ' ||
    COALESCE(REPLACE(REPLACE(c.column_default, '::regclass', ''), '::character varying', ''), '-') || ' | ' ||
    CASE 
        WHEN pk.column_name IS NOT NULL THEN '**PK**'
        WHEN fk.column_name IS NOT NULL THEN '**FK** → `' || fk.ref_table || '.' || fk.ref_column || '`'
        ELSE '-'
    END || ' |'
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT 
        ku.table_name, ku.column_name,
        ccu.table_name as ref_table, ccu.column_name as ref_column
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    JOIN information_schema.constraint_column_usage ccu ON tc.constraint_name = ccu.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'users'
ORDER BY c.ordinal_position;

-- Table: email_verification_codes
SELECT 
    '' as ligne
UNION ALL
SELECT '---'
UNION ALL
SELECT ''
UNION ALL
SELECT '## 📋 Table : `email_verification_codes`'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Description** : Codes de vérification email pour l''inscription'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Nombre d''enregistrements** : ' || (SELECT COUNT(*)::text FROM email_verification_codes)
UNION ALL
SELECT ''
UNION ALL
SELECT '| Colonne | Type | Nullable | Valeur par défaut | Contraintes |'
UNION ALL
SELECT '|---------|------|----------|-------------------|-------------|'
UNION ALL
SELECT 
    '| `' || c.column_name || '` | `' ||
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END || '` | ' ||
    CASE WHEN c.is_nullable = 'NO' THEN 'NON NULL' ELSE 'NULL' END || ' | ' ||
    COALESCE(REPLACE(REPLACE(c.column_default, '::regclass', ''), '::character varying', ''), '-') || ' | ' ||
    CASE 
        WHEN pk.column_name IS NOT NULL THEN '**PK**'
        WHEN fk.column_name IS NOT NULL THEN '**FK** → `' || fk.ref_table || '.' || fk.ref_column || '`'
        ELSE '-'
    END || ' |'
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT 
        ku.table_name, ku.column_name,
        ccu.table_name as ref_table, ccu.column_name as ref_column
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    JOIN information_schema.constraint_column_usage ccu ON tc.constraint_name = ccu.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'email_verification_codes'
ORDER BY c.ordinal_position;

-- Table: locations
SELECT 
    '' as ligne
UNION ALL
SELECT '---'
UNION ALL
SELECT ''
UNION ALL
SELECT '## 📋 Table : `locations`'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Description** : Lieux de recharge (adresses où se trouvent les bornes)'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Nombre d''enregistrements** : ' || (SELECT COUNT(*)::text FROM locations)
UNION ALL
SELECT ''
UNION ALL
SELECT '| Colonne | Type | Nullable | Valeur par défaut | Contraintes |'
UNION ALL
SELECT '|---------|------|----------|-------------------|-------------|'
UNION ALL
SELECT 
    '| `' || c.column_name || '` | `' ||
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END || '` | ' ||
    CASE WHEN c.is_nullable = 'NO' THEN 'NON NULL' ELSE 'NULL' END || ' | ' ||
    COALESCE(REPLACE(REPLACE(c.column_default, '::regclass', ''), '::character varying', ''), '-') || ' | ' ||
    CASE 
        WHEN pk.column_name IS NOT NULL THEN '**PK**'
        WHEN fk.column_name IS NOT NULL THEN '**FK** → `' || fk.ref_table || '.' || fk.ref_column || '`'
        ELSE '-'
    END || ' |'
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT 
        ku.table_name, ku.column_name,
        ccu.table_name as ref_table, ccu.column_name as ref_column
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    JOIN information_schema.constraint_column_usage ccu ON tc.constraint_name = ccu.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'locations'
ORDER BY c.ordinal_position;

-- Table: stations
SELECT 
    '' as ligne
UNION ALL
SELECT '---'
UNION ALL
SELECT ''
UNION ALL
SELECT '## 📋 Table : `stations`'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Description** : Bornes de recharge'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Nombre d''enregistrements** : ' || (SELECT COUNT(*)::text FROM stations)
UNION ALL
SELECT ''
UNION ALL
SELECT '| Colonne | Type | Nullable | Valeur par défaut | Contraintes |'
UNION ALL
SELECT '|---------|------|----------|-------------------|-------------|'
UNION ALL
SELECT 
    '| `' || c.column_name || '` | `' ||
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END || '` | ' ||
    CASE WHEN c.is_nullable = 'NO' THEN 'NON NULL' ELSE 'NULL' END || ' | ' ||
    COALESCE(REPLACE(REPLACE(c.column_default, '::regclass', ''), '::character varying', ''), '-') || ' | ' ||
    CASE 
        WHEN pk.column_name IS NOT NULL THEN '**PK**'
        WHEN fk.column_name IS NOT NULL THEN '**FK** → `' || fk.ref_table || '.' || fk.ref_column || '`'
        ELSE '-'
    END || ' |'
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT 
        ku.table_name, ku.column_name,
        ccu.table_name as ref_table, ccu.column_name as ref_column
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    JOIN information_schema.constraint_column_usage ccu ON tc.constraint_name = ccu.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'stations'
ORDER BY c.ordinal_position;

-- Table: reservations
SELECT 
    '' as ligne
UNION ALL
SELECT '---'
UNION ALL
SELECT ''
UNION ALL
SELECT '## 📋 Table : `reservations`'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Description** : Réservations de bornes par les conducteurs'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Nombre d''enregistrements** : ' || (SELECT COUNT(*)::text FROM reservations)
UNION ALL
SELECT ''
UNION ALL
SELECT '| Colonne | Type | Nullable | Valeur par défaut | Contraintes |'
UNION ALL
SELECT '|---------|------|----------|-------------------|-------------|'
UNION ALL
SELECT 
    '| `' || c.column_name || '` | `' ||
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END || '` | ' ||
    CASE WHEN c.is_nullable = 'NO' THEN 'NON NULL' ELSE 'NULL' END || ' | ' ||
    COALESCE(REPLACE(REPLACE(c.column_default, '::regclass', ''), '::character varying', ''), '-') || ' | ' ||
    CASE 
        WHEN pk.column_name IS NOT NULL THEN '**PK**'
        WHEN fk.column_name IS NOT NULL THEN '**FK** → `' || fk.ref_table || '.' || fk.ref_column || '`'
        ELSE '-'
    END || ' |'
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT 
        ku.table_name, ku.column_name,
        ccu.table_name as ref_table, ccu.column_name as ref_column
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    JOIN information_schema.constraint_column_usage ccu ON tc.constraint_name = ccu.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'reservations'
ORDER BY c.ordinal_position;

-- Table: medias
SELECT 
    '' as ligne
UNION ALL
SELECT '---'
UNION ALL
SELECT ''
UNION ALL
SELECT '## 📋 Table : `medias`'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Description** : Photos et vidéos des bornes de recharge'
UNION ALL
SELECT ''
UNION ALL
SELECT '**Nombre d''enregistrements** : ' || (SELECT COUNT(*)::text FROM medias)
UNION ALL
SELECT ''
UNION ALL
SELECT '| Colonne | Type | Nullable | Valeur par défaut | Contraintes |'
UNION ALL
SELECT '|---------|------|----------|-------------------|-------------|'
UNION ALL
SELECT 
    '| `' || c.column_name || '` | `' ||
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END || '` | ' ||
    CASE WHEN c.is_nullable = 'NO' THEN 'NON NULL' ELSE 'NULL' END || ' | ' ||
    COALESCE(REPLACE(REPLACE(c.column_default, '::regclass', ''), '::character varying', ''), '-') || ' | ' ||
    CASE 
        WHEN pk.column_name IS NOT NULL THEN '**PK**'
        WHEN fk.column_name IS NOT NULL THEN '**FK** → `' || fk.ref_table || '.' || fk.ref_column || '`'
        ELSE '-'
    END || ' |'
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT 
        ku.table_name, ku.column_name,
        ccu.table_name as ref_table, ccu.column_name as ref_column
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    JOIN information_schema.constraint_column_usage ccu ON tc.constraint_name = ccu.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'medias'
ORDER BY c.ordinal_position;

-- ============================================================
-- 4. RÉSUMÉ DES CONTRAINTES
-- ============================================================
SELECT 
    '' as ligne
UNION ALL
SELECT '---'
UNION ALL
SELECT ''
UNION ALL
SELECT '## 🔗 Relations et Contraintes'
UNION ALL
SELECT ''
UNION ALL
SELECT '### Clés primaires'
UNION ALL
SELECT ''
UNION ALL
SELECT '| Table | Colonne(s) |'
UNION ALL
SELECT '|-------|-----------|'
UNION ALL
SELECT 
    '| `' || tc.table_name || '` | `' || 
    string_agg(ku.column_name, ', ' ORDER BY ku.ordinal_position) || '` |'
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
WHERE tc.constraint_type = 'PRIMARY KEY'
AND tc.table_schema = 'public'
AND tc.table_name != 'flyway_schema_history'
GROUP BY tc.table_name
ORDER BY tc.table_name;

SELECT 
    '' as ligne
UNION ALL
SELECT '### Clés étrangères'
UNION ALL
SELECT ''
UNION ALL
SELECT '| Table | Colonne | Table référencée | Colonne référencée |'
UNION ALL
SELECT '|-------|---------|------------------|-------------------|'
UNION ALL
SELECT 
    '| `' || tc.table_name || '` | `' || ku.column_name || '` | `' || 
    ccu.table_name || '` | `' || ccu.column_name || '` |'
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
JOIN information_schema.constraint_column_usage ccu ON tc.constraint_name = ccu.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY'
AND tc.table_schema = 'public'
AND tc.table_name != 'flyway_schema_history'
ORDER BY tc.table_name, ku.column_name;




