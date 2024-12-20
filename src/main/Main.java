package main;

import factory.*;
import model.*;
import factory.ItemBiblioteca;
import observer.*;
import strategy.*;
import service.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Serviços e variáveis principais
        UsuarioService usuarioService = new UsuarioService();
        BibliotecaService bibliotecaService = new BibliotecaService();
        HistoricoEmprestimo historicoEmprestimo = new HistoricoEmprestimo();

        boolean running = true;

        while (running) {
            System.out.println("\n==== Menu do Sistema ====");
            System.out.println("1. Cadastrar Usuário");
            System.out.println("2. Gerenciar Itens da Biblioteca");
            System.out.println("3. Registrar Empréstimo");
            System.out.println("4. Registrar Devolução");
            System.out.println("5. Fazer Reserva de Item");
            System.out.println("6. Consultar Histórico de Empréstimos");
            System.out.println("7. Buscar Item no Catálogo");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1:
                    cadastrarUsuario(scanner, usuarioService);
                    break;
                case 2:
                    gerenciarItens(scanner, bibliotecaService);
                    break;
                case 3:
                    registrarEmprestimo(scanner, usuarioService, bibliotecaService, historicoEmprestimo);
                    break;
                case 4:
                    registrarDevolucao(scanner, historicoEmprestimo);
                    break;
                case 5:
                    fazerReserva(scanner, usuarioService, bibliotecaService);
                    break;
                case 6:
                    consultarHistorico(scanner, usuarioService, historicoEmprestimo);
                    break;
                case 7:
                    buscarItem(scanner, bibliotecaService);
                    break;
                case 0:
                    running = false;
                    System.out.println("Encerrando o sistema...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }

        scanner.close();
    }

    private static void cadastrarUsuario(Scanner scanner, UsuarioService usuarioService) {
        System.out.print("Digite o nome: ");
        String nome = scanner.nextLine();
        System.out.print("Digite a matrícula/função: ");
        String matricula = scanner.nextLine();
        System.out.print("Digite o telefone: ");
        String telefone = scanner.nextLine();
        System.out.print("Digite o tipo (Aluno/Professor/Funcionário): ");
        String tipo = scanner.nextLine();

        Usuario usuario = new Usuario(nome, matricula, telefone, tipo);
        if (tipo.equalsIgnoreCase("Aluno")) {
            System.out.print("Digite o curso: ");
            String curso = scanner.nextLine();
            usuario.setCurso(curso);
        } else {
            System.out.print("Digite o departamento: ");
            String departamento = scanner.nextLine();
            usuario.setDepartamento(departamento);
        }

        usuarioService.cadastrarUsuario(usuario);
        System.out.println("Usuário cadastrado com sucesso!");
    }

    private static void gerenciarItens(Scanner scanner, BibliotecaService bibliotecaService) {
        System.out.println("\n=== Gerenciar Itens ===");
        System.out.println("1. Adicionar Item");
        System.out.println("2. Atualizar Item");
        System.out.println("3. Excluir Item");
        System.out.println("4. Consultar Itens");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        switch (opcao) {
            case 1:
                System.out.print("Digite o título: ");
                String titulo = scanner.nextLine();
                System.out.print("Digite o autor: ");
                String autor = scanner.nextLine();
                System.out.print("Digite o tipo (Livro/Periódico): ");
                String tipo = scanner.nextLine();
                System.out.print("Digite a area do item: ");
                String area = scanner.nextLine();

                ItemBiblioteca item;
                if (tipo.equalsIgnoreCase("Livro")) {
                    item = ItemBibliotecaFactory.criaItem(titulo, autor, tipo, area);
                    
                } else {
                    item = ItemBibliotecaFactory.criaItem(titulo, autor, tipo, area);
                                       
                }

                bibliotecaService.adicionarItem(item);
                System.out.println("Item adicionado ao catálogo!");
                break;
            case 2:
                System.out.print("Digite o título do item a ser atualizado: ");
                String tituloAtualizar = scanner.nextLine();
                ItemBiblioteca itemAtualizar = bibliotecaService.buscarPorTitulo(tituloAtualizar).get(0);
                System.out.print("Disponibilidade (true/false): ");
                boolean disponibilidade = scanner.nextBoolean();
                itemAtualizar.setDisponibilidade(disponibilidade);
                bibliotecaService.atualizarItem(itemAtualizar);
                System.out.println("Item atualizado!");
                break;
            case 3:
                System.out.print("Digite o título do item a ser excluído: ");
                String tituloExcluir = scanner.nextLine();
                bibliotecaService.excluirItem(tituloExcluir);
                System.out.println("Item excluído!");
                break;
            case 4:
                System.out.println("Itens no catálogo:");
                bibliotecaService.getItens().forEach(it -> System.out.println("- " + it.getTitulo()));
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    private static void registrarEmprestimo(Scanner scanner, UsuarioService usuarioService,
                                            BibliotecaService bibliotecaService, HistoricoEmprestimo historicoEmprestimo) {
        System.out.print("Digite a matrícula do usuário: ");
        String matricula = scanner.nextLine();
        Usuario usuario = usuarioService.consultarUsuario(matricula);

        System.out.print("Digite o título do item: ");
        String titulo = scanner.nextLine();
        ItemBiblioteca item = bibliotecaService.buscarPorTitulo(titulo).get(0);

        EmprestimoStrategy strategy;
        if (usuario.getTipo().equalsIgnoreCase("Aluno")) {
            strategy = new AlunoEmprestimoStrategy();
        } else {
            strategy = new ProfessorEmprestimoStrategy();
        }

        Emprestimo emprestimo = new Emprestimo(usuario, item, strategy);
        historicoEmprestimo.registrarEmprestimo(emprestimo);

        System.out.println("Empréstimo registrado! Data de devolução: " + emprestimo.getDataDevolucao());
    }

    private static void registrarDevolucao(Scanner scanner, HistoricoEmprestimo historicoEmprestimo) {
        System.out.print("Digite o título do item devolvido: ");
        String titulo = scanner.nextLine();
        historicoEmprestimo.registrarDevolucao(titulo);
        System.out.println("Devolução registrada!");
    }

    private static void fazerReserva(Scanner scanner, UsuarioService usuarioService,
                                     BibliotecaService bibliotecaService) {
        System.out.print("Digite a matrícula do usuário: ");
        String matricula = scanner.nextLine();
        Usuario usuario = usuarioService.consultarUsuario(matricula);
        UsuarioObserver observer = new UsuarioObserver(usuario);

        System.out.print("Digite o título do item a ser reservado: ");
        String titulo = scanner.nextLine();
        ItemBiblioteca item = bibliotecaService.buscarPorTitulo(titulo).get(0);
        item.addObserver(observer);

        System.out.println("Reserva registrada! Você será notificado quando o item estiver disponível.");
    }

    private static void consultarHistorico(Scanner scanner, UsuarioService usuarioService,
                                           HistoricoEmprestimo historicoEmprestimo) {
        System.out.print("Digite a matrícula do usuário: ");
        String matricula = scanner.nextLine();
        Usuario usuario = usuarioService.consultarUsuario(matricula);

        System.out.println("Histórico de empréstimos:");
        historicoEmprestimo.consultarHistoricoPorUsuario(usuario).forEach(emp -> {
            System.out.println("- Item: " + emp.getItem().getTitulo() + ", Data de Devolução: " + emp.getDataDevolucao());
        });
    }

    private static void buscarItem(Scanner scanner, BibliotecaService bibliotecaService) {
        System.out.print("Digite o termo de busca (título, autor ou área): ");
        String termo = scanner.nextLine();

        System.out.println("Resultados:");
        bibliotecaService.buscarPorTitulo(termo).forEach(it -> System.out.println("- " + it.getTitulo()));
    }
}
