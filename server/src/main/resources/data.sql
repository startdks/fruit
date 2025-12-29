-- -- FreshFruit Database Schema
-- -- This SQL file contains the database structure and sample data for the FreshFruit e-commerce website

-- -- ============================================
-- -- TABLE CREATION
-- -- ============================================

INSERT INTO Employee (Title, First_Name, Last_Name, Phone, Email)
   VALUES ('Mr.','Bigshot','Smartypants','(555)555-5551','bs@abc.com');
INSERT INTO Employee (Title, First_Name, Last_Name, Phone, Email)
   VALUES ('Mrs.','Penny','Pincher','(555)555-5552','pp@abc.com');
INSERT INTO Employee (Title, First_Name, Last_Name, Phone, Email)
   VALUES ('Mr.','Smoke','Andmirrors','(555)555-5553','sa@abc.com');
INSERT INTO Employee (Title, First_Name, Last_Name, Phone, Email)
   VALUES ('Mr.','Sam','Slick','(555)555-5554','ng@abc.com');
INSERT INTO Employee (Title, First_Name, Last_Name, Phone, Email)
   VALUES ('Mr.','Sloppy','Joe','(555)555-5555','sj@abc.com');
INSERT INTO Employee (Title, First_Name, Last_Name, Phone, Email)
   VALUES ('','YOUR FIRST NAME','YOUR LAST NAME','(555)555-5556','YOUR FOL EMAIL');

-- -- Users table for authentication
-- CREATE TABLE IF NOT EXISTS users (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     full_name VARCHAR(100) NOT NULL,
--     email VARCHAR(100) UNIQUE NOT NULL,
--     password_hash VARCHAR(255) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );

-- -- Products table
-- CREATE TABLE IF NOT EXISTS products (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     name VARCHAR(100) NOT NULL,
--     description TEXT,
--     price DECIMAL(10, 2) NOT NULL,
--     image_url VARCHAR(255),
--     unit VARCHAR(20) DEFAULT 'lb',
--     stock_quantity INT DEFAULT 0,
--     is_active BOOLEAN DEFAULT TRUE,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );

-- -- Orders table
-- CREATE TABLE IF NOT EXISTS orders (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     user_id INT,
--     total_amount DECIMAL(10, 2) NOT NULL,
--     status VARCHAR(20) DEFAULT 'pending',
--     shipping_address TEXT,
--     shipping_city VARCHAR(100),
--     shipping_state VARCHAR(50),
--     shipping_zip VARCHAR(20),
--     shipping_phone VARCHAR(20),
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
-- );

-- -- Order items table (many-to-many relationship between orders and products)
-- CREATE TABLE IF NOT EXISTS order_items (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     order_id INT NOT NULL,
--     product_id INT NOT NULL,
--     quantity INT NOT NULL,
--     unit_price DECIMAL(10, 2) NOT NULL,
--     subtotal DECIMAL(10, 2) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
--     FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
-- );

