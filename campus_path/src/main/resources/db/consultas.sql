-- =====================================================================
-- CampusPath - Consultas de apoio (padrao das aulas de BD)
-- Executar depois do dados_iniciais.sql.
-- =====================================================================

use campuspath;

-- ---------------------------------------------------------------------
-- Produto cartesiano com filtro = inner join (como em aulaBD)
-- ---------------------------------------------------------------------
select *
from ponto, professor
where ponto.id_ponto = professor.id_ponto_atual;

select *
from ponto inner join professor on
   ponto.id_ponto = professor.id_ponto_atual;

-- ---------------------------------------------------------------------
-- Todas as salas, com ou sem professor presente (RF03 / RF05)
-- ---------------------------------------------------------------------
select ponto.nome as sala, professor.nome as professor, professor.mensagem
from ponto left join professor on
   ponto.id_ponto = professor.id_ponto_atual
where ponto.tipo = 'SALA'
order by ponto.nome;

-- ---------------------------------------------------------------------
-- Todos os professores, com ou sem localizacao informada
-- ---------------------------------------------------------------------
select professor.nome, ponto.nome as sala
from ponto right join professor on
   ponto.id_ponto = professor.id_ponto_atual;

-- ---------------------------------------------------------------------
-- Uniao dos dois lados (equivale ao full outer join)
-- ---------------------------------------------------------------------
select ponto.nome as sala, professor.nome as professor
from ponto left join professor on
   ponto.id_ponto = professor.id_ponto_atual
union
select ponto.nome as sala, professor.nome as professor
from ponto right join professor on
   ponto.id_ponto = professor.id_ponto_atual;

-- ---------------------------------------------------------------------
-- Trechos liberados, com o nome das duas extremidades (base do Dijkstra)
-- ---------------------------------------------------------------------
select t.id_trecho,
       pa.nome as ponto_a,
       pb.nome as ponto_b,
       t.distancia_metros,
       t.possui_escadas,
       t.usa_elevador,
       t.coberto
from trecho t
inner join ponto pa on pa.id_ponto = t.id_ponto_a
inner join ponto pb on pb.id_ponto = t.id_ponto_b
where t.interditado = false
order by t.id_trecho;

-- ---------------------------------------------------------------------
-- Trechos que podem ser usados com "evitar escadas" (RF12)
-- ---------------------------------------------------------------------
select id_trecho, id_ponto_a, id_ponto_b, distancia_metros
from trecho
where interditado = false
and possui_escadas = false;

-- ---------------------------------------------------------------------
-- Trechos que podem ser usados com "somente coberto" (RF13)
-- ---------------------------------------------------------------------
select id_trecho, id_ponto_a, id_ponto_b, distancia_metros
from trecho
where interditado = false
and coberto = true;
