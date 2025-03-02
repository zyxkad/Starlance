# Cosmic Horizons x Valkyrien Skies

Current (done) features:
- Redstone thrusters (global and position modes)
- Atmospheric collision: VS ships can get to space (works but has bugs, especially in multiplayer)
- Redstone air thrusters (weak thruster) (global and position modes)
- Planet collision: VS ships can enter planets (working on controlling landing location)
- Drag Inducer block, weak thruster that always thrusts retrograde when redstone powered
- Different VS gravity per dimensions
- Create ponders (for optional compat)

WIP features:
- Multi-ship teleporting (e.g. attached VMod ships)

Planned features:
- Captains chair, allows warping of VS ships and priority over planet coll GUI
- CC Compat
- Fixed multiplayer :(


## Contributing
We welcome contributions, and will generally approve PR's if we agree with their features.
However, we do ask that you try to stick to our loose style guide, so the code can be somewhat consistent.

- 1 tab for indentation
- CamelCase class names (Java usually uses this anyway)
- CamelCase variable and function names
- Function names should easily explain what it does, javadoc should be used if a longer explanation is needed
- Variable names should describe what they store, not what they’re used for. E.g. "targetDimension" instead of "dimensionGoingTo"
- `static final` variables should have all caps names (e.g. `public static final MODID` not `public static final modid`)
- If a function is only used in one class, no need to put it in a util class
- Avoid multiple casts on one line, it can get confusing (use multiple lines or multiple variables, or both)
- Organising classes:
  - The class or interface itself should be at the top, private classes and interfaces should go at the bottom
  - For class variables, define `static` variables, then `instance` variables. Use the order:
    - `public`
    - `protected`
    - `private`
  - Functions don't need to be sorted, simple use an order that is easiest to look through.
  (So overloads and related functions should go next to each other)