/**
 * Descrittore del modulo applicativo.
 *
 * <p>Dichiararlo rende il progetto un vero modulo JPMS: JavaFX viene caricato
 * dal module path invece che dal classpath, come la libreria si aspetta dalla
 * versione 9 in poi.</p>
 *
 * <p>Il modulo non esporta alcun package perche' nessun altro modulo lo usa:
 * e' un'applicazione eseguibile, non una libreria. Concede invece tre aperture
 * mirate, una per ciascun punto in cui una libreria esterna deve raggiungere
 * queste classi tramite reflection. Ogni apertura e' rivolta al solo modulo che
 * ne ha bisogno, non a chiunque.</p>
 */
module it.unicam.cs.mpgc.rpg126710 {

    requires javafx.controls;
    requires com.google.gson;

    // JavaFX istanzia da solo la classe dell'applicazione: senza questa
    // apertura Application.launch non riuscirebbe a costruirla.
    opens it.unicam.cs.mpgc.rpg126710.ui to javafx.graphics;

    // Gson costruisce i DTO del dungeon leggendone i campi privati.
    opens it.unicam.cs.mpgc.rpg126710.persistence.dto to com.google.gson;

    // Stesso motivo per lo stato della partita, che Gson scrive e rilegge.
    opens it.unicam.cs.mpgc.rpg126710.service to com.google.gson;
}
