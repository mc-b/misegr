BPMN Microservice Beispiel
--------------------------

Die [Business Process Model and Notation (BPMN, deutsch Geschäftsprozessmodell und -notation)](http://www.bpmn.org/) ist eine grafische Spezifikationssprache in der Wirtschaftsinformatik und im Prozessmanagement. Sie stellt Symbole zur Verfügung, mit denen Fach-, Methoden- und Informatikspezialisten Geschäftsprozesse und Arbeitsabläufe modellieren und dokumentieren können.

Die Beispiele basieren auf dem [BPMN Tutorial](https://github.com/bernet-tbz/bpmn-tutorial) entwickelt für das Informatik Modul [254 Geschäftsprozesse beschreiben](https://cf.ict-berufsbildung.ch/modules.php?name=Mbk&a=20101&cmodnr=254&noheader=1).

Die Beispiele `camunda` und `frontend` verwenden das Ingress Add-on von Kubernetes und sind mittels `https://<Cluster-IP>:30443/<Prefix Service>` erreichbar, z.B. `https://localhost:30443/camunda/`.

### BPMN Workflow Engine

Das Herzstück ist die [BPMN Workflow Engine](https://camunda.com/de/products/) von [camunda](https://camunda.com).

Die Camunda Workflow Engine unterstützt (Micro-)Service Orchestration, Human Workflow Management und vieles mehr. Sie können die Engine als Remote REST Service benutzen, oder auch in Ihre Java-Anwendung direkt einbetten.

Starten der Workflow Engine:

	kubectl create -f misegr/bpmn/camunda.yaml

Die Oberfläche kann mittels [https://Cluster-IP:30443/camunda](https://localhost:30443/camunda) erreicht werden.

* Username: demo
* Password: demo
	
Nach dem Starten muss der Rechnungsprozess in die Workflow Engine importiert werden. Dies geht am einfachsten in der Bash Umgebung mit `curl`

	cd misegr/bpmn/
	curl -k -w "\n" \
	-H "Accept: application/json" \
	-F "deployment-name=rechnung" \
	-F "enable-duplicate-filtering=true" \
	-F "deploy-changed-only=true" \
	-F "Rechnung.bpmn=@RechnungStep3.bpmn" \
	https://localhost:30443/engine-rest/deployment/create

### Frontend

Ein einfaches in HTML und JavaScript implementiertes Frontend, erreichbar mittels [https://Cluster-IP:30443/frontend/index.html](https://localhost:30443/frontend/index.html).

Starten mittels:

	kubectl create -f misegr/bpmn/bpmn-frontend.yaml

Nach Eingabe von Rechnungs-Nummer und Betrag und drücken von `+` wird der Rechnungsprozess gestartet. 

**Hinweis**: Ist der Kubernetes Cluster auf einer anderen IP als 192.168.60.100 erreichbar, ist im `/usr/local/apache2/htdocs/frontend/src/app/app.js` der Aufruf der Workflow Engine anzupassen.

	/** Abhandlung submit Button */
	function addToDo()
	{
	    var rnr = $('#rnr');
	    var rnrValue = rnr.val();
	    if ( rnrValue ) 
	    {
	    	rbetrag =  $('#rbetrag');
	    	rbetragValue = rbetrag.val();
	    	text = '{ "variables": { "rnr": {"value": "' + rnrValue + '", "type": "long"}, ' + 
	 	              '"rbetrag": {"value": "' + rbetragValue + '", "type": "String"} } }';
	        $('<li>')
	            .appendTo('#bpmn-list')
	            .text( text )
	            .append(
	                $('<button>')
	                    .text('X')
	                    .on('click', removeToDo)
	            );
	        rnr.val( '' );
	        rbetrag.val( '' );
	        $.post( "https://192.168.60.100:30443/engine-rest/process-definition/key/RechnungStep3/start",
	        		text );
	    }
	}


### Backend

Das Backend ist in Java implementiert als J2EE Server kommt [Grizzly](https://javaee.github.io/grizzly/) zum Einsatz.

Starten mittels:

	kubectl create -f misegr/bpmn/bpmn-backend.yaml
	
Der Service wird nicht am Cluster veröffentlicht und ist nur aus einem Pod heraus mittels 

	curl http://bpmn-backend:8090/myapp/application.wadl

ansprechbar. Die Ausgabe erfolgt im [WADL Format](https://javaee.github.io/wadl/). 
	
Aufruf des REST Backends:
	
	curl -vvv -H "Content-Type: application/json" -X POST http://bpmn-backend:8090/myapp/rechnung/zahlen -d '{ "rnr": "123", "rdatum": "28.11.17", "rbetrag": "200.00" }'
