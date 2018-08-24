# Beispiele zum Kurs [Microservices-Grundlagen («MISEGR»)](https://www.digicomp.ch/weiterbildung/web-und-softwareentwicklungs-trainings/software-engineering/softwarearchitektur/microservices-grundlagen)

### Quick Start

Installiert [Git/Bash](https://git-scm.com/downloads), [Vagrant](https://www.vagrantup.com/) und [VirtualBox](https://www.virtualbox.org/).

Projekt [lernkube](https://github.com/mc-b/lernkube), auf der Git/Bash Kommandozeile (CLI), klonen, Konfigurationsdatei (.yaml) kopieren und Installation starten. 

	git clone https://github.com/mc-b/lernkube
	cd lernkube
	cp templates/MISEGR.yaml config.yaml
	vagrant plugin install vagrant-disksize
	vagrant up

**Weitere Installationsmöglichkeiten und Details** zur Installation siehe Projekt [lernkube](https://github.com/mc-b/lernkube).

*Optional*: Zusätzliche Services wie `jenkins` und SQL Web UI `adminer` starten.

	kubectl create -f https://raw.githubusercontent.com/mc-b/duk/master/devops/jenkins.yaml
	kubectl create -f https://raw.githubusercontent.com/mc-b/duk/master/mysql/adminer.yaml
	
Und alle Microservices starten
	
	kubectl create -f misegr/ewolff/
	kubectl create -f misegr/ewolff/ms-kafka/
	kubectl create -f misegr/ewolff/ms-kubernetes/
	kubectl create -f misegr/bpmn/

### Microservices Beispiele

Es stehen folgende Microservice Beispiel zur Verfügung
* [Microservices](ewolff/) - Basierend auf dem Buch [Microservices-Praxisbuch](http://microservices-praxisbuch.de/rezepte.html) adaptiert und erweitert für [Kubernetes](https://kubernetes.io/).
* [BPMN Beispiel](bpmn/)

### Links

* [Maven Umgebung](https://github.com/mc-b/duk/tree/master/dockerindocker)
* [Jenkins Umgebung](https://github.com/mc-b/duk/tree/master/devops#jenkins-mit-blueocean)
* [Interaktives Lernen mit Jupyter/BeakerX](https://github.com/mc-b/duk/tree/master/jupyter)
* [Weitere Kubernetes Beispiele](https://github.com/mc-b/dok#weitere-beispiele)
