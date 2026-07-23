# OCI OKE module

创建私有 API Endpoint 的 OKE 与 Flex shape Node Pool。默认不创建任何资源；OKE、计算节点和负载均衡器会产生费用。生产需从 OCI data source 选择兼容镜像和 Kubernetes 版本，并由 IAM Policy 授权集群动态组。销毁前先删除 Kubernetes LoadBalancer，再执行 `tofu destroy`，避免遗留云负载均衡器。
