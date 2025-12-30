-- Migration V25: Restauration des relations Media selon le MCD
-- Media peut être lié à User, Location ou Station (mais un seul à la fois)
-- Conformité avec le Modèle Conceptuel de Données

-- 1. Rendre station_id nullable (un média peut ne pas être lié à une station)
ALTER TABLE medias ALTER COLUMN station_id DROP NOT NULL;

-- 2. Ajouter les colonnes user_id et location_id (optionnelles)
ALTER TABLE medias 
ADD COLUMN IF NOT EXISTS user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
ADD COLUMN IF NOT EXISTS location_id BIGINT REFERENCES locations(id) ON DELETE CASCADE;

-- 3. Créer les index pour optimiser les recherches
CREATE INDEX IF NOT EXISTS idx_medias_user_id ON medias(user_id);
CREATE INDEX IF NOT EXISTS idx_medias_location_id ON medias(location_id);

-- 4. Ajouter la contrainte CHECK pour s'assurer qu'un média est lié à exactement une entité parent
-- (Station, Location ou User) - comme dans V15
ALTER TABLE medias DROP CONSTRAINT IF EXISTS media_single_parent_check;
ALTER TABLE medias ADD CONSTRAINT media_single_parent_check CHECK (
  (CASE WHEN station_id IS NOT NULL THEN 1 ELSE 0 END +
   CASE WHEN location_id IS NOT NULL THEN 1 ELSE 0 END +
   CASE WHEN user_id IS NOT NULL THEN 1 ELSE 0 END) = 1
);

-- 5. Mettre à jour les commentaires
COMMENT ON TABLE medias IS 'Table des medias (photos et videos). Un media peut illustrer un utilisateur, un lieu ou une borne, mais un seul a la fois.';
COMMENT ON COLUMN medias.station_id IS 'ID de la station (optionnel) - Un media peut illustrer une borne';
COMMENT ON COLUMN medias.location_id IS 'ID du lieu (optionnel) - Un media peut illustrer un lieu';
COMMENT ON COLUMN medias.user_id IS 'ID de l utilisateur (optionnel) - Un media peut illustrer un utilisateur';
COMMENT ON COLUMN medias.type IS 'Type de media : IMAGE ou VIDEO';




