This directory contains code which can't or shouldn't be covered by unit tests.  
Separating this out lets us aim for 100% test coverage in the rest of the code.
Untested code includes:

- Object construction and object graph construction
- Enums and other constants
- Classes derived directly from Android class.