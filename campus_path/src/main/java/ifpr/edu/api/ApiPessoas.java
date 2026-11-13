package ifpr.edu.api;

import ifpr.edu.model.Administrador;
import ifpr.edu.model.Professor;
import ifpr.edu.util.Senha;

import java.util.Map;

public final class ApiPessoas {

    private ApiPessoas() {
    }

    public static Resposta loginProfessor(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Map<String, Object> corpo = requisicao.corpoJson();
        Professor professor = contexto.autenticacao.autenticarProfessor(
            Json.textoObrigatorio(corpo, "login"),
            Json.textoObrigatorio(corpo, "senha"));
        String token = contexto.sessoes.criar("PROFESSOR", professor.getIdProfessor(), professor.getNome());
        return Resposta.ok(Json.mapa(
            "token", token,
            "tipo", "PROFESSOR",
            "professor", JsonModelo.professor(professor)));
    }

    public static Resposta loginAdmin(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Map<String, Object> corpo = requisicao.corpoJson();
        Administrador administrador = contexto.autenticacao.autenticarAdministrador(
            Json.textoObrigatorio(corpo, "email"),
            Json.textoObrigatorio(corpo, "senha"));
        String token = contexto.sessoes.criar("ADMIN", administrador.getIdAdmin(), administrador.getNomeAdmin());
        return Resposta.ok(Json.mapa(
            "token", token,
            "tipo", "ADMIN",
            "administrador", JsonModelo.administrador(administrador)));
    }

    public static Resposta sessao(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        SessaoService.Sessao sessao = Autorizacao.exigirSessao(contexto, requisicao);
        return Resposta.ok(Json.mapa(
            "tipo", sessao.tipo(),
            "id", sessao.id(),
            "nome", sessao.nome(),
            "expiraEm", sessao.expiraEm().toString()));
    }

    public static Resposta sair(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        contexto.sessoes.remover(requisicao.getToken());
        return Resposta.ok(Json.mapa("mensagem", "Sessao encerrada."));
    }

    public static Resposta listarProfessores(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Integer idSala = requisicao.parametroInteiro("sala");
        if (requisicao.parametroBooleano("presentes")) {
            return Resposta.ok(JsonModelo.professores(contexto.professores.listarPresentes()));
        }
        if (idSala != null) {
            return Resposta.ok(JsonModelo.professores(contexto.professores.listarPorSala(idSala)));
        }
        return Resposta.ok(JsonModelo.professores(contexto.professores.listar()));
    }

    public static Resposta criarProfessor(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        Map<String, Object> corpo = requisicao.corpoJson();
        Professor professor = new Professor(
            Json.textoObrigatorio(corpo, "nome"),
            Json.textoObrigatorio(corpo, "login"),
            Senha.hash(Json.textoObrigatorio(corpo, "senha")));
        professor.setFotoPerfil(Json.texto(corpo, "fotoPerfil"));
        contexto.professores.inserir(professor);
        return Resposta.criado(Json.mapa("mensagem", "Professor cadastrado."));
    }

    public static Resposta alterarProfessor(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        int id = ApiMapa.inteiro(parametros, "id");
        Professor professor = contexto.professores.buscarPorId(id);
        if (professor == null) {
            throw new ErroHttp(404, "Professor nao encontrado.");
        }
        Map<String, Object> corpo = requisicao.corpoJson();
        professor.setNome(Json.textoObrigatorio(corpo, "nome"));
        professor.setLogin(Json.textoObrigatorio(corpo, "login"));
        professor.setFotoPerfil(Json.texto(corpo, "fotoPerfil"));
        contexto.professores.atualizar(professor);
        return Resposta.ok(Json.mapa("mensagem", "Professor alterado."));
    }

    public static Resposta excluirProfessor(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        contexto.professores.excluir(ApiMapa.inteiro(parametros, "id"));
        return Resposta.ok(Json.mapa("mensagem", "Professor excluido. Salas e trechos foram mantidos."));
    }

