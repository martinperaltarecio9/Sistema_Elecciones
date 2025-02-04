package aed;

public class SistemaCNE {
    private int[] _votosPresidente;
    private Distrito[] _Distritos;
    private String[] _nombresPartidos;
    private int[] _ultimasMesas;
    private boolean _ballotage;

    // Invariante de Representación:
    // 1 - El largo de '_ultimasMesas' es igual a la cantidad de distritos.
    // 2 - '_ballotage' es TRUE si y sólo si hay escenario de ballotage en '_votosPresidente'.
    // 3 - El largo de '_nombresPartidos' es igual a uno menos el largo de '_votosPresidente' (por los votos en blanco). 
    // 4 - 'ultimasMesas' está ordenado en orden ascendente, y sus elementos son todos mayores a cero.

    public class VotosPartido {
        private int presidente;
        private int diputados;
        VotosPartido(int presidente, int diputados){this.presidente = presidente; this.diputados = diputados;}
        public int votosPresidente(){return presidente;}
        public int votosDiputados(){return diputados;}
    }
    
    private int encontrarDistrito(int idMesa) {
        int L = 0;
        int R = _ultimasMesas.length - 1;
        int i;

        while(L <= R){
            if(R == L) return R;
            if(L == R - 1) i = R;
            else i = Math.floorDiv((L+R),2);
            if(i == 0) {
                if ((_ultimasMesas[0] < idMesa) && (idMesa <= _ultimasMesas[1])) return 1;
                else return 0;
            }
            if((_ultimasMesas[i-1] <= idMesa) && (idMesa < _ultimasMesas[i])) return i;
            if(_ultimasMesas[i] <= idMesa) L = i;
            else R = i - 1;
        }
        return 0;
    }

    public SistemaCNE(String[] nombresDistritos, int[] diputadosPorDistrito, String[] nombresPartidos, int[] ultimasMesasDistritos) {
        this._ultimasMesas = ultimasMesasDistritos;
        this._nombresPartidos = nombresPartidos;
        this._votosPresidente = new int[nombresPartidos.length];
        int P = nombresPartidos.length - 1;
        this._Distritos = new Distrito[nombresDistritos.length]; 

        for(int i = 0; i < nombresDistritos.length; i ++) {     // O(PxD)
            this._Distritos[i] = new Distrito(diputadosPorDistrito[i],nombresDistritos[i],P);
        }

        this._ballotage = false;
    }

    public String nombrePartido(int idPartido) { return _nombresPartidos[idPartido]; }

    public String nombreDistrito(int idDistrito) { return _Distritos[idDistrito].Nombre(); }

    public int diputadosEnDisputa(int idDistrito) { return _Distritos[idDistrito].cantidadBancas(); }

    public String distritoDeMesa(int idMesa) {
        int d = encontrarDistrito(idMesa);
        return (_Distritos[d].Nombre());
    }

    public void registrarMesa(int idMesa, VotosPartido[] actaMesa) {
        int d = encontrarDistrito(idMesa);  // se encuentra el distrito en O(log_2 D)
        int primero = _votosPresidente[0] + actaMesa[0].votosPresidente();
        int aux;
        int segundo = -1; 
        int sumaVotosPresidente = primero;
        int sumaVotosDiputados = 0;

        // se actualizan los votos a presidente y se guardan los de diputados en un arreglo auxiliar
        int[] A = new int[actaMesa.length-1];               // O(P)

        for(int i = 0; i < actaMesa.length - 1; i ++) {     // O(P)
            A[i] = actaMesa[i].votosDiputados();
            sumaVotosDiputados += actaMesa[i].votosDiputados();
            
            if (i >= 1) { // esto se hizo para revisar el ballotage
                aux = _votosPresidente[i] + actaMesa[i].votosPresidente();
                sumaVotosPresidente += aux;
                if (aux > segundo) {
                    if (aux > primero) {
                        segundo = primero;
                        primero = aux;
                    } else segundo = aux;
                }
            }

            _votosPresidente[i] += actaMesa[i].votosPresidente();
        }

        _votosPresidente[_votosPresidente.length - 1] += actaMesa[actaMesa.length - 1].votosPresidente();

        _Distritos[d].registrarMesa_D(A,actaMesa[actaMesa.length - 1].votosDiputados(),sumaVotosDiputados);    // lleva O(P)

        // el resto del código se hizo para el tema del ballotage (todo en tiempo constante)
        if(_votosPresidente.length == 2) {
            _ballotage = false;
            return;
        }

        sumaVotosPresidente += _votosPresidente[_votosPresidente.length - 1]; // se suman los votos en blanco

        if(sumaVotosPresidente == 0) {
            _ballotage = false; 
            return;
        }

        double porcentajePrimero = primero*100/sumaVotosPresidente;
        double porcentajeSegundo = segundo*100/sumaVotosPresidente;
        if(porcentajePrimero >= 45) _ballotage = false;
        else if ((porcentajePrimero >= 40) && (porcentajePrimero - porcentajeSegundo >= 10)) _ballotage = false;
        else _ballotage = true;

        // TOTAL = O(log_2 D + 2P)
    }

    public int votosPresidenciales(int idPartido) {
        return (_votosPresidente[idPartido]);
    }

    public int votosDiputados(int idPartido, int idDistrito) {
        if(idPartido < _nombresPartidos.length - 1) return _Distritos[idDistrito].votosDiputados_D(idPartido);
        else return _Distritos[idDistrito].Blanco();
    }

    public int[] resultadosDiputados(int idDistrito){
        _Distritos[idDistrito].calcularBancas();
        return(_Distritos[idDistrito].resultadosBancas());
    }

    public boolean hayBallotage(){
        return _ballotage;
    }

}

