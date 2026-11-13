# Horta Comunitária

Projeto da AEP do 4º semestre de Engenharia de Software da UniCesumar. A aplicação ajuda a prefeitura a identificar terrenos baldios, administrar hortas comunitárias e acompanhar a produção e a distribuição de alimentos. A proposta se relaciona com os ODS 2, 11, 12 e 13.

## Segunda entrega

- [Código-fonte da aplicação](Aep4S/src/main)
- [Script de criação do banco PostgreSQL](database/criar-banco.sql)
- [Script de criação das tabelas PostgreSQL](database/schema.sql)
- [Diretrizes oficiais da AEP](docs/detalhesProjeto/Diretrizes%20Gerais%20da%20AEP%20%284%C2%BA%20Semestre%20de%202026%29.pdf)
- [Relatório da primeira entrega](docs/detalhesProjeto/AEP%20Horta%20-%20Finalizado.pdf)
- [Modelo de classes e relacionamentos da implementação](docs/modelo-implementacao.md)

### Como executar

Pré-requisitos: **JDK 21**, **Maven 3.9+** e **PostgreSQL** em execução. Confira o JDK utilizado com `java -version` e `mvn -version`.

No IntelliJ, abra `Aep4S/pom.xml` como projeto Maven e selecione o JDK 21 como SDK do projeto e como JRE do Maven.

1. No pgAdmin, abra a **Query Tool** do banco de manutenção `postgres` e execute [database/criar-banco.sql](database/criar-banco.sql) uma vez, com autocommit habilitado. Se `horta_comunitaria` já existir, pule esta etapa. Também é possível criar o banco com `createdb -U postgres horta_comunitaria`.
2. Na raiz do repositório, informe o usuário e a senha do seu PostgreSQL. Se o host, a porta ou o banco forem diferentes dos padrões, ajuste `HORTA_DB_URL`.

   **PowerShell**

   ```powershell
   $env:HORTA_DB_USER = 'postgres'
   $env:HORTA_DB_PASSWORD = 'SUA_SENHA_DO_POSTGRES'
   mvn -f Aep4S/pom.xml clean verify
   java -jar Aep4S/target/Aep4S-1.0.0.jar
   ```

   **Linux e macOS**

   ```bash
   export HORTA_DB_USER=postgres
   export HORTA_DB_PASSWORD='SUA_SENHA_DO_POSTGRES'
   mvn -f Aep4S/pom.xml clean verify
   java -jar Aep4S/target/Aep4S-1.0.0.jar
   ```

