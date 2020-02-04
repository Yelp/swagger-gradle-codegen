.PHONY: install-hooks regenerate-samples test

.git/hooks/pre-commit: venv
	${CURDIR}/venv/bin/pre-commit install --install-hooks

install-hooks: .git/hooks/pre-commit
	@true

regenerate-samples:
	${CURDIR}/gradlew plugin:build
	${CURDIR}/gradlew plugin:publishToMavenLocal
	${CURDIR}/gradlew generateSwagger

run-hooks: install-hooks
	${CURDIR}/venv/bin/pre-commit run --all-files

test: run-hooks
	${CURDIR}/gradlew plugin:build
	${CURDIR}/gradlew plugin:publishToMavenLocal
	${CURDIR}/gradlew generateSwagger
	${CURDIR}/gradlew assembleDebug
	${CURDIR}/gradlew check
	deactivate

venv:
	virtualenv venv
	./venv/bin/pip install pre-commit
