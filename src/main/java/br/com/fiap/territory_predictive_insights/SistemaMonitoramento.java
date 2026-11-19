package br.com.fiap.territory_predictive_insights;

import br.com.fiap.territory_predictive_insights.dominio.Alerta;
import br.com.fiap.territory_predictive_insights.dominio.DadosTerritorio;
import br.com.fiap.territory_predictive_insights.dominio.EstrategiaPrevisao;
import br.com.fiap.territory_predictive_insights.dominio.Medicao;
import br.com.fiap.territory_predictive_insights.dominio.Sensor;
import br.com.fiap.territory_predictive_insights.dominio.SensorFila;
import br.com.fiap.territory_predictive_insights.dominio.SensorOcupacao;
import br.com.fiap.territory_predictive_insights.dominio.SensorTempoEspera;
import br.com.fiap.territory_predictive_insights.dominio.UnidadeSaude;
import br.com.fiap.territory_predictive_insights.excecao.DadosInvalidosException;
import br.com.fiap.territory_predictive_insights.excecao.FonteIndisponivelException;
import br.com.fiap.territory_predictive_insights.persistencia.UnidadeSaudeRepository;
import br.com.fiap.territory_predictive_insights.servico.ConfiguracaoSistema;
import br.com.fiap.territory_predictive_insights.servico.FabricaPrevisao;
import br.com.fiap.territory_predictive_insights.servico.ServicoMonitoramento;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.List;
@Component
public class SistemaMonitoramento implements CommandLineRunner {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
    private static final String UNIDADE = "UBS Centro";

    private final UnidadeSaudeRepository unidadeRepository;
    private final ServicoMonitoramento servico;
    private final List<Sensor> sensores =
            List.of(new SensorFila(), new SensorTempoEspera(), new SensorOcupacao());

    public SistemaMonitoramento(UnidadeSaudeRepository unidadeRepository,
                                ServicoMonitoramento servico) {
        this.unidadeRepository = unidadeRepository;
        this.servico = servico;
    }
    private void titulo(String texto) {
        System.out.println("\n============================================");
        System.out.println(" " + texto.toUpperCase());
        System.out.println("============================================");
    }

    private Sensor escolherSensor(Scanner sc) {
        System.out.println("\nEscolha o sensor:");
        for (int i = 0; i < sensores.size(); i++) {
            System.out.println("  " + (i + 1) + " - " + sensores.get(i).getTipo());
        }
        System.out.print("Número do sensor: ");

        String mensagem = "Sensor inválido: digite um número de 1 a " + sensores.size();
        int numero;
        try {
            numero = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException(mensagem);
        }
        if (numero < 1 || numero > sensores.size()) {
            throw new DadosInvalidosException(mensagem);
        }
        return sensores.get(numero - 1);
    }

