## Quy tắc Tổng thể và Bối cảnh Dự án

**Dự án:** Hệ thống Quản lý Công việc (Task Management System).
**Nền tảng:** Ứng dụng Console (CLI) bằng Java, không dùng Spring Boot (trừ khi làm bonus).
**Công nghệ chính:** Java, Hibernate, PostgreSQL, Maven.
**Đối tượng hỗ trợ:** Fresher Java muốn thực hiện bài toán nâng cao.

**QUY TẮC QUAN TRỌNG NHẤT:** Mọi gợi ý và mã nguồn được tạo ra phải **TUÂN THỦ NGHIÊM NGẶT** tài liệu SRS đã cung cấp. Tài liệu này là nguồn chân lý duy nhất cho mọi yêu cầu về kiến trúc, nghiệp vụ và tính năng.

---

## 1. Kiến trúc Bắt buộc (Layered Architecture)

Luôn tuân thủ kiến trúc phân lớp đã được định nghĩa. Không được trộn lẫn trách nhiệm giữa các lớp.

### Lớp Main (CLI)
- **Trách nhiệm:** Chỉ xử lý việc hiển thị menu, nhận đầu vào từ người dùng (ví dụ: `Scanner`), và in kết quả ra console.
- Gọi các phương thức của **Service Layer** để thực thi nghiệp vụ.
- Thiết lập `correlationId` qua MDC cho mỗi luồng thao tác của người dùng.

### Lớp Service (Interface và Impl)
- **Trách nhiệm:** Chứa **TOÀN BỘ** logic nghiệp vụ (business logic).
- Đây là **ranh giới của transaction**. Mỗi phương thức public trong Service là một transaction.
- Áp dụng các Business Rules (R1-R8) tại đây.

### Lớp Repository (DAO)
- **Trách nhiệm:** Chỉ thực hiện các thao tác truy cập dữ liệu (CRUD, queries) bằng Hibernate.
- **KHÔNG** được tự quản lý transaction (không gọi `session.beginTransaction()`, `commit()`, `rollback`).
- Cung cấp các phương thức hỗ trợ kiểm tra business rule, ví dụ: `isUserMemberOfProject(userId, projectId)`.

---

## 2. Mô hình Dữ liệu và Hibernate Mapping

- **Quan hệ:** Ánh xạ chính xác các quan hệ `One-to-Many`, `Many-to-Many`.
- **Quan hệ Tự tham chiếu:** Đặc biệt chú ý đến quan hệ **tự tham chiếu N-1** của `Task` tới chính nó (`parentTask`) để triển khai sub-task.
- **Auditing (Ghi log):** Ánh xạ entity `TaskHistory` và quan hệ N-1 của nó tới `Task` và `User`.
- **Optimistic Locking:** **BẮT BUỘC** sử dụng `@Version` trên tất cả các entity chính (`User`, `Project`, `Task`) để tránh mất mát cập nhật đồng thời.

---

## 3. Lớp Service và Business Rules

- **Transaction:** Mỗi phương thức public phải là một transaction. Rollback khi có `RuntimeException` hoặc các ngoại lệ nghiệp vụ.
- **Thực thi Business Rules:** Luôn kiểm tra và áp dụng các quy tắc sau trước khi thực hiện hành động:
  - **R1:** Kiểm tra tất cả `Task` của `Project` phải ở trạng thái `DONE` trước khi cho phép xóa `Project`.
  - **R2:** `Task.dueDate` phải sau hoặc bằng `Project.startDate`.
  - **R3:** Validate `User` là thành viên của `Project` trước khi gán `Task`.
  - **R4:** Ngăn chặn vòng lặp khi tạo/cập nhật sub-task (một công việc không thể là cha của chính nó hoặc của tổ tiên nó).
  - **R5:** **Tự động tạo** một bản ghi `TaskHistory` khi `status` hoặc `assignee` của `Task` thay đổi.
  - **R6:** Kiểm tra `User.email` và `Tag.name` phải là duy nhất.
- **Ngoại lệ (Exceptions):** Ném các ngoại lệ tùy chỉnh, rõ ràng như `BusinessRuleViolationException`, `CircularDependencyException`, `ResourceNotFoundException`.

---

## 4. Repository, Truy vấn Nâng cao và Caching

- **Truy vấn:**
  - Mặc định sử dụng **HQL** hoặc **Criteria API** cho các truy vấn tìm kiếm, phân trang, sắp xếp.
  - Chỉ sử dụng **Native Query** cho báo cáo "liệt kê các công việc quá hạn".
- **Stored Procedure:**
  - Cung cấp phương thức gọi Stored Procedure `countCompletedTasksByUser(userId, days)` và ánh xạ kết quả trả về (tổng số công việc).
- **Caching:**
  - Bật **Second-Level Cache** (sử dụng `@Cache`) cho entity `Project` và `Tag`.
  - Bật **Query Cache** cho các truy vấn lấy danh sách Project và danh sách Tag.

---

## 5. Validation và Logging

- **Validation (JSR-380):**
  - Sử dụng các annotation của Hibernate Validator cho các trường (`@NotEmpty`, `@Email`).
  - Tạo **custom validator** để kiểm tra tính duy nhất của email.
  - Tạo **class-level validator** cho `Task` để kiểm tra các rule phức tạp như `dueDate` vs `project.startDate` và `assignee` phải thuộc project.
- **Logging (SLF4J):**
  - **MDC:** Luôn thiết lập `correlationId` cho mỗi luồng xử lý chính từ CLI.
  - **Mức log:**
    - `INFO`: Log các sự kiện thành công (tạo, cập nhật, gán việc).
    - `DEBUG`: Log chi tiết tham số đầu vào của các phương thức quan trọng.
    - `WARN`: Log khi một business rule bị vi phạm.
    - `ERROR`: Log tất cả các exception không mong muốn kèm stack trace.

---

## 6. Testing (JUnit 5)

- Yêu cầu tạo unit test cho các business rule quan trọng sau:
  - Logic chống gán việc cho người ngoài project (R3).
  - Logic tự động tạo `TaskHistory` khi đổi status (R5).
  - Logic chống tạo vòng lặp cho sub-task (R4).
  - Logic không cho xóa project còn việc dang dở (R1).
- **Bonus Mockito:** Khi viết test cho Service, hãy mock Repository bằng Mockito để chỉ kiểm tra logic nghiệp vụ.

---

## 7. Hỗ trợ các tính năng Bonus

- **Spring Core:** Nếu tích hợp, hãy sử dụng **Java-based configuration** (`@Configuration`, `@Bean`). Dùng Spring cho **Dependency Injection (DI)** và quản lý transaction khai báo (`@Transactional` trên các phương thức của Service).
- **Batch Processing:** Khi tạo các hàm xử lý theo lô, hãy nhớ gọi `session.flush()` và `session.clear()` sau mỗi `batchSize` để tránh lỗi `OutOfMemoryError`.