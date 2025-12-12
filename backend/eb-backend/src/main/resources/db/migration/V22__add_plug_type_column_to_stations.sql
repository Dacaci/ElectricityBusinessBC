-- V22: Ajouter la colonne plug_type à la table stations
-- Remplacement de la relation Many-to-Many avec plug_types par un simple champ String

-- Ajouter la colonne plug_type avec valeur par défaut
ALTER TABLE stations 
ADD COLUMN plug_type VARCHAR(50) NOT NULL DEFAULT 'TYPE_2S';

-- Commentaire pour documentation
COMMENT ON COLUMN stations.plug_type IS 'Type de prise de la borne (ex: TYPE_2S). Toutes les bornes utilisent TYPE_2S par défaut.';



