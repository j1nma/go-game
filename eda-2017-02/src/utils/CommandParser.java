package utils;

import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import static model.ModelConstants.BOARD_SIZE;

/**
 * Parses user's input from command line. Saves required parameters for MMTree operation.
 */
public class CommandParser {

    private static int[][] matrix = new int[BOARD_SIZE][BOARD_SIZE];
    private static boolean fileWasProvided = false;
    private static int playerToPlay;
    private static int maxTime;
    private static int treeDepth;
    private static boolean pruneActivated = false;
    private static boolean treeActivated = false;

    public static void parse(String[] args) {

        Options options = new Options();

        Option visual = new Option("visual", false, "Ejecuta la aplicación en " +
                "modo visual, permitiendo al usuario jugar una partida completa contra la computadora. Se muestra una" +
                " ventana con el estado inicial del tablero, y comienza a jugar el usuario. Una vez que realiza una " +
                "jugada, la computadora responde, y así continúan jugando alternadamente hasta que no se puedan " +
                "realizar más jugadas, y se informa quién ganó.");

        Option file = new Option("file", true, "Ejecuta la aplicación para " +
                "realizar una sola jugada. Lee de un archivo el estado del tablero, analiza la mejor jugada que se " +
                "puede realizar, e imprime por consola dicha jugada en formato “fila, columna”. Si no se puede " +
                "realizar ninguna jugada, imprime por consola el texto “PASS”.");
        file.setArgName("filename");
        file.setType(String.class);

        Option player = new Option("player", true, "Utilizado junto con el parámetro file," +
                " para indicar qué jugador es el que debe realizar la jugada. El valor de n puede ser 1 o 2.");
        player.setArgName("n");

        options.addOption(visual);
        options.addOption(file);
        options.addOption(player);

        Option maxtime = new Option("maxtime", true, "Indica el tiempo máximo para obtener una " +
                "solución, expresado en segundos. Pasado este tiempo, el algoritmo debe retornar con la mejor jugada" +
                " analizada hasta el momento. Si se utiliza con la opción “-visual”, es el tiempo máximo a utilizar " +
                "por cada jugada de la computadora.");
        maxtime.setArgName("segundos");
        maxtime.setType(Integer.class);
        options.addOption(maxtime);

        Option depth = new Option("depth", true, "Indica la profundidad del árbol que se desea" +
                " explorar. Si se utiliza con la opción “-visual”, es la profundidad máxima a analizar en cada jugada" +
                " de la computadora.");
        depth.setArgName("profundidad");
        options.addOption(depth);

        Option prune = new Option("prune", false, "Habilita la poda alfa-beta en el algoritmo.");
        options.addOption(prune);

        Option tree = new Option("tree", false, "Genera un archivo llamado tree.dot con una" +
                " representación del árbol explorado, luego de aplicar el algoritmo minimax. Sólo se puede utilizar " +
                "con el parámetro –file.");
        options.addOption(tree);

        CommandLine aux;
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            aux = parser.parse(options, args);
            validate(aux, visual.getOpt(), file.getOpt(), player.getOpt(), maxtime.getOpt(),
                    depth.getOpt(), prune.getOpt(), tree.getOpt());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java -jar go.jar (-visual | -file archivo -player n) " +
                    "(-maxtime n | -depth n) [-prune] [-tree]", options);

