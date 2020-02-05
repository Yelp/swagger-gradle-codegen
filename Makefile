.PHONY: install-hooks regenerate-samples test run-hooks

.git/hooks/pre-commit: venv
	${CURDIR}/venv/bin/pre-commit install --install-hooks

install-hooks: .git/hooks/pre-commit
	@true


run-hooks: venv
	${CURDIR}/venv/bin/pre-commit run --all-files

test: run-hooks
	${CURDIR}/gradlew plugin:build check
	${CURDIR}/gradlew -p samples/ generateSwagger
	${CURDIR}/gradlew -p samples/ assembleDebug
	${CURDIR}/gradlew -p samples/ check

venv:
	virtualenv venv
	./venv/bin/pip install pre-commit
