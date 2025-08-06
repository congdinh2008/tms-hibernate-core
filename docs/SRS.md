# BÀI TẬP TỔNG HỢP HIBERNATE – FRESHER JAVA
**Dự án**: Task Management System (Hệ thống Quản lý Công việc)  
**Hình thức**: Ứng dụng Console (CLI), không dùng Spring Boot  
**CSDL**: PostgreSQL  
**Build**: Maven  
**Thời lượng gợi ý**: 8-9 ngày  
**Đối tượng**: Fresher Java (muốn thử thách nâng cao)  

---

## 1. Mục tiêu học tập (Learning Outcomes)
Sau khi hoàn thành bài tập, học viên có thể:
1.  Cấu hình và vận hành Hibernate ORM với Maven (SessionFactory, Session, Transaction).
2.  Thiết kế **Layered Architecture** rõ ràng: `Main → Service (Interface/Impl) → Repository (DAO) → Entities`.
3.  Mapping thành thạo các quan hệ phức hợp: `One-to-Many`, `Many-to-One`, `Many-to-Many`, và **quan hệ tự tham chiếu (self-referencing)**. Sử dụng `@Version` cho optimistic locking.
4.  Thực hiện đầy đủ **CRUD**, **Search + Paging + Sorting** (HQL/Criteria/Native), gọi **Stored Procedure**.
5.  Áp dụng **Hibernate Validator (JSR‑380)** để đảm bảo business logic và dữ liệu đầu vào.
6.  Bật và sử dụng **Second‑Level Cache** cho entity, **Query Cache** cơ bản.
7.  Viết **JUnit 5** unit tests cho các rule quan trọng; **Mockito** (bonus) để mock repository.
8.  (Nâng cao) Ghi nhận lịch sử thay đổi (auditing) một cách có hệ thống.
9.  (Bonus) Thực hiện **Batch Processing** hiệu quả và an toàn.
10. (Bonus) Tích hợp **Spring Core** (spring‑core/beans/context) cho DI/transaction.

> **Lưu ý**: Bài này **không yêu cầu** đưa kèm code cấu hình, và **không cần** kịch bản SQL trong tài liệu nộp. Triển khai trực tiếp trong mã nguồn dự án.

---

## 2. Bài toán & Phạm vi
### 2.1 Bối cảnh
Xây dựng ứng dụng **Quản lý Công việc** cho các đội nhóm, với các đối tượng: **Project**, **User**, **Task**, và **Tag**. Ứng dụng chạy dưới dạng **CLI** cho phép quản lý các dự án, công việc, phân công và báo cáo.

### 2.2 Chức năng bắt buộc (Functional Scope)
1.  **Quản lý Dự án (Project)**
    - Thêm, sửa, xoá, xem chi tiết, liệt kê.
    - **Thêm/xoá thành viên** vào một dự án.
2.  **Quản lý Người dùng (User)**
    - Thêm, sửa, xoá, xem; tìm theo tên, email.
3.  **Quản lý Công việc (Task)**
    - Thêm, sửa, xoá, xem chi tiết.
    - **Tạo công việc con (sub-task)** của một công việc khác.
    - Tìm kiếm theo: trạng thái, độ ưu tiên, người được giao, dự án, tag.
    - **Gán (assign)** công việc cho một thành viên trong dự án.
    - **Thay đổi trạng thái** (TODO, IN_PROGRESS, DONE).
    - **Gắn/gỡ Tag** cho một công việc.
4.  **Báo cáo & Truy vấn nâng cao**
    - Liệt kê các công việc **quá hạn** (Native Query).
    - Liệt kê **lịch sử thay đổi** của một công việc (ví dụ: ai đã đổi trạng thái từ A sang B vào lúc nào).
    - **Stored Procedure**: Thống kê số lượng công việc đã hoàn thành bởi một User trong vòng X ngày qua.

---

## 3. Kiến trúc & Mô hình
### 3.1 Kiến trúc yêu cầu
- **Layered Architecture**: `Main (CLI) → Service → Repository (DAO) → Entities`.
- **Transaction boundary**: Tại **Service** cho mỗi thao tác nghiệp vụ.
- **Logging**: Sử dụng `INFO/DEBUG/WARN/ERROR` và `correlationId` (MDC).

### 3.2 Mô hình dữ liệu khái quát (Conceptual)
- **User**: id, name, email, password (lưu dạng hash), version.
- **Project**: id, name, description, startDate, version. Quan hệ *N-N* với **User**.
- **Task**: id, title, description, status (enum: TODO, IN_PROGRESS, DONE), priority (enum: HIGH, MEDIUM, LOW), dueDate, version.
    - Quan hệ *N-1* tới **Project**.
    - Quan hệ *N-1* tới **User** (assignee).
    - Quan hệ *N-N* tới **Tag**.
    - Quan hệ **tự tham chiếu N-1** tới chính nó (parentTask) để làm sub-task.
- **Tag**: id, name. Quan hệ *N-N* với **Task**.
- **(Thêm) TaskHistory**: id, changeDate, fieldChanged, oldValue, newValue.
    - Quan hệ *N-1* tới **Task**.
    - Quan hệ *N-1* tới **User** (người thực hiện thay đổi).

---

## 4. Business Rules & Validation
### 4.1 Business Rules (bắt buộc)
- **R1**: **Không xoá Project** nếu vẫn còn công việc chưa ở trạng thái `DONE`.
- **R2**: `Task.dueDate` phải **sau hoặc bằng** `Project.startDate`.
- **R3**: Chỉ có thể **gán (assign) Task** cho một **User** là thành viên của **Project** đó.
- **R4**: Một công việc **không thể là cha của chính nó** (phải kiểm tra khi tạo/cập nhật sub-task để tránh vòng lặp).
- **R5**: Khi thay đổi `status` hoặc `assignee` của một Task, một bản ghi **TaskHistory** tương ứng phải được tạo ra.
- **R6**: `User.email` phải là **duy nhất**. `Tag.name` cũng phải là **duy nhất**.
- **R7**: Sử dụng **Optimistic Locking** (`version`) để tránh mất cập nhật.
- **(Nâng cao) R8**: Chỉ người quản lý dự án (vai trò `ADMIN` trong bảng trung gian User-Project) hoặc người đang được giao việc mới có thể thay đổi `dueDate` của công việc.

### 4.2 Validation (Hibernate Validator – JSR‑380)
- **Field‑level**:
    - `name`, `title`: bắt buộc (không rỗng).
    - `email`: định dạng hợp lệ, duy nhất (custom validator).
    - `status`, `priority`, `dueDate`: bắt buộc.
- **Class‑level** (khi tạo Task):
    - `dueDate` hợp lệ (so với `startDate` của Project).
    - `assigneeId` (nếu có) phải thuộc project.

---

## 5. Service Layer – Yêu cầu chi tiết
> Mỗi phương thức Service là **một transaction**. Rollback khi có exception.

### 5.1 ProjectService
- **create(request)**
- **getById(id)**; **update(id, request)**; **delete(id)** (tuân thủ **R1**).
- **addMemberToProject(projectId, userId)**
- **removeMemberFromProject(projectId, userId)**
- **findTasksByProject(projectId, pageRequest)**

### 5.2 TaskService
- **create(request)**
- **createSubtask(parentTaskId, request)**: Tạo công việc con, đảm bảo tuân thủ **R4**.
- **getById(id)**; **update(id, request)**
- **assignTask(taskId, userId)**: Gán việc, tuân thủ **R3**. Khi gán, tạo `TaskHistory` theo **R5**.
- **changeTaskStatus(taskId, newStatus)**: Thay đổi trạng thái, tạo `TaskHistory` theo **R5**.
- **addTagToTask(taskId, tagId)**
- **removeTagFromTask(taskId, tagId)**
- **getTaskHistory(taskId)**: Trả về lịch sử thay đổi của một công việc.
- **findOverdueTasks(referenceDate, pageRequest)**: Gọi Native Query.
- **countCompletedTasksByUser(userId, days)**: Gọi Stored Procedure.
- **search(criteria, pageRequest)**: Tìm kiếm theo nhiều tiêu chí.

### 5.3 UserService & TagService
- Cung cấp các phương thức CRUD và tìm kiếm cơ bản.
- `UserService.findByEmail(email)`.
- `TagService.findOrCreate(tagName)`: Tìm tag theo tên, nếu không có thì tạo mới.

---

## 6. Repository Layer – Yêu cầu
- Tương tự đề bài gốc: chỉ truy cập dữ liệu, không quản lý transaction.
- Cung cấp các phương thức hỗ trợ kiểm tra business rule (ví dụ: `isUserMemberOfProject(userId, projectId)`).

---

## 7. Caching – Yêu cầu cơ bản
1.  Bật **Second‑Level Cache** cho **Project** và **Tag** (các entity ít thay đổi).
2.  Bật **Query Cache** cho truy vấn lấy danh sách project, danh sách tag.
3.  Trình bày ngắn gọn trong README về lựa chọn và cấu hình cache.

---

## 8. Stored Procedure – Yêu cầu
- Cung cấp một Stored Procedure có 2 tham số đầu vào: `userId` và `numberOfDays`.
- SP trả về **tổng số công việc** mà `userId` đó đã hoàn thành (chuyển sang `DONE`) trong `numberOfDays` gần nhất.
- Gọi SP qua Hibernate và ánh xạ kết quả.

---

## 9. CLI – Yêu cầu trải nghiệm
Menu gợi ý:
1.  **Dự án**: Tạo / Sửa / Xoá / Xem; Thêm/Xoá thành viên.
2.  **Công việc**: Tạo / Sửa / Xoá / Xem; Tạo việc con; Gán việc; Đổi trạng thái; Gắn/gỡ Tag.
3.  **Người dùng & Tag**: Các chức năng quản lý cơ bản.
4.  **Báo cáo**: Liệt kê việc quá hạn; Xem lịch sử thay đổi của Task; Thống kê việc hoàn thành (SP).
5.  **Thoát**.

---

## 10. Testing – Yêu cầu
### 10.1 Unit Test (bắt buộc, JUnit 5)
- Kiểm thử **Business Rules** trọng yếu:
    - Không gán việc cho người ngoài project (**R3**).
    - Logic tạo `TaskHistory` khi đổi status (**R5**).
    - Logic chống tạo vòng lặp cho sub-task (**R4**).
    - Không cho xoá project còn việc dang dở (**R1**).
- Kiểm thử **Search + Paging + Sorting**.

### 10.2 Mockito (bonus)
- Mock Repository trong Service test để kiểm tra logic nghiệp vụ độc lập.

---

## 11. Các mục Bonus, Bàn giao, Rubric
- Các mục **Batch Processing (bonus)**, **Spring Core (bonus)**, **Bàn giao & Hướng dẫn nộp bài**, và **Rubric đánh giá** có thể giữ nguyên cấu trúc và tiêu chí như trong đề bài gốc, chỉ cần điều chỉnh các mô tả nhỏ để phù hợp với ngữ cảnh "Task Management".
- Rubric cần bổ sung điểm cho việc xử lý đúng các yêu cầu phức tạp mới:
    - **Entity Mapping**: Mapping đúng quan hệ tự tham chiếu (sub-task) và bảng ghi lịch sử.
    - **Business Rules**: Áp dụng đúng các rule R4, R5, R8.

---

## 12. Checklist tự đánh giá trước khi nộp
- [ ] CRUD đầy đủ cho Project/User/Task/Tag, tuân thủ R1–R8.
- [ ] Tạo được sub-task và chống được vòng lặp.
- [ ] Ghi nhận được `TaskHistory` khi có thay đổi.
- [ ] Search + Paging + Sorting hoạt động đúng.
- [ ] Gọi Stored Procedure và Native Query thành công.
- [ ] Bật 2nd-level cache cho Project/Tag.
- [ ] JUnit 5: test các rule quan trọng.
- [ ] README rõ ràng, hướng dẫn đầy đủ.
- [ ] (Bonus) Đã hoàn thành các phần nâng cao.

---

**Kết thúc đề bài.** *Chúc bạn hoàn thành tốt và học được nhiều điều từ bài tập này!*