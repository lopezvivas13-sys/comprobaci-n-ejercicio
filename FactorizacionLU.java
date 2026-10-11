import java.util.Scanner;

public class Main {
 static Scanner teclado = new Scanner(System.in);
    static final double CERO = 1e-9; // por debajo de este valor se considera cero

    // ============================================
    // VERIFICAR SI UN TEXTO ES UN NÚMERO (entero o decimal, con signo opcional)
    // ============================================
    static boolean esNumero(String texto) {
        return texto.matches("[+-]?(\\d+\\.?\\d*|\\.\\d+)");
    }

    // ============================================
    // FUNCIÓN PARA INGRESAR LOS NÚMEROS (uno por uno)
    // ============================================
    static double ingresarNumero(String mensaje) {

        while (true) {

            System.out.print(mensaje);
            System.out.flush(); // fuerza a que el mensaje se vea antes de esperar el dato

            if (!teclado.hasNextLine()) {
                System.out.println();
                System.out.println("No hay más datos de entrada.");
                System.exit(1);
            }

            // Se acepta 2,5 como 2.5
            String dato = teclado.nextLine().trim().replace(',', '.');

            // Si contiene una fracción
            if (dato.contains("/")) {

                String[] partes = dato.split("/");

                if (partes.length == 2 && esNumero(partes[0].trim()) && esNumero(partes[1].trim())) {

                    double numerador = Double.parseDouble(partes[0].trim());
                    double denominador = Double.parseDouble(partes[1].trim());

                    // Evitar división entre cero
                    if (denominador == 0) {
                        System.out.println("Error: el denominador no puede ser cero.");
                        continue;
                    }

                    return numerador / denominador;
                }

            // Si es decimal o entero
            } else if (esNumero(dato)) {

                return Double.parseDouble(dato);
            }

            System.out.println();
            System.out.println("Dato incorrecto.");
            System.out.println("Ingrese un número entero, decimal o fracción.");
            System.out.println("Ejemplos: 5, 2.5, 3/4, -7/3");
            System.out.println();
        }
    }

    // ============================================
    // MOSTRAR UN NÚMERO: entero, fracción (11/7) o decimal
    // ============================================
    static String texto(double x) {

        if (Math.abs(x) < CERO) return "0";

        // ¿Es entero?
        if (Math.abs(x - Math.rint(x)) < CERO) return String.valueOf((long) Math.rint(x));

        // ¿Se puede escribir como fracción con denominador pequeño?
        for (int d = 2; d <= 1000; d++) {
            double n = x * d;
            if (Math.abs(n - Math.rint(n)) < 1e-7) {
                return ((long) Math.rint(n)) + "/" + d;
            }
        }

        // Si no, se muestra con 4 decimales
        return String.format(java.util.Locale.US, "%.4f", x);
    }

    // Igual que texto(), pero pone las fracciones entre paréntesis: 5/2 -> (5/2)
    // Así una división se lee claro: 5 / (5/2) = 2
    static String par(double x) {
        String t = texto(x);
        return t.contains("/") ? "(" + t + ")" : t;
    }

    // ============================================
    // FUNCIÓN PARA MOSTRAR MATRICES
    // ============================================
    static void mostrarMatriz(String nombre, double[][] matriz) {

        System.out.println();
        System.out.println(nombre);

        for (double[] fila : matriz) {

            System.out.print("[ ");

            for (double elemento : fila) {
                System.out.print(texto(elemento) + " ");
            }

            System.out.println("]");
        }
    }

    // ============================================
    // PROGRAMA PRINCIPAL
    // ============================================
    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("   FACTORIZACIÓN LU DE UNA MATRIZ 3x3");
        System.out.println("           MÉTODO DE DOOLITTLE");
        System.out.println("======================================");
        System.out.println();
        System.out.println("Ejemplos de datos válidos:");
        System.out.println("Entero:     5");
        System.out.println("Decimal:    2.5");
        System.out.println("Fracción:   3/4");
        System.out.println("Fracción:  -5/2");
        System.out.println();

        // ============================================
        // INGRESAR MATRIZ A
        // ============================================

        System.out.println("INGRESE LOS ELEMENTOS DE LA MATRIZ A");
        System.out.println("--------------------------------------");

        double a11 = ingresarNumero("Ingrese a11: ");
        double a12 = ingresarNumero("Ingrese a12: ");
        double a13 = ingresarNumero("Ingrese a13: ");

        double a21 = ingresarNumero("Ingrese a21: ");
        double a22 = ingresarNumero("Ingrese a22: ");
        double a23 = ingresarNumero("Ingrese a23: ");

        double a31 = ingresarNumero("Ingrese a31: ");
        double a32 = ingresarNumero("Ingrese a32: ");
        double a33 = ingresarNumero("Ingrese a33: ");

        double[][] A = {
            {a11, a12, a13},
            {a21, a22, a23},
            {a31, a32, a33}
        };

