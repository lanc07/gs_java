# Territory Predictive Insights: Monitor de Demanda da UBS

Global Solution · Programação Orientada a Objetos 

**Integrantes:** 
- Allan de Souza Cardoso · RM 561721
- Júlia Borges Paschoalinoto · RM 564725


---

## Público beneficiado e decisão apoiada

**Quem sofre o problema:** os pacientes de uma UBS, que enfrentam fila longa, tempo de espera alto e unidade lotada nos picos de procura.

**Quem usa o sistema:** o coordenador (gestor) da unidade.

**Decisão que o sistema apoia:** reforçar ou remanejar a equipe **antes** do pico. O sistema lê indicadores da unidade (fila, tempo de espera e ocupação), prevê o próximo período a partir do histórico e emite alertas explicados. A decisão continua sendo do gestor.

Nenhum dado pessoal é usado. O território é descrito por unidade e região, com valores agregados (número de pessoas, minutos, percentual), nunca por indivíduos.

---

## Como rodar

**Requisitos:** JDK 17 ou superior e internet na primeira execução (o Maven baixa as dependências).

```bash
git clone <URL-DO-REPOSITORIO>
cd territory-predictive-insights-1

# Windows (PowerShell)
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```


**Testes:**

```bash
.\mvnw.cmd test        # Windows
./mvnw test            # Linux / macOS
```

### Banco de dados

- Banco **H2 em modo arquivo** (`jdbc:h2:file:./data/territorio`), configurado em `src/main/resources/application.properties`.
- As tabelas são criadas automaticamente (`spring.jpa.hibernate.ddl-auto=update`) e os dados **sobrevivem ao reiniciar** o programa.
- Na primeira execução são cadastradas duas unidades: **UBS Centro** e **UBS Norte**.

### Código de acesso

A opção de ajustar limites de alerta exige o código **`1306`**.

---

## O que o sistema faz

| Opção do menu | Função |
|---|---|
| 1 | Nova leitura dos sensores: lê, valida e grava no banco |
| 2 | Previsões e alertas: roda as duas estratégias sobre o histórico do banco |
| 3 | Histórico de um sensor, vindo do banco |
| 4 | Alertas já emitidos, vindos do banco |
| 5 | Ajustar limites de alerta (exige código de acesso) |
| 6 | Resumo geral: unidades, leituras gravadas, alertas e limites |
| 7 | Gerar 10 rodadas de leituras de teste (simulação) |
| 0 | Sair |

### Sensores (valores simulados com `Random`)

| Sensor | Faixa simulada | Limite padrão |
|---|---|---|
| `FILA` | 0 a 80 pessoas | 30 |
| `TEMPO_ESPERA` | 5 a 180 minutos | 60 |
| `OCUPACAO` | 0 a 100 % | 85 |

Cada sensor simula uma falha em cerca de 5% das leituras, lançando `FonteIndisponivelException`.

### Previsões

Duas estratégias polimórficas, calculadas sobre o histórico gravado no banco (janela de 3 leituras):

- **Média móvel:** média das últimas 3 leituras (precisa de pelo menos 3).
- **Comparação com período anterior:** compara as últimas 3 leituras com as 3 anteriores e projeta a tendência (precisa de pelo menos 6).

### Alertas

A previsão é comparada ao limite do sensor:

| Nível | Quando |
|---|---|
| `ATENCAO` | previsão ≥ 80% do limite |
| `ALERTA` | previsão ≥ 100% do limite |
| `CRITICO` | previsão ≥ 120% do limite |

Cada alerta é gravado no banco e explica o porquê: qual sensor, valor previsto, limite e uma orientação para o gestor (por exemplo, "Considere reforçar o atendimento").

Exemplo de saída da opção 2:

```
FILA  (limite: 30 pessoas na fila)
  media movel                      52 pessoas na fila
     >> [CRITICO] Reforce a equipe agora: o limite deve ser ultrapassado com folga
```

---

## Conceitos de POO aplicados

