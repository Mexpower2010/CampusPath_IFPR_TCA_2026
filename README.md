# CampusPath

Sistema de localização de professores e navegação no campus do IFPR Cascavel.
TCB 2026 - Linguagem de Programação II - Profª Roberta Vanessa Rojo Parcianello.

## Requisitos atendidos

| Grupo | Requisitos |
|---|---|
| Mapa e visualização | RF01 a RF05 |
| Consulta de professores | RF06, RF07 |
| Cálculo de rota (Dijkstra) | RF08 a RF15 |
| Autenticação e perfil | RF16 a RF19 |
| Localização e mensagem | RF20 a RF23 |
| Administração e CRUD | RF24 a RF28 |

## Tecnologia

- Java 17, Maven
- JDBC puro com MySQL 8 (`com.mysql:mysql-connector-j` 8.3.0)
- JUnit 5
- Interface de console

## Estrutura

```
campus_path/
  src/main/java/ifpr/edu/
    db/        Conexao, PersistenciaException
    model/     Bloco, Andar, Ponto, TipoPonto, Trecho, Professor, Administrador
    dao/       BlocoDAO, AndarDAO, PontoDAO, TrechoDAO, ProfessorDAO,
               AdministradorDAO, SnapshotService
    service/   RotaService (Dijkstra), Rota, AutenticacaoService
    ui/        MenuPrincipal, MenuAluno, MenuProfessor, MenuAdmin, Entrada
    util/      Senha (SHA-256)
    Main.java
  src/main/resources/
    db.properties.example
    db/schema.sql          DDL das 6 tabelas + triggers
    db/dados_iniciais.sql  dados de teste
    db/procedures.sql      procedures de apoio
    db/consultas.sql       consultas de apoio
    db/usuario.sql         usuario da aplicacao
  dados/snapshot.sql       estado atual do banco (versionado no Git)
```

## Como executar

1. Instalar o MySQL Server e criar o banco, uma vez por computador:

```bash
mysql -u root -p < campus_path/src/main/resources/db/schema.sql
mysql -u root -p < campus_path/src/main/resources/db/dados_iniciais.sql
```

2. Configurar a conexão: copiar `campus_path/src/main/resources/db.properties.example`
   para `campus_path/src/main/resources/db.properties` e preencher usuário e senha.
   Alternativamente, definir as variáveis de ambiente `CAMPUSPATH_DB_URL`,
   `CAMPUSPATH_DB_USER` e `CAMPUSPATH_DB_PASSWORD`.

3. Compilar e executar:

```bash
cd campus_path
mvn clean package
java -jar target/campus_path-1.0-SNAPSHOT.jar
```

Sem Maven instalado, compilar direto com o driver no classpath:

```bash
javac -encoding UTF-8 -d target/classes -cp <caminho>/mysql-connector-j-8.3.0.jar \
  $(find src/main/java -name "*.java")
java -cp "target/classes;<caminho>/mysql-connector-j-8.3.0.jar" ifpr.edu.Main
```

## Credenciais de teste

| Perfil | Acesso | Senha |
|---|---|---|
| Administrador | admin@campuspath.ifpr.edu.br | admin123 |
| Professora | roberta.parcianello | 123456 |
| Professor | fabio.moreira | 123456 |
| Professora | ana.souza | 123456 |

As senhas são gravadas como hash SHA-256 em `senha_hash` e `senha_admin`.

## Estado do banco entre computadores

O banco MySQL não é versionado. O que vai para o Git é `campus_path/dados/snapshot.sql`,
gerado pela aplicação ao sair pela opção `0` do menu principal.

- O arquivo contém o estado completo: blocos, andares, pontos, trechos,
  administradores e professores.
- Ao iniciar, a aplicação compara o horário de geração do snapshot com o valor
  gravado em `controle_sync.snapshot_aplicado_em`. Se forem diferentes, o snapshot
  é aplicado dentro de uma transação (tudo ou nada).
- Em outro computador: criar o banco com `schema.sql`, copiar o repositório e abrir
  a aplicação. O estado é restaurado automaticamente.
- A exportação confere a contagem de registros de cada tabela e falha com mensagem
  clara se algo não for gravado.

## Regras de negócio garantidas no banco

- Trecho não pode ter as duas extremidades no mesmo ponto e distância maior que zero.
- Ponto só pode ser SALA, PASSAGEM ou ACESSO.
- Login de professor e e-mail de administrador são únicos.
- Localização do professor restrita a ponto do tipo SALA (triggers de insert e update).
- Exclusão bloqueada por integridade referencial: bloco com andares, andar com pontos,
  ponto usado em trecho ou como localização de professor.
- Trechos interditados nunca entram no cálculo de rota.
