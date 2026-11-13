const el = (id) => document.getElementById(id);

const estado = {
  sessao: null,
  aba: "blocos",
  editando: null,
  dados: {
    blocos: [],
    andares: [],
    pontos: [],
    trechos: [],
    professores: [],
    administradores: []
  }
};

const ENTIDADES = {
  blocos: {
    titulo: "Blocos",
    caminho: "/api/blocos",
    id: "idBloco",
    rotulo: (item) => item.nomeBloco,
    campos: [
      { nome: "nomeBloco", rotulo: "Nome do bloco", tipo: "texto", obrigatorio: true }
    ]
  },
  andares: {
    titulo: "Andares",
    caminho: "/api/andares",
    id: "idAndar",
    rotulo: (item) => `${item.nome} (nivel ${item.nivel})`,
    campos: [
      { nome: "nome", rotulo: "Nome do andar", tipo: "texto", obrigatorio: true },
      { nome: "nivel", rotulo: "Nivel", tipo: "numero", obrigatorio: true },
      { nome: "idBloco", rotulo: "Bloco", tipo: "select", obrigatorio: true, opcoes: () => estado.dados.blocos.map((b) => ({ valor: b.idBloco, texto: b.nomeBloco })) },
      { nome: "imagemMapa", rotulo: "Imagem do mapa", tipo: "texto" }
    ]
  },
  pontos: {
    titulo: "Pontos",
    caminho: "/api/pontos",
    id: "idPonto",
    rotulo: (item) => `${item.nome} (${item.tipo})`,
    campos: [
      { nome: "idAndar", rotulo: "Andar", tipo: "select", obrigatorio: true, opcoes: () => estado.dados.andares.map((a) => ({ valor: a.idAndar, texto: `${a.nome} - ${a.nomeBloco || "bloco " + a.idBloco}` })) },
      { nome: "nome", rotulo: "Nome do ponto", tipo: "texto", obrigatorio: true },
      { nome: "tipo", rotulo: "Tipo", tipo: "select", obrigatorio: true, opcoes: () => ["SALA", "PASSAGEM", "ACESSO"].map((t) => ({ valor: t, texto: t })) },
      { nome: "coordenadaX", rotulo: "Coordenada X", tipo: "numero" },
      { nome: "coordenadaY", rotulo: "Coordenada Y", tipo: "numero" }
    ]
  },
  trechos: {
    titulo: "Trechos",
    caminho: "/api/trechos",
    id: "idTrecho",
    rotulo: (item) => `${item.nomePontoA || item.idPontoA} -> ${item.nomePontoB || item.idPontoB}`,
    campos: [
      { nome: "idPontoA", rotulo: "Ponto A", tipo: "select", obrigatorio: true, opcoes: () => estado.dados.pontos.map((p) => ({ valor: p.idPonto, texto: `${p.nome} (${p.tipo})` })) },
      { nome: "idPontoB", rotulo: "Ponto B", tipo: "select", obrigatorio: true, opcoes: () => estado.dados.pontos.map((p) => ({ valor: p.idPonto, texto: `${p.nome} (${p.tipo})` })) },
      { nome: "distanciaMetros", rotulo: "Distancia (m)", tipo: "numero", obrigatorio: true, passo: "0.1" },
      { nome: "possuiEscadas", rotulo: "Possui escadas", tipo: "check" },
      { nome: "usaElevador", rotulo: "Usa elevador", tipo: "check" },
      { nome: "coberto", rotulo: "Coberto", tipo: "check" },
      { nome: "interditado", rotulo: "Interditado", tipo: "check" }
    ]
  },
  professores: {
    titulo: "Professores",
    caminho: "/api/professores",
    id: "idProfessor",
    rotulo: (item) => item.nome,
    campos: [
      { nome: "nome", rotulo: "Nome", tipo: "texto", obrigatorio: true },
      { nome: "login", rotulo: "Login", tipo: "texto", obrigatorio: true },
      { nome: "senha", rotulo: "Senha", tipo: "senha", somenteCriacao: true },
      { nome: "fotoPerfil", rotulo: "Foto de perfil", tipo: "texto" }
    ]
  },
  administradores: {
    titulo: "Administradores",
    caminho: "/api/administradores",
    id: "idAdmin",
    rotulo: (item) => item.nomeAdmin,
    campos: [
      { nome: "nomeAdmin", rotulo: "Nome", tipo: "texto", obrigatorio: true },
      { nome: "emailAdmin", rotulo: "E-mail", tipo: "email", obrigatorio: true },
      { nome: "senha", rotulo: "Senha", tipo: "senha" }
    ]
  }
};

