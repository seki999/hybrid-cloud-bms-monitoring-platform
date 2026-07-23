output "vpc_id" {
  value = try(aws_vpc.this[0].id, null)
}
output "public_subnet_ids" {
  value = values(aws_subnet.public)[*].id
}
output "private_subnet_ids" {
  value = values(aws_subnet.private)[*].id
}
output "lambda_security_group_id" {
  value = try(aws_security_group.lambda[0].id, null)
}
