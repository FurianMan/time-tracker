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


class TestTaskClose:


    """
    Перед закрытием фикстурой создаем пользователя и задачу.
    Потом фикстура сама удалит пользователя вместе с данными
    Из фикстуры берем task_id и user_id (оба обязательные) и закрываем задачу
    """
    def test_putTask(self, create_task_before):
        http_client = Requester()  # создание клиента

        # берем созданный task из фикстуры словарем
        payload = create_task_before

        request = http_client.put_request(endpoint=endpoint, payload=payload)
        assert request.status_code == 200, f"Error close task: {request.json()['message']}"

    
    
    """
    Один из обязательных параметров не указан
    """
    @pytest.mark.parametrize(
        "user_id, task_id",
        [
            (None, 6006),  # нет user_id
            (1, None),  # нет task_id 
        ],
    )
    def test_CloseTaskErr(self, user_id, task_id):
        http_client = Requester()  # создание клиента

        payload = TaskData(
            user_id=user_id, task_id=task_id
        ).__dict__  # получаем атрибуты класса в виде словаря

        request = http_client.put_request(endpoint=endpoint, payload=payload)
        assert request.status_code == 415, f"Error has not got: {request.json()['message']}"


    """
    Перед закрытием фикстурой создаем пользователя и задачу.
    Потом фикстура сама удалит пользователя вместе с данными
    Закрываем задачу самостоятельно и снова пытаемся закрыть её //TODO сделать фикстуру
    """
    def test_TaskClosedErr(self, create_task_before):
        http_client = Requester()  # создание клиента

        # берем созданный task из фикстуры словарем
        payload = create_task_before

        request = http_client.put_request(endpoint=endpoint, payload=payload) # закрываем первый раз
        request = http_client.put_request(endpoint=endpoint, payload=payload) # второй раз должна быть ошибка
        assert request.status_code == 415, f"Error has not got: {request.json()['message']}"

    

    def test_TaskNeverExistedErr(self, create_task_before):
        http_client = Requester()  # создание клиента

        # берем созданный task из фикстуры словарем
        payload = create_task_before
        payload["task_num"] = 2315151

        request = http_client.put_request(endpoint=endpoint, payload=payload)
        assert request.status_code == 415, f"Error has not got: {request.json()['message']}"