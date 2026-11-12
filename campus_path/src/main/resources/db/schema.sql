-- =====================================================================
-- CampusPath - Script de criacao do banco de dados (DDL)
-- TCB 2026 - Linguagem de Programacao II - IFPR Cascavel
-- Baseado no DER da Entrega 1 e no padrao de codigo da disciplina:
--   create database if not exists / use
--   primary key(...) e constraint FK_... foreign key(...) references ...
--   DELIMITER $$ ... END$$ para triggers
-- Executar uma vez no MySQL Workbench conectado como root.
-- =====================================================================

create database if not exists campuspath;
use campuspath;

-- ---------------------------------------------------------------------
-- BLOCO
-- ---------------------------------------------------------------------
create table if not exists bloco(
   id_bloco int not null auto_increment,
   nome_bloco varchar(45) not null,
   primary key(id_bloco)
);

-- ---------------------------------------------------------------------
-- ANDAR (cada andar pertence a exatamente um bloco)
-- ---------------------------------------------------------------------
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

-- ---------------------------------------------------------------------
-- PONTO (SALA, PASSAGEM ou ACESSO; pertence a exatamente um andar)
-- ---------------------------------------------------------------------
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

-- ---------------------------------------------------------------------
-- TRECHO (bidirecional; extremidades em pontos distintos; distancia > 0)
-- ---------------------------------------------------------------------
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

-- ---------------------------------------------------------------------
-- PROFESSOR (login unico; localizacao atual opcional e restrita a SALA)
-- ---------------------------------------------------------------------
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

-- ---------------------------------------------------------------------
-- ADMINISTRADOR (email unico)
-- ---------------------------------------------------------------------
create table if not exists administrador(
   id_admin int not null auto_increment,
   nome_admin varchar(45) not null,
   email_admin varchar(45) not null,
   senha_admin varchar(255) not null,
   primary key(id_admin),
   constraint UN_Admin_Email unique(email_admin)
);

-- ---------------------------------------------------------------------
-- CONTROLE_SYNC (marca qual snapshot do repositorio ja foi aplicado)
-- ---------------------------------------------------------------------
create table if not exists controle_sync(
   id_controle int not null,
   snapshot_aplicado_em varchar(19),
   primary key(id_controle)
);

insert into controle_sync(id_controle, snapshot_aplicado_em)
select 1, null from dual
where not exists (select 1 from controle_sync where id_controle = 1);

-- ---------------------------------------------------------------------
-- Regra de negocio: a localizacao do professor so pode ser um ponto
-- do tipo SALA. O check nao consulta outra tabela, por isso o trigger.
-- ---------------------------------------------------------------------
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
