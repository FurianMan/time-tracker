package com.github.FurianMan.time_tracker.mysqlTables;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class TableTasksTest {

    TableTasks task;
    final int task_numForSet = 666999;
    final String start_timeForSet = "2022-07-24 15:45:23";
    final String end_timeForSet = "2022-07-24 16:20:05";
    final int user_idForSet = 69;

    @BeforeEach
    void setUp() {
        task = new TableTasks();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setTask_num() {
        task.setTask_num(task_numForSet);
        assertEquals(task_numForSet, task.getTask_num());
    }

    @Test
    void setStart_time() {
        task.setStart_time(start_timeForSet);
        assertEquals(start_timeForSet, task.getStart_time());
    }

    @Test
    void setEnd_time() {
        task.setEnd_time(end_timeForSet);
        assertEquals(end_timeForSet, task.getEnd_time());
    }

    @Test
    void setUser_id() {
        task.setUser_id(user_idForSet);
        assertEquals(user_idForSet, task.getUser_id());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class  getTests {
        TableTasks task;
        final int setUpTask_num = 995566;
        final int setUpUser_id = 54;
        final String setUpStart_time = "1984-04-01 04:25:59";
        final String setUpEnd_time = "1999-12-01 12:00:40";

        @BeforeAll //TODO если упадет один из сеттеров, то отразиться на всех тестах, надо разделить
        void setUp() {
            task = new TableTasks();
            task.setUser_id(setUpUser_id);
            task.setTask_num(setUpTask_num);
            task.setStart_time(setUpStart_time);
            task.setEnd_time(setUpEnd_time);
        }

        @Test
        void getUser_id() {
            assertEquals(setUpUser_id, task.getUser_id());
        }

        @Test
        void getTask_num() {
            assertEquals(setUpTask_num, task.getTask_num());
        }

        @Test
        void getStart_time() {
            assertEquals(setUpStart_time, task.getStart_time());
        }

        @Test
        void getEnd_time() {
            assertEquals(setUpEnd_time, task.getEnd_time());
        }
    }
}