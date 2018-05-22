
netsh interface portproxy add v4tov4 listenaddress=localhost listenport=32100  connectaddress=$args connectport=32100
netsh interface portproxy add v4tov4 listenaddress=localhost listenport=32200  connectaddress=$args connectport=32200
netsh interface portproxy add v4tov4 listenaddress=localhost listenport=32300  connectaddress=$args connectport=32300

netsh interface portproxy add v4tov4 listenaddress=localhost listenport=32080  connectaddress=$args connectport=32080
netsh interface portproxy add v4tov4 listenaddress=localhost listenport=32090  connectaddress=$args connectport=32090
netsh interface portproxy add v4tov4 listenaddress=localhost listenport=31280  connectaddress=$args connectport=32180
netsh interface portproxy add v4tov4 listenaddress=localhost listenport=32280  connectaddress=$args connectport=32280

netsh interface portproxy add v4tov4 listenaddress=localhost listenport=30443  connectaddress=$args connectport=30443

netsh interface portproxy add v4tov4 listenaddress=localhost listenport=32088  connectaddress=$args connectport=32088
netsh interface portproxy add v4tov4 listenaddress=localhost listenport=32288  connectaddress=$args connectport=32288

start-process kubectl proxy 
start-process http://localhost:8001/api/v1/namespaces/kube-system/services/https:kubernetes-dashboard:/proxy/. 

$env:pod=(kubectl get -n weave pod --selector=weave-scope-component=app -o jsonpath='{.items..metadata.name}')
start-process http://localhost:4040
kubectl port-forward -n weave $env:pod 4040
