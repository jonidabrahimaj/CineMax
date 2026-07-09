# CineMax — Laboratorio Interdisciplinare A

CineMax è un'applicazione a riga di comando (TUI) sviluppata in Java per la gestione di un piccolo cinema monosala da 200 posti. Permette ai proiezionisti di gestire il palinsesto, ai clienti di cercare proiezioni e prenotare posti, e ai bigliettai di consultare le prenotazioni. I dati di proiezioni, utenti e prenotazioni sono salvati su file in formato CSV.

## Requisiti
- Java JDK 17 o superiore (sviluppato e testato concettualmente su JDK 21)

## Struttura del progetto
```
CineMax/
├── src/cinemax/
│   ├── CineMax.java              (classe principale con il metodo main)
│   ├── model/                    (Film, Proiezione, Utente, Ruolo, Prenotazione)
│   ├── persistence/              (GestoreProiezioni, GestoreUtenti, GestorePrenotazioni)
│   ├── ui/                       (menu a terminale e visualizzazione)
│   └── util/                     (CsvUtil, PasswordUtil, InputUtil)
├── data/
│   ├── proiezioni.csv            (palinsesto di esempio)
│   ├── utenti.csv                (2 proiezionisti + 5 bigliettai + 1 cliente)
│   └── prenotazioni.csv          (prenotazione di esempio)
├── build.sh                      (compilazione su Linux/macOS)
├── build.bat                     (compilazione su Windows)
└── README.md
```

## Compilazione ed esecuzione

### Linux / macOS
```bash
chmod +x build.sh
./build.sh
java -jar CineMax.jar
```

### Windows
```bat
build.bat
java -jar CineMax.jar
```

### In alternativa (manuale, senza script)
```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
jar cfe CineMax.jar cinemax.CineMax -C bin .
java -jar CineMax.jar
```

> La cartella `data/` deve trovarsi nella stessa posizione da cui si lancia
> il `.jar` (i percorsi dei file sono relativi: `data/...`).

## Credenziali di prova
| Ruolo          | Username        | Password  |
|----------------|-----------------|-----------|
| Proiezionista  | proiezionista1  | proj123   |
| Proiezionista  | proiezionista2  | proj456   |
| Bigliettaio    | bigliettaio1    | big111    |
| Bigliettaio    | bigliettaio2    | big222    |
| Bigliettaio    | bigliettaio3    | big333    |
| Bigliettaio    | bigliettaio4    | big444    |
| Bigliettaio    | bigliettaio5    | big555    |
| Cliente        | mrossi          | cliente1  |

Le password sono salvate sul file **solo come hash SHA-256**, mai in chiaro.

## Note importanti
- **Intestazioni dei file `.java`**: ogni file ha in cima un blocco con
  `<Nome Cognome> - Matricola <numero> - Sede <VA/CO>`. Vanno compilati con i
  dati reali del gruppo (requisito obbligatorio della consegna).
- **Eliminazione prenotazione (cliente)**: implementata secondo il testo
  letterale delle specifiche, ossia consentita solo se la data della proiezione
  è *precedente* a quella odierna. Se l'intenzione del docente fosse l'opposto
  (consentirla solo per proiezioni future), basta invertire il controllo in
  `MenuCliente.eliminaPrenotazione()`.
- Il file `proiezioni.csv` qui incluso è un esempio: può essere sostituito con
  quello fornito dal docente, adattando eventualmente l'ordine delle colonne nel
  metodo di lettura di `GestoreProiezioni`.
```
```
