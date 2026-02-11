# Student Ranking System

Веб-система аналітики успішності студентів.
Проєкт показує не тільки оцінки, а й аналітику: рейтинг, ризики боргів, тренд, кореляцію відвідуваності з результатами та what-if прогноз.

## Stack

- Backend: `Java + Spring Boot + Spring Data JPA`
- Frontend: `React`
- Database: `PostgreSQL 15`
- Infra: `Docker Compose`

## Основний функціонал

- Дашборд студента:
  - середній бал
  - місце в рейтингу потоку
  - кредити (ECTS)
  - статус боргів
  - порівняння з групою
  - тренд успішності
- Рейтинг:
  - scope: `STREAM`, `FACULTY`, `UNIVERSITY`
  - сортування: `PERFORMANCE`, `ALPHABET`, `GROUP`
  - фільтри: семестр, факультет, форма навчання, борги
  - окремі режими: тільки боржники / тільки без боргів
- Аналітика:
  - weighted GPA
  - risk (кількість боргів)
  - trend (UP / DOWN / STABLE)
  - attendance-performance correlation (Pearson)
  - what-if калькулятор
  - класифікація `TOP / MIDDLE / RISK`

## Структура даних

Ієрархія:

`Faculty -> Department -> Specialty -> Group -> Students`

Також:

- `Semester`
- `Subjects` (з `ects`)
- `Grades` (з `attempt`, береться latest-attempt)
- `Attendance`

## Демо-дані

Ініціалізація налаштована через:

- `src/main/resources/application.properties`
- `spring.sql.init.data-locations=classpath:data_extra.sql`

`data_extra.sql` створює:

- 5 факультетів
- потік `ІПЗ` (`ІПЗ-31`, `ІПЗ-32`, `ІПЗ-33`) по 30 студентів
- додаткові факультети з тестовими студентами
- оцінки та відвідуваність на `SEM1`/`SEM2`
- частину студентів з боргами (`score < 50`) для перевірки ризиків

## Запуск

### 1) Підняти БД

```bash
docker compose up -d
```

### 2) Запустити backend

```bash
bash ./mvnw spring-boot:run
```

Backend: `http://localhost:8080`

### 3) Запустити frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend: `http://localhost:5173`

`Vite` проксить `/api` на `http://localhost:8080` (`frontend/vite.config.js`).

## Демо-вхід

На сторінці `/login` використовується демо-логіка фронта:

## API (основні ендпоїнти)

### Health

- `GET /api/health`

### Ranking

- `GET /api/ranking/top?semesterId=SEM1&limit=10`
- `GET /api/ranking/list`
  - параметри:
    - `semesterId=SEM1|SEM2`
    - `scope=STREAM|FACULTY|UNIVERSITY`
    - `facultyId=<id>` (обов'язково для `FACULTY`)
    - `formOfStudy=BUDGET|CONTRACT`
    - `debtFilter=ALL|WITH_DEBTS|WITHOUT_DEBTS`
    - `sortBy=PERFORMANCE|ALPHABET|GROUP`
    - `direction=ASC|DESC`
    - `limit=<n>`

### Analytics

- `GET /api/analytics/weighted-gpa?studentId=61&semesterId=SEM1`
- `GET /api/analytics/risk?studentId=61&semesterId=SEM1`
- `GET /api/analytics/trend?studentId=61`
- `GET /api/analytics/correlation?studentId=61&semesterId=SEM1`
- `GET /api/analytics/classification?semesterId=SEM1`
- `POST /api/analytics/what-if`

Приклад body:

```json
{
  "studentId": 61,
  "semesterId": "SEM1",
  "expectedScores": [
    { "subjectId": 1, "expectedScore": 95 },
    { "subjectId": 2, "expectedScore": 92 }
  ]
}
```

### Comparison

- `GET /api/comparison/student?studentId=61&semesterId=SEM1`

### Raw data

- `GET /api/students`
- `GET /api/students/{id}`
- `GET /api/subjects`
- `GET /api/semesters`
- `GET /api/faculties`
- `GET /api/grades`
- `GET /api/attendance`

## Швидка перевірка

```bash
curl -s "http://localhost:8080/api/health"
curl -s "http://localhost:8080/api/ranking/list?semesterId=SEM1&scope=STREAM&sortBy=PERFORMANCE&direction=DESC&debtFilter=ALL&limit=20"
curl -s "http://localhost:8080/api/ranking/list?semesterId=SEM1&scope=STREAM&debtFilter=WITH_DEBTS&limit=20"
curl -s "http://localhost:8080/api/analytics/weighted-gpa?studentId=61&semesterId=SEM1"
```

## Команда

- Третяк Мирослав — `ІПЗ-33`
- Яворський Владислав — `ІПЗ-33`
- Семкович Денис — `ІПЗ-33`
- Шевчук Мирослав — `ІПЗ-33`
- Орихівський Роман — `ІПЗ-32`

