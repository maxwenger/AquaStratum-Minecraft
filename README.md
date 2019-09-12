# AquaStratum

Requirements:
* JDK 1.8

How to build (IntelliJ):
1. Clone the project
2. Import  `gradle.build` into IntelliJ
3. Wait for gradle to stop building
4. Go to Gradle -> AquaStratum -> setupDecomp workspace (should take a little bit of time)
5. Create a directoy called `run` in the project root.
6. Add new Run/Debug configureaton.
7. Press the `+` in the top left
8. Select Application
9. Name it `Client`
10. Main Class: `GradleStart`
11. Working directory: [Project Locaton]`\run`
12. Run the configureation, the Minecraft client should start!
