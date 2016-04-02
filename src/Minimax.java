import java.util.LinkedList;
import java.util.Hashtable;

public class Minimax {
    /**
     * Clase para representar un estado del juego del gato. 
     * Cada estado sabe cómo generar a sus sucesores.
     */
    static class Gato{
        int[][] tablero = new int[3][3];     // Tablero del juego
        Gato padre;                          // Quién generó este estado.
        LinkedList<Gato> sucesores;          // Posibles jugadas desde este estado.
        boolean jugador1 = false;            // Jugador que tiró en este tablero.
        boolean hayGanador = false;          // Indica si la última tirada produjo un ganador.
        int tiradas = 0;   
        final int MARCA1 = 1;             // Número usado en el tablero del gato para marcar al primer jugador.
        final int MARCA2 = 4;             // Se usan int en lugar de short porque coincide con el tamaño de la palabra, el código se ejecuta ligeramente más rápido.
                  // Número de casillas ocupadas.

        /** Constructor del estado inicial. */
        Gato() {}

        /** Constructor que copia el tablero de otro gato y el número de tiradas */
        Gato(Gato g){
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    tablero[i][j] = g.tablero[i][j];
                }
            }
            tiradas = g.tiradas;
        }

        /** Indica si este estado tiene sucesores expandidos. */
        int getNumHijos(){
            if(sucesores != null) return sucesores.size();
            else return 0;
        }

        /* Función auxiliar.
        * Dada la última posición en la que se tiró y la marca del jugador
        * calcula si esta jugada produjo un ganador y actualiza el atributo correspondiente.
        * 
        * Esta función debe ser lo más eficiente posible para que la generación del árbol no sea demasiado lenta.
        */
        void hayGanador(int x, int y, int marca){
        // Horizontal
            if (tablero[y][(x + 1) % 3] == marca && tablero[y][(x + 2) % 3] == marca) { hayGanador = true; return; }
            // Vertical
            if (tablero[(y + 1) % 3][x] == marca && tablero[(y + 2) % 3][x] == marca) { hayGanador = true; return; }
            // Diagonal
            if((x == 1 && y != 1) || (y == 1 && x!= 1)) return; // No pueden hacer diagonal
            // Centro y esquinas
            if(x == 1 && y == 1){
              // Diagonal \
                if(tablero[0][0] == marca && tablero[2][2] == marca) { hayGanador = true; return; }
                if(tablero[2][0] == marca && tablero[0][2] == marca) { hayGanador = true; return; }
            } else if (x == y){
              // Diagonal \
                if (tablero[(y + 1) % 3][(x + 1) % 3] == marca && tablero[(y + 2) % 3][(x + 2) % 3] == marca) { hayGanador = true; return; }
            } else {
              // Diagonal /
                if (tablero[(y + 2) % 3][(x + 1) % 3] == marca && tablero[(y + 1) % 3][(x + 2) % 3] == marca) { hayGanador = true; return; }
            }
        }

        /* Función auxiliar.
        * Coloca la marca del jugador en turno para este estado en las coordenadas indicadas.
        * Asume que la casilla está libre.
        * Coloca la marca correspondiente, verifica y asigna la variable si hay un ganador.
        */
        void tiraEn(int x, int y){
            tiradas++;
            int marca = (jugador1) ? MARCA1 : MARCA2;
            tablero[y][x] = marca;
            hayGanador(x,y, marca);
        }

        void tiraEn(int x, int y, boolean jugador1){
            tiradas++;
            int marca = (jugador1) ? MARCA1 : MARCA2;
            tablero[y][x] = marca;
            hayGanador(x,y, marca);
        }

        /**
        * Crea la lista sucesores y agrega a todos los estados que surjen de tiradas válidas.
        * Se consideran tiradas válidas a aquellas en una casilla libre.
        * Además, se optimiza el proceso no agregando estados con jugadas simétricas.
        * Los estados nuevos tendrán una tirada más y el jugador en turno será el jugador contrario.
        */
        LinkedList<Gato> generaSucesores() {
            if (hayGanador || tiradas == 9) return null;

            if (tiradas < 5)
                System.out.println(this);
            Hashtable<Integer, Gato> hw =  new Hashtable<Integer, Gato>();

            for (int i = 0; i < 3 ; i++ ) {
                for (int j = 0; j < 3; j++) {
                    boolean add = true;
                    Gato g = new Gato(this);
                    g.jugador1 = !jugador1;
                    g.padre = this;

                    if (g.tablero[i][j] == 0)
                        g.tiraEn(i,j);
                    
                    int h = g.hashCode();

                    if (!hw.containsKey(h) && g.tiradas > g.padre.tiradas) {
                        hw.put(g.hashCode(), g);
                    } 
                }   
            }
            sucesores = new LinkedList<Gato>(hw.values());
            return sucesores;


            // -------------------------------
            //        IMPLEMENTACION
            // -------------------------------
            // Hint: se debe verificar si el estado sigue siendo valido, si lo es, generar a sus sucesores
            // usando una lista ligada. recuerden que deben especificar que jugador jugó. No vayan a  
            // dejar sin padre a los sucesores.
        }

        int [][] reflejaIzq (int[][] t) {
            int [][] nuevo = new int[3][3];
            for (int i = 0; i < t.length; i++) {
                for (int j = 2, k = 0; j >= 0; j--, k++) {
                    nuevo[i][k] = t[i][j];
                }
            }
            return nuevo;
        }

        int [][] reflejaArriba (int[][] t) {
            int [][] nuevo = new int[3][3];
            for (int i = 2, ii = 0; i >= 0; i--, ii++) {
                for (int j = 0; j < 3; j++) {
                    nuevo[ii][j] = t[i][j];
                }
            }
            return nuevo;
        }

        int [][] traspuesta (int [][] t) {
            int [][] nueva = new int[3][3];
            for (int i = 0; i < 3 ; i++ ) {
                for (int j = 0; j < 3; j++) {
                    nueva[i][j] = nueva[j][i];
                }
            }
            return nueva;
        }

        boolean tablerosIguales (int [][] a, int [][] b) {
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    if(a[i][j] != b[i][j]) return false;
                }
            }
            return true;

        }


        // ------- *** ------- *** -------
        // Serie de funciones que revisan la equivalencia de estados considerando las simetrías de un cuadrado.
        // ------- *** ------- *** -------
        // http://en.wikipedia.org/wiki/Examples_of_groups#The_symmetry_group_of_a_square_-_dihedral_group_of_order_8
        // ba es reflexion sobre / y ba3 reflexion sobre \.

        /** Revisa si ambos gatos son exactamente el mismo. */
        boolean esIgual(Gato otro){
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    if(tablero[i][j] != otro.tablero[i][j]) return false;
                }
            }
            return true;
        }

        /** Al reflejar el gato sobre la diagonal \ son iguales (ie traspuesta) */
        boolean esSimetricoDiagonalInvertida(Gato otro){
            int [][] tmp = otro.tablero;
            int [][] tmp2 = reflejaIzq(tablero);
            return tablerosIguales(tmp, traspuesta(tmp2));
        }

        /** Al reflejar el gato sobre la diagonal / son iguales (ie traspuesta) */
        boolean esSimetricoDiagonal(Gato otro){
            int [][] tmp = otro.tablero;
            int [][] tmp2 = tablero;
            return tablerosIguales(tmp, traspuesta(tmp2));

        }

        /** Al reflejar el otro gato sobre la vertical son iguales */
        boolean esSimetricoVerticalmente(Gato otro){
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    if(tablero[i][j] != otro.tablero[i][2-j]) return false;
                }
            }
            return true;
        }

        /** Al reflejar el otro gato sobre la horizontal son iguales */
        boolean esSimetricoHorizontalmente(Gato otro){
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    if(tablero[i][j] != otro.tablero[i][2-j]) return false;
                }
            }
            return true;
        }

        /** Rota el otro tablero 90° en la dirección de las manecillas del reloj. */
        boolean esSimetrico90(Gato otro){
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    if(tablero[i][j] != otro.tablero[2-j][i]) return false;
                }
            }
            return true;
        }

        /** Rota el otro tablero 180° en la dirección de las manecillas del reloj. */
        boolean esSimetrico180(Gato otro){
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    if(tablero[i][j] != otro.tablero[2-i][j]) return false;
                }
            }
            return true;
        }

        /** Rota el otro tablero 270° en la dirección de las manecillas del reloj. */
        boolean esSimetrico270(Gato otro){
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    if(tablero[i][j] != otro.tablero[2-j][2-i]) return false;
                }
            }
            return true;
        }

        /**
        * Indica si dos estados del juego del gato son iguales, considerando simetrías, 
        * de este modo el problema se vuelve manejable.
        */
        @Override
        public boolean equals(Object o){
            Gato otro = (Gato)o;
            if(esIgual(otro)) return true;

            if(esSimetricoDiagonalInvertida(otro)) return true;
            if(esSimetricoDiagonal(otro)) return true;
            if(esSimetricoVerticalmente(otro)) return true;
            if(esSimetricoHorizontalmente(otro)) return true;
            if(esSimetrico90(otro)) return true;
            if(esSimetrico180(otro)) return true;
            if(esSimetrico270(otro)) return true;


            return false;
        }

        /* Explicación el README */
        @Override
        public int hashCode () {
            int hash = 0;
            int [][] kernel = {{1,2,1},{2,4,2},{1,2,1}};
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    hash += kernel[i][j] * tablero[i][j];
                }
            }
            return hash;
        }


        /** Devuelve una representación con caracteres de este estado.
        *  Se puede usar como auxiliar al probar segmentos del código. 
        */
        @Override
        public String toString(){
            char simbolo = jugador1 ? 'o' : 'x';
            String gs = "";
            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    gs += ((tablero[i][j] == MARCA2) ? 'x' : (tablero[i][j] == MARCA1) ? 'o' : " ") + " ";
                }
                gs += '\n';
            }
            return gs;
        }
    }

    class Accion {
        int simbolo;
        int x;
        int y;

        public Accion(int simbolo, int x, int y) {
           this.simbolo =  simbolo;
           this.x =  x;
           this.y =  y; 
        }
    }

    /*
        Calcula la decision que se debe tomar en base al 
        en base al algoritmo de Minimax
    */
    public Accion decision(Gato estado) {
        return null;
    }

    public static void main (String[] args) {
        System.out.println("Test minimax");
        Minimax m =  new Minimax();

        
        Gato estado1 =  new Gato();
        estado1.tiraEn(1,0, true);
        estado1.tiraEn(1,1, true);
        estado1.tiraEn(2,1, true);
        estado1.tiraEn(2,0, false);
        estado1.tiraEn(0,1, false);
        estado1.tiraEn(2,2, false);

        Gato estado2 =  new Gato();
        estado2.tiraEn(2,1, true);
        estado2.tiraEn(1,2, true);
        estado2.tiraEn(0,2, false);
        estado2.tiraEn(2,0, false);
        estado2.tiraEn(2,2, false);

        Gato estado3 =  new Gato();
        estado3.tiraEn(1,0, true);
        estado3.tiraEn(1,1, true);
        estado3.tiraEn(2,1, true);
        estado3.tiraEn(2,0, false);
        estado3.tiraEn(0,1, false);
        estado3.tiraEn(2,2, false);

        System.out.println(estado1);
        System.out.println(estado2);
        System.out.println(estado3);



    }


}