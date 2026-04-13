# Fjord Line – Booking Case

Dette er mitt forslag til løsning på casen for Fjord Line. Jeg valgte ganske tidlig å fokusere på det som virket mest krevende i oppgaven, nemlig håndtering av kapasitet på tvers av flere delstrekninger (multi-leg). Resten av løsningen er i stor grad bygget rundt det.

## Tanker rundt valgene som er gjort

Jeg startet med å sette opp en ganske standard lagdelt struktur med Controller, Service, Repository og Model. Det gjorde det lettere å holde ting ryddig underveis, og spesielt å isolere logikken rundt booking.

Etter det gikk mesteparten av tiden til `BookingService`. Målet der var egentlig bare å få kontroll på hvordan kapasitet reserveres per delstrekning. Det er fort gjort å bomme her og ende opp med overbooking hvis man ikke håndterer alle legs riktig.

For lagring gikk jeg for en enkel in-memory løsning med `ConcurrentHashMap`. Det føltes mer relevant i denne casen enn å bruke tid på database. Samtidig er det lagt opp slik at det ikke burde være noe problem å koble på et repository mot database senere.

Jeg tok også med litt rundt drift. Loggingen er i JSON-format, og løsningen kan kjøres i container. Det er ikke veldig avansert, men tanken var å vise hvordan dette kunne fungert i et mer realistisk oppsett.

## Hva løsningen faktisk gjør

Systemet håndterer ruter med flere etapper. Hvis noen booker fra Bergen til Hirtshals, blir kapasitet reservert på alle delstrekningene som inngår i reisen.

Hvis passasjerer går av underveis, blir plassen tilgjengelig igjen med en gang. Det samme gjelder hvis en booking blir kansellert.

Jeg har også lagt inn enkel støtte for kjøretøy. En bil teller for eksempel som flere enheter, slik at det påvirker kapasiteten på en litt mer realistisk måte.

Det er også mulig å hente ut et manifest per avgang, altså en liste over hvem som er med.

## Teknisk oppsett og arkitektur

Løsningen er laget med Spring Boot 3.2.5 og Java 17.

Strukturen følger vanlig oppdeling med controller, service, repository og model. Jeg bruker en egen `BookingRequest` som DTO for input, slik at API-kontrakten er skilt fra domenemodellen.

Lombok er brukt for å slippe en del boilerplate.

Siden alt går i minne, har jeg synkronisert noen av metodene i servicen for å unngå problemer med samtidige bookinger.

Loggingen er satt opp i JSON-format via Logstash encoder, så det kan enkelt kobles mot verktøy som ELK eller Splunk.

## Hvordan teste

Den enkleste måten å teste på er å bruke `test.http`-fila i rotmappen. Den inneholder ferdige kall som kan kjøres direkte fra IntelliJ eller VS Code.

## Bygg og kjør

```bash
mvn clean package
java -jar target/fjordline-booking-0.0.1-SNAPSHOT.jar