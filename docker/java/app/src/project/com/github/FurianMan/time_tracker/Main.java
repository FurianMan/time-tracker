package com.github.FurianMan.time_tracker;

import com.github.FurianMan.time_tracker.mysqlUtilities.CheckConnectionToDB;

public class Main {

    public static void main(String[] args) {

        MyHttpServer.startServer();
        CheckConnectionToDB.checkConnectionToDB();
    }
}

//TODO исправить все ответы по http, возможно убрать кавычки
//TODO обернуть все в неизвестные ошибки
//TODO получить id всех незавершенных задач по пользователю
//TODO Дать внятные ошибки и описание в лог
//TODO Сделать регулярки на проверку даты в GetWorkStats.
//TODO Сделать другую уникальность для пользователей
//TODO Проверить GetUser. Поменял поиск на name,surname,patronymic,birthday.
//TODO Фамилия может быть на рус, а имя например на англ и это пропустит
//TODO Поменять местами в GetWorkStats , сначала ошибка на user , потом на поля
//TODO Проверить как работает после новой склейки