# staging

从 `../dev` 复制受评审的环境根模块后，使用独立 OCI Compartment、AWS account/role、远端 state key 和 staging tfvars。所有 `enable_*` 默认保持 `false`；先执行 `tofu plan -out=staging.tfplan` 并由第二人复核。该目录不提交 OCID、账户号、Wallet 或密码。
