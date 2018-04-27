# Beispiele zum Kurs [Microservices-Grundlagen («MISEGR»)](https://www.digicomp.ch/weiterbildung/web-und-softwareentwicklungs-trainings/software-engineering/softwarearchitektur/microservices-grundlagen)

Basierend auf dem Buch [Microservices-Praxisbuch](http://microservices-praxisbuch.de/rezepte.html) adaptiert und erweitert für [Kubernetes](https://kubernetes.io/).

### Benötigte Software

* [Git](https://git-scm.com/)
* [VirtualBox](https://www.virtualbox.org/)
* [Vagrant](https://www.vagrantup.com/) 

**Hinweis:** Git auf Windows ohne CR/LF Umwandlung installieren.

### Installation 

Dieses Repository in der Git/Bash Shell clonen und Virtuelle Maschine (VM) erstellen:

	git clone https://github.com/mc-b/misegr.git
	cd misegr
	vagrant up
	exit

[docker](https://download.docker.com/win/static/stable/x86_64/) und [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/) downloaden und ausführbare Dateien ins `misegr/bin` Verzeichnis ablegen	

Beim Erstellen der VM werden mehrere BATCH-Dateien angelegt:

* `dockerps.bat` - Setzen der Umgebungsvariablen für den Zugriff auf die VM und Starten der Kommandline (PowerShell) 
* `dashboard.bat` - Aufstarten des Kubernetes Dashboards

### Microservices Beispiele

Es stehen folgende Microservice Beispiel zur Verfügung
* [Microservices-Praxisbuch](ewolff/)
* [BPMN Beispiel inkl. Tutorial](https://github.com/mc-b/bpmn-tutorial)

### Dashboard

Das Dashboard ist Standardmässig nicht erreichbar. Dazu muss zuerst ein Proxy zur lokalen Maschine eingerichtet werden:
	
Diese Arbeit und das Starten des Browsers übernimmt die Datei `dashboard.bat`.
	
Der Logindialog kann mit `Skip` übersprungen werden.

### Weave Scope 

[Weave Scope](https://www.weave.works/) ist ein Werkzeug zur grafischen Visualisierung Ihrer Container, Pods, Dienste usw.

Die Weave Scope kann in der PowerShell wie folgt gestartet werden:

	$env:pod=(kubectl get -n weave pod --selector=weave-scope-component=app -o jsonpath='{.items..metadata.name}')
	start-process http://localhost:4040
	kubectl port-forward -n weave $env:pod 4040

### Links

* [Maven Umgebung](https://github.com/mc-b/devops/tree/master/kubernetes/dockerindocker)
* [Jenkins Umgebung](https://github.com/mc-b/devops/tree/master/kubernetes/devops#jenkins-mit-blueocean)
* [DevOps GitHub Projekt](https://github.com/mc-b/devops)

