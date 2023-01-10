--Task 1: Select all primary skills that contain more than one word (both ‘-‘ and ‘ ’ could be used as a separator)
SELECT 
  DISTINCT primary_skill 
FROM 
  student 
WHERE 
  primary_skill SIMILAR TO ('%-%|% %');

--Task 2: Select all students who does not have second name (it is absent or consists from only one letter/letter with dot)
SELECT 
  * 
FROM 
  student 
WHERE 
  name ~* '^[a-z]*\s?[a-z]?.?$';

--Task 3: Select number of students passed exams for each subject and order result by number of student descending
SELECT 
  sub.name, 
  COUNT(*) students_number 
FROM 
  subject sub 
  JOIN exam_result ex_r ON sub.id = ex_r.subject_id 
GROUP BY 
  sub.name 
ORDER BY 
  students_number DESC;

--Task 4: Select number of students with the same exam marks for each subject.
SELECT 
  sub.name, 
  ex_r.mark, 
  COUNT(*) students_headcount 
FROM 
  subject sub 
  JOIN exam_result ex_r ON sub.id = ex_r.subject_id 
GROUP BY 
  sub.name, 
  ex_r.mark 
ORDER BY 
  sub.name, 
  ex_r.mark;

--Task 5: Select students who passed at least two exams for different subject.
SELECT 
  st.id,
  st.name, 
  st.surname, 
  COUNT(DISTINCT ex_r.subject_id) subject_exams_passed 
FROM 
  student st
  JOIN exam_result ex_r ON st.id = ex_r.student_id 
GROUP BY 
  st.id,
  st.name, 
  st.surname 
HAVING 
  COUNT(DISTINCT ex_r.subject_id) > 1;
  
--Task 6: Select students who passed at least two exams for the same subject.
SELECT 
  st.id, 
  st.name, 
  st.surname 
FROM 
  student st
WHERE 
  EXISTS (
    SELECT 
      ex_r.subject_id, 
      ex_r.student_id 
    FROM 
      exam_result ex_r 
    WHERE 
      st.id = ex_r.student_id 
    GROUP BY 
      ex_r.student_id, 
      ex_r.subject_id 
    HAVING 
      COUNT(ex_r.subject_id) > 1
  );

--Task 7: Select all subjects which exams passed only students with the same primary skills.
SELECT 
  sub.id, 
  sub.name, 
  sub.tutor 
FROM 
  subject sub 
  JOIN exam_result ex_r ON sub.id = ex_r.subject_id 
  JOIN student st ON ex_r.student_id = st.id 
GROUP BY 
  sub.id, 
  sub.name, 
  sub.tutor 
HAVING 
  COUNT(DISTINCT st.primary_skill) = 1;

/*Task 8: Select all subjects which exams passed only students with the different primary skills. 
It means that all students passed the exam for the one subject must have different primary skill.*/
SELECT 
  sub.id, 
  sub.name, 
  sub.tutor 
FROM 
  subject sub 
  JOIN exam_result ex_r ON sub.id = ex_r.subject_id 
  JOIN student st ON ex_r.student_id = st.id 
GROUP BY 
  sub.id, 
  sub.name, 
  sub.tutor 
HAVING 
  COUNT(DISTINCT st.id) = COUNT(DISTINCT st.primary_skill);
  
/*Task 9: Select students who does not pass any exam using each the following operator and 
Check which approach is faster for 1000, 10K, 100K exams and 10, 1K, 100K students: */
-- Outer join
SELECT 
  st.id, 
  st.name, 
  st.surname 
FROM 
  student st 
  LEFT JOIN exam_result ex_r ON st.id = ex_r.student_id 
WHERE 
  ex_r.student_id IS NULL;
  
-- Subquery with ‘not in’ clause
SELECT 
  st.id, 
  st.name, 
  st.surname 
FROM 
  student st 
WHERE 
  st.id NOT IN (
    SELECT 
      ex_r.student_id 
    FROM 
      exam_result ex_r 
  );

-- Subquery with ‘any ‘ clause
SELECT 
  st.id, 
  st.name, 
  st.surname 
FROM 
  student st 
WHERE 
  NOT st.id = ANY (
    SELECT 
      ex_r.student_id 
    FROM 
      exam_result ex_r
  );
  
-- Task 10: Select all students whose average mark is bigger than overall average mark.
SELECT 
  st.id, 
  st.name, 
  st.surname 
FROM 
  student st 
  JOIN exam_result ex_r ON st.id = ex_r.student_id 
GROUP BY 
  st.id, 
  st.name, 
  st.surname 
HAVING 
  AVG(ex_r.mark) > (
    SELECT 
      AVG(mark) 
    FROM 
      exam_result
  );
	
-- Task 11: Select top 5 students who passed their last exam better than average students.
WITH exam_last_result AS(
  SELECT 
    *, 
    ROW_NUMBER() OVER (
      PARTITION BY student_id 
      ORDER BY 
        id DESC
    ) 
  FROM 
    exam_result
)

SELECT 
  st.id, 
  st.name, 
  st.surname, 
  ex_r.mark 
FROM 
  student st 
  JOIN exam_last_result ex_r ON st.id = ex_r.student_id 
GROUP BY 
  st.id, 
  st.name, 
  st.surname, 
  ex_r.row_number, 
  ex_r.mark 
HAVING 
  ex_r.row_number = 1 
  AND ex_r.mark > (
    SELECT 
      AVG(mark) 
    FROM 
      exam_result
  ) 
ORDER BY 
  ex_r.mark DESC 
LIMIT 
  5;

-- Task 12: Select biggest mark for each student and add text description for the mark (use COALESCE and WHEN operators)
/* 	In case if student has not passed any exam ‘not passed' should be returned.
	If student mark is 1,2,3 – it should be returned as ‘BAD’
	If student mark is 4,5,6 – it should be returned as ‘AVERAGE’
	If student mark is 7,8 – it should be returned as ‘GOOD’
	If student mark is 9,10 – it should be returned as ‘EXCELLENT’ */
SELECT 
  st.id, 
  st.name, 
  st.surname, 
  COALESCE(
    CASE 
	  WHEN ex_r.mark >= 1 AND ex_r.mark <= 3 THEN 'BAD' 
	  WHEN ex_r.mark >= 4 AND ex_r.mark <= 6 THEN 'AVERAGE' 
	  WHEN ex_r.mark >= 7 AND ex_r.mark <= 8 THEN 'GOOD' 
	  WHEN ex_r.mark >= 9 AND ex_r.mark <= 10 THEN 'EXCELLENT' 
	END, 
    'NOT PASSED'
  ) AS level 
FROM 
  student st 
  LEFT JOIN exam_result ex_r ON st.id = ex_r.student_id 
GROUP BY 
  st.id, 
  st.name, 
  st.surname, 
  ex_r.mark;
	
-- Task 13: Select number of all marks for each mark type (‘BAD’, ‘AVERAGE’,…) 
SELECT 
  CASE 
	WHEN ex_r.mark >= 1 AND ex_r.mark <= 3 THEN 'BAD' 
	WHEN ex_r.mark >= 4 AND ex_r.mark <= 6 THEN 'AVERAGE' 
	WHEN ex_r.mark >= 7 AND ex_r.mark <= 8 THEN 'GOOD' 
	WHEN ex_r.mark >= 9 AND ex_r.mark <= 10 THEN 'EXCELLENT' 
  END AS result_level,
  COUNT(1) AS level_amount
FROM 
  exam_result ex_r
GROUP BY  
  result_level;
