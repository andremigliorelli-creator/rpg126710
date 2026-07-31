# Dichiarazione dettagliata sull'uso di strumenti di AI

> ⚠️ **Questa pagina è una base da completare e verificare prima della consegna.**
> La specifica richiede una dichiarazione *dettagliata* e *veritiera*. Le sezioni
> contrassegnate con **[DA COMPLETARE]** vanno riempite personalmente: nessuno
> può farlo al posto dello studente, perché descrivono il suo effettivo
> contributo e il suo effettivo livello di comprensione.

---

## Strumento utilizzato

| Voce | Dettaglio |
|---|---|
| Strumento | **Claude (Anthropic)**, tramite l'interfaccia **Claude Code** |
| Periodo di utilizzo | **[DA COMPLETARE — date delle sessioni di lavoro]** |
| Modalità | Sessione interattiva con lettura del materiale del corso e della specifica |

---

## Attività svolte con il supporto dell'AI

Per trasparenza, di seguito l'elenco puntuale di ciò che è stato prodotto con
l'assistenza dello strumento.

### 1. Analisi preliminare

- Lettura ed estrazione del testo delle slide del corso (31 file PDF) per
  ricavare l'elenco delle competenze richieste dal progetto.
- Lettura della specifica di progetto e individuazione dei requisiti vincolanti
  (package obbligatorio, esecuzione con due soli comandi Gradle, GUI,
  persistenza, README, Wiki).
- Consultazione del repository di riferimento del docente
  (`FabrizioFornari/MDP-MGC-2526`) per allinearsi alle convenzioni adottate a
  lezione: Kotlin DSL per Gradle, Java 25, struttura del README, `.gitignore`.

### 2. Progettazione

- Scelta del dominio applicativo (dungeon crawler a turni) fra alternative
  proposte e discusse.
- Definizione dell'architettura a package (`api`, `model`, `service`,
  `persistence`, `ui`, `util`).
- Individuazione delle responsabilità e definizione delle interfacce del dominio.

### 3. Implementazione

- Stesura del codice Java di tutte le classi del progetto.
- Configurazione di Gradle (`build.gradle.kts`, wrapper, dipendenze JavaFX, Gson,
  JUnit).
- Creazione del file dei contenuti `dungeon.json` e del foglio di stile
  `stile.css`.

### 4. Test

- Stesura della suite di 94 test JUnit.
- Correzione di un test che aveva evidenziato un fraintendimento sulla regola del
  colpo possente.

### 5. Documentazione

- Stesura del README e di tutte le pagine di questa Wiki.
- Redazione dei messaggi di commit.

---

## Livello di intervento personale

**[DA COMPLETARE]** — Questa è la sezione più importante per la valutazione.
Descrivere onestamente:

- Quali parti del codice sono state **lette e comprese** in dettaglio.
- Quali parti sono state **modificate, riscritte o rifiutate** personalmente, e
  perché.
- Quali **scelte di progettazione** sono state prese autonomamente e quali sono
  state accettate dallo strumento dopo averle valutate.
- Quali **funzionalità sono state aggiunte** in autonomia dopo la generazione
  iniziale.
- Come è stato **verificato** che il codice funzioni e sia corretto (esecuzione
  dei test, prove manuali, debug).
- Quali **difficoltà** si sono incontrate e come sono state risolte.

Esempio di come potrebbe essere formulata questa sezione:

> Ho fatto generare una prima versione dell'architettura, che ho poi studiato
> classe per classe. Ho riscritto personalmente `[...]` perché `[...]`. Ho
> aggiunto `[...]`. Ho verificato il comportamento eseguendo `[...]` e ho corretto
> `[...]`. Le parti che ho trovato più difficili da comprendere sono state
> `[...]`, che ho approfondito consultando `[...]`.

---

## Verifica della comprensione

**[DA COMPLETARE]** — Prima della consegna è opportuno essere in grado di
rispondere a domande di questo tipo, perché il progetto viene discusso:

- Perché `Personaggio` non ridefinisce `equals` mentre `Stanza` sì?
- Perché l'inventario usa una `Map` e non una `List`?
- Cosa cambierebbe togliendo l'interfaccia `Dado` e usando `Random` direttamente?
- Dove interverresti per aggiungere una quarta classe giocabile?
- Perché il file dei contenuti sta in `resources` e i salvataggi no?
- Quale principio SOLID è violato se `Combattimento` assegnasse l'esperienza?

Se una di queste risposte non è chiara, conviene studiare la porzione di codice
corrispondente prima della consegna.

---

## Nota di metodo

Lo strumento di AI è stato usato come **supporto alla stesura**, non come
sostituto della comprensione. Il valore del progetto, come ricordato più volte a
lezione, non sta nel gioco ma in come è progettato: perché la dichiarazione sia
credibile è necessario che il codice sia effettivamente compreso e che lo
studente sia in grado di estenderlo e discuterlo.
