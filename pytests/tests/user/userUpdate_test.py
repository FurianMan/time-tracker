#!/usr/bin/python3
# coding: utf-8
from tests.api import Requester
import pytest
from tests.tools.supplements import UserData, UserDataUpdate
from russian_names import RussianNames
from pyhocon import ConfigFactory

conf = ConfigFactory.parse_file("./constants.conf")
endpoint = conf.get_string(
    "endpoints.user"
)  # конечная точка url http://ip:port/time-tracker/endpoint


class TestUserUpdate:
    """
    Обновление происходит по обязательным полям: name,surname,position,birthday
    """
    def test_updateUserFullParams(self, create_user_before, delete_user_after):
        http_client = Requester()

        created_user = create_user_before

        # user_id будет равен 0
        payload = UserDataUpdate(
            name=created_user["name"], 
            surname=created_user["surname"], 
            patronymic=created_user["patronymic"],
            position=created_user["position"],
            birthday=created_user["birthday"]
        ).__dict__  # получаем атрибуты класса в виде словаря

        request = http_client.put_request(endpoint=endpoint, payload=payload)
        assert (
            request.status_code == 200
        ), f"Error get user: {request.json()['message']}"

        # Запрашиваем пользователя с новыми полями
        newPayload = UserData(
            name=payload["newName"], 
            surname=payload["newSurname"], 
            patronymic=payload["newPatronymic"],
            position=payload["newPosition"],
            birthday=payload["newBirthday"]
        ).__dict__

        get_request = http_client.get_request(endpoint=endpoint, payload=newPayload)
        assert request.status_code == 200, f"Error get user: {get_request['message']}"

        # удаляем пользователя после теста
        delete_user_after.append(newPayload)



    # """
    # Меняем каждый тест только по 1 параметру.
    # Изменение пользователя в бд идет благодаря user_id.
    # Если другие поля не назначены, то должны остаться прежними
    # """
    # @pytest.mark.parametrize(
    #     "newSurname, newName, newPatronymic, newPosition, newBirthday",
    #     [
    #         ("Толстой", None, None, None, None),
    #         (None, "Лев", None, None, None),
    #         (None, None, "Николаевич", None, None),
    #         (None, None, None, "писатель", None),
    #         (None, None, None, None, "1900-05-13"),
    #     ],
    # )
    # def test_updateUserById(self, newSurname, newName, newPatronymic, newPosition, newBirthday, create_user_before, delete_user_after):
    #     http_client = Requester()

    #     created_user = create_user_before

    #     payload = UserDataUpdate(
    #         user_id=created_user["user_id"],
    #         name=created_user["name"], 
    #         surname=created_user["surname"], 
    #         patronymic=created_user["patronymic"],
    #         position=created_user["position"],
    #         birthday=created_user["birthday"],
    #         newName=newName, 
    #         newSurname=newSurname, 
    #         newPatronymic=newPatronymic,
    #         newPosition=newPosition,
    #         newBirthday=newBirthday
    #     ).__dict__  # получаем атрибуты класса в виде словаря

    #     request = http_client.put_request(endpoint=endpoint, payload=payload)
    #     assert (
    #         request.status_code == 200
    #     ), f"Error get user: {request.json()['message']}"

    #     # Запрашиваем пользователя с новыми полями
    #     newPayload = UserData(
    #         name=payload["newName"], 
    #         surname=payload["newSurname"], 
    #         patronymic=payload["newPatronymic"],
    #         position=payload["newPosition"],
    #         birthday=payload["newBirthday"]
    #     ).__dict__

    #     get_request = http_client.get_request(endpoint=endpoint, payload=newPayload)
    #     assert request.status_code == 200, f"Error get user: {get_request['message']}"

    #     # удаляем пользователя после теста
    #     delete_user_after.append(newPayload)