#!/bin/sh
helm delete moviemanager
helm install moviemanager ./  --set serviceType=NodePort