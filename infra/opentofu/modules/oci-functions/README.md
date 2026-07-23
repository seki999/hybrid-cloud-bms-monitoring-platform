# OCI Functions module

部署 Java 告警联动函数的 Application 与 Function。生产还需在环境层建立 Dynamic Group/IAM Policy，使函数最小权限访问 Notifications、Logging、Monitoring 与 Vault；镜像存于 OCIR，禁止在 config 中放 Secret。函数调用和日志均可能计费，默认关闭。先删除函数，再 `tofu destroy` 清理应用。
