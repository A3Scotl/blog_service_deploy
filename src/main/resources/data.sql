-- Insert categories
INSERT INTO categories (name, description, created_at, updated_at, active) VALUES
                                                                               ('Technology', 'Articles about tech and innovation', '2025-05-16 15:00:00', '2025-05-16 15:00:00', TRUE),
                                                                               ('Lifestyle', 'Tips and stories about daily life', '2025-05-16 15:00:00', '2025-05-16 15:00:00', TRUE),
                                                                               ('Travel', 'Travel guides and experiences', '2025-05-16 15:00:00', '2025-05-16 15:00:00', TRUE);

-- Insert blogs (assuming author_id 1 and 2 exist in the user service)
INSERT INTO blogs (title, content, status, thumbnail, author_id, category_id, created_at, updated_at, active) VALUES
                                                                                                                  ('The Future of AI', 'AI is transforming the world...', 'PUBLISHED', 'https://example.com/thumbnail1.jpg', 1, 1, '2025-05-16 15:10:00', '2025-05-16 15:10:00', TRUE),
                                                                                                                  ('Healthy Living Tips', 'How to live a healthier life...', 'DRAFT', 'https://example.com/thumbnail2.jpg', 2, 2, '2025-05-16 15:20:00', '2025-05-16 15:20:00', TRUE),
                                                                                                                  ('Exploring Japan', 'A guide to traveling in Japan...', 'PUBLISHED', 'https://example.com/thumbnail3.jpg', 1, 3, '2025-05-16 15:30:00', '2025-05-16 15:30:00', TRUE);

-- Insert comments (assuming user_id 1 and 2 exist in the user service)
INSERT INTO comments (content, user_id, blog_id, created_at, updated_at, active) VALUES
                                                                                     ('Great article on AI!', 1, 1, '2025-05-16 15:40:00', '2025-05-16 15:40:00', TRUE),
                                                                                     ('Looking forward to more tips!', 2, 2, '2025-05-16 15:45:00', '2025-05-16 15:45:00', TRUE),
                                                                                     ('Japan is amazing!', 1, 3, '2025-05-16 15:50:00', '2025-05-16 15:50:00', TRUE);

-- Insert likes (assuming user_id 1 and 2 exist in the user service)
INSERT INTO likes (user_id, blog_id, created_at, updated_at, active) VALUES
                                                                         (1, 1, '2025-05-16 15:40:00', '2025-05-16 15:40:00', TRUE),
                                                                         (2, 1, '2025-05-16 15:41:00', '2025-05-16 15:41:00', TRUE),
                                                                         (1, 3, '2025-05-16 15:42:00', '2025-05-16 15:42:00', TRUE);