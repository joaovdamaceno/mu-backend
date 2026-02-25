-- Script de população inicial do banco de dados
-- Compatível com migrations V1..V17
-- Considera o vínculo de materiais extras diretamente com módulos (V15)
-- e team_name opcional para contest individual (V16), além da simplificação de campos de módulos (V17)

BEGIN;

-- Limpa dados existentes para permitir reexecução do script
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
    ('Fundamentos de Programação', 'Conceitos básicos de lógica e algoritmos.', TRUE),
    ('Estruturas de Dados', 'Introdução a listas, pilhas, filas e árvores.', TRUE);

-- Aulas
INSERT INTO lessons (module_id, title, video_url, position)
VALUES
    (1, 'Variáveis e Tipos', 'https://www.youtube.com/watch?v=video001', 1),
    (1, 'Condicionais', 'https://www.youtube.com/watch?v=video002', 2),
    (2, 'Listas e Vetores', 'https://www.youtube.com/watch?v=video003', 1),
    (2, 'Pilhas e Filas', 'https://www.youtube.com/watch?v=video004', 2);

-- Exercícios
INSERT INTO exercises (module_id, title, oj_url, difficulty)
VALUES
    (1, 'Soma de Dois Números', 'https://www.beecrowd.com.br/judge/pt/problems/view/1003', 'EASY'),
    (1, 'Maior de Três Valores', 'https://www.beecrowd.com.br/judge/pt/problems/view/1013', 'EASY'),
    (2, 'Sequência de Fibonacci', 'https://leetcode.com/problems/fibonacci-number/', 'MEDIUM'),
    (2, 'Valid Parentheses', 'https://leetcode.com/problems/valid-parentheses/', 'MEDIUM');

-- Tags dos exercícios
INSERT INTO exercise_tags (exercise_id, tag)
VALUES
    (1, 'iniciante'),
    (1, 'aritmetica'),
    (2, 'condicional'),
    (3, 'recursao'),
    (3, 'dp'),
    (4, 'pilha');

-- Materiais extras
INSERT INTO extra_materials (module_id, title, url)
VALUES
    (1, 'PDF', 'https://example.com/materiais/variaveis.pdf'),
    (1, 'Artigo', 'https://example.com/materiais/condicionais.html'),
    (2, 'Slides', 'https://example.com/materiais/listas.pptx'),
    (2, 'Vídeo complementar', 'https://example.com/materiais/pilhas-filas.mp4');

-- Posts do blog
INSERT INTO posts (title, tag, slug, summary, cover_image_url, author_name, status, main_text)
VALUES
    (
        'Como estudar algoritmos de forma eficiente',
        'estudos',
        'como-estudar-algoritmos',
        'Estratégias práticas para evoluir em resolução de problemas.',
        'https://example.com/images/post-algoritmos.jpg',
        'Equipe MU',
        'PUBLISHED',
        'Neste post, reunimos técnicas para estudar algoritmos com consistência...'
    ),
    (
        'Guia rápido de estruturas de dados',
        'estrutura-de-dados',
        'guia-rapido-estruturas-de-dados',
        'Resumo objetivo das principais estruturas usadas em entrevistas e competições.',
        'https://example.com/images/post-estruturas.jpg',
        'Equipe MU',
        'DRAFT',
        'As estruturas de dados são a base para organizar informações com eficiência...'
    );

-- Seções dos posts
INSERT INTO post_sections (post_id, image_url, text, position)
VALUES
    (1, 'https://example.com/images/sec-1.jpg', 'Defina um cronograma semanal e revise conteúdo anterior.', 1),
    (1, 'https://example.com/images/sec-2.jpg', 'Resolva problemas de dificuldade progressiva e registre aprendizados.', 2),
    (2, 'https://example.com/images/sec-3.jpg', 'Compare arrays, linked lists, stacks, queues e árvores.', 1);

-- Inscrições
INSERT INTO registrations (
    name,
    email,
    whatsapp,
    institution,
    campus,
    course,
    semester,
    how_did_you_hear,
    previous_experience,
    message
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
        'Tenho interesse em melhorar para maratonas de programação.'
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
        'Busco reforçar fundamentos e praticar exercícios semanais.'
    );


-- Contests
INSERT INTO contests (name, duration_minutes, start_datetime, is_team_based, codeforces_mirror_url)
VALUES
    ('MU Contest de Aquecimento', 120, NOW() + INTERVAL '7 days', TRUE, 'https://mirror.example.com/contest/mu-aquecimento'),
    ('MU Contest Individual #1', 150, NOW() + INTERVAL '14 days', FALSE, 'https://mirror.example.com/contest/mu-individual-1');

-- Times inscritos em contest por equipes
INSERT INTO contest_teams (contest_id, team_name, coach_name, institution, reserve_name, is_cafe_com_leite)
VALUES
    (1, 'Byte Benders', 'Prof. Carlos Mendes', 'UNIOESTE', 'João Pedro', FALSE),
    (1, 'Runtime Hunters', 'Profa. Fernanda Silva', 'UNIOESTE', NULL, TRUE);

INSERT INTO contest_team_members (team_id, member_index, member_name)
VALUES
    (1, 1, 'Ana Souza'),
    (1, 2, 'Bruno Lima'),
    (1, 3, 'Carla Nunes'),
    (2, 1, 'Diego Alves'),
    (2, 2, 'Eduarda Reis'),
    (2, 3, 'Felipe Rocha');

COMMIT;
