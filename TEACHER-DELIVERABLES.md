# QLTXM - Teacher Deliverables

## 1. Mục tiêu hoàn thành
- Kết nối GitHub với Jira thành công.
- Tạo branch và commit có issue key Jira.
- Tạo Pull Request từ branch `feature/KAN-2-login`.
- Hiển thị `branch`, `commit`, `pull request` trong Jira issue.
- Chuẩn hóa workflow theo yêu cầu thầy.

## 2. Repo và nhánh
- Repository: `https://github.com/DanTruong209/QLTXM`
- Branch chính: `main`
- Branch mẫu Jira: `feature/KAN-2-login`

## 3. Những gì đã thực hiện
- Tạo repo Git cục bộ và push lên GitHub.
- Cấu trúc commit rõ ràng theo từng phần:
  - `feat: setup Spring Boot project structure and Maven configuration`
  - `feat: add main application class and basic home controller`
  - `feat: add JPA entity models...`
  - `feat: add Spring Data JPA repositories...`
  - `feat: add business logic services...`
  - `feat: add DTO classes...`
  - `feat: add controllers...`
  - `feat: add security and data initialization`
  - `feat: add UI templates and static resources`
  - `test: add unit tests for Spring Boot application`
  - `docs: add project documentation and development guidelines`
- Kết nối GitHub với Jira trên tài khoản Jira hiện tại.
- Tạo branch `feature/KAN-2-login` với commit message chứa `KAN-2`.
- Tạo Pull Request mẫu `KAN-2 feat: add Jira branch and commit example`.

## 4. Jira / GitHub integration
- Jira đã hiển thị:
  - 1 branch
  - 1 commit
  - 1 pull request
- Điều này chứng tỏ Jira đã nhận dữ liệu từ GitHub thành công.

## 5. Workflow theo yêu cầu thầy
- Board đề xuất: `Todo` → `Dev` → `InProgress` → `Test` → `Done`
- Test có thể quay lại `InProgress` nếu cần sửa
- Branch và commit phải chứa Jira issue key để liên kết

## 6. Epic / Story / Task mẫu
- Epic: `KAN-1 Quản lý thuê xe máy`
  - Story: `KAN-2 Đăng nhập, đăng ký và xác thực người dùng`
    - Task: `KAN-3 Tạo giao diện đăng nhập/đăng ký`
    - Task: `KAN-4 Cài đặt Spring Security và xác thực`
    - Task: `KAN-5 Lưu thông tin người dùng và phân quyền`
  - Story: `KAN-6 Quản lý danh sách xe và trạng thái thuê`
    - Task: `KAN-7 Xây dựng model Motorbike và repository`
    - Task: `KAN-8 Tạo controller và view danh sách xe`
    - Task: `KAN-9 Hiển thị trạng thái xe và chi tiết xe`
  - Story: `KAN-10 Chức năng thuê và trả xe`
    - Task: `KAN-11 Tạo form thuê xe và xử lý đặt xe`
    - Task: `KAN-12 Tạo form trả xe và cập nhật thanh toán`
    - Task: `KAN-13 Tính toán tổng tiền và lưu lịch sử thuê`
  - Story: `KAN-14 Báo cáo và dashboard`
    - Task: `KAN-15 Thống kê doanh thu theo ngày/tuần`
    - Task: `KAN-16 Tạo dashboard hiển thị chỉ số chính`

## 7. Hướng dẫn review và nghiệm thu
1. Mở Jira issue `KAN-2`.
2. Kiểm tra phần `Development` và `Pull request`.
3. Nếu muốn, merge PR trên GitHub và refresh Jira.
4. Đóng issue hoặc chuyển trạng thái `Done` khi hoàn tất.

## 7. Ghi chú
- Nếu thầy cần, có thể bổ sung thêm `Epic`, `Story`, `Task` trực tiếp trên Jira.
- File này là tài liệu hoàn chỉnh để thầy đánh giá tiến trình và cách làm.
