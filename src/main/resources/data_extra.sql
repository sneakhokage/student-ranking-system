BEGIN;

TRUNCATE TABLE attendance, grades, students, groups, subjects, specialty, department, semester, faculty
RESTART IDENTITY CASCADE;

INSERT INTO faculty (id, name) VALUES (1, 'Факультет математики та інформатики');
INSERT INTO faculty (id, name) VALUES (2, 'Факультет іноземних наук');
INSERT INTO faculty (id, name) VALUES (3, 'Факультет природничих наук');
INSERT INTO faculty (id, name) VALUES (4, 'Факультет філософії');
INSERT INTO faculty (id, name) VALUES (5, 'Факультет історії та археології');

INSERT INTO department (id, name, faculty_id) VALUES (1, 'Кафедра програмної інженерії', 1);
INSERT INTO department (id, name, faculty_id) VALUES (2, 'Кафедра комп''ютерних наук', 1);
INSERT INTO department (id, name, faculty_id) VALUES (3, 'Кафедра прикладної математики', 1);
INSERT INTO department (id, name, faculty_id) VALUES (4, 'Кафедра системного моделювання', 1);
INSERT INTO department (id, name, faculty_id) VALUES (5, 'Кафедра прикладної лінгвістики', 2);
INSERT INTO department (id, name, faculty_id) VALUES (6, 'Кафедра біології та екології', 3);
INSERT INTO department (id, name, faculty_id) VALUES (7, 'Кафедра філософії та етики', 4);
INSERT INTO department (id, name, faculty_id) VALUES (8, 'Кафедра історії та археології', 5);

INSERT INTO specialty (id, code, name, department_id) VALUES (1, '121', 'Інженерія програмного забезпечення', 1);
INSERT INTO specialty (id, code, name, department_id) VALUES (2, '122', 'Комп''ютерні науки', 2);
INSERT INTO specialty (id, code, name, department_id) VALUES (3, '113', 'Прикладна математика', 3);
INSERT INTO specialty (id, code, name, department_id) VALUES (4, '124', 'Системний аналіз та моделювання', 4);
INSERT INTO specialty (id, code, name, department_id) VALUES (5, '035', 'Філологія', 5);
INSERT INTO specialty (id, code, name, department_id) VALUES (6, '091', 'Біологія', 6);
INSERT INTO specialty (id, code, name, department_id) VALUES (7, '033', 'Філософія', 7);
INSERT INTO specialty (id, code, name, department_id) VALUES (8, '032', 'Історія та археологія', 8);

INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (1, 'ІПЗ-31', 3, 1);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (2, 'ІПЗ-32', 3, 1);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (3, 'ІПЗ-33', 3, 1);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (4, 'КН-31', 3, 2);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (5, 'КН-32', 3, 2);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (6, 'ПМ-31', 3, 3);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (7, 'СОМ-31', 3, 4);

INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (8, 'ФІН-31', 3, 5);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (9, 'ПРН-31', 3, 6);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (10, 'ФІЛ-31', 3, 7);
INSERT INTO groups (id, name, year_of_study, specialty_id) VALUES (11, 'ІСТ-31', 3, 8);

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
    (ARRAY[
      'Коваленко','Шевченко','Бондаренко','Мельник','Ткаченко','Поліщук','Романюк','Лисенко','Іваненко','Олійник',
      'Гриценко','Петренко','Савчук','Бойко','Дячук','Тимченко','Захаренко','Левченко','Кравець','Марчук',
      'Клименко','Сидоренко','Паламарчук','Білик','Кулик','Чумак','Стеценко','Власенко','Яремчук','Остапенко',
      'Козак','Луценко','Шаповал','Гнатюк','Яценко','Кириченко','Павленко','Мороз','Черненко','Литвин'
    ])[1 + ((person_idx * 3) % 40)] || ' ' ||
    (
      CASE
        WHEN is_female THEN
          (ARRAY[
            'Олена','Марія','Софія','Катерина','Вікторія','Поліна','Юлія','Ірина','Оксана','Ганна',
            'Анастасія','Дарина','Наталія','Лілія','Тетяна','Христина','Ярина','Валерія','Аліна','Діана',
            'Марта','Надія','Орися','Світлана','Леся','Олександра','Ілона','Соломія','Зоряна','Вероніка'
          ])[1 + ((person_idx * 5) % 30)]
        ELSE
          (ARRAY[
            'Андрій','Олексій','Максим','Денис','Богдан','Тарас','Роман','Владислав','Юрій','Ігор',
            'Назар','Михайло','Євген','Степан','Остап','Віталій','Арсен','Павло','Кирило','Марко',
            'Ярослав','Володимир','Ростислав','Валентин','Дмитро','Олег','Сергій','Василь','Ілля','Тимур'
          ])[1 + ((person_idx * 7) % 30)]
      END
    ) || ' ' ||
    (
      CASE
        WHEN is_female THEN
          (ARRAY[
            'Олександрівна','Петрівна','Ігорівна','Сергіївна','Андріївна','Тарасівна','Василівна','Романівна','Миколаївна','Юріївна',
            'Богданівна','Володимирівна','Михайлівна','Олегівна','Павлівна','Степанівна','Віталіївна','Євгенівна','Іванівна','Дмитрівна',
            'Арсенівна','Ярославівна','Ростиславівна','Валентинівна','Іллівна'
          ])[1 + ((person_idx * 11) % 25)]
        ELSE
          (ARRAY[
            'Олександрович','Петрович','Ігорович','Сергійович','Андрійович','Тарасович','Васильович','Романович','Миколайович','Юрійович',
            'Богданович','Володимирович','Михайлович','Олегович','Павлович','Степанович','Віталійович','Євгенович','Іванович','Дмитрович',
            'Арсенович','Ярославович','Ростиславович','Валентинович','Ілліч'
          ])[1 + ((person_idx * 13) % 25)]
      END
    ),
    CASE WHEN i % 4 = 0 OR i % 9 = 0 THEN 'CONTRACT' ELSE 'BUDGET' END,
    'ACTIVE',
    CASE
      WHEN i BETWEEN 1 AND 30 THEN 1
      WHEN i BETWEEN 31 AND 60 THEN 2
      WHEN i BETWEEN 61 AND 90 THEN 3
      WHEN i BETWEEN 91 AND 115 THEN 4
      WHEN i BETWEEN 116 AND 140 THEN 5
      WHEN i BETWEEN 141 AND 165 THEN 6
      WHEN i BETWEEN 166 AND 190 THEN 7
      WHEN i BETWEEN 191 AND 215 THEN 8
      WHEN i BETWEEN 216 AND 240 THEN 9
      WHEN i BETWEEN 241 AND 265 THEN 10
      ELSE 11
    END
FROM (
    SELECT
      i,
      (i % 2 = 0) AS is_female,
      i - 1 AS person_idx
    FROM generate_series(1, 290) i
) generated;

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
            WHEN student_id % 17 = 0 AND subject_id IN (3, 7)
                THEN 32 + ((student_id + subject_id * 2) % 15)
            WHEN student_id % 23 = 0 AND semester_id = 'SEM2' AND subject_id IN (1, 5, 9)
                THEN 35 + ((student_id + subject_id) % 15)
            WHEN student_id BETWEEN 91 AND 190 AND student_id % 19 = 0 AND subject_id IN (2, 8)
                THEN 36 + ((student_id + subject_id) % 13)
            WHEN student_id > 190 AND student_id % 13 = 0 AND subject_id IN (4, 10)
                THEN 34 + ((student_id + subject_id) % 14)
            ELSE 55 + ((student_id * 7 + subject_id * 11 + CASE WHEN semester_id = 'SEM2' THEN 13 ELSE 0 END) % 46)
        END AS score
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

INSERT INTO attendance (id, student_id, subject_id, semester_id, percentage)
SELECT
    ROW_NUMBER() OVER (ORDER BY g.student_id, g.semester_id, g.subject_id) AS id,
    g.student_id,
    g.subject_id,
    g.semester_id,
    CASE
        WHEN g.score < 50 THEN 45 + ((g.student_id * 3 + g.subject_id * 5 + CASE WHEN g.semester_id = 'SEM2' THEN 7 ELSE 0 END) % 26)
        ELSE 70 + ((g.student_id * 5 + g.subject_id * 3 + CASE WHEN g.semester_id = 'SEM2' THEN 9 ELSE 0 END) % 31)
    END AS percentage
FROM grades g
WHERE g.attempt = 1;

COMMIT;
