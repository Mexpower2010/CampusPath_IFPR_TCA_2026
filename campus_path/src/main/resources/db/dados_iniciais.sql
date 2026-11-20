-- =====================================================================
-- CampusPath - Dados iniciais para teste
-- Executar depois do schema.sql, conectado como root.
-- Os ids sao informados manualmente para que os trechos e as
-- localizacoes dos professores apontem para os pontos corretos.
-- Mapa atual: 4 blocos (Cantina, Bloco de Salas, Bloco Administrativo
-- e Bloco Original) e 6 andares.
-- =====================================================================

use campuspath;

-- ---------------------------------------------------------------------
-- Blocos e andares
-- ---------------------------------------------------------------------
insert into bloco(id_bloco, nome_bloco) values
(1, 'Cantina'),
(2, 'Bloco de Salas'),
(3, 'Bloco Administrativo'),
(4, 'Bloco Original');

insert into andar(id_andar, nome, nivel, imagem_mapa, id_bloco) values
(1, 'Terreo', 0, NULL, 1),
(2, 'Terreo', 0, NULL, 2),
(3, 'Terreo', 0, NULL, 3),
(4, 'Primeiro Andar', 1, NULL, 3),
(5, 'Terreo', 0, NULL, 4),
(6, 'Primeiro Andar', 1, NULL, 4);

