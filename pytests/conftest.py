#!/usr/bin/python3
# coding: utf-8
from random import randint
from tests.api import Requester
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

    yield payload
    delete_user_after.append(payload)

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


@pytest.fixture(scope="function")
def delete_user_after():
    # добавляем только один словарь
    delete_list = []

    yield delete_list

    http_client = Requester()  # создание клиента
    payload = delete_list[0]

    request = http_client.delete_request(endpoint=userEndpoint, payload=payload)
    assert request.status_code == 200, f"Error delete user: {request.json()['message']}"


@pytest.fixture(scope="function")
def create_task_before(create_user_before, delete_user_after):


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
    delete_user_after.append(payload)



@pytest.fixture(scope="function")
def close_task_after():
    # добавляем только один словарь
    close_list = []

    yield close_list

    http_client = Requester()  # создание клиента
    payload = close_list[0]

    request = http_client.put_request(endpoint=workEndpoint, payload=payload)
    assert request.status_code == 200, f"Error close task: {request.json()['message']}"