#!/usr/bin/env python3
"""Run UI tests defined in test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


TEST_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = TEST_DIR.parent
PLAN_FILE = TEST_DIR / "ui-test-plan.md"


@dataclass
class TestCase:
    number: int
    name: str
    aim: str
    inputs: str
    expected_output: str


def parse_plan(content: str) -> tuple[list[str], list[TestCase]]:
    run_command_match = re.search(r"\*\*Run command:\*\*\s*`([^`]+)`", content)
    if not run_command_match:
        raise ValueError("ui-test-plan.md must define **Run command:** `...`")

    run_command = run_command_match.group(1).strip()
    run_parts = run_command.split()

    case_pattern = re.compile(
        r"^## Test Case (\d+):\s*(.+?)\s*\n\n"
        r"\*\*Aim:\*\*\s*(.+?)\n\n"
        r"\*\*Inputs:\*\*\n```\n(.*?)\n```\n\n"
        r"\*\*Expected output:\*\*\n```\n(.*?)\n```",
        re.MULTILINE | re.DOTALL,
    )

    cases: list[TestCase] = []
    for match in case_pattern.finditer(content):
        cases.append(
            TestCase(
                number=int(match.group(1)),
                name=match.group(2).strip(),
                aim=match.group(3).strip(),
                inputs=match.group(4),
                expected_output=match.group(5),
            )
        )

    if not cases:
        raise ValueError("No test cases found in ui-test-plan.md")

    return run_parts, cases


def run_program(run_parts: list[str], inputs: str) -> str:
    result = subprocess.run(
        run_parts,
        input=inputs,
        capture_output=True,
        text=True,
        cwd=PROJECT_ROOT,
    )
    if result.returncode != 0:
        stderr = result.stderr.strip()
        raise RuntimeError(
            f"Program exited with code {result.returncode}"
            + (f"\n{stderr}" if stderr else "")
        )
    return result.stdout


def print_session_header(case: TestCase) -> None:
    print(f"=== Test Case {case.number}: {case.name} ===")
    print(f"Aim: {case.aim}")
    print()
    print("--- Input ---")
    print(case.inputs)
    print()
    print("--- Output ---")


def main() -> int:
    if not PLAN_FILE.is_file():
        print(f"Test plan not found: {PLAN_FILE}", file=sys.stderr)
        return 1

    content = PLAN_FILE.read_text(encoding="utf-8")
    try:
        run_parts, cases = parse_plan(content)
    except ValueError as error:
        print(f"Failed to parse test plan: {error}", file=sys.stderr)
        return 1

    print("UI Test Session")
    print(f"Plan: {PLAN_FILE.relative_to(PROJECT_ROOT)}")
    print(f"Run command: {' '.join(run_parts)}")
    print(f"Test cases: {len(cases)}")
    print()

    passed = 0
    for case in cases:
        print_session_header(case)
        try:
            actual_output = run_program(run_parts, case.inputs)
        except RuntimeError as error:
            print(error)
            print()
            print("--- Result ---")
            print("FAIL (program error)")
            return 1

        print(actual_output, end="" if actual_output.endswith("\n") else "\n")
        print()
        print("--- Result ---")

        if actual_output == case.expected_output:
            print("PASS")
            passed += 1
            print()
            continue

        print("FAIL")
        print()
        print("Expected output:")
        print("```")
        print(case.expected_output, end="" if case.expected_output.endswith("\n") else "\n")
        print("```")
        print()
        print("Actual output:")
        print("```")
        print(actual_output, end="" if actual_output.endswith("\n") else "\n")
        print("```")
        print()
        print(f"Test session terminated after Test Case {case.number}: {case.name}")
        return 1

    print(f"All {passed} test case(s) passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
