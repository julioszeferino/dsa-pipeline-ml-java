package app;

import java.io.File; // biblioteca para manipular arquivos
import java.util.ArrayList; // biblioteca para manipular listas
import java.util.List; // biblioteca para implementar operacoes com listas
import java.util.Locale; // biblioteca para manipular dados de localizacao
import java.util.Scanner; // biblioteca para ler dados e arquivos
import java.util.concurrent.ExecutionException;

import javax.swing.*; // biblioteca para criar interfaces graficas
import java.io.PrintWriter; // biblioteca para escrever dados em arquivos

class DataSet {

    // definindo as variaveis
    public List<double[]> trainData = new ArrayList<>();
    public double maxMilage = 0.0;
    public double minMilage = 0.0;
    public double maxPrice = 0.0;
    public double minPrice = 0.0;
    public double theta0 = 0.0;
    public double theta1 = 0.0;
    public int iterations = 0;
    public int precision = 0;

    // criando um metodo getter publico para acessar os dados de treino
    public DataSet(String fileName[]) {
        // chamando a funcao que le os dados do arquivo csv
        readCsv(fileName);
    }

    private void readCsv(String fileName[]) {
        /*
        Funcao que le os dados de um arquivo csv
        
        :params fileName: path do arquivo csv
        :return: void/None
        */
        Scanner scanner;
        String line;
        String values[];
        double data[];
        
        // validando se o path do arquivo foi informado como entrada
        if (fileName.length == 0) {
            System.out.println("Erro: arquivo csv nao encontrado");
            System.exit(1);
        }

        try {
            // leitura do arquivo csv
            scanner = new Scanner(new File(fileName[0]));

            // validando se o cabecalho do arquivo csv possui os dados corretos
            line = scanner.nextLine(); // le a primeira linha do arquivo
            values = line.split(","); // separa os valores da linha por virgula
            if (!(
                values.length == 2 && 
                values[0].equals("km") && 
                values[1].equals("gastomensal"))) {
                    System.out.println("A primeira linha do arquivo esta invalida. Esperado: \"km,gastomensal\"");
                    System.exit(1);
            }

            // Se o arquivo estiver com o cabeçalho correto...
            // percorre o arquivo e armazena os dados em uma lista
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                values = line.split(",");

                // validando se o arquivo possui os dados corretos
                if (values.length != 2) {
                    System.out.println("Erro: a linha do arquivo esta invalida. Esperado: \"int, int\"");
                    System.exit(1);
                }

                // tratando os dados
                // convertendo os dados de string para double e
                // armazenando os dados em um array
                data = new double[2];
                data[0] = Double.parseDouble(values[0]);
                data[1] = Double.parseDouble(values[1]);

                // adicionando os dados na lista de dados de treino
                trainData.add(data);

                // buscando os valores maximos e minimos para as variaveis
                // maxMilage, minMilage, maxPrice e minPrice
                if (trainData.size() == 1) {
                    // Se for o primeiro dado, atribui o valor a todas as variaveis
                    maxMilage = data[0];
                    minMilage = data[0];
                    maxPrice = data[1];
                    minPrice = data[1];
                } else {
                    // Se nao for o primeiro dado, compara o valor com as variaveis ja armazenadas
                    // e atribui o maior ou menor valor a variavel de acordo com a comparacao
                    maxMilage = (maxMilage < data[0]) ? data[0] : maxMilage;
                    minMilage = (minMilage > data[0]) ? data[0] : minMilage;
                    maxPrice = (maxPrice < data[1]) ? data[1] : maxPrice;
                    minPrice = (minPrice > data[1]) ? data[1] : minPrice;
                }
            }

            scanner.close(); // fecha o arquivo
        } catch (Exception e) {
            System.out.println("Erro ao ler o dataset " + e.getMessage());
            System.exit(1);
        }
    }


    public void scale() {
        /*
        Metodo que deixa os dados de treino na mesma escala
        divindo os valores por 1000.
        */
        double data[];

        // percorre a lista de dados de treino
        for (int i = 0; i < trainData.size(); i++) {
            data = trainData.get(i);

            // normaliza os dados de treino
            data[0] = data[0] / 1000;
            data[1] = data[1] / 1000;

            // atualiza os dados na lista de dados de treino
            trainData.set(i, data);
        }

        maxMilage = maxMilage / 1000;
        minMilage = minMilage / 1000;
        maxPrice = maxPrice / 1000;
        minPrice = minPrice / 1000;
    }


    public void printData() {
        /*
        Metodo que percorre o array de treino e imprime os dados
        na tela
        */
        for (int i = 0; i < trainData.size(); i++) {
            System.out.printf("%f : %f\n", trainData.get(i)[0], trainData.get(i)[1]);
        }
    }

    public void writeTheta() {
        /*
        Metodo que escreve os valores de theta0 e theta1 em um arquivo
        no formato txt
        */
        PrintWriter writer;

        try {
            writer = new PrintWriter("modelo.txt");
            writer.println(String.format(Locale.US, "%f,%f", theta0, theta1));
            writer.close();
        } catch (Exception e) {
            System.out.println("Erro ao salvar o modelo " + e.getMessage());
            System.exit(1);
        }
    }
}


