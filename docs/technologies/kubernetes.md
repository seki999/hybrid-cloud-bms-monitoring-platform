# Kubernetes
## 技术是什么
声明式容器编排平台，管理 Deployment、Service、Config、Secret、存储和网络策略。
## 为什么使用
同一镜像可拆分五个逻辑角色，分别伸缩接收、Web 和定时处理负载。
## 项目位置
`infra/kubernetes/base` 与 `overlays/kind`，本地脚本成对支持 PowerShell/bash。
## 主要概念
Pod、Deployment、Service、Ingress、ConfigMap/Secret、PVC、probe、HPA、PDB、RBAC、NetworkPolicy。
## 主要配置
非 root、只读 rootfs、资源 request/limit、restricted namespace、默认拒绝网络、Actuator probes。
## 示例代码
`kubectl kustomize infra/kubernetes/overlays/kind` 在不访问集群时验证渲染。
## 常用命令
`scripts/kind-up.*` 创建；`kubectl get pods -n bms-monitoring` 检查；`kind-down.*` 删除。
## 常见错误
Secret 占位值上线、probe 依赖未就绪、LoadBalancer 在 kind pending、镜像未 load、NetworkPolicy 漏 DNS。
