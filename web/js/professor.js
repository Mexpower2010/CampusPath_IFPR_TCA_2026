const el = (id) => document.getElementById(id);

const estado = {
  sessao: null,
  professor: null,
  salas: []
};

function mostrarPainel(autenticado) {
  el("painelLogin").classList.toggle("oculto", autenticado);
  el("painelSessao").classList.toggle("oculto", !autenticado);
  el("painelAcoes").classList.toggle("oculto", !autenticado);
}

async function carregarSalas() {
  estado.salas = await API.get("/api/pontos?tipo=SALA");
  const select = el("sala");
  select.innerHTML = "";
  const vazio = document.createElement("option");
  vazio.value = "";
  vazio.textContent = "sem sala definida";
  select.appendChild(vazio);
  estado.salas.forEach((sala) => {
    const opcao = document.createElement("option");
    opcao.value = String(sala.idPonto);
    opcao.textContent = `${sala.nome} - ${sala.nomeAndar || "andar " + sala.idAndar}`;
    select.appendChild(opcao);
  });
}

async function carregarProfessor() {
  const lista = await API.get("/api/professores");
  estado.professor = lista.find((p) => p.idProfessor === estado.sessao.id) || null;
  if (!estado.professor) {
    aviso(el("aviso"), "Professor da sessao nao encontrado na base.", "erro");
    return;
  }
  el("nome").value = estado.professor.nome || "";
  el("foto").value = estado.professor.fotoPerfil || "";
  el("sala").value = estado.professor.idPontoAtual ? String(estado.professor.idPontoAtual) : "";
  el("mensagem").value = estado.professor.mensagem || "";
  el("dadosProfessor").innerHTML = `
    <div class="cartao">
      <div class="nome">${escapar(estado.professor.nome)}</div>
      <div class="detalhe">Login: ${escapar(estado.professor.login)}</div>
      <div class="detalhe">Sala atual: ${escapar(estado.professor.nomeSala || "nao definida")}</div>
      <div class="detalhe">Mensagem: ${escapar(estado.professor.mensagem || "nenhuma")}</div>
      <div class="horario">Publicada em ${escapar(formatarData(estado.professor.mensagemEnviadaEm))}</div>
    </div>`;
}

async function entrar(evento) {
  evento.preventDefault();
  try {
    const dados = await API.post("/api/auth/professor", {
      login: el("login").value.trim(),
      senha: el("senha").value
    });
    API.definirToken(dados.token);
    await abrirSessao();
    aviso(el("aviso"), "Sessao iniciada.", "ok");
  } catch (e) {
    aviso(el("aviso"), e.message, "erro");
  }
}

async function abrirSessao() {
  if (!API.temSessao()) {
    mostrarPainel(false);
    return;
  }
  try {
    estado.sessao = await API.get("/api/auth/sessao");
  } catch (e) {
    API.definirToken(null);
    mostrarPainel(false);
    return;
  }
  if (estado.sessao.tipo !== "PROFESSOR") {
    aviso(el("aviso"), "Esta sessao nao e de professor. Use a area de administracao.", "erro");
    mostrarPainel(false);
    return;
  }
  mostrarPainel(true);
  el("nomeSessao").textContent = estado.sessao.nome;
  el("expiraSessao").textContent = formatarData(estado.sessao.expiraEm);
  await carregarSalas();
  await carregarProfessor();
}

async function salvarLocalizacao(evento) {
  evento.preventDefault();
  const valor = el("sala").value;
  try {
    await API.put(`/api/professores/${estado.sessao.id}/localizacao`, {
      idPonto: valor ? Number(valor) : null
    });
    aviso(el("aviso"), "Localizacao atualizada.", "ok");
    await carregarProfessor();
  } catch (e) {
    aviso(el("aviso"), e.message, "erro");
  }
}

async function removerPresenca() {
  try {
    await API.del(`/api/professores/${estado.sessao.id}/presenca`);
    aviso(el("aviso"), "Presenca removida.", "ok");
    await carregarProfessor();
  } catch (e) {
    aviso(el("aviso"), e.message, "erro");
  }
}

async function publicarMensagem(evento) {
  evento.preventDefault();
  try {
    await API.put(`/api/professores/${estado.sessao.id}/mensagem`, {
      mensagem: el("mensagem").value.trim()
    });
    aviso(el("aviso"), "Mensagem publicada.", "ok");
    await carregarProfessor();
  } catch (e) {
    aviso(el("aviso"), e.message, "erro");
  }
}

async function salvarPerfil(evento) {
  evento.preventDefault();
  try {
    await API.put(`/api/professores/${estado.sessao.id}/nome`, { nome: el("nome").value.trim() });
    await API.put(`/api/professores/${estado.sessao.id}/foto`, { fotoPerfil: el("foto").value.trim() });
    aviso(el("aviso"), "Perfil atualizado.", "ok");
    await carregarProfessor();
  } catch (e) {
    aviso(el("aviso"), e.message, "erro");
  }
}

async function sair() {
  try {
    await API.post("/api/auth/sair", {});
  } catch (e) {
    // sessao ja pode ter expirado
  }
  API.definirToken(null);
  estado.sessao = null;
  estado.professor = null;
  mostrarPainel(false);
  aviso(el("aviso"), "Sessao encerrada.", "info");
}

document.addEventListener("DOMContentLoaded", () => {
  el("formLogin").addEventListener("submit", entrar);
  el("formLocalizacao").addEventListener("submit", salvarLocalizacao);
  el("formMensagem").addEventListener("submit", publicarMensagem);
  el("formPerfil").addEventListener("submit", salvarPerfil);
  el("removerPresenca").addEventListener("click", removerPresenca);
  el("sair").addEventListener("click", sair);
  abrirSessao();
});
