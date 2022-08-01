#!/usr/bin/python3
# coding: utf-8
from random import randint
from tests.api import Requester
import pytest
from tests.tools.supplements import TaskData
from pyhocon import ConfigFactory

conf = ConfigFactory.parse_file("./constants.conf")
endpoint = conf.get_string(
    "endpoints.work"
)  # конечная точка url http://ip:port/time-tracker/endpoint


class TestTaskAdd:

    """
    Перед тестом создаем пользователя и забираем его user_id
    Далее создаем создачу (user_id и task_num - обязательные параметры)
    Метода на получения задач нет, так что проверяем только 200 ответ
    После теста удаляем пользователя, вместе с ним и данные из БД
    """
    def test_postTask(self, create_user_before, delete_user_after):
        http_client = Requester()  # создание клиента

        user_id = create_user_before["user_id"]
        task_num=randint(1000, 99999)

        payload = TaskData(
            user_id=user_id, task_num=task_num
        ).__dict__  # получаем атрибуты класса в виде словаря

        request = http_client.post_request(endpoint=endpoint, payload=payload)
        assert request.status_code == 200, f"Error add task: {request.json()['message']}"

    
        # удаляем данные после теста, удалятся вместе с пользователем
        delete_user_after.append(payload)


    @pytest.mark.parametrize(
        "user_id, task_num",
        [
            (None, 6006),  # нет user_id
            (1, None),  # нет task_num
        ],
    )
    def test_TaskErr415(self, user_id, task_num):
        http_client = Requester()  # создание клиента

        payload = TaskData(
            user_id=user_id, task_num=task_num
        ).__dict__  # получаем атрибуты класса в виде словаря

        request = http_client.post_request(endpoint=endpoint, payload=payload)
        assert request.status_code == 415, f"Error has not got: {request.json()['message']}"


    """
    Есть обязательные поля, пользователь и задача есть, но задача еще открыта
    """
    def test_TaskOpenErr(self, create_task_before):
        http_client = Requester()  # создание клиента

        user_id = create_task_before["user_id"]
        task_num=create_task_before["task_num"]

        payload = TaskData(
            user_id=user_id, task_num=task_num
        ).__dict__  # получаем атрибуты класса в виде словаря

        request = http_client.post_request(endpoint=endpoint, payload=payload)
        assert request.status_code == 415, f"Error has not got: {request.json()['message']}"

    

