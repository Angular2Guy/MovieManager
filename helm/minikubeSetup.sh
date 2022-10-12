#!/bin/sh
# execute helmCommand.sh

kubectl get services
minikube ip
http://<minikube ip>:<node-port>/

minikube config set memory 16384
minikube config set cpu 2
minikube config set driver docker
minikube addons list
minikube addons enable metrics-server
minikube start --extra-config=apiserver.service-node-port-range=1024-65535
kubectl edit deployment -n kube-system metrics-server

kubectl logs --previous <pod-name>
kubectl exec --stdin --tty <mongodb-pod-name> -- /bin/bash
kubectl expose pod <postgresql-pod-name> --port=5432 --type="NodePort"


minikube pause
minikube unpause