#!/usr/bin/python3
# coding: utf-8
from tests.api import Requester
import pytest
from tests.tools.supplements import UserData
from russian_names import RussianNames
from pyhocon import ConfigFactory

conf = ConfigFactory.parse_file("./constants.conf")
endpoint = conf.get_string(
    "endpoints.user"
)  # конечная точка url http://ip:port/time-tracker/endpoint


class TestUserDelete:

    """
    Удаление происходит по обязательным полям: name,surname,position,birthday
    """
    def test_delUserFullParams(self, create_user_before_without_del):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = create_user_before_without_del
        payload["user_id"] = 0

        request = http_client.delete_request(endpoint=endpoint, payload=payload)
        assert (
            request.status_code == 200
        ), f"Error delete user: {request.json()['message']}"
    
    """
    Удаление происходит по обязательным полям: user_id
    """
    def test_delUserById(self, create_user_before_without_del):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = create_user_before_without_del

        request = http_client.delete_request(endpoint=endpoint, payload=payload)
        assert (
            request.status_code == 200
        ), f"Error delete user: {request.json()['message']}"



    """
    Нет одного из обязательных полей
    """
    @pytest.mark.parametrize(
        "surname, name, patronymic, position, birthday",
        [
            (None, "Егор", "Викторович", "QA", "1993-05-13"),
            (
                "Романов",
                None,
                "Викторович",
                "QA",
                "1993-05-13",
            ),
            (
                "Петров",
                "Виктор",
                "Викторович",
                None,
                "1993-05-13",
            ),
            ("Петров", "Виктор", "Викторович", "QA", None),
        ],
    )
    def test_delUserErr(self, name, surname, patronymic, position, birthday):
        http_client = Requester()

        payload = UserData(
            name=name,
            surname=surname,
            patronymic=patronymic,
            position=position,
            birthday=birthday,
        ).__dict__  # получаем атрибуты класса в виде словаря

        request = http_client.delete_request(endpoint=endpoint, payload=payload)
        assert (
            request.status_code == 415
        ), f"Error has not got: {request.json()['message']}"



    """
    Удаление несуществующего пользователя
    """
    def test_delUserNeverExistedErr(self):
        http_client = Requester()

        payload = UserData(
            name="Меня",
            surname="Никогда",
            patronymic="Не",
            position="Было",
            birthday="9999-08-01",
        ).__dict__  # получаем атрибуты класса в виде словаря

        request = http_client.delete_request(endpoint=endpoint, payload=payload)
        assert (
            request.status_code == 404
        ), f"Error has not got: {request.json()['message']}"
