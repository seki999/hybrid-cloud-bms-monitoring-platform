# OCI OKE
## 技术是什么
Oracle 托管 Kubernetes control plane 和 Node Pool 服务。
## 为什么使用
让五个 Spring Boot 逻辑角色独立部署、扩缩、滚动升级和故障隔离。
## 项目位置
`modules/oci-oke` 与 `infra/kubernetes` base/overlay。
## 主要概念
Cluster endpoint、Node Pool、CNI、Workload、Service、Ingress、Pod Security、autoscaling。
## 主要配置
私有 API endpoint、私有 node subnet、Flex shape；版本/镜像须在部署时通过 OCI data source 选择。
## 示例代码
Web 使用 2 replicas、HPA、PDB；receiver 使用 NLB Service；worker 不暴露业务入口。
## 常用命令
`kubectl apply -k infra/kubernetes/base`；`kubectl rollout status deployment/bms-web-app`。
## 常见错误
镜像版本与节点不兼容、LB 子网权限不足、无 metrics-server 却期待 HPA、单节点环境被 PDB 阻止 drain。
