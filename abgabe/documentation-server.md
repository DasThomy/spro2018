# [Laufende Live Instanz aus dem Uni Netzwerk](http://134.245.1.240:9001/swarm-composer-0.0.1-SNAPSHOT/)

# Starten vom Sourcecode
- In dem `webapp` Ordner auf der Kommandozeile
`mvn spring-boot:run` eingeben.
- Es wird eine Datenbank unter `~/sopro-spring-project-database` erstellt.
- Die Webseite steht dann gesichert unter [`https://localhost:8443`](https://localhost:8443) zur Verfügung (ggf. muss ein Zertifikat akzeptiert werden).

# Deployment auf einem Tomcat Server
- Commit `22241ac2fe732c202349bd959b1c95121a35077c` (Git-Tag: 1.0-release-server), wenn es als zu spät angesehen wird, dann der commit `3228ad1b40e6445b07e72444e1494ddf180be243`
- Tomcat Server Webseite öffnen.
- Auf Manager App klicken.
- `Select WAR file to upload` auswählen und die im Abgabeorder mitgelieferte Datei
`swarm-composer-0.0.1-SNAPSHOT.war` hochladen.
- Auf `Depoly` klicken.


## User-/Testdaten
- Es werden per default alle von Adesso gestellten Produkte aus der JSON Datei ohne Bilder initalisiert.

- Es werden automatisch 20 Benutzer erstellt, die die Form: User: `maxZ@web.de` und
Passwort: `passZ` haben, wobei Z € [0:19] ist.

- `max5@web.de` mit `pass5` ist ein Administrator.

- Es werden Kombinationen zufällig generiert.

- Bei einem Neustart der Anwendung werden alle Daten zurückgesetzt.

## Verwendete Technologien
- [Spring Boot](https://projects.spring.io/spring-boot)
- [H2 Database Engine](http://www.h2database.com/html/main.html)
- [Thymeleaf](https://www.thymeleaf.org)
- [Bootstrap](https://getbootstrap.com)
- [Fabric.js](http://fabricjs.com)

## Weitere Informationen
- Tests sind unter `src/test/java/de/sopro/` zu finden.
- Frontendsachen sind unter `src/main/resources` zu finden
- Der Server Quellcode ist unter `src/main/java/de/sopro` zu finden.

### Packages
- Im Package `de.sopro.controller` sind die Controller von Spring drin. Für die Rest Schnittstelle gibt es einen eigenen.

- Im Package `de.sopro.DTO` sind Klassen, die für den HTTP Datenaustausch verwendet werde.

- Im Package `de.sopro.exceptions` sind eigene Exceptions deklariert.

- Im Package `de.sopro.filter` ist eine Klasse, die es uns ermöglicht @JsonView Annotation zu verwenden, um nur gewünschte Daten von Objekten zu senden.

- Im Package `de.sopro.logic` ist unsere Logikkomponente, um alternativen zu suchen.

- Im Package `de.sopro.model` sind unsere @Entity Klassen, wovon nachher Objekte in der Datenbank gespeichert werden.

- Im Package `de.sopro.repository` sind die benutzten Repsitories, um sich Daten aus der Datenbank zu holen.

- Im Package `de.sopro.security` sind benötigte Sachen für die Authorisierung und Authentifizierung.

- Im Package `de.sopro.services` werden Services zur Verfügung gestellt. Diese nehmen unter anderem Logik aus den Controllern und dem Model raus.

### Knwon bugs Webseite
Home
- Logos der Produkte werden in vorhandenen Kombinationen nicht angezeigt
- Nicht-quadratische Logos werden verzehrt angezeigt.
- Ein Klick auf 'nur zertifizierte Produkte' lädt die Liste nicht automatisch neu.

Produkte
- Als nicht-eingeloggter Benutzer können Bilder ausgetauscht werden.
- Es wird eine zweite 'nur zertifizierte Produkte'-Checkbox angezeigt, die aber keine Funktion hat.
- Produkte können nicht gelöscht werden.

Formate
- Nichtsaussagende Error Page, wenn man ein Format löscht, welches in einem Produkt verwendet wird.