| Conceito | Onde está |
|---|---|
| Classe abstrata | `ComponenteTerritorial` (método abstrato `verificarStatus()`) e `EstrategiaPrevisao` (método abstrato `prever()`) |
| Herança | `UnidadeSaude extends ComponenteTerritorial`; `PrevisaoMediaMovel` e `PrevisaoComparacao extends EstrategiaPrevisao`, usando `super()` |
| Interface | `Sensor`, implementada por `SensorFila`, `SensorTempoEspera` e `SensorOcupacao` |
| Polimorfismo | O serviço e o menu chamam `prever()` e `lerValor()` sem saber qual classe concreta está por trás, sem `if tipo ==` nem `instanceof` |
| Encapsulamento | `DadosTerritorio` (atributos privados, limiares só alterados com código de acesso e validação); `Medicao` sem setters |
| Exceções customizadas | `DadosInvalidosException` e `FonteIndisponivelException` |
| Persistência | Spring Data JPA: entidades `Medicao`, `Alerta` e `UnidadeSaude`; repositórios `MedicaoRepository`, `AlertaRepository` e `UnidadeSaudeRepository` |
| Padrões criacionais | `ConfiguracaoSistema` (Singleton) e `FabricaPrevisao` (Factory) |
| Testes | JUnit 5 e Mockito |

### Estrutura de pastas

```
src/main/java/br/com/fiap/territory_predictive_insights/
├── dominio/        Alerta, ComponenteTerritorial, DadosTerritorio, EstrategiaPrevisao,
│                   Medicao, NivelAlerta, PrevisaoComparacao, PrevisaoMediaMovel, Sensor,
│                   SensorFila, SensorTempoEspera, SensorOcupacao, UnidadeSaude
├── excecao/        DadosInvalidosException, FonteIndisponivelException
├── persistencia/   MedicaoRepository, AlertaRepository, UnidadeSaudeRepository
├── servico/        ServicoMonitoramento, ConfiguracaoSistema, FabricaPrevisao
├── SistemaMonitoramento.java                     (menu principal)
└── TerritoryPredictiveInsightsApplication.java   (classe principal do Spring Boot)

src/test/java/br/com/fiap/territory_predictive_insights/
└── MedicaoTest, PrevisaoTest, DadosTerritorioTest, ServicoMonitoramentoTest
```

---

## Decisões do projeto

- **Spring Boot + JPA em vez de JDBC puro:** menos código de persistência e prática com uma tecnologia usada no mercado.
- **H2 em arquivo:** não exige instalação e mantém o histórico entre execuções, o que a previsão precisa.
- **Menu de console dentro do Spring** (`CommandLineRunner`): o enunciado dispensa API REST e interface gráfica.
- **Validação em camadas:** a entidade valida o que depende só dela (valor negativo, texto vazio). A regra "a unidade existe?" fica no serviço, porque consulta o banco e a entidade não deve fazer isso. Isso também permite testar a regra com Mockito, sem banco.
- **`Medicao` sem setters:** uma leitura já feita não deve ser alterada.

- **Recorte "demanda por serviços (UBS)":** escolhido por ser um problema real e comum no atendimento público de saúde, com indicadores simples de medir.
- **Sensores simulados:** valores aleatórios em faixas realistas, como o enunciado permite. A interface `Sensor` isola a fonte dos dados, então usar dados reais mudaria só o método `lerValor()`.
- **Níveis de alerta em percentual do limite** (80%, 100% e 120%), e não em valores fixos por sensor. Assim a mesma regra serve para fila, tempo de espera e ocupação, mesmo com unidades de medida diferentes.
- **Exceções customizadas não verificadas (`RuntimeException`):** evitam `throws` em toda a cadeia de chamadas, e o tratamento fica concentrado no menu.
- **Getters e validações escritos à mão, sem Lombok:** o encapsulamento fica explícito no código.
- **Segurança:** o código de acesso está fixo no código-fonte 
  (`ConfiguracaoSistema`). Serve para o trabalho, mas num sistema real viria de uma configuração segura.
