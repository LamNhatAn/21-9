# Spring Security Examples

Project hoàn toàn độc lập với bài CRUD trước, minh họa:

1. Login bang email (vi du 1).
2. Custom login bang username hoac email, hien thi thong tin user (vi du 2).
3. Register + OTP, forgot password + OTP, session security, CRUD user/product, tim kiem phan trang va upload anh local/Cloudinary (vi du 3).

Mặc định chạy với H2 file database, port `8081`. Có thể thay bằng SQL Server qua biến môi trường trong `application.properties`.

Tài khoản admin mặc định: `admin@example.com` / `123456`.

## Chạy project

Yêu cầu JDK 22 và Maven 3.9+:

```powershell
mvn spring-boot:run
```

Các URL chính:

- `http://localhost:8081/login`: đăng nhập bằng username hoặc email.
- `http://localhost:8081/register`: đăng ký và xác thực OTP.
- `http://localhost:8081/forgot-password`: đặt lại mật khẩu bằng OTP.
- `http://localhost:8081/admin/products`: CRUD sản phẩm dành cho ADMIN.
