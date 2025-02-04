package aed;

import aed.Distrito.Votos;


public class Heap {
    private Votos[] _A;
    
    // Invariante de Representación:
    // 1 - Todo nodo es un heap: si tiene hijos, los mismos son menores que la raíz, y sus hijos son un heap a la vez.

    // Funciones privadas (para modularizar el problema):

    private boolean noEsHoja(int pos){ return ((2*pos + 1) < _A.length); } // O(1)

    private boolean derEsMasGrande(int pos) { // O(1)
        if((2*pos + 2) >= _A.length) return false;
        else return (_A[2*pos + 1].votos() < _A[2*pos + 2].votos());
    }

    private boolean hayHijoMasGrande(int pos) { // O(1)
        // requiere que tenga al menos un hijo izq
        int izq = 2*pos + 1;
        int der = 2*pos + 2;
        if(der >= _A.length) return (_A[pos].votos() < _A[izq].votos());
        return ((_A[pos].votos() < _A[der].votos()) || (_A[pos].votos() < _A[izq].votos()));
    } 
 
    private int Padre(int pos) { // O(1)
        if (pos%2 == 0) return (Math.floorDiv((pos-2),2));
        else return (Math.floorDiv(pos - 1, 2));
    }

    private void Bajar(int i){ // baja cualquier pos del array en O(log_2 n)
        int pos = i;
        Votos aux;
        while(noEsHoja(pos)){
            if(hayHijoMasGrande(pos)) {
                if(derEsMasGrande(pos)) {
                    aux = _A[pos];
                    _A[pos] = _A[2*pos + 2];
                    pos = 2*pos + 2;
                    _A[pos] = aux;
                } else {
                    aux = _A[pos];
                    _A[pos] = _A[2*pos + 1];
                    pos = 2*pos + 1;
                    _A[pos] = aux;
                }
            } else break;
        }
    }


    // Funciones públicas :

    public Votos Siguiente(){ return _A[0]; }   // O(1)
    
    public void modificarMaximo(Votos max) {    // O(log_2 n)
        _A[0] = max;
        Bajar(0);
    }

    public void Ver(){ // Para debuggear 
        String res = "[";
        for(int i = 0; i < _A.length - 1; i ++) res += _A[i].VerVotos() + ",";
        res += _A[_A.length - 1].VerVotos() + "]";
        System.out.println(res);
    }


    // Heapify (constructor): 

    public Heap(Votos[] A) { 
        _A = A; // se pasa por referencia -> más eficiente

        // arranca desde el último padre para ahorrar operaciones
        int ultPadre = Padre(_A.length - 1); 
        // Floyd :
        for(int i = ultPadre; i >= 0; i--) Bajar(i); // ordena los subárboles en orden ascendente en O(P)
    }

    // OBS : la primera versión tenía funciones para subir, encolar y desencolar, pero nos dimos cuenta de
    //       que el problema no lo requería; la única función que importa es la de "Bajar".

}