# Principi SOLID e Clean Code applicati

## SOLID

### S — Single Responsibility Principle

Ogni classe ha un solo motivo per cambiare.

- `Combattimento` fa rispettare le regole del turno e stabilisce quando lo
  scontro finisce. **Non** assegna esperienza, **non** distribuisce il bottino,
  **non** stampa nulla: sono compiti di `MotoreGioco` e della vista.
- `MotoreGioco` applica le regole della partita ma non salva e non disegna.
- `RepositoryPartitaJson` si occupa solo di scrivere e rileggere.
- `Equipaggiamento` sa cosa è indosso e quale bonus ne deriva; non decide *quando*
  equipaggiare, che è una regola di gioco e vive nel motore.

### O — Open/Closed Principle

Il sistema è aperto all'estensione e chiuso alla modifica.

- Aggiungere una mossa significa aggiungere una classe che implementa `Azione`:
  `Combattimento` non cambia, perché non contiene alcun `if` sul tipo di mossa.
- Aggiungere una classe giocabile significa estendere `Eroe` e registrarla in
  `FabbricaEroi`: il metodo di creazione non cambia, perché le classi sono in un
  registro e non in una catena di confronti.
- Aggiungere contenuti significa modificare un file JSON: il codice non cambia
  affatto.

### L — Liskov Substitution Principle

Ogni sottotipo può sostituire il tipo base senza sorprendere chi lo usa.

- Le aspettative implicite sono state rese **esplicite nella documentazione dei
  contratti**: `Dado` deve restituire un valore fra 1 e il numero di facce,
  `Combattente.calcolaDanno()` non può restituire valori negativi, `Azione` non
  può restituire `null`.
- Le sottoclassi di `AzioneAttacco` cambiano *quanto* danno fanno, mai il
  contratto: restituiscono sempre un `EsitoAzione` valido e non toccano
  combattenti diversi da attore e bersaglio.
- `DadoFisso`, usato nei test, rispetta il contratto di `Dado` e può quindi
  sostituirlo ovunque senza che il codice se ne accorga: è la prova pratica che
  il principio è rispettato.

### I — Interface Segregation Principle

Nessuna classe è costretta a implementare metodi che non le servono.

- I contratti sono minuscoli: `Dado` ha un metodo, `GeneratoreMappa` uno,
  `OsservatoreGioco` uno, `StrategiaCombattimento` uno.
- `Equipaggiabile` e `Utilizzabile` sono **separati**: un'arma si equipaggia e
  resta attiva, una pozione si consuma. Un unico contratto obbligherebbe la
  pozione a dichiarare un bonus permanente e l'arma un effetto immediato.
- `CaricatoreDungeonJson` implementa due interfacce distinte perché chi la usa
  ne ha bisogno in momenti diversi: il motore vuole una mappa, il caricamento di
  una partita vuole ricostruire oggetti.

> **Una correzione fatta in corso d'opera.** Il progetto conteneva inizialmente
> un'interfaccia `Attaccante` separata da `Combattente`. È stata rimossa: in
> questo dominio **ogni** combattente attacca, quindi la separazione non evitava
> metodi inutili a nessuno e aggiungeva soltanto un livello di indirezione. ISP
> chiede interfacce mirate, non interfacce numerose.

### D — Dependency Inversion Principle

Si dipende dalle astrazioni, non dalle implementazioni concrete.

- `MotoreGioco` dipende da `OsservatoreGioco`, non da `SchermataGioco`.
- `GestorePartita` dipende da `GeneratoreMappa`, `CatalogoOggetti`,
  `RepositoryPartita` e `Dado`: riceve tutto dal costruttore e non costruisce
  nulla da sé.
- `Personaggio` dipende da `Dado`, non da `java.util.Random`.
- Le implementazioni concrete vengono scelte **in un unico punto**,
  `ApplicazioneRpg`, che è l'unica classe a conoscere sia i contratti sia le
  implementazioni.

---

## Clean Code

