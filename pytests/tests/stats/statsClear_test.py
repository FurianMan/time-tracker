#!/usr/bin/python3
# coding: utf-8
from tests.api import Requester
import pytest
from tests.tools.supplements import Stats
from pyhocon import ConfigFactory

conf = ConfigFactory.parse_file("./constants.conf")
endpoint = conf.get_string("endpoints.workStats")  # конечная точка url http://ip:port/time-tracker/endpoint

class TestStatsClear:

    """
    Удаляем статистику по пользователю.
    """
    def test_statsClear(self, create_stats_before):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = Stats(
            user_id=create_stats_before["user_id"],
        ).__dict__

        request = http_client.delete_request(endpoint=endpoint, payload=payload)
        assert request.status_code == 200, f"Error get stats: {request.json()['message']}"


        getRequest = http_client.get_request(endpoint=endpoint, payload=payload)
        getData = getRequest.json()
        assert getRequest.status_code == 200, f"Error get stats: {getData['message']}"

        # Если стата удалена, то будет 00:00
        assert (getData['user_id'] == payload["user_id"] 
        and getData['timeStatsOneline'] == "00:00")


    

    """
    Удаляем статистику по несуществующему пользователю.
    Ждем 404, пользователь не найден.
    """
    def test_statsClearUserNeverExisted(self):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = Stats(
            user_id=898584,
        ).__dict__

        request = http_client.delete_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 404, f"Error has not got: {data['message']}"

    

    """
    Не указали user_id вообще.
    415 ловим
    """
    def test_statsClearWithoutUserId(self):
        http_client = Requester()

        # берем созданного пользователя из фикстуры словарем
        payload = Stats(
            user_id=None,
        ).__dict__

        request = http_client.delete_request(endpoint=endpoint, payload=payload)
        data = request.json()
        assert request.status_code == 415, f"Error has not got: {data['message']}"