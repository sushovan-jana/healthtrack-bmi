-- Enable gen_random_uuid support (native in modern PostgreSQL)
CREATE TABLE doctors (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE patients (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50) UNIQUE NOT NULL, -- Globally unique patient identifier
    age INTEGER NOT NULL,
    gender VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,  -- Soft delete indicator
    deleted_at TIMESTAMP WITH TIME ZONE NULL, -- Soft delete timestamp
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE bmi_calculations (
    id BIGSERIAL PRIMARY KEY,
    patient_id UUID NOT NULL,
    height DOUBLE PRECISION NOT NULL, -- in cm
    weight DOUBLE PRECISION NOT NULL, -- in kg
    bmi_value DOUBLE PRECISION NOT NULL,
    classification VARCHAR(100) NOT NULL, -- WHO standard categories
    calculated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_bmi_calc_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE doctor_notes (
    id BIGSERIAL PRIMARY KEY,
    patient_id UUID NOT NULL,
    note TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_notes_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- Performance indices for lookups and search matching
CREATE INDEX idx_patients_search ON patients(name, phone_number);
CREATE INDEX idx_bmi_calculations_patient ON bmi_calculations(patient_id, calculated_at DESC);
CREATE INDEX idx_doctor_notes_patient ON doctor_notes(patient_id, created_at DESC);
