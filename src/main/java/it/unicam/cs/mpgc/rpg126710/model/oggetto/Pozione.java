package it.unicam.cs.mpgc.rpg126710.model.oggetto;

import it.unicam.cs.mpgc.rpg126710.api.Combattente;
import it.unicam.cs.mpgc.rpg126710.api.EsitoAzione;
import it.unicam.cs.mpgc.rpg126710.api.Utilizzabile;

/**
 * Oggetto consumabile che ripristina punti vita al bersaglio.
 *
 * <p>Implementa {@link Utilizzabile} e non {@link it.unicam.cs.mpgc.rpg126710.api.Equipaggiabile}
 * perche' produce un effetto immediato e si esaurisce: non avrebbe senso
 * obbligarla a dichiarare un bonus permanente alle statistiche.</p>
 */
public final class Pozione extends Oggetto implements Utilizzabile {

    private final int puntiVitaRipristinati;

    /**
     * @param nome                  nome della pozione, non vuoto
     * @param descrizione           testo mostrato all'utente, non vuoto
     * @param valore                valore in monete, non negativo
     * @param puntiVitaRipristinati vita restituita all'uso, maggiore di zero
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    public Pozione(String nome, String descrizione, int valore, int puntiVitaRipristinati) {
        super(nome, descrizione, valore);
        if (puntiVitaRipristinati <= 0) {
            throw new IllegalArgumentException(
                    "Una pozione deve ripristinare punti vita positivi: " + puntiVitaRipristinati);
        }
        this.puntiVitaRipristinati = puntiVitaRipristinati;
    }

    public int getPuntiVitaRipristinati() {
        return puntiVitaRipristinati;
    }

    @Override
    public EsitoAzione applicaEffetto(Combattente bersaglio) {
        if (bersaglio == null) {
            throw new IllegalArgumentException("Il bersaglio della pozione non puo' essere nullo");
        }
        int vitaRecuperata = bersaglio.curati(puntiVitaRipristinati);
        if (vitaRecuperata == 0) {
            return EsitoAzione.senzaEffetto(
                    bersaglio.getNome() + " e' gia' in piena salute: la " + getNome() + " non ha effetto.");
        }
        return EsitoAzione.conCura(
                bersaglio.getNome() + " beve " + getNome() + " e recupera " + vitaRecuperata + " PV.",
                vitaRecuperata);
    }

    @Override
    public CategoriaOggetto getCategoria() {
        return CategoriaOggetto.POZIONE;
    }

    @Override
    public String riepilogo() {
        return getNome() + " (cura " + puntiVitaRipristinati + " PV)";
    }
}