        // L empieza como la matriz identidad (unos en la diagonal)
        double[][] L = {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
        };

        // U empieza llena de ceros
        double[][] U = new double[3][3];

        System.out.println();
        System.out.println("======================================");
        System.out.println("PROCEDIMIENTO PASO A PASO");
        System.out.println("======================================");

        // ============================================
        // PASO 1: PRIMERA FILA DE U (es igual a la primera fila de A)
        // ============================================

        U[0][0] = a11;
        U[0][1] = a12;
        U[0][2] = a13;

        System.out.println();
        System.out.println("Paso 1: la primera fila de U es la primera fila de A");
        System.out.println("u11 = " + texto(U[0][0]));
        System.out.println("u12 = " + texto(U[0][1]));
        System.out.println("u13 = " + texto(U[0][2]));

        // Verificar primer pivote
        if (Math.abs(U[0][0]) < CERO) {
            System.out.println();
            System.out.println("No se puede realizar la factorización LU.");
            System.out.println("El primer pivote es cero.");
            return;
        }

        // ============================================
        // PASO 2: PRIMERA COLUMNA DE L (multiplicadores)
        // l = elemento a eliminar / pivote
        // ============================================

        L[1][0] = a21 / U[0][0];
        L[2][0] = a31 / U[0][0];

        System.out.println();
        System.out.println("Paso 2: primera columna de L (elemento a eliminar / pivote u11)");
        System.out.println("l21 = a21 / u11 = " + par(a21) + " / " + par(U[0][0]) + " = " + texto(L[1][0]));
        System.out.println("l31 = a31 / u11 = " + par(a31) + " / " + par(U[0][0]) + " = " + texto(L[2][0]));

        // ============================================
        // PASO 3: SEGUNDA FILA DE U
        // u = a - l * (fila de arriba)
        // ============================================

        U[1][1] = a22 - L[1][0] * U[0][1];
        U[1][2] = a23 - L[1][0] * U[0][2];

        System.out.println();
        System.out.println("Paso 3: segunda fila de U");
        System.out.println("u22 = a22 - l21*u12 = " + texto(a22) + " - (" + texto(L[1][0]) + ")(" + texto(U[0][1]) + ") = " + texto(U[1][1]));
        System.out.println("u23 = a23 - l21*u13 = " + texto(a23) + " - (" + texto(L[1][0]) + ")(" + texto(U[0][2]) + ") = " + texto(U[1][2]));

        // Verificar segundo pivote
        if (Math.abs(U[1][1]) < CERO) {
            System.out.println();
            System.out.println("No se puede realizar la factorización LU.");
            System.out.println("El segundo pivote es cero.");
            return;
        }

        // ============================================
        // PASO 4: SEGUNDA COLUMNA DE L
        // ============================================

        L[2][1] = (a32 - L[2][0] * U[0][1]) / U[1][1];

        System.out.println();
        System.out.println("Paso 4: segunda columna de L");
        System.out.println("l32 = (a32 - l31*u12) / u22 = (" + texto(a32) + " - (" + texto(L[2][0]) + ")(" + texto(U[0][1]) + ")) / " + par(U[1][1]) + " = " + texto(L[2][1]));

        // ============================================
        // PASO 5: TERCERA FILA DE U
        // ============================================

        U[2][2] = a33 - L[2][0] * U[0][2] - L[2][1] * U[1][2];

        System.out.println();
        System.out.println("Paso 5: tercera fila de U");
        System.out.println("u33 = a33 - l31*u13 - l32*u23 = " + texto(a33) + " - (" + texto(L[2][0]) + ")(" + texto(U[0][2]) + ") - (" + texto(L[2][1]) + ")(" + texto(U[1][2]) + ") = " + texto(U[2][2]));

        // ============================================
        // MOSTRAR RESULTADOS
        // ============================================

        System.out.println();
        System.out.println("======================================");
        System.out.println("RESULTADOS");
        System.out.println("======================================");

        mostrarMatriz("MATRIZ A:", A);
        mostrarMatriz("MATRIZ L:", L);
        mostrarMatriz("MATRIZ U:", U);

        // ============================================
        // VERIFICACIÓN: L x U debe ser igual a A
        // ============================================

        double[][] LU = new double[3][3];
        boolean correcto = true;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    LU[i][j] += L[i][k] * U[k][j];
                }
                if (Math.abs(LU[i][j] - A[i][j]) > 1e-7) {
                    correcto = false;
                }
            }
        }

        mostrarMatriz("L x U:", LU);

        System.out.println();
        System.out.println("======================================");
        System.out.println("FACTORIZACIÓN:");
        System.out.println("A = L × U");
        System.out.println(correcto ? "Verificación correcta: L x U = A" : "Verificación fallida: L x U no es igual a A");
        System.out.println("======================================");
    }
}
