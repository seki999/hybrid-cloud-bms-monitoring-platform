# TCP Ping 本地模拟器

独立二进制不是必需的。`scripts/run-tcp-ping.ps1` / `.sh` 调用 Spring Boot API，Socket 连接本身由 Java `TcpPingService` 执行，因此结果、错误分类、重试次数与 Web 画面使用同一业务路径。

