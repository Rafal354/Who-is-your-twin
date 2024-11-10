FROM python:3.12.7-slim-bookworm

WORKDIR /app

COPY requirements.cfg requirements.cfg
COPY rest_api_service/ .
RUN pip3 --version
RUN pip3 install -r requirements.cfg

COPY . .

CMD [ "python3", "-m" , "flask", "run", "--host=0.0.0.0"]