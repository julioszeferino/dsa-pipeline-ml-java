// Realiza a Previsao com o modelo treinado
package app;

import java.io.File; // biblioteca para manipular arquivos
import java.util.Scanner; // biblioteca para ler dados e arquivos

public class Previsao {

    public static void main(String args[]) {
        
        // definindo as variaveis
        Scanner scanner = null;
        double milage = 0;
        double theta0 = 0;
        double theta1 = 0;
        int scale = 1000;

        try {
            // lendo os dados do arquivo do modelo treinado
            scanner = new Scanner(new File("modelo.txt"));
            String line = scanner.nextLine();
            // recuperando os valores de theta0 e theta1
            theta0 = Double.parseDouble(line.split(",")[0]);
            theta1 = Double.parseDouble(line.split(",")[1]);
        } catch (Exception e) {
            theta0 = 0;
            theta1 = 0;
        }

        // fechando o scanner
        if (scanner != null) {
            scanner.close();
        }

        // Recebendo os dados de entrada do usuario para realizar as previsoes do modelo
        scanner = new Scanner(System.in);
        System.out.println("Bem vindo(a) ao Modelo de Machine Learning em Java\n");
        System.out.println("Digite a quilometragem do carro: ");

        try{
            // lendo o dado e convertendo para double
            milage = Double.parseDouble(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Erro ao ler o dado de entrada - Valor Invalido!");
            System.exit(1);
        }

        System.out.printf("Previsao de Gasto Mensal com Combustivel: $%d\n", (int)((theta0 + theta1 * milage / scale) * scale));
        
    }
    
}
