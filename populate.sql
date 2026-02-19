-- Script de população inicial do banco de dados
-- Compatível com o schema após as migrations V1..V10

BEGIN;

-- Limpa dados existentes para permitir reexecução do script
TRUNCATE TABLE
    exercise_tags,
    extra_materials,
    exercises,
    post_sections,
    posts,
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
INSERT INTO lessons (module_id, title, video_url, position, slug, summary)
VALUES
    (1, 'Variáveis e Tipos', 'https://www.youtube.com/watch?v=video001', 1, 'variaveis-e-tipos', 'Aprenda a declarar variáveis e entender tipos primitivos.'),
    (1, 'Condicionais', 'https://www.youtube.com/watch?v=video002', 2, 'condicionais', 'Introdução ao uso de if, else e operadores lógicos.'),
    (2, 'Listas e Vetores', 'https://www.youtube.com/watch?v=video003', 1, 'listas-e-vetores', 'Como armazenar coleções de dados e percorrê-las.'),
    (2, 'Pilhas e Filas', 'https://www.youtube.com/watch?v=video004', 2, 'pilhas-e-filas', 'Entenda estruturas LIFO e FIFO e casos de uso.');

-- Exercícios
INSERT INTO exercises (module_id, lesson_id, title, oj_name, oj_url, difficulty)
VALUES
    (1, 1, 'Soma de Dois Números', 'Beecrowd', 'https://www.beecrowd.com.br/judge/pt/problems/view/1003', 'EASY'),
    (1, 2, 'Maior de Três Valores', 'Beecrowd', 'https://www.beecrowd.com.br/judge/pt/problems/view/1013', 'EASY'),
    (2, 3, 'Sequência de Fibonacci', 'LeetCode', 'https://leetcode.com/problems/fibonacci-number/', 'MEDIUM'),
    (2, 4, 'Valid Parentheses', 'LeetCode', 'https://leetcode.com/problems/valid-parentheses/', 'MEDIUM');

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
INSERT INTO extra_materials (lesson_id, type, url)
VALUES
    (1, 'PDF', 'https://example.com/materiais/variaveis.pdf'),
    (2, 'Artigo', 'https://example.com/materiais/condicionais.html'),
    (3, 'Slides', 'https://example.com/materiais/listas.pptx'),
    (4, 'Vídeo complementar', 'https://example.com/materiais/pilhas-filas.mp4');

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

COMMIT;
