package it.unicam.cs.mpgc.rpg126710.service;

import java.util.Objects;

/**
 * Notizia immutabile di qualcosa accaduto nella partita.
 *
 * <p>Tipo e messaggio viaggiano sempre insieme, quindi stanno in un unico
 * oggetto invece di essere passati come due parametri separati.</p>
 */
public final class EventoGioco {

    private final TipoEvento tipo;
    private final String messaggio;

    /**
     * @param tipo      categoria dell'evento, non nulla
     * @param messaggio testo leggibile dall'utente, non vuoto
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    public EventoGioco(TipoEvento tipo, String messaggio) {
        if (tipo == null) {
            throw new IllegalArgumentException("Il tipo dell'evento non puo' essere nullo");
        }
        if (messaggio == null || messaggio.isBlank()) {
            throw new IllegalArgumentException("Il messaggio dell'evento non puo' essere vuoto");
        }
        this.tipo = tipo;
        this.messaggio = messaggio;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public String getMessaggio() {
        return messaggio;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EventoGioco)) {
            return false;
        }
        EventoGioco altro = (EventoGioco) obj;
        return this.tipo == altro.tipo && this.messaggio.equals(altro.messaggio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, messaggio);
    }

    @Override
    public String toString() {
        return messaggio;
    }
}
