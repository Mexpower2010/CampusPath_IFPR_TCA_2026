const Mapa = (() => {
  const NS = "http://www.w3.org/2000/svg";

  function criar(nome, atributos) {
    const elemento = document.createElementNS(NS, nome);
    Object.entries(atributos || {}).forEach(([chave, valor]) => {
      elemento.setAttribute(chave, valor);
    });
    return elemento;
  }

  function corDoPonto(ponto, ocupada) {
    if (ocupada) {
      return "#22c55e";
    }
    if (ponto.tipo === "SALA") {
      return "#64748b";
    }
    if (ponto.tipo === "ACESSO") {
      return "#f59e0b";
    }
    return "#475569";
  }

  function desenhar(svg, dados) {
    const pontos = dados.pontos || [];
    const trechos = dados.trechos || [];
    const professores = dados.professores || [];
    const rotaPontos = new Set(dados.rotaPontos || []);
    const rotaTrechos = new Set(dados.rotaTrechos || []);

    svg.innerHTML = "";

    const comCoordenada = pontos.filter((p) => p.coordenadaX !== null && p.coordenadaY !== null);
    if (comCoordenada.length === 0) {
      const texto = criar("text", { x: 50, y: 50, fill: "#9fb0c0", "font-size": 4, "text-anchor": "middle" });
      texto.textContent = "Nenhum ponto com coordenadas neste andar.";
      svg.setAttribute("viewBox", "0 0 100 100");
      svg.appendChild(texto);
      return;
    }

    const xs = comCoordenada.map((p) => Number(p.coordenadaX));
    const ys = comCoordenada.map((p) => Number(p.coordenadaY));
    const margem = 14;
    const minX = Math.min(...xs) - margem;
    const minY = Math.min(...ys) - margem;
    const largura = Math.max(...xs) - Math.min(...xs) + margem * 2;
    const altura = Math.max(...ys) - Math.min(...ys) + margem * 2;
    svg.setAttribute("viewBox", `${minX} ${minY} ${largura} ${altura}`);

    const fonte = Math.max(2.6, Math.min(5, Math.min(largura, altura) / 13));
    const porId = new Map(pontos.map((p) => [p.idPonto, p]));
    const ocupadas = new Set(professores.filter((p) => p.idPontoAtual).map((p) => p.idPontoAtual));

    trechos.forEach((trecho) => {
      const a = porId.get(trecho.idPontoA);
      const b = porId.get(trecho.idPontoB);
      if (!a || !b || a.coordenadaX === null || b.coordenadaX === null) {
        return;
      }
      const naRota = rotaTrechos.has(trecho.idTrecho);
      let cor = "#334155";
      let espessura = 1.6;
      let tracejado = null;
      if (trecho.interditado) {
        cor = "#ef4444";
        tracejado = "3 2";
      }
      if (naRota) {
        cor = "#3b82f6";
        espessura = 3.4;
        tracejado = null;
      }
      const linha = criar("line", {
        x1: a.coordenadaX,
        y1: a.coordenadaY,
        x2: b.coordenadaX,
        y2: b.coordenadaY,
        stroke: cor,
        "stroke-width": espessura,
        "stroke-linecap": "round"
      });
      if (tracejado) {
        linha.setAttribute("stroke-dasharray", tracejado);
      }
      const titulo = criar("title", {});
      titulo.textContent = `${a.nome} - ${b.nome} | ${formatarMetros(trecho.distanciaMetros)}`
        + (trecho.possuiEscadas ? " | escadas" : "")
        + (trecho.usaElevador ? " | elevador" : "")
        + (trecho.coberto ? " | coberto" : " | descoberto")
        + (trecho.interditado ? " | INTERDITADO" : "");
      linha.appendChild(titulo);
      svg.appendChild(linha);
    });

    pontos.forEach((ponto) => {
      if (ponto.coordenadaX === null || ponto.coordenadaY === null) {
        return;
      }
      const naRota = rotaPontos.has(ponto.idPonto);
      const ocupada = ocupadas.has(ponto.idPonto);
      const raio = ponto.tipo === "SALA" ? 4.2 : 3.2;

      if (naRota) {
        svg.appendChild(criar("circle", {
          cx: ponto.coordenadaX,
          cy: ponto.coordenadaY,
          r: raio + 3,
          fill: "none",
          stroke: "#3b82f6",
          "stroke-width": 1.4,
          opacity: 0.7
        }));
      }

      const circulo = criar("circle", {
        cx: ponto.coordenadaX,
        cy: ponto.coordenadaY,
        r: raio,
        fill: corDoPonto(ponto, ocupada),
        stroke: "#0f1720",
        "stroke-width": 0.8,
        "data-ponto": ponto.idPonto,
        style: "cursor:pointer"
      });
      const titulo = criar("title", {});
      titulo.textContent = `${ponto.nome} [${ponto.tipo}]`;
      circulo.appendChild(titulo);
      if (dados.aoClicar) {
        circulo.addEventListener("click", () => dados.aoClicar(ponto));
      }
      svg.appendChild(circulo);

      const rotulo = criar("text", {
        x: ponto.coordenadaX,
        y: Number(ponto.coordenadaY) + raio + fonte * 1.05,
        fill: naRota ? "#bfdbfe" : "#9fb0c0",
        "font-size": fonte,
        "text-anchor": "middle",
        "pointer-events": "none"
      });
      rotulo.textContent = ponto.nome;
      svg.appendChild(rotulo);
    });
  }

  return { desenhar };
})();
