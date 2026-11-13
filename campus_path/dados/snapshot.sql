-- CampusPath - estado atual do banco, gerado automaticamente pela aplicacao.
-- Versione este arquivo no Git para restaurar o estado em outro computador.
-- Gerado em 2026-11-13 08:56:20

use campuspath;

delete from professor;
delete from administrador;
delete from trecho;
delete from ponto;
delete from andar;
delete from bloco;

insert into bloco(id_bloco, nome_bloco) values (1, 'Bloco A');
insert into bloco(id_bloco, nome_bloco) values (2, 'Bloco B');
insert into andar(id_andar, nome, nivel, imagem_mapa, id_bloco) values (1, 'Terreo', 0, 'mapas/bloco_a_terreo.png', 1);
insert into andar(id_andar, nome, nivel, imagem_mapa, id_bloco) values (2, 'Primeiro Andar', 1, 'mapas/bloco_a_primeiro.png', 1);
insert into andar(id_andar, nome, nivel, imagem_mapa, id_bloco) values (3, 'Terreo', 0, 'mapas/bloco_b_terreo.png', 2);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (1, 1, 'Hall de Entrada', 'ACESSO', 10.0, 90.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (2, 1, 'Corredor A1', 'PASSAGEM', 30.0, 90.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (3, 1, 'Sala 101', 'SALA', 30.0, 70.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (4, 1, 'Sala 102', 'SALA', 30.0, 110.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (5, 1, 'Escada Bloco A - Terreo', 'ACESSO', 50.0, 90.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (6, 1, 'Elevador Bloco A - Terreo', 'ACESSO', 55.0, 90.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (7, 2, 'Corredor A2', 'PASSAGEM', 30.0, 90.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (8, 2, 'Sala 201', 'SALA', 30.0, 70.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (9, 2, 'Sala 202', 'SALA', 30.0, 110.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (10, 2, 'Escada Bloco A - Primeiro Andar', 'ACESSO', 50.0, 90.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (11, 2, 'Elevador Bloco A - Primeiro Andar', 'ACESSO', 55.0, 90.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (12, 3, 'Corredor B1', 'PASSAGEM', 30.0, 90.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (13, 3, 'Laboratorio 1', 'SALA', 30.0, 70.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (14, 3, 'Laboratorio 2', 'SALA', 30.0, 110.0);
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values (15, 3, 'Acesso Bloco B', 'ACESSO', 10.0, 90.0);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (1, 1, 2, 10.0, false, false, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (2, 2, 3, 8.0, false, false, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (3, 2, 4, 12.0, false, false, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (4, 2, 5, 6.0, true, false, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (5, 2, 6, 7.0, false, true, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (6, 5, 10, 3.0, true, false, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (7, 6, 11, 3.0, false, true, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (8, 10, 7, 5.0, false, false, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (9, 11, 7, 5.0, false, true, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (10, 7, 8, 9.0, false, false, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (11, 7, 9, 11.0, false, false, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (12, 1, 15, 25.0, false, false, false, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (13, 15, 12, 10.0, false, false, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (14, 12, 13, 8.0, false, false, true, false);
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, usa_elevador, coberto, interditado) values (15, 12, 14, 10.0, false, false, true, false);
insert into administrador(id_admin, nome_admin, email_admin, senha_admin) values (1, 'Administrador', 'admin@campuspath.ifpr.edu.br', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');
insert into professor(id_professor, nome, login, senha_hash, foto_perfil, id_ponto_atual, mensagem, mensagem_enviada_em) values (1, 'Roberta Vanessa Rojo Parcianello', 'roberta.parcianello', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'fotos/roberta.png', 3, 'Disponivel para atendimento ate as 17h.', '2026-11-12 14:00:00');
insert into professor(id_professor, nome, login, senha_hash, foto_perfil, id_ponto_atual, mensagem, mensagem_enviada_em) values (2, 'Fabio Moreira', 'fabio.moreira', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'fotos/fabio.png', 8, 'Em banca de TCC, retorno as 16h.', '2026-11-12 13:30:00');
insert into professor(id_professor, nome, login, senha_hash, foto_perfil, id_ponto_atual, mensagem, mensagem_enviada_em) values (3, 'Ana Paula Souza', 'ana.souza', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'fotos/ana.png', 13, 'Atendimento no laboratorio.', '2026-11-12 15:10:00');

update controle_sync set snapshot_aplicado_em = '2026-11-13 08:56:20' where id_controle = 1;
