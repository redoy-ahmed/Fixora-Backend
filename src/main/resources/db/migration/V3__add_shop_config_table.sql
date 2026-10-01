-- Migration V3: Add Shop Config Table
CREATE TABLE IF NOT EXISTS shop_config (
    id INT PRIMARY KEY DEFAULT 1,
    shop_name VARCHAR(255) NOT NULL DEFAULT 'TechCare Fixora',
    tagline VARCHAR(255) NOT NULL DEFAULT 'Professional Electronics Repair & Digital Passport',
    primary_phone VARCHAR(50) NOT NULL DEFAULT '+880 1711 000000',
    primary_email VARCHAR(255) NOT NULL DEFAULT 'support@techcare.com',
    currency_symbol VARCHAR(10) NOT NULL DEFAULT '$',
    tax_rate_percent DOUBLE PRECISION NOT NULL DEFAULT 5.0,
    default_warranty_days INT NOT NULL DEFAULT 90,
    receipt_footer_text TEXT NOT NULL DEFAULT 'Thank you for choosing TechCare Fixora.',
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO shop_config (id, shop_name, tagline, primary_phone, primary_email, currency_symbol, tax_rate_percent, default_warranty_days, receipt_footer_text)
VALUES (1, 'TechCare Fixora Main', 'Professional Electronics Repair & Digital Passport', '+880 1711 000000', 'support@techcare.com', '$', 5.0, 90, 'Thank you for choosing TechCare Fixora. Track repair record via QR code.')
ON CONFLICT (id) DO NOTHING;