    public static Resposta alterarNome(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        int id = ApiMapa.inteiro(parametros, "id");
        Autorizacao.exigirProfessor(contexto, requisicao, id);
        Map<String, Object> corpo = requisicao.corpoJson();
        contexto.professores.atualizarNome(id, Json.textoObrigatorio(corpo, "nome"));
        return Resposta.ok(Json.mapa("mensagem", "Nome atualizado."));
    }

    public static Resposta alterarFoto(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        int id = ApiMapa.inteiro(parametros, "id");
        Autorizacao.exigirProfessor(contexto, requisicao, id);
        Map<String, Object> corpo = requisicao.corpoJson();
        contexto.professores.atualizarFoto(id, Json.texto(corpo, "fotoPerfil"));
        return Resposta.ok(Json.mapa("mensagem", "Foto de perfil atualizada."));
    }

    public static Resposta alterarLocalizacao(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        int id = ApiMapa.inteiro(parametros, "id");
        Autorizacao.exigirProfessor(contexto, requisicao, id);
        Map<String, Object> corpo = requisicao.corpoJson();
        Integer idPonto = Json.inteiro(corpo, "idPonto");
        if (idPonto != null) {
            var ponto = contexto.pontos.buscarPorId(idPonto);
            if (ponto == null || !ponto.isSala()) {
                throw new ErroHttp(400, "A localizacao do professor deve ser um ponto do tipo SALA.");
            }
        }
        contexto.professores.atualizarLocalizacao(id, idPonto);
        return Resposta.ok(Json.mapa("mensagem", "Localizacao atualizada."));
    }

    public static Resposta publicarMensagem(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        int id = ApiMapa.inteiro(parametros, "id");
        Autorizacao.exigirProfessor(contexto, requisicao, id);
        Map<String, Object> corpo = requisicao.corpoJson();
        contexto.professores.publicarMensagem(id, Json.textoObrigatorio(corpo, "mensagem"));
        return Resposta.ok(Json.mapa("mensagem", "Mensagem publicada com horario registrado."));
    }

    public static Resposta removerPresenca(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        int id = ApiMapa.inteiro(parametros, "id");
        Autorizacao.exigirProfessor(contexto, requisicao, id);
        contexto.professores.removerPresenca(id);
        return Resposta.ok(Json.mapa("mensagem", "Presenca removida. O perfil continua cadastrado."));
    }

    public static Resposta listarAdministradores(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        return Resposta.ok(JsonModelo.administradores(contexto.administradores.listar()));
    }

    public static Resposta criarAdministrador(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        Map<String, Object> corpo = requisicao.corpoJson();
        Administrador administrador = new Administrador(
            Json.textoObrigatorio(corpo, "nomeAdmin"),
            Json.textoObrigatorio(corpo, "emailAdmin"),
            Senha.hash(Json.textoObrigatorio(corpo, "senha")));
        contexto.administradores.inserir(administrador);
        return Resposta.criado(Json.mapa("mensagem", "Administrador cadastrado."));
    }

    public static Resposta alterarAdministrador(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        int id = ApiMapa.inteiro(parametros, "id");
        Administrador administrador = contexto.administradores.buscarPorId(id);
        if (administrador == null) {
            throw new ErroHttp(404, "Administrador nao encontrado.");
        }
        Map<String, Object> corpo = requisicao.corpoJson();
        administrador.setNomeAdmin(Json.textoObrigatorio(corpo, "nomeAdmin"));
        administrador.setEmailAdmin(Json.textoObrigatorio(corpo, "emailAdmin"));
        String senha = Json.texto(corpo, "senha");
        if (senha != null && !senha.isBlank()) {
            administrador.setSenhaAdmin(Senha.hash(senha));
        }
        contexto.administradores.atualizar(administrador);
        return Resposta.ok(Json.mapa("mensagem", "Administrador alterado."));
    }

    public static Resposta excluirAdministrador(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        SessaoService.Sessao sessao = Autorizacao.exigirAdmin(contexto, requisicao);
        int id = ApiMapa.inteiro(parametros, "id");
        if (sessao.id() == id) {
            throw new ErroHttp(400, "Nao e permitido excluir o administrador em uso.");
        }
        contexto.administradores.excluir(id);
        return Resposta.ok(Json.mapa("mensagem", "Administrador excluido."));
    }
}
