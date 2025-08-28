# 1. Adoção de Modelo de Processamento Bloqueante (Spring MVC)

**Decisão:**
Optou-se pelo modelo tradicional de processamento bloqueante, implementado com o stack Spring MVC, Spring Data JPA e um driver JDBC, ao invés de um model reativo.

**Consequências:**
* **Positivas:**
    * **Consistência Forte e Simples:** Este modelo permite o uso direto de transações ACID e locks pessimistas a nível de banco de dados. Essa é a forma testada pela indústria para garantir a atomicidade, evitando problemas em aplicações financeiras.
    * **Maturidade e Simplicidade:** O ecossistema bloqueante (JPA, JDBC) é maduro e simples.

* **Negativas:**
    * **Uso de Recursos:** O modelo "thread-per-request" pode ser menos eficiente em termos de uso de threads sob altíssima concorrência, pois as threads ficam bloqueadas esperando operações.

**Mitigação:**

A disponibilidade será alcançada através da escalabilidade horizontal. A aplicação é stateless e pode ser replicada em múltiplas instâncias usando um load balancer.