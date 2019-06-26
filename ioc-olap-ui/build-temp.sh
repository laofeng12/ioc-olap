docker rmi harbor.node02.paas/ioc-paas/platform-webui:latest

docker build . -t harbor.node02.paas/ioc-paas/platform-webui:latest

docker push harbor.node02.paas/ioc-paas/platform-webui:latest
