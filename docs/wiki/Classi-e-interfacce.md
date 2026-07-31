# Classi e interfacce sviluppate

Elenco completo dei tipi del progetto con la responsabilità di ciascuno.

---

## Package `api` — i contratti

Interfacce piccole e mirate. Nessuna classe è costretta a implementare metodi che
non le servono.

| Tipo | Responsabilità |
|---|---|
| `Combattente` | Descrive chi può stare in un combattimento: nome, punti vita, statistiche, capacità di subire danno, curarsi e produrre danno |
| `Azione` | Una mossa eseguibile in un turno; è il punto di estensione del combattimento |
| `EsitoAzione` | Risultato immutabile di un'azione: descrizione, variazione di punti vita, eventuale interruzione dello scontro |
| `StrategiaCombattimento` | Criterio con cui un personaggio non controllato dall'utente sceglie la mossa |
| `Equipaggiabile` | Oggetto che, indossato, modifica le statistiche |
| `Utilizzabile` | Oggetto che produce un effetto immediato e si consuma |
| `Dado` | Sorgente di casualità; astrarla rende il combattimento verificabile nei test |
| `GeneratoreMappa` | Sorgente da cui nasce il dungeon |
| `CatalogoOggetti` | Elenco degli oggetti esistenti, usato per ricostruire un inventario salvato |
| `RepositoryPartita` | Archivio in cui conservare e da cui recuperare una partita |
| `OsservatoreGioco` | Componente che vuole essere avvisato di ciò che accade nella partita |

---

## Package `model.personaggio`

| Tipo | Responsabilità |
|---|---|
| `Statistiche` | Value object immutabile: attacco, difesa, punti vita massimi. Valida i valori e ridefinisce `equals`/`hashCode` |
| `Bonus` | Value object immutabile: contributo di attacco e difesa dato da un oggetto equipaggiato |
| `Personaggio` *(astratta)* | Base comune a eroi e nemici: nome, statistiche, punti vita, calcolo del danno. Astratta perché non esiste un personaggio generico |
| `Eroe` *(astratta)* | Personaggio dell'utente: inventario, equipaggiamento, livelli. Dichiara astratta l'abilità speciale, perché ogni classe combatte diversamente |
| `Guerriero` | Eroe robusto; abilità: colpo possente |
| `Mago` | Eroe fragile ma potente; abilità: palla di fuoco |
| `Ladro` | Eroe agile; abilità: colpo furtivo |
| `Nemico` | Creatura ostile; riceve dall'esterno strategia, esperienza concessa ed eventuale bottino |

> **Nota di progettazione.** `Personaggio` **non** ridefinisce `equals`: due
> goblin con lo stesso nome sono creature distinte. L'uguaglianza è stata
> implementata solo dove esiste un'identità chiara e stabile.

---

## Package `model.oggetto`

| Tipo | Responsabilità |
|---|---|
| `Oggetto` *(astratta)* | Radice degli oggetti raccoglibili: nome, descrizione, valore. Ridefinisce `equals`/`hashCode` sul nome |
| `CategoriaOggetto` *(enum)* | Arma, armatura o pozione; serve a raggruppare e a ricostruire da salvataggio |
| `Arma` | Oggetto equipaggiabile che aumenta l'attacco |
| `Armatura` | Oggetto equipaggiabile che aumenta la difesa |
| `Pozione` | Oggetto utilizzabile che ripristina punti vita |
| `Inventario` | Oggetti posseduti con le relative quantità; ricerca in tempo costante |
| `Equipaggiamento` | Cosa è indossato e quale bonus complessivo ne deriva |

---

## Package `model.mondo`

| Tipo | Responsabilità |
|---|---|
| `Direzione` *(enum)* | Le quattro direzioni cardinali e il calcolo della direzione opposta |
| `Stanza` | Un ambiente: descrizione, uscite, nemici, tesori, stato di visita. Identità sull'id |
| `Mappa` | Insieme delle stanze indicizzate per id; verifica la coerenza dei collegamenti alla costruzione |

---

## Package `model.combattimento`

| Tipo | Responsabilità |
|---|---|
| `AzioneAttacco` | Colpo base; espone punti di estensione protetti per il calcolo del danno e il resoconto |
| `ColpoPossente` | Abilità del Guerriero: danno raddoppiato con prova di riuscita |
| `PallaDiFuoco` | Abilità del Mago: danno che ignora la difesa |
| `ColpoFurtivo` | Abilità del Ladro: possibilità di colpo critico |
| `AzioneFuga` | Tentativo di abbandonare lo scontro |
| `AzioneUsaOggetto<T>` | Consuma un oggetto dell'inventario applicandone l'effetto; il tipo è vincolato a essere insieme `Oggetto` e `Utilizzabile`, così il compilatore garantisce la coerenza senza cast |
| `StrategiaAggressiva` | Attacca sempre |
| `StrategiaCauta` | Attacca finché è in forze, poi tenta la fuga |
| `Combattimento` | Fa rispettare le regole del turno e stabilisce quando lo scontro finisce |
| `EsitoCombattimento` *(enum)* | Stato dello scontro: in corso, vittoria, sconfitta, fuga dell'eroe, fuga del nemico |

---

## Package `service`

| Tipo | Responsabilità |
|---|---|
| `MotoreGioco` | Regia della partita: movimento, avvio degli scontri, ricompense, raccolta dei tesori, condizioni di vittoria e sconfitta. Annuncia gli eventi agli osservatori |
| `StatoGioco` *(enum)* | Fase corrente: esplorazione, combattimento, vittoria, sconfitta |
| `EventoGioco` | Notizia immutabile di qualcosa accaduto: tipo e messaggio |
| `TipoEvento` *(enum)* | Categorie di evento, usate dalla vista per differenziare la presentazione |
| `StatoPartita` | Fotografia della partita fatta di soli testi e numeri, pronta per essere scritta su disco |
| `GestorePartita` | Coordina avvio, salvataggio e ripristino, ricevendo tutti i collaboratori come astrazioni |
| `FabbricaEroi` | Crea un eroe dal nome della sua classe; le classi disponibili sono in un registro, non in una catena di `if` |

---

## Package `persistence`

| Tipo | Responsabilità |
|---|---|
| `CaricatoreDungeonJson` | Costruisce mappa e catalogo leggendo `dungeon.json`; implementa sia `GeneratoreMappa` sia `CatalogoOggetti` |
| `RepositoryPartitaJson` | Scrive e rilegge il salvataggio su file, con scrittura sicura tramite file temporaneo |
| `dto.DatiDungeon` | Contenuto grezzo del file di configurazione |
| `dto.DatiStanza` | Stanza come letta dal file |
| `dto.DatiNemico` | Modello di creatura come letto dal file |
| `dto.DatiOggetto` | Oggetto come letto dal file |

> **Nota di progettazione.** Le classi `dto` esistono per tenere la libreria
> Gson fuori dal modello di dominio: lì i dati sono ancora testo e numeri senza
> regole, mentre le classi del dominio nascono già valide grazie ai controlli nei
> loro costruttori.

---

## Package `ui`

| Tipo | Responsabilità |
|---|---|
| `ApplicazioneRpg` | Costruisce le implementazioni concrete e alterna le schermate; unico punto in cui si sceglie *quale* implementazione usare |
| `SchermataIniziale` | Raccoglie nome e classe, avvia una partita nuova o ripresa |
| `SchermataGioco` | Mostra stanza, eroe, nemico, diario e comandi. Si registra come osservatore del motore |
| `AvvioPartita` | Richiamo con cui la schermata iniziale segnala che la partita è pronta, senza conoscere chi la riceve |

---

## Package `util`

| Tipo | Responsabilità |
|---|---|
| `DadoCasuale` | Implementazione di `Dado` basata sul generatore pseudocasuale standard; accetta un seme per rendere i lanci riproducibili |

## Sorgenti di test

| Tipo | Responsabilità |
|---|---|
| `DadoFisso` | Dado di prova che restituisce sempre lo stesso valore, per rendere ripetibili le asserzioni sui combattimenti |
