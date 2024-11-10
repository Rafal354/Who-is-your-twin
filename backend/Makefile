prepare_venv:
	python3 -m venv venv

install_dependencies:
	pip3 install -r requirements.cfg

docker_build:
	docker build --tag flask-service .

docker_run:
	echo "exposing on port 5001"
	docker run -p 5001:5000 flask-service

docker_all:
	$(MAKE) docker_build
	$(MAKE) docker_run
