"""
Main service module to solve the mobile app requests
"""

from http import HTTPStatus
from flask import Flask, request

from .endpoints.v1.get_similarity_v1 import recognize
from .utils.utils import error_handling
from .config.config import Config

app = Flask(__name__)


@app.route("/")
def hello_world():
    """
    hello world
    """
    return "Hello, World!"


@app.route("/v1/similarity", methods=["POST"])
def get_v1_similarity():
    """
    gets the similarity to the celebrities based on trained model
    """
    if 'file' not in request.files:
        return error_handling("No file part", HTTPStatus.BAD_REQUEST)

    file = request.files['file']
    if file.filename == '':
        return error_handling("No selected file", HTTPStatus.BAD_REQUEST)

    resp, status_code = recognize(file.stream)

    return resp, status_code


if __name__ == '__main__':
    conf = Config()
    app.run(host=conf.host, port=conf.port, debug=conf.debug)
