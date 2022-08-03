#!/usr/bin/python3
# coding: utf-8
from random import randint
from time import sleep
from tests.api import Requester
import copy
import pytest
from tests.tools.supplements import TaskData, UserData
from russian_names import RussianNames
from pyhocon import ConfigFactory

conf = ConfigFactory.parse_file("./constants.conf")
userEndpoint = conf.get_string("endpoints.user")
workEndpoint = conf.get_string("endpoints.work")
workStatsEndpoint = conf.get_string("endpoints.workStats")

users_generator_settings = RussianNames(
    count=1, patronymic=True, output_type="dict"
)  # Настраиваем ФИО пользователей


"""
Фикстура создания пользователя перед тестом
Создаем пользователя и через get добавляем user_id в словарь
После отдаем словарь в тесты
Как только тест закончится - добавляем словарь в список фикстуры на удаление 
"""
@pytest.fixture(scope="function")
def create_user_before(delete_user_after):

    http_client = Requester()  # создание клиента

    # Получаем ФИО пользователей
    user = users_generator_settings.get_batch()[0]

    payload = UserData(
        name=user["name"],
        surname=user["surname"],
        patronymic=user["patronymic"]
    ).__dict__  # получаем атрибуты класса в виде словаря

    request = http_client.post_request(endpoint=userEndpoint, payload=payload)
    assert request.status_code == 200, f"Error post user: {request.json()['message']}"
    payload["user_id"] = request.json()['user_id']

    yield payload #TODO: передавать копию словаря в тесты
    delete_user_after.append(payload) 



"""
Фикстура создания пользователя без удаления.
"""
@pytest.fixture(scope="function")
def create_user_before_without_del():

    http_client = Requester()  # создание клиента

    # Получаем ФИО пользователей
    user = users_generator_settings.get_batch()[0]

    payload = UserData(
        name=user["name"],
        surname=user["surname"],
        patronymic=user["patronymic"]
    ).__dict__  # получаем атрибуты класса в виде словаря

    request = http_client.post_request(endpoint=userEndpoint, payload=payload)
    assert request.status_code == 200, f"Error post user: {request.json()['message']}"
    payload["user_id"] = request.json()['user_id']

    yield payload



"""
Фикстура на удаление пользователя после теста
Отдает лист в тест, ждет добавления одного словаря с данными пользователя
"""
@pytest.fixture(scope="function")
def delete_user_after():
    # добавляем только один словарь
    delete_list = []

    yield delete_list

    http_client = Requester()  # создание клиента
    payload = delete_list[0]

    request = http_client.delete_request(endpoint=userEndpoint, payload=payload)
    assert request.status_code == 200, f"Error delete user: {request.json()['message']}"


"""
Фикстура нужна для создания пользователя + открытой задачи.
Сначала вызываем фикстуру на создание пользователя, берем данные и создаем ему задание
После передаем данные в тест.
После теста фикстура по созданию пользователя удалит пользователя, а значит и все данные 
"""
@pytest.fixture(scope="function")
def create_task_before(create_user_before):


    http_client = Requester()  # создание клиента

    user_id = create_user_before["user_id"]
    task_num=randint(1000, 99999)

    payload = TaskData(
        user_id=user_id, task_num=task_num
    ).__dict__  # получаем атрибуты класса в виде словаря

    request = http_client.post_request(endpoint=workEndpoint, payload=payload)
    assert request.status_code == 200, f"Error add task: {request.json()['message']}"
    payload["task_id"] = request.json()['task_id']


    yield payload


"""
Фикстура только закрывает задачу, пока что нигде не используется.
"""
@pytest.fixture(scope="function")
def close_task_after():
    # добавляем только один словарь
    close_list = []

    yield close_list

    http_client = Requester()  # создание клиента
    payload = close_list[0]

    request = http_client.put_request(endpoint=workEndpoint, payload=payload)
    assert request.status_code == 200, f"Error close task: {request.json()['message']}"


"""
Создаем пользователя, делаем ему задания и закрываем их
После отдаем словарь с созданным пользователем в тест, откуда смогут достать user_id для поиска
Как только тест пройдет - удаляем пользователя, а значит и всю статистику. 
"""
@pytest.fixture(scope="function")
def create_stats_before(delete_user_after):

    http_client = Requester()  # создание клиента

    # Начали создавать пользователя
    # Получаем ФИО пользователей
    user = users_generator_settings.get_batch()[0]

    payloadCreateUser = UserData(
        name=user["name"],
        surname=user["surname"],
        patronymic=user["patronymic"]
    ).__dict__  # получаем атрибуты класса в виде словаря

    request = http_client.post_request(endpoint=userEndpoint, payload=payloadCreateUser)
    assert request.status_code == 200, f"Error post user: {request.json()['message']}"
    payloadCreateUser["user_id"] = request.json()['user_id']
    # Закончили создавать пользователя

    # В цикле создаем задание, ждем минуту и закрываем его.
    for i in range(3):
        user_id = payloadCreateUser["user_id"]
        task_num=randint(1000, 99999)
        payloadCreateUser[f"task_num{i}"] = task_num # добавляю номера, чтобы в тестах их сверять

        payloadCreateTask = TaskData(
            user_id=user_id, task_num=task_num
        ).__dict__  # получаем атрибуты класса в виде словаря

        request = http_client.post_request(endpoint=workEndpoint, payload=payloadCreateTask)
        assert request.status_code == 200, f"Error add task: {request.json()['message']}"
        payloadCreateTask["task_id"] = request.json()['task_id']

        sleep(60)

        request = http_client.put_request(endpoint=workEndpoint, payload=payloadCreateTask)
        assert request.status_code == 200, f"Error close task: {request.json()['message']}"
    
    payloadCreateUserCopy = copy.deepcopy(payloadCreateUser) # отдавать в тесты будем копию


    yield payloadCreateUserCopy
    delete_user_after.append(payloadCreateUser)