-- Script de população para ambiente local/desenvolvimento
-- Compatível com migrations V1..V17
-- Melhorias:
--   1) idempotente via TRUNCATE + RESTART IDENTITY;
--   2) sem dependência de IDs fixos (joins por nome/título);
--   3) dados mais completos para teste manual de endpoints.

BEGIN;

TRUNCATE TABLE
    exercise_tags,
    extra_materials,
    exercises,
    post_sections,
    posts,
    contest_team_members,
    contest_teams,
    contests,
    lessons,
    modules,
    registrations
RESTART IDENTITY CASCADE;

-- Módulos
INSERT INTO modules (title, notes, published)
VALUES
    ('Fundamentos de Programação', 'Variáveis, condicionais, laços e noções iniciais de complexidade.', TRUE),
    ('Estruturas de Dados', 'Vetores, listas, pilhas, filas, árvores e análise de desempenho.', TRUE),
    ('Programação Competitiva: Base', 'Leitura rápida de problemas, modelagem e técnicas de implementação.', FALSE);

-- Aulas (sem IDs fixos)
INSERT INTO lessons (module_id, title, video_url, position)
SELECT m.id, l.title, l.video_url, l.position
FROM modules m
JOIN (
    VALUES
        ('Fundamentos de Programação', 'Variáveis e Tipos', 'https://www.youtube.com/watch?v=video001', 1),
        ('Fundamentos de Programação', 'Condicionais', 'https://www.youtube.com/watch?v=video002', 2),
        ('Fundamentos de Programação', 'Laços de Repetição', 'https://www.youtube.com/watch?v=video005', 3),
        ('Estruturas de Dados', 'Vetores e Matrizes', 'https://www.youtube.com/watch?v=video003', 1),
        ('Estruturas de Dados', 'Pilhas e Filas', 'https://www.youtube.com/watch?v=video004', 2),
        ('Estruturas de Dados', 'Árvores Binárias', 'https://www.youtube.com/watch?v=video006', 3),
        ('Programação Competitiva: Base', 'Entrada e Saída Eficientes', 'https://www.youtube.com/watch?v=video007', 1)
) AS l(module_title, title, video_url, position)
    ON l.module_title = m.title;

-- Exercícios
INSERT INTO exercises (module_id, title, oj_url, difficulty)
SELECT m.id, e.title, e.oj_url, e.difficulty
FROM modules m
JOIN (
    VALUES
        ('Fundamentos de Programação', 'Soma de Dois Números', 'https://www.beecrowd.com.br/judge/pt/problems/view/1003', 'EASY'),
        ('Fundamentos de Programação', 'Maior de Três Valores', 'https://www.beecrowd.com.br/judge/pt/problems/view/1013', 'EASY'),
        ('Fundamentos de Programação', 'Par ou Ímpar', 'https://www.beecrowd.com.br/judge/pt/problems/view/1074', 'EASY'),
        ('Estruturas de Dados', 'Fibonacci Number', 'https://leetcode.com/problems/fibonacci-number/', 'MEDIUM'),
        ('Estruturas de Dados', 'Valid Parentheses', 'https://leetcode.com/problems/valid-parentheses/', 'MEDIUM'),
        ('Estruturas de Dados', 'Binary Tree Inorder Traversal', 'https://leetcode.com/problems/binary-tree-inorder-traversal/', 'HARD'),
        ('Programação Competitiva: Base', 'A+B Problem', 'https://codeforces.com/problemset/problem/1/A', 'EASY')
) AS e(module_title, title, oj_url, difficulty)
    ON e.module_title = m.title;

-- Tags dos exercícios (join por título)
INSERT INTO exercise_tags (exercise_id, tag)
SELECT ex.id, t.tag
FROM exercises ex
JOIN (
    VALUES
        ('Soma de Dois Números', 'iniciante'),
        ('Soma de Dois Números', 'aritmetica'),
        ('Maior de Três Valores', 'condicional'),
        ('Par ou Ímpar', 'condicional'),
        ('Fibonacci Number', 'recursao'),
        ('Fibonacci Number', 'dp'),
        ('Valid Parentheses', 'pilha'),
        ('Binary Tree Inorder Traversal', 'arvore'),
        ('A+B Problem', 'implementacao')
) AS t(exercise_title, tag)
    ON t.exercise_title = ex.title;

-- Materiais extras
INSERT INTO extra_materials (module_id, title, url)
SELECT m.id, em.title, em.url
FROM modules m
JOIN (
    VALUES
        ('Fundamentos de Programação', 'PDF: Variáveis e Tipos', 'https://example.com/materiais/fundamentos-variaveis.pdf'),
        ('Fundamentos de Programação', 'Checklist de depuração', 'https://example.com/materiais/checklist-debug.md'),
        ('Estruturas de Dados', 'Slides de Pilhas e Filas', 'https://example.com/materiais/pilhas-filas-slides.pptx'),
        ('Estruturas de Dados', 'Resumo visual de árvores', 'https://example.com/materiais/arvores-resumo.png'),
        ('Programação Competitiva: Base', 'Template C++ para maratona', 'https://example.com/materiais/template-cp.cpp')
) AS em(module_title, title, url)
    ON em.module_title = m.title;

