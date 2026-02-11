BEGIN;

TRUNCATE TABLE attendance, grades, students, groups, subjects, specialty, department, semester, faculty
RESTART IDENTITY CASCADE;

INSERT INTO faculty (id, name) VALUES (1, 'Факультет математики та інформатики');
INSERT INTO faculty (id, name) VALUES (2, 'Факультет іноземних наук');
INSERT INTO faculty (id, name) VALUES (3, 'Факультет природничих наук');
INSERT INTO faculty (id, name) VALUES (4, 'Факультет філософії');
INSERT INTO faculty (id, name) VALUES (5, 'Факультет історії та археології');

INSERT INTO department (id, name, faculty_id) VALUES (1, 'Кафедра програмної інженерії', 1);
INSERT INTO department (id, name, faculty_id) VALUES (7, 'Кафедра прикладної лінгвістики', 2);
INSERT INTO department (id, name, faculty_id) VALUES (8, 'Кафедра біології та екології', 3);
INSERT INTO department (id, name, faculty_id) VALUES (9, 'Кафедра філософії та етики', 4);
INSERT INTO department (id, name, faculty_id) VALUES (10, 'Кафедра історії та археології', 5);

INSERT INTO specialty (id, code, name, department_id) VALUES (1, '121', 'Інженерія програмного забезпечення', 1);
INSERT INTO specialty (id, code, name, department_id) VALUES (7, '035', 'Філологія', 7);
INSERT INTO specialty (id, code, name, department_id) VALUES (8, '091', 'Біологія', 8);
INSERT INTO specialty (id, code, name, department_id) VALUES (9, '033', 'Філософія', 9);
INSERT INTO specialty (id, code, name, department_id) VALUES (10, '032', 'Історія та археологія', 10);

INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (1, 'ІПЗ-31', 3, 1);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (2, 'ІПЗ-32', 3, 1);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (3, 'ІПЗ-33', 3, 1);

INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (25, 'ФІН-31', 3, 7);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (26, 'ПРН-31', 3, 8);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (27, 'ФІЛ-31', 3, 9);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (28, 'ІСТ-31', 3, 10);

INSERT INTO semester (id, name) VALUES ('SEM1', 'Перший семестр');
INSERT INTO semester (id, name) VALUES ('SEM2', 'Другий семестр');

INSERT INTO subjects (id, name, ects, category) VALUES (1, 'Математичний аналіз', 6, 'обов''язкова');
INSERT INTO subjects (id, name, ects, category) VALUES (2, 'Лінійна алгебра та аналітична геометрія', 5, 'обов''язкова');
INSERT INTO subjects (id, name, ects, category) VALUES (3, 'Дискретна математика', 5, 'обов''язкова');
INSERT INTO subjects (id, name, ects, category) VALUES (4, 'Алгоритми та структури даних', 6, 'обов''язкова');
INSERT INTO subjects (id, name, ects, category) VALUES (5, 'Бази даних', 5, 'обов''язкова');
INSERT INTO subjects (id, name, ects, category) VALUES (6, 'Об''єктно-орієнтоване програмування', 6, 'обов''язкова');
INSERT INTO subjects (id, name, ects, category) VALUES (7, 'Операційні системи', 4, 'обов''язкова');
INSERT INTO subjects (id, name, ects, category) VALUES (8, 'Комп''ютерні мережі', 4, 'обов''язкова');
INSERT INTO subjects (id, name, ects, category) VALUES (9, 'Веб-технології', 4, 'вибіркова');
INSERT INTO subjects (id, name, ects, category) VALUES (10, 'Теорія ймовірностей та математична статистика', 5, 'обов''язкова');

INSERT INTO students (id, full_name, form_of_study, status, group_id)
SELECT
    i,
    (
      ARRAY[
        'Коваленко','Шевченко','Бондаренко','Мельничук','Ткаченко','Поліщук','Романюк','Лисенко','Іваненко','Олійник',
        'Гриценко','Петренко','Савчук','Бойчук','Дячук','Тимченко','Захаренко','Левченко','Кравець','Марчук'
      ]
    )[1 + (person_idx % 20)] || ' ' ||
    (
      CASE
        WHEN is_female THEN
          (ARRAY[
            'Олена','Марія','Софія','Катерина','Вікторія','Поліна','Юлія','Ірина','Оксана','Ганна',
            'Анастасія','Дарина','Наталія','Лілія','Тетяна','Христина','Ярина','Валерія','Аліна','Діана'
          ])[1 + ((person_idx / 20) % 20)]
        ELSE
          (ARRAY[
            'Андрій','Олексій','Максим','Денис','Богдан','Тарас','Роман','Владислав','Юрій','Ігор',
            'Назар','Михайло','Євген','Степан','Остап','Віталій','Арсен','Павло','Кирило','Марко'
          ])[1 + ((person_idx / 20) % 20)]
      END
    ) || ' ' ||
    (
      CASE
        WHEN is_female THEN
          (ARRAY[
            'Олександрівна','Петрівна','Ігорівна','Сергіївна','Андріївна','Тарасівна','Василівна','Романівна','Миколаївна','Юріївна',
            'Богданівна','Володимирівна','Михайлівна','Олегівна','Павлівна','Степанівна','Віталіївна','Євгенівна','Іванівна','Дмитрівна'
          ])[1 + ((person_idx / 400) % 20)]
        ELSE
          (ARRAY[
            'Олександрович','Петрович','Ігорович','Сергійович','Андрійович','Тарасович','Васильович','Романович','Миколайович','Юрійович',
            'Богданович','Володимирович','Михайлович','Олегович','Павлович','Степанович','Віталійович','Євгенович','Іванович','Дмитрович'
          ])[1 + ((person_idx / 400) % 20)]
      END
    ),
    CASE WHEN i % 3 = 0 THEN 'CONTRACT' ELSE 'BUDGET' END,
    'ACTIVE',
    CASE
      WHEN i BETWEEN 1 AND 30 THEN 1
      WHEN i BETWEEN 31 AND 60 THEN 2
      ELSE 3
    END
