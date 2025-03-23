-- Crear tabla para las opciones de menú
CREATE TABLE IF NOT EXISTS menu_options (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255),
    background_image_url VARCHAR(255),
    route VARCHAR(255),
    "order" INTEGER,
    is_active BOOLEAN DEFAULT true,
    menu_level_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
); 