    private double lerNumero(Scanner sc, String pergunta) {
        System.out.print(pergunta);
        String texto = sc.nextLine().trim().replace(',', '.');   // aceita 35,5
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("\"" + texto + "\" não é um número. Use algo como 40 ou 35,5");
        }
    }

    @Override
    public void run(String... args) {
        if (unidadeRepository.count() == 0) {
            unidadeRepository.save(new UnidadeSaude("UBS Centro", "Centro"));
            unidadeRepository.save(new UnidadeSaude("UBS Norte", "Norte"));
        }
        Scanner sc = new Scanner(System.in);
        int opcao = -1;
        while (opcao != 0) {
            exibirMenu();
            try {
                opcao = Integer.parseInt(sc.nextLine().trim());
                executar(opcao, sc);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: digite um número.");
            } catch (DadosInvalidosException | FonteIndisponivelException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void exibirMenu() {
        System.out.println("\n=== Sistema de Monitoramento da UBS ===");
        System.out.println("1 - Situação atual da UBS");
        System.out.println("2 - Ver previsões e alertas");
        System.out.println("3 - Ver histórico de um sensor");
        System.out.println("4 - Ver alertas já emitidos");
        System.out.println("5 - Ajustar limites de alerta");
        System.out.println("6 - Ver resumo geral");
        System.out.println("7 - Gerar leituras de teste (10 rodadas)");
        System.out.println("0 - Sair\n");
        System.out.print("Escolha uma opção: ");
    }

    private void executar(int opcao, Scanner sc) {
        switch (opcao) {
            case 1 -> verificarSensores();
            case 2 -> rodarPrevisoes();
            case 3 -> verHistorico(sc);
            case 4 -> verAlertas();
            case 5 -> gerenciarTerritorio(sc);
            case 6 -> statusCompleto();
            case 7 -> gerarLeiturasDeTeste();
            case 0 -> System.out.println("Encerrando...");
            default -> System.out.println("Opção inválida.");
        }
    }

    private void verificarSensores() {
        titulo("Situação atual - " + UNIDADE);
        lerTodos(true);
    }

    private void gerarLeiturasDeTeste() {
        titulo("Gerando leituras de teste");
        int gravadas = 0;
        for (int i = 0; i < 10; i++) gravadas += lerTodos(false);
        int total = 10 * sensores.size();
        System.out.println("  " + gravadas + " leituras gravadas, " + (total - gravadas) + " falharam (simulação).");
    }

    private int lerTodos(boolean mostrar) {
        int gravadas = 0;
        for (Sensor s : sensores) {
            try {
                Medicao m = servico.registrarLeitura(s, UNIDADE);
                gravadas++;
                if (mostrar) System.out.printf("  %-13s %-26s [OK]%n", s.getTipo(), s.formatar(m.getValor()));
            } catch (FonteIndisponivelException | DadosInvalidosException e) {
                if (mostrar) System.out.printf("  %-13s %-26s [FALHA]%n", s.getTipo(), e.getMessage());
            }
        }
        return gravadas;
    }

    private void rodarPrevisoes() {
        titulo("Previsões e alertas");
        int janela = ConfiguracaoSistema.getInstance().getJanela();
        List<EstrategiaPrevisao> estrategias = List.of(
                FabricaPrevisao.criar("MEDIA_MOVEL", janela),
                FabricaPrevisao.criar("COMPARACAO", janela));
        for (Sensor s : sensores) {
            double limite = ConfiguracaoSistema.getInstance().getDados().getLimiar(s.getTipo());
            System.out.println("\n" + s.getTipo() + "  (limite: " + s.formatar(limite) + ")");
            for (EstrategiaPrevisao e : estrategias) {
                try {
                    double previsto = servico.prever(s.getTipo(), e);
                    System.out.printf("  %-32s %s%n", e.getNome(), s.formatar(previsto));
                    servico.emitirAlertaSeNecessario(s.getTipo(), UNIDADE, e.getNome(), previsto)
                            .ifPresent(a -> System.out.println("     >> [" + a.getNivel() + "] "
                                    + a.getNivel().getOrientacao()));
                } catch (DadosInvalidosException ex) {
                    System.out.printf("  %-32s %s%n", e.getNome(), ex.getMessage());
                }
            }
        }
    }

    private void verHistorico(Scanner sc) {
        Sensor sensor = escolherSensor(sc);
        List<Medicao> historico = servico.listarHistorico(sensor.getTipo());
        titulo("Histórico de " + sensor.getTipo() + " (" + historico.size() + " leituras)");
        if (historico.isEmpty()) {
            System.out.println("  Nenhuma leitura ainda. Use a opção 1 ou 7.");
            return;
        }
        System.out.printf("  %-5s %-16s %s%n", "ID", "QUANDO", "VALOR");
        for (Medicao m : historico) {
            System.out.printf("  %-5d %-16s %s%n", m.getId(),
                    m.getDataHora().format(FORMATO), sensor.formatar(m.getValor()));
        }
    }

    private void verAlertas() {
        List<Alerta> alertas = servico.listarAlertas();
        titulo("Alertas emitidos (" + alertas.size() + " no total)");
        if (alertas.isEmpty()) {
            System.out.println("  Nenhum alerta emitido até agora. Tudo sob " +
                    "controle");
            return;
        }
        int inicio = Math.max(0, alertas.size() - 10);
        for (Alerta a : alertas.subList(inicio, alertas.size())) {
            System.out.println("  [" + a.getNivel() + "] " + a.getDataHora().format(FORMATO));
            System.out.println("     " + a.getMensagem());
        }
    }

    private void gerenciarTerritorio(Scanner sc) {
        DadosTerritorio dados = ConfiguracaoSistema.getInstance().getDados();
        titulo("Ajustar limites de alerta");
        for (Sensor s : sensores) {
            System.out.printf("  %-13s limite atual: %s%n", s.getTipo(), s.formatar(dados.getLimiar(s.getTipo())));
        }
        Sensor sensor = escolherSensor(sc);
        double novo = lerNumero(sc, "Novo limite para " + sensor.getTipo() + ": ");
        System.out.print("Código de acesso: ");
        String codigo = sc.nextLine().trim();
        dados.setLimiar(sensor.getTipo(), novo, codigo);
        System.out.println("Limite atualizado: " + sensor.formatar(novo));
    }

    private void statusCompleto() {
        System.out.println("--- Unidades ---");
        for (UnidadeSaude u : unidadeRepository.findAll()) {
            System.out.println(u.verificarStatus());
        }
        System.out.println("--- Leituras gravadas ---");
        for (Sensor s : sensores) {
            System.out.println(s.getTipo() + ": " + servico.listarHistorico(s.getTipo()).size());
        }
        System.out.println("--- Alertas emitidos: " + servico.listarAlertas().size());
        System.out.println("--- Limiares: " + ConfiguracaoSistema.getInstance().getDados().getLimiares());
    }
}