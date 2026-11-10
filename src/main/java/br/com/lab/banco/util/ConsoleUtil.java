package br.com.lab.banco.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class ConsoleUtil {

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ConsoleUtil() {
    }

    public static String lerTexto(Scanner scanner, String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    public static int lerInteiro(Scanner scanner, String label) {
        while (true) {
            System.out.print(label);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

    public static BigDecimal lerDecimal(Scanner scanner, String label) {
        while (true) {
            System.out.print(label);
            String entrada = scanner.nextLine().trim().replace(",", ".");
            try {
                return new BigDecimal(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Exemplo: 100.50");
            }
        }
    }

    public static LocalDate lerData(Scanner scanner, String label) {
        while (true) {
            System.out.print(label);
            String entrada = scanner.nextLine().trim();
            try {
                return LocalDate.parse(entrada, DATA_BR);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato dd/MM/yyyy.");
            }
        }
    }

    public static void pausar(Scanner scanner) {
        System.out.println("\nPressione ENTER para continuar...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    public static void limparTela() {
        System.out.println("\n".repeat(2));
    }
}
