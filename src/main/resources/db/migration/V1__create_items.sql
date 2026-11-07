CREATE TABLE items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sku BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    price NUMERIC(12, 2) NOT NULL,
    CONSTRAINT items_name_not_blank CHECK (length(trim(name)) > 0),
    CONSTRAINT items_price_non_negative CHECK (price >= 0 AND price <> 'NaN'::numeric)
);