-- Posts
INSERT INTO posts (title, tag, slug, summary, cover_image_url, author_name, status, main_text)
VALUES
    (
        'Como estudar algoritmos de forma eficiente',
        'estudos',
        'como-estudar-algoritmos',
        'Estratégias práticas para evoluir em resolução de problemas sem perder consistência.',
        'https://example.com/images/post-algoritmos.jpg',
        'Equipe MU',
        'PUBLISHED',
        'Neste post, reunimos técnicas práticas para criar rotina, revisar conteúdos e medir evolução semanal.'
    ),
    (
        'Guia rápido de estruturas de dados',
        'estrutura-de-dados',
        'guia-rapido-estruturas-de-dados',
        'Resumo direto das estruturas mais cobradas em provas e entrevistas.',
        'https://example.com/images/post-estruturas.jpg',
        'Equipe MU',
        'DRAFT',
        'Estruturas de dados são fundamentais para soluções eficientes; aqui destacamos quando usar cada uma.'
    ),
    (
        'Primeiros passos em programação competitiva',
        'programacao-competitiva',
        'primeiros-passos-programacao-competitiva',
        'Como começar em contests, montar repertório e reduzir erros comuns.',
        'https://example.com/images/post-cp.jpg',
        'Equipe MU',
        'PUBLISHED',
        'Programação competitiva exige leitura, implementação sólida e prática deliberada com feedback rápido.'
    );

-- Seções dos posts (join por slug)
INSERT INTO post_sections (post_id, image_url, text, position)
SELECT p.id, s.image_url, s.text, s.position
FROM posts p
JOIN (
    VALUES
        ('como-estudar-algoritmos', 'https://example.com/images/sec-1.jpg', 'Defina um cronograma semanal com metas mensuráveis de problemas por tema.', 1),
        ('como-estudar-algoritmos', 'https://example.com/images/sec-2.jpg', 'Faça revisões espaçadas e registre padrões de erro para evitar repetição.', 2),
        ('guia-rapido-estruturas-de-dados', 'https://example.com/images/sec-3.jpg', 'Compare custo de acesso, inserção e remoção entre as estruturas mais comuns.', 1),
        ('primeiros-passos-programacao-competitiva', 'https://example.com/images/sec-4.jpg', 'Treine leitura rápida de enunciado e validação com casos de borda.', 1)
) AS s(slug, image_url, text, position)
    ON s.slug = p.slug;

-- Inscrições
INSERT INTO registrations (
    name, email, whatsapp, institution, campus, course, semester,
    how_did_you_hear, previous_experience, message, created_at
)
VALUES
    (
        'Ana Souza',
        'ana.souza@example.com',
        '+55 45 99999-1111',
        'UNIOESTE',
        'Foz do Iguaçu',
        'Ciência da Computação',
        '4º',
        'Instagram',
        'Já programei em Python e Java.',
        'Tenho interesse em melhorar para maratonas de programação.',
        NOW() - INTERVAL '3 days'
    ),
    (
        'Bruno Lima',
        'bruno.lima@example.com',
        '+55 45 98888-2222',
        'UNIOESTE',
        'Cascavel',
        'Sistemas de Informação',
        '2º',
        'Indicação de colegas',
        'Noções básicas de lógica e C.',
        'Busco reforçar fundamentos e praticar exercícios semanais.',
        NOW() - INTERVAL '2 days'
    ),
    (
        'Carla Nunes',
        'carla.nunes@example.com',
        '+55 45 97777-3333',
        'IFPR',
        'Foz do Iguaçu',
        'Análise e Desenvolvimento de Sistemas',
        '3º',
        'Site oficial',
        'Resolvo exercícios básicos no beecrowd.',
        'Quero ganhar consistência para problemas intermediários.',
        NOW() - INTERVAL '1 day'
    );

-- Contests
INSERT INTO contests (name, duration_minutes, start_datetime, is_team_based, codeforces_mirror_url)
VALUES
    ('MU Contest de Aquecimento', 120, NOW() + INTERVAL '7 days', TRUE, 'https://mirror.example.com/contest/mu-aquecimento'),
    ('MU Contest Individual #1', 150, NOW() + INTERVAL '14 days', FALSE, 'https://mirror.example.com/contest/mu-individual-1');

-- Times (inclui contest individual com team_name nulo, permitido pela V16)
INSERT INTO contest_teams (contest_id, team_name, coach_name, institution, reserve_name, is_cafe_com_leite)
SELECT c.id, t.team_name, t.coach_name, t.institution, t.reserve_name, t.is_cafe_com_leite
FROM contests c
JOIN (
    VALUES
        ('MU Contest de Aquecimento', 'Byte Benders', 'Prof. Carlos Mendes', 'UNIOESTE', 'João Pedro', FALSE),
        ('MU Contest de Aquecimento', 'Runtime Hunters', 'Profa. Fernanda Silva', 'UNIOESTE', NULL, TRUE),
        ('MU Contest Individual #1', NULL, NULL, 'UNIOESTE', NULL, FALSE)
) AS t(contest_name, team_name, coach_name, institution, reserve_name, is_cafe_com_leite)
    ON t.contest_name = c.name;

INSERT INTO contest_team_members (team_id, member_index, member_name)
SELECT ct.id, m.member_index, m.member_name
FROM contest_teams ct
JOIN (
    VALUES
        ('Byte Benders', 1, 'Ana Souza'),
        ('Byte Benders', 2, 'Bruno Lima'),
        ('Byte Benders', 3, 'Carla Nunes'),
        ('Runtime Hunters', 1, 'Diego Alves'),
        ('Runtime Hunters', 2, 'Eduarda Reis'),
        ('Runtime Hunters', 3, 'Felipe Rocha')
) AS m(team_name, member_index, member_name)
    ON m.team_name = ct.team_name;

COMMIT;
