#!/bin/bash
#
#	Installationsscript misegr

# Kubernetes Namespaces Microservices
for	n in scsesi ms-kafka ms-kubernetes
do
	kubectl create namespace ${n}
done

# Jupyter Scripte etc. Allgemein verfuegbar machen
cp -rpv data/* /data/