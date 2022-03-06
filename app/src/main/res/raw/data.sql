DROP TABLE IF EXISTS MLConfigTable
DROP TABLE IF EXISTS ExerciseTable
DROP TABLE IF EXISTS UserTable
DROP TABLE IF EXISTS ScheduleTable
DROP TABLE IF EXISTS CompletedExerciseTable
DROP TABLE IF EXISTS CompletedExerciseCalendarTable
CREATE TABLE MLConfigTable ( _id INTEGER PRIMARY KEY, coordinates TEXT)
CREATE TABLE ExerciseTable ( _id INTEGER PRIMARY KEY, name TEXT, mlId INTEGER, FOREIGN KEY ( _id ) REFERENCES MLConfigTable (_id) ON DELETE CASCADE ON UPDATE NO ACTION)
CREATE TABLE UserTable ( _id INTEGER PRIMARY KEY, height FLOAT, weight FLOAT, bmi FLOAT)
CREATE TABLE ScheduleTable ( _id INTEGER PRIMARY KEY,user_id INTEGER, exercise_id INTEGER, startDateTime TEXT, notes TEXT, no_of_sets INTEGER, title TEXT, FOREIGN KEY (user_id) REFERENCES UserTable (_id),FOREIGN KEY (exercise_id) REFERENCES ExerciseTable (_id))
CREATE TABLE CompletedExerciseTable ( user_id INTEGER, exercise_id INTEGER, completed_dateTime TEXT, total_calories FLOAT, no_completed_sets INTEGER, PRIMARY KEY(user_id,exercise_id,completed_dateTime), FOREIGN KEY (user_id) REFERENCES UserTable (_id),FOREIGN KEY (exercise_id) REFERENCES ExerciseTable (_id))
CREATE TABLE CompletedExerciseCalendarTable ( schedule_id INTEGER, user_id INTEGER, exercise_id INTEGER, completed_dateTime TEXT, isCompleted INTEGER, PRIMARY KEY(schedule_id,user_id,exercise_id,completed_dateTime), FOREIGN KEY (user_id) REFERENCES UserTable (_id),FOREIGN KEY (exercise_id) REFERENCES ExerciseTable (_id), FOREIGN KEY (schedule_id) REFERENCES ScheduleTable (_id),FOREIGN KEY(completed_dateTime) REFERENCES CompletedExerciseTable(completed_dateTime) )
INSERT INTO UserTable (height,weight,bmi) VALUES( 163.5, 75.3,13.3)
INSERT INTO UserTable (height,weight,bmi) VALUES( 183.5, 75.3,13.3)
INSERT INTO UserTable (height,weight,bmi) VALUES( 193.5, 75.3,13.3)
INSERT INTO MLConfigTable (coordinates) VALUES("12.3 , 11.1, 11.1")
INSERT INTO MLConfigTable (coordinates) VALUES("12.3 , 11.1, 11.1")
INSERT INTO MLConfigTable (coordinates) VALUES("12.3 , 11.1, 11.1")
INSERT INTO MLConfigTable (coordinates) VALUES("12.3 , 11.1, 11.1")
INSERT INTO ExerciseTable (name, mlId) VALUES("Exercise 1",1)
INSERT INTO ExerciseTable (name, mlId) VALUES("Exercise 2",2)
INSERT INTO ExerciseTable (name, mlId) VALUES("Exercise 3",3)
INSERT INTO ExerciseTable (name, mlId) VALUES("Exercise 4",4)
INSERT INTO ScheduleTable (user_id, exercise_id,startDateTime,notes,no_of_sets,title) VALUES(1,1,"2016-12-21 00:00:00.000","Must do workout!",4,"abs")
INSERT INTO ScheduleTable (user_id, exercise_id,startDateTime,notes,no_of_sets,title) VALUES(1,2,"2016-12-21 00:00:00.000","Must run",3,"legs")
INSERT INTO ScheduleTable (user_id, exercise_id,startDateTime,notes,no_of_sets,title) VALUES(1,3,"2016-12-21 00:00:00.000","Must do workout!",2,"arms")
INSERT INTO ScheduleTable (user_id, exercise_id,startDateTime,notes,no_of_sets,title) VALUES(1,4,"2016-12-21 00:00:00.000","Must do workout!",8,"abs")
INSERT INTO CompletedExerciseTable (user_id, exercise_id,completed_dateTime,total_calories,no_completed_sets) VALUES(1,1,"2016-12-21 00:00:00.000",112,4)
INSERT INTO CompletedExerciseTable (user_id, exercise_id,completed_dateTime,total_calories,no_completed_sets) VALUES(2,1,"2016-12-21 00:00:00.000",192,77)
INSERT INTO CompletedExerciseTable (user_id, exercise_id,completed_dateTime,total_calories,no_completed_sets) VALUES(1,2,"2016-12-21 00:00:00.000",132,4)
INSERT INTO CompletedExerciseCalendarTable (schedule_id, user_id,exercise_id,completed_dateTime,isCompleted) VALUES(1,1,1,"2016-12-21 00:00:00.000",1)
INSERT INTO CompletedExerciseCalendarTable (schedule_id, user_id,exercise_id,completed_dateTime,isCompleted) VALUES(2,1,2,"2016-12-21 00:00:00.000",1)