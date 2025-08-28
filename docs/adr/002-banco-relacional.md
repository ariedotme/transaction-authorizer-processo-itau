# 2. Utilização de Banco de Dados Relacional (PostgreSQL)

**Decisão:**
Optou-se por utilizar o PostgreSQL como banco de dados principal da aplicação.

**Consequências:**
* **Positivas:**
    * **ACID:** O PostgreSQL oferece total conformidade com ACID (Atomicidade, Consistência, Isolamento e Durabilidade), o que é mandatório para transações financeiras.
    * **Integridade:** Bancos relacionais, com seu schema bem definido e constraints, garante a integridade dos dados.
    * **Ecossistema Maduro:** Possui um ecossistema maduro e é compatível com as ferramentas escolhidas (JPA/Hibernate).

* **Negativas:**
    * **Complexidade de Escala de Input:** A escalabilidade horizontal de escrita em bancos de dados relacionais pode ser mais complexa do que em alguns bancos NoSQL.

**Mitigação:**

Uma instância primária de PostgreSQL com réplicas de leitura oferece alta performance e disponibilidade. A decisão prioriza a consistência e a integridade ao custo de escabilidade de escrita, que não é o principal gargalo desse usecase.