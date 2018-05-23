# -*- mode: ruby -*-
# vi: set ft=ruby :

#
#	Ubuntu Xenial 64-bit Linux mit Docker und Kubernetes
#
#	Es wird ein Server Zertifikat fuer Hostname vgkube mit fixer IP erstellt.
#	Soll ein anderer Hostname als vgkube oder der fixen IP verwendet werden sind 
#	diese mittels suchen und ersetzen im Vagrantfile zu ändern.
#	Ebenfalls kann zwischen einem Host-Only oder Public-IP gewaehlt werden.
#

Vagrant.configure("2") do |config|

  config.vm.box = "ubuntu/xenial64"
  # resize hd, need a plugin vagrant-disksize, see https://github.com/sprotheroe/vagrant-disksize
  config.disksize.size = '40GB'
      
  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  config.vm.hostname = "misegr"
  config.vm.network "private_network", ip: "192.168.60.100"
  # config.vm.network "public_network", bridge: "enp0s8"

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. 
  # NOTE: This will enable public access to the opened port
  # gogs, kanboard, jenkins
  config.vm.network "forwarded_port", guest: 32100, host: 32100
  config.vm.network "forwarded_port", guest: 32200, host: 32200
  config.vm.network "forwarded_port", guest: 32300, host: 32300
  # microservices
  config.vm.network "forwarded_port", guest: 32080, host: 32080
  config.vm.network "forwarded_port", guest: 32090, host: 32090
  config.vm.network "forwarded_port", guest: 32180, host: 32180
  config.vm.network "forwarded_port", guest: 32280, host: 32280
  # jupyter/beakerX (ML)
  config.vm.network "forwarded_port", guest: 32088, host: 32088
  config.vm.network "forwarded_port", guest: 32188, host: 32188
  config.vm.network "forwarded_port", guest: 32288, host: 32288
  # Kubernetes Port (ingress)
  config.vm.network "forwarded_port", guest: 30443, host: 30443
      
  # default router.
  # config.vm.provision "shell",
  #  run: "always",
  #  inline: "route add default gw 192.168.178.1 enp0s8 && route del default gw 10.0.2.2 enp0s3"   

  # Gemeinsames Datenverzeichnis fuer Kubernetes Master und Nodes
  config.vm.synced_folder "data", "/data"
  
  config.vm.provider "virtualbox" do |vb|
     vb.memory = "6144"
  end

  # Docker Provisioner
  config.vm.provision "docker" do |d|
     d.pull_images "maven:3-alpine"
     d.pull_images "jenkinsci/blueocean"
     # Cached Images damit der Start schneller geht
     d.pull_images "camunda/camunda-bpm-platform"
     d.pull_images "misegr/scsesi_varnish"
     d.pull_images "misegr/mskafka_postgres"
     d.pull_images "wurstmeister/kafka:1.0.0"
     d.pull_images "wurstmeister/zookeeper:3.4.6"
  end
  
  config.vm.provision "shell", inline: <<-SHELL 
    # Debug ON!!!
    set -o xtrace

	wget https://pkg.cfssl.org/R1.2/cfssljson_linux-amd64 -O cfssljson
	wget https://pkg.cfssl.org/R1.2/cfssl_linux-amd64 -O cfssl
	sudo chmod +x cfssljson cfssl
	sudo mv cfssljson cfssl /usr/local/bin
	cd /vagrant/csr
	cfssl gencert -initca ca-csr.json | cfssljson -bare ca
	cat >docker-server-csr.json <<%EOF%
{
  "CN": "*.kubestack.io",
  "hosts": [
    "127.0.0.1",
    "vgkube",
    "$(hostname -I | cut -d ' ' -f 2)"
  ],
  "key": {
    "algo": "rsa",
    "size": 2048
  },
  "names": [
    {
      "C": "CH",
      "L": "Zurich",
      "O": "Docker",
      "OU": "Docker Engine",
      "ST": "ZH"
    }
  ]
}	
%EOF%

	cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=server docker-server-csr.json | cfssljson -bare docker-server
    cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=client docker-client-csr.json | cfssljson -bare docker-client
	sudo cp ca.pem /etc/docker/ca.pem
	sudo mv docker-server-key.pem /etc/docker/server-key.pem
	sudo mv docker-server.pem /etc/docker/server.pem
	sudo chmod 0600 /etc/docker/*.pem
	sudo chown root:root /etc/docker/*.pem
	mkdir -p /home/vagrant/.docker
	mv ca.pem /home/vagrant/.docker/
    mv docker-client.pem /home/vagrant/.docker/cert.pem
    mv docker-client-key.pem /home/vagrant/.docker/key.pem
    chmod 0600 /home/vagrant/.docker/*.pem
    chmod 0700 /home/vagrant/.docker
    chown vagrant:vagrant /home/vagrant/.docker
    sudo rm -rf /vagrant/.docker
    sudo cp -rp /home/vagrant/.docker /vagrant/
    sudo rm /vagrant/csr/*.csr /home/vagrant/csr/*.pem
	sudo cat > /etc/systemd/system/docker.service <<%EOF%
[Service]
Type=notify
# the default is not to use systemd for cgroups because the delegate issues still
# exists and systemd currently does not support the cgroup feature set required
# for containers run by docker
ExecStart=/usr/bin/dockerd --tlsverify --tlscacert=/etc/docker/ca.pem --tlscert=/etc/docker/server.pem \
--tlskey=/etc/docker/server-key.pem -H=0.0.0.0:2376 -H fd://
ExecReload=/bin/kill -s HUP $MAINPID
LimitNOFILE=1048576
# Having non-zero Limit*s causes performance problems due to accounting overhead
# in the kernel. We recommend using cgroups to do container-local accounting.
LimitNPROC=infinity
LimitCORE=infinity
# Uncomment TasksMax if your systemd version supports it.
# Only systemd 226 and above support this version.
TasksMax=infinity
TimeoutStartSec=0
# set delegate yes so that systemd does not reset the cgroups of docker containers
Delegate=yes
# kill only the docker process, not all processes in the cgroup
KillMode=process
# restart the docker process if it exits prematurely
Restart=on-failure
StartLimitBurst=3
StartLimitInterval=60s

[Install]
WantedBy=multi-user.target
%EOF%

	sudo systemctl daemon-reload
	sudo systemctl restart docker

	# Install Kubernetes
	curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
    sudo echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" >> /etc/apt/sources.list.d/Kubernetes.list
    sudo apt-get update
    sudo apt-get install -y kubelet=1.9.7-00 kubeadm=1.9.7-00 kubectl=1.9.7-00 dos2unix
    
    sudo kubeadm init --pod-network-cidr=10.244.0.0/16 --apiserver-advertise-address  $(hostname -I | cut -d ' ' -f 2)
    
    mkdir -p $HOME/.kube
	sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
	sudo chown $(id -u):$(id -g) $HOME/.kube/config
    
	# this for loop waits until kubectl can access the api server that kubeadm has created
	for i in {1..150}; do # timeout for 5 minutes
	   ./kubectl get po &> /dev/null
	   if [ $? -ne 1 ]; then
	      break
	  fi
	  sleep 2
	done
    
    # Internes Pods Netzwerk
	kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/v0.10.0/Documentation/kube-flannel.yml
    
	# Pods auf Master Node erlauben
	kubectl taint nodes --all node-role.kubernetes.io/master-
	
	# Vagrant User Zugriff auf Cluster erlauben
	cp -rp $HOME/.kube /home/vagrant/
	chown -R vagrant:vagrant /home/vagrant/.kube
	# Externer Zugriff
	cp -rp $HOME/.kube /vagrant/
	
	# Install nginx ingress Add-on
	kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/namespace.yaml
	kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/default-backend.yaml
	kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/configmap.yaml 
	kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/tcp-services-configmap.yaml 
	kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/udp-services-configmap.yaml
	kubectl apply -f  https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/rbac.yaml 
	kubectl apply -f  https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/with-rbac.yaml 	
	kubectl apply -f  /vagrant/addons/service-nodeport.yaml
	
	# Dashboard und User einrichten - Zugriff kubectl proxy und beim Logins skip
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/recommended/kubernetes-dashboard.yaml
    kubectl apply -f /vagrant/addons/dashboard-admin.yaml
	
	# Weave Scope 
	kubectl apply -f "https://cloud.weave.works/k8s/scope.yaml?k8s-version=$(kubectl version | base64 | tr -d '\n')"
	
	# Namespaces Microservices
	kubectl create namespace scsesi
	kubectl create namespace ms-kafka
	kubectl create namespace ms-kubernetes
	
	cat >/vagrant/dashboard.bat <<%EOF%
REM Startet den Browser mit der Dashboard Startseite und den Proxy 
cd /d %~d0%~p0
set DOCKER_HOST=tcp://$(hostname -I | cut -d ' ' -f 2):2376
set DOCKER_TLS_VERIFY=1
set DOCKER_CERT_PATH=%~d0%~p0.docker
set PATH=%PATH%;%~d0%~p0bin
set KUBECONFIG=%~d0%~p0.kube\\config
start http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/. /B
kubectl proxy        
%EOF%
	unix2dos /vagrant/dashboard.bat
	
	cat >/vagrant/dockerps.bat <<%EOF%
REM Setzt die Docker Umgebungsvariablen und startet PowerShell 
cd /d %~d0%~p0
set DOCKER_HOST=tcp://$(hostname -I | cut -d ' ' -f 2):2376
set DOCKER_TLS_VERIFY=1
set DOCKER_CERT_PATH=%~d0%~p0.docker
set PATH=%~d0%~p0bin;~d0%~p0git\\bin;%~d0%~p0git\\mingw64\\bin;%~d0%~p0git\\usr\\bin;%PATH%
set KUBECONFIG=%~d0%~p0.kube\\config
powershell.exe      
%EOF%
	unix2dos /vagrant/dockerps.bat
	
	curl -L https://storage.googleapis.com/kubernetes-release/release/v1.10.0/bin/windows/amd64/kubectl.exe -o /vagrant/bin/kubectl.exe
	
	# Hilfsprogramme
	# kubectl create -f https://raw.githubusercontent.com/mc-b/devops/master/kubernetes/devops/jenkins.yaml
	# kubectl create -f https://raw.githubusercontent.com/mc-b/devops/master/kubernetes/mysql/adminer.yaml
	
	# Microservices
	cd /vagrant
	# kubectl create -f ewolff/
	# kubectl create -f ewolff/ms-kafka/
	# kubectl create -f ewolff/ms-kubernetes/
	# kubectl create -f bpmn/	
	
SHELL
end
