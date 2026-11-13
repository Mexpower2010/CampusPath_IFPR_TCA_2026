const estado = {
  dados: null,
  bloco: null,
  andar: null,
  rota: null
};

const el = (id) => document.getElementById(id);

function pontosDoAndar() {
  return estado.dados.pontos.filter((p) => p.idAndar === estado.andar);
}

function trechosDoAndar() {
  const ids = new Set(pontosDoAndar().map((p) => p.idPonto));
  return estado.dados.trechos.filter((t) => ids.has(t.idPontoA) && ids.has(t.idPontoB));
}

function professoresDaSala(idPonto) {
  return estado.dados.professores.filter((p) => p.idPontoAtual === idPonto);
}

function preencherSelect(select, itens, valorAtual, rotulo) {
  select.innerHTML = "";
  itens.forEach((item) => {
    const opcao = document.createElement("option");
    opcao.value = item.valor;
    opcao.textContent = item.texto;
    if (String(item.valor) === String(valorAtual)) {
      opcao.selected = true;
    }
    select.appendChild(opcao);
  });
  if (rotulo && itens.length === 0) {
    const opcao = document.createElement("option");
    opcao.value = "";
    opcao.textContent = rotulo;
    select.appendChild(opcao);
  }
}

function desenharMapa() {
  const rotaPontos = estado.rota ? estado.rota.caminho : [];
  const rotaTrechos = estado.rota ? estado.rota.trechos : [];
  Mapa.desenhar(el("mapa"), {
    pontos: pontosDoAndar(),
    trechos: trechosDoAndar(),
    professores: estado.dados.professores,
    rotaPontos,
    rotaTrechos,
    aoClicar: (ponto) => {
      if (ponto.tipo === "SALA") {
        el("destino").value = String(ponto.idPonto);
        mostrarDetalhe(ponto.idPonto);
      } else {
        el("origem").value = String(ponto.idPonto);
      }
    }
  });
}

function desenharSalas() {
  const salas = pontosDoAndar().filter((p) => p.tipo === "SALA");
  const container = el("salas");
  container.innerHTML = "";
  if (salas.length === 0) {
    container.innerHTML = '<div class="item"><div class="detalhe">Nenhuma sala neste andar.</div></div>';
    return;
  }
  salas.forEach((sala) => {
    const professores = professoresDaSala(sala.idPonto);
    const item = document.createElement("div");
    item.className = "item" + (professores.length > 0 ? " ocupada" : "");
    const resumo = professores.length === 0
      ? "sem professor presente"
      : professores.map((p) => p.nome).join(", ");
    item.innerHTML = `<div class="titulo">${escapar(sala.nome)}</div>
      <div class="detalhe">${escapar(resumo)}</div>`;
    item.addEventListener("click", () => {
      el("destino").value = String(sala.idPonto);
      mostrarDetalhe(sala.idPonto);
    });
    container.appendChild(item);
  });
}

function mostrarDetalhe(idPonto) {
  const sala = estado.dados.pontos.find((p) => p.idPonto === idPonto);
  const container = el("detalhe");
  if (!sala) {
    container.textContent = "Sala nao encontrada.";
    return;
  }
  const professores = professoresDaSala(idPonto);
  if (professores.length === 0) {
    container.innerHTML = `<div class="cartao"><div class="nome">${escapar(sala.nome)}</div>
      <div class="detalhe">Nenhum professor presente nesta sala no momento.</div></div>`;
    return;
  }
  container.innerHTML = professores.map((p) => `
    <div class="cartao">
      <div class="nome">${escapar(p.nome)}</div>
      <div class="detalhe">${escapar(p.mensagem || "sem mensagem")}</div>
      <div class="horario">Publicada em ${escapar(formatarData(p.mensagemEnviadaEm))}</div>
      <div class="detalhe">Foto: ${escapar(p.fotoPerfil || "nao informada")}</div>
    </div>`).join("");
}

