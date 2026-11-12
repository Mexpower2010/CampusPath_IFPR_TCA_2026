package ifpr.edu.service;

import ifpr.edu.dao.AdministradorDAO;
import ifpr.edu.dao.ProfessorDAO;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Administrador;
import ifpr.edu.model.Professor;
import ifpr.edu.util.Senha;

public class AutenticacaoService {

    private final ProfessorDAO professores = new ProfessorDAO();
    private final AdministradorDAO administradores = new AdministradorDAO();

    public Professor autenticarProfessor(String login, String senha) {
        Professor professor = professores.buscarPorLogin(login);
        if (professor == null || !Senha.hash(senha).equals(professor.getSenhaHash())) {
            throw new PersistenciaException("Login ou senha invalidos.");
        }
        return professor;
    }

    public Administrador autenticarAdministrador(String email, String senha) {
        Administrador administrador = administradores.buscarPorEmail(email);
        if (administrador == null || !Senha.hash(senha).equals(administrador.getSenhaAdmin())) {
            throw new PersistenciaException("E-mail ou senha invalidos.");
        }
        return administrador;
    }
}
