# Beispiele zum Kurs [Microservices-Grundlagen («MISEGR»)](https://www.digicomp.ch/weiterbildung/web-und-softwareentwicklungs-trainings/software-engineering/softwarearchitektur/microservices-grundlagen)

### Benötigte Software

* [Git](https://git-scm.com/)
* [VirtualBox](https://www.virtualbox.org/)
* [Vagrant](https://www.vagrantup.com/) 

Nach der Vagrant Installation ist das Plug-in [vagrant-disksize](https://github.com/sprotheroe/vagrant-disksize) zu Installieren um genügend Speicherplatz für die Microservices zur Verfügung zu stellen:

	vagrant plugin install vagrant-disksize

**Hinweis:** Git auf Windows ohne CR/LF Umwandlung installieren.

### Installation 

Dieses Repository in der Git/Bash Shell clonen und Virtuelle Maschine (VM) erstellen:

	git clone https://github.com/mc-b/misegr.git
	cd misegr
	vagrant up
	exit

*Optional*: [docker](https://download.docker.com/win/static/stable/x86_64/) downloaden, entpacken und `docker.exe` im `misegr/bin` Verzeichnis ablegen	

Beim Erstellen der VM werden mehrere BATCH-Dateien angelegt:

* `dockerps.bat` - Setzen der Umgebungsvariablen für den Zugriff auf die VM und Starten der Kommandline (PowerShell) 
* `dashboard.bat` - Aufstarten des Kubernetes Dashboards

### Microservices Beispiele

Es stehen folgende Microservice Beispiel zur Verfügung
* [Microservices](ewolff/) - Basierend auf dem Buch [Microservices-Praxisbuch](http://microservices-praxisbuch.de/rezepte.html) adaptiert und erweitert für [Kubernetes](https://kubernetes.io/).
* [BPMN Beispiel inkl. Tutorial](https://github.com/mc-b/bpmn-tutorial)

### Dashboard

Das Dashboard ist Standardmässig nicht erreichbar. Dazu muss zuerst ein Proxy zur lokalen Maschine eingerichtet werden:
	
Diese Arbeit und das Starten des Browsers übernimmt die Datei `dashboard.bat`.
	
Der Logindialog kann mit `Skip` übersprungen werden.

### Weave Scope 

[Weave Scope](https://www.weave.works/) ist ein Werkzeug zur grafischen Visualisierung Ihrer Container, Pods, Dienste usw.

Die Weave Scope Oberfläche, kann in der PowerShell, mittels `waeve.bat` gestartet werden.

### Links

* [Maven Umgebung](https://github.com/mc-b/devops/tree/master/kubernetes/dockerindocker)
* [Jenkins Umgebung](https://github.com/mc-b/devops/tree/master/kubernetes/devops#jenkins-mit-blueocean)
* [Kubernetes Cluster einrichten](https://github.com/mc-b/devops/blob/master/kubernetes/README.md#cluster-einrichten)
* [DevOps GitHub Projekt](https://github.com/mc-b/devops)

### FAQ

**Vagrant kann unter Windows 10 keine VM erzeugen, weil Hyper-V aktiv ist**
* **Lösung:** Hyper-V wie in [Hyper-V unter Windows 10 aktivieren und deaktivieren](https://www.xcep.net/blog/hyper-v-unter-windows-10-aktivieren-und-deaktivieren/) beschrieben, deaktiveren. 

**Vagrant up finishes but VM's not showing up in VirtualBox**
* Das vagrant/mmdb Beispiel kann keinen Netzwerkadapter anlegen.
* **Lösung:** Netzwerk manuell unter Datei -> Einstellungen -> Netzwerk -> Host-only Netzwerke mit IPv4 Adresse 192.168.60.1 und Netzmaske 255.255.255.0 anlegen.

**VirtualBox und vagrant nicht mehr Synchron.**
* **Lösung:** VM in VirtualBox manuell löschen und im Beispielverzeichnis (wo Vagrantfile steht) das Verzeichnis .vagrant weglöschen.

**Vagrant kann keine ssh Verbindung zur VM aufbauen.**
* **Lösung:** Firewall deaktivieren.

**vagrant up kann keine Host Ordner mehr mounten.**
* **Lösung:** Installieren Sie VirtualBox in der Version 5.2.6 ab Download

**Vagrant und VirtualBox Produzieren nicht nachvollziehbare Fehler.**
* **Lösung:** Beispiele in ein Verzeichnis ohne " " Leerschlag clonen/downloaden.

**vagrant wird in der Bash nicht gefunden.**
* **Lösung:** Verzeichnis wo sich vagrant.exe befindet in PATH eintragen.

**Alle anderen Fehler.**
* **Lösung:** Vagrant mittels `vagrant up --debug` starten.
