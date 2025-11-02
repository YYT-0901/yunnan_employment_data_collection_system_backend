# Redis连接问题修复

## 问题诊断

从日志中看到：
```
Redis连接失败，请检查Redis配置！
Unable to connect to Redis
Connection refused: getsockopt: /127.0.0.1:6379
```

**根本原因**：Redis服务没有启动或无法连接。

## 解决方案

### 方案1：启动Redis服务（推荐）

#### Windows系统：

1. **检查Redis是否安装**
   ```powershell
   # 查找Redis安装位置
   Get-Command redis-server -ErrorAction SilentlyContinue
   ```

2. **启动Redis服务**
   
   **方法A：使用服务方式启动（如果已安装为服务）**
   ```powershell
   # 启动Redis服务
   net start Redis
   # 或
   sc start Redis
   ```

   **方法B：手动启动Redis**
   ```powershell
   # 找到Redis安装目录，然后运行
   redis-server.exe
   # 或者指定配置文件
   redis-server.exe redis.windows.conf
   ```

   **方法C：如果使用Docker**
   ```powershell
   docker run -d -p 6379:6379 redis
   ```

3. **验证Redis是否启动**
   ```powershell
   # 测试连接
   redis-cli ping
   # 应该返回：PONG
   ```

### 方案2：检查Redis配置

如果Redis已安装但连接失败，检查配置：

#### 检查配置文件
```yaml
# yunnan-enterprise/src/main/resources/application.yml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 1
```

确保：
- Redis监听在 `127.0.0.1:6379`
- 防火墙允许6379端口
- Redis没有设置密码（或配置文件中设置了密码）

### 方案3：临时禁用Redis（不推荐）

如果暂时无法启动Redis，可以修改代码跳过Redis，但**不推荐**，因为token存储依赖Redis。

## 快速验证

### 1. 检查Redis是否运行
```powershell
# PowerShell
Test-NetConnection -ComputerName 127.0.0.1 -Port 6379
```

如果 `TcpTestSucceeded` 为 `False`，说明Redis未启动。

### 2. 尝试连接Redis
```powershell
# 如果安装了redis-cli
redis-cli ping
```

## Redis下载和安装

如果没有安装Redis：

### Windows安装方式：

1. **使用WSL（推荐）**
   ```powershell
   wsl --install
   # 然后在WSL中安装Redis
   wsl sudo apt-get install redis-server
   wsl sudo service redis-server start
   ```

2. **下载Windows版本**
   - 下载地址：https://github.com/microsoftarchive/redis/releases
   - 解压后运行 `redis-server.exe`

3. **使用Docker（如果有Docker）**
   ```powershell
   docker pull redis
   docker run -d -p 6379:6379 --name redis redis
   ```

## 修复后验证

启动Redis后，重启yunnan-enterprise服务：

```powershell
cd a-yunnan\yunnan_employment_data_collection_system_backend-master\yunnan-enterprise
.\mvnw.cmd spring-boot:run
```

启动日志应该显示：
```
项目启动成功,开启愉快的开发之旅吧!(卓越2组加油)
```

而不是：
```
Redis连接失败，请检查Redis配置！
```

## 注意事项

1. **Redis必须运行**：登录功能依赖Redis存储token
2. **端口6379**：确保6379端口没有被其他程序占用
3. **防火墙**：确保防火墙允许6379端口
4. **持久化**：建议配置Redis持久化，避免重启后数据丢失

