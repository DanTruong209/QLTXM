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

## Liên kết GitHub với Jira
1. Trên Jira, vào `Settings` -> `Apps` hoặc `Integrations`.
2. Chọn `GitHub` hoặc `DVCS accounts` rồi `Connect GitHub account`.
3. Cấp quyền cho Jira truy cập repository `DanTruong209/QLTXM`.
4. Sau khi kết nối xong, Jira sẽ tự động hiển thị commit/branch liên quan đến issue.

### Quy tắc đặt tên branch và commit
- Tên branch nên có key issue Jira: `feature/QLTXM-1-add-login`
- Commit message nên bắt đầu bằng key issue: `QLTXM-1 feat: add login page`

### Ví dụ commit message
- `QLTXM-1 feat: add login page`
- `QLTXM-2 fix: correct rental date calculation`
- `QLTXM-3 chore: update README with Jira workflow`

### Smart commits (nếu Jira hỗ trợ)
- `QLTXM-1 #comment Completed login UI #time 2h`
- `QLTXM-1 #done`

### Ví dụ với project key Jira `KAN`
- Branch: `feature/KAN-2-login`
- Commit: `KAN-2 feat: add login page`
- Pull request title: `KAN-2 add login page`

> Lưu ý: để Jira liên kết đúng, phải dùng issue key chính xác từ Jira trong branch/commit message.
