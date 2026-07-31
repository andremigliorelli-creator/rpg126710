package it.unicam.cs.mpgc.rpg000000.model.personaggio;

import it.unicam.cs.mpgc.rpg000000.api.Dado;
import it.unicam.cs.mpgc.rpg000000.api.StrategiaCombattimento;
import it.unicam.cs.mpgc.rpg000000.model.oggetto.Oggetto;

import java.util.Optional;

/**
 * Creatura ostile che popola le stanze del dungeon.
 *
 * <p>Il comportamento in combattimento non e' scritto qui ma ricevuto dal
 * costruttore sotto forma di {@link StrategiaCombattimento}: due nemici con le
 * stesse statistiche possono cosi' comportarsi in modo diverso senza bisogno di
 * creare due classi.</p>
 */
public class Nemico extends Personaggio {

    private final StrategiaCombattimento strategia;
    private final int esperienzaConcessa;
    private final Oggetto bottino;

    /**
     * @param nome               nome della creatura, non vuoto
     * @param statistiche        statistiche di combattimento, non nulle
     * @param dado               sorgente di casualita', non nulla
     * @param strategia          criterio di scelta delle mosse, non nullo
     * @param esperienzaConcessa esperienza data all'eroe che la sconfigge, non negativa
     * @param bottino            oggetto lasciato cadere, eventualmente {@code null}
     * @throws IllegalArgumentException se un parametro non rispetta i vincoli
     */
    public Nemico(String nome,
                  Statistiche statistiche,
                  Dado dado,
                  StrategiaCombattimento strategia,
                  int esperienzaConcessa,
                  Oggetto bottino) {
        super(nome, statistiche, dado);
        if (strategia == null) {
            throw new IllegalArgumentException("La strategia del nemico non puo' essere nulla");
        }
        if (esperienzaConcessa < 0) {
            throw new IllegalArgumentException(
                    "L'esperienza concessa non puo' essere negativa: " + esperienzaConcessa);
        }
        this.strategia = strategia;
        this.esperienzaConcessa = esperienzaConcessa;
        this.bottino = bottino;
    }

    public StrategiaCombattimento getStrategia() {
        return strategia;
    }

    public int getEsperienzaConcessa() {
        return esperienzaConcessa;
    }

    /**
     * @return l'oggetto lasciato cadere alla sconfitta, se previsto
     */
    public Optional<Oggetto> getBottino() {
        return Optional.ofNullable(bottino);
    }
}
