# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Advanced
* IDE and level of expertise: Advanced

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Testing

This project uses two layers of automated tests:

* **UI tests** — console session tests in `test/ui-test-plan.md`, run via `python3 test/run-ui-tests.py`.
* **JUnit tests** — unit tests under `src/test/java/`, run via `./gradlew test`.

### JUnit test coverage target

Aim to cover the **top ~50% highest-value methods** in the codebase. Prioritize complex, core, or critical business logic (e.g. parsing, command handling, task-list operations) over thin wrappers, I/O, or the main application loop.

When adding or changing production code, **update the relevant JUnit tests in the same change** so they stay aligned with behavior and continue to meet the ~50% coverage target. Run `./gradlew test` before considering the work complete.

### JUnit conventions

* Mirror the main source package structure under `src/test/java/` (e.g. `axiom.task.Todo` → `axiom.task.TodoTest`).
* Name test classes `<ClassUnderTest>Test`.
* For longer test names, use `featureUnderTest_testScenario_expectedBehavior()` (e.g. `parse_invalidFormat_exceptionThrown()`).