| Principio | Applicazione nel progetto |
|---|---|
| **Nomi significativi** | Nomi in italiano, per intero e nel dominio del problema: `assegnaRicompense`, `avviaCombattimentoSePresenteNemico`, `stanzaAdiacente`. Nessuna abbreviazione |
| **Funzioni piccole** | I metodi pubblici raccontano *cosa* accade e delegano il *come*: `entraNellaStanza` chiama `avviaCombattimentoSePresenteNemico` e `risolviStanzaLibera`. Quasi tutti i metodi stanno in poche righe |
| **Code as prose** | `eseguiTurno` si legge come una frase: esegui la mossa dell'eroe, aggiorna lo stato, fai rispondere il nemico, aggiorna lo stato |
| **Organizzazione verticale** | In ogni classe i metodi pubblici stanno in alto e i metodi privati di supporto sotto, nell'ordine in cui vengono richiamati |
| **DRY** | La logica del colpo base è scritta una volta in `AzioneAttacco`; le abilità speciali ridefiniscono solo ciò che cambia. La validazione dei partecipanti è in un unico metodo protetto |
| **Data clumping** | Dati che viaggiano sempre insieme stanno in un oggetto: `EsitoAzione` invece di tre valori di ritorno, `EventoGioco` invece di tipo e messaggio separati, `Bonus` invece di due interi sciolti |
| **Error handling** | I controlli stanno vicino ai dati che proteggono, cioè nei costruttori e nei setter. Si valida subito, si fallisce subito, con messaggi che dicono quale valore è sbagliato: `"Matricola non valida: -3"` e non `"errore"` |
| **Commenti utili** | Il Javadoc spiega il *perché* delle scelte non ovvie — perché `Personaggio` non ridefinisce `equals`, perché i punti vita massimi non si sommano ai bonus — e documenta le aspettative dei contratti. Nessun commento che ripete ciò che il codice già dice |
| **Niente codice commentato** | Le versioni precedenti stanno nella cronologia Git, non fra i commenti |
| **KISS** | Nessuna astrazione introdotta "per il futuro" senza un uso reale nel progetto |

### Un esempio di refactoring documentato

Il bonus di equipaggiamento era inizialmente espresso con la stessa classe
`Statistiche` usata per i personaggi. Questo costringeva a scrivere
`new Statistiche(3, 0, 1)` per descrivere una spada, dove quell'`1` era un
valore di punti vita **privo di significato**, presente solo per soddisfare un
controllo di validità.

È stato introdotto il tipo `Bonus`, che contiene esattamente ciò che un oggetto
equipaggiato aggiunge: attacco e difesa. Il codice ora dice la verità su ciò che
rappresenta, e il controllo "i punti vita massimi devono essere positivi" resta
valido senza eccezioni artificiose.

---

## Testing

94 test di unità con JUnit 6, che coprono value object, inventario, progressione
dell'eroe, mappa, combattimento, persistenza e motore di gioco.

Sono stati seguiti i criteri visti a lezione:

- **Automatici e rapidi**: `./gradlew test` esegue tutto in pochi secondi.
- **Ripetibili**: `DadoFisso` sostituisce il generatore pseudocasuale, altrimenti
  l'esito di uno scontro cambierebbe a ogni esecuzione. È possibile farlo perché
  il codice di produzione dipende dall'astrazione `Dado`.
- **Indipendenti dall'ambiente**: i test di persistenza scrivono in una cartella
  temporanea fornita da JUnit e non lasciano tracce.
- **Indipendenti fra loro**: ogni test prepara il proprio stato in `@BeforeEach`.
- **Condizioni al contorno**: valori negativi, zero, quantità nulle, oggetti
  nulli, danno superiore ai punti vita residui, cure oltre il massimo.
- **Verifica delle eccezioni** con `assertThrows`, non solo dei casi felici.
- Uso di `@Nested` per raggruppare gli scenari, `@DisplayName` per rendere il
  report leggibile, `@ParameterizedTest` con `@CsvSource` per i casi ripetitivi e
  `assertAll` per non far nascondere un errore dagli altri.

Un test ha effettivamente **individuato un fraintendimento**: nel colpo possente
il dado dell'abilità decide solo se il colpo va a segno, mentre la potenza
dipende dal dado di chi attacca. L'attesa iniziale era sbagliata ed è stata
corretta insieme al commento che ora spiega la regola.
