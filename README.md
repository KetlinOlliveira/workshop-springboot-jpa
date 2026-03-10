<div align="center">
  <h1>🚀DashBoard & WebService</h1>
</div>


<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot 3">
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" alt="JavaScript">
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white" alt="HTML5">
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white" alt="CSS3">
  <img src="https://img.shields.io/badge/Database-H2/PostgreSQL-0064a5?style=for-the-badge&logo=postgresql&logoColor=white" alt="Database">
  <img src="https://img.shields.io/badge/-Swagger-%2385EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger">
</p>

<div align="center">
  <h2>🌟 Novidades: Interface de Gestão Integrada</h2> 
  O projeto evoluiu de um Web Service isolado para uma plataforma Full Stack. Agora, além da API robusta, a aplicação conta com um Dashboard Dinâmico que permite a gestão completa dos dados (CRUD)       diretamente pelo navegador.
  
</div>

---

## 📖 Sobre o Projeto
Originalmente concebido como um Web Service para estudo pessoal, o projeto implementa um domínio de e-commerce e gestão de serviços. A arquitetura foi expandida para suportar uma camada de visualização (Frontend) que consome a API de forma assíncrona.

### 🎯 Objetivos Técnicos
* Implementação de um modelo de domínio complexo.
* Consumo de API Assíncrona: Implementação de Fetch API com async/await.
* Estruturação de camadas lógicas: **Resource**, **Service** e **Repository**.
* Povoamento automático de banco de dados (Database Seeding).
* Tratamento de exceções personalizado para APIs REST.
* Relacionamentos Muitos-para-Muitos (Many-to-Many) com tabelas de associação.

---

## 🏗️ Estrutura e Camadas

<div align="center">
O projeto segue o padrão de arquitetura em camadas para garantir escalabilidade e manutenção:

| Camada | Tecnologia | Responsabilidade |
| :--- | :--- | :---
| **FrontEnd** | HTML5 / CSS3 / JS | Controladores REST que expõem os endpoints da API. |
| **Resource** | Spring RestController |  Controladores REST que expõem os endpoints da API. |
| **Service** | Spring Service| Regras de negócio e intermediação entre Resource e Repository. |
| **Repository** | Spring JPA |Interfaces de acesso a dados (Spring Data JPA). |

</div>


## 🛠️ Tecnologias Utilizadas

<div align="center">

| Tecnologia | Finalidade |
| :--- | :--- |
| **JavaScript ES6+** | Lógica de injeção de templates e chamadas assíncronas. |
| **Spring Data JPA** | Persistência de dados e consultas facilitadas. |
| **Springdoc OpenAPI** | Geração automática da documentação Swagger. |
| **Fetch API** | Comunicação entre o navegador e o servidor Java.|
| **CSS Grid/FlexBox**|Comunicação entre o navegador e o servidor Java.
| **H2 Database** | Layout moderno e responsivo para o Dashboard. |
| **PostgreSQL** | Banco de dados para ambiente de produção. |

</div>

## 🚀 Funcionalidades

- **Painel de Controle**: Visualização rápida de todas as entidades do sistema.
- **Gerenciamento de Usuários**: CRUD completo para usuários do sistema.
- **Catálogo de Produtos**: Listagem e categorias de itens.
- **Fluxo de Pedidos**: Registro de ordens de serviço e pagamentos.
- **Documentação Viva**: Teste de rotas `GET`, `POST`, `PUT` e `DELETE` via interface gráfica.

---

## 🔗 Modelo Conceitual (Domínio)
A aplicação gerencia as seguintes entidades e relacionamentos:
- **User** 1 ➡️ N **Order** (Um usuário tem vários pedidos)
- **Order** 1 ➡️ 1 **Payment** (Um pedido tem um pagamento)
- **Product** N ➡️ N **Category** (Produtos e categorias se relacionam em muitos-para-muitos)
- **Order** N ➡️ N **Product** (Através da entidade associativa `OrderItem`)

---

## ⚙️ Como Executar (Java)

```bash
# 1. Clone o repositório
$ git clone [https://github.com/KetlinOlliveira/workshop-springboot-jpa.git](https://github.com/KetlinOlliveira/workshop-springboot-jpa.git)

# 2. Entre na pasta do projeto
$ cd workshop-springboot-jpa

# 3. Execute o projeto com o Maven
$ ./mvnw spring-boot:run

```

## 💻 FrontEnd (DashBoard)
Comunicação entre o navegador e o servidor Java.

```bash
Caminho: /view/index.html
Ação: Abrir com o Navegador (ou Live Server no VS Code)

```

## 📚 Documentação da API (Swagger)

O projeto utiliza o **Swagger (OpenAPI 3)** para facilitar o consumo e teste dos endpoints. Com ele, você pode visualizar todos os recursos disponíveis e testar as requisições em tempo real.

<p align="left">
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger UI">
</p>

### 🔗 Como acessar:
```bash

# 1. Com a aplicação rodando, abra o seu navegador.

# 2. Acesse o endereço: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

```
<div align="center">
<p>Projeto desenvolvido como parte de estudos avançados em Java e Spring Boot. 💻</p>
</div>