function mostrarPainel(autenticado) {
  el("painelLogin").classList.toggle("oculto", autenticado);
  el("painelSessao").classList.toggle("oculto", !autenticado);
  el("painelAdmin").classList.toggle("oculto", !autenticado);
}

function montarAbas() {
  const abas = el("abas");
  abas.innerHTML = "";
  Object.keys(ENTIDADES).forEach((chave) => {
    const botao = document.createElement("button");
    botao.type = "button";
    botao.className = chave === estado.aba ? "ativo" : "";
    botao.textContent = ENTIDADES[chave].titulo;
    botao.addEventListener("click", () => {
      estado.aba = chave;
      estado.editando = null;
      montarAbas();
      renderizar();
    });
    abas.appendChild(botao);
  });
}

function valorCelula(item, campo) {
  const valor = item[campo.nome];
  if (campo.tipo === "check") {
    return valor ? "Sim" : "Nao";
  }
  if (valor === null || valor === undefined || valor === "") {
    return "-";
  }
  return String(valor);
}

function renderizarTabela() {
  const entidade = ENTIDADES[estado.aba];
  const itens = estado.dados[estado.aba] || [];
  const container = el("tabela");
  if (itens.length === 0) {
    container.innerHTML = '<p class="detalhe">Nenhum registro cadastrado.</p>';
    return;
  }
  const colunas = entidade.campos.filter((campo) => campo.tipo !== "senha");
  const tabela = document.createElement("table");
  const cabecalho = document.createElement("thead");
  cabecalho.innerHTML = "<tr>" + colunas.map((c) => `<th>${escapar(c.rotulo)}</th>`).join("") + "<th>Acoes</th></tr>";
  tabela.appendChild(cabecalho);
  const corpo = document.createElement("tbody");
  itens.forEach((item) => {
    const linha = document.createElement("tr");
    linha.innerHTML = colunas.map((c) => `<td>${escapar(valorCelula(item, c))}</td>`).join("");
    const acoes = document.createElement("td");
    acoes.className = "acoes";
    const editar = document.createElement("button");
    editar.type = "button";
    editar.className = "mini";
    editar.textContent = "Editar";
    editar.addEventListener("click", () => iniciarEdicao(item));
    const excluir = document.createElement("button");
    excluir.type = "button";
    excluir.className = "mini perigo";
    excluir.textContent = "Excluir";
    excluir.addEventListener("click", () => excluir(item));
    acoes.appendChild(editar);
    acoes.appendChild(excluir);
    linha.appendChild(acoes);
    corpo.appendChild(linha);
  });
  tabela.appendChild(corpo);
  container.innerHTML = "";
  container.appendChild(tabela);
}

function criarCampo(campo, valor) {
  const bloco = document.createElement("div");
  const idCampo = "campo_" + campo.nome;
  const label = document.createElement("label");
  label.htmlFor = idCampo;
  label.textContent = campo.rotulo + (campo.obrigatorio ? " *" : "");
  bloco.appendChild(label);

  let entrada;
  if (campo.tipo === "select") {
    entrada = document.createElement("select");
    const vazio = document.createElement("option");
    vazio.value = "";
    vazio.textContent = "selecione";
    entrada.appendChild(vazio);
    campo.opcoes().forEach((opcao) => {
      const item = document.createElement("option");
      item.value = String(opcao.valor);
      item.textContent = opcao.texto;
      entrada.appendChild(item);
    });
  } else if (campo.tipo === "check") {
    entrada = document.createElement("input");
    entrada.type = "checkbox";
  } else {
    entrada = document.createElement("input");
    entrada.type = campo.tipo === "numero" ? "number" : campo.tipo;
    if (campo.passo) {
      entrada.step = campo.passo;
    }
  }
  entrada.id = idCampo;
  entrada.name = campo.nome;
  if (campo.obrigatorio) {
    entrada.required = true;
  }
  if (campo.tipo === "check") {
    entrada.checked = Boolean(valor);
  } else if (valor !== null && valor !== undefined) {
    entrada.value = String(valor);
  }
  bloco.appendChild(entrada);
  return bloco;
}

