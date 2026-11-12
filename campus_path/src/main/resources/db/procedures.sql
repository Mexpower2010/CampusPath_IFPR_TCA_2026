-- =====================================================================
-- CampusPath - Procedures no padrao da disciplina
-- Executar depois do schema.sql e do dados_iniciais.sql.
-- Segue o modelo das aulas: DELIMITER $$ ... END$$, parametros IN e OUT.
-- =====================================================================

use campuspath;

-- ---------------------------------------------------------------------
-- Lista os professores presentes no campus (RF06)
-- ---------------------------------------------------------------------
DELIMITER $$
drop procedure if exists Listar_Professores_Presentes$$
create procedure Listar_Professores_Presentes()
begin
   select p.nome, pt.nome as sala, p.mensagem, p.mensagem_enviada_em
   from professor p
   inner join ponto pt on pt.id_ponto = p.id_ponto_atual
   where p.id_ponto_atual is not null
   order by pt.nome, p.nome;
end$$
DELIMITER ;

call Listar_Professores_Presentes();

-- ---------------------------------------------------------------------
-- Conta quantos pontos um andar possui (parametro OUT)
-- ---------------------------------------------------------------------
DELIMITER $$
drop procedure if exists Contar_Pontos_Andar$$
create procedure Contar_Pontos_Andar(in ida int, out total int)
begin
   select count(*) into total
   from ponto
   where id_andar = ida;
end$$
DELIMITER ;

call Contar_Pontos_Andar(1, @total);
select @total;

-- ---------------------------------------------------------------------
-- Remove a presenca do professor sem excluir o perfil (RF23)
-- ---------------------------------------------------------------------
DELIMITER $$
drop procedure if exists Remover_Presenca$$
create procedure Remover_Presenca(in idp int)
begin
   update professor
   set id_ponto_atual = null,
       mensagem = null,
       mensagem_enviada_em = null
   where id_professor = idp;
end$$
DELIMITER ;

-- call Remover_Presenca(1);

-- ---------------------------------------------------------------------
-- Atualiza a localizacao do professor, substituindo a anterior (RF22)
-- ---------------------------------------------------------------------
DELIMITER $$
drop procedure if exists Atualizar_Localizacao$$
create procedure Atualizar_Localizacao(in idp int, in idpt int)
begin
   update professor
   set id_ponto_atual = idpt
   where id_professor = idp;
end$$
DELIMITER ;

-- call Atualizar_Localizacao(1, 4);
