const URL_BASE = "http://localhost:8080";

async function carregarDados(endpoint, colunas) {
    const corpoTabela = document.getElementById('corpo-tabela');
    const cabecalhoTabela = document.getElementById('cabecalho-tabela');

    corpoTabela.innerHTML = "<tr><td colspan='100%'>Carregando...</td></tr>";

    try{
        const response = await fetch(`${URL_BASE}/${endpoint}`);
        const dados = await response.json();

        cabecalhoTabela.innerHTML = colunas.map(col => `<th>${col.toUpperCase()}</th>`).join('');
        corpoTabela.innerHTML = dados.map(dado => `
            <tr>
                ${colunas.map(col => `<td>${dado[col]}</td>`).join('')}
            </tr>`).join('');
    } catch (error) {
        console.error('Erro ao carregar dados:', error);
        corpoTabela.innerHTML = "<tr><td colspan='100%'>Erro ao carregar dados</td></tr>";
    }
}

function mostrarSecao(tipo){
    document.getElementById('titulo-secao').innerText = `Gestão de ${tipo.charAt(0).toUpperCase() + tipo.slice(1)}`;
    if (tipo === 'usuarios') {
        carregarDados('users', ['id', 'name', 'email', 'telefone']);
    } else if (tipo === 'pedidos') {
        carregarDados('orders', ['id', 'moment', 'orderStatus']);
    } else if (tipo === 'produtos') {
        carregarDados('products', ['id', 'name', 'price']);
    }
    else if (tipo === 'categorias') {
        carregarDados('categories', ['id', 'nome']);
    }
}

mostrarSecao('usuarios');