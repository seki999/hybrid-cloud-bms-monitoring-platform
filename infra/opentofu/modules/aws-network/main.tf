resource "aws_vpc" "this" {
  count                = var.enabled ? 1 : 0
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true
  tags = {
    Name = "${var.name}-vpc"
  }
}
resource "aws_internet_gateway" "this" {
  count  = var.enabled ? 1 : 0
  vpc_id = aws_vpc.this[0].id
  tags = {
    Name = "${var.name}-igw"
  }
}
resource "aws_subnet" "public" {
  for_each = var.enabled ? {
    for i, cidr in var.public_subnet_cidrs : i => cidr
  } : {}
  vpc_id                  = aws_vpc.this[0].id
  cidr_block              = each.value
  availability_zone       = var.availability_zones[tonumber(each.key)]
  map_public_ip_on_launch = false
  tags = {
    Name = "${var.name}-public-${each.key}"
  }
}
resource "aws_subnet" "private" {
  for_each = var.enabled ? {
    for i, cidr in var.private_subnet_cidrs : i => cidr
  } : {}
  vpc_id            = aws_vpc.this[0].id
  cidr_block        = each.value
  availability_zone = var.availability_zones[tonumber(each.key)]
  tags = {
    Name = "${var.name}-private-${each.key}"
  }
}
resource "aws_security_group" "lambda" {
  count       = var.enabled ? 1 : 0
  name_prefix = "${var.name}-lambda-"
  description = "Egress-only monitoring Lambda"
  vpc_id      = aws_vpc.this[0].id
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  lifecycle {
    create_before_destroy = true
  }
}
