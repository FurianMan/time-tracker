# Модуль с полезными функциями для тестов
from dataclasses import dataclass


@dataclass
class UserData:
    user_id:int = 0
    name:str = "Александр"
    surname:str = "Роликов"
    patronymic:str = "Константинович"
    position:str = "QA"
    birthday:str = "1993-01-08"

@dataclass
class UserDataUpdate:
    user_id:int = 0
    name:str = "Виктория"
    surname:str = "Елеская"
    patronymic:str = "Тимофеевна"
    position:str = "QA"
    birthday:str = "2000-01-08"
    newName:str = "Ева"
    newSurname:str = "Миллер"
    newPatronymic:str = "Антоновна"
    newPosition:str = "официант"
    newBirthday:str = "2001-01-08"

@dataclass
class TaskData:
    user_id:int = 0
    task_id:int = 0
    task_num:int = 666666

@dataclass
class Stats:
    user_id:int = 0
    mode:str = "oneline"
    start_time:str = "1999-08-01 00:00:00"
    end_time:str = "2050-08-01 00:00:00"