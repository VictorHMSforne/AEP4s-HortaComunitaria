const campo = (chave, rotulo, opcoes = {}) => ({chave, rotulo, ...opcoes});
const texto = (chave, rotulo, limite, opcoes = {}) => campo(chave, rotulo, {limite, ...opcoes});
const numero = (chave, rotulo, min = 0, passo = 0.01, max = 9999999999.99) =>
    campo(chave, rotulo, {tipo: "number", min, passo, max});
const data = (chave, rotulo) => campo(chave, rotulo, {tipo: "date"});
const vinculo = (chave, rotulo, fonte, opcional = false) => campo(chave, rotulo, {fonte, opcional});
const opcoes = (chave, rotulo, valores) => campo(chave, rotulo, {valores});
const endereco = texto("endereco", "Endereço", 240);
const nome = texto("nome", "Nome", 160);
const cpf = texto("cpf", "CPF (11 dígitos)", 11, {padrao: "[0-9]{11}"});
const prefeitura = vinculo("prefeituraId", "Prefeitura", "prefeituras");
const horta = vinculo("hortaId", "Horta", "hortas");

const secoes = {
    terrenos: ["Terrenos", "Cadastre terrenos e acompanhe a disponibilidade para novas hortas.", [
        endereco, numero("area", "Área (m²)", 0.01),
        opcoes("status", "Disponibilidade", {DISPONIVEL: "Disponível", INDISPONIVEL: "Indisponível"}),
        opcoes("situacaoLegal", "Situação legal", {A_ANALISAR: "A analisar", APTO: "Apto", INAPTO: "Inapto"}),
        vinculo("bairroId", "Bairro", "bairros"), prefeitura,
        vinculo("proprietarioId", "Proprietário", "proprietarios", true)
    ]],
    bairros: ["Bairros", "Cadastre os bairros usados no levantamento de terrenos.", [texto("nome", "Nome", 120)]],
    prefeituras: ["Prefeituras", "Cadastre o órgão municipal que administra os terrenos e contrata os gestores.", [
        nome, texto("cnpj", "CNPJ (14 dígitos)", 14, {padrao: "[0-9]{14}"})
    ]],
    proprietarios: ["Proprietários", "Cadastre os proprietários que podem ser vinculados aos terrenos.", [
        nome, cpf, endereco, texto("telefone", "Telefone", 30), texto("email", "E-mail", 160, {tipo: "email"})
    ]],
    contratos: ["Contratos", "Registre os termos acordados entre a prefeitura e o proprietário do terreno.", [
        vinculo("terrenoId", "Terreno", "terrenos"), prefeitura,
        vinculo("proprietarioId", "Proprietário", "proprietarios"),
        data("dataInicio", "Início"), data("dataFim", "Término"), numero("valorAluguel", "Aluguel (R$)"),
        numero("percentualProducao", "Repasse da produção (%)", 0, 0.01, 100),
        texto("incentivoFiscal", "Incentivo fiscal", 240, {opcional: true}),
        opcoes("tipoAdesao", "Adesão", {LEGAL: "Legal", VOLUNTARIA: "Voluntária"})
    ]],
    gestores: ["Gestores", "Registre a contratação dos responsáveis pelas hortas.", [
        nome, cpf, endereco, texto("matricula", "Matrícula", 40), data("dataContratacao", "Contratação"),
        numero("ajudaCusto", "Ajuda de custo (R$)"), prefeitura
    ]],
    hortas: ["Hortas", "Vincule cada horta a um terreno apto e a um gestor da mesma prefeitura.", [
        nome, vinculo("terrenoId", "Terreno", "terrenos"), vinculo("gestorId", "Gestor", "gestores")
    ]],
    familias: ["Famílias", "Vincule as famílias beneficiadas às hortas.", [nome, horta]],
    membros: ["Membros das famílias", "Registre os integrantes das famílias beneficiadas.", [
        nome, cpf, endereco, vinculo("familiaId", "Família", "familias")
    ]],
    colheitas: ["Colheitas", "Registre o que foi cultivado e a quantidade colhida em cada horta.", [
        horta, texto("produto", "Produto", 120), numero("quantidadeProduzida", "Quantidade colhida (kg)", 0.01),
        data("dataPlantio", "Plantio"), data("dataColheita", "Colheita")
    ]],
    minhocarios: ["Minhocários", "Registre a produção de adubo orgânico por horta.", [
        horta, numero("quantidadeMinhocas", "Quantidade de minhocas", 0, 1, 2147483647),
        numero("quantidadeAdubo", "Adubo produzido (kg)"), data("dataRegistro", "Data do registro")
    ]],
    distribuicoes: ["Distribuições", "Registre as entregas de alimentos às famílias da horta.", [
        vinculo("colheitaId", "Colheita", "colheitas"), vinculo("familiaId", "Família", "familias"),
        numero("quantidade", "Quantidade entregue (kg)", 0.01), data("dataEntrega", "Entrega")
    ]],
    "relatorios/terrenos-por-bairro": ["Terrenos por bairro", "Veja quantos terrenos foram cadastrados, continuam baldios ou estão aptos para receber uma horta.", [
        campo("bairro", "Bairro"), campo("terrenosCadastrados", "Cadastrados"),
        campo("terrenosBaldios", "Baldios"), campo("terrenosAptos", "Baldios aptos")
    ], true]
};

const obter = (id) => document.getElementById(id);
const menu = obter("menu");
const lista = obter("lista");
const aviso = obter("aviso");
const dialogo = obter("edicao");
const formulario = obter("formulario");
const salvar = obter("salvar");
let secaoAtual;
let versao = 0;
let idEdicao = null;
let salvando = false;

for (const [chave, [titulo]] of Object.entries(secoes)) {
    const link = document.createElement("a");
    link.href = "#" + chave;
    link.textContent = titulo;
    menu.append(link);
}

async function api(recurso, opcoes) {
    const resposta = await fetch("/api/" + recurso, opcoes);
    if (!resposta.ok) {
        const erro = await resposta.json().catch(() => ({}));
        throw new Error(erro.mensagem || "Não foi possível concluir a operação.");
    }
    return resposta.status === 204 ? null : resposta.json();
}

function informar(mensagem, erro = false) {
    aviso.textContent = mensagem;
    aviso.classList.toggle("erro", erro);
}

function elemento(tag, conteudo) {
    const no = document.createElement(tag);
    no.textContent = conteudo;
    return no;
}

function botao(texto, executar, classe = "secundario") {
    const no = elemento("button", texto);
    no.type = "button";
    no.className = classe;
    no.addEventListener("click", executar);
    return no;
}

function rotulo(registro) {
    if (registro.produto) {
        return registro.produto + " · " + formatarData(registro.dataColheita) + " (#" + registro.id + ")";
    }
    return (registro.nome || registro.endereco) + " (#" + registro.id + ")";
}

function formatarData(valor) {
    return valor.split("-").reverse().join("/");
}

async function carregarVinculos(campos) {
    const fontes = [...new Set(campos.filter(c => c.fonte).map(c => c.fonte))];
    return Object.fromEntries(await Promise.all(fontes.map(async fonte => [fonte, await api(fonte)])));
}

function valorCelula(c, valor, vinculos) {
    if (valor == null || valor === "") return "—";
    if (c.fonte) {
        const registro = vinculos[c.fonte].find(r => r.id === valor);
        return registro ? rotulo(registro) : "#" + valor;
    }
    if (c.valores) return c.valores[valor] || valor;
    if (c.tipo === "date") return formatarData(valor);
    if (typeof valor === "number") return valor.toLocaleString("pt-BR");
    return valor;
}

async function abrir(secao, registro = {}) {
    const [, , campos] = secoes[secao];
    try {
        const vinculos = await carregarVinculos(campos);
        if (secao !== secaoAtual || dialogo.open) return;
        const ausente = campos.find(c => c.fonte && !c.opcional && !vinculos[c.fonte].length);
        if (ausente) {
            informar("Cadastre primeiro um registro em " + secoes[ausente.fonte][0] + ".", true);
            return;
        }
        idEdicao = registro.id ?? null;
        informar("");
        obter("titulo-edicao").textContent = idEdicao === null ? "Novo cadastro" : "Editar cadastro";
        obter("erro-edicao").textContent = "";
        obter("campos").replaceChildren(...campos.map(c => criarCampo(c, registro[c.chave], vinculos)));
        dialogo.showModal();
    } catch (erro) {
        informar(erro.message, true);
    }
}

function criarCampo(c, valor, vinculos) {
    const label = document.createElement("label");
    const rotuloCampo = elemento("span", c.rotulo + (c.opcional ? " (opcional)" : ""));
    rotuloCampo.id = "rotulo-" + c.chave;
    const entrada = document.createElement(c.fonte || c.valores ? "select" : "input");
    entrada.name = c.chave;
    entrada.required = !c.opcional;
    entrada.setAttribute("aria-labelledby", rotuloCampo.id);
    if (entrada.tagName === "SELECT") {
        entrada.add(new Option(c.opcional ? "Sem vínculo" : "Selecione", ""));
        const escolhas = c.fonte ? vinculos[c.fonte].map(r => [r.id, rotulo(r)]) : Object.entries(c.valores);
        for (const [id, texto] of escolhas) entrada.add(new Option(texto, id));
    } else {
        entrada.type = c.tipo || "text";
        if (c.limite) entrada.maxLength = c.limite;
        if (c.padrao) {
            entrada.pattern = c.padrao;
            entrada.inputMode = "numeric";
        }
        if (c.tipo === "number") {
            entrada.min = c.min;
            entrada.step = c.passo;
            entrada.max = c.max;
        }
    }
    entrada.value = valor ?? "";
    label.append(rotuloCampo, entrada);
    return label;
}

