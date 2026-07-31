# Funzionalità implementate

## Creazione del personaggio

All'avvio l'utente sceglie un nome e una fra tre classi giocabili, ciascuna con
statistiche di partenza e abilità speciale diverse.

| Classe | Attacco | Difesa | Punti vita | Abilità speciale |
|---|---|---|---|---|
| Guerriero | 6 | 4 | 30 | **Colpo possente** — raddoppia il danno, ma può mancare |
| Mago | 4 | 2 | 22 | **Palla di fuoco** — danno che ignora l'armatura |
| Ladro | 5 | 3 | 26 | **Colpo furtivo** — può diventare un colpo critico |

## Esplorazione del dungeon

- Il dungeon è composto da stanze collegate nelle quattro direzioni cardinali.
- Ogni stanza ha una descrizione, può ospitare nemici e custodire oggetti.
- I passaggi sono percorribili nei due sensi ed è sempre possibile tornare
  indietro.
- Le stanze già viste vengono ricordate come visitate.
- Raggiungere la stanza segnata come uscita conclude vittoriosamente la partita.

## Combattimento a turni

Entrare in una stanza presidiata avvia automaticamente lo scontro. In ogni turno
agisce prima l'eroe, poi il nemico.

Mosse disponibili per l'eroe:

- **Attacca** — colpo base; il danno è l'attacco effettivo più un lancio di
  dado, ridotto dalla difesa dell'avversario, con un minimo di 1 danno garantito.
- **Abilità speciale** — dipende dalla classe scelta.
- **Usa pozione** — compare una voce per ogni tipo di pozione posseduta.
- **Fuggi** — tentativo con esito incerto; se fallisce il turno è perso.

I nemici non decidono da soli: seguono una **strategia** ricevuta alla
creazione. Sono previste due strategie:

- **Aggressiva** — attacca sempre, non si ritira mai (scheletri, boss);
- **Cauta** — combatte finché è in forze, poi tenta la fuga (goblin).

Esiti possibili: vittoria, sconfitta, fuga dell'eroe, fuga del nemico.

## Progressione

- Sconfiggere un nemico assegna punti esperienza.
- Al raggiungimento della soglia l'eroe **sale di livello**: aumentano attacco,
  difesa e punti vita massimi, e la vita viene ripristinata.
- Una singola ricompensa può far guadagnare più livelli in una volta.

## Oggetti ed equipaggiamento

- **Armi** aumentano l'attacco, **armature** la difesa, **pozioni** curano.
- Gli oggetti trovati in una stanza vengono raccolti automaticamente.
- Un ritrovamento **migliore** di quello in uso viene equipaggiato subito, per
  non costringere l'utente a ricordarsene.
- L'inventario accorpa le copie identiche dello stesso oggetto.

## Persistenza

- **Salvataggio della partita** su file JSON: eroe, livello, esperienza, punti
  vita, inventario, equipaggiamento, posizione e progressi nel dungeon.
- **Ripresa della partita** dalla schermata iniziale.
- I contenuti del gioco (stanze, nemici, oggetti) sono anch'essi definiti in un
  file JSON, letto all'avvio di ogni partita.

## Interfaccia grafica

Realizzata con JavaFX e organizzata secondo lo schema Modello-Vista-Controllo:

- descrizione della stanza corrente in alto;
- pannello dell'eroe a sinistra: vita, statistiche, equipaggiamento, zaino;
- pannello del nemico a destra durante gli scontri;
- **diario della spedizione** al centro, con righe colorate per tipo di evento;
- comandi in basso, che cambiano automaticamente fra esplorazione,
  combattimento e fine partita;
- aspetto grafico definito in un foglio di stile CSS esterno.

---

## Cosa è stato volutamente lasciato fuori

La specifica chiede un sistema ben progettato e chiarisce che non è necessario
un gioco completo. Sono quindi stati **esclusi consapevolmente**:

- combattimenti con più nemici contemporaneamente (una stanza può contenerne
  più d'uno, ma vengono affrontati in sequenza);
- gestione del denaro, negozi e scambi;
- effetti di stato prolungati come veleno o benedizioni;
- generazione procedurale del dungeon;
- grafica con immagini o animazioni;
- modalità multigiocatore.

Per ciascuna di queste voci la pagina
[Meccanismi di estensione](Meccanismi-di-estensione) indica dove interverrebbe
un'eventuale implementazione futura.
