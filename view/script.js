const URL_BASE = "http://localhost:8080";

async function carregarDados(endpoint, colunas) {
    const corpoTabela = document.getElementById('corpo-tabela');
    const cabecalhoTabela = document.getElementById('cabecalho-tabela');

    corpoTabela.innerHTML = "<tr><td colspan='100%'>Carregando...</td></tr>";

    try{
        const response = await fetch(`${URL_BASE}/${endpoint}`);
        const dados = await response.json();

        cabecalhoTabela.innerHTML = colunas.map(col => `<th>${col.toUpperCase()}</th>`).join('');
       corpoTabela.innerHTML = dados.map(item => `
            <tr>
                ${colunas.map(col => `<td>${item[col] || '---'}</td>`).join('')}
                <td>
                    <button class="btn-excluir" onclick="excluirItem('${endpoint}', ${item.id})">Excluir</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar dados:', error);
        corpoTabela.innerHTML = "<tr><td colspan='100%'>Erro ao carregar dados</td></tr>";
    }
}

function mostrarSecao(tipo) {
    const inputs = document.getElementById('inputs-dinamicos');
    const titulo = document.getElementById('titulo-secao');
    
    // Limpa os inputs antes de gerar novos
    inputs.innerHTML = ""; 

    if (tipo === 'usuarios') {
        titulo.innerText = "Gestão de Usuarios";
        inputs.innerHTML = `
            <input type="text" name="name" placeholder="Nome" required>
            <input type="email" name="email" placeholder="Email" required>
            <input type="text" name="phone" placeholder="Telefone">
            <input type="password" name="password" placeholder="Senha" required>`;
        carregarDados('users', ['id', 'name', 'email']);

    } else if (tipo === 'produtos') {
        titulo.innerText = "Gestão de Produtos";
        inputs.innerHTML = `
            <input type="text" name="name" placeholder="Nome do Produto" required>
            <input type="number" name="price" placeholder="Preço" step="0.01" required>
            <input type="text" name="description" placeholder="Descrição">`;
        carregarDados('products', ['id', 'name', 'price']);

    } else if (tipo === 'categorias') {
        titulo.innerText = "Gestão de Categorias";
        inputs.innerHTML = `
            <input type="text" name="name" placeholder="Nome da Categoria" required>`;
        carregarDados('categories', ['id', 'name']);

    } else if (tipo === 'pedidos') {
        titulo.innerText = "Gestão de Pedidos";
        inputs.innerHTML = `<p>Para pedidos, a criação geralmente é feita via seleção de produtos (fluxo complexo), mas você pode listar aqui.</p>`;
        carregarDados('orders', ['id', 'moment', 'orderStatus']);
    }
}

// Chame a função uma vez ao carregar a página para não começar vazia
mostrarSecao('usuarios');

async function excluirItem(endpoint, id) {
    if(!confirm('Tem certeza que deseja excluir este item?')) return;

    try{
        const response = await fetch(`${URL_BASE}/${endpoint}/${id}`, {
            method: 'DELETE'
        });

        if(response.ok){
            alert('Item excluído com sucesso!');
            location.reload();
        }else{
            alert('Erro ao excluir item, verifique se existe algum pedido associado a ele.');
        }
    }catch (error) {
        console.error('Erro ao excluir item:', error);
        alert('Erro ao excluir item, verifique se existe algum pedido associado a ele.');
    }
}

document.getElementById('meuForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    // Captura o elemento do título com segurança
    const elementoTitulo = document.getElementById('titulo-secao');
    
    // Verifica se o elemento existe, se não existir, interrompe para não dar erro
    if (!elementoTitulo) {
        console.error("Erro: Elemento 'titulo-secao' não encontrado no HTML.");
        return;
    }

    // Pega o texto, transforma em string e coloca em minúsculo
    const tituloTexto = elementoTitulo.innerText.toLowerCase();
    let endpoint = "";
    
    // Define o endpoint baseado no texto do título
    if (tituloTexto.includes("usuarios")) {
        endpoint = "users";
    } else if (tituloTexto.includes("produtos")) {
        endpoint = "products";
    } else if (tituloTexto.includes("categorias")) {
        endpoint = "categories";
    } else if (tituloTexto.includes("pedidos")) {
        endpoint = "orders";
    }

    const formData = new FormData(e.target);
    const objetoDados = Object.fromEntries(formData.entries());

    // --- IMPORTANTE: Verificação do JSON no Console ---
    console.log("Enviando para:", `${URL_BASE}/${endpoint}`);
    console.log("Dados:", JSON.stringify(objetoDados));

    try {
        const response = await fetch(`${URL_BASE}/${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(objetoDados)
        });

        if (response.ok) {
            alert("Salvo com sucesso!");
            location.reload(); 
        } else {
            alert("Erro ao salvar no Banco de Dados. Verifique se os campos estão corretos.");
        }
    } catch (error) {
        alert("Erro de conexão com o servidor Java.");
    }
});
