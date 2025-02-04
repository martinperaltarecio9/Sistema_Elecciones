package aed;

public class Distrito {
    private int _cantBancas;
    private String _nombre;
    private int[] _bancasPartidos; // se guarda la cantidad de bancas por partido
    private int[] _votosDiputados; // se guardan los votos a diputados por cada partido
    private Heap _maxHeap; // esta va a ser la forma en la que se calculan las bancas (según dhont)
    private int _votosEnBlanco;
    private boolean _yaSeCalculoBancas; // para resolver el problema de si nos piden dos veces los resultados
    private int _sumaTotal; // la suma total de los votos para diputados en el distrito

    // Invariante de Representación:
    // 1 - La cantidad de bancas '_cantBancas' es mayor o igual a la suma de las bancas por partido. (va a ser igual
    //     si hay al menos un partido que pasa el umbral, y mayor en caso de que ninguno pase el umbral, o si se 
    //     registró una nueva mesa).
    // 2 - La suma total va a ser igual a la suma de los votos de cada partido y los votos en blanco.
    // 3 - La distribución de las bancas en '_bancasPartidos' sigue el método D'Hont (considerando a '_votosDiputados' 
    //     y con la condición de que se calculan las bancas sólo para los partidos que pasen el umbral).
    // 4 - Las bancas de cada partido son nulas si se registra una mesa.
    // 5 - '_yaSeCalculoBancas' será TRUE en caso de que se haya accionado 'calcularBancas', y FALSE al momento de 
    //      registrarse una nueva mesa.
    // 6 - Los id de los partidos se guardan en el maxHeap como corresponde: es decir, no se repiten ids, y cada id aparece.
    //     Además, al momento de registrarse una mesa, el maxHeap guarda los votos de cada partido (con su id correspondiente),
    //     tal cual están en la actualidad (si pasan el umbral, el que no lo pase tendrá 0 votos). Luego, cuando se calculen las bancas,
    //     quedarán los últimos coeficientes de la matriz de D'Hont (ya que se utiliza el heap para calcular la cantidad de bancas).  

    public Distrito(int cantBancas, String nombre, int P) { 
        this._cantBancas = cantBancas; 
        this._nombre = nombre;
        this._bancasPartidos = new int[P]; // O(P)
        this._votosDiputados = new int[P]; // O(P)
        this._votosEnBlanco = 0;
        this._yaSeCalculoBancas = false;
        this._sumaTotal = 0;
    }

    public class Votos {    // para conservar el id de los partidos al hacer el heap
        private int _idPartido;
        private double _votos;
        Votos(double d, int n) { this._idPartido = n; this._votos = d; }
        public void modificar(double d, int n) { this._idPartido = n; this._votos = d; }
        public double votos() { return _votos; }
        public int id() { return _idPartido; }
        public String VerVotos() {
            String res = "<";
            res += Double.toString(_votos) + "," + Integer.toString(_idPartido) + ">";
            return res;
        }
    }
    
    public int Blanco() { return _votosEnBlanco; }
    public String Nombre() { return _nombre; }
    public int cantidadBancas() { return _cantBancas; }
    public int votosDiputados_D(int idPartido) { return _votosDiputados[idPartido]; }

    public void calcularBancas() {

        if(_yaSeCalculoBancas == true) return; 

        _yaSeCalculoBancas = true; // ya que las vamos a calcular ahora

        for(int j = 0; j < _cantBancas; j ++){
            Votos v = _maxHeap.Siguiente();
            _bancasPartidos[v.id()] ++;                               // suma una banca al id correspondiente
            int divisor = _bancasPartidos[v.id()] + 1;                // se guarda el divisor que le toca
            v.modificar(_votosDiputados[v.id()]/divisor, v.id());     // modifica el máximo valor del heap
            _maxHeap.modificarMaximo(v);                              // reajusta el heap   O(log_2 P)
        }
    // TOTAL = O(cantBancas * log_2 P)
    }

    public int[] resultadosBancas() { return _bancasPartidos; }

    public void registrarMesa_D(int[] votosDiputados, int votosEnBlanco, int sumaVotosDiputados) {
        // Se actualizan los votos para diputados y se "reinician" las bancas asignadas a cada partido
        _votosEnBlanco += votosEnBlanco;
        _sumaTotal = _sumaTotal + (sumaVotosDiputados + votosEnBlanco);
        double umbral = 3*_sumaTotal/100;
        Votos[] pasanUmbral = new Votos[_votosDiputados.length];
        double aux;

        for(int i = 0; i < _votosDiputados.length; i ++) {  // O(P)
            _bancasPartidos[i] = 0;

            aux = _votosDiputados[i] + votosDiputados[i];
            if (aux >= umbral) pasanUmbral[i] = new Votos(aux,i);
            else pasanUmbral[i] = new Votos(0,i);
            
             _votosDiputados[i] += votosDiputados[i];
        }

        // Se desecha el anterior heap y se construye uno nuevo con los valores actuales (que pasan el umbral).     
        _maxHeap = new Heap(pasanUmbral); // O(P)
        _yaSeCalculoBancas = false; // cada vez que se registra una mesa se deben calcular nuevamente las bancas

        // TOTAL = O(2P)
    }

}
