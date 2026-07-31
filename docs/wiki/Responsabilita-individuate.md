# Responsabilità individuate

Prima di scrivere codice il problema è stato scomposto chiedendosi *di quali
compiti distinti è fatto questo sistema*. Ogni compito è diventato un package, e
all'interno di ciascun package ogni sotto-compito è diventato una classe.

## Le domande di partenza

Seguendo l'impostazione vista a lezione:

- **Quali entità esistono nel problema?** → eroi, nemici, oggetti, stanze, scontri
- **Quali informazioni le descrivono?** → i campi delle classi del `model`
- **Quali azioni possono compiere?** → i metodi esposti
- **Come interagiscono fra loro?** → le associazioni e i contratti del package `api`

## Le sei responsabilità principali

### 1. Definire i contratti — package `api`

Stabilire *cosa* i vari pezzi si promettono a vicenda, senza dire *come*.
È lo strato da cui dipendono tutti gli altri e che non dipende da nessuno.

### 2. Rappresentare il dominio — package `model`

Sapere cos'è un eroe, quanto danno fa un colpo, cosa contiene una stanza. Le
classi del modello **proteggono il proprio stato**: nascono valide e rifiutano
operazioni che le renderebbero incoerenti. Non sanno nulla di file, finestre o
salvataggi.

Il modello è a sua volta suddiviso per sotto-dominio:

| Sotto-package | Responsabilità |
|---|---|
| `model.personaggio` | chi combatte: statistiche, eroi, classi giocabili, nemici |
| `model.oggetto` | cosa si raccoglie: armi, armature, pozioni, inventario, equipaggiamento |
| `model.mondo` | dove si combatte: direzioni, stanze, mappa |
| `model.combattimento` | come si combatte: azioni, strategie, motore a turni |

### 3. Far rispettare le regole della partita — package `service`

Tenere insieme eroe, mappa e scontri e decidere cosa può accadere e quando: non
ci si sposta mentre si combatte, entrare in una stanza presidiata avvia uno
scontro, vincere assegna esperienza. Questo strato **annuncia** ciò che accade,
ma non sa a chi.

### 4. Conservare e recuperare i dati — package `persistence`

Leggere i contenuti del gioco, scrivere e rileggere i salvataggi. È l'unico
punto che conosce il formato JSON e il file system.

### 5. Mostrare e raccogliere comandi — package `ui`

Disegnare lo stato della partita e tradurre i clic in richieste al motore. Non
contiene alcuna regola di gioco: se una condizione decide qualcosa, quella
condizione sta nel motore, non nella finestra.

### 6. Avviare il programma — `Main`

Un solo compito: far partire l'applicazione.

## Il criterio usato per separare

Per ogni classe è stata posta la domanda suggerita a lezione: **"per quale
motivo questa classe dovrebbe cambiare?"** Se le risposte erano più di una, la
classe è stata divisa.

Alcuni esempi concreti di separazione decisa in questo modo:

| Cambiamento ipotizzato | Cosa si modifica | Cosa **non** si modifica |
|---|---|---|
| Il danno di un'arma va ribilanciato | `dungeon.json` | nessuna classe Java |
| Si aggiunge una stanza al dungeon | `dungeon.json` | nessuna classe Java |
| Cambia il modo in cui un nemico sceglie le mosse | una classe strategia | `Nemico`, `Combattimento` |
| Si aggiunge una nuova abilità | una nuova classe azione | il motore di combattimento |
| I salvataggi passano a un database | una nuova implementazione di `RepositoryPartita` | motore, modello, interfaccia |
| Si affianca un'interfaccia testuale | un nuovo osservatore | motore e modello |

## Responsabilità che sono state tolte da dove erano finite

Durante lo sviluppo due responsabilità sono state spostate, dopo essersi accorti
che stavano nel posto sbagliato:

1. **Il calcolo del danno di un'azione** era inizialmente diviso fra un'interfaccia
   `Attaccante` e le classi azione. L'interfaccia è stata rimossa: in questo
   dominio ogni combattente attacca, quindi separarla non evitava metodi inutili
   a nessuno e complicava soltanto la gerarchia.

2. **Il bonus di equipaggiamento** era espresso con la stessa classe `Statistiche`
   usata per i personaggi, il che costringeva a inventare un valore fittizio di
   punti vita per descrivere una spada. È stato introdotto il tipo `Bonus`,
   che rappresenta esattamente ciò che un oggetto equipaggiato aggiunge.
