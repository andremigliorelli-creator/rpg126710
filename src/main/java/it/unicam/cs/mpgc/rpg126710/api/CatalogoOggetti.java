package it.unicam.cs.mpgc.rpg126710.api;

import it.unicam.cs.mpgc.rpg126710.model.oggetto.Oggetto;

import java.util.Collection;
import java.util.Optional;

/**
 * Elenco degli oggetti che possono esistere in una partita.
 *
 * <p>Serve a ricostruire un inventario a partire da un salvataggio: nel file
 * vengono memorizzati solo i nomi degli oggetti, ed e' il catalogo a
 * restituirne la definizione completa. In questo modo un salvataggio resta
 * valido anche se in futuro si decide di ribilanciare le statistiche di
 * un'arma.</p>
 */
public interface CatalogoOggetti {

    /**
     * Cerca un oggetto per nome.
     *
     * @param nome nome dell'oggetto, non nullo
     * @return l'oggetto corrispondente, se presente nel catalogo
     */
    Optional<Oggetto> trova(String nome);

    /**
     * @return tutti gli oggetti conosciuti, in sola lettura
     */
    Collection<Oggetto> tutti();
}
