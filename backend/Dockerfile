FROM python:3.12.7-slim-bookworm

WORKDIR /app

COPY requirements.cfg requirements.cfg
RUN pip install --upgrade pip  # Ensure latest pip version
RUN pip install --no-cache-dir -r requirements.cfg  # Install dependencies with hashes

COPY . .

CMD [ "python3", "-m", "flask", "run", "--host=0.0.0.0" ]