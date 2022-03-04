DROP TABLE IF EXISTS MLConfigTable
DROP TABLE IF EXISTS ExerciseTable
CREATE TABLE MLConfigTable ( _id INTEGER PRIMARY KEY, coordinates TEXT)
CREATE TABLE ExerciseTable ( _id INTEGER PRIMARY KEY, name TEXT, mlId INTEGER, FOREIGN KEY ( _id ) REFERENCES MLConfigTable (_id) ON DELETE CASCADE ON UPDATE NO ACTION)
INSERT INTO MLConfigTable (_id,coordinates) VALUES(1,"12.3 , 11.1, 11.1")
INSERT INTO MLConfigTable (_id,coordinates) VALUES(2,"12.3 , 11.1, 11.1")
INSERT INTO MLConfigTable (_id,coordinates) VALUES(3,"12.3 , 11.1, 11.1")
INSERT INTO MLConfigTable (_id,coordinates) VALUES(4,"12.3 , 11.1, 11.1")
INSERT INTO ExerciseTable (_id,name, mlId) VALUES(1,"Exercise 1",1)
INSERT INTO ExerciseTable (_id,name, mlId) VALUES(2,"Exercise 2",2)
INSERT INTO ExerciseTable (_id,name, mlId) VALUES(3,"Exercise 3",3)
INSERT INTO ExerciseTable (_id,name, mlId) VALUES(4,"Exercise 4",4)