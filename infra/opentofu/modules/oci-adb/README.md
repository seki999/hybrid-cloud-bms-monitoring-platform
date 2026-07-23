# OCI Autonomous Database module

创建私有端点、强制 mTLS 的 Autonomous Transaction Processing 实例。该资源可能显著计费且密码/Wallet 属于 Secret，所以默认关闭，真实密码只可由 CI Secret/Vault 传入，Wallet 下载后必须置于 Git 外。应用通过 `oracle` profile 与 Oracle JDBC 连接；销毁会永久删除数据，应先验证备份和保留策略。
