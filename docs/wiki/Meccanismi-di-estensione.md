# Meccanismi messi a disposizione per integrare nuove funzionalità

La specifica chiede che sia chiaro **come** il progetto possa crescere, anche per
funzionalità non presenti nella prima versione. Questa pagina elenca i punti di
estensione previsti, dal più economico al più impegnativo.

---

## 1. Estensioni a costo zero: solo dati

Non richiedono di scrivere né ricompilare codice Java. Basta modificare
`src/main/resources/dungeon.json`.

| Cosa si vuole fare | Come |
|---|---|
| Aggiungere una stanza | Nuova voce in `stanze` e un collegamento da una stanza esistente |
| Ampliare o riprogettare il dungeon | Riscrivere l'elenco `stanze` |
| Aggiungere un'arma, un'armatura o una pozione | Nuova voce in `oggetti` |
| Aggiungere un tipo di nemico | Nuova voce in `nemici`, riutilizzando una strategia esistente |
| Ribilanciare danni, difese, ricompense | Cambiare i numeri nel file |
| Spostare l'uscita del dungeon | Impostare `uscitaDelDungeon` su un'altra stanza |

Volendo più campagne, è sufficiente affiancare altri file e passarne il nome al
costruttore di `CaricatoreDungeonJson`, che lo accetta già come parametro.

---

## 2. Estensioni per aggiunta di una classe

Si aggiunge una classe nuova; **nessuna classe esistente viene modificata**.
È l'applicazione diretta del principio aperto/chiuso.

### Una nuova abilità o mossa di combattimento

Implementare `Azione`, oppure estendere `AzioneAttacco` ridefinendo solo il
calcolo del danno e la frase di resoconto:

```java
public class ColpoParalizzante extends AzioneAttacco {
    @Override
    protected int calcolaDannoInflitto(Combattente attore, Combattente bersaglio) { ... }
}
```

Il motore di combattimento non cambia: lavora su `Azione`, non su classi
concrete.

### Un nuovo comportamento dei nemici

Implementare `StrategiaCombattimento` e assegnarla nel file dei contenuti,
aggiungendo il nome nel punto in cui il caricatore converte la stringa in
strategia:

```java
public class StrategiaCurativa implements StrategiaCombattimento {
    @Override
    public Azione scegliAzione(Combattente attore, Combattente bersaglio) { ... }
}
```

### Una nuova classe giocabile

Estendere `Eroe`, dichiarare l'abilità speciale e registrarla:

```java
public class Paladino extends Eroe {
    @Override public String getNomeClasse() { return "Paladino"; }
    @Override public Azione abilitaSpeciale() { return new ScudoSacro(getDado()); }
}

// in FabbricaEroi
registra("Paladino", Paladino::new);
```

La schermata iniziale popola l'elenco chiedendolo alla fabbrica: la nuova classe
compare da sola.

### Un nuovo tipo di oggetto

Estendere `Oggetto` e implementare `Equipaggiabile` o `Utilizzabile` a seconda
che sia permanente o consumabile. Grazie ai vincoli di tipo di
`AzioneUsaOggetto<T extends Oggetto & Utilizzabile>`, un nuovo consumabile è
utilizzabile in combattimento senza modifiche al motore.

---

## 3. Estensioni per sostituzione di un'implementazione

Ogni collaboratore esterno passa da un'interfaccia. Cambiare implementazione
richiede di modificare **una sola riga**, in `ApplicazioneRpg`.

| Contratto | Implementazione attuale | Alternative possibili |
|---|---|---|
| `GeneratoreMappa` | lettura da file JSON | generazione procedurale, lettura da XML validato con XSD, download da un servizio remoto |
| `RepositoryPartita` | file JSON su disco | database relazionale con JPA, salvataggi multipli con slot, archiviazione remota |
| `CatalogoOggetti` | catalogo dal file dei contenuti | catalogo da database |
| `Dado` | generatore pseudocasuale | dado con seme fisso per partite riproducibili, dado truccato per una modalità facilitata |

Esempio: passare i salvataggi su database significherebbe scrivere
`RepositoryPartitaJpa implements RepositoryPartita` e sostituire l'istanza
creata in `ApplicazioneRpg`. Motore, modello e interfaccia grafica resterebbero
invariati.

---

## 4. Estensione verso altri dispositivi

La specifica chiede esplicitamente che il progetto sia predisposto all'uso su
più dispositivi. Il meccanismo previsto è il **sistema di osservazione**.

`MotoreGioco` non conosce JavaFX: annuncia gli eventi a chiunque si sia
registrato tramite `OsservatoreGioco`, e riceve comandi attraverso metodi
pubblici (`muovi`, `eseguiAzioneCombattimento`).

Ne consegue che:

- **un'interfaccia testuale** si realizza scrivendo un osservatore che stampa a
  console e un ciclo che legge comandi da tastiera, senza toccare una riga del
  motore;
- **un'interfaccia web o mobile** userebbe lo stesso motore, con un osservatore
  che inoltra gli eventi al client;
- **più viste possono convivere**: gli osservatori registrati sono una lista, non
  uno solo. Si potrebbe aggiungere un osservatore che scrive un file di log della
  partita senza modificare nulla.

Il modello di dominio (`model`) e le regole (`service`) non contengono alcun
riferimento a classi grafiche: è il presupposto perché tutto questo sia
possibile.

---

## 5. Funzionalità non implementate e dove si innesterebbero

| Funzionalità | Punto di innesto |
|---|---|
| Scontri con più nemici insieme | `Combattimento` passerebbe da un avversario singolo a una lista; le azioni riceverebbero il bersaglio scelto |
| Effetti di stato (veleno, scudo) | Nuova astrazione `Effetto` applicata a inizio turno da `Combattimento` |
| Denaro e negozi | `Oggetto` espone già un `valore`; servirebbe un contatore di monete su `Eroe` e una nuova stanza-negozio |
| Salvataggi multipli | `RepositoryPartita` con un parametro identificativo dello slot |
| Dungeon generato proceduralmente | Nuova implementazione di `GeneratoreMappa` |
| Colonna sonora ed effetti | Nuovo osservatore che reagisce ai `TipoEvento` riproducendo suoni |
| Statistiche di fine partita | Nuovo osservatore che accumula i dati degli eventi |

Si noti come diverse voci si risolvano aggiungendo **un osservatore**: è il
punto di estensione meno invasivo del progetto, perché non richiede alcuna
modifica al codice esistente.