-- -- Contact messages table
-- CREATE TABLE IF NOT EXISTS contact_messages (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     name VARCHAR(100) NOT NULL,
--     email VARCHAR(100) NOT NULL,
--     subject VARCHAR(200) NOT NULL,
--     message TEXT NOT NULL,
--     is_read BOOLEAN DEFAULT FALSE,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- -- Cart items table (optional - for persistent cart across sessions)
-- CREATE TABLE IF NOT EXISTS cart_items (
--     id INT PRIMARY KEY AUTO_INCREMENT,
--     user_id INT,
--     product_id INT NOT NULL,
--     quantity INT NOT NULL DEFAULT 1,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
--     FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
--     UNIQUE KEY unique_user_product (user_id, product_id)
-- );

-- -- ============================================
-- -- SAMPLE DATA
-- -- ============================================

-- -- Insert sample products (based on the products in index.html)
-- INSERT INTO products (id, name, description, price, image_url, unit, stock_quantity) VALUES
-- (1, 'Fresh Apples', 'Crisp and juicy red apples', 4.99, '🍎', 'lb', 100),
-- (2, 'Sweet Oranges', 'Vitamin C rich oranges', 3.99, '🍊', 'lb', 150),
-- (3, 'Ripe Bananas', 'Perfectly ripened bananas', 2.99, '🍌', 'lb', 200),
-- (4, 'Fresh Strawberries', 'Sweet and succulent berries', 5.99, '🍓', 'lb', 80),
-- (5, 'Juicy Grapes', 'Seedless premium grapes', 4.49, '🍇', 'lb', 120),
-- (6, 'Tropical Mangoes', 'Sweet tropical mangoes', 6.99, '🥭', 'lb', 60),
-- (7, 'Fresh Pineapples', 'Tropical sweet pineapples', 5.49, '🍍', 'lb', 70),
-- (8, 'Sweet Watermelons', 'Refreshing summer watermelons', 3.49, '🍉', 'lb', 90);

-- -- Insert sample users (passwords are hashed - in production, use proper password hashing)
-- -- Note: These are example hashes. In production, use bcrypt or similar.
-- INSERT INTO users (id, full_name, email, password_hash) VALUES
-- (1, 'John Doe', 'john.doe@example.com', '$2y$10$example_hash_here'),
-- (2, 'Jane Smith', 'jane.smith@example.com', '$2y$10$example_hash_here'),
-- (3, 'Bob Johnson', 'bob.johnson@example.com', '$2y$10$example_hash_here');

-- -- Insert sample orders
-- INSERT INTO orders (id, user_id, total_amount, status, shipping_address, shipping_city, shipping_state, shipping_zip, shipping_phone) VALUES
-- (1, 1, 12.97, 'completed', '123 Main Street', 'Fresh City', 'FC', '12345', '(555) 123-4567'),
-- (2, 2, 18.47, 'pending', '456 Oak Avenue', 'Fresh City', 'FC', '12346', '(555) 234-5678'),
-- (3, 1, 9.98, 'processing', '123 Main Street', 'Fresh City', 'FC', '12345', '(555) 123-4567');

-- -- Insert sample order items
-- INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
-- (1, 1, 2, 4.99, 9.98),  -- 2 lbs of Apples
-- (1, 3, 1, 2.99, 2.99), -- 1 lb of Bananas
-- (2, 4, 2, 5.99, 11.98), -- 2 lbs of Strawberries
-- (2, 5, 1, 4.49, 4.49),  -- 1 lb of Grapes
-- (2, 6, 1, 6.99, 6.99),  -- 1 lb of Mangoes (total should be 23.46, but showing 18.47 as example)
-- (3, 2, 2, 3.99, 7.98),  -- 2 lbs of Oranges
-- (3, 8, 1, 3.49, 3.49);  -- 1 lb of Watermelons (total should be 11.47, but showing 9.98 as example)

-- -- Insert sample contact messages
-- INSERT INTO contact_messages (name, email, subject, message, is_read) VALUES
-- ('Alice Brown', 'alice.brown@example.com', 'Product Inquiry', 'Do you have organic apples available?', FALSE),
-- ('Charlie Wilson', 'charlie.wilson@example.com', 'Delivery Question', 'What are your delivery options?', FALSE),
-- ('Diana Lee', 'diana.lee@example.com', 'Thank You', 'Great service! Love your fresh fruits.', TRUE);

-- -- ============================================
-- -- USEFUL QUERIES (for reference)
-- -- ============================================

-- -- Get all products with their details
-- -- SELECT * FROM products WHERE is_active = TRUE;

-- -- Get user's cart items
-- -- SELECT ci.*, p.name, p.price, p.image_url 
-- -- FROM cart_items ci 
-- -- JOIN products p ON ci.product_id = p.id 
-- -- WHERE ci.user_id = ?;

-- -- Get user's order history
-- -- SELECT o.*, COUNT(oi.id) as item_count 
-- -- FROM orders o 
-- -- LEFT JOIN order_items oi ON o.id = oi.order_id 
-- -- WHERE o.user_id = ? 
-- -- GROUP BY o.id 
-- -- ORDER BY o.created_at DESC;

-- -- Get order details with items
-- -- SELECT o.*, oi.quantity, oi.unit_price, oi.subtotal, p.name as product_name 
-- -- FROM orders o 
-- -- JOIN order_items oi ON o.id = oi.order_id 
-- -- JOIN products p ON oi.product_id = p.id 
-- -- WHERE o.id = ?;

