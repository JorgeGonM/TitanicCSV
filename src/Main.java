import model.Pasajero;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        System.out.println("Titanic");

        Path fichero = Path.of("../data/titanic.csv");

        try {

            // Leemos el CSV utilizando Files
            List<Pasajero> pasajeros = Files.lines(fichero)
                    .skip(1)
                    .map(linea -> {

                        String[] datos = separarCSV(linea);

                        return new Pasajero(
                                Integer.parseInt(datos[14]), // survived
                                Integer.parseInt(datos[1]),  // pclass
                                datos[3],                    // sex
                                datos[4].isEmpty()
                                        ? null
                                        : Double.parseDouble(datos[4]), // age
                                datos[10]                   // embarked
                        );
                    })
                    .collect(Collectors.toList());


           
            // 1. Pasajeros que han sobrevivido

            System.out.println();
            System.out.println("1. Pasajeros que han sobrevivido:");

            long sobrevivientes = pasajeros.stream()
                    .filter(pasajero -> pasajero.getSurvived() == 1)
                    .count();

            System.out.println("Total de pasajeros supervivientes: " + sobrevivientes);


            // Clasificados por clase y sexo

            System.out.println();
            System.out.println("Supervivientes por clase y sexo:");

            Map<String, Long> sobrevivientesPorClaseSexo =
                    pasajeros.stream()
                            .filter(pasajero ->
                                    pasajero.getSurvived() == 1
                            )
                            .collect(Collectors.groupingBy(
                                    pasajero ->
                                            "Clase " + pasajero.getPclass()
                                            + " - " + pasajero.getSex(), 
                                            Collectors.counting()
                            ));

            sobrevivientesPorClaseSexo.forEach(
                    (grupo, cantidad) ->
                            System.out.println(grupo + ": " + cantidad + " supervivientes")
            );


            // 2. Puerto de embarque más común

            System.out.println();
            System.out.println("2. Puerto de embarque más común:");

            Map<String, Long> pasajerosPorPuerto =
                    pasajeros.stream()
                            .filter(pasajero ->
                                    pasajero.getEmbarked() != null
                                            && !pasajero.getEmbarked().isEmpty()
                            )
                            .collect(Collectors.groupingBy(
                                    Pasajero::getEmbarked,
                                    Collectors.counting()
                            ));

            pasajerosPorPuerto.forEach(
                    (puerto, cantidad) ->
                            System.out.println("Puerto " + puerto + ": " + cantidad + " pasajeros")
            );

            pasajerosPorPuerto.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .ifPresent(entrada ->
                            System.out.println("Puerto más común: " + entrada.getKey() + " (" + entrada.getValue() + " pasajeros)")
                    );


            // Relación entre puerto y supervivencia

            System.out.println();
            System.out.println("Supervivencia según el puerto de embarque:");

            Map<String, Map<Integer, Long>> supervivenciaPorPuerto =
                    pasajeros.stream()
                            .filter(pasajero ->
                                    pasajero.getEmbarked() != null
                                            && !pasajero.getEmbarked().isEmpty()
                            )
                            .collect(Collectors.groupingBy(
                                    Pasajero::getEmbarked,
                                    Collectors.groupingBy(
                                            Pasajero::getSurvived,
                                            Collectors.counting()
                                    )
                            ));

            supervivenciaPorPuerto.forEach(
                    (puerto, supervivencia) ->
                            System.out.println(
                                    "Puerto " + puerto
                                            + " -> "
                                            + "No sobrevivieron: "
                                            + supervivencia.getOrDefault(
                                                    0, 0L
                                            )
                                            + ", "
                                            + "Sobrevivieron: "
                                            + supervivencia.getOrDefault(
                                                    1, 0L
                                            )
                            )
            );


            // 3. Tasa de supervivencia de niños en comparación con adultos

            System.out.println();
            System.out.println("3. Tasa de supervivencia de niños en comparación con adultos:");


            // Menores de 12 años
            long totalNinos = pasajeros.stream()
                    .filter(pasajero ->
                            pasajero.getAge() != null
                                    && pasajero.getAge() < 12
                    )
                    .count();

            long ninosSobrevivientes = pasajeros.stream()
                    .filter(pasajero ->
                            pasajero.getAge() != null
                                    && pasajero.getAge() < 12
                                    && pasajero.getSurvived() == 1
                    )
                    .count();


            // adultos (12 años o más)

            long totalAdultos = pasajeros.stream()
                    .filter(pasajero ->
                            pasajero.getAge() != null
                                    && pasajero.getAge() >= 12
                    )
                    .count();

            long adultosSobrevivientes = pasajeros.stream()
                    .filter(pasajero ->
                            pasajero.getAge() != null
                                    && pasajero.getAge() >= 12
                                    && pasajero.getSurvived() == 1
                    )
                    .count();


            // Calculamos las tasas

            double tasaNinos =
                    (double) ninosSobrevivientes
                            / totalNinos * 100;

            double tasaAdultos =
                    (double) adultosSobrevivientes
                            / totalAdultos * 100;


            System.out.printf(
                    "Niños: %d/%d (%.2f%%)%n",
                    ninosSobrevivientes,
                    totalNinos,
                    tasaNinos
            );

            System.out.printf(
                    "Adultos: %d/%d (%.2f%%)%n",
                    adultosSobrevivientes,
                    totalAdultos,
                    tasaAdultos
            );


        } catch (IOException e) {

            System.out.println(
                    "Error al leer el fichero: "
                            + e.getMessage()
            );
        }
    }


    //Método para separar una línea de CSV en campos, considerando comillas
    private static String[] separarCSV(String linea) {

        List<String> campos = new ArrayList<>();

        StringBuilder campo = new StringBuilder();

        boolean dentroComillas = false;

        for (int i = 0; i < linea.length(); i++) {

            char caracter = linea.charAt(i);

            if (caracter == '"') {

                dentroComillas = !dentroComillas;

            } else if (caracter == ',' && !dentroComillas) {

                campos.add(campo.toString());

                campo.setLength(0);

            } else {

                campo.append(caracter);
            }
        }

        // Añadimos el último campo
        campos.add(campo.toString());

        return campos.toArray(new String[0]);
    }
}
