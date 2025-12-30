-- ============================================================
-- SCRIPT SQL POUR GÉNÉRER LE DICTIONNAIRE DE DONNÉES (Format Excel)
-- Electricity Business - Base de données PostgreSQL
-- ============================================================
-- 
-- Format : Nom du Champ, Type, Obligatoire ?, Clé, Description & Règles de gestion
-- ============================================================

-- Table: users
SELECT 
    'users' as "Table",
    c.column_name as "Nom du Champ",
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END as "Type",
    CASE WHEN c.is_nullable = 'NO' THEN 'OUI' ELSE 'NON' END as "Obligatoire ?",
    CASE 
        WHEN pk.column_name IS NOT NULL THEN 'PK'
        WHEN fk.column_name IS NOT NULL THEN 'FK'
        ELSE ''
    END as "Clé",
    CASE c.column_name
        WHEN 'id' THEN 'Identifiant unique de l''utilisateur.'
        WHEN 'first_name' THEN 'Prénom de l''utilisateur.'
        WHEN 'last_name' THEN 'Nom de famille de l''utilisateur.'
        WHEN 'email' THEN 'Adresse email unique de l''utilisateur (utilisée pour la connexion).'
        WHEN 'phone' THEN 'Numéro de téléphone (optionnel).'
        WHEN 'date_of_birth' THEN 'Date de naissance (optionnel).'
        WHEN 'address' THEN 'Adresse postale complète.'
        WHEN 'postal_code' THEN 'Code postal.'
        WHEN 'city' THEN 'Ville de résidence.'
        WHEN 'password_hash' THEN 'Mot de passe hashé avec BCrypt (jamais stocké en clair).'
        WHEN 'status' THEN 'Statut de l''utilisateur : PENDING (en attente de validation email), ACTIVE (actif).'
        WHEN 'created_at' THEN 'Date et heure de création du compte.'
        WHEN 'updated_at' THEN 'Date et heure de dernière modification.'
        ELSE ''
    END as "Description & Règles de gestion"
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'users'
ORDER BY c.ordinal_position

UNION ALL

-- Table: email_verification_codes
SELECT 
    'email_verification_codes' as "Table",
    c.column_name as "Nom du Champ",
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END as "Type",
    CASE WHEN c.is_nullable = 'NO' THEN 'OUI' ELSE 'NON' END as "Obligatoire ?",
    CASE 
        WHEN pk.column_name IS NOT NULL THEN 'PK'
        WHEN fk.column_name IS NOT NULL THEN 'FK'
        ELSE ''
    END as "Clé",
    CASE c.column_name
        WHEN 'id' THEN 'Identifiant unique du code de vérification.'
        WHEN 'user_id' THEN 'Lien vers l''utilisateur (users).'
        WHEN 'code_hash' THEN 'Code de vérification hashé (jamais stocké en clair).'
        WHEN 'expires_at' THEN 'Date et heure d''expiration du code (généralement 24h après création).'
        WHEN 'attempt_count' THEN 'Nombre de tentatives de vérification (limite à 3 tentatives).'
        WHEN 'used_at' THEN 'Date et heure d''utilisation du code (NULL si non utilisé).'
        WHEN 'created_at' THEN 'Date et heure de création du code.'
        ELSE ''
    END as "Description & Règles de gestion"
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'email_verification_codes'
ORDER BY c.ordinal_position

UNION ALL

-- Table: locations
SELECT 
    'locations' as "Table",
    c.column_name as "Nom du Champ",
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END as "Type",
    CASE WHEN c.is_nullable = 'NO' THEN 'OUI' ELSE 'NON' END as "Obligatoire ?",
    CASE 
        WHEN pk.column_name IS NOT NULL THEN 'PK'
        WHEN fk.column_name IS NOT NULL THEN 'FK'
        ELSE ''
    END as "Clé",
    CASE c.column_name
        WHEN 'id' THEN 'Identifiant unique du lieu.'
        WHEN 'owner_id' THEN 'Lien vers l''utilisateur propriétaire (users).'
        WHEN 'label' THEN 'Nom donné au lieu (ex: "Maison", "Bureau").'
        WHEN 'address' THEN 'Adresse complète du lieu.'
        WHEN 'latitude' THEN 'Coordonnée GPS pour le placement sur la carte.'
        WHEN 'longitude' THEN 'Coordonnée GPS.'
        WHEN 'description' THEN 'Description détaillée du lieu (optionnel).'
        WHEN 'is_active' THEN 'Permet de désactiver le lieu sans le supprimer (Soft Delete).'
        WHEN 'created_at' THEN 'Date et heure de création du lieu.'
        WHEN 'updated_at' THEN 'Date et heure de dernière modification.'
        ELSE ''
    END as "Description & Règles de gestion"
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'locations'
ORDER BY c.ordinal_position

UNION ALL

