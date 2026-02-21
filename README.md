# 🚀 Workshop Spring Boot & JPA/Hibernate

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot 3">
  <img src="https://img.shields.io/badge/Database-H2/PostgreSQL-0064a5?style=for-the-badge&logo=postgresql&logoColor=white" alt="Database">
</p>

<p align="center">
  <b>API RESTful completa para gerenciamento de pedidos, produtos e categorias.</b>
</p>

---

## 📖 Sobre o Projeto
Este projeto é um Web Service robusto desenvolvido para consolidar conhecimentos em **Spring Boot 3** e **Jência de dados com JPA/Hibernate**. A aplicação implementa um domínio completo de e-commerce, incluindo usuários, pedidos, produtos, categorias e pagamentos.

### 🎯 Objetivos Técnicos
* Implementação de um modelo de domínio complexo.
* Estruturação de camadas lógicas: **Resource**, **Service** e **Repository**.
* Povoamento automático de banco de dados (Database Seeding).
* Tratamento de exceções personalizado para APIs REST.
* Relacionamentos Muitos-para-Muitos (Many-to-Many) com tabelas de associação.

---

## 🏗️ Estrutura e Camadas

O projeto segue o padrão de arquitetura em camadas para garantir escalabilidade e manutenção:

| Camada | Responsabilidade |
| :--- | :--- |
| **Resource** | Controladores REST que expõem os endpoints da API. |
| **Service** | Regras de negócio e intermediação entre Resource e Repository. |
| **Repository** | Interfaces de acesso a dados (Spring Data JPA). |
| **Domain** | Entidades principais e mapeamento objeto-relacional. |

---

## 🛠️ Tecnologias Utilizadas

<div align="center">

| Tecnologia | Finalidade |
| :--- | :--- |
| **Spring Data JPA** | Persistência de dados e consultas facilitadas. |
| **Hibernate** | Mecanismo de ORM (Object-Relational Mapping). |
| **H2 Database** | Banco de dados em memória para testes. |
| **Maven** | Gerenciador de dependências e build. |
| **PostgreSQL** | Banco de dados para ambiente de produção. |

</div>

---

## 🔗 Modelo Conceitual (Domínio)
A aplicação gerencia as seguintes entidades e relacionamentos:
- **User** 1 ➡️ N **Order** (Um usuário tem vários pedidos)
- **Order** 1 ➡️ 1 **Payment** (Um pedido tem um pagamento)
- **Product** N ➡️ N **Category** (Produtos e categorias se relacionam em muitos-para-muitos)
- **Order** N ➡️ N **Product** (Através da entidade associativa `OrderItem`)

---

## ⚙️ Como Executar

```bash
# 1. Clone o repositório
$ git clone [https://github.com/KetlinOlliveira/workshop-springboot-jpa.git](https://github.com/KetlinOlliveira/workshop-springboot-jpa.git)

# 2. Entre na pasta do projeto
$ cd workshop-springboot-jpa

# 3. Execute o projeto com o Maven
$ ./mvnw spring-boot:run

```
<div align="center">
<p>Projeto desenvolvido como parte de estudos avançados em Java e Spring Boot. 💻</p>
</div>
