# QLTXM

Ứng dụng quản lý thuê xe máy phát triển bằng Spring Boot.

## Mục tiêu
- Quản lý xe, khách hàng, thuê trả xe
- Đăng ký / đăng nhập người dùng
- Bảng điều khiển và báo cáo

## Cấu trúc Git
- `main`: mã chính ổn định
- `develop`: nhánh tích hợp tính năng
- `feature/*`: nhánh cho từng chức năng
- `hotfix/*`: nhánh sửa lỗi gấp

## Quy trình làm việc đề xuất
1. Tạo issue/epic trên GitHub/Jira
2. Tạo branch feature: `feature/<ten-tinh-nang>`
3. Commit rõ ràng:
   - `feat: ...`
   - `fix: ...`
   - `chore: ...`
4. Merge về `develop`, sau đó về `main`

## Hướng dẫn push
```bash
git remote add origin https://github.com/DanTruong209/QLTXM.git
git branch -M main
git push -u origin main
```
