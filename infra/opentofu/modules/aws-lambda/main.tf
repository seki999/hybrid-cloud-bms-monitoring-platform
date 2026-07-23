resource "aws_iam_role" "this" {
  count              = var.enabled ? 1 : 0
  name               = "${var.name}-role"
  assume_role_policy = jsonencode({ Version = "2012-10-17", Statement = [{ Effect = "Allow", Principal = { Service = "lambda.amazonaws.com" }, Action = "sts:AssumeRole" }] })
}
resource "aws_iam_role_policy_attachment" "basic" {
  count      = var.enabled ? 1 : 0
  role       = aws_iam_role.this[0].name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}
resource "aws_lambda_function" "this" {
  count            = var.enabled ? 1 : 0
  function_name    = var.name
  role             = aws_iam_role.this[0].arn
  filename         = var.jar_path
  source_code_hash = var.jar_path == "" ? null : filebase64sha256(var.jar_path)
  runtime          = "java21"
  handler          = var.handler
  timeout          = 30
  memory_size      = 512
  environment {
    variables = {
      SPRING_API_URL = var.spring_api_url, SPRING_API_KEY = var.spring_api_key
    }
  }
  dynamic "vpc_config" {
    for_each = length(var.subnet_ids) > 0 ? [1] : []
    content {
      subnet_ids         = var.subnet_ids
      security_group_ids = var.security_group_ids
    }

  }
}
resource "aws_cloudwatch_event_rule" "schedule" {
  count               = var.enabled ? 1 : 0
  name                = "${var.name}-schedule"
  schedule_expression = "rate(5 minutes)"
}
resource "aws_cloudwatch_event_target" "lambda" {
  count = var.enabled ? 1 : 0
  rule  = aws_cloudwatch_event_rule.schedule[0].name
  arn   = aws_lambda_function.this[0].arn
}
resource "aws_lambda_permission" "eventbridge" {
  count         = var.enabled ? 1 : 0
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.this[0].function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.schedule[0].arn
}
