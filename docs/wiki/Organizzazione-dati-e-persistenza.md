# Organizzazione dei dati e persistenza

## Strutture dati scelte, e perché

Le strutture non sono state scelte a caso: ognuna risponde a un'esigenza precisa
del dominio.

| Dove | Struttura | Motivo |
|---|---|---|
| `Inventario` | `LinkedHashMap<Oggetto, Integer>` | Cercare o rimuovere un oggetto costa in media tempo costante invece di richiedere la scansione di una lista di duplicati. La variante `Linked` conserva l'ordine di raccolta, così l'utente ritrova lo zaino sempre uguale |
| `Stanza.uscite` | `EnumMap<Direzione, String>` | Le direzioni sono un insieme chiuso e noto: è la mappa più compatta ed efficiente possibile |
| `Mappa.stanze` | `LinkedHashMap<String, Stanza>` | Ogni spostamento risolve un id in una stanza: con una lista servirebbe scorrere l'intero dungeon a ogni passo |
| `Stanza.nemici` / `tesori` | `ArrayList` | Collezioni piccole, percorse per intero, dove conta l'ordine di comparsa |
| `FabbricaEroi` | `LinkedHashMap<String, BiFunction<...>>` | Registro delle classi giocabili: aggiungerne una è una riga, non un ramo `if` in più |
| `MotoreGioco.osservatori` | `ArrayList<OsservatoreGioco>` | Elenco percorso per intero a ogni notifica |

### Il ruolo di `equals` e `hashCode`

Le mappe di cui sopra funzionano solo se le chiavi sanno dire quando sono
"la stessa cosa". L'uguaglianza è stata ridefinita **solo dove esiste
un'identità chiara e stabile**:

| Classe | `equals` | Criterio | Motivo |
|---|---|---|---|
| `Oggetto` | ✅ | nome | Due pozioni minori sono lo stesso oggetto: vanno accorpate nell'inventario |
| `Stanza` | ✅ | id | L'id è assegnato alla creazione della mappa e non cambia mai |
| `Statistiche`, `Bonus`, `EsitoAzione`, `EventoGioco` | ✅ | tutti i campi | Sono value object: contano i valori, non l'istanza |
| `Personaggio` e sottoclassi | ❌ | — | Due goblin identici sono **creature diverse**: ridefinire l'uguaglianza sul nome impedirebbe di tenerne due nella stessa stanza |

Dove `equals` è stato ridefinito, `hashCode` lo è stato di conseguenza con
`Objects.hash(...)` sugli stessi campi: se due oggetti sono uguali devono
avere lo stesso codice hash, altrimenti le mappe si comportano in modo scorretto.

---

## Due tipi di dati, due destinazioni diverse

Il progetto distingue nettamente fra **dati statici di configurazione** e **dati
dinamici della partita**, perché hanno esigenze opposte.

### Contenuti del gioco → `src/main/resources/dungeon.json`

Stanze, nemici e oggetti sono definiti in un file incluso fra le risorse.

- Viene **impacchettato nel JAR** e letto con `getResourceAsStream`.
- È di **sola lettura** a esecuzione avviata: non è modificabile dall'interno
  dell'applicazione, ed è corretto così, perché descrive com'è fatto il gioco.
- Aggiungere una stanza, un nemico o un oggetto significa modificare questo file:
  **nessuna ricompilazione, nessuna riga di Java toccata**.

Struttura del file:

```json
{
  "stanzaIniziale": "ingresso",
  "oggetti": [ { "nome": "...", "categoria": "ARMA|ARMATURA|POZIONE", "potenza": 3, ... } ],
  "nemici":  [ { "id": "goblin", "attacco": 4, "strategia": "CAUTA|AGGRESSIVA", "bottino": "...", ... } ],
  "stanze":  [ { "id": "...", "uscite": { "NORD": "..." }, "nemici": ["goblin"], "tesori": ["..."] } ]
}
```

Due scelte di formato meritano una nota:

1. **I collegamenti si dichiarano una volta sola.** Il caricatore aggiunge
   automaticamente il passaggio inverso usando `Direzione.opposta()`. Il file
   resta compatto e non può contenere corridoi incoerenti a senso unico.
2. **I nemici sono modelli, non istanze.** Da un unico `"id": "scheletro"` il
   caricatore crea un oggetto distinto per ogni stanza che lo ospita: ferire uno
   scheletro non ferisce l'altro. Un test verifica proprio questo.

### Salvataggi → cartella `salvataggi/` esterna

I salvataggi cambiano continuamente, quindi **non possono** stare fra le
risorse: vengono scritti in una cartella accanto all'eseguibile, creata al
bisogno ed esclusa dal controllo di versione.

La scrittura è protetta: i dati vanno prima in un file temporaneo che sostituisce
il precedente solo a operazione completata. Se il programma venisse interrotto a
metà salvataggio, la partita precedente resterebbe integra.

---

## Cosa viene salvato

`StatoPartita` è una fotografia fatta di **soli testi, numeri e collezioni**:
nessun riferimento a oggetti del dominio.

```json
{
  "nomeEroe": "Aldo",
  "classeEroe": "Guerriero",
  "livello": 2,
  "esperienza": 50,
  "puntiVita": 24,
  "idStanzaCorrente": "corridoio",
  "inventario": { "Pozione minore": 2, "Spada corta": 1 },
  "idStanzeVisitate": ["ingresso", "corridoio"],
  "idStanzeCompletate": ["ingresso"],
  "armaEquipaggiata": "Spada corta"
}
```

### Perché si salvano i nomi e non gli oggetti interi

Degli oggetti raccolti si conserva **solo il nome**. La definizione completa vive
già nel catalogo dei contenuti, quindi:

- il file di salvataggio resta piccolo e leggibile a occhio;
- non serve gestire il salvataggio di gerarchie di classi diverse;
- **un ribilanciamento delle statistiche di un'arma si applica anche alle partite
  già salvate**, perché al caricamento l'oggetto viene ricostruito dal catalogo
  aggiornato.

### Come viene ripristinato il progresso

- `idStanzeVisitate` → le stanze tornano segnate come già viste;
- `idStanzeCompletate` → le stanze già ripulite restano prive di nemici e tesori,
  così non si affronta due volte lo stesso avversario.

### Limiti dichiarati

Il salvataggio **non comprende lo stato di un combattimento in corso**. Per
questo motivo il pulsante di salvataggio è disabilitato durante gli scontri,
con un suggerimento che ne spiega il motivo: è preferibile impedire l'operazione
piuttosto che permetterla e perdere silenziosamente dei dati.

---

## Il dominio non conosce il formato dei file

Nessuna classe di `model` o `service` importa Gson.

- Le classi `persistence.dto` fanno da cuscinetto: contengono i dati grezzi
  letti dal file, ancora privi di regole.
- Il caricatore li traduce in oggetti di dominio passando dai costruttori, che
  **validano**: una configurazione sbagliata fallisce subito, con un messaggio
  che indica il file e l'elemento incriminato, invece di produrre un gioco
  incoerente.
- Se il formato dei contenuti cambiasse, cambierebbero solo le classi `dto` e il
  caricatore.

Lo stesso vale per la destinazione: motore e interfaccia dipendono
dall'interfaccia `RepositoryPartita`. Passare a un database relazionale con JPA
significherebbe aggiungere una nuova implementazione di quel contratto e
cambiare **una riga** in `ApplicazioneRpg`.
