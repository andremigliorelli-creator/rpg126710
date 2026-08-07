package it.unicam.cs.mpgc.rpg126710.api;

import java.util.Objects;

/**
 * Risultato immutabile prodotto dall'esecuzione di una {@link Azione}.
 *
 * <p>Raccoglie in un unico oggetto tre informazioni che viaggiano sempre
 * insieme: la descrizione leggibile di cio' che e' accaduto, i punti vita
 * variati e l'eventuale richiesta di terminare il combattimento. Tenerle unite
 * evita il <em>data clumping</em> che si avrebbe restituendo valori separati.</p>
 */
public final class EsitoAzione {

    private final String descrizione;
    private final int puntiVitaVariati;
    private final boolean terminaCombattimento;

    private EsitoAzione(String descrizione, int puntiVitaVariati, boolean terminaCombattimento) {
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("La descrizione dell'esito non puo' essere vuota");
        }
        this.descrizione = descrizione;
        this.puntiVitaVariati = puntiVitaVariati;
        this.terminaCombattimento = terminaCombattimento;
    }

    /**
     * Crea l'esito di un'azione che ha inflitto danno a un bersaglio.
     *
     * @param descrizione racconto leggibile dell'accaduto
     * @param danno       punti vita sottratti, non negativi
     * @return il nuovo esito
     */
    public static EsitoAzione conDanno(String descrizione, int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno di un esito non puo' essere negativo: " + danno);
        }
        return new EsitoAzione(descrizione, -danno, false);
    }

    /**
     * Crea l'esito di un'azione che ha ripristinato punti vita.
     *
     * @param descrizione racconto leggibile dell'accaduto
     * @param cura        punti vita recuperati, non negativi
     * @return il nuovo esito
     */
    public static EsitoAzione conCura(String descrizione, int cura) {
        if (cura < 0) {
            throw new IllegalArgumentException("La cura di un esito non puo' essere negativa: " + cura);
        }
        return new EsitoAzione(descrizione, cura, false);
    }

    /**
     * Crea l'esito di un'azione che non altera i punti vita di nessuno.
     *
     * @param descrizione racconto leggibile dell'accaduto
     * @return il nuovo esito
     */
    public static EsitoAzione senzaEffetto(String descrizione) {
        return new EsitoAzione(descrizione, 0, false);
    }

    /**
     * Crea l'esito di un'azione che interrompe il combattimento, come una fuga
     * riuscita.
     *
     * @param descrizione racconto leggibile dell'accaduto
     * @return il nuovo esito
     */
    public static EsitoAzione conInterruzione(String descrizione) {
        return new EsitoAzione(descrizione, 0, true);
    }

    public String getDescrizione() {
        return descrizione;
    }

    /**
     * @return variazione dei punti vita: negativa se e' stato inflitto danno,
     *         positiva in caso di cura, zero se l'azione non ha alterato la vita
     */
    public int getPuntiVitaVariati() {
        return puntiVitaVariati;
    }

    /**
     * @return {@code true} se dopo questa azione il combattimento deve chiudersi
     */
    public boolean terminaCombattimento() {
        return terminaCombattimento;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EsitoAzione)) {
            return false;
        }
        EsitoAzione altro = (EsitoAzione) obj;
        return this.puntiVitaVariati == altro.puntiVitaVariati
                && this.terminaCombattimento == altro.terminaCombattimento
                && this.descrizione.equals(altro.descrizione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descrizione, puntiVitaVariati, terminaCombattimento);
    }

    @Override
    public String toString() {
        return descrizione;
    }
}
