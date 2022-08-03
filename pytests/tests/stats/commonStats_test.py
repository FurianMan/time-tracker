#!/usr/bin/python3
# coding: utf-8
from tests.api import Requester
import pytest
from tests.tools.supplements import Stats
from pyhocon import ConfigFactory

conf = ConfigFactory.parse_file("./constants.conf")
endpoint = conf.get_string("endpoints.workStats")  # конечная точка url http://ip:port/time-tracker/endpoint


"""
В этом классе общие тесты для методов получения статистики.
Тестируются они тут, потому что методы используют один и тот же код.
Поэтому чтобы не дублировать тесты, выносим их в отдельный класс.
Сюда попадают только тесты по принципу:
"Что работает для одного метода - справедливо будет и для остальных".
"""
class TestCommonStats:
    """
    Получаем 404, т.к. по запросу нет даже существующего пользователя
    """
    def test_UserNeverExistedErr(self):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = Stats(
            user_id=9998877
        ).__dict__

        request = http_client.get_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 404, f"Error has not got: {data['message']}"



    """
    Проверяем регулярки на дату, в тесте только ошибочные.
    Так же отсутствие одной из дат.
    Проверять можно в одном поле, т.к. регулярка одна и та же для двух полей.
    В данном случа достаточно фикстуры создания пользователей 
    """
    @pytest.mark.parametrize(
        "start_time, end_time",
        [
            ("1999:08:01 00:00:00", "2050-08-01 00:00:00"),  # неверные разделители в дате
            ("1999-08-01 00-00-00", "2050-08-01 00:00:00"),  # неверные разделители во времени
            ("1999-08-01 00.00.00", "2050-08-01 00:00:00"),  # неверные разделители во времени
            ("1999-13-01 00:00:00", "2050-08-01 00:00:00"),  # неверный месяц (13-ый)
            ("1999-12-32 00:00:00", "2050-08-01 00:00:00"),  # неверное число (32-ое)
            ("1999-12-32 00:00:60", "2050-08-01 00:00:00"),  # неверное время (60)
            ("1999-12-32 00:60:00", "2050-08-01 00:00:00"),  # неверное время (60)
            ("1999-12-32 24:00:00", "2050-08-01 00:00:00"),  # неверное время (24)
            (None, "2050-08-01 00:00:00"),  # нет start_time
            ("1999-08-01 00:00:00", None),  # нет end_time
        ],
    )
    def test_TimeErr(self, create_user_before, start_time, end_time):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = Stats(
            user_id=create_user_before["user_id"],
            start_time=start_time,
            end_time=end_time,
        ).__dict__

        request = http_client.get_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 415, f"Error get stats: {data['message']}"



    """
    Получаем 415, т.к. мод не соответствует "oneline"/"sum"/"period"
    """
    def test_ModeErr(self, create_user_before):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = Stats(
            user_id=create_user_before["user_id"],
            mode="idontknow"
        ).__dict__

        request = http_client.get_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 415, f"Error has not got: {data['message']}"
