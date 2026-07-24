# 创建 OCI VCN、公私子网、网关与路由关系，为容器、数据库和函数提供分层网络。
# 公网与私网职责分离，避免后端工作负载因默认路由而被意外直接暴露。
resource "oci_core_vcn" "this" {
  count          = var.enabled ? 1 : 0
  compartment_id = var.compartment_id
  cidr_blocks    = [var.vcn_cidr]
  display_name   = "${var.name}-vcn"
  dns_label      = "bmsvcn"
}
resource "oci_core_internet_gateway" "this" {
  count          = var.enabled ? 1 : 0
  compartment_id = var.compartment_id
  vcn_id         = oci_core_vcn.this[0].id
  display_name   = "${var.name}-igw"
}
resource "oci_core_nat_gateway" "this" {
  count          = var.enabled ? 1 : 0
  compartment_id = var.compartment_id
  vcn_id         = oci_core_vcn.this[0].id
  display_name   = "${var.name}-nat"
}
resource "oci_core_route_table" "public" {
  count          = var.enabled ? 1 : 0
  compartment_id = var.compartment_id
  vcn_id         = oci_core_vcn.this[0].id
  display_name   = "${var.name}-public-rt"
  route_rules {
    network_entity_id = oci_core_internet_gateway.this[0].id
    destination       = "0.0.0.0/0"
    destination_type  = "CIDR_BLOCK"
  }
}
resource "oci_core_route_table" "private" {
  count          = var.enabled ? 1 : 0
  compartment_id = var.compartment_id
  vcn_id         = oci_core_vcn.this[0].id
  display_name   = "${var.name}-private-rt"
  route_rules {
    network_entity_id = oci_core_nat_gateway.this[0].id
    destination       = "0.0.0.0/0"
    destination_type  = "CIDR_BLOCK"
  }
}
resource "oci_core_network_security_group" "bms" {
  count          = var.enabled ? 1 : 0
  compartment_id = var.compartment_id
  vcn_id         = oci_core_vcn.this[0].id
  display_name   = "${var.name}-nsg"
}
resource "oci_core_subnet" "public" {
  count                      = var.enabled ? 1 : 0
  compartment_id             = var.compartment_id
  vcn_id                     = oci_core_vcn.this[0].id
  cidr_block                 = var.public_subnet_cidr
  display_name               = "${var.name}-public"
  route_table_id             = oci_core_route_table.public[0].id
  prohibit_public_ip_on_vnic = false
}
resource "oci_core_subnet" "private" {
  count                      = var.enabled ? 1 : 0
  compartment_id             = var.compartment_id
  vcn_id                     = oci_core_vcn.this[0].id
  cidr_block                 = var.private_subnet_cidr
  display_name               = "${var.name}-private"
  route_table_id             = oci_core_route_table.private[0].id
  prohibit_public_ip_on_vnic = true
}
