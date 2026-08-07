package it.unicam.cs.mpgc.rpg126710.model.mondo;

/**
 * Direzioni percorribili per passare da una stanza all'altra.
 */
public enum Direzione {

    NORD("Nord"),
    SUD("Sud"),
    EST("Est"),
    OVEST("Ovest");

    private final String etichetta;

    Direzione(String etichetta) {
        this.etichetta = etichetta;
    }

    /**
     * @return il nome della direzione da mostrare all'utente
     */
    public String getEtichetta() {
        return etichetta;
    }

    /**
     * Restituisce la direzione da cui si proviene percorrendo questa.
     *
     * <p>Serve al generatore di mappe per creare passaggi percorribili nei due
     * sensi senza doverli dichiarare due volte.</p>
     *
     * @return la direzione opposta
     */
    public Direzione opposta() {
        return switch (this) {
            case NORD -> SUD;
            case SUD -> NORD;
            case EST -> OVEST;
            case OVEST -> EST;
        };
    }
}
