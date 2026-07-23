# production

生产环境必须使用独立 Compartment/account、短期 OIDC 凭据、加密远端 state 与锁、双人审批和维护窗口。本学习仓库不提供可直接 apply 的生产 tfvars，避免误建收费资源；基于 staging 已验证版本固定 module commit 后再引入。
