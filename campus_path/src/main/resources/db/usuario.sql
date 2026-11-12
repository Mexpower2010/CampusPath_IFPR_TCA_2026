-- =====================================================================
-- CampusPath - Usuario de aplicacao e permissoes
-- Executar UMA vez, conectado como root no MySQL Workbench.
-- Troque a senha do placeholder antes de executar e use a mesma
-- senha no arquivo db.properties (que nao vai para o Git).
-- =====================================================================

CREATE USER IF NOT EXISTS 'campuspath'@'localhost'
    IDENTIFIED BY 'TROCAR_ESTA_SENHA';

GRANT SELECT, INSERT, UPDATE, DELETE ON campuspath.* TO 'campuspath'@'localhost';

FLUSH PRIVILEGES;
