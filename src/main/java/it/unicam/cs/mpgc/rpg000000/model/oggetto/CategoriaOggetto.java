package it.unicam.cs.mpgc.rpg000000.model.oggetto;

/**
 * Categorie in cui si suddividono gli oggetti del gioco.
 *
 * <p>Serve all'interfaccia grafica per raggruppare l'inventario e al livello di
 * persistenza per sapere quale sottoclasse ricostruire quando rilegge un
 * salvataggio.</p>
 */
public enum CategoriaOggetto {

    ARMA("Arma"),
    ARMATURA("Armatura"),
    POZIONE("Pozione");

    private final String etichetta;

    CategoriaOggetto(String etichetta) {
        this.etichetta = etichetta;
    }

    /**
     * @return il nome della categoria da mostrare all'utente
     */
    public String getEtichetta() {
        return etichetta;
    }
}