class TreinaModelo {
    
    static JFrame frame; // criando uma janela principal

    static void plot (DataSet dataset) {
        /*
        Metodo que plota os dados de treino em um grafico

        :params dataset: Array com os dados de treino
        */
        // se a janela principal nao existir, cria uma
        if (frame == null) {
            frame = new JFrame(); // criando uma janela principal
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // definindo o comportamento ao fechar a janela
            Plot plot = new Plot(dataset); // criando um grafico com os dados de treino
            frame.add(plot); // adicionando o grafico a janela principal
            frame.setSize(800, 600); // definindo o tamanho da janela
            frame.setLocation(200, 200); // definindo a posicao da janela na tela
            frame.setVisible(true); // tornando a janela visivel
        } else {
            SwingUtilities.updateComponentTreeUI(frame); // atualiza a janela
        }

        // aguarda 50 milisegundos para atualizar o grafico
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            System.out.println("Erro ao plotar o grafico " + e.getMessage());
            System.exit(1);
        }
    }


    static double estimatePrice(int i, DataSet dataset) {
        /*
        Funcao que estima o preco do carro

        :params i: indice do dado de treino
        :params dataset: Array com os dados de treino
        :return: valor estimado do preco do carro
        */
        return dataset.theta0 + dataset.trainData.get(i)[0] * dataset.theta1;
    }


    public static int calculateError(DataSet dataset) {
        /*
        Metodo que calcula o erro medio do modelo

        :params dataset: Array com os dados de treino
        :return: erro medio do modelo
        */
        double error = 0;

        for (int i = 0; i < dataset.trainData.size(); i++) {
            // erro = erro + (abs(preco real - preco estimado) / preco real)
            error += Math.abs(dataset.trainData.get(i)[1] - estimatePrice(i, dataset) / dataset.trainData.get(i)[1]);
        }

        // erro medio = erro / numero de dados de treino
        error = error / dataset.trainData.size();
        return (int) (error * 100);
    }


    public static void main(String args[]) {
        /*
        Metodo principal
        */
        DataSet dataset = new DataSet(args); 
        dataset.scale(); // normaliza os dados de treino
        double learningRate = 0.0001; // taxa de aprendizado
        int iterations = 300000; // numero de iteracoes
        double summ1 = 0.0;
        double summ2 = 0.0;
        plot(dataset); // plota os dados de treino

        // Realiza o treinamento do modelo
        for (int j = 0; j <= iterations; j++) {

            if (j % 1000 == 0 || j == 0 || j == iterations - 1) {
                dataset.iterations = j;
                plot(dataset);  
            }
            summ1 = 0;
            summ2 = 0;

            // Criando as estimativas
            for (int i = 0; i < dataset.trainData.size(); i++) {
                // estimando o preco
                double ePrice = estimatePrice(i, dataset); 
                summ1 += (ePrice - dataset.trainData.get(i)[1]);
                summ2 += (ePrice - dataset.trainData.get(i)[1]) * dataset.trainData.get(i)[0];
            }

            summ1 = learningRate * (summ1 / dataset.trainData.size()); 
            summ2 = learningRate * (summ2 / dataset.trainData.size()); 

            // Atualizando os valores de theta0 e theta1
            dataset.theta0 = dataset.theta0 - summ1;
            dataset.theta1 = dataset.theta1 - summ2;
            dataset.precision = 100 - calculateError(dataset);
        }

        dataset.writeTheta(); // escreve os valores de theta0 e theta1 em um arquivo
    }
}

