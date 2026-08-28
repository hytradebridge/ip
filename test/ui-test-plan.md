# UI Test Plan

This file defines automated UI tests for the AXIOM chatbot. Each test case runs a full
interactive session: inputs are piped to the program, and the complete stdout is compared
against the expected output.

**Run command:** `java src/main/java/Axiom.java`

**Test runner:** `python3 test/run-ui-tests.py`

On macOS, ensure Java 25 is active before running tests:
`sdk use java 25.0.3.fx-zulu`

---

## Test Case 1: Add todo

**Aim:** Verify that `todo` adds a todo task and confirms with the new message format.

**Inputs:**
```
todo read book
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 2: List empty tasks

**Aim:** Verify that `list` on an empty task list shows the header with no numbered tasks.

**Inputs:**
```
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 3: Add todo, deadline, event and list

**Aim:** Verify that all three task types are stored and listed with correct type prefixes.

**Inputs:**
```
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Sunday)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 4: Deadline with arbitrary by string

**Aim:** Verify that the `/by` value is stored as a plain string without parsing.

**Inputs:**
```
deadline do homework /by no idea :-p
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] do homework (by: no idea :-p)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] do homework (by: no idea :-p)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 5: Mark and unmark todo

**Aim:** Verify that mark and unmark work with todo tasks and update the `[T]` status icon.

**Inputs:**
```
todo read book
todo return book
todo buy bread
mark 2
list
unmark 2
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] return book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy bread
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] return book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][X] return book
 3.[T][ ] buy bread
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] return book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] return book
 3.[T][ ] buy bread
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 6: Empty todo description

**Aim:** Verify that `todo` without a description shows an error and does not add a task.

**Inputs:**
```
todo
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 A todo needs a description. Usage: todo <description>
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 7: Unknown command

**Aim:** Verify that an unrecognised command shows an error message.

**Inputs:**
```
blah
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Sorry, I don't understand that command.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 8: Deadline missing /by

**Aim:** Verify that a deadline without `/by` is rejected with a helpful message.

**Inputs:**
```
deadline homework
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 A deadline must include /by. Usage: deadline <description> /by <time>
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 9: Invalid mark command

**Aim:** Verify that `mark` without a number and `mark` with an out-of-range number both produce errors.

**Inputs:**
```
mark
mark 99
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Please specify which task to mark. Usage: mark <task number>
____________________________________________________________
____________________________________________________________
 That task number isn't in your list. You currently have 0 task(s).
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 10: Continue after error

**Aim:** Verify that the chatbot keeps running after an error and still accepts valid commands.

**Inputs:**
```
todo read book
blah
todo return book
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Sorry, I don't understand that command.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] return book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] return book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 11: Event missing /to

**Aim:** Verify that an event without `/to` is rejected with a helpful message.

**Inputs:**
```
event meeting /from 2pm
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 An event must include /from and /to. Usage: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```
