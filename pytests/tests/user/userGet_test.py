#!/usr/bin/python3
# coding: utf-8
from tests.api import Requester
import pytest
from tests.tools.supplements import UserData
# from russian_names import RussianNames
from pyhocon import ConfigFactory

conf = ConfigFactory.parse_file("./constants.conf")
endpoint = conf.get_string("endpoints.user")  # конечная точка url http://ip:port/time-tracker/endpoint

class TestUserGet:

    """
    Поиск происходит по обязательным полям: name,surname,position,birthday
    """
    def test_getUserFullParams(self, create_user_before):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = create_user_before
        payload["user_id"] = 0

        request = http_client.get_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 200, f"Error get user: {data['message']}"

        assert (data['name'] == payload["name"] 
        and data['surname'] == payload["surname"]
        and data['position'] == payload["position"]
        and data['patronymic'] == payload["patronymic"]
        and data['birthday'] == payload["birthday"]
        )



    """
    Поиск происходит по обязательным полям: user_id
    """
    def test_getUserById(self, create_user_before):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = create_user_before

        request = http_client.get_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 200, f"Error get user: {data['message']}"

        assert (data['name'] == payload["name"] 
        and data['surname'] == payload["surname"]
        and data['position'] == payload["position"]
        and data['patronymic'] == payload["patronymic"]
        and data['birthday'] == payload["birthday"]
        )



    """
    Поиск происходит по обязательным полям: name, surname
    Вернеться последний созданный пользователь, если имя и фамилия совпадут
    """
    def test_getUserShortParams(self, create_user_before):
        http_client = Requester()


        # берем созданного пользователя из фикстуры словарем
        payload = create_user_before
        user_id = payload["user_id"] # сохраняем, чтобы вернуть для удаления
        del payload["user_id"] # удаляем/зануляем для того, чтобы не искать по нему
        del payload["birthday"] # удаляем, чтобы не искать по связке name,surname,patronymic,birthday


        request = http_client.get_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 200, f"Error get user: {data['message']}"

        assert (data['name'] == payload["name"] 
        and data['surname'] == payload["surname"]
        and data['position'] == payload["position"]
        and data['patronymic'] == payload["patronymic"]
        )

        payload["user_id"] = user_id # возвращаем id для удаления
