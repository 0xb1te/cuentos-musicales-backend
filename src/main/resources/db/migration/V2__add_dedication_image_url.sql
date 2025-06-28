-- Add dedication_image_url column to stories table
ALTER TABLE stories ADD COLUMN dedication_image_url VARCHAR(255);

-- Add presentation_image_url column to stories table
ALTER TABLE stories ADD COLUMN presentation_image_url VARCHAR(255);

-- Add custom_phrase column to stories table (if it doesn't exist)
ALTER TABLE stories ADD COLUMN IF NOT EXISTS custom_phrase TEXT;

-- Add comments to the columns for documentation
COMMENT ON COLUMN stories.dedication_image_url IS 'URL of the dedication image displayed when user clicks on Dedicatoria';
COMMENT ON COLUMN stories.presentation_image_url IS 'URL of the presentation image displayed when user clicks on Presentación';
COMMENT ON COLUMN stories.custom_phrase IS 'Admin-defined custom phrase displayed at the top of story items'; 