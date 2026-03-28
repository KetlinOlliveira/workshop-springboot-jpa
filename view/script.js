const URL_BASE = "http://localhost:8080";

const tradutorStatus = {
    1: "Aguardando Pagamento",
    2: "Pago",
    3: "Enviado",
    4: "Entregue",
    5: "Cancelado"
};


async function carregarDados(endpoint, colunas) {
    const corpoTabela = document.getElementById('corpo-tabela');
    const cabecalhoTabela = document.getElementById('cabecalho-tabela');

    corpoTabela.innerHTML = "<tr><td colspan='100%'>Carregando...</td></tr>";

    try{
        const response = await fetch(`${URL_BASE}/${endpoint}`);
        const dados = await response.json();

        cabecalhoTabela.innerHTML = colunas.map(col => `<th>${col.toUpperCase()}</th>`).join('') + `<th>AÇÕES</th>`;

        // o corpo faz a tradução dos valores (td)
        corpoTabela.innerHTML = dados.map(item => `
            <tr>
                ${colunas.map(col => {
                    let valor = item[col] || '---';
                    
                    // Tradução do Status do Pedido
                    if (col === 'orderStatus' && tradutorStatus[valor]) {
                        valor = tradutorStatus[valor];
                    }
                    
                    return `<td>${valor}</td>`;
                }).join('')}
                <td>
                    <div style="display: flex; gap: 8px;">
                        ${endpoint === 'orders' ? 
                            `<button class="btn-detalhes" onclick="verDetalhes(${item.id})">Ver detalhes</button>` : 
                            ''
                        }
                        <button class="btn-excluir" onclick="excluirItem('${endpoint}', ${item.id})">Excluir</button>
                    </div>
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
        inputs.innerHTML = `<p style="grid-column: 1/-1;">Carregando dados do servidor...</p>`;

        // Busca Clientes e Produtos ao mesmo tempo
        Promise.all([
            fetch(`${URL_BASE}/users`).then(res => res.json()),
            fetch(`${URL_BASE}/products`).then(res => res.json())
        ]).then(([users, products]) => {
            
            // Monta as opções do Select
            let opcoesUsuarios = users.map(u => `<option value="${u.id}">${u.name}</option>`).join('');
            let opcoesProdutos = products.map(p => `<option value="${p.id}">📦 ${p.name} - R$ ${p.price}</option>`).join('');

            window.opcoesProdutosGlobais = products.map(p => `<option value="${p.id}">📦 ${p.name} - R$ ${p.price}</option>`).join('');

        inputs.innerHTML = `
            <div style="display: flex; flex-direction: column; gap: 5px;">
                <label style="font-size: 13px; color: var(--text-muted);">Cliente:</label>
                <select name="clientId" required class="grid-inputs">
                    <option value="">-- Escolha --</option>
                    ${users.map(u => `<option value="${u.id}">${u.name}</option>`).join('')}
                </select>
            </div>

            <div style="display: flex; flex-direction: column; gap: 5px;">
                <label style="font-size: 13px; color: var(--text-muted);">Status:</label>
                <select name="orderStatus" id="novo-status" class="grid-inputs">
                    <option value="1">Aguardando Pagamento</option>
                    <option value="2">Pago</option>
                    <option value="3">Enviado</option>
                    <option value="4">Entregue</option>
                    <option value="5">Cancelado</option>
                </select>
            </div>

            <!-- CONTAINER DE ITENS -->
            <div id="container-itens" style="grid-column: 1 / -1; margin-top: 10px; padding-top: 10px; border-top: 1px dashed var(--border-color);">
                <h4 style="font-size: 14px; margin-bottom: 10px;">Itens do Pedido</h4>
                
                <!-- Linha Inicial (TEMPLATE) -->
                <div class="item-linha" style="display: grid; grid-template-columns: 2fr 1fr auto; gap: 15px; margin-bottom: 10px;">
                    <select name="productId" required class="grid-inputs select-produto">
                        <option value="">-- Produto --</option>
                        ${window.opcoesProdutosGlobais}
                    </select>
                    <input type="number" name="quantity" min="1" value="1" class="grid-inputs input-quantidade" required>
                    <button type="button" onclick="this.parentElement.remove()" style="background:none; border:none; color:red; cursor:pointer; font-weight:bold;">X</button>
                </div>
            </div>

            <button type="button" onclick="adicionarLinhaItem()" style="grid-column: 1 / -1; padding: 8px; background: #eee; border: 1px solid #ccc; border-radius: 5px; cursor: pointer;">
                + Adicionar outro produto
            </button>
`;
        }).catch(error => {
            inputs.innerHTML = `<p style="color: red; grid-column: 1/-1;">Erro ao carregar dados. Verifique se a API está rodando.</p>`;
        });

        carregarDados('orders', ['id', 'moment', 'orderStatus']);
    }
}

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
    
    const elementoTitulo = document.getElementById('titulo-secao');
    if (!elementoTitulo) return;

    const tituloTexto = elementoTitulo.innerText.toLowerCase();
    let endpoint = "";
    
    if (tituloTexto.includes("usuarios")) endpoint = "users";
    else if (tituloTexto.includes("produtos")) endpoint = "products";
    else if (tituloTexto.includes("categorias")) endpoint = "categories";
    else if (tituloTexto.includes("pedidos")) endpoint = "orders";

    const formData = new FormData(e.target);
    let objetoDados = Object.fromEntries(formData.entries());

    if (endpoint === "orders") {
        //Captura os dados básicos do pedido
        const clientId = parseInt(formData.get('clientId'));
        const statusDigitado = parseInt(formData.get('orderStatus'));
        const dataAtual = new Date().toISOString().split('.')[0] + "Z";

        // Captura TODOS os itens das linhas dinâmicas
        const linhasItens = document.querySelectorAll('.item-linha');
        const items = [];

        linhasItens.forEach(linha => {
            const selectProd = linha.querySelector('.select-produto');
            const inputQtd = linha.querySelector('.input-quantidade');

            if (selectProd.value) {
                // Pegar o preço que está no texto da option: "📦 Nome - R$ 100.00"
                const textoOption = selectProd.options[selectProd.selectedIndex].text;
                const precoReal = parseFloat(textoOption.split('R$ ')[1]) || 0;

                items.push({
                    product: { id: parseInt(selectProd.value) },
                    price: precoReal,
                    quantity: parseInt(inputQtd.value)
                });
            }
        });

        // Monta o objeto final para o Spring Boot
        objetoDados = {
            moment: dataAtual,
            orderStatus: statusDigitado, 
            client: { id: clientId },
            items: items 
        };

    } else if (endpoint === "products") {
        objetoDados.price = parseFloat(objetoDados.price);
    }

    console.log("Enviando JSON:", JSON.stringify(objetoDados));

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
            const erroDetallhado = await response.json().catch(() => ({}));
            console.error("Erro do servidor:", erroDetallhado);
            alert("Erro ao salvar. Verifique o console para detalhes.");
        }
    } catch (error) {
        alert("Erro de conexão com o servidor Java.");
    }
});

async function verDetalhes(id) {
    try {
        const response = await fetch(`${URL_BASE}/orders/${id}`);
        const pedido = await response.json();

        document.getElementById('detalhe-id').innerText = pedido.id;
        const conteudo = document.getElementById('conteudo-modal');
        
        // Calculando total e listando itens
        let itensHtml = pedido.items.map(item => `
            <div class="item-detalhe">
                <span>${item.productName} (x${item.quantity})</span>
                <strong>R$ ${item.price.toFixed(2)}</strong>
            </div>
        `).join('');
        conteudo.innerHTML = `
            <div style="margin-bottom: 20px;">
                <p><strong>Cliente:</strong> ${pedido.clientName}</p>
                <p><strong>Data:</strong> ${new Date(pedido.moment).toLocaleString()}</p>
            </div>
            
            <h4 style="margin-bottom: 10px;">Itens do Pedido:</h4>
            ${itensHtml}
            
            <div style="margin-top: 15px; text-align: right; font-size: 1.2rem; color: var(--primary-color);">
                <strong>Total: R$ ${pedido.total.toFixed(2)}</strong>
            </div>

            <hr style="margin: 20px 0; border: 0; border-top: 1px solid #eee;">
            <label>Alterar Status:</label>
            <select id="status-modal" class="grid-inputs" style="margin-top: 5px;">
                <option value="WAITING_PAYMENT" ${pedido.orderStatus === 'WAITING_PAYMENT' ? 'selected' : ''}>Aguardando Pagamento</option>
                <option value="PAID" ${pedido.orderStatus === 'PAID' ? 'selected' : ''}>Pago</option>
                <option value="SHIPPED" ${pedido.orderStatus === 'SHIPPED' ? 'selected' : ''}>Enviado</option>
                <option value="DELIVERED" ${pedido.orderStatus === 'DELIVERED' ? 'selected' : ''}>Entregue</option>
                <option value="CANCELED" ${pedido.orderStatus === 'CANCELED' ? 'selected' : ''}>Cancelado</option>
            </select>
            
            <button onclick="atualizarStatus(${pedido.id})" class="btn-primary" style="width: 100%; margin-top: 15px;">Atualizar Pedido</button>
        `;

        document.getElementById('modalDetalhes').style.display = 'flex';
    } catch (error) {
        console.error('Erro:', error);
        alert("Erro ao carregar detalhes do pedido.");
    }
}

function fecharModal() {
    const modal = document.getElementById('modalDetalhes');
    if (modal) {
        modal.style.display = 'none';
    }
}

// fechar ao clicar fora da caixinha branca
window.onclick = function(event) {
    const modal = document.getElementById('modalDetalhes');
    if (event.target == modal) {
        fecharModal();
    }
}

async function atualizarStatus(id) {
    // Busca o ID específico do modal
    const selectModal = document.getElementById('status-modal');
    
    if (!selectModal) {
        console.error("Elemento status-modal não encontrado!");
        return;
    }

    const dados = {
        orderStatus: selectModal.value
    };

    console.log("Enviando dados:", dados);
    console.log("URL:", `${URL_BASE}/orders/${id}`);

    try {
        const response = await fetch(`${URL_BASE}/orders/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        });

        console.log("Status da resposta:", response.status);
        const responseText = await response.text();
        console.log("Resposta da API:", responseText);

        if (response.ok) {
            alert("Status atualizado com sucesso!");
            fecharModal();
            // Atualiza a tabela de pedidos sem recarregar a página inteira
            mostrarSecao('pedidos');
        } else {
            alert(`Erro ao atualizar (${response.status}): ${responseText}`);
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
        alert("Erro na requisição: " + error.message);
    }
}

function adicionarLinhaItem() {
    const container = document.getElementById('container-itens');
    const novaLinha = document.createElement('div');
    novaLinha.className = 'item-linha';
    novaLinha.style = "display: grid; grid-template-columns: 2fr 1fr auto; gap: 15px; margin-bottom: 10px;";
    
    novaLinha.innerHTML = `
        <select name="productId" required class="grid-inputs select-produto">
            <option value="">-- Produto --</option>
            ${window.opcoesProdutosGlobais}
        </select>
        <input type="number" name="quantity" min="1" value="1" class="grid-inputs input-quantidade" required>
        <button type="button" onclick="this.parentElement.remove()" style="background:none; border:none; color:red; cursor:pointer; font-weight:bold;">X</button>
    `;
    
    container.appendChild(novaLinha);
}