-- ---------------------------------------------------------------------
-- Pontos do mapa (coordenada global = deslocamento do bloco + local)
-- ---------------------------------------------------------------------
insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values
(1, 1, 'Acesso Cantina', 'ACESSO', 20.0, 20.0),
(2, 2, 'Entrada Bloco de Salas', 'ACESSO', 140.0, 20.0),
(3, 2, 'Corredor Salas 1', 'PASSAGEM', 148.0, 20.0),
(4, 2, 'Corredor Salas 2', 'PASSAGEM', 160.0, 20.0),
(5, 2, 'Corredor Salas 3', 'PASSAGEM', 172.0, 20.0),
(6, 2, 'Corredor Salas 4', 'PASSAGEM', 184.0, 20.0),
(7, 2, 'Corredor Salas 5', 'PASSAGEM', 196.0, 20.0),
(8, 2, 'Sala 101', 'SALA', 148.0, 5.0),
(9, 2, 'Sala 102', 'SALA', 160.0, 5.0),
(10, 2, 'Sala 103', 'SALA', 172.0, 5.0),
(11, 2, 'Sala 104', 'SALA', 184.0, 5.0),
(12, 2, 'Sala 105', 'SALA', 148.0, 35.0),
(13, 2, 'Sala 106', 'SALA', 160.0, 35.0),
(14, 2, 'Sala 107', 'SALA', 172.0, 35.0),
(15, 2, 'Laboratorio de Quimica 1', 'SALA', 184.0, 35.0),
(16, 2, 'Laboratorio de Quimica 2', 'SALA', 196.0, 35.0),
(17, 3, 'Entrada Bloco Administrativo', 'ACESSO', 30.0, 85.0),
(18, 3, 'Corredor Adm Terreo 1', 'PASSAGEM', 30.0, 100.0),
(19, 3, 'Corredor Adm Terreo 2', 'PASSAGEM', 30.0, 120.0),
(20, 3, 'Corredor Adm Terreo 3', 'PASSAGEM', 30.0, 140.0),
(21, 3, 'Corredor Adm Terreo 4', 'PASSAGEM', 30.0, 160.0),
(22, 3, 'Corredor Adm Terreo 5', 'PASSAGEM', 30.0, 180.0),
(23, 3, 'Biblioteca', 'ACESSO', 10.0, 120.0),
(24, 3, 'SEPAE', 'ACESSO', 10.0, 160.0),
(25, 3, 'Sala 108', 'SALA', 50.0, 120.0),
(26, 3, 'Sala 109', 'SALA', 50.0, 160.0),
(27, 3, 'Escada Adm A - Terreo', 'ACESSO', 50.0, 180.0),
(28, 3, 'Escada Adm B - Terreo', 'ACESSO', 30.0, 195.0),
(29, 3, 'Elevador Adm - Terreo', 'ACESSO', 40.0, 195.0),
(30, 4, 'Corredor Adm 1o Andar 1', 'PASSAGEM', 30.0, 100.0),
(31, 4, 'Corredor Adm 1o Andar 2', 'PASSAGEM', 30.0, 120.0),
(32, 4, 'Corredor Adm 1o Andar 3', 'PASSAGEM', 30.0, 140.0),
(33, 4, 'Corredor Adm 1o Andar 4', 'PASSAGEM', 30.0, 160.0),
(34, 4, 'Corredor Adm 1o Andar 5', 'PASSAGEM', 30.0, 180.0),
(35, 4, 'Ala Administrativa', 'ACESSO', 10.0, 100.0),
(36, 4, 'Laboratorio de Informatica 1', 'SALA', 10.0, 120.0),
(37, 4, 'Laboratorio de Informatica 2', 'SALA', 10.0, 140.0),
(38, 4, 'Laboratorio de Informatica 3', 'SALA', 10.0, 160.0),
(39, 4, 'Laboratorio de Informatica 4', 'SALA', 10.0, 180.0),
(40, 4, 'Cozinha', 'ACESSO', 50.0, 120.0),
(41, 4, 'Sala dos Professores 1', 'SALA', 50.0, 140.0),
(42, 4, 'Sala dos Professores 2', 'SALA', 50.0, 160.0),
(43, 4, 'Escada Adm A - 1o Andar', 'ACESSO', 50.0, 180.0),
(44, 4, 'Escada Adm B - 1o Andar', 'ACESSO', 30.0, 195.0),
(45, 4, 'Elevador Adm - 1o Andar', 'ACESSO', 40.0, 195.0),
(46, 5, 'Entrada Bloco Original', 'ACESSO', 140.0, 130.0),
(47, 5, 'Corredor Original Terreo 1', 'PASSAGEM', 150.0, 130.0),
(48, 5, 'Corredor Original Terreo 2', 'PASSAGEM', 170.0, 130.0),
(49, 5, 'Corredor Original Terreo 3', 'PASSAGEM', 190.0, 130.0),
(50, 5, 'Corredor Original Terreo 4', 'PASSAGEM', 210.0, 130.0),
(51, 5, 'Laboratorio de Fisica', 'SALA', 150.0, 115.0),
(52, 5, 'Laboratorio de Informatica 5', 'SALA', 190.0, 115.0),
(53, 5, 'Sala Administrativa', 'ACESSO', 210.0, 115.0),
(54, 5, 'Escada Original - Terreo', 'ACESSO', 170.0, 145.0),
(55, 5, 'Elevador Original - Terreo', 'ACESSO', 180.0, 145.0),
(56, 6, 'Corredor Original 1o Andar 1', 'PASSAGEM', 150.0, 130.0),
(57, 6, 'Corredor Original 1o Andar 2', 'PASSAGEM', 170.0, 130.0),
(58, 6, 'Corredor Original 1o Andar 3', 'PASSAGEM', 190.0, 130.0),
(59, 6, 'Corredor Original 1o Andar 4', 'PASSAGEM', 210.0, 130.0),
(60, 6, 'Sala 201', 'SALA', 150.0, 115.0),
(61, 6, 'Sala 202', 'SALA', 190.0, 115.0),
(62, 6, 'Sala 203', 'SALA', 210.0, 115.0),
(63, 6, 'Escada Original - 1o Andar', 'ACESSO', 170.0, 145.0),
(64, 6, 'Elevador Original - 1o Andar', 'ACESSO', 180.0, 145.0);

