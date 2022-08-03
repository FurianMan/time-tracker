#!/usr/bin/python3
# coding: utf-8
from tests.api import Requester
import pytest
from tests.tools.supplements import Stats
from pyhocon import ConfigFactory

conf = ConfigFactory.parse_file("./constants.conf")
endpoint = conf.get_string("endpoints.workStats")  # конечная точка url http://ip:port/time-tracker/endpoint

class TestStatsGetOneline:

    """
    Получаем статистику по пользователю в режиме мода "oneline"
    Вся статистика создается фикстурой
    """
    def test_getStatsOneline(self, create_stats_before):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = Stats(
            user_id=create_stats_before["user_id"],
        ).__dict__

        request = http_client.get_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 200, f"Error get stats: {data['message']}"

        assert (data['user_id'] == payload["user_id"] 
        and data['timeStatsOneline'] == "00:03") # получаем значение в hh:mm , оно всегда равно 3 минуты, т.к. 
                                                          # фикстура закрывает каждую задачу через 3 минуты


    
    """
    Если по пользователю нет статы, то выдаст 00:00
    """
    def test_EmptyStatsErr(self, create_user_before):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = Stats(
            user_id=create_user_before["user_id"],
        ).__dict__

        request = http_client.get_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 200, f"Error get stats: {data['message']}"

        assert (data['user_id'] == payload["user_id"] 
        and data['timeStatsOneline'] == "00:00")
