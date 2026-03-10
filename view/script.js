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

            inputs.innerHTML = `
                <!-- LINHA 1: Cliente e Status -->
                <div style="display: flex; flex-direction: column; gap: 5px;">
                    <label style="font-size: 13px; color: var(--text-muted);">Cliente:</label>
                    <select name="clientId" required style="padding: 12px; border: 1px solid var(--border-color); border-radius: 10px; outline: none;">
                        <option value="">-- Escolha --</option>
                        ${opcoesUsuarios}
                    </select>
                </div>

                <div style="display: flex; flex-direction: column; gap: 5px;">
                    <label style="font-size: 13px; color: var(--text-muted);">Status:</label>
                    <select id="novo-status" class="grid-inputs">
                        <option value="1">Aguardando Pagamento</option>
                        <option value="2">Pago</option>
                        <option value="3">Enviado</option>
                        <option value="4">Entregue</option>
                         <option value="5">Cancelado</option>
                    </select>
                </div>

                <!-- LINHA 2: Produto e Quantidade -->
                <div style="grid-column: 1 / -1; margin-top: 10px; padding-top: 10px; border-top: 1px dashed var(--border-color);">
                    <h4 style="font-size: 14px; margin-bottom: 10px;">Adicionar Item</h4>
                    <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 15px;">
                        <select name="productId" required style="padding: 12px; border: 1px solid var(--border-color); border-radius: 10px; outline: none;">
                            <option value="">-- Produto --</option>
                            ${opcoesProdutos}
                        </select>
                        <input type="number" name="quantity" min="1" value="1" placeholder="Qtd" required style="padding: 12px; border: 1px solid var(--border-color); border-radius: 10px; outline: none;">
                    </div>
                </div>
            `;
        }).catch(error => {
            inputs.innerHTML = `<p style="color: red; grid-column: 1/-1;">Erro ao carregar dados. Verifique se a API está rodando.</p>`;
        });

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
    let objetoDados = Object.fromEntries(formData.entries());

    if (endpoint === "orders") {

        const productSelect = document.querySelector('select[name="productId"]');
        // Pega os valores dos selects
        const clientId = parseInt(formData.get('clientId'));
        const productId = parseInt(formData.get('productId'));
        const quantity = parseInt(formData.get('quantity'));
        const statusDigitado = parseInt(formData.get('orderStatus'));

        const textoOption = productSelect.options[productSelect.selectedIndex].text;
        const precoReal = parseFloat(textoOption.split('R$ ')[1]) || 0;

        const dataAtual = new Date().toISOString().split('.')[0] + "Z";

        // Remonta o objeto no padrão que o Spring Boot espera, incluindo a data atual no formato ISO
        objetoDados = {
            moment: dataAtual,
            orderStatus: statusDigitado, 
            client: { 
                id: clientId 
            },
            items: [
                {
                    product: { id: productId },
                    price: precoReal,// O preço será calculado no backend com base no produto
                    quantity: quantity
                }
            ]
        };
    } else if (endpoint === "products") {
        // Garante que o preço vá como número decimal e não string
        objetoDados.price = parseFloat(objetoDados.price);
    }

    // Log para verificar o que está sendo enviado
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

async function verDetalhes(id) {
    try {
        const response = await fetch(`${URL_BASE}/orders/${id}`);
        const pedido = await response.json();

        document.getElementById('detalhe-id').innerText = pedido.id;
        const conteudo = document.getElementById('conteudo-modal');
        
        // Calculando total e listando itens
        let itensHtml = pedido.items.map(item => `
            <div class="item-detalhe">
                <span>${item.product.name} (x${item.quantity})</span>
                <strong>R$ ${item.price.toFixed(2)}</strong>
            </div>
        `).join('');
        conteudo.innerHTML = `
            <div style="margin-bottom: 20px;">
                <p><strong>Cliente:</strong> ${pedido.client.name}</p>
                <p><strong>Email:</strong> ${pedido.client.email}</p>
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
                <option value="1" ${pedido.orderStatus === 'WAITING_PAYMENT' ? 'selected' : ''}>Aguardando Pagamento</option>
                <option value="2" ${pedido.orderStatus === 'PAID' ? 'selected' : ''}>Pago</option>
                <option value="3" ${pedido.orderStatus === 'SHIPPED' ? 'selected' : ''}>Enviado</option>
                <option value="4" ${pedido.orderStatus === 'DELIVERED' ? 'selected' : ''}>Entregue</option>
                <option value="5" ${pedido.orderStatus === 'CANCELED' ? 'selected' : ''}>Cancelado</option>
            </select>
            
            <button onclick="atualizarStatus(${pedido.id})" class="btn-primary" style="width: 100%; margin-top: 15px;">Atualizar Pedido</button>
        `;

        document.getElementById('modalDetalhes').style.display = 'flex';
    } catch (error) {
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
    // Buscamos o ID específico do modal
    const selectModal = document.getElementById('status-modal');
    
    if (!selectModal) {
        console.error("Elemento status-modal não encontrado!");
        return;
    }

    const dados = {
        orderStatus: parseInt(selectModal.value)
    };

    try {
        const response = await fetch(`${URL_BASE}/orders/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        });

        if (response.ok) {
            alert("Status atualizado com sucesso!");
            fecharModal();
            // Atualiza a tabela de pedidos sem recarregar a página inteira
            mostrarSecao('pedidos');
        } else {
            alert("Erro ao atualizar no Java. Verifique se o ID existe no banco.");
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
    }
}