- **Limitação:** os limites ajustados pela opção 5 ficam na memória e voltam 
  ao padrão ao reiniciar o programa.


---

## Padrões criacionais: justificativa

**Singleton, `ConfiguracaoSistema`:** os limiares e a janela de previsão precisam ser **únicos** em todo o sistema. A opção 5 do menu altera um limite e o serviço de alertas lê esse mesmo valor na hora de classificar. Com uma única instância, os dois enxergam sempre o mesmo dado. Não faz sentido aplicar em entidades (`Medicao`, `Alerta`), que têm várias instâncias por natureza.

**Factory, `FabricaPrevisao`:** concentra a criação das estratégias de previsão em um único lugar, por meio de um catálogo (`Map`). O menu e o serviço pedem a estratégia pelo nome e recebem o objeto pronto, sem `if` espalhado. Para adicionar uma terceira estratégia, basta uma nova classe e uma linha no catálogo. Ela fica na criação das estratégias, e não nos sensores, porque é ali que existe variação de tipo escolhida em tempo de execução.

---

##  Testes

Testes unitários com JUnit 5, no padrão AAA (Arrange, Act, Assert), sem depender de banco nem de digitação no console.

| Classe | O que verifica |
|---|---|
| `MedicaoTest` | valor negativo lança `DadosInvalidosException` (`assertThrows`); dados válidos são guardados |
| `PrevisaoTest` | média móvel calculada corretamente; histórico curto lança exceção; comparação projeta tendência de alta |
| `DadosTerritorioTest` | alteração de limiar com código correto; código errado e limiar inválido (`NaN`, negativo) são recusados |
| `ServicoMonitoramentoTest` | **com Mockito** (repositórios simulados): alerta crítico emitido e gravado; nenhum alerta quando a previsão é baixa; medição não é gravada quando a unidade não existe |

---

## Reflexão crítica

**Quando o sistema deve se calar?**
Quando não há dados suficientes. A média móvel precisa de 3 leituras e a comparação de 6. Com menos que isso, o sistema recusa a previsão com a mensagem "histórico insuficiente" (`DadosInvalidosException`) em vez de chutar um número e gerar um alerta sem base. Mesmo com o mínimo atingido, poucas leituras ainda dão uma previsão frágil, e um sistema real exigiria mais histórico, cobrindo diferentes horários e dias. O sistema também se cala quando o sensor falha: a leitura não é inventada, e o erro é informado ao usuário.

**O que os dados não representam?**
Os valores são simulados de forma aleatória, então não carregam padrões reais como horário de pico, dia da semana, campanhas de vacinação ou surtos sazonais. Fila e ocupação dizem quanto de gente há, mas não a gravidade dos casos nem o motivo da procura. Os indicadores representam uma única unidade, e o histórico mistura toda a série do sensor, sem separar por unidade. Uma média simples também suaviza picos curtos, que podem ser exatamente o que o gestor precisa enxergar.

**Por que o alerta apoia a decisão da pessoa, em vez de decidir por ela?**
Porque o gestor conhece o que o sistema não conhece: quem está de folga, se há um evento no bairro, se a fila alta é só um dia atípico. A previsão pode errar, e um sistema que ordena ações pode gerar falsos alarmes e perder a confiança de quem o usa. Por isso cada alerta explica qual sensor, qual valor foi previsto e qual limite foi atingido, e traz apenas uma **orientação** ("considere reforçar o atendimento"). Os limites também são ajustáveis pelo gestor, protegidos por código de acesso. A decisão final continua sendo humana.

---

## Uso de IA

Foi usado o **Claude (Anthropic)** como apoio ao longo do projeto.

**Onde foi usado:**
- Diagnóstico de erros de compilação e de configuração (Maven e IntelliJ).
- Estruturação/formatação do readme.

**Como foi revisado:**

- Erros apontados pela IDE e pelo Maven foram investigados e corrigidos.

- Readme foi revisado e preenchido com as partes faltantes.

---

##  Divisão de tarefas

foi feito em pair programming :)

