CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description VARCHAR(255),
                          price DECIMAL(12,2) NOT NULL CHECK (price > 0),
                          stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
                          category VARCHAR(255)
);