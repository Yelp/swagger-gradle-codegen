.PHONY: install-hooks regenerate-samples test

.git/hooks/pre-commit: venv
	${CURDIR}/venv/bin/pre-commit install --install-hooks

install-hooks: .git/hooks/pre-commit
	@true

regenerate-samples:
	${CURDIR}/gradlew plugin:build
	${CURDIR}/gradlew plugin:publishToMavenLocal
	${CURDIR}/gradlew generateSwagger

test:
	${CURDIR}/gradlew plugin:build
	${CURDIR}/gradlew plugin:publishToMavenLocal
	${CURDIR}/gradlew generateSwagger
	${CURDIR}/gradlew assembleDebug
	${CURDIR}/gradlew check

venv:
	virtualenv venv
	./venv/bin/pip install pre-commit
