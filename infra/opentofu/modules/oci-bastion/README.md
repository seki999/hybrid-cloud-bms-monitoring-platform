# OCI Bastion module

提供临时、审计化的私网访问入口，替代长期暴露 SSH 22 的跳板机。Smart Jumper 指运维人员通过受控 Bastion 会话访问目标，不给目标分配公网 IP。允许网段必须缩到人员出口 `/32`，会话 TTL 有上限；不要提交私钥。服务/会话可能计费，默认关闭，结束后执行 `tofu destroy`。
