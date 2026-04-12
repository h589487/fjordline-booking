# Fjord Line – Booking Case

Dette er mitt forslag til løsning på casen for Fjord Line. Jeg valgte ganske tidlig å fokusere på det som virket mest krevende i oppgaven, nemlig håndtering av kapasitet på tvers av flere delstrekninger (multi-leg). Resten av løsningene bygger i stor grad rundt dette.

## Tanker rundt valgene som er gjort

Jeg startet med kjernelogikken i `BookingService`. Poenget var å få kontroll på hvordan kapasitet faktisk reserveres per delstrekning, siden det er her det fort kan gå galt (for eksempel overbooking). Når den biten først satt, ble resten av systemet enklere å bygge rundt.

For lagring gikk jeg for en enkel *in-memory*-tilnærming med `ConcurrentHashMap`. Det holder fokus på logikken i casen i stedet for databaseoppsett. Samtidig er strukturen lagt opp slik at det ikke er noe problem å bytte til en database senere via et repository-lag.

Jeg har også tatt med litt rundt drift og hvordan dette kunne kjørt i praksis. Derfor er loggingen strukturert som JSON, og løsningen kan kjøres i container. Tanken er å vise hvordan dette kunne fungert i et miljø hvor man faktisk overvåker og skalerer tjenesten.

Når det gjelder kjøretøy, la jeg det inn mest for å vise at modellen tåler ulike typer last. En bil teller for eksempel som flere enheter, og det håndteres på samme måte som passasjerer i kapasitetsberegningen.

## Hva løsningen faktisk gjør

Systemet håndterer ruter som består av flere etapper. For eksempel vil en reise fra Bergen til Hirtshals automatisk reservere kapasitet på begge delstrekningene.

Hvis noen går av underveis, blir plassen tilgjengelig igjen med en gang, slik at den kan brukes av andre passasjerer senere på ruten. Tilsvarende vil en kansellering frigjøre kapasitet akkurat der den ble brukt.

Det er også mulig å hente ut et manifest for en avgang, altså en samlet oversikt over passasjerer.

## Teknisk oppsett

Løsningen er laget med Spring Boot 3.2.5 og Java 17. Jeg har brukt Lombok for å slippe mye boilerplate i modellene.

For å unngå trøbbel med samtidighet uten database, er kritiske metoder synkronisert. Det er en enkel løsning, men fungerer fint i denne sammenhengen.

Loggingen er på JSON-format (via Logstash encoder), så den kan enkelt plugges inn i verktøy som ELK eller Splunk. Det ligger også Docker-oppsett og Kubernetes-manifester i `k8s/`-mappen.

## Hvordan teste

Den enkleste måten å teste på er å bruke `test.http`-fila i rotmappen. Den inneholder ferdige kall du kan kjøre direkte mot API-et.

## Bygg og kjør

```bash
mvn clean package
java -jar target/fjordline-booking-0.0.1-SNAPSHOT.jar