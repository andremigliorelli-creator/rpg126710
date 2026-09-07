# 🗡️ Le Cripte di Camerino — RPG a combattimento a turni

Progetto d'esame per il corso di **Metodologie di Programmazione / Modellazione
e Gestione della Conoscenza** (A.A. 2025/2026, Università degli Studi di
Camerino).

Un piccolo gioco di ruolo: si esplora un dungeon stanza per stanza, si
affrontano i nemici che lo sorvegliano in combattimenti a turni, si raccoglie
equipaggiamento e si sale di livello. La partita si vince raggiungendo
l'uscita del dungeon, si perde cadendo in battaglia.

Il gioco è il contesto, non il fine: l'obiettivo del progetto è un sistema
software modulare, estendibile e testato, non un RPG ricco di funzionalità.

**Autore:** Andrea Migliorelli — matricola 126710
**Package radice:** `it.unicam.cs.mpgc.rpg126710`
**Documentazione estesa:** vedi la [Wiki del repository](../../wiki)

---

## Requisiti

- **Java 25 (LTS)** — il progetto dichiara una toolchain Java 25; se non è già
  installata, Gradle la scarica da solo
- Nessuna installazione di Gradle: si usa il wrapper incluso (`gradlew`)
- Nessuna installazione di JavaFX: la scarica il plugin
  `org.openjfx.javafxplugin`

## Come si esegue

Dalla cartella del progetto:

```bash
./gradlew run      # avvia il gioco (finestra JavaFX)
./gradlew build    # compila ed esegue tutti i test
./gradlew test     # solo i test
```

Su Windows, da PowerShell o dal Prompt dei comandi, i comandi sono
`.\gradlew.bat run` e così via.

Il report leggibile dei test viene generato in
`build/reports/tests/test/index.html`.

## Come si gioca

1. Nella schermata iniziale si sceglie un **nome** e una **classe** fra
   Guerriero, Mago e Ladro — oppure si riprende la partita salvata, se ne
   esiste una.
2. Ci si sposta fra le stanze con i pulsanti **Vai a Nord / Sud / Est /
   Ovest**: sono attivi solo dove esiste davvero un'uscita. Gli oggetti
   trovati vengono raccolti automaticamente e, se migliori di quelli in uso,
   equipaggiati.
3. Entrando in una stanza presidiata inizia subito un **combattimento a
   turni**: si sceglie un'azione per volta fra Attacca, l'abilità speciale
   della propria classe (colpo possente per il Guerriero, palla di fuoco per
   il Mago, colpo furtivo per il Ladro), una pozione dall'inventario, oppure
   la fuga. Il diario al centro racconta ogni colpo.
4. Vinto lo scontro, l'eroe incassa l'esperienza e la stanza resta libera.
5. Con **Salva partita** la partita si mette da parte e si riprende in
   qualsiasi momento; il salvataggio è consentito solo durante l'esplorazione,
   non a scontro in corso.
6. La partita finisce quando l'eroe raggiunge l'**uscita del dungeon**
   (vittoria) o quando cade in battaglia (sconfitta): in entrambi i casi si
   arriva alla schermata di fine partita.

## Struttura del progetto

Tutto il codice sta sotto `it.unicam.cs.mpgc.rpg126710`, diviso per
responsabilità:

| Package | Responsabilità |
|---|---|
| `api` | i contratti (interfacce) su cui poggia tutto il sistema: `Combattente`, `Azione`, `Dado`, `RepositoryPartita`, `GeneratoreMappa`, `CatalogoOggetti`, ... |
| `model.personaggio` | chi combatte: `Personaggio`, `Statistiche`, `Bonus`, l'astratto `Eroe` con le sue tre classi (`Guerriero`, `Mago`, `Ladro`) |
| `model.oggetto` | cosa si porta con sé: `Oggetto`, `Arma`, `Armatura`, `Pozione`, `Inventario` |
| `model.mondo` | dove si combatte: `Stanza`, `Direzione`, `Mappa` (il grafo del dungeon) |
| `model.combattimento` | come si combatte: `Combattimento` (le regole del turno) e le azioni (`AzioneAttacco`, `ColpoPossente`, `AzioneUsaOggetto`, ...) |
| `service` | la regia della partita: `MotoreGioco`, `GestorePartita`, `FabbricaEroi`, `EventoGioco`, `StatoPartita` |
| `persistence` | lettura dei contenuti del dungeon (`CaricatoreDungeonJson`) e archiviazione delle partite (`RepositoryPartitaJson`) |
| `ui` | l'interfaccia grafica JavaFX: `SchermataIniziale`, `SchermataGioco`, il foglio di stile, e il contratto `AvvioPartita` con cui le schermate comunicano senza conoscersi |
| `util` | utilità di supporto (il generatore di numeri casuali) |
| `Main` | punto di ingresso |

Le dipendenze vanno in una direzione sola: `ui` e `persistence` conoscono il
`service`, il `service` conosce il `model`, il `model` non conosce nessuno
degli altri due. Il dominio del gioco può quindi essere riusato con
un'interfaccia diversa (console, web) senza modifiche.

## Persistenza

I contenuti del dungeon (stanze, nemici, oggetti disponibili) vivono in
`dungeon.json`, fra le risorse: **aggiungere una stanza, un nemico o un
oggetto non richiede di ricompilare né di modificare il codice.**

Il salvataggio della partita è invece un file JSON scritto fuori dal
pacchetto eseguibile, in `salvataggi/partita.json`, dietro il contratto
`RepositoryPartita` (`salva` / `carica`): il resto del programma non sa in che
formato si stia scrivendo, quindi un'implementazione su database prenderebbe
il posto di quella JSON senza toccare nient'altro. La scrittura passa da un
file temporaneo che sostituisce il precedente solo a operazione completata,
così un salvataggio interrotto a metà non danneggia quello buono.

## Test

Il progetto ha 94 test JUnit 5, eseguiti da `./gradlew build`. Coprono il
dominio (statistiche, combattimento, progressione dell'eroe, inventario,
mondo), il motore di gioco (`MotoreGioco`, `GestorePartita`) e la persistenza
(andata e ritorno su file temporaneo).

## Stato del progetto

Il gioco è completo e giocabile dall'inizio alla fine: esplorazione del
dungeon, combattimento a turni con abilità di classe e pozioni, progressione
dell'eroe, salvataggio e caricamento su file, vittoria e sconfitta con la
relativa schermata di fine partita.

---

## 🤖 Uso di strumenti di AI

Per la realizzazione di questo progetto ho utilizzato un assistente basato su
intelligenza artificiale (**Claude, tramite Claude Code**) come strumento di
supporto durante lo sviluppo.

L'IA è stata utilizzata principalmente per:

- discutere l'organizzazione dei package e le responsabilità delle classi;
- scrivere una prima stesura del codice Java, dei test JUnit e della
  documentazione, poi rivista;
- realizzare le rifiniture dell'interfaccia (foglio di stile, colori);


## 📄 Licenza

Progetto realizzato a scopo didattico. Sviluppato da Migliorelli Andrea, email personale andrea.migliorelli12@gmail.com .
