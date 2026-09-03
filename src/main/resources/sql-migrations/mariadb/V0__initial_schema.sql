CREATE TABLE snapshots (
    id UUID PRIMARY KEY,
    created_at BIGINT NOT NULL,
    player UUID NOT NULL,
    cause VARCHAR(255) NOT NULL,
    data JSON NOT NULL
);