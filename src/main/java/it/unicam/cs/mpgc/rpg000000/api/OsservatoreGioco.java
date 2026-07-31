package it.unicam.cs.mpgc.rpg000000.api;

import it.unicam.cs.mpgc.rpg000000.service.EventoGioco;

/**
 * Componente interessato a essere avvisato di cio' che accade nella partita.
 *
 * <p>E' il meccanismo con cui il modello resta indipendente dall'interfaccia
 * grafica: il motore di gioco non conosce la finestra JavaFX, si limita ad
 * annunciare gli eventi a chi si e' registrato. Sostituire la vista, o
 * aggiungerne una seconda, non richiede di modificare il motore.</p>
 */
@FunctionalInterface
public interface OsservatoreGioco {

    /**
     * Notifica che nella partita e' accaduto qualcosa.
     *
     * @param evento descrizione dell'accaduto, mai {@code null}
     */
    void suEvento(EventoGioco evento);
}