            System.exit(1);
        }

    }

    private static void validate(final CommandLine cmdLine, String visual, String file, String player, String maxtime,
                                 String depth, String prune, String tree) throws ParseException {
        final boolean visualSupplied = cmdLine.hasOption(visual);

        final boolean fileAndplayerSupplied = cmdLine.hasOption(file) && cmdLine.hasOption(player);

        final boolean bOrCSupplied = !fileAndplayerSupplied && (cmdLine.hasOption(file) || cmdLine.hasOption(player));

        if ((visualSupplied && fileAndplayerSupplied) || (!visualSupplied && !fileAndplayerSupplied)
                || (visualSupplied && bOrCSupplied)) {
            throw new ParseException("Debe indicar un modo de juego: visual o file con player.");
        }

        if (fileAndplayerSupplied) {
            if (!Pattern.matches("^.*\\.txt$", cmdLine.getOptionValue(file)))
                throw new ParseException("-file debe ser un archivo de texto con formato .txt.");

            FileReader input;
            try {
                input = new FileReader(cmdLine.getOptionValue(file));
            } catch (FileNotFoundException e) {
                throw new ParseException(cmdLine.getOptionValue(file) + " no se encontró.");
            }

            parseTxt(input);

            if (!Pattern.matches("^[1-2]{1}$", cmdLine.getOptionValue(player)))
                throw new ParseException("-player debe tener como argumento 1 o 2.");

            fileWasProvided = true;
            playerToPlay = Integer.parseInt(cmdLine.getOptionValue(player));
        }

        validate(cmdLine, maxtime, depth);

        final boolean treeSupplied = cmdLine.hasOption(tree);

        final boolean pruneSupplied = cmdLine.hasOption(prune);

        if (treeSupplied && !fileAndplayerSupplied) {
            throw new ParseException("Sólo se puede utilizar -tree con el parámetro –file.");
        }

        if (treeSupplied) treeActivated = true;

        if (pruneSupplied) pruneActivated = true;

    }

    private static void validate(CommandLine cmdLine, String maxtime, String depth) throws ParseException {
        final boolean maxtimeSupplied = cmdLine.hasOption(maxtime);

        final boolean depthSupplied = cmdLine.hasOption(depth);

        if ((maxtimeSupplied && depthSupplied) || (!maxtimeSupplied && !depthSupplied)) {
            throw new ParseException("Debe indicar una poda por tiempo (maxtime) o profundidad (depth).");
        }

        if (maxtimeSupplied && !Pattern.matches("[1-9]+[0-9]*", cmdLine.getOptionValue(maxtime)))
            throw new ParseException("-maxtime debe tener un argumento entero positivo.");

        if (depthSupplied && !Pattern.matches("[1-9]+[0-9]*", cmdLine.getOptionValue(depth)))
            throw new ParseException("-depth debe tener un argumento entero positivo.");

        if (maxtimeSupplied) maxTime = Integer.parseInt(cmdLine.getOptionValue(maxtime));

        if (depthSupplied) treeDepth = Integer.parseInt(cmdLine.getOptionValue(depth));
    }

    private static void parseTxt(FileReader input) throws ParseException {

        BufferedReader bufRead = new BufferedReader(input);
        String myLine;

        int row = 0;

        try {
            while ((myLine = bufRead.readLine()) != null) {

                if (myLine.length() != 13)
                    throw new ParseException("El formato del archivo de entrada no es compatible.");

                char[] col = myLine.toCharArray();

                for (int i = 0; i < myLine.length(); i++) {
                    if (col[i] == ' ') {
                        matrix[row][i] = 0;
                    } else if (col[i] == '1') {
                        matrix[row][i] = 1;
                    } else if (col[i] == '2') {
                        matrix[row][i] = 2;
                    } else {
                        throw new ParseException("El formato del archivo de entrada no es compatible.");
                    }
                }

                row++;
            }

//            printMatrix();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * For debugging purposes.
     */
    private static void printMatrix() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            if (i != matrix.length - 1) System.out.println();
        }
    }

    public static int[][] getMatrix() {
        return fileWasProvided ? matrix : null;
    }

    public static boolean fileWasProvided() {
        return fileWasProvided;
    }

    public static int getPlayerToPlay() {
        return playerToPlay;
    }

    public static boolean pruneActivated() {
        return pruneActivated;
    }

    public static boolean treeActivated() {
        return treeActivated;
    }

    public static int getMaxTime() {
        return maxTime;
    }

    public static int getDepth() {
        return treeDepth;
    }


}
