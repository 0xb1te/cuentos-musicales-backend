-- Add container background color field to stories table
ALTER TABLE stories 
ADD COLUMN container_background_color VARCHAR(7);

-- Set default color for existing stories (slightly darker than main background)
UPDATE stories SET 
    container_background_color = '#374151'
WHERE container_background_color IS NULL; 