CREATE TABLE IF NOT EXISTS "order" (
    id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL CHECK (status IN ('CREATED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED')),
    origin VARCHAR(150) NOT NULL,
    destination VARCHAR(150) NOT NULL,
    createdAt TIMESTAMP NOT NULL,
    updatedAt TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS driver (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    licenseNumber VARCHAR(50) UNIQUE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS assignment (
    id UUID PRIMARY KEY,
    order_id UUID UNIQUE NOT NULL,
    driver_id UUID NOT NULL,
    pdf VARCHAR(255),
    image VARCHAR(255),
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES "order"(id),
    CONSTRAINT fk_driver FOREIGN KEY (driver_id) REFERENCES driver(id)
);