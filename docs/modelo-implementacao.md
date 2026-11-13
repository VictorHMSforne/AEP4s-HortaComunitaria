# Modelo da implementação

Os diagramas representam o código e o esquema PostgreSQL da segunda entrega. Os diagramas originais e o relatório de planejamento estão em [diagramas](diagramas) e [detalhesProjeto](detalhesProjeto).

## Classes

```mermaid
classDiagram
    class Registro {
        <<abstract>>
        +getId() Long
    }
    class Pessoa {
        <<abstract>>
        -String nome
        -String cpf
        -String endereco
    }
    class Producao {
        <<abstract>>
        -Long hortaId
        +calcularRendimento() BigDecimal
    }
    class ProducaoHorta {
        -String produto
        -BigDecimal quantidadeProduzida
        -LocalDate dataPlantio
        -LocalDate dataColheita
        +calcularRendimento() BigDecimal
    }
    class Minhocario {
        -Integer quantidadeMinhocas
        -BigDecimal quantidadeAdubo
        -LocalDate dataRegistro
        +calcularRendimento() BigDecimal
    }
    Registro <|-- Pessoa
    Registro <|-- Producao
    Registro <|-- Prefeitura
    Registro <|-- Bairro
    Registro <|-- Terreno
    Registro <|-- Contrato
    Registro <|-- Horta
    Registro <|-- Familia
    Registro <|-- Distribuicao
    Pessoa <|-- Proprietario
    Pessoa <|-- GestorHorta
    Pessoa <|-- MembroFamilia
    Producao <|-- ProducaoHorta
    Producao <|-- Minhocario
    Bairro "1" -- "0..*" Terreno
    Prefeitura "1" -- "0..*" Terreno
    Proprietario "0..1" -- "0..*" Terreno
    Terreno "1" -- "0..*" Contrato
    Prefeitura "1" -- "0..*" Contrato
    Proprietario "1" -- "0..*" Contrato
    Prefeitura "1" -- "0..*" GestorHorta
    Terreno "1" -- "0..1" Horta
    GestorHorta "1" -- "0..*" Horta
    Horta "1" -- "0..*" Familia
    Familia "1" -- "0..*" MembroFamilia
    Horta "1" *-- "0..*" Producao
    ProducaoHorta "1" -- "0..*" Distribuicao
    Familia "1" -- "0..*" Distribuicao
```

## Banco de dados

```mermaid
erDiagram
    BAIRRO ||--o{ TERRENO : localiza
    PREFEITURA ||--o{ TERRENO : administra
    PROPRIETARIO o|--o{ TERRENO : possui
    TERRENO ||--o{ CONTRATO : fundamenta
    PREFEITURA ||--o{ CONTRATO : firma
    PROPRIETARIO ||--o{ CONTRATO : assina
    PESSOA ||--o| PROPRIETARIO : especializa
    PESSOA ||--o| GESTOR_HORTA : especializa
    PESSOA ||--o| MEMBRO_FAMILIA : especializa
    PREFEITURA ||--o{ GESTOR_HORTA : contrata
    TERRENO ||--o| HORTA : recebe
    GESTOR_HORTA ||--o{ HORTA : gerencia
    HORTA ||--o{ FAMILIA : atende
    FAMILIA ||--o{ MEMBRO_FAMILIA : inclui
    HORTA ||--o{ PRODUCAO : registra
    PRODUCAO ||--o| PRODUCAO_HORTA : especializa
    PRODUCAO ||--o| MINHOCARIO : especializa
    PRODUCAO_HORTA ||--o{ DISTRIBUICAO : fornece
    FAMILIA ||--o{ DISTRIBUICAO : recebe
    TERRENO {
        bigint id PK
        bigint bairro_id FK
        bigint prefeitura_id FK
        bigint proprietario_id FK
        varchar endereco
        numeric area
        varchar status
        varchar situacao_legal
    }
    CONTRATO {
        bigint id PK
        bigint terreno_id FK
        bigint prefeitura_id FK
        bigint proprietario_id FK
        date data_inicio
        date data_fim
        numeric valor_aluguel
        numeric percentual_producao
        varchar incentivo_fiscal
        varchar tipo_adesao
    }
    HORTA {
        bigint id PK
        bigint terreno_id FK
        bigint gestor_id FK
        varchar nome
    }
    PRODUCAO {
        bigint id PK
        bigint horta_id FK
    }
    PRODUCAO_HORTA {
        bigint id PK, FK
        varchar produto
        numeric quantidade_produzida
        date data_plantio
        date data_colheita
    }
    MINHOCARIO {
        bigint id PK, FK
        integer quantidade_minhocas
        numeric quantidade_adubo
        date data_registro
    }
    DISTRIBUICAO {
        bigint id PK
        bigint colheita_id FK
        bigint familia_id FK
        numeric quantidade
        date data_entrega
    }
```

As referências de prefeitura e proprietário em `contrato` são garantidas pela chave estrangeira composta para `terreno`. A definição de todas as colunas e restrições está no [script SQL](../database/schema.sql).
