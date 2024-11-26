"""
base app config
"""

import json

class Config:
    """
    basic config loaded for the app
    """
    def __init__(self) -> None:
        conf_file = {}
        with open("./config.json", encoding='utf-8') as fp:
            conf_file = json.load(fp)

        self.trained_models_path = conf_file["trained_models_path"]
        self.model_name = conf_file["model_name"]
        self.port = conf_file["port"]
        self.host = conf_file["host"]
        self.debug = conf_file["debug"]
