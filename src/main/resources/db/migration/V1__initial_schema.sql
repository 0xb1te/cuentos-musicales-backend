-- Eliminar tablas en orden correcto (por las dependencias)
DROP TABLE IF EXISTS sales CASCADE;
DROP TABLE IF EXISTS story_menu_levels CASCADE;
DROP TABLE IF EXISTS interactive_elements CASCADE;
DROP TABLE IF EXISTS teaching_guides CASCADE;
DROP TABLE IF EXISTS stories CASCADE;
DROP TABLE IF EXISTS menu_options CASCADE;
DROP TABLE IF EXISTS menu_levels CASCADE;
DROP TABLE IF EXISTS admin CASCADE;

-- Crear tabla de administrador
CREATE TABLE admin (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla para el menú multinivel
CREATE TABLE menu_levels (
    id BIGSERIAL PRIMARY KEY,
    menu_structure JSONB NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla para las opciones de menú con el campo images como array
CREATE TABLE menu_options (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    images TEXT[], -- Array of image URLs instead of single icon_url
    route VARCHAR(255),
    "order" INTEGER,
    is_active BOOLEAN DEFAULT true,
    menu_level_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de cuentos con los campos adicionales para admin
CREATE TABLE stories (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(200) UNIQUE NOT NULL,
    author VARCHAR(200),
    description TEXT,
    content TEXT NOT NULL,
    price DECIMAL(10,2),
    is_free BOOLEAN DEFAULT false,
    stripe_price_id VARCHAR(100),
    preview_content TEXT,
    cover_image_url VARCHAR(255),
    image_url VARCHAR(255),
    audio_preview_url VARCHAR(255),
    audio_full_url VARCHAR(255),
    indicative_image1 VARCHAR(255),
    indicative_image2 VARCHAR(255),
    emotional_guide_url VARCHAR(255),
    musical_guide_url VARCHAR(255),
    educational_guide_url VARCHAR(255),
    duration INTEGER,
    has_interactive_elements BOOLEAN DEFAULT false,
    menu_level_id BIGINT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de guías didácticas
CREATE TABLE teaching_guides (
    id BIGSERIAL PRIMARY KEY,
    story_id BIGINT REFERENCES stories(id) ON DELETE CASCADE,
    preview TEXT NOT NULL,
    full_content TEXT,
    download_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de elementos interactivos
CREATE TABLE interactive_elements (
    id BIGSERIAL PRIMARY KEY,
    story_id BIGINT REFERENCES stories(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL CHECK (type IN ('game', 'quiz', 'activity')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de ventas
CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    stripe_payment_id VARCHAR(100) UNIQUE NOT NULL,
    story_id BIGINT REFERENCES stories(id),
    amount DECIMAL(10,2) NOT NULL,
    customer_email VARCHAR(100),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar estructura del menú principal actualizada usando images array en lugar de imageUrl
INSERT INTO menu_levels (menu_structure, active, created_at, updated_at)
VALUES (
'{
  "items": [
    {
      "id": 2,
      "title": "Cuentos",
      "children": [
        {
          "id": 1,
          "title": "Ver todos",
          "route": "/"
        },
        {
          "id": 21,
          "title": "Por Edad",
          "children": [
            {
              "id": 211,
              "title": "3-5 años",
              "route": "/category/3-5",
              "images": ["https://dummyimage.com/600x400/000/fff&text=3-5+años"]
            },
            {
              "id": 212,
              "title": "6-8 años",
              "route": "/category/6-8",
              "images": ["https://dummyimage.com/600x400/000/fff&text=6-8+años"]
            },
            {
              "id": 213,
              "title": "9-12 años",
              "route": "/category/9-12",
              "images": ["https://dummyimage.com/600x400/000/fff&text=9-12+años"]
            }
          ]
        },
        {
          "id": 22,
          "title": "Más Recientes",
          "route": "/recent",
          "images": ["https://dummyimage.com/600x400/000/fff&text=Recientes"]
        },
        {
          "id": 23,
          "title": "Más Populares",
          "route": "/popular",
          "images": ["https://dummyimage.com/600x400/000/fff&text=Populares"]
        }
      ]
    },
    {
      "id": 3,
      "title": "Guías Didácticas",
      "route": "/guides",
      "images": ["https://dummyimage.com/600x400/000/fff&text=Guías"]
    },
    {
      "id": 4,
      "title": "Sobre Nosotros",
      "route": "/about"
    },
    {
      "id": 5,
      "title": "Contacto",
      "route": "/contact"
    }
  ]
}'::jsonb,
true,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP
);

-- Insertar algunas opciones de menú iniciales con imágenes como arrays
INSERT INTO menu_options (title, description, images, route, "order", is_active, menu_level_id)
VALUES 
('Cuentos para niños', 'Descubre nuestros cuentos para niños de todas las edades', 
 ARRAY['https://example.com/icons/books.png', 'https://example.com/backgrounds/kids_books.jpg'], 
 '/cuentos', 1, true, 211),
('Cuentos musicales', 'Cuentos con elementos musicales interactivos', 
 ARRAY['https://example.com/icons/music.png', 'https://example.com/backgrounds/music_books.jpg'], 
 '/cuentos-musicales', 2, true, 212),
('Novedades', 'Nuestras últimas publicaciones', 
 ARRAY['https://example.com/icons/new.png', 'https://example.com/backgrounds/new_books.jpg'], 
 '/novedades', 3, true, 22);

-- Crear índices
CREATE INDEX idx_stories_slug ON stories(slug);
CREATE INDEX idx_stories_author ON stories(author);
CREATE INDEX idx_stories_is_free ON stories(is_free);
CREATE INDEX idx_menu_levels_active ON menu_levels(active);
CREATE INDEX idx_teaching_guides_story ON teaching_guides(story_id);
CREATE INDEX idx_interactive_elements_story ON interactive_elements(story_id);
CREATE INDEX idx_menu_options_menu_level_id ON menu_options(menu_level_id);
CREATE INDEX idx_menu_options_is_active ON menu_options(is_active);

-- Insertar cuentos mock con los nuevos campos
INSERT INTO stories (
    title, slug, author, description, content, price, is_free, 
    stripe_price_id, preview_content, cover_image_url, image_url, 
    audio_preview_url, audio_full_url, indicative_image1, indicative_image2,
    emotional_guide_url, musical_guide_url, educational_guide_url, duration,
    has_interactive_elements, menu_level_id, created_at, updated_at
)
VALUES
(
    'El Cuento del Conejo Valiente',
    'el-cuento-del-conejo-valiente',
    'María López',
    'Un cuento sobre un conejo que enfrenta sus miedos.',
    'Había una vez un conejo que vivía en un bosque...',
    9.99,
    false,
    'price_1',
    'Había una vez un conejo que vivía en un bosque...',
    'https://dummyimage.com/600x400/000/fff&text=Conejo+Valiente',
    'https://dummyimage.com/600x400/000/fff&text=Conejo+Valiente',
    'https://example.com/audio/preview/conejo.mp3',
    'https://example.com/audio/full/conejo.mp3',
    'https://dummyimage.com/300x200/000/fff&text=Conejo+1',
    'https://dummyimage.com/300x200/000/fff&text=Conejo+2',
    'https://example.com/guides/emotional/conejo.pdf',
    'https://example.com/guides/musical/conejo.pdf',
    'https://example.com/guides/educational/conejo.pdf',
    12,
    true,
    ARRAY[211, 22],
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'La Aventura del Dragón Dorado',
    'la-aventura-del-dragon-dorado',
    'Carlos Martínez',
    'Un dragón dorado busca su hogar perdido.',
    'En una tierra lejana, un dragón dorado volaba por los cielos...',
    12.99,
    false,
    'price_2',
    'En una tierra lejana, un dragón dorado volaba por los cielos...',
    'https://dummyimage.com/600x400/000/fff&text=Dragón+Dorado',
    'https://dummyimage.com/600x400/000/fff&text=Dragón+Dorado',
    'https://example.com/audio/preview/dragon.mp3',
    'https://example.com/audio/full/dragon.mp3',
    'https://dummyimage.com/300x200/000/fff&text=Dragon+1',
    'https://dummyimage.com/300x200/000/fff&text=Dragon+2',
    'https://example.com/guides/emotional/dragon.pdf',
    'https://example.com/guides/musical/dragon.pdf',
    'https://example.com/guides/educational/dragon.pdf',
    15,
    false,
    ARRAY[212, 23],
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'El Pequeño Elefante Curioso',
    'el-pequeno-elefante-curioso',
    'Ana Gómez',
    'Un cuento sobre un elefante que descubre el mundo.',
    'Había una vez un pequeño elefante que siempre tenía curiosidad por todo...',
    8.99,
    true,
    'price_3',
    'Había una vez un pequeño elefante que siempre tenía curiosidad por todo...',
    'https://dummyimage.com/600x400/000/fff&text=Elefante+Curioso',
    'https://dummyimage.com/600x400/000/fff&text=Elefante+Curioso',
    'https://example.com/audio/preview/elefante.mp3',
    'https://example.com/audio/full/elefante.mp3',
    'https://dummyimage.com/300x200/000/fff&text=Elefante+1',
    'https://dummyimage.com/300x200/000/fff&text=Elefante+2',
    'https://example.com/guides/emotional/elefante.pdf',
    'https://example.com/guides/musical/elefante.pdf',
    'https://example.com/guides/educational/elefante.pdf',
    10,
    true,
    ARRAY[211],
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insertar guías didácticas mock
INSERT INTO teaching_guides (story_id, preview, full_content, download_url, created_at, updated_at)
VALUES
(
    1,
    'Guía didáctica para "El Cuento del Conejo Valiente".',
    'Contenido completo de la guía didáctica...',
    'https://example.com/downloads/guide-conejo-valiente.pdf',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    2,
    'Guía didáctica para "La Aventura del Dragón Dorado".',
    'Contenido completo de la guía didáctica...',
    'https://example.com/downloads/guide-dragon-dorado.pdf',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    3,
    'Guía didáctica para "El Pequeño Elefante Curioso".',
    'Contenido completo de la guía didáctica...',
    'https://example.com/downloads/guide-elefante-curioso.pdf',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insertar elementos interactivos mock
INSERT INTO interactive_elements (story_id, title, description, type, created_at, updated_at)
VALUES
(
    1,
    'Juego: Atrapa las Zanahorias',
    'Un juego interactivo para niños.',
    'game',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    1,
    'Quiz: Preguntas sobre el Conejo Valiente',
    'Un quiz para evaluar la comprensión del cuento.',
    'quiz',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    3,
    'Juego: Encuentra las Frutas',
    'Un juego interactivo para niños.',
    'game',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    3,
    'Quiz: Preguntas sobre el Elefante Curioso',
    'Un quiz para evaluar la comprensión del cuento.',
    'quiz',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Función para agregar una opción de menú (actualizada para usar images array)
CREATE OR REPLACE FUNCTION add_menu_option(
    parent_id BIGINT, 
    title VARCHAR(200), 
    route VARCHAR(200), 
    images TEXT[] DEFAULT NULL
) RETURNS JSONB AS $$
DECLARE
    menu JSONB;
    new_id BIGINT;
    menu_item JSONB;
BEGIN
    -- Obtener la estructura actual del menú
    SELECT menu_structure INTO menu FROM menu_levels WHERE active = true LIMIT 1;
    
    -- Generar un nuevo ID (el último ID + 1)
    SELECT COALESCE(MAX(id), 0) + 1 INTO new_id
    FROM (
        SELECT jsonb_array_elements(jsonb_path_query(menu, '$.items[*]')) ->> 'id' AS id
        UNION ALL
        SELECT jsonb_array_elements(jsonb_path_query(menu, '$.items[*].children[*]')) ->> 'id' AS id
        UNION ALL
        SELECT jsonb_array_elements(jsonb_path_query(menu, '$.items[*].children[*].children[*]')) ->> 'id' AS id
    ) AS ids;
    
    -- Crear el nuevo item de menú
    IF images IS NULL THEN
        menu_item := jsonb_build_object(
            'id', new_id,
            'title', title,
            'route', route
        );
    ELSE
        menu_item := jsonb_build_object(
            'id', new_id,
            'title', title,
            'route', route,
            'images', to_jsonb(images)
        );
    END IF;
    
    -- Agregar el nuevo item al padre correspondiente (implementación simplificada)
    -- Para una implementación completa, se debería recorrer recursivamente la estructura
    IF parent_id = 0 THEN
        -- Agregar al nivel superior
        menu := jsonb_set(menu, '{items}', menu->'items' || jsonb_build_array(menu_item));
    ELSE
        -- Buscar el elemento padre y agregar a sus hijos
        -- Nota: Esta es una implementación simplificada
        -- En un escenario real, necesitarías recorrer toda la estructura recursivamente
        RAISE NOTICE 'Implementación simplificada: para agregar elementos a niveles anidados, modifique manualmente la estructura JSON';
    END IF;
    
    -- Actualizar la estructura del menú
    UPDATE menu_levels SET menu_structure = menu, updated_at = CURRENT_TIMESTAMP WHERE active = true;
    
    RETURN menu;
END;
$$ LANGUAGE plpgsql;

-- Función para obtener el menú para el sidebar
CREATE OR REPLACE FUNCTION get_sidebar_menu() RETURNS JSONB AS $$
DECLARE
    menu JSONB;
BEGIN
    SELECT menu_structure INTO menu FROM menu_levels WHERE active = true LIMIT 1;
    RETURN menu;
END;
$$ LANGUAGE plpgsql;