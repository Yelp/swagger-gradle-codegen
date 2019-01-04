.PHONY: install-hooks

venv:
	virtualenv venv
	./venv/bin/pip install pre-commit

install-hooks: venv
	./venv/bin/pre-commit install --install-hooks
