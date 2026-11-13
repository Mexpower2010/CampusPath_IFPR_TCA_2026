const API = (() => {
  const CHAVE = "campuspath.token";
  let token = localStorage.getItem(CHAVE);

  function definirToken(valor) {
    token = valor || null;
    if (token) {
      localStorage.setItem(CHAVE, token);
    } else {
      localStorage.removeItem(CHAVE);
    }
  }

  async function pedir(metodo, caminho, corpo) {
    const opcoes = { method: metodo, headers: {} };
    if (token) {
      opcoes.headers.Authorization = "Bearer " + token;
    }
    if (corpo !== undefined) {
      opcoes.headers["Content-Type"] = "application/json";
      opcoes.body = JSON.stringify(corpo);
    }
    let resposta;
    try {
      resposta = await fetch(caminho, opcoes);
    } catch (e) {
      throw new Error("Nao foi possivel falar com o servidor. Ele esta rodando?");
    }
    const texto = await resposta.text();
    let dados = null;
    if (texto) {
      try {
        dados = JSON.parse(texto);
      } catch (e) {
        dados = { erro: texto };
      }
    }
    if (!resposta.ok) {
      if (resposta.status === 401) {
        definirToken(null);
      }
      throw new Error((dados && dados.erro) || "Erro " + resposta.status);
    }
    return dados;
  }

  return {
    get: (caminho) => pedir("GET", caminho),
    post: (caminho, corpo) => pedir("POST", caminho, corpo),
    put: (caminho, corpo) => pedir("PUT", caminho, corpo),
    patch: (caminho, corpo) => pedir("PATCH", caminho, corpo),
    del: (caminho) => pedir("DELETE", caminho),
    token: () => token,
    definirToken,
    temSessao: () => Boolean(token)
  };
})();

function escapar(valor) {
  if (valor === null || valor === undefined) {
    return "";
  }
  return String(valor)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function aviso(elemento, texto, tipo) {
  if (!elemento) {
    return;
  }
  elemento.textContent = texto;
  elemento.className = "mensagem visivel " + (tipo || "info");
  if (!texto) {
    elemento.className = "mensagem";
  }
}

function formatarData(valor) {
  if (!valor) {
    return "nao informado";
  }
  const data = new Date(valor);
  if (Number.isNaN(data.getTime())) {
    return valor;
  }
  return data.toLocaleString("pt-BR");
}

function formatarMetros(valor) {
  return Number(valor).toFixed(1).replace(".", ",") + " m";
}
