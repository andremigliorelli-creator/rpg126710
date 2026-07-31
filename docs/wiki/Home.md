# Le Cripte di Camerino — Documentazione

Progetto d'esame per **Metodologie di Programmazione / Modellazione e Gestione
della Conoscenza**, A.A. 2025/2026.

Applicativo Java che implementa un gioco di ruolo di tipo *dungeon crawler* a
turni, con interfaccia grafica JavaFX e persistenza dei dati in formato JSON.

---

## Indice della Wiki

| Pagina | Contenuto |
|---|---|
| [Funzionalità implementate](Funzionalita-implementate) | Cosa fa l'applicazione e cosa è stato volutamente lasciato fuori |
| [Responsabilità individuate](Responsabilita-individuate) | Come è stato suddiviso il problema prima di scrivere codice |
| [Classi e interfacce](Classi-e-interfacce) | Elenco dei tipi sviluppati con la responsabilità di ciascuno |
| [Organizzazione dei dati e persistenza](Organizzazione-dati-e-persistenza) | Strutture dati scelte e come vengono conservati i dati |
| [Meccanismi di estensione](Meccanismi-di-estensione) | Come aggiungere funzionalità senza modificare il codice esistente |
| [Principi SOLID e Clean Code](Principi-SOLID-e-Clean-Code) | Dove e come sono stati applicati |
| [Dichiarazione uso di AI](Dichiarazione-uso-di-AI) | Strumenti usati, per cosa e con quale intervento personale |

---

## Idea del progetto

Il gioco è il **contesto**, non il fine: l'obiettivo del progetto è mostrare un
sistema software ben progettato, modulare, estendibile e manutenibile.

Il dominio scelto — un dungeon esplorabile con combattimenti a turni — è
abbastanza ricco da richiedere ereditarietà, interfacce, classi astratte,
polimorfismo, strutture dati diverse fra loro e un livello di persistenza, ma
abbastanza contenuto da restare leggibile.

## Colpo d'occhio sull'architettura

```
            ┌──────────────────────────────┐
            │            ui                │  JavaFX: mostra e raccoglie comandi
            └──────────────┬───────────────┘
                           │ osserva
            ┌──────────────▼───────────────┐
            │          service             │  regole della partita, eventi
            └──────┬────────────────┬──────┘
                   │                │
      ┌────────────▼─────┐   ┌──────▼──────────┐
      │      model       │   │   persistence   │  file JSON: contenuti e salvataggi
      └────────────┬─────┘   └──────┬──────────┘
                   │                │
            ┌──────▼────────────────▼──────┐
            │            api               │  contratti condivisi
            └──────────────────────────────┘
```

Le frecce indicano la direzione delle dipendenze. Nessuno strato conosce quello
sopra di sé: il modello non sa che esiste un'interfaccia grafica, il motore non
sa dove finiscono i salvataggi. Tutto passa dalle astrazioni del package `api`.