function desenharRota() {
  const resumo = el("rotaResumo");
  const passos = el("passos");
  passos.innerHTML = "";
  if (!estado.rota) {
    resumo.innerHTML = '<div class="detalhe">Nenhuma rota calculada.</div>';
    return;
  }
  resumo.innerHTML = `<div class="distancia">${formatarMetros(estado.rota.distanciaTotal)}</div>
    <div class="detalhe">${escapar(estado.rota.origem.nome)} ate ${escapar(estado.rota.destino.nome)}</div>`;
  estado.rota.passos.forEach((passo) => {
    const item = document.createElement("li");
    item.textContent = passo;
    if (!passo.startsWith("  ")) {
      item.className = "principal";
    }
    passos.appendChild(item);
  });
}

function atualizarAndares() {
  const andares = estado.dados.andares.filter((a) => a.idBloco === estado.bloco);
  preencherSelect(el("andar"), andares.map((a) => ({ valor: a.idAndar, texto: `${a.nome} (nivel ${a.nivel})` })), estado.andar, "sem andares");
  if (andares.length > 0 && !andares.some((a) => a.idAndar === estado.andar)) {
    estado.andar = andares[0].idAndar;
    el("andar").value = String(estado.andar);
  }
}

function atualizarTudo() {
  atualizarAndares();
  desenharMapa();
  desenharSalas();
  desenharRota();
}

async function calcularRota() {
  const origem = el("origem").value;
  const destino = el("destino").value;
  if (!origem || !destino) {
    aviso(el("aviso"), "Selecione origem e destino.", "erro");
    return;
  }
  const parametros = new URLSearchParams({
    origem,
    destino,
    evitarEscadas: el("evitarEscadas").checked,
    somenteCoberto: el("somenteCoberto").checked
  });
  try {
    estado.rota = await API.get("/api/rotas?" + parametros.toString());
    aviso(el("aviso"), "", "");
    desenharMapa();
    desenharRota();
  } catch (e) {
    estado.rota = null;
    desenharMapa();
    desenharRota();
    aviso(el("aviso"), e.message, "erro");
  }
}

async function iniciar() {
  try {
    estado.dados = await API.get("/api/mapa");
  } catch (e) {
    aviso(el("aviso"), e.message, "erro");
    return;
  }
  if (estado.dados.blocos.length === 0) {
    aviso(el("aviso"), "Nenhum bloco cadastrado. Use a administracao para cadastrar o campus.", "info");
    return;
  }
  estado.bloco = estado.dados.blocos[0].idBloco;
  estado.andar = estado.dados.andares.find((a) => a.idBloco === estado.bloco)?.idAndar ?? null;

  preencherSelect(el("bloco"), estado.dados.blocos.map((b) => ({ valor: b.idBloco, texto: b.nomeBloco })), estado.bloco);
  preencherSelect(el("origem"), estado.dados.pontos.map((p) => ({ valor: p.idPonto, texto: `${p.nome} (${p.tipo})` })), estado.dados.pontos[0]?.idPonto);
  const salas = estado.dados.pontos.filter((p) => p.tipo === "SALA");
  preencherSelect(el("destino"), salas.map((p) => ({ valor: p.idPonto, texto: p.nome })), salas[0]?.idPonto, "sem salas");

  atualizarTudo();

  el("bloco").addEventListener("change", (evento) => {
    estado.bloco = Number(evento.target.value);
    estado.rota = null;
    atualizarTudo();
  });
  el("andar").addEventListener("change", (evento) => {
    estado.andar = Number(evento.target.value);
    desenharMapa();
    desenharSalas();
  });
  el("calcular").addEventListener("click", calcularRota);
  el("limpar").addEventListener("click", () => {
    estado.rota = null;
    desenharMapa();
    desenharRota();
    aviso(el("aviso"), "", "");
  });
}

document.addEventListener("DOMContentLoaded", iniciar);
