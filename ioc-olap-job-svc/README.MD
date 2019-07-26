#暂不可用，有待修改

## 计划任务发布服务器操作说明
### 公司测试环境(192.168.6.102)
1. cd /root/ioc-paas-component/ioc-job-svc
2. git pull
3. oc delete -f platform-job-test.yaml
4. ./build-test.sh
5. oc create -f platform-job-test.yaml


### 政务云内网测试机
1. cd /app/ioc-paas-component/ioc-job-svc
2. git pull
3. ./build-govtest.sh
