.PHONY: install-hooks regenerate-samples test run-hooks

.git/hooks/pre-commit: venv
	${CURDIR}/venv/bin/pre-commit install --install-hooks

install-hooks: .git/hooks/pre-commit
	@true

run-hooks: venv
	${CURDIR}/venv/bin/pre-commit run --all-files

test: run-hooks
	${CURDIR}/gradlew -p plugin-root build check
	${CURDIR}/gradlew generateSwagger
	# The check task requires a lot of MetaSpace
	${CURDIR}/gradlew assembleDebug check -Dorg.gradle.jvmargs="-Xmx4g -XX:MaxMetaspaceSize=2g"

venv:
	virtualenv venv
	./venv/bin/pip install pre-commit
