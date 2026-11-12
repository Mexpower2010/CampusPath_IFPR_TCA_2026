-- =====================================================================
-- CampusPath - Dados iniciais para teste
-- Executar depois do schema.sql, conectado como root.
-- Os ids sao informados manualmente para que os trechos e as
-- localizacoes dos professores apontem para os pontos corretos.
-- =====================================================================

use campuspath;

-- ---------------------------------------------------------------------
-- Blocos e andares
-- ---------------------------------------------------------------------
insert into bloco(id_bloco, nome_bloco) values
(1, 'Bloco A'),
(2, 'Bloco B');

insert into andar(id_andar, nome, nivel, imagem_mapa, id_bloco) values
(1, 'Terreo', 0, 'mapas/bloco_a_terreo.png', 1),
(2, 'Primeiro Andar', 1, 'mapas/bloco_a_primeiro.png', 1),
(3, 'Terreo', 0, 'mapas/bloco_b_terreo.png', 2);

-- ---------------------------------------------------------------------
-- Pontos do mapa
-- ---------------------------------------------------------------------
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values
(1, 1, 'Hall de Entrada', 'ACESSO', 10.0, 90.0),
(2, 1, 'Corredor A1', 'PASSAGEM', 30.0, 90.0),
(3, 1, 'Sala 101', 'SALA', 30.0, 70.0),
(4, 1, 'Sala 102', 'SALA', 30.0, 110.0),
(5, 1, 'Escada Bloco A - Terreo', 'ACESSO', 50.0, 90.0),
(6, 1, 'Elevador Bloco A - Terreo', 'ACESSO', 55.0, 90.0),
(7, 2, 'Corredor A2', 'PASSAGEM', 30.0, 90.0),
(8, 2, 'Sala 201', 'SALA', 30.0, 70.0),
(9, 2, 'Sala 202', 'SALA', 30.0, 110.0),
(10, 2, 'Escada Bloco A - Primeiro Andar', 'ACESSO', 50.0, 90.0),
(11, 2, 'Elevador Bloco A - Primeiro Andar', 'ACESSO', 55.0, 90.0),
(12, 3, 'Corredor B1', 'PASSAGEM', 30.0, 90.0),
(13, 3, 'Laboratorio 1', 'SALA', 30.0, 70.0),
(14, 3, 'Laboratorio 2', 'SALA', 30.0, 110.0),
(15, 3, 'Acesso Bloco B', 'ACESSO', 10.0, 90.0);

-- ---------------------------------------------------------------------
-- Trechos (bidirecionais)
-- ---------------------------------------------------------------------
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros,
possui_escadas, usa_elevador, coberto, interditado) values
(1, 1, 2, 10.0, false, false, true, false),
(2, 2, 3, 8.0, false, false, true, false),
(3, 2, 4, 12.0, false, false, true, false),
(4, 2, 5, 6.0, true, false, true, false),
(5, 2, 6, 7.0, false, true, true, false),
(6, 5, 10, 3.0, true, false, true, false),
(7, 6, 11, 3.0, false, true, true, false),
(8, 10, 7, 5.0, false, false, true, false),
(9, 11, 7, 5.0, false, true, true, false),
(10, 7, 8, 9.0, false, false, true, false),
(11, 7, 9, 11.0, false, false, true, false),
(12, 1, 15, 25.0, false, false, false, false),
(13, 15, 12, 10.0, false, false, true, false),
(14, 12, 13, 8.0, false, false, true, false),
(15, 12, 14, 10.0, false, false, true, false);

-- ---------------------------------------------------------------------
-- Administrador (senha_admin deve receber o hash gerado pela aplicacao)
-- ---------------------------------------------------------------------
insert into administrador(id_admin, nome_admin, email_admin, senha_admin) values
(1, 'Administrador', 'admin@campuspath.ifpr.edu.br', 'TROCAR_PELO_HASH');

-- ---------------------------------------------------------------------
-- Professores (senha_hash deve receber o hash gerado pela aplicacao)
-- ---------------------------------------------------------------------
insert into professor(id_professor, nome, login, senha_hash, foto_perfil,
                      id_ponto_atual, mensagem, mensagem_enviada_em) values
(1, 'Roberta Vanessa Rojo Parcianello', 'roberta.parcianello', 'TROCAR_PELO_HASH',
    'fotos/roberta.png', 3, 'Disponivel para atendimento ate as 17h.', '2026-11-12 14:00:00'),
(2, 'Fabio Moreira', 'fabio.moreira', 'TROCAR_PELO_HASH',
    'fotos/fabio.png', 8, 'Em banca de TCC, retorno as 16h.', '2026-11-12 13:30:00'),
(3, 'Ana Paula Souza', 'ana.souza', 'TROCAR_PELO_HASH',
    'fotos/ana.png', 13, 'Atendimento no laboratorio.', '2026-11-12 15:10:00');

select * from bloco;
select * from andar;
select * from ponto;
select * from trecho;
select * from professor;
select * from administrador;
