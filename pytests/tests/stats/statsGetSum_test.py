#!/usr/bin/python3
# coding: utf-8
from tests.api import Requester
import pytest
from tests.tools.supplements import Stats
from pyhocon import ConfigFactory

conf = ConfigFactory.parse_file("./constants.conf")
endpoint = conf.get_string("endpoints.workStats")  # конечная точка url http://ip:port/time-tracker/endpoint

class TestStatsGetSum:

    """
    Получаем статистику по пользователю в режиме мода "sum"
    Вся статистика создается фикстурой
    """
    def test_getStatsSum(self, create_stats_before):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = Stats(
            user_id=create_stats_before["user_id"],
            mode="sum"
        ).__dict__

        request = http_client.get_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 200, f"Error get stats: {data['message']}"

        assert (data['user_id'] == payload["user_id"]
        and data['timeStatsSum'][0]['task_num'] == create_stats_before['task_num0']
        and data['timeStatsSum'][0]['duration'] == "00:01"
        and data['timeStatsSum'][1]['task_num'] == create_stats_before['task_num1']
        and data['timeStatsSum'][1]['duration'] == "00:01"
        and data['timeStatsSum'][2]['task_num'] == create_stats_before['task_num2']
        and data['timeStatsSum'][2]['duration'] == "00:01"
        ) #TODO не очень красивый блог, но пока что он работает как надо. Стоит обдумать как можно переделать

    

    """
    Получаем 404, т.к. по запрашиваемому пользователю нет статы
    Пользователь создается фикстурой
    """
    def test_EmptyStatsErr(self, create_user_before):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = Stats(
            user_id=create_user_before["user_id"],
            mode="sum"
        ).__dict__

        request = http_client.get_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 404, f"Error has not got: {data['message']}"