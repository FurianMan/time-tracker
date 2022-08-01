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


users_generator_settings = RussianNames(
    count=1, patronymic=True, output_type="dict"
)  # Настраиваем ФИО пользователей


class TestUserCreate:

    def test_postUser(self, delete_user_after):
        user = (
            users_generator_settings.get_batch()[0]
            )   # отдаем dict с ФИО пользователей

        http_client = Requester()  # создание клиента
        payload = UserData(
            name=user["name"], surname=user["surname"], patronymic=user["patronymic"]
        ).__dict__  # получаем атрибуты класса в виде словаря

        request = http_client.post_request(endpoint=endpoint, payload=payload)
        assert request.status_code == 200, f"Error create user: {request.json()['message']}"

        # Запрашиваем пользователя и сравниваем значение полей
        get_request = http_client.get_request(endpoint=endpoint, payload=payload)
        assert request.status_code == 200, f"Error get user: {get_request['message']}"
        get_data = get_request.json()
        assert (
            get_data['name'] == payload["name"]
            and get_data['surname'] == payload["surname"]
            and get_data['patronymic'] == payload["patronymic"]
            and get_data['position'] == payload["position"]
            and get_data['birthday'] == payload["birthday"]
        )

        # удаляем пользователя после теста
        delete_user_after.append(payload)

    @pytest.mark.parametrize(
        "surname, name, patronymic, position, birthday",
        [
            ("Лаптев", "123", "Викторович", "QA", "1993-05-13"),  # цифры
            ("Лаптев", "Егор", "Викторович", "QA", "1993-05-32"),  # неверная дата
            (
                "ROMан",
                "Егор",
                "Викторович",
                "QA",
                "1993.05.13",
            ),  # неверный разделитель у даты
            (
                "ROMан",
                "Егор",
                "Викторович",
                "QA",
                "1993-05-13",
            ),  # англ + рус в одном поле
            (None, "Егор", "Викторович", "QA", "1993-05-13"),  # Нет обязательного поля
            (
                "Романов",
                None,
                "Викторович",
                "QA",
                "1993-05-13",
            ),  # Нет обязательного поля
            (
                "Петров",
                "Виктор",
                "Викторович",
                None,
                "1993-05-13",
            ),  # Нет обязательного поля
            ("Петров", "Виктор", "Викторович", "QA", None),
        ],
    )
    def test_UserErr415(self, name, surname, patronymic, position, birthday):
        http_client = Requester()  # создание клиента

        payload = UserData(
            name=name,
            surname=surname,
            patronymic=patronymic,
            position=position,
            birthday=birthday,
        ).__dict__  # получаем атрибуты класса в виде словаря

        request = http_client.post_request(endpoint=endpoint, payload=payload)
        assert (
            request.status_code == 415
        ), f"Error has not got: {request.json()['message']}"


    """
    Создаем пользователя и его же пытаемся внести снова 
    """
    def test_UserErr500(self, create_user_before):
        http_client = Requester()  # создание клиента
        payload = create_user_before

        request = http_client.post_request(endpoint=endpoint, payload=payload)
        assert request.status_code == 500, f"Error has not got: {request.json()['message']}"