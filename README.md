# CampusPath

### Sistema de Localização de Professores e Navegação no Campus

**Instituto Federal do Paraná – Campus Cascavel**
Curso Técnico Integrado de Informática
Linguagem de Programação II – 4.º Bimestre de 2026
Profª M.a. Roberta Vanessa Rojo Parcianello

**TCB – Trabalho de Conclusão do Bimestre**

| Item | Valor |
|---|---|
| Integrante | Marcelo Henrique Rocha Filho |
| RA | 20251CAS0020007 |
| Repositório | https://github.com/Mexpower2010/CampusPath_IFPR_TCA_2026 |
| Entrega documentada | Entrega 1 – Especificação e modelagem |
| Licença | MIT (ver `LICENSE`) |

Cascavel, PR — novembro de 2026

---

## Sumário

1. [Visão geral do sistema](#1-visão-geral-do-sistema)
2. [Requisitos funcionais (RF01–RF28)](#2-requisitos-funcionais-rf01rf28)
3. [Diagrama Entidade-Relacionamento (DER)](#3-diagrama-entidade-relacionamento-der)
4. [Relação de atributos das entidades](#4-relação-de-atributos-das-entidades)
5. [Estrutura do banco de dados](#5-estrutura-do-banco-de-dados)
6. [Configuração do projeto (pom.xml)](#6-configuração-do-projeto-pomxml)
7. [Estrutura de diretórios](#7-estrutura-de-diretórios)
8. [Como compilar e executar](#8-como-compilar-e-executar)
9. [Scripts SQL](#9-scripts-sql)
10. [Sincronização do estado entre computadores](#10-sincronização-do-estado-entre-computadores)
11. [Credenciais de teste](#11-credenciais-de-teste)
12. [Rastreabilidade: requisito → implementação](#12-rastreabilidade-requisito--implementação)
13. [Solução de problemas](#13-solução-de-problemas)
14. [Convenções de código](#14-convenções-de-código)
15. [Estado de verificação](#15-estado-de-verificação)

---

## 1. Visão geral do sistema

### 1.1 Finalidade

O CampusPath é um sistema de informação desenvolvido para o campus do Instituto Federal do Paraná –
Cascavel. Seu propósito é permitir que alunos encontrem professores que estejam atualmente no campus
e, quando desejado, calculem uma rota até a sala onde o professor se encontra. O sistema representa o
campus por meio de um grafo de pontos (salas, corredores e acessos) e de conexões entre eles, e aplica
o algoritmo de Dijkstra para encontrar o menor caminho entre dois pontos, respeitando as condições
físicas do percurso e as restrições escolhidas pelo usuário.

### 1.2 Usuários

O sistema possui três perfis de usuário.

| Perfil | Autenticação | Responsabilidade |
|---|---|---|
| **Aluno** | Não se autentica | Acessa o mapa e consulta a presença dos professores |
| **Professor** | Login e senha | Informa sua localização atual e publica uma mensagem destinada aos alunos |
| **Administrador** | E-mail e senha | Gerencia os cadastros, remove professores que deixam de utilizar o sistema e atualiza as condições dos trechos, como cobertura e interdição de percursos que utilizam escadas ou elevadores |

Todos os administradores possuem as mesmas permissões, sem divisão por tipo de administrador.

### 1.3 Funcionalidades

O sistema apresenta o mapa do campus como tela principal. As salas que possuem professores associados
exibem marcadores com as fotos de perfil dos professores presentes. Ao interagir com o marcador, o
aluno visualiza o nome do professor, sua mensagem e o horário em que a mensagem foi publicada ou
atualizada. A mensagem é escrita pelo professor e serve para explicar sua situação naquele momento —
por exemplo, se está disponível para atendimento ou se está em banca. Estar em uma sala, portanto, não
equivale automaticamente a estar disponível.

O aluno pode solicitar uma rota entre qualquer ponto de origem e uma sala de destino. O sistema aplica
o algoritmo de Dijkstra sobre os pontos e trechos cadastrados no banco de dados, destaca o percurso no
mapa e informa a distância total. O aluno pode escolher evitar escadas ou exigir que todo o percurso
seja coberto. O campus tem dois andares, e o mapa permite visualizar os blocos, os andares e as
ligações entre eles. Se as condições escolhidas pelo aluno impedirem qualquer caminho válido, o
sistema informa isso com uma mensagem clara.

O professor, após autenticar-se, informa a sala em que se encontra, publica ou atualiza sua mensagem
e, ao sair, remove sua presença. O horário associado à mensagem é registrado automaticamente pelo
sistema. O professor também pode atualizar sua foto de perfil. Cada professor tem no máximo uma
localização atual, e vários professores podem estar associados à mesma sala ao mesmo tempo, cada um
com sua própria mensagem individual.

O administrador realiza a manutenção dos cadastros de blocos, andares, pontos, trechos e professores.
Para acompanhar a rotatividade dos professores, pode cadastrar novos professores, alterar seus dados e
remover os que deixam de utilizar o sistema. Também atualiza as condições dos trechos, indicando se são
cobertos e se estão interditados. Quando um elevador ou uma escada está fora de funcionamento, o
administrador interdita os trechos correspondentes; quando o acesso é restabelecido, libera esses
trechos. O bloqueio de acesso a uma sala é representado pela interdição dos trechos que permitem
alcançá-la.

### 1.4 Regras de negócio

**Localização e mensagem**

- Um professor só pode informar uma sala como localização atual; ao informar outra sala, a localização
  anterior é substituída.
- Uma sala pode reunir vários professores ao mesmo tempo, mas cada um mantém sua mensagem
  independente.
- O horário da mensagem é registrado pelo sistema no momento da publicação ou atualização, e o
  professor não precisa informá-lo manualmente.
- A localização de um professor só pode ser associada a pontos do tipo `SALA`; pontos de passagem e
  acessos não são destinos válidos de presença.

**Cálculo de rota**

- A opção “evitar escadas” exclui do cálculo os trechos com escadas.
- O modo padrão permite escadas e não utiliza elevadores como caminho principal, pois o campus tem
  apenas dois andares e os elevadores são destinados prioritariamente a pessoas com deficiência.
- No modo "evitar escadas", elevadores em funcionamento podem ser utilizados.
- Trechos interditados nunca são incluídos no cálculo, independentemente do modo.
- Se não existir rota válida com as condições escolhidas, o sistema apresenta uma mensagem informativa
  e não desativa os filtros automaticamente.

**Integridade dos dados**

- Não é permitido cadastrar um trecho com as duas extremidades no mesmo ponto.
- A distância de um trecho deve ser maior que zero.
- O login do professor deve ser único no sistema.
- Não é permitido excluir um andar que ainda tenha pontos cadastrados.
- Não é permitido excluir um ponto que seja extremidade de trechos ou localização atual de algum
  professor.
- Cada andar pertence a exatamente um bloco, e cada ponto pertence a exatamente um andar.
- Blocos diferentes podem possuir andares no mesmo nível.
- Não é permitido excluir um bloco que ainda tenha andares cadastrados.
- Um trecho pode conectar pontos de andares ou blocos diferentes, desde que represente uma ligação
  física existente no campus.

**Operações administrativas**

- As operações administrativas exigem autenticação de administrador.
- Todos os administradores podem executar as mesmas funções administrativas.
- Ao remover um professor, o sistema exclui seu cadastro e as informações de localização e mensagem
  nele armazenadas, sem excluir a sala ou os trechos do mapa.
- Remover a presença, por outro lado, continua sendo uma ação do professor que não exclui seu perfil.
- A interdição de um trecho impede seu uso no cálculo de rotas, mas não exclui os pontos nem os
  professores associados às salas.

---

## 2. Requisitos funcionais (RF01–RF28)

Os requisitos funcionais a seguir estão numerados sequencialmente de RF01 a RF28 e agrupados por tema
para facilitar a leitura.

### 2.1 Mapa e Visualização

| RF | Requisito |
|---|---|
| **RF01** | O sistema deve exibir o mapa do campus como tela principal ao ser iniciado, sem exigir login do usuário. |
| **RF02** | O sistema deve permitir ao usuário selecionar o bloco e alternar entre os andares do campus no mapa. |
| **RF03** | O sistema deve exibir marcadores nas salas que possuem pelo menos um professor com localização atual informada, mostrando as fotos de perfil dos professores presentes. |
| **RF04** | Ao expandir um marcador de sala, o sistema deve exibir, para cada professor presente, seu nome, sua mensagem e o horário em que a mensagem foi publicada ou atualizada. |
| **RF05** | O sistema deve exibir mensagem informativa quando uma sala selecionada não possuir nenhum professor presente. |

### 2.2 Consulta de Professores

| RF | Requisito |
|---|---|
| **RF06** | O sistema deve permitir ao aluno consultar a lista de professores atualmente presentes no campus, com nome, sala e mensagem. |
| **RF07** | O sistema deve registrar automaticamente o horário de publicação ou atualização de cada mensagem do professor, sem exigir que o professor informe esse horário manualmente. |

### 2.3 Cálculo de Rota

| RF | Requisito |
|---|---|
| **RF08** | O sistema deve permitir ao aluno selecionar um ponto de origem e uma sala de destino para calcular uma rota. |
| **RF09** | O sistema deve calcular a rota de menor distância entre origem e destino utilizando o algoritmo de Dijkstra aplicado sobre os pontos e trechos cadastrados no banco de dados. |
| **RF10** | O sistema deve exibir a sequência de pontos do percurso e a distância total estimada em metros. |
| **RF11** | O sistema deve destacar visualmente o percurso calculado no mapa. |
| **RF12** | O sistema deve permitir ao aluno ativar a opção “evitar escadas”, excluindo completamente do cálculo os trechos que possuem escadas. |
| **RF13** | O sistema deve permitir ao aluno ativar a opção “somente coberto”, excluindo do cálculo os trechos descobertos. Essa opção pode ser combinada com “evitar escadas”. |
| **RF14** | Quando as condições escolhidas impedirem qualquer percurso válido entre origem e destino, o sistema deve exibir mensagem clara informando a ausência de rota e o motivo, sem desativar filtros automaticamente nem sugerir alternativas que violem as restrições escolhidas. |
| **RF15** | Quando a rota envolver dois andares ou blocos diferentes, o sistema deve representar a mudança de andar ou de bloco de forma compreensível ao aluno. |

### 2.4 Autenticação e Perfil do Professor

| RF | Requisito |
|---|---|
| **RF16** | O sistema deve permitir que o professor faça login informando seu login e senha. |
| **RF17** | O sistema deve exibir mensagem de erro clara em caso de credenciais inválidas. |
| **RF18** | O sistema deve permitir que o professor atualize sua foto de perfil. |
| **RF19** | O sistema deve permitir que o professor altere seu nome exibido. |

### 2.5 Localização e Mensagem do Professor

| RF | Requisito |
|---|---|
| **RF20** | O sistema deve permitir que o professor autenticado informe a sala em que se encontra, associando sua localização atual a um ponto do tipo SALA. |
| **RF21** | O sistema deve permitir que o professor autenticado publique ou atualize sua mensagem, registrando automaticamente o horário da atualização. |
| **RF22** | O sistema deve permitir que o professor autenticado altere sua localização para outra sala, substituindo a localização anterior. |
| **RF23** | O sistema deve permitir que o professor autenticado remova sua presença ao sair, limpando sua localização atual e sua mensagem sem excluir seu perfil. |

### 2.6 Administração (CRUD)

| RF | Requisito |
|---|---|
| **RF24** | O sistema deve oferecer ao administrador autenticado operações de cadastro, consulta, alteração e exclusão para blocos, andares, pontos do mapa, trechos e professores, respeitando as restrições de integridade referencial definidas no modelo de dados. |
| **RF25** | O sistema deve permitir que o administrador faça login informando seu e-mail e senha, com mensagem de erro clara em caso de credenciais inválidas. |
| **RF26** | O sistema deve conceder as mesmas permissões a todos os administradores autenticados, sem distinção por tipo de administrador. |
| **RF27** | O sistema deve permitir ao administrador remover professores que deixam de utilizar o sistema, excluindo os respectivos cadastros e suas informações de presença e mensagem, sem excluir as salas ou os trechos do mapa. |
| **RF28** | O sistema deve permitir ao administrador atualizar as condições dos trechos, alterando sua cobertura e sua interdição ou liberação, inclusive nos percursos que utilizam escadas e elevadores. Trechos interditados não devem ser utilizados no cálculo de rotas. |

---

## 3. Diagrama Entidade-Relacionamento (DER)

O modelo de dados do CampusPath é composto por seis entidades: **BLOCO, ANDAR, PONTO, TRECHO,
PROFESSOR e ADMINISTRADOR**. BLOCO organiza os andares do campus, e ADMINISTRADOR representa os
usuários responsáveis pela manutenção dos cadastros, pela remoção de professores e pela atualização
das condições dos trechos.

As duas ligações entre PONTO e TRECHO representam os dois papéis das extremidades de um mesmo trecho.
As letras **A** e **B** não indicam origem e destino fixos: todo trecho é **bidirecional** no cálculo
de rotas.

```mermaid
erDiagram
    BLOCO ||--o{ ANDAR : "contém"
    ANDAR ||--o{ PONTO : "contém"
    PONTO ||--o{ TRECHO : "extremidade A"
    PONTO ||--o{ TRECHO : "extremidade B"
    PONTO ||--o{ PROFESSOR : "localização atual (opcional)"

    BLOCO {
        INTEGER id_bloco PK
        VARCHAR nome_bloco
    }
    ANDAR {
        INTEGER id_andar PK
        VARCHAR nome
        INTEGER nivel
        VARCHAR imagem_mapa
        INTEGER id_bloco FK
    }
    PONTO {
        INTEGER id_ponto PK
        INTEGER id_andar FK
        VARCHAR nome
        VARCHAR tipo
        DECIMAL coordenada_x
        DECIMAL coordenada_y
    }
    TRECHO {
        INTEGER id_trecho PK
        INTEGER id_ponto_a FK
        INTEGER id_ponto_b FK
        DECIMAL distancia_metros
        BOOLEAN possui_escadas
        BOOLEAN usa_elevador
        BOOLEAN coberto
        BOOLEAN interditado
    }
    PROFESSOR {
        INTEGER id_professor PK
        VARCHAR nome
        VARCHAR login UK
        VARCHAR senha_hash
        VARCHAR foto_perfil
        INTEGER id_ponto_atual FK
        TEXT mensagem
        TIMESTAMP mensagem_enviada_em
    }
    ADMINISTRADOR {
        INTEGER id_admin PK
        VARCHAR nome_admin
        VARCHAR email_admin UK
        VARCHAR senha_admin
    }
```

*Figura 1 – Diagrama Entidade-Relacionamento do CampusPath.*

---

## 4. Relação de atributos das entidades

As tabelas a seguir listam os atributos de cada entidade. Na coluna **Chave**, `PK` identifica a chave
primária, `FK` identifica a chave estrangeira (com a entidade referenciada) e `UNIQUE` indica valor
que não pode se repetir.

### Tabela 1 – Relacionamentos e cardinalidades

| Relacionamento | Cardinalidade | Descrição |
|---|---|---|
| BLOCO → ANDAR | 1:N | Um bloco contém vários andares; cada andar pertence a exatamente um bloco. |
| ANDAR → PONTO | 1:N | Um andar contém vários pontos; cada ponto pertence a exatamente um andar. |
| PONTO → TRECHO (extremidade A) | 1:N | Um ponto pode ser a extremidade A de vários trechos; cada trecho tem uma extremidade A. |
| PONTO → TRECHO (extremidade B) | 1:N | Um ponto pode ser a extremidade B de vários trechos; cada trecho tem uma extremidade B. |
| PONTO (SALA) → PROFESSOR | 1:N | Uma sala pode ser a localização atual de vários professores; cada professor tem no máximo uma sala atual (FK opcional). |

### Tabela 2 – Entidade: BLOCO

| Atributo | Tipo | Chave | Descrição |
|---|---|---|---|
| `id_bloco` | INTEGER | PK | Identificador único do bloco, gerado automaticamente. |
| `nome_bloco` | VARCHAR(45) | — | Nome do bloco, utilizado para identificá-lo na seleção dos mapas. |

### Tabela 3 – Entidade: ANDAR

| Atributo | Tipo | Chave | Descrição |
|---|---|---|---|
| `id_andar` | INTEGER | PK | Identificador único do andar, gerado automaticamente. |
| `nome` | VARCHAR(50) | — | Nome descritivo do andar (ex.: “Térreo”, “Primeiro Andar”). |
| `nivel` | INTEGER | — | Número do nível (0 para térreo, 1 para primeiro andar). |
| `imagem_mapa` | VARCHAR(255) | — | Caminho ou referência para a imagem do mapa deste andar. |
| `id_bloco` | INTEGER | FK → BLOCO | Bloco ao qual o andar pertence. Referência obrigatória. |

### Tabela 4 – Entidade: PONTO

| Atributo | Tipo | Chave | Descrição |
|---|---|---|---|
| `id_ponto` | INTEGER | PK | Identificador único do ponto, gerado automaticamente. |
| `id_andar` | INTEGER | FK → ANDAR | Andar ao qual o ponto pertence. |
| `nome` | VARCHAR(100) | — | Nome do ponto (ex.: “Sala 201”, “Corredor A”). |
| `tipo` | VARCHAR(20) | — | Categoria do ponto: SALA, PASSAGEM ou ACESSO. |
| `coordenada_x` | DECIMAL(8,1) | — | Posição horizontal no mapa do andar. |
| `coordenada_y` | DECIMAL(8,1) | — | Posição vertical no mapa do andar. |

### Tabela 5 – Entidade: TRECHO

| Atributo | Tipo | Chave | Descrição |
|---|---|---|---|
| `id_trecho` | INTEGER | PK | Identificador único do trecho, gerado automaticamente. |
| `id_ponto_a` | INTEGER | FK → PONTO | Primeira extremidade do trecho (bidirecional). |
| `id_ponto_b` | INTEGER | FK → PONTO | Segunda extremidade do trecho (bidirecional). |
| `distancia_metros` | DECIMAL(8,1) | — | Distância em metros entre os dois pontos; deve ser maior que zero. |
| `possui_escadas` | BOOLEAN | — | Indica se o trecho passa por escadas. |
| `usa_elevador` | BOOLEAN | — | Indica se o trecho utiliza o elevador. |
| `coberto` | BOOLEAN | — | Indica se o trecho é coberto (protegido de chuva). |
| `interditado` | BOOLEAN | — | Indica se o trecho está interditado e não pode ser usado. |

### Tabela 6 – Entidade: PROFESSOR

| Atributo | Tipo | Chave | Descrição |
|---|---|---|---|
| `id_professor` | INTEGER | PK | Identificador único do professor, gerado automaticamente. |
| `nome` | VARCHAR(120) | — | Nome completo do professor, exibido no mapa e no perfil. |
| `login` | VARCHAR(60) | UNIQUE | Identificador de acesso único do professor. |
| `senha_hash` | VARCHAR(255) | — | Hash da senha do professor; a senha não é armazenada em texto puro. |
| `foto_perfil` | VARCHAR(255) | — | Caminho ou referência para a imagem de perfil do professor. |
| `id_ponto_atual` | INTEGER | FK → PONTO (opcional) | Sala atual do professor; nulo quando não há localização informada. Restrito a pontos do tipo SALA. |
| `mensagem` | TEXT | — | Mensagem atual do professor para os alunos. |
| `mensagem_enviada_em` | TIMESTAMP | — | Horário registrado automaticamente ao publicar ou atualizar a mensagem. |

### Tabela 7 – Entidade: ADMINISTRADOR

| Atributo | Tipo | Chave | Descrição |
|---|---|---|---|
| `id_admin` | INTEGER | PK | Identificador único do administrador, gerado automaticamente. |
| `nome_admin` | VARCHAR(45) | — | Nome do administrador, utilizado para identificá-lo no sistema. |
| `email_admin` | VARCHAR(45) | UNIQUE | E-mail utilizado para autenticação. Não pode se repetir entre os administradores. |
| `senha_admin` | VARCHAR(255) | — | Hash da senha do administrador; a senha não é armazenada em texto puro. |

---

## 5. Estrutura do banco de dados

O schema implementa exatamente as seis entidades e os atributos das Tabelas 2 a 7. O script completo
está em `campus_path/src/main/resources/db/schema.sql`.

### 5.1 DDL

```sql
create database if not exists campuspath;
use campuspath;

create table if not exists bloco(
   id_bloco int not null auto_increment,
   nome_bloco varchar(45) not null,
   primary key(id_bloco)
);

create table if not exists andar(
   id_andar int not null auto_increment,
   nome varchar(50) not null,
   nivel int not null,
   imagem_mapa varchar(255),
   id_bloco int not null,
   primary key(id_andar),
   constraint FK_Andar_Bloco foreign key(id_bloco)
      references bloco(id_bloco)
);

create table if not exists ponto(
   id_ponto int not null auto_increment,
   id_andar int not null,
   nome varchar(100) not null,
   tipo varchar(20) not null,
   coordenada_x decimal(8,1),
   coordenada_y decimal(8,1),
   primary key(id_ponto),
   constraint FK_Ponto_Andar foreign key(id_andar)
      references andar(id_andar),
   constraint CK_Ponto_Tipo check(tipo in ('SALA','PASSAGEM','ACESSO'))
);

create table if not exists trecho(
   id_trecho int not null auto_increment,
   id_ponto_a int not null,
   id_ponto_b int not null,
   distancia_metros decimal(8,1) not null,
   possui_escadas boolean not null default false,
   usa_elevador boolean not null default false,
   coberto boolean not null default false,
   interditado boolean not null default false,
   primary key(id_trecho),
   constraint FK_Trecho_PontoA foreign key(id_ponto_a)
      references ponto(id_ponto),
   constraint FK_Trecho_PontoB foreign key(id_ponto_b)
      references ponto(id_ponto),
   constraint CK_Trecho_Extremidades check(id_ponto_a <> id_ponto_b),
   constraint CK_Trecho_Distancia check(distancia_metros > 0)
);

create table if not exists professor(
   id_professor int not null auto_increment,
   nome varchar(120) not null,
   login varchar(60) not null,
   senha_hash varchar(255) not null,
   foto_perfil varchar(255),
   id_ponto_atual int,
   mensagem text,
   mensagem_enviada_em timestamp,
   primary key(id_professor),
   constraint UN_Professor_Login unique(login),
   constraint FK_Professor_Ponto foreign key(id_ponto_atual)
      references ponto(id_ponto)
);

create table if not exists administrador(
   id_admin int not null auto_increment,
   nome_admin varchar(45) not null,
   email_admin varchar(45) not null,
   senha_admin varchar(255) not null,
   primary key(id_admin),
   constraint UN_Admin_Email unique(email_admin)
);
```

### 5.2 Tabela de controle da aplicação

Além das seis entidades, existe uma tabela de apoio que **não faz parte do DER**: `controle_sync`
guarda o timestamp do último snapshot importado (ver seção 10).

```sql
create table if not exists controle_sync(
   id_controle int not null,
   snapshot_aplicado_em varchar(19),
   primary key(id_controle)
);
```

### 5.3 Triggers

A regra "a localização do professor só pode ser um ponto do tipo SALA" não pode ser expressa em um
`CHECK`, porque exige consultar outra tabela. Por isso é garantida por dois triggers:

```sql
DELIMITER $$

drop trigger if exists Tgr_Professor_PontoSala_Insert$$
drop trigger if exists Tgr_Professor_PontoSala_Update$$

create trigger Tgr_Professor_PontoSala_Insert before insert
on professor
for each row
begin
   if new.id_ponto_atual is not null then
      if (select tipo from ponto where id_ponto = new.id_ponto_atual) <> 'SALA' then
         signal sqlstate '45000'
            set message_text = 'A localizacao do professor deve ser um ponto do tipo SALA.';
      end if;
   end if;
end$$

create trigger Tgr_Professor_PontoSala_Update before update
on professor
for each row
begin
   if new.id_ponto_atual is not null then
      if (select tipo from ponto where id_ponto = new.id_ponto_atual) <> 'SALA' then
         signal sqlstate '45000'
            set message_text = 'A localizacao do professor deve ser um ponto do tipo SALA.';
      end if;
   end if;
end$$

DELIMITER ;
```

### 5.4 Onde cada regra de negócio é garantida

| Regra (seção 1.4) | Garantida por | Objeto |
|---|---|---|
| Trecho não pode ter as duas extremidades no mesmo ponto | `CHECK` | `CK_Trecho_Extremidades` |
| Distância do trecho maior que zero | `CHECK` | `CK_Trecho_Distancia` |
| Tipo de ponto restrito a SALA, PASSAGEM ou ACESSO | `CHECK` | `CK_Ponto_Tipo` |
| Login do professor único | `UNIQUE` | `UN_Professor_Login` |
| E-mail do administrador único | `UNIQUE` | `UN_Admin_Email` |
| Localização do professor só em ponto SALA | Trigger | `Tgr_Professor_PontoSala_Insert/Update` |
| Não excluir bloco com andares | FK | `FK_Andar_Bloco` |
| Não excluir andar com pontos | FK | `FK_Ponto_Andar` |
| Não excluir ponto usado em trecho | FK | `FK_Trecho_PontoA`, `FK_Trecho_PontoB` |
| Não excluir ponto que é localização de professor | FK | `FK_Professor_Ponto` |
| Trecho interditado nunca entra na rota | Aplicação | `RotaService.filtrar()` |
| "Evitar escadas" libera elevador | Aplicação | `RotaService.filtrar()` |
| Sem rota: mensagem informativa, filtros mantidos | Aplicação | `RotaService.motivoSemRota()` |
| Senha nunca em texto puro | Aplicação | `Senha.hash()` (SHA-256) |
| Horário da mensagem registrado pelo sistema | Aplicação | `ProfessorDAO.publicarMensagem()` |

As FKs usam o comportamento padrão do InnoDB, `ON DELETE RESTRICT`: o banco recusa apagar um registro
que ainda é referenciado, o que realiza as regras de integridade sem código adicional.

---

## 6. Configuração do projeto (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>ifpr.edu</groupId>
    <artifactId>campus_path</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
    </properties>
    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
    </dependencies>
</project>
```

| Item | Valor |
|---|---|
| `groupId` | `ifpr.edu` |
| `artifactId` | `campus_path` |
| `version` | `1.0-SNAPSHOT` |
| Java | 21 (`maven.compiler.source` e `target`) |
| Driver JDBC | `mysql:mysql-connector-java:8.0.33` |
| Empacotamento | padrão (`jar`) |

O projeto tem **uma única dependência externa**: o driver JDBC do MySQL. Não há ORM, framework de
injeção, biblioteca de terceiros para o algoritmo de rota, nem suíte de testes declarada. O Dijkstra é
implementado à mão com `java.util.PriorityQueue`.

Como o POM não configura `maven-jar-plugin` com `Main-Class` nem `maven-shade-plugin`, o `mvn package`
produz um JAR comum, sem o driver embutido. A execução é feita com `-cp` explícito (seção 8).

---

## 7. Estrutura de diretórios

```
CampusPath_IFPR_TCA_2026/
├── README.md                      este arquivo
├── LICENSE                        MIT
├── .gitignore                     ignora target/, db.properties e arquivos de IDE
└── campus_path/                   projeto Maven
    ├── pom.xml
    ├── dados/
    │   └── snapshot.sql           estado do banco versionado no Git (seção 10)
    ├── src/main/java/ifpr/edu/
    │   ├── Main.java              ponto de entrada: abre a janela
    │   ├── view/
    │   │   ├── JanelaPrincipal.java  JFrame com a navegação entre as três telas
    │   │   ├── PainelMapa.java       mapa, seleção de origem/destino e rota
    │   │   ├── PainelProfessor.java  login e ações do professor
    │   │   ├── PainelAdmin.java      CRUD das seis entidades
    │   │   ├── ComponenteMapa.java   desenho do mapa em Graphics2D
    │   │   ├── Formulario.java       construtor de formulários em GridBagLayout
    │   │   └── Estilo.java           cores e fontes do tema escuro
    │   ├── controller/
    │   │   ├── MapaController.java      consultas do mapa e cálculo de rota
    │   │   ├── ProfessorController.java autenticação e ações do professor
    │   │   └── AdminController.java     CRUD e exportação do snapshot
    │   ├── dao/
    │   │   ├── BlocoDAO.java
    │   │   ├── AndarDAO.java
    │   │   ├── PontoDAO.java
    │   │   ├── TrechoDAO.java
    │   │   ├── ProfessorDAO.java
    │   │   ├── AdministradorDAO.java
    │   │   └── SnapshotService.java
    │   ├── model/
    │   │   ├── Bloco.java
    │   │   ├── Andar.java
    │   │   ├── Ponto.java
    │   │   ├── TipoPonto.java     enum SALA, PASSAGEM, ACESSO
    │   │   ├── Trecho.java
    │   │   ├── Professor.java
    │   │   └── Administrador.java
    │   ├── service/
    │   │   ├── RotaService.java   Dijkstra + filtros
    │   │   ├── Rota.java          resultado imutável
    │   │   └── AutenticacaoService.java
    │   ├── db/
    │   │   ├── Conexao.java       lê db.properties/env e devolve Connection
    │   │   └── PersistenciaException.java
    │   └── util/
    │       └── Senha.java         SHA-256
    └── src/main/resources/
        ├── db.properties.example  modelo de configuração (versionado)
        └── db/
            ├── schema.sql         DDL: 6 tabelas + controle_sync + 2 triggers
            ├── dados_iniciais.sql dados de teste
            ├── procedures.sql     4 procedures de apoio
            ├── consultas.sql      consultas e joins de apoio
            └── usuario.sql        usuário de aplicação com permissão mínima
```

Total: **31 arquivos Java**. Não existe `src/test`.

### 7.1 Camadas (MVC)

```
view        JanelaPrincipal, PainelMapa, PainelProfessor, PainelAdmin, ComponenteMapa
controller  MapaController, ProfessorController, AdminController
service     RotaService (Dijkstra), Rota, AutenticacaoService
dao         acesso a dados com PreparedStatement
model       objetos de domínio (POJOs)
db          Conexao e PersistenciaException
util        Senha (SHA-256)
```

Regras de dependência: `view` nunca contém SQL nem abre `Connection`; `view` conversa apenas com
`controller`; `controller` orquestra `dao` e `service`; `dao` é a única camada que fala SQL; `model`
não depende de ninguém. Toda falha de banco chega à `view` como `PersistenciaException` com mensagem
em português, exibida em `JOptionPane` ou na barra de status.

---

## 8. Como compilar e executar

O sistema é uma aplicação desktop Swing: rodar o `Main` abre a janela, sem servidor, sem navegador e
sem porta de rede.

### 8.1 Passo a passo no Windows (do zero até a janela abrir)

Este roteiro foi executado nesta máquina e é o caminho mais curto. Abra o **PowerShell** e siga na
ordem. Todos os comandos partem da raiz do repositório.

**Passo 0 — Conferir o que já existe**

```powershell
cd A:\CampusPath_IFPR_TCA_2026
java -version
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" --version
```

É preciso JDK 17 ou superior (aqui roda o 25) e o MySQL Server instalado. O Maven **não** é
necessário: os passos abaixo compilam com `javac`.

**Passo 1 — Testar o login do root**

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p -e "select version();"
```

Digite a senha do root definida na instalação. Se der `Access denied`, a senha está errada e nada
adiante vai funcionar — redefina pelo MySQL Installer (Reconfigure) ou pelo método `--init-file` da
documentação oficial antes de continuar.

**Passo 2 — Criar o banco e carregar os dados**

```powershell
$M = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
& $M -u root -p --default-character-set=utf8mb4 < campus_path\src\main\resources\db\schema.sql
& $M -u root -p --default-character-set=utf8mb4 < campus_path\src\main\resources\db\dados_iniciais.sql
& $M -u root -p --default-character-set=utf8mb4 < campus_path\src\main\resources\db\procedures.sql
```

Não passe o nome do banco no comando: o `schema.sql` já executa `create database if not exists` e
`use campuspath`. O `procedures.sql` é opcional para a janela, mas faz parte da entrega.

Confira:

```powershell
& $M -u root -p -e "use campuspath; select count(*) as pontos from ponto; select count(*) as trechos from trecho;"
```

Esperado: `15` e `15`.

**Passo 3 — Criar o usuário da aplicação**

```powershell
& $M -u root -p -e "CREATE USER IF NOT EXISTS 'campuspath'@'localhost' IDENTIFIED BY 'campuspath'; GRANT SELECT, INSERT, UPDATE, DELETE ON campuspath.* TO 'campuspath'@'localhost'; FLUSH PRIVILEGES;"
```

O `usuario.sql` do repositório faz o mesmo, mas com o placeholder `TROCAR_ESTA_SENHA`. Se preferir
usá-lo, troque o placeholder antes de executar e use a mesma senha no passo 4.

**Passo 4 — Criar o `db.properties`**

Crie `campus_path\src\main\resources\db.properties` com este conteúdo:

```properties
db.url=jdbc:mysql://localhost:3306/campuspath?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8
db.user=campuspath
db.password=campuspath
```

O arquivo está no `.gitignore` e não deve ser commitado. Se preferir usar o root, troque para
`db.user=root` e `db.password=<senha do root>`.

| Parâmetro da URL | Motivo |
|---|---|
| `useSSL=false` | Conexão local, sem certificado |
| `allowPublicKeyRetrieval=true` | Sem isso o MySQL 8 recusa login com `caching_sha2_password` |
| `serverTimezone=America/Sao_Paulo` | Evita erro de fuso em `mensagem_enviada_em` (TIMESTAMP) |
| `characterEncoding=UTF-8` | Preserva acentos nas mensagens dos professores |

Alternativamente, use as variáveis de ambiente `CAMPUSPATH_DB_URL`, `CAMPUSPATH_DB_USER` e
`CAMPUSPATH_DB_PASSWORD`, que têm precedência sobre o arquivo.

**Passo 5 — Localizar o driver JDBC**

```powershell
Get-ChildItem "$env:USERPROFILE\.m2\repository" -Recurse -Filter "mysql-connector*.jar" |
  Select-Object -ExpandProperty FullName
```

Use o caminho que aparecer. Nesta máquina é:

```
C:\Users\Marcelo\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar
```

Se não houver nenhum jar, baixe o `mysql-connector-j` no site oficial e aponte o `$CP` para ele.

**Passo 6 — Compilar**

```powershell
cd A:\CampusPath_IFPR_TCA_2026\campus_path
$CP = "C:\Users\Marcelo\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar"
New-Item -ItemType Directory -Force target\classes | Out-Null
javac -encoding UTF-8 -d target\classes -cp $CP (Get-ChildItem -Recurse src\main\java -Filter *.java).FullName
Copy-Item -Recurse -Force src\main\resources\* target\classes\
```

O `Copy-Item` é obrigatório: sem ele o `db.properties` e os `.sql` não chegam ao classpath e a
aplicação responde `Configuracao do banco ausente`. Esperado: 42 arquivos `.class`, sem erro.

**Passo 7 — Abrir a janela**

```powershell
java -cp "target\classes;$CP" ifpr.edu.Main
```

A janela `CampusPath - IFPR Cascavel` abre em tela cheia lógica de 1180x760. Não há parâmetro de
porta nem endereço para digitar.

**Passo 8 — Usar o sistema**

| Tela | Como chegar | O que faz |
|---|---|---|
| Mapa e rotas | botão **Mapa e rotas** | escolhe bloco e andar, mostra o mapa, calcula rota |
| Professor | botão **Professor** | login, sala atual, mensagem, nome e foto |
| Administração | botão **Administracao** | CRUD das seis entidades e exportação do snapshot |

Credenciais:

| Perfil | Usuário | Senha |
|---|---|---|
| Administrador | `admin@campuspath.ifpr.edu.br` | `admin123` |
| Professor | `roberta.parcianello` | `123456` |
| Professor | `fabio.moreira` | `123456` |
| Professor | `ana.souza` | `123456` |

**Passo 9 — Fechar**

Feche a janela pelo **X** ou com `Alt+F4`. O processo termina junto.

### 8.2 Com Maven (se você instalar o Maven)

```powershell
cd campus_path
mvn clean package
java -cp "target\classes;$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar" ifpr.edu.Main
```

O `pom.xml` declara `mysql:mysql-connector-java:8.0.33`; se o Maven baixar essa versão, ajuste o
`$CP` para o caminho correspondente. No Linux, troque `;` por `:` no classpath.

### 8.3 Telas do sistema

**Mapa e rotas.** Barra superior com bloco, andar, `Evitar escadas`, `Somente coberto` e
`Atualizar`. O mapa desenha os pontos do andar e os trechos: verde para trecho coberto, tracejado
vermelho para interditado, azul grosso para o caminho calculado. Clicar em um ponto define o destino;
a origem sai do combo. À direita ficam o resultado da rota (distância e passos) e a lista de
professores presentes. O rodapé do mapa informa quantos pontos o andar tem.

**Professor.** Login com login e senha. Depois do login: sala atual e horário da última mensagem,
combo de salas com `Atualizar localizacao` e `Remover presenca`, área de mensagem com
`Publicar mensagem`, e nome e foto com `Salvar perfil`. O botão `Sair` volta ao login.

**Administração.** Login com e-mail e senha. Depois do login, um seletor de entidade
(`Blocos`, `Andares`, `Pontos`, `Trechos`, `Professores`, `Administradores`) troca a tabela e o
formulário. Selecionar uma linha carrega o formulário para edição; `Novo` limpa; `Salvar` insere ou
altera; `Excluir` pede confirmação. `Exportar snapshot` grava `dados/snapshot.sql`.

### 8.4 Fluxo de inicialização

`Main.main()` → `SwingUtilities.invokeLater` → `SnapshotService.sincronizar()` (importa
`dados/snapshot.sql` se o banco estiver vazio) → `JanelaPrincipal`. Se o banco não estiver
configurado, aparece um `JOptionPane` com a mensagem e a janela não abre.

---

## 9. Scripts SQL

Executar nesta ordem, conectado como `root`:

| Ordem | Arquivo | O que faz |
|---|---|---|
| 1 | `db/schema.sql` | Cria o banco `campuspath`, as 6 tabelas, `controle_sync` e os 2 triggers |
| 2 | `db/dados_iniciais.sql` | Insere 2 blocos, 3 andares, 15 pontos, 15 trechos, 1 administrador e 3 professores |
| 3 | `db/procedures.sql` | Cria 4 procedures de apoio (opcional) |
| 4 | `db/consultas.sql` | Apenas consultas de exemplo, não altera dados (opcional) |
| 5 | `db/usuario.sql` | Cria o usuário `campuspath@localhost` com permissão mínima (troque a senha placeholder antes) |

### 9.1 Procedures

| Procedure | Assinatura | Uso |
|---|---|---|
| `Listar_Professores_Presentes()` | sem parâmetros | Lista nome, sala, mensagem e horário |
| `Contar_Pontos_Andar(in ida, out total)` | IN/OUT | Conta pontos de um andar |
| `Remover_Presenca(in idp)` | IN | Anula sala, mensagem e horário |
| `Atualizar_Localizacao(in idp, in idpt)` | IN | Troca a sala do professor |

Todas usam `drop procedure if exists` antes do `create`, então podem ser reexecutadas.

### 9.2 Consultas de apoio

`consultas.sql` reproduz os joins vistos em aula: produto cartesiano equivalente a `inner join`,
`left join` de salas com professores, `right join` de professores com salas, `union` como
`full outer join`, e as consultas de trechos liberados, sem escada e cobertos.

---

## 10. Sincronização do estado entre computadores

O banco MySQL **não** é versionado. O que vai para o Git é `campus_path/dados/snapshot.sql`, um
arquivo texto com o estado completo das seis tabelas.

### 10.1 Como funciona

1. Ao usar **Exportar snapshot** na tela de administração, `SnapshotService.exportar()` grava:
   - cabeçalho com `-- Gerado em yyyy-MM-dd HH:mm:ss`;
   - `delete from` das seis tabelas, na ordem que respeita as FKs;
   - `insert into` de blocos, andares, pontos, trechos, administradores e professores;
   - `update controle_sync set snapshot_aplicado_em = '<timestamp>'`.
2. Antes de gravar, `verificado()` compara `count(*)` de cada tabela com o número de linhas geradas.
   Se divergir, lança exceção em vez de gravar um snapshot incompleto.
3. Ao iniciar, `SnapshotService.sincronizar()` lê o `-- Gerado em` do arquivo e compara com
   `controle_sync.snapshot_aplicado_em`. Se forem iguais, nada acontece. Se forem diferentes, o
   arquivo é aplicado.
4. A aplicação é feita em **transação** (`setAutoCommit(false)` + `commit`/`rollback`): ou todas as
   linhas entram, ou nenhuma.
5. Se o snapshot referenciar pontos inexistentes (erro 1452), a mensagem orienta executar o
   `schema.sql` antes.

### 10.2 Uso em outro computador

```bash
# 1. criar a estrutura
mysql -u root -p < campus_path/src/main/resources/db/schema.sql
# 2. clonar o repositório (o snapshot vem junto)
git clone https://github.com/Mexpower2010/CampusPath_IFPR_TCA_2026
# 3. configurar db.properties e executar
java -cp "target/classes;$CP" ifpr.edu.Main
# saída esperada: "Estado sincronizado a partir de dados/snapshot.sql"
```

### 10.3 Limitações

- A sincronização é por **commit**, não em tempo real. Se duas máquinas alterarem o banco sem
  commitar entre si, o último commit vence.
- O snapshot carrega os hashes de senha. Como o repositório é público, as credenciais de teste são
  conhecidas — aceitável para trabalho acadêmico, não para uso real.
- O arquivo é gravado no diretório de trabalho atual (`dados/snapshot.sql`). Execute a aplicação a
  partir da pasta `campus_path` para que o caminho coincida com o versionado.

---

## 11. Credenciais de teste

| Perfil | Acesso | Senha |
|---|---|---|
| Administrador | `admin@campuspath.ifpr.edu.br` | `admin123` |
| Professora | `roberta.parcianello` | `123456` |
| Professor | `fabio.moreira` | `123456` |
| Professora | `ana.souza` | `123456` |

As senhas são gravadas como SHA-256 hexadecimal em `senha_hash` (professor) e `senha_admin`
(administrador), conforme a Tabela 6 e a Tabela 7.

> SHA-256 sem salt atende ao escopo da disciplina, mas não é adequado para produção — o correto seria
> `bcrypt`, `scrypt` ou `PBKDF2` com salt por usuário.

---

## 12. Rastreabilidade: requisito → implementação

| RF | Implementação |
|---|---|
| RF01 | `JanelaPrincipal` → botão **Mapa e rotas** → `PainelMapa` |
| RF02 | `PainelMapa.carregarBlocos()` e `carregarAndares()` com `BlocoDAO.listar()` e `AndarDAO.listarPorBloco()` |
| RF03 | `ComponenteMapa` desenha anel verde quando `ProfessorDAO.listarPresentes()` tem professor no ponto |
| RF04 | `PainelMapa.selecionarDestino()` ao clicar no ponto |
| RF05 | `PainelMapa` mostra `sem sala` quando `Professor.getNomeSala()` é nulo |
| RF06 | `PainelMapa.carregarProfessores()` → `ProfessorDAO.listarPresentes()` |
| RF07 | `ProfessorDAO.publicarMensagem()` grava `LocalDateTime.now()` |
| RF08 | `PainelMapa.calcularRota()` |
| RF09 | `RotaService.calcular()` — Dijkstra com `PriorityQueue` |
| RF10 | `Rota.getPassos()` e `Rota.getDistanciaTotal()` |
| RF11 | `ComponenteMapa.setCaminho()` desenha o caminho em azul grosso |
| RF12 | `RotaService.filtrar()` descarta `possui_escadas = true` |
| RF13 | `RotaService.filtrar()` descarta `coberto = false`, combinável com RF12 |
| RF14 | `RotaService.motivoSemRota()` |
| RF15 | `RotaService.calcular()` insere "Troca de andar" e "Troca de bloco" |
| RF16 | `PainelProfessor.entrar()` → `AutenticacaoService.autenticarProfessor()` |
| RF17 | `AutenticacaoService` → "Login ou senha invalidos." |
| RF18 | `PainelProfessor.salvarPerfil()` → `ProfessorDAO.atualizarFoto()` |
| RF19 | `PainelProfessor.salvarPerfil()` → `ProfessorDAO.atualizarNome()` |
| RF20 | `PainelProfessor.atualizarLocalizacao()` valida `Ponto.isSala()` |
| RF21 | `PainelProfessor.publicarMensagem()` → `ProfessorDAO.publicarMensagem()` |
| RF22 | `ProfessorDAO.atualizarLocalizacao()` |
| RF23 | `ProfessorDAO.removerPresenca()` |
| RF24 | `PainelAdmin` → abas `Blocos`, `Andares`, `Pontos`, `Trechos`, `Professores` |
| RF25 | `PainelAdmin.entrar()` → `AutenticacaoService.autenticarAdministrador()` |
| RF26 | Não há coluna de perfil ou nível; qualquer linha em `administrador` tem acesso total |
| RF27 | `ProfessorDAO.excluir()` remove só a linha de `professor` |
| RF28 | Aba `Trechos` → `TrechoDAO.atualizarCondicoes()` |

Na janela Swing, cada RF tem o equivalente direto usando os mesmos DAOs e serviços: RF01–RF15 em
`PainelMapa` (`MapaController`); RF16–RF23 em `PainelProfessor` (`ProfessorController`); RF24–RF28 em
`PainelAdmin` (`AdminController`), com a edição de `coberto` e `interditado` na aba `Trechos` para
RF28.

### 12.1 Algoritmo de rota

`RotaService.calcular(idOrigem, idDestino, evitarEscadas, somenteCoberto)`:

1. Carrega todos os pontos em um `Map<Integer, Ponto>` e valida origem e destino.
2. Carrega todos os trechos e aplica os filtros **antes** de montar o grafo:
   - descarta `interditado = true` sempre;
   - se `evitarEscadas`, descarta `possui_escadas = true`;
   - se `somenteCoberto`, descarta `coberto = false`.
3. Monta lista de adjacência `Map<Integer, List<Trecho>>` — cada trecho entra nas duas extremidades,
   o que realiza a bidirecionalidade descrita na seção 3.
4. Dijkstra com `PriorityQueue` ordenada pela distância acumulada, `Map` de distâncias inicializado
   com `Double.MAX_VALUE` e `Map` de anteriores para reconstruir o caminho.
5. Se o destino continuar em `MAX_VALUE`, lança `PersistenciaException` com o texto de
   `motivoSemRota()`, que informa quantos trechos estão interditados, quantos foram excluídos por
   escada e por serem descobertos, quantos sobraram, e se existiria caminho sem os filtros.
6. Reconstrói o caminho e monta os passos, inserindo "Troca de andar" (com indicação de escada ou
   elevador) e "Troca de bloco" quando aplicável.

Complexidade: O((V + E) log V). `Rota` é imutável, com listas `Collections.unmodifiableList`.

---

## 13. Solução de problemas

| Sintoma | Causa | Solução |
|---|---|---|
| `Unknown database 'campuspath'` | Schema não criado, ou `Default Schema` preenchido no Workbench | Rodar `schema.sql`; deixar `Default Schema` vazio |
| `Access denied for user 'root'@'localhost'` | Senha errada, ou usuário do Windows usado como usuário do MySQL | Usar `root` com a senha da instalação; o MySQL não usa o usuário do SO |
| `Access denied for user 'campuspath'` | `usuario.sql` não executado, ou senha divergente | Executar `usuario.sql` e alinhar a senha |
| `Configuracao do banco ausente` | Nem `db.properties` nem variáveis de ambiente definidos, ou o `db.properties` não foi copiado para `target\classes` | Criar o arquivo conforme o passo 4 e repetir o `Copy-Item` do passo 6 |
| `Public Key Retrieval is not allowed` | Falta `allowPublicKeyRetrieval=true` | Ajustar a URL |
| `The server time zone value ... is unrecognized` | Falta `serverTimezone` | Ajustar a URL |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Driver fora do classpath | Incluir o JAR no `-cp` |
| `ClassNotFoundException: ifpr.edu.Main` | `target/classes` fora do classpath | Recompilar e conferir o `-cp` |
| `Communications link failure` | Servidor parado ou porta diferente | Iniciar o serviço e conferir a porta 3306 |
| `Cannot delete or update a parent row` | Exclusão bloqueada por FK | Excluir primeiro os registros dependentes |
| `A localizacao do professor deve ser um ponto do tipo SALA` | Trigger barrou localização em PASSAGEM/ACESSO | Escolher um ponto `SALA` |
| `Nao existe rota ...` | Filtros eliminaram todos os caminhos | Ler as contagens da mensagem; desativar o filtro que bloqueia |
| Snapshot não é aplicado | `-- Gerado em` igual ao `controle_sync`, ou arquivo ausente | Conferir o arquivo e a coluna `snapshot_aplicado_em` |
| Acentuação quebrada na janela | Fonte do sistema sem suporte | Usar `-Dfile.encoding=UTF-8` ao iniciar |
| `Address already in use` | Não se aplica: a aplicação não abre porta de rede | — |
| Janela não abre e aparece `Configuracao do banco ausente` | `db.properties` não existe ou não foi copiado para `target\classes` | Criar o arquivo conforme o passo 4 e repetir o `Copy-Item` do passo 6 |
| `Login ou senha invalidos` | Senha errada ou hash do seed não aplicado | Usar as credenciais da seção 11 |
| `A localizacao do professor deve ser um ponto do tipo SALA` | Sala escolhida não é do tipo SALA | Escolher uma sala na lista |
| Tabela do administrador vazia | Sessão não iniciada ou banco sem dados | Fazer login e conferir o passo 2 |

---

## 14. Convenções de código

- Pacote base `ifpr.edu`; subpacotes por camada (`view`, `controller`, `db`, `model`, `dao`, `service`, `util`).
- Classes em `PascalCase`, métodos e variáveis em `camelCase`, constantes em `UPPER_SNAKE_CASE`.
- SQL em minúsculas dentro das strings Java, com `?` para parâmetros — nunca concatenação de valores.
- Todo `PreparedStatement`, `ResultSet` e `Connection` em `try-with-resources`.
- Erros de banco convertidos por `PersistenciaException.de(e, contexto)`, que traduz:
  - 1048 → campo obrigatório vazio;
  - 1062 → login ou e-mail duplicado;
  - 1451 → registro em uso por outro cadastro;
  - 1452 → registro referenciado inexistente;
  - 1644 → mensagem do trigger.
- Nenhuma classe de `view` ou `controller` contém SQL; nenhuma classe de `dao` imprime no console.
- `view` trata `RuntimeException` e mostra a mensagem em `JOptionPane` ou na barra de status; nunca
  deixa a exceção subir até o `EventDispatchThread`.
- `Conexao` lê o `db.properties` uma única vez e guarda o resultado em cache estático; sem isso o
  arquivo seria relido do classpath a cada consulta.
- `ComponenteMapa` calcula os limites das coordenadas uma vez por troca de andar, em vez de a cada
  repaint, e desenha com um único `Graphics2D` derivado.
- `AbaCrud` recarrega a tabela apenas depois de uma operação bem-sucedida, evitando consultas
  repetidas ao banco a cada clique.
- Textos de interface sem acento no código-fonte, para não depender do encoding do terminal.

---

## 15. Estado de verificação

O que foi efetivamente executado e conferido:

| Verificação | Resultado |
|---|---|
| Compilação com `javac -encoding UTF-8` (JDK 25, driver 8.3.0) | 31 arquivos, 42 classes, sem erro |
| `schema.sql` + `dados_iniciais.sql` em MySQL 8.0.46 (Windows, porta 3306) | 6 tabelas, 2 triggers, 15 pontos, 15 trechos, 3 professores, 1 administrador |
| `procedures.sql` | 4 procedures criadas; `Listar_Professores_Presentes` devolve 3 linhas |
| Usuário `campuspath@localhost` com SELECT/INSERT/UPDATE/DELETE | Criado e usado pela aplicação |
| `db.properties` lido do classpath | Conexão OK sem variáveis de ambiente |
| Reexecução de `schema.sql` e `procedures.sql` | Sem erro (`if not exists` / `drop ... if exists`) |
| Constraints negativas (extremidades iguais, distância 0, tipo inválido, login duplicado) | Todas rejeitadas |
| FK negativas (excluir bloco com andares, ponto usado em trecho) | Todas rejeitadas |
| Trigger de localização em ponto não-SALA | Rejeitado com SQLSTATE 45000 |
| Janela abre com `java ifpr.edu.Main` | Título `CampusPath - IFPR Cascavel`, 1180x760, sem exceção no console |
| Tela Mapa e rotas | 6 pontos no Terreiro do Bloco A, trechos desenhados, 3 professores listados |
| Tela Professor: login `roberta.parcianello` / `123456` | OK; painel mostra sala 101, mensagem e perfil |
| Tela Administração: login `admin@...` / `admin123` | OK; tabela de blocos com 2 linhas |
| CRUD pela janela: inserir `Bloco C` | Linha apareceu na tabela com id 4 e status `Operacao concluida.` |
| Rota com "evitar escadas" entre andares | 36,0 m pelo elevador, com troca de andar indicada |
| Rota impossível com filtros | Mensagem com contagens, filtros preservados |
| Snapshot: exportar, apagar banco, reimportar | Estado restaurado, incluindo trecho interditado |
| Snapshot em banco vazio de outro "computador" | `Estado sincronizado`, 15 trechos e 3 professores |

O que **não** foi verificado neste ambiente:

- `mvn clean package` — Maven não está instalado; a compilação foi feita com `javac` e o classpath
  montado à mão, exatamente como no passo 6 da seção 8.1.
- Testes automatizados — não existe `src/test` e o `pom.xml` não declara JUnit.
- Demais abas do administrador (`Andares`, `Pontos`, `Trechos`, `Professores`, `Administradores`) —
  a inserção foi exercitada apenas em `Blocos`; as outras usam o mesmo `AbaCrud`.
- Redimensionamento da janela em telas menores que 1180x760.

---

## Licença

MIT. Ver `LICENSE` — Copyright (c) 2026 Marcelo Henrique Rocha Filho.