function renderizarFormulario() {
  const entidade = ENTIDADES[estado.aba];
  const form = el("formEntidade");
  form.innerHTML = "";
  const item = estado.editando;
  entidade.campos.forEach((campo) => {
    if (campo.somenteCriacao && item) {
      return;
    }
    form.appendChild(criarCampo(campo, item ? item[campo.nome] : null));
  });
  const botao = document.createElement("button");
  botao.type = "submit";
  botao.textContent = item ? "Salvar alteracoes" : "Cadastrar";
  form.appendChild(botao);
  el("tituloForm").textContent = item ? "Editar registro" : "Novo registro";
  el("cancelarEdicao").classList.toggle("oculto", !item);
}

function renderizar() {
  el("tituloAba").textContent = ENTIDADES[estado.aba].titulo;
  renderizarTabela();
  renderizarFormulario();
}

function iniciarEdicao(item) {
  estado.editando = item;
  renderizarFormulario();
}

function cancelarEdicao() {
  estado.editando = null;
  renderizarFormulario();
}

function corpoDoFormulario() {
  const entidade = ENTIDADES[estado.aba];
  const corpo = {};
  entidade.campos.forEach((campo) => {
    const entrada = el("campo_" + campo.nome);
    if (!entrada) {
      return;
    }
    if (campo.tipo === "check") {
      corpo[campo.nome] = entrada.checked;
    } else if (campo.tipo === "numero") {
      corpo[campo.nome] = entrada.value === "" ? null : Number(entrada.value);
    } else if (campo.tipo === "senha") {
      if (entrada.value.trim() !== "") {
        corpo[campo.nome] = entrada.value;
      }
    } else if (campo.tipo === "select") {
      corpo[campo.nome] = entrada.value === "" ? null : Number(entrada.value);
    } else {
      corpo[campo.nome] = entrada.value.trim() === "" ? null : entrada.value.trim();
    }
  });
  return corpo;
}

async function salvar(evento) {
  evento.preventDefault();
  const entidade = ENTIDADES[estado.aba];
  const corpo = corpoDoFormulario();
  try {
    if (estado.editando) {
      await API.put(`${entidade.caminho}/${estado.editando[entidade.id]}`, corpo);
      aviso(el("aviso"), "Registro alterado.", "ok");
    } else {
      await API.post(entidade.caminho, corpo);
      aviso(el("aviso"), "Registro cadastrado.", "ok");
    }
    estado.editando = null;
    await recarregar();
  } catch (e) {
    aviso(el("aviso"), e.message, "erro");
  }
}

async function excluir(item) {
  const entidade = ENTIDADES[estado.aba];
  const rotulo = entidade.rotulo(item);
  if (!window.confirm(`Excluir "${rotulo}"?`)) {
    return;
  }
  try {
    await API.del(`${entidade.caminho}/${item[entidade.id]}`);
    aviso(el("aviso"), "Registro excluido.", "ok");
    estado.editando = null;
    await recarregar();
  } catch (e) {
    aviso(el("aviso"), e.message, "erro");
  }
}

async function carregarDados() {
  const mapa = await API.get("/api/mapa");
  estado.dados.blocos = mapa.blocos;
  estado.dados.andares = mapa.andares;
  estado.dados.pontos = mapa.pontos;
  estado.dados.trechos = mapa.trechos;
  estado.dados.professores = await API.get("/api/professores");
  estado.dados.administradores = await API.get("/api/administradores");
}

async function recarregar() {
  try {
    await carregarDados();
    renderizar();
  } catch (e) {
    aviso(el("aviso"), e.message, "erro");
  }
}

async function entrar(evento) {
  evento.preventDefault();
  try {
    const dados = await API.post("/api/auth/admin", {
      email: el("email").value.trim(),
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
  if (estado.sessao.tipo !== "ADMIN") {
    aviso(el("aviso"), "Esta sessao nao e de administrador. Use a area do professor.", "erro");
    mostrarPainel(false);
    return;
  }
  mostrarPainel(true);
  el("nomeSessao").textContent = estado.sessao.nome;
  el("expiraSessao").textContent = formatarData(estado.sessao.expiraEm);
  montarAbas();
  await recarregar();
}

async function exportar() {
  try {
    const dados = await API.post("/api/snapshot", {});
    aviso(el("aviso"), `${dados.mensagem} Arquivo: ${dados.arquivo}`, "ok");
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
  mostrarPainel(false);
  aviso(el("aviso"), "Sessao encerrada.", "info");
}

document.addEventListener("DOMContentLoaded", () => {
  el("formLogin").addEventListener("submit", entrar);
  el("formEntidade").addEventListener("submit", salvar);
  el("cancelarEdicao").addEventListener("click", cancelarEdicao);
  el("exportar").addEventListener("click", exportar);
  el("sair").addEventListener("click", sair);
  abrirSessao();
});