FROM (
    SELECT
      i,
      (i % 2 = 0) AS is_female,
      CASE
        WHEN i % 2 = 0 THEN ((i / 2) - 1)
        ELSE ((i + 1) / 2) - 1
      END AS person_idx
    FROM generate_series(1, 90) i
) generated;

INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (91, 'Мельник Олексій', 'BUDGET', 'ACTIVE', 25);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (92, 'Кравченко Дарина', 'CONTRACT', 'ACTIVE', 25);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (93, 'Шаповал Марія', 'BUDGET', 'ACTIVE', 25);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (94, 'Тимченко Ігор', 'CONTRACT', 'ACTIVE', 25);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (95, 'Захаренко Софія', 'BUDGET', 'ACTIVE', 25);

INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (96, 'Романюк Антон', 'BUDGET', 'ACTIVE', 26);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (97, 'Ковтун Олена', 'CONTRACT', 'ACTIVE', 26);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (98, 'Гнатюк Максим', 'BUDGET', 'ACTIVE', 26);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (99, 'Левченко Поліна', 'CONTRACT', 'ACTIVE', 26);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (100, 'Яценко Владислав', 'BUDGET', 'ACTIVE', 26);

INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (101, 'Білик Назар', 'BUDGET', 'ACTIVE', 27);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (102, 'Дорошенко Катерина', 'CONTRACT', 'ACTIVE', 27);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (103, 'Паламарчук Юлія', 'BUDGET', 'ACTIVE', 27);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (104, 'Клименко Артем', 'CONTRACT', 'ACTIVE', 27);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (105, 'Литвин Марко', 'BUDGET', 'ACTIVE', 27);

INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (106, 'Мороз Вікторія', 'BUDGET', 'ACTIVE', 28);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (107, 'Шевчук Денис', 'CONTRACT', 'ACTIVE', 28);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (108, 'Кузьменко Ірина', 'BUDGET', 'ACTIVE', 28);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (109, 'Остапенко Богдан', 'CONTRACT', 'ACTIVE', 28);
INSERT INTO students (id, full_name, form_of_study, status, group_id) VALUES (110, 'Стеценко Ангеліна', 'BUDGET', 'ACTIVE', 28);

WITH base AS (
    SELECT
        s.id AS student_id,
        sem.semester_id,
        subj.subject_id
    FROM students s
    JOIN (VALUES ('SEM1'), ('SEM2')) sem(semester_id) ON TRUE
    JOIN generate_series(1, 10) subj(subject_id) ON TRUE
),
calc AS (
    SELECT
        ROW_NUMBER() OVER (ORDER BY student_id, semester_id, subject_id) AS rn,
        student_id,
        semester_id,
        subject_id,
        CASE
            WHEN student_id <= 90 AND student_id % 9 = 0 AND subject_id IN (3, 7)
                THEN 35 + ((student_id + subject_id) % 10) -- 35..44
            WHEN student_id <= 90 AND student_id % 10 = 0 AND semester_id = 'SEM2' AND subject_id IN (1, 5)
                THEN 40 + ((student_id + subject_id) % 8) -- 40..47
            ELSE 55 + ((student_id * 7 + subject_id * 11 + CASE WHEN semester_id = 'SEM2' THEN 13 ELSE 0 END) % 46)
        END AS score,
        60 + ((student_id * 5 + subject_id * 3 + CASE WHEN semester_id = 'SEM2' THEN 9 ELSE 0 END) % 41) AS attendance_percentage
    FROM base
)
INSERT INTO grades (id, student_id, subject_id, semester_id, score, attempt, date_entered)
SELECT
    rn,
    student_id,
    subject_id,
    semester_id,
    score,
    1,
    CASE
        WHEN semester_id = 'SEM1' THEN DATE '2025-11-01' + ((rn % 60) * INTERVAL '1 day')
        ELSE DATE '2026-03-01' + ((rn % 90) * INTERVAL '1 day')
    END::date
FROM calc;

WITH base AS (
    SELECT
        s.id AS student_id,
        sem.semester_id,
        subj.subject_id
    FROM students s
    JOIN (VALUES ('SEM1'), ('SEM2')) sem(semester_id) ON TRUE
    JOIN generate_series(1, 10) subj(subject_id) ON TRUE
),
calc AS (
    SELECT
        ROW_NUMBER() OVER (ORDER BY student_id, semester_id, subject_id) AS rn,
        student_id,
        semester_id,
        subject_id,
        60 + ((student_id * 5 + subject_id * 3 + CASE WHEN semester_id = 'SEM2' THEN 9 ELSE 0 END) % 41) AS attendance_percentage
    FROM base
)
INSERT INTO attendance (id, student_id, subject_id, semester_id, percentage)
SELECT
    rn,
    student_id,
    subject_id,
    semester_id,
    attendance_percentage
FROM calc;

COMMIT;
