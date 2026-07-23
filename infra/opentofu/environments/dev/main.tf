module "oci_network" {
  source         = "../../modules/oci-network"
  enabled        = var.enable_oci
  compartment_id = var.oci_compartment_id
  name           = "bms-dev"
}
module "aws_network" {
  source               = "../../modules/aws-network"
  enabled              = var.enable_aws
  name                 = "bms-dev"
  availability_zones   = var.aws_availability_zones
  public_subnet_cidrs  = ["10.10.10.0/24", "10.10.11.0/24"]
  private_subnet_cidrs = ["10.10.20.0/24", "10.10.21.0/24"]
}

# 下列模块即使关闭也会参与 init/validate，从而在不创建资源时检查全部模块接口与 Provider schema。
module "oci_oke" {
  source            = "../../modules/oci-oke"
  enabled           = false
  compartment_id    = var.oci_compartment_id
  vcn_id            = module.oci_network.vcn_id == null ? "" : module.oci_network.vcn_id
  cluster_subnet_id = module.oci_network.public_subnet_id == null ? "" : module.oci_network.public_subnet_id
  node_subnet_id    = module.oci_network.private_subnet_id == null ? "" : module.oci_network.private_subnet_id
}

module "oci_load_balancer" {
  source            = "../../modules/oci-load-balancer"
  enabled           = false
  compartment_id    = var.oci_compartment_id
  public_subnet_id  = module.oci_network.public_subnet_id == null ? "" : module.oci_network.public_subnet_id
  private_subnet_id = module.oci_network.private_subnet_id == null ? "" : module.oci_network.private_subnet_id
}

module "oci_adb" {
  source         = "../../modules/oci-adb"
  enabled        = false
  compartment_id = var.oci_compartment_id
  subnet_id      = module.oci_network.private_subnet_id == null ? "" : module.oci_network.private_subnet_id
}

module "oci_functions" {
  source         = "../../modules/oci-functions"
  enabled        = false
  compartment_id = var.oci_compartment_id
}

module "oci_bastion" {
  source           = "../../modules/oci-bastion"
  enabled          = false
  compartment_id   = var.oci_compartment_id
  target_subnet_id = module.oci_network.private_subnet_id == null ? "" : module.oci_network.private_subnet_id
}

module "aws_lambda" {
  source  = "../../modules/aws-lambda"
  enabled = false
}
