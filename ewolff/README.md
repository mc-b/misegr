Beispiele aus dem Buch Microservices Rezepte
--------------------------------------------

Diese Beispiele basieren auf dem Buch [Microservices-Praxisbuch](http://microservices-praxisbuch.de/rezepte.html).

Sie wurden adaptiert und erweitert für [Kubernetes](https://kubernetes.io/).

Um die Beispiele zu Compilieren braucht es eine Java/Maven Umgebung mit `docker` und `kubectl`.

Deshalb muss zuerst die Java/Maven Umgebung gestartet und in dessen Container gewechselt werden:

	kubectl create -f https://raw.githubusercontent.com/mc-b/duk/master/dockerindocker/maven-cli.yaml
	
### SCS ESI Beispiel (Frontend)

Für in sich geschlossene Systeme müssen mehrere Frontends integriert werden. Dieses Beispiel zeigt, wie dies zu tun ist. Varnish dient als Cache, und ESI (Edge Side Includes) wird verwendet, um mehrere Backends in eine HTTP-Site zu integrieren.

#### Compilieren (optional)

Wechsel in Java/Maven Container, Microservices compilieren und Docker Images aufbereiten:

	kubectl exec -it maven-cli -- bash
	
	cd /src
	git clone https://github.com/mc-b/SCS-ESI.git
	cd SCS-ESI/scs-demo-esi-order/
	mvn clean package -Dmaven.test.skip=true
	cd ..
    docker build -t misegr/scsesi_varnish docker/varnish
    docker build -t misegr/scsesi_common scs-demo-esi-common
    docker build -t misegr/scsesi_order scs-demo-esi-order
	docker images | grep scs
	exit

Die compilierten Microservices werden im Startverzeichnis der VM abgelegt. 	

#### Starten

Anschliessend können die Microservices gestartet und mit `kubectl get pods` der aktuelle Zustand abgefragt werden:
	
	kubectl create -f misegr/ewolff/SCS-ESI.yaml
	kubectl create -f misegr/ewolff/SCS-ESI-order.yaml
	kubectl get pods -n scsesi
    
Probieren mittels [http://ip NodePort:32080](http://localhost:32080) und [http://ip NodePort:32090](http://localhost:32090).

Nach dem Test die Container wieder beenden, mittels:

	kubectl delete -f misegr/ewolff/SCS-ESI.yaml
	kubectl delete -f misegr/ewolff/SCS-ESI-order.yaml

#### Links

* [github Repository](https://github.com/ewolff/SCS-ESI)

### Microservice Kafka Sample

Dies ist ein Beispiel um zu zeigen, wie Kafka für die Kommunikation zwischen Microservices verwendet werden kann.

#### Compilieren (optional)

Wechsel in Java/Maven Container, Microservices compilieren und Docker Images aufbereiten:

	kubectl exec -it maven-cli -- bash
	
	cd /src
	git clone https://github.com/mc-b/microservice-kafka.git
	cd microservice-kafka/microservice-kafka
	mvn clean package -Dmaven.test.skip=true
	cd ..
	
    docker build -t misegr/mskafka_apache docker/apache	
    docker build -t misegr/mskafka_postgres docker/postgres
    docker build -t misegr/mskafka_order microservice-kafka/microservice-kafka-order
    docker build -t misegr/mskafka_shipping microservice-kafka/microservice-kafka-shipping
    docker build -t misegr/mskafka_invoicing microservice-kafka/microservice-kafka-invoicing
	docker images | grep mskafka
	exit
	
Die compilierten Microservices werden im Startverzeichnis der VM abgelegt. 	

#### Starten

Anschliessend können die Microservices gestartet und mit `kubectl get pods` der aktuelle Zustand abgefragt werden:
	
	kubectl create -f misegr/ewolff/ms-kafka/
    kubectl get pods -n ms-kafka	

Probieren mittels [http://ip NodePort:32180](http://localhost:32180).

Nach dem Test die Container wieder beenden, mittels:

	kubectl delete -f misegr/ewolff/ms-kafka/

#### Testen

In Kafka Container wechseln und Topics anschauen und dessen Meldungen

	kubectl exec -it $(kubectl get po --selector=app=mskafka-kafka -o=jsonpath='{ .items[0].metadata.name }') -- bash
	cd /opt/kafka
	bin/kafka-topics.sh --list --zookeeper zookeeper:2181
	bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order --from-beginning

#### Links

* [github Repository](https://github.com/ewolff/microservice-kafka)

### Microservice Kubernetes Sample

Dieses Projekt ist eine Demo für das
[Microservices Buch](http://microservices-buch.de/) ([English](http://microservices-book.com/)).

Dieses Projekt erzeugt eine vollständigen Microservice-Demo in 
Docker-Containern. Die Services sind mit Java, Spring und Spring Cloud
implementiert.

Das System hat drei Microservices:
- Order um Bestellungen entgegenzunehmen
- Customer für Kundendaten
- Catalog für die Waren

#### Compilieren (optional)

Wechsel in Java/Maven Container, Microservices compilieren und Docker Images aufbereiten:

	kubectl exec -it maven-cli -- bash
	
	cd /src
	git clone https://github.com/mc-b/microservice-kubernetes.git
	cd microservice-kubernetes/microservice-kubernetes-demo/
	mvn clean package -Dmaven.test.skip=true
	
	# docker build
	docker build -t misegr/microservice-kubernetes-demo-apache:v0.0.1 apache
	docker build -t misegr/microservice-kubernetes-demo-catalog:v0.0.1 microservice-kubernetes-demo-catalog
	docker build -t misegr/microservice-kubernetes-demo-customer:v0.0.1 microservice-kubernetes-demo-customer
	docker build -t misegr/microservice-kubernetes-demo-customer:v0.0.1 microservice-kubernetes-demo-customer
	docker build -t misegr/microservice-kubernetes-demo-order:v0.0.1 microservice-kubernetes-demo-order
	docker build -t misegr/microservice-kubernetes-demo-hystrix-dashboard:v0.0.1 microservice-kubernetes-demo-hystrix-dashboard
	docker images | grep microservice
	exit
   
Die compilierten Microservices werden im Startverzeichnis der VM abgelegt. 	

#### Starten

Anschliessend können die Microservices gestartet und mit `kubectl get pods` der aktuelle Zustand abgefragt werden:

	kubectl create -f misegr/ewolff/ms-kubernetes/
    kubectl get pods -n ms-kubernetes	

Probieren mittels [http://ip NodePort:32280](http://localhost:32280).

Nach dem Test die Container wieder beenden, mittels:

	kubectl delete -f misegr/ewolff/ms-kubernetes/
	
#### Testen (in der Bash Shell)

Kunden anzeigen (im JSON Format) und anlegen/ändern

	curl -X GET http://localhost:32280/customer/customer
	
	curl http://localhost:32080/customer/search/findByName?name=Johnson

	curl -X POST http://localhost:32280/customer/form.html \
	     -H "Content-Type: application/x-www-form-urlencoded" \
		 -d "name=name2" -d "firstname=firstname2" -d "email=mail@ch.ch" -d "street=street2" -d "city=city 2" 


Produkte anzeigen 

	curl -X GET http://localhost:32280/catalog/catalog

Produkte Bestellen

	curl -X POST http://localhost:32280/order/ \
	     -H "Content-Type: application/x-www-form-urlencoded" \
	     -d "customerId=1" \
	     -d "orderLine[0].count=1" \
	     -d "orderLine[0].itemId=3"
		 
	    
#### Links

* [github Repository](https://github.com/ewolff/microservice-kubernetes) 
* [github Repository Microservice](https://github.com/ewolff/microservice)   