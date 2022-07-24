package com.github.FurianMan.time_tracker.mysqlTables;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

 class TableUsersTest {
    TableUsers user;
    final String nameForSet = "Егор";
    final String surnameForSet = "Давыдов";
    final String patronymicForSet = "Сергеевич";
    final String positionForSet = "Программист";
    final String birthdayForSet = "1997-08-01";
    final int userIdForSet = 666;
    final String dateCreatingForSet = "2022-07-24 13:45:27";
    boolean flag;
    @BeforeEach
    void setUp() {
        user = new TableUsers();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setName() {
        user.setName(nameForSet);
        assertEquals(nameForSet, user.getName());
    }

    @Test
    void setSurname() {
        user.setSurname(surnameForSet);
        assertEquals(surnameForSet, user.getSurname());
    }

    @Test
    void setPatronymic() {
        user.setPatronymic(patronymicForSet);
        assertEquals(patronymicForSet, user.getPatronymic());
    }

    @Test
    void setPosition() {
        user.setPosition(positionForSet);
        assertEquals(positionForSet, user.getPosition());
    }

    @Test
    void setBirthday() {
        user.setBirthday(birthdayForSet);
        assertEquals(birthdayForSet, user.getBirthday());
    }

    @Test
    void setUser_id() {
        user.setUser_id(userIdForSet);
        assertEquals(userIdForSet, user.getUser_id());
    }

    @Test
    void setDateCreating() {
        user.setDateCreating(dateCreatingForSet);
        assertEquals(dateCreatingForSet, user.getDateCreating());
    }

    @Nested
    @TestInstance (TestInstance.Lifecycle.PER_CLASS)
    class  getTests {
        TableUsers user;
        final String setUpName = "Александр";
        final String setUpSurname = "Роликов";
        final String setUpPatronymic = "Константинович";
        final String setUpPosition = "Стажер";
        final String setUpBirthday = "1997-10-03";
        final String setUpNewName = "Юрий";
        final String setUpNewSurname = "Сидоров";
        final String setUpNewBirthday = "1997-10-01";
        String setUpNewPatronymic = "Сергеевич";
        final String setUpNewPosition = "Консультант";
        final int setUpUser_id = 999;

        @BeforeAll //TODO если упадет один из сеттеров, то отразиться на всех тестах, надо разделить
        void setUp() {
            user = new TableUsers();
            user.setName(setUpName);
            user.setSurname(setUpSurname);
            user.setPatronymic(setUpPatronymic);
            user.setUser_id(setUpUser_id);
            user.setPosition(setUpPosition);
            user.setBirthday(setUpBirthday);
            user.setNewName(setUpNewName);
            user.setNewBirthday(setUpNewBirthday);
            user.setNewSurname(setUpNewSurname);
            user.setNewPatronymic(setUpNewPatronymic);
            user.setNewPosition(setUpNewPosition);
        }

        @Test
        void getName() {
            assertEquals(setUpName, user.getName());
        }

        @Test
        void getSurname() {
            assertEquals(setUpSurname, user.getSurname());
        }

        @Test
        void getPatronymic() {
            assertEquals(setUpPatronymic, user.getPatronymic());
        }

        @Test
        void getPosition() {
            assertEquals(setUpPosition, user.getPosition());
        }

        @Test
        void getBirthday() {
            assertEquals(setUpBirthday, user.getBirthday());
        }

        @Test
        void getNewName() {
            assertEquals(setUpNewName, user.getNewName());
        }

        @Test
        void getNewBirthday() {
            assertEquals(setUpNewBirthday, user.getNewBirthday());
        }

        @Test
        void getNewPatronymic() {
            assertEquals(setUpNewPatronymic, user.getNewPatronymic());
        }

        @Test
        void getNewPosition() {
            assertEquals(setUpNewPosition, user.getNewPosition());
        }

        @Test
        void getNewSurname() {
            assertEquals(setUpNewSurname, user.getNewSurname());
        }

        @Test
        void getUser_id() {
            assertEquals(setUpUser_id, user.getUser_id());
        }

        @Test
        void getValues() {
            ArrayList<String> myCheckList = new ArrayList<>();
            myCheckList.add(setUpName);
            myCheckList.add(setUpSurname);
            myCheckList.add(setUpPatronymic);
            myCheckList.add(setUpPosition);
            myCheckList.add(setUpNewName);
            myCheckList.add(setUpNewSurname);
            myCheckList.add(setUpNewPatronymic);
            myCheckList.add(setUpNewPosition);

            ArrayList<String> listValues = user.getValues();

            for (String param : myCheckList) {
                 flag = listValues.contains(param);
                 if (flag == false) {break;}
            }

            assertEquals(true, flag);
        }
    }
}