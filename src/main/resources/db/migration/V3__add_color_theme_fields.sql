-- Add color theme fields to stories table
ALTER TABLE stories 
ADD COLUMN background_color VARCHAR(7),
ADD COLUMN buttons_color VARCHAR(7),
ADD COLUMN text_color_buttons VARCHAR(7),
ADD COLUMN text_color VARCHAR(7);

-- Set default colors for existing stories
UPDATE stories SET 
    background_color = '#1f2937',
    buttons_color = '#3b82f6', 
    text_color_buttons = '#ffffff',
    text_color = '#f9fafb'
WHERE background_color IS NULL; 