-- Migration to rename educational_guide_url column to awakening_guide_url
-- This migration supports the change from "Guía Educativa" to "Guía Despertador"

-- Rename the column from educational_guide_url to awakening_guide_url
ALTER TABLE stories RENAME COLUMN educational_guide_url TO awakening_guide_url;

-- Add comment to document the change
COMMENT ON COLUMN stories.awakening_guide_url IS 'URL for the awakening guide image (previously educational guide)'; 