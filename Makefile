.PHONY: install-hooks

.git/hooks/pre-commit: venv
	${CURDIR}/venv/bin/pre-commit install --install-hooks

install-hooks: .git/hooks/pre-commit
	@true

venv:
	virtualenv venv
	./venv/bin/pip install pre-commit