async function excluir(secao, id) {
    if (!window.confirm("Excluir o cadastro #" + id + "?")) return;
    try {
        await api(secao + "/" + id, {method: "DELETE"});
        if (secao === secaoAtual) await atualizar("Cadastro excluído.");
    } catch (erro) {
        if (secao === secaoAtual) informar(erro.message, true);
    }
}

function mostrarLista(secao, registros, vinculos) {
    if (!registros.length) {
        lista.replaceChildren(elemento("p", "Nenhum registro encontrado."));
        return;
    }
    const [, , campos, relatorio] = secoes[secao];
    const tabela = document.createElement("table");
    tabela.setAttribute("aria-labelledby", "titulo");
    const cabecalho = tabela.createTHead().insertRow();
    const titulos = campos.map(c => c.rotulo);
    if (!relatorio) {
        titulos.unshift("ID");
        titulos.push("Ações");
    }
    for (const titulo of titulos) {
        const th = elemento("th", titulo);
        th.scope = "col";
        cabecalho.append(th);
    }
    const corpo = tabela.createTBody();
    for (const registro of registros) {
        const linha = corpo.insertRow();
        if (!relatorio) linha.insertCell().textContent = registro.id;
        for (const c of campos) linha.insertCell().textContent = valorCelula(c, registro[c.chave], vinculos);
        if (!relatorio) {
            const acoes = linha.insertCell();
            acoes.className = "acoes";
            acoes.append(botao("Editar", () => abrir(secao, registro)),
                botao("Excluir", () => excluir(secao, registro.id), "secundario perigo"));
        }
    }
    lista.replaceChildren(tabela);
}

async function atualizar(mensagem = "") {
    const secao = secaoAtual;
    const pedido = ++versao;
    lista.replaceChildren(elemento("p", "Carregando…"));
    informar("");
    try {
        const [, , campos] = secoes[secao];
        const [registros, vinculos] = await Promise.all([api(secao), carregarVinculos(campos)]);
        if (pedido !== versao) return;
        mostrarLista(secao, registros, vinculos);
        informar(mensagem);
    } catch (erro) {
        if (pedido !== versao) return;
        lista.replaceChildren();
        informar(erro.message, true);
    }
}

function navegar() {
    const destino = location.hash.slice(1);
    secaoAtual = Object.hasOwn(secoes, destino) ? destino : "terrenos";
    if (dialogo.open && !salvando) dialogo.close();
    const [titulo, descricao, , relatorio] = secoes[secaoAtual];
    obter("titulo").textContent = titulo;
    obter("descricao").textContent = descricao;
    obter("novo").hidden = Boolean(relatorio);
    for (const link of menu.children) {
        if (link.hash === "#" + secaoAtual) link.setAttribute("aria-current", "page");
        else link.removeAttribute("aria-current");
    }
    atualizar();
}

document.querySelector(".atalho").addEventListener("click", evento => {
    evento.preventDefault();
    obter("titulo").focus();
    obter("conteudo").scrollIntoView();
});
obter("novo").addEventListener("click", () => abrir(secaoAtual));
obter("cancelar").addEventListener("click", () => dialogo.close());
dialogo.addEventListener("cancel", evento => {
    if (salvando) evento.preventDefault();
});
formulario.addEventListener("submit", async evento => {
    evento.preventDefault();
    if (salvando) return;
    const secao = secaoAtual;
    const dados = {};
    for (const c of secoes[secao][2]) {
        const valor = formulario.elements.namedItem(c.chave).value.trim();
        dados[c.chave] = valor === "" ? null : c.fonte || c.tipo === "number" ? Number(valor) : valor;
    }
    salvando = true;
    salvar.disabled = true;
    obter("cancelar").disabled = true;
    obter("erro-edicao").textContent = "";
    try {
        await api(idEdicao === null ? secao : secao + "/" + idEdicao, {
            method: idEdicao === null ? "POST" : "PUT",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(dados)
        });
        dialogo.close();
        if (secao === secaoAtual) await atualizar("Cadastro salvo.");
    } catch (erro) {
        if (secao === secaoAtual) obter("erro-edicao").textContent = erro.message;
        else dialogo.close();
    } finally {
        salvando = false;
        salvar.disabled = false;
        obter("cancelar").disabled = false;
    }
});
window.addEventListener("hashchange", navegar);
navegar();
