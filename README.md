# 🗡️ Le Cripte di Camerino

Gioco di ruolo con interfaccia grafica sviluppato in Java per il corso di
**Metodologie di Programmazione / Modellazione e Gestione della Conoscenza**
(A.A. 2025/2026, Università degli Studi di Camerino).

Il giocatore crea un eroe, esplora le stanze di un dungeon, affronta i nemici in
combattimenti a turni, raccoglie equipaggiamento e cerca di raggiungere l'uscita.
La partita può essere salvata e ripresa in un secondo momento.

---

## 🚀 Come eseguire il progetto

### Prerequisiti

- **Java 25 (LTS)**
- Nessun'altra installazione richiesta: Gradle viene scaricato automaticamente
  dal wrapper incluso nel repository.

### Istruzioni

```bash
git clone <url-del-repository>
cd ProgettoRPG
```

### Build del progetto

```bash
./gradlew build
```

### Esecuzione

```bash
./gradlew run
```

Su Windows, se si usa il Prompt dei comandi o PowerShell, sostituire `./gradlew`
con `gradlew.bat`.

### Test

```bash
./gradlew test
```

Il report leggibile viene generato in `build/reports/tests/test/index.html`.

---

## 🎮 Come si gioca

1. Nella schermata iniziale scegli un **nome** e una **classe** fra Guerriero,
   Mago e Ladro; oppure riprendi la partita salvata, se ne esiste una.
2. Durante l'**esplorazione** usa i pulsanti di direzione per spostarti fra le
   stanze. Gli oggetti trovati vengono raccolti automaticamente e, se migliori
   di quelli in uso, vengono equipaggiati.
3. Entrando in una stanza presidiata inizia un **combattimento a turni**: puoi
   attaccare, usare l'abilità della tua classe, bere una pozione o tentare la
   fuga.
4. Il **diario** al centro racconta tutto ciò che accade; i pannelli laterali
   mostrano lo stato dell'eroe e del nemico.
5. Raggiungi l'**uscita del dungeon** per vincere.

Il salvataggio è consentito solo durante l'esplorazione: la fotografia della
partita non comprende lo stato di uno scontro in corso.

---

## 🧱 Struttura del progetto

```
src/main/java/it/unicam/cs/mpgc/rpg000000/
├── api/            contratti (interfacce) su cui poggia tutto il sistema
├── model/          concetti del dominio
│   ├── personaggio/  eroi, nemici, statistiche
│   ├── oggetto/      armi, armature, pozioni, inventario
│   ├── mondo/        stanze, direzioni, mappa
│   └── combattimento/ azioni, strategie, motore a turni
├── service/        regia della partita, eventi, salvataggio/ripristino
├── persistence/    lettura dei contenuti e archiviazione delle partite
├── ui/             interfaccia grafica JavaFX
├── util/           utilità di supporto
└── Main.java       punto di ingresso

src/main/resources/
├── dungeon.json    contenuti del gioco: stanze, nemici, oggetti
└── stile.css       aspetto grafico dell'interfaccia
```

I contenuti del dungeon vivono in `dungeon.json`: **aggiungere stanze, nemici o
oggetti non richiede di ricompilare né di modificare il codice.**

La documentazione completa (funzionalità, responsabilità, classi e interfacce,
organizzazione dei dati, meccanismi di estensione) si trova nella
**[Wiki del repository](../../wiki)**.

---

## 🤖 Uso di strumenti di AI

Nella realizzazione di questo progetto sono stati utilizzati strumenti di
intelligenza artificiale.

- **Strumento utilizzato:** Claude (Anthropic), tramite Claude Code.
- **Attività svolte con il supporto dell'AI:**
  - analisi del materiale del corso e della specifica di progetto;
  - progettazione dell'architettura a package e delle interfacce del dominio;
  - stesura del codice Java, dei test JUnit e della documentazione;
  - configurazione di Gradle e organizzazione della cronologia Git.
- **Livello di intervento personale:** da compilare a cura dello studente.

> ⚠️ **Questa sezione va completata e adattata prima della consegna.**
> La dichiarazione deve descrivere in modo veritiero *quali parti* sono state
> prodotte con l'AI, *quali* sono state scritte, modificate o riviste
> personalmente e *come* il codice è stato compreso e verificato. Una
> descrizione dettagliata è richiesta anche nella pagina dedicata della Wiki.

---

## 📄 Licenza

Progetto realizzato a scopo didattico.
