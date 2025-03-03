# Educational Game Engine

**If you want to learn how game engines and 3D graphics work and how to create
your own engine, this project is for you.**

The goal of this educational game engine project is to help you learn what
happens under the hood, just like creating it helped me learn more about the
technical details. It's not intended to be used for serious game projects,
although you can use it to create your own games if you want to understand
in detail what happens between the player's input and the final image on
the screen. Along with the game engine I'm developing a sample game to
incrementally drive the engine's requirements and demo its features.

![In-engine screenshot of example scene.](chess-sample-scene.png)

This engine does many things in code running on the CPU that would normally
happen in hardware, e.g., on the GPU. Also, it is implemented in Kotlin running
in the JVM because of the educational focus, whereas a commercial game engine
would typically be implemented in C or C++. As a result, although you can achieve
pretty nice looking graphics with it, if you want decent framerates for your game
you will have to pretend it's the nineties and you're programming for one of the
[early game consoles](https://en.wikipedia.org/wiki/Sixth_generation_of_video_game_consoles).

Note that the project is still in an early stage and will receive many significant
and breaking changes.

## Contribution

Currently I am not taking any feature requests or bug reports.

## License

The MIT License. Please refer to the license file.