-- ---------------------------------------------------------------------
-- Trechos (bidirecionais)
-- ---------------------------------------------------------------------
insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros,
possui_escadas, usa_elevador, coberto, interditado) values
(1, 2, 3, 8.0, false, false, true, false),
(2, 3, 4, 12.0, false, false, true, false),
(3, 4, 5, 12.0, false, false, true, false),
(4, 5, 6, 12.0, false, false, true, false),
(5, 6, 7, 12.0, false, false, true, false),
(6, 8, 3, 15.0, false, false, true, false),
(7, 9, 4, 15.0, false, false, true, false),
(8, 10, 5, 15.0, false, false, true, false),
(9, 11, 6, 15.0, false, false, true, false),
(10, 12, 3, 15.0, false, false, true, false),
(11, 13, 4, 15.0, false, false, true, false),
(12, 14, 5, 15.0, false, false, true, false),
(13, 15, 6, 15.0, false, false, true, false),
(14, 16, 7, 15.0, false, false, true, false),
(15, 17, 18, 15.0, false, false, true, false),
(16, 18, 19, 20.0, false, false, true, false),
(17, 19, 20, 20.0, false, false, true, false),
(18, 20, 21, 20.0, false, false, true, false),
(19, 21, 22, 20.0, false, false, true, false),
(20, 23, 19, 20.0, false, false, true, false),
(21, 24, 21, 20.0, false, false, true, false),
(22, 25, 19, 20.0, false, false, true, false),
(23, 26, 21, 20.0, false, false, true, false),
(24, 27, 22, 20.0, false, false, true, false),
(25, 28, 22, 15.0, false, false, true, false),
(26, 29, 22, 18.0, false, false, true, false),
(27, 30, 31, 20.0, false, false, true, false),
(28, 31, 32, 20.0, false, false, true, false),
(29, 32, 33, 20.0, false, false, true, false),
(30, 33, 34, 20.0, false, false, true, false),
(31, 35, 30, 20.0, false, false, true, false),
(32, 36, 31, 20.0, false, false, true, false),
(33, 37, 32, 20.0, false, false, true, false),
(34, 38, 33, 20.0, false, false, true, false),
(35, 39, 34, 20.0, false, false, true, false),
(36, 40, 31, 20.0, false, false, true, false),
(37, 41, 32, 20.0, false, false, true, false),
(38, 42, 33, 20.0, false, false, true, false),
(39, 43, 34, 20.0, false, false, true, false),
(40, 44, 34, 15.0, false, false, true, false),
(41, 45, 34, 18.0, false, false, true, false),
(42, 46, 47, 10.0, false, false, true, false),
(43, 47, 48, 20.0, false, false, true, false),
(44, 48, 49, 20.0, false, false, true, false),
(45, 49, 50, 20.0, false, false, true, false),
(46, 51, 47, 15.0, false, false, true, false),
(47, 52, 49, 15.0, false, false, true, false),
(48, 53, 50, 15.0, false, false, true, false),
(49, 54, 48, 15.0, false, false, true, false),
(50, 55, 48, 18.0, false, false, true, false),
(51, 56, 57, 20.0, false, false, true, false),
(52, 57, 58, 20.0, false, false, true, false),
(53, 58, 59, 20.0, false, false, true, false),
(54, 60, 56, 15.0, false, false, true, false),
(55, 61, 58, 15.0, false, false, true, false),
(56, 62, 59, 15.0, false, false, true, false),
(57, 63, 57, 15.0, false, false, true, false),
(58, 64, 57, 18.0, false, false, true, false),
(59, 27, 43, 6.0, true, false, true, false),
(60, 28, 44, 6.0, true, false, true, false),
(61, 29, 45, 4.0, false, true, true, false),
(62, 54, 63, 6.0, true, false, true, false),
(63, 55, 64, 4.0, false, true, true, false),
(64, 17, 2, 20.0, false, false, false, false),
(65, 2, 1, 40.0, false, false, true, false),
(66, 1, 46, 30.0, false, false, false, false),
(67, 17, 46, 40.0, false, false, true, false),
(68, 1, 17, 30.0, false, false, true, false),
(69, 2, 46, 30.0, false, false, true, false);

-- ---------------------------------------------------------------------
-- Administrador (senha de teste: admin123)
-- ---------------------------------------------------------------------
insert into administrador(id_admin, nome_admin, email_admin, senha_admin) values
(1, 'Administrador', 'admin@campuspath.ifpr.edu.br',
    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');

-- ---------------------------------------------------------------------
-- Professores (senha de teste: 123456)
-- ---------------------------------------------------------------------
insert into professor(id_professor, nome, login, senha_hash, foto_perfil,
                      id_ponto_atual, mensagem, mensagem_enviada_em) values
(1, 'Roberta Vanessa Rojo Parcianello', 'roberta.parcianello',
    '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
    'fotos/gato.png', 8, 'Disponivel para atendimento ate as 17h.', '2026-11-12 14:00:00'),
(2, 'Fabio Moreira', 'fabio.moreira',
    '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
    'fotos/cachorro.png', 60, 'Em banca de TCC, retorno as 16h.', '2026-11-12 13:30:00'),
(3, 'Ana Paula Souza', 'ana.souza',
    '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
    'fotos/raposa.png', 36, 'Atendimento no laboratorio.', '2026-11-12 15:10:00');

select * from bloco;
select * from andar;
select * from ponto;
select * from trecho;
select * from professor;
select * from administrador;

