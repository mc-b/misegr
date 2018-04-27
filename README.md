# Beispiele zum Kurs [Microservices-Grundlagen («MISEGR»)](https://www.digicomp.ch/weiterbildung/web-und-softwareentwicklungs-trainings/software-engineering/softwarearchitektur/microservices-grundlagen)

Basierend auf dem Buch [Microservices-Praxisbuch](http://microservices-praxisbuch.de/rezepte.html) adaptiert und erweitert für [Kubernetes](https://kubernetes.io/).

### Benötigte Software

* [Git](https://git-scm.com/)
* [VirtualBox](https://www.virtualbox.org/)
* [Vagrant](https://www.vagrantup.com/) 

**Hinweis:** Git auf Windows ohne CR/LF Umwandlung installieren.

### Installation 

Dieses Repository in der Git/Bash Shell clonen:

	git clone https://github.com/mc-b/misegr.git

[docker](https://download.docker.com/win/static/stable/x86_64/) und [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/) downloaden und ausführbare Dateien ins `bin` Verzeichnis ablegen

Virtuelle Maschine erstellen:

	cd misegr
	vagrant up

Evtl. ist vorher die fixe IP und der Hostname im Vagrantfile anzupassen. Siehe Bemerkungen im Vagrantfile.

Beim Starten werden zwei Verzeichnisse angelegt:

- .docker - Zertifikate für Docker Client 
- .kube - Konfigurationsdatei und Zertifikate für Kubernetes.

Diese zwei Verzeichnisse sind ins HOME Verzeichnis des Users zu kopieren.

Anschliessend kann mittels `docker` und `kubectl` die Kubernetes VM gesteuert werden

	docker -H <fixe IP>:2376 --tls ps
	kubectl get services

Um auf die `-H` und `--tls` Argumente verzichten zu können sind folgende Umgebungsvariablen zu setzen:

	DOCKER_HOST=tcp://<fixe-IP>:2376
	DOCKER_TLS_VERIFY=1

Wird die IP-Adresse oder der Hostname geändert, muss die Virtuelle Maschine frisch erstellt werden.

ACHTUNG: Evtl. gesetzte Umgebungsvariable `DOCKER_CERT_PATH` darf nicht gesetzt sein.

#### Dashboard

Das Dashboard ist Standardmässig nicht erreichbar. Dazu muss zuerst ein Proxy zur lokalen Maschine eingerichtet werden:
	
Diese Arbeit und das Starten des Browsers übernimmt die Datei `dashboard.bat`.
	
Der Logindialog kann mit `Skip` übersprungen werden.

### Weave Scope u

[Weave Scope](https://www.weave.works/) ist ein Werkzeug zur grafischen Visualisierung Ihrer Container, Pods, Dienste usw.

Die Weave Scope kann mittels der Datei `weave.bat` gestartet werden. Dabei wird ein Proxy auf Port 4040 eingerichtet und [http://localhost:4040](http://localhost:4040) im Browser geöffnet.

