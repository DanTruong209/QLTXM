# REST API

- Base URL: `http://localhost:8081`
- Public endpoint: `GET /api/tracking?bookingCode=BOOK-000001`
- Admin endpoints require login with `ROLE_ADMIN`

## Endpoints
- `GET /api/customers?q=keyword`
- `GET /api/customers/{id}`
- `POST /api/customers`
- `PUT /api/customers/{id}`
- `GET /api/motorbikes?q=keyword&status=AVAILABLE`
- `GET /api/motorbikes/{id}`
- `POST /api/motorbikes`
- `PUT /api/motorbikes/{id}`
- `GET /api/rentals?q=keyword&status=PENDING`
- `GET /api/rentals/{id}`
- `POST /api/rentals`
- `PUT /api/rentals/{id}`
- `POST /api/rentals/{id}/approve`
- `POST /api/rentals/{id}/complete`
- `POST /api/rentals/{id}/cancel`
- `POST /api/rentals/{id}/reject`
- `GET /api/tracking?bookingCode=BOOK-000001`

## Sample payloads

### Create customer
```json
{
  "fullName": "Nguyen Van A",
  "phone": "0901234567",
  "idCard": "079123456789",
  "address": "Da Nang",
  "notes": "Khach than thiet"
}
```

### Create motorbike
```json
{
  "code": "XM-001",
  "brand": "Honda",
  "model": "Vision",
  "licensePlate": "43A-12345",
  "dailyRate": 150000,
  "status": "AVAILABLE",
  "locationLabel": "Da Nang"
}
```

### Create rental
```json
{
  "customerId": 1,
  "motorbikeId": 1,
  "startDate": "2026-04-17",
  "endDate": "2026-04-19",
  "depositAmount": 200000,
  "notes": "Giao xe tai trung tam"
}
```

### Complete rental
```json
{
  "actualReturnDate": "2026-04-19",
  "extraFee": 0,
  "returnNotes": "Tra xe dung hen"
}
```

### Reject rental
```json
{
  "reason": "Thong tin dat xe chua hop le"
}
```
