-- ============================================================
-- SCRIPT SQL POUR GÉNÉRER LE DICTIONNAIRE DE DONNÉES (Format Excel)
-- Electricity Business - Base de données PostgreSQL
-- ============================================================
-- 
-- Format : Table, Nom du Champ, Type, Obligatoire ?, Clé, Description & Règles de gestion
-- ============================================================

SELECT 
    c.table_name as "Table",
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
    CASE 
        -- Table: users
        WHEN c.table_name = 'users' AND c.column_name = 'id' THEN 'Identifiant unique de l''utilisateur.'
        WHEN c.table_name = 'users' AND c.column_name = 'first_name' THEN 'Prenom de l''utilisateur.'
        WHEN c.table_name = 'users' AND c.column_name = 'last_name' THEN 'Nom de famille de l''utilisateur.'
        WHEN c.table_name = 'users' AND c.column_name = 'email' THEN 'Adresse email unique de l''utilisateur (utilisee pour la connexion).'
        WHEN c.table_name = 'users' AND c.column_name = 'phone' THEN 'Numéro de téléphone (optionnel).'
        WHEN c.table_name = 'users' AND c.column_name = 'date_of_birth' THEN 'Date de naissance (optionnel).'
        WHEN c.table_name = 'users' AND c.column_name = 'address' THEN 'Adresse postale complète.'
        WHEN c.table_name = 'users' AND c.column_name = 'postal_code' THEN 'Code postal.'
        WHEN c.table_name = 'users' AND c.column_name = 'city' THEN 'Ville de résidence.'
        WHEN c.table_name = 'users' AND c.column_name = 'password_hash' THEN 'Mot de passe hashe avec BCrypt (jamais stocke en clair).'
        WHEN c.table_name = 'users' AND c.column_name = 'status' THEN 'Statut de l''utilisateur : PENDING (en attente de validation email), ACTIVE (actif).'
        WHEN c.table_name = 'users' AND c.column_name = 'created_at' THEN 'Date et heure de création du compte.'
        WHEN c.table_name = 'users' AND c.column_name = 'updated_at' THEN 'Date et heure de dernière modification.'
        
        -- Table: email_verification_codes
        WHEN c.table_name = 'email_verification_codes' AND c.column_name = 'id' THEN 'Identifiant unique du code de verification.'
        WHEN c.table_name = 'email_verification_codes' AND c.column_name = 'user_id' THEN 'Lien vers l''utilisateur (users).'
        WHEN c.table_name = 'email_verification_codes' AND c.column_name = 'code_hash' THEN 'Code de verification hashe (jamais stocke en clair).'
        WHEN c.table_name = 'email_verification_codes' AND c.column_name = 'expires_at' THEN 'Date et heure d''expiration du code (generalement 24h apres creation).'
        WHEN c.table_name = 'email_verification_codes' AND c.column_name = 'attempt_count' THEN 'Nombre de tentatives de verification (limite a 3 tentatives).'
        WHEN c.table_name = 'email_verification_codes' AND c.column_name = 'used_at' THEN 'Date et heure d''utilisation du code (NULL si non utilise).'
        WHEN c.table_name = 'email_verification_codes' AND c.column_name = 'created_at' THEN 'Date et heure de création du code.'
        
        -- Table: locations
        WHEN c.table_name = 'locations' AND c.column_name = 'id' THEN 'Identifiant unique du lieu.'
        WHEN c.table_name = 'locations' AND c.column_name = 'owner_id' THEN 'Lien vers l''utilisateur propriétaire (users).'
        WHEN c.table_name = 'locations' AND c.column_name = 'label' THEN 'Nom donné au lieu (ex: "Maison", "Bureau").'
        WHEN c.table_name = 'locations' AND c.column_name = 'address' THEN 'Adresse complète du lieu.'
        WHEN c.table_name = 'locations' AND c.column_name = 'latitude' THEN 'Coordonnée GPS pour le placement sur la carte.'
        WHEN c.table_name = 'locations' AND c.column_name = 'longitude' THEN 'Coordonnée GPS.'
        WHEN c.table_name = 'locations' AND c.column_name = 'description' THEN 'Description detaillee du lieu (optionnel).'
        WHEN c.table_name = 'locations' AND c.column_name = 'is_active' THEN 'Permet de désactiver le lieu sans le supprimer (Soft Delete).'
        WHEN c.table_name = 'locations' AND c.column_name = 'created_at' THEN 'Date et heure de création du lieu.'
        WHEN c.table_name = 'locations' AND c.column_name = 'updated_at' THEN 'Date et heure de dernière modification.'
        
        -- Table: stations
        WHEN c.table_name = 'stations' AND c.column_name = 'id' THEN 'Identifiant unique de la borne.'
        WHEN c.table_name = 'stations' AND c.column_name = 'location_id' THEN 'Lien vers le lieu où se trouve la borne (locations).'
        WHEN c.table_name = 'stations' AND c.column_name = 'name' THEN 'Nom de la borne (ex: "Borne 1", "Chargeur principal").'
        WHEN c.table_name = 'stations' AND c.column_name = 'hourly_rate' THEN 'Tarif horaire de la recharge en euros (décimal, 2 décimales).'
        WHEN c.table_name = 'stations' AND c.column_name = 'plug_type' THEN 'Type de prise de la borne (ex: "TYPE_2S"). Toutes les bornes utilisent TYPE_2S par défaut.'
        WHEN c.table_name = 'stations' AND c.column_name = 'is_active' THEN 'Permet de désactiver la borne sans la supprimer (Soft Delete).'
        WHEN c.table_name = 'stations' AND c.column_name = 'status' THEN 'Statut de la borne : ACTIVE, INACTIVE, MAINTENANCE.'
        WHEN c.table_name = 'stations' AND c.column_name = 'power' THEN 'Puissance de la borne en kW (optionnel).'
        WHEN c.table_name = 'stations' AND c.column_name = 'city' THEN 'Ville où se trouve la borne (optionnel, peut être hérité du lieu).'
        WHEN c.table_name = 'stations' AND c.column_name = 'latitude' THEN 'Coordonnee GPS de la borne (optionnel, peut etre herite du lieu).'
        WHEN c.table_name = 'stations' AND c.column_name = 'longitude' THEN 'Coordonnee GPS de la borne (optionnel, peut etre herite du lieu).'
        WHEN c.table_name = 'stations' AND c.column_name = 'instructions' THEN 'Instructions d''acces a la borne (optionnel).'
        WHEN c.table_name = 'stations' AND c.column_name = 'on_foot' THEN 'Indique si la borne est accessible à pied (optionnel).'
        WHEN c.table_name = 'stations' AND c.column_name = 'created_at' THEN 'Date et heure de création de la borne.'
        WHEN c.table_name = 'stations' AND c.column_name = 'updated_at' THEN 'Date et heure de dernière modification.'
        
        -- Table: reservations
        WHEN c.table_name = 'reservations' AND c.column_name = 'id' THEN 'Identifiant unique de la réservation.'
        WHEN c.table_name = 'reservations' AND c.column_name = 'user_id' THEN 'Lien vers l''utilisateur conducteur (users).'
        WHEN c.table_name = 'reservations' AND c.column_name = 'station_id' THEN 'Lien vers la borne réservée (stations).'
        WHEN c.table_name = 'reservations' AND c.column_name = 'start_time' THEN 'Date et heure de debut de la reservation (doit etre dans le futur ou present).'
        WHEN c.table_name = 'reservations' AND c.column_name = 'end_time' THEN 'Date et heure de fin de la reservation (doit etre apres start_time).'
        WHEN c.table_name = 'reservations' AND c.column_name = 'total_amount' THEN 'Montant total de la reservation en euros (calcule : duree x tarif horaire).'
        WHEN c.table_name = 'reservations' AND c.column_name = 'status' THEN 'Statut de la reservation : PENDING (en attente), CONFIRMED (confirmee), CANCELLED (annulee), COMPLETED (terminee), REFUSED (refusee).'
        WHEN c.table_name = 'reservations' AND c.column_name = 'notes' THEN 'Notes ou commentaires sur la réservation (optionnel, max 1000 caractères).'
        WHEN c.table_name = 'reservations' AND c.column_name = 'created_at' THEN 'Date et heure de création de la réservation.'
        WHEN c.table_name = 'reservations' AND c.column_name = 'updated_at' THEN 'Date et heure de dernière modification.'
        
        -- Table: medias
        WHEN c.table_name = 'medias' AND c.column_name = 'id' THEN 'Identifiant unique du média.'
        WHEN c.table_name = 'medias' AND c.column_name = 'station_id' THEN 'Lien vers la borne concernee (stations). Un media appartient toujours a une borne.'
        WHEN c.table_name = 'medias' AND c.column_name = 'name' THEN 'Nom du fichier média.'
        WHEN c.table_name = 'medias' AND c.column_name = 'url' THEN 'URL ou chemin d''accès au fichier média.'
        WHEN c.table_name = 'medias' AND c.column_name = 'type' THEN 'Type de média : IMAGE ou VIDEO.'
        WHEN c.table_name = 'medias' AND c.column_name = 'description' THEN 'Description du média (optionnel).'
        WHEN c.table_name = 'medias' AND c.column_name = 'file_size' THEN 'Taille du fichier en octets (optionnel).'
        WHEN c.table_name = 'medias' AND c.column_name = 'mime_type' THEN 'Type MIME du fichier (ex: "image/jpeg", "video/mp4") (optionnel).'
        WHEN c.table_name = 'medias' AND c.column_name = 'created_at' THEN 'Date et heure de création/upload du média.'
        WHEN c.table_name = 'medias' AND c.column_name = 'updated_at' THEN 'Date et heure de dernière modification.'
        
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
WHERE c.table_schema = 'public' 
AND c.table_name IN ('users', 'email_verification_codes', 'locations', 'stations', 'reservations', 'medias')
ORDER BY 
    CASE c.table_name
        WHEN 'users' THEN 1
        WHEN 'email_verification_codes' THEN 2
        WHEN 'locations' THEN 3
        WHEN 'stations' THEN 4
        WHEN 'reservations' THEN 5
        WHEN 'medias' THEN 6
    END,
    c.ordinal_position;