3. Abra [http://127.0.0.1:8080](http://127.0.0.1:8080). Para trocar a porta, defina `PORT` antes de iniciar a aplicação.

O Spring executa [database/schema.sql](database/schema.sql) na inicialização. As tabelas existentes são preservadas e o Hibernate valida sua compatibilidade com o modelo Java. O usuário do banco precisa poder criar as tabelas no primeiro uso. A URL padrão é `jdbc:postgresql://localhost:5432/horta_comunitaria`.

Para preparar as tabelas manualmente pelo pgAdmin, abra uma **nova Query Tool** no banco `horta_comunitaria` e execute [database/schema.sql](database/schema.sql). Para a demonstração, se a sua cópia incluir `database/seed.sql`, execute-o nessa mesma Query Tool depois de criar as tabelas. Atualize a página da aplicação para visualizar os dados fictícios.

Para testar a persistência, faça um cadastro pela aplicação, reinicie o sistema usando o mesmo banco e consulte o cadastro novamente.

Se o Java no Windows apresentar `Unable to establish loopback connection` com causa em `UnixDomainSockets.connect0`, execute com `java "-Djdk.net.unixdomain.tmpdir=$env:TEMP/sem-diretorio-de-socket" -jar Aep4S/target/Aep4S-1.0.0.jar`. O caminho informado deve ser inexistente para que o Java use o loopback TCP nesse ambiente.

### Roteiro de demonstração

1. Cadastre um bairro, uma prefeitura, um proprietário e um gestor vinculado à prefeitura. Para a demonstração, use nomes fictícios e sequências de 11 dígitos para CPF e 14 para CNPJ.
2. Cadastre um terreno disponível e legalmente apto. Associe o bairro, a prefeitura e o proprietário. Consulte e edite o terreno. Para demonstrar a exclusão, cadastre um segundo terreno e o exclua antes de vinculá-lo a uma horta ou contrato.
3. Registre o contrato com o proprietário vinculado ao terreno. Cadastre a horta nesse terreno com um gestor da mesma prefeitura. Cadastre uma família e seus integrantes.
4. Registre uma colheita e um registro de produção de adubo. Distribua parte da colheita a uma família da mesma horta. Uma entrega acima do saldo disponível deve ser recusada.
5. Abra **Terrenos por bairro**. O relatório contabiliza todos os terrenos cadastrados, os que seguem sem horta e os baldios disponíveis e legalmente aptos. Reinicie a aplicação e confirme que os dados seguem cadastrados.

### Regras e arquitetura

- O terreno pode começar sem proprietário identificado. O contrato usa o proprietário e a prefeitura vinculados ao terreno. O status de disponibilidade e a situação legal são cadastrados pelo operador.
- Cada terreno comporta uma horta, e o gestor deve estar contratado pela prefeitura do terreno. A exclusão de cadastros utilizados é bloqueada pelas chaves estrangeiras.
- Colheita, adubo e distribuição são registrados em quilogramas. O percentual previsto no contrato é um dado contratual. As entregas são registradas explicitamente e limitadas ao saldo da colheita.
- Spring MVC recebe as requisições, a camada de serviço valida os vínculos em transações e JPA/Hibernate persiste as entidades. O relatório consulta o PostgreSQL diretamente.
- `Pessoa` especializa-se em `Proprietario`, `GestorHorta` e `MembroFamilia`. `Horta` agrega seus registros de produção, que dependem dela no banco. `Producao` é abstrata e `calcularRendimento()` usa polimorfismo em `ProducaoHorta` e `Minhocario`.
- A interface roda no navegador e a aplicação escuta no endereço local por padrão. O ambiente acadêmico é de uso local e pressupõe um único perfil de operador, com dados fictícios.

### API

Os recursos `bairros`, `prefeituras`, `proprietarios`, `gestores`, `terrenos`, `contratos`, `hortas`, `familias`, `membros`, `colheitas`, `minhocarios` e `distribuicoes` oferecem `GET` e `POST` em `/api/{recurso}`, e `GET`, `PUT` e `DELETE` em `/api/{recurso}/{id}`. As consultas adicionais são `GET /api/hortas/{id}/producoes` e `GET /api/relatorios/terrenos-por-bairro`.

## Planejamento e requisitos da primeira entrega

### Registro de decisões

| Sprint / Data | Épico | User Story | Responsável |
|---|---|---|---|
| **Sprint 1**<br>05/09 – 19/09 | Terrenos e Estrutura Base | **COMO UM** funcionário da Prefeitura **EU QUERO** cadastrar, consultar, atualizar e excluir terrenos baldios, contendo informações como endereço, bairro, tamanho e situação, **PARA QUE** os terrenos disponíveis sejam mantidos de forma organizada. | Murilo |
| **Sprint 2**<br>20/09 – 04/10 | Cadastro de Hortas e Proprietários | **COMO UM** funcionário da Prefeitura **EU QUERO** cadastrar proprietários e relacioná-los aos respectivos terrenos, além de cadastrar e gerenciar as hortas comunitárias, **PARA QUE** os terrenos possam ser utilizados de forma organizada e as hortas sejam devidamente vinculadas aos locais disponíveis. | Victor |
| **Sprint 3**<br>05/10 – 19/10 | Famílias e Gestores | **COMO UM** funcionário da Prefeitura **EU QUERO** cadastrar as famílias beneficiadas e os gestores responsáveis pelas hortas, **PARA QUE** seja possível organizar os responsáveis pela manutenção das hortas e controlar as famílias atendidas pelo projeto. | Matheus |
| **Sprint 4**<br>20/10 – 03/11 | Produção e Distribuição | **COMO UM** gestor da horta **EU QUERO** registrar os produtos cultivados, as quantidades produzidas e a distribuição dos alimentos, **PARA QUE** seja possível acompanhar a produção e organizar a distribuição dos alimentos entre as famílias beneficiadas. | Murilo, Victor e Matheus |

---

### Lista de requisitos funcionais

- **RF01** — O sistema deve permitir o cadastro de terrenos baldios, incluindo endereço, área, status, situação legal e bairro.
- **RF02** — O sistema deve permitir o cadastro de proprietários e o vínculo destes aos terrenos que possuem.
- **RF03** — O sistema deve permitir o cadastro e a gestão de contratos entre a prefeitura e os proprietários, contendo valor de aluguel, percentual de produção repassado, incentivo fiscal e tipo de adesão (legal ou voluntária).
- **RF04** — O sistema deve permitir o cadastro de famílias e o vínculo destas a uma horta comunitária.
- **RF05** — O sistema deve permitir o cadastro de hortas comunitárias, vinculando-as a um terreno e a um gestor responsável.
- **RF06** — O sistema deve permitir o cadastro e a contratação de gestores de horta pela prefeitura.
- **RF07** — O sistema deve permitir o registro da produção de cada horta, com quantidade produzida, data de plantio e data de colheita.
- **RF08** — O sistema deve permitir o controle de minhocários vinculados às hortas, incluindo o registro da produção de adubo.
- **RF09** — O sistema deve permitir que a prefeitura realize o levantamento da quantidade de terrenos baldios por bairro.
