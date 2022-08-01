#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import requests
from pyhocon import ConfigFactory

conf = ConfigFactory.parse_file("./constants.conf")
server_address = conf.get_string("server_params.ip")
port = conf.get_string("server_params.port")

class Requester:
    def __init__(self, server_address:str=server_address, port:str=port):
        self.server_address = server_address
        self.port = port
        self.path = f"http://{server_address}:{port}/time-tracker/" 

    def get_request(self, endpoint:str, payload:dict):
        return requests.get(f"{self.path}{endpoint}", json=payload)
    
    def post_request(self, endpoint:str, payload:dict):
        return requests.post(f"{self.path}{endpoint}", json=payload)

    def put_request(self, endpoint:str, payload:dict):
        return requests.put(f"{self.path}{endpoint}", json=payload)

    def delete_request(self, endpoint:str, payload:dict):
        return requests.delete(f"{self.path}{endpoint}", json=payload)