# Fjord Line – Booking Case

Dette er min løsning på casen for Fjord Line. Jeg har fokusert på å bygge en robust kjerne som håndterer den største utfordringen i oppgaven: **kapasitetsstyring over flere delstrekninger (multi-leg).**

## Prioriteringer og valg
I tråd med oppgaveteksten har jeg gjort noen bevisste valg underveis:

### Kjernelogikk først
Jeg har prioritert arbeidet i `BookingService` for å sikre at logikken for delstrekninger (*legs*) er helt presis. Dette er det kritiske punktet for å unngå overbooking på skipet.

### Enkelhet i lagring
Jeg valgte en *in-memory*-løsning med `ConcurrentHashMap`. Dette holder oppgaven fokusert på selve forretningslogikken fremfor databaseoppsett, men arkitekturen er klargjort slik at en database kan kobles på senere via repository-laget.

### Driftsklarhet
Jeg har lagt vekt på strukturert JSON-logging og containerisering. Dette viser hvordan koden er tenkt å fungere i et moderne produksjonsmiljø hvor overvåking og skalering er viktig.

### Kjøretøy som utvidelse
Jeg valgte å implementere logikk for kjøretøy for å vise hvordan modellen enkelt kan håndtere ulik type last med forskjellig plassbehov (en bil teller som 5 enheter).

---

## Funksjonalitet
* **Smart rutehåndtering:** Systemet forstår at en reise fra Bergen til Hirtshals består av to etapper. Kapasitet reserveres på begge.
* **Plassutnyttelse:** Passasjerer som går av underveis frigjør plass umiddelbart for nye reisende.
* **Kansellering:** Ved sletting av en booking frigjøres kapasiteten nøyaktig på de delstrekningene som ble reservert.
* **Manifest:** Henter ut en samlet passasjerliste for en spesifikk avgang.

## Teknisk oppsett
Løsningen kjører på **Spring Boot 3.2.5** og **Java 17**. Jeg har brukt **Lombok** for å holde modellene rene for boilerplate-kode. For å sikre trådsikkerhet uten en database-lås, brukes `synchronized` på kritiske metoder.

Loggene leveres i **JSON-format** via Logstash-encoderen, noe som gjør dem klare for verktøy som ELK-stack eller Splunk. Du finner også Docker-oppsett og Kubernetes-manifester i `k8s/` mappen.

---

## Hvordan teste løsningen
Den raskeste måten å verifisere API-et på er å bruke fila `test.http` som ligger i rotmappen. Den inneholder ferdige kall for booking og manifest.

### Bygging og kjøring
```bash
mvn clean package
java -jar target/fjordline-booking-0.0.1-SNAPSHOT.jar