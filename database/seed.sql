-- Dados fictícios para a demonstração da Horta Comunitária.
-- No pgAdmin, abra a Query Tool do banco horta_comunitaria.
-- Execute schema.sql primeiro (ou inicie a aplicação para criar as tabelas).
-- Depois, abra e execute este arquivo. Atualize a página da aplicação.
-- A prefeitura de demonstração identifica a carga já aplicada. Uma nova
-- execução mantém os registros como estão, inclusive as edições feitas na tela.

BEGIN;

-- Evita duas cargas simultâneas no mesmo banco.
SELECT pg_advisory_xact_lock(744026, 1);

DO $$
DECLARE
    prefeitura_demo bigint;
    bairro_primavera bigint;
    bairro_esperanca bigint;
    proprietario_demo bigint;
    gestor_demo bigint;
    membro_demo bigint;
    terreno_demo bigint;
    horta_demo bigint;
    familia_demo bigint;
    colheita_demo bigint;
    adubo_demo bigint;
BEGIN
    INSERT INTO prefeitura (nome, cnpj)
    VALUES ('Prefeitura de Vila Verde (demonstração)', '00000000000000')
    ON CONFLICT (cnpj) DO NOTHING
    RETURNING id INTO prefeitura_demo;

    IF prefeitura_demo IS NULL THEN
        RAISE NOTICE 'A prefeitura de demonstração já existe. Nenhum cadastro foi alterado.';
        RETURN;
    END IF;

    INSERT INTO bairro (nome)
    VALUES ('Jardim Primavera (demo)'), ('Vila Esperança (demo)')
    ON CONFLICT (nome) DO NOTHING;

    SELECT id INTO STRICT bairro_primavera FROM bairro WHERE nome = 'Jardim Primavera (demo)';
    SELECT id INTO STRICT bairro_esperanca FROM bairro WHERE nome = 'Vila Esperança (demo)';

    INSERT INTO pessoa (nome, cpf, endereco)
    VALUES ('Lúcia Ribeiro (demo)', '00000000001', 'Rua das Sementes, 12')
    RETURNING id INTO proprietario_demo;

    INSERT INTO proprietario (id, telefone, email)
    VALUES (proprietario_demo, '00000000000', 'lucia@example.test');

    INSERT INTO pessoa (nome, cpf, endereco)
    VALUES ('Ana Carvalho (demo)', '00000000002', 'Rua das Sementes, 28')
    RETURNING id INTO gestor_demo;

    INSERT INTO gestor_horta (id, matricula, data_contratacao, ajuda_custo, prefeitura_id)
    VALUES (gestor_demo, 'DEMO-001', DATE '2026-08-15', 1500.00, prefeitura_demo);

    INSERT INTO terreno (endereco, area, status, situacao_legal, bairro_id, prefeitura_id, proprietario_id)
    VALUES ('Rua Ipê Amarelo, 120', 250.00, 'DISPONIVEL', 'APTO',
            bairro_primavera, prefeitura_demo, proprietario_demo)
    RETURNING id INTO terreno_demo;

    INSERT INTO terreno (endereco, area, status, situacao_legal, bairro_id, prefeitura_id)
    VALUES
        ('Rua das Acácias, 50', 180.00, 'DISPONIVEL', 'APTO', bairro_primavera, prefeitura_demo),
        ('Rua dos Girassóis, 81', 120.00, 'INDISPONIVEL', 'A_ANALISAR', bairro_esperanca, prefeitura_demo);

    INSERT INTO contrato (terreno_id, prefeitura_id, proprietario_id, data_inicio, data_fim,
                          valor_aluguel, percentual_producao, incentivo_fiscal, tipo_adesao)
    VALUES (terreno_demo, prefeitura_demo, proprietario_demo, DATE '2026-09-01', DATE '2027-08-31',
            350.00, 10.00, 'Incentivo ilustrativo para demonstração', 'VOLUNTARIA');

    INSERT INTO horta (nome, terreno_id, gestor_id)
    VALUES ('Horta Jardim Primavera (demo)', terreno_demo, gestor_demo)
    RETURNING id INTO horta_demo;

    INSERT INTO familia (nome, horta_id)
    VALUES ('Família Oliveira (demo)', horta_demo)
    RETURNING id INTO familia_demo;

    INSERT INTO pessoa (nome, cpf, endereco)
    VALUES ('João Oliveira (demo)', '00000000003', 'Rua das Acácias, 16')
    RETURNING id INTO membro_demo;

    INSERT INTO membro_familia (id, familia_id)
    VALUES (membro_demo, familia_demo);

    INSERT INTO producao (horta_id)
    VALUES (horta_demo)
    RETURNING id INTO colheita_demo;

    INSERT INTO producao_horta (id, produto, quantidade_produzida, data_plantio, data_colheita)
    VALUES (colheita_demo, 'Alface crespa', 24.00, DATE '2026-09-02', DATE '2026-10-16');

    INSERT INTO producao (horta_id)
    VALUES (horta_demo)
    RETURNING id INTO adubo_demo;

    INSERT INTO minhocario (id, quantidade_minhocas, quantidade_adubo, data_registro)
    VALUES (adubo_demo, 150, 6.50, DATE '2026-10-15');

    INSERT INTO distribuicao (colheita_id, familia_id, quantidade, data_entrega)
    VALUES (colheita_demo, familia_demo, 8.00, DATE '2026-10-17');
END;
$$;

COMMIT;
