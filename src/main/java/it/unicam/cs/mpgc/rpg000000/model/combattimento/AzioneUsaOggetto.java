package it.unicam.cs.mpgc.rpg000000.model.combattimento;

import it.unicam.cs.mpgc.rpg000000.api.Azione;
import it.unicam.cs.mpgc.rpg000000.api.Combattente;
import it.unicam.cs.mpgc.rpg000000.api.EsitoAzione;
import it.unicam.cs.mpgc.rpg000000.api.Utilizzabile;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Inventario;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Oggetto;

/**
 * Azione con cui un combattente consuma un oggetto del proprio inventario per
 * applicarne l'effetto su se stesso.
 *
 * <p>Il parametro di tipo e' vincolato a essere sia un {@link Oggetto} (quindi
 * conservabile in un {@link Inventario}) sia {@link Utilizzabile} (quindi
 * dotato di un effetto). In questo modo il compilatore garantisce la coerenza
 * fra l'oggetto consumato e l'effetto applicato, senza bisogno di cast.</p>
 *
 * <p>Applicazione dell'effetto e rimozione dall'inventario avvengono nella
 * stessa azione: separarle rischierebbe di lasciare l'inventario incoerente se
 * una delle due venisse dimenticata.</p>
 *
 * @param <T> tipo dell'oggetto consumato
 */
public class AzioneUsaOggetto<T extends Oggetto & Utilizzabile> implements Azione {

    private final T oggetto;
    private final Inventario inventario;

    /**
     * @param oggetto    oggetto da consumare, non nullo
     * @param inventario inventario da cui rimuoverlo, non nullo
     * @throws IllegalArgumentException se un parametro e' nullo
     */
    public AzioneUsaOggetto(T oggetto, Inventario inventario) {
        if (oggetto == null) {
            throw new IllegalArgumentException("L'oggetto da usare non puo' essere nullo");
        }
        if (inventario == null) {
            throw new IllegalArgumentException("L'inventario non puo' essere nullo");
        }
        this.oggetto = oggetto;
        this.inventario = inventario;
    }

    @Override
    public String getNome() {
        return "Usa " + oggetto.getNome();
    }

    @Override
    public String getDescrizione() {
        return oggetto.getDescrizione();
    }

    @Override
    public EsitoAzione esegui(Combattente attore, Combattente bersaglio) {
        if (attore == null) {
            throw new IllegalArgumentException("L'attore dell'azione non puo' essere nullo");
        }
        if (!inventario.contiene(oggetto)) {
            return EsitoAzione.senzaEffetto(attore.getNome() + " non possiede " + oggetto.getNome() + ".");
        }
        inventario.rimuovi(oggetto);
        return oggetto.applicaEffetto(attore);
    }
}