-- Table: stations
SELECT 
    'stations' as "Table",
    c.column_name as "Nom du Champ",
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END as "Type",
    CASE WHEN c.is_nullable = 'NO' THEN 'OUI' ELSE 'NON' END as "Obligatoire ?",
    CASE 
        WHEN pk.column_name IS NOT NULL THEN 'PK'
        WHEN fk.column_name IS NOT NULL THEN 'FK'
        ELSE ''
    END as "Clé",
    CASE c.column_name
        WHEN 'id' THEN 'Identifiant unique de la borne.'
        WHEN 'location_id' THEN 'Lien vers le lieu où se trouve la borne (locations).'
        WHEN 'name' THEN 'Nom de la borne (ex: "Borne 1", "Chargeur principal").'
        WHEN 'hourly_rate' THEN 'Tarif horaire de la recharge en euros (décimal, 2 décimales).'
        WHEN 'plug_type' THEN 'Type de prise de la borne (ex: "TYPE_2S"). Toutes les bornes utilisent TYPE_2S par défaut.'
        WHEN 'is_active' THEN 'Permet de désactiver la borne sans la supprimer (Soft Delete).'
        WHEN 'status' THEN 'Statut de la borne : ACTIVE, INACTIVE, MAINTENANCE.'
        WHEN 'power' THEN 'Puissance de la borne en kW (optionnel).'
        WHEN 'city' THEN 'Ville où se trouve la borne (optionnel, peut être hérité du lieu).'
        WHEN 'latitude' THEN 'Coordonnée GPS de la borne (optionnel, peut être hérité du lieu).'
        WHEN 'longitude' THEN 'Coordonnée GPS de la borne (optionnel, peut être hérité du lieu).'
        WHEN 'instructions' THEN 'Instructions d''accès à la borne (optionnel).'
        WHEN 'on_foot' THEN 'Indique si la borne est accessible à pied (optionnel).'
        WHEN 'created_at' THEN 'Date et heure de création de la borne.'
        WHEN 'updated_at' THEN 'Date et heure de dernière modification.'
        ELSE ''
    END as "Description & Règles de gestion"
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'stations'
ORDER BY c.ordinal_position

UNION ALL

-- Table: reservations
SELECT 
    'reservations' as "Table",
    c.column_name as "Nom du Champ",
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END as "Type",
    CASE WHEN c.is_nullable = 'NO' THEN 'OUI' ELSE 'NON' END as "Obligatoire ?",
    CASE 
        WHEN pk.column_name IS NOT NULL THEN 'PK'
        WHEN fk.column_name IS NOT NULL THEN 'FK'
        ELSE ''
    END as "Clé",
    CASE c.column_name
        WHEN 'id' THEN 'Identifiant unique de la réservation.'
        WHEN 'user_id' THEN 'Lien vers l''utilisateur conducteur (users).'
        WHEN 'station_id' THEN 'Lien vers la borne réservée (stations).'
        WHEN 'start_time' THEN 'Date et heure de début de la réservation (doit être dans le futur ou présent).'
        WHEN 'end_time' THEN 'Date et heure de fin de la réservation (doit être après start_time).'
        WHEN 'total_amount' THEN 'Montant total de la réservation en euros (calculé : durée × tarif horaire).'
        WHEN 'status' THEN 'Statut de la réservation : PENDING (en attente), CONFIRMED (confirmée), CANCELLED (annulée), COMPLETED (terminée), REFUSED (refusée).'
        WHEN 'notes' THEN 'Notes ou commentaires sur la réservation (optionnel, max 1000 caractères).'
        WHEN 'created_at' THEN 'Date et heure de création de la réservation.'
        WHEN 'updated_at' THEN 'Date et heure de dernière modification.'
        ELSE ''
    END as "Description & Règles de gestion"
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'reservations'
ORDER BY c.ordinal_position

UNION ALL

-- Table: medias
SELECT 
    'medias' as "Table",
    c.column_name as "Nom du Champ",
    CASE 
        WHEN c.character_maximum_length IS NOT NULL 
        THEN c.data_type || '(' || c.character_maximum_length || ')'
        WHEN c.numeric_precision IS NOT NULL 
        THEN c.data_type || '(' || c.numeric_precision || ',' || COALESCE(c.numeric_scale, 0) || ')'
        ELSE c.data_type
    END as "Type",
    CASE WHEN c.is_nullable = 'NO' THEN 'OUI' ELSE 'NON' END as "Obligatoire ?",
    CASE 
        WHEN pk.column_name IS NOT NULL THEN 'PK'
        WHEN fk.column_name IS NOT NULL THEN 'FK'
        ELSE ''
    END as "Clé",
    CASE c.column_name
        WHEN 'id' THEN 'Identifiant unique du média.'
        WHEN 'station_id' THEN 'Lien vers la borne concernée (stations). Un média appartient toujours à une borne.'
        WHEN 'name' THEN 'Nom du fichier média.'
        WHEN 'url' THEN 'URL ou chemin d''accès au fichier média.'
        WHEN 'type' THEN 'Type de média : IMAGE ou VIDEO.'
        WHEN 'description' THEN 'Description du média (optionnel).'
        WHEN 'file_size' THEN 'Taille du fichier en octets (optionnel).'
        WHEN 'mime_type' THEN 'Type MIME du fichier (ex: "image/jpeg", "video/mp4") (optionnel).'
        WHEN 'created_at' THEN 'Date et heure de création/upload du média.'
        WHEN 'updated_at' THEN 'Date et heure de dernière modification.'
        ELSE ''
    END as "Description & Règles de gestion"
FROM information_schema.columns c
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'PRIMARY KEY' AND tc.table_schema = 'public'
) pk ON c.table_name = pk.table_name AND c.column_name = pk.column_name
LEFT JOIN (
    SELECT ku.table_name, ku.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage ku ON tc.constraint_name = ku.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_schema = 'public'
) fk ON c.table_name = fk.table_name AND c.column_name = fk.column_name
WHERE c.table_schema = 'public' AND c.table_name = 'medias'
ORDER BY c.ordinal_position;




