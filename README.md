# ProjectGame

A [libGDX](https://libgdx.com/) project generated
with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an
`ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew`
commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot
  versions.
- `android:lint`: performs Android project validation.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where
the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.

Следующие шаги

1) Создание фасада для мира:

    * Интерфейс WorldFacade для абстракции доступа к миру

    * Реализация WorldFacadeImpl для инкапсуляции логики мира

2) Создание абстракции для рендеринга:

    * Интерфейс WorldRenderer для абстракции рендеринга

    * Реализация HexMapWorldRenderer для конкретной реализации

3) Уменьшение зависимостей GameScreen:

    * Замена конкретных зависимостей на абстракции

    * Упрощение конструктора GameScreen

4) Создание менеджера миров:

    * Для управления несколькими мирами или уровнями

    * Для обработки переходов между мирам

```
io.github.game

game
├── core
│   └── world
│       ├── generator
│       │   ├── RectangularWorldGenerator.java
│       │   └── WorldGenerator.java
│       ├── hex
│       │   ├── Hex.java
│       │   ├── HexCoordinates.java
│       │   ├── HexType.java
│       │   └── HexUtils.java
│       └── HexMap.java
├── di
│   ├── modules
│   │   ├── CoreModule.java
│   │   ├── ECSModule.java
│   │   ├── GraphicsModule.java
│   │   ├── InputModule.java
│   │   ├── MonitoringModule.java
│   │   ├── ScreenModule.java
│   │   ├── ServicesModule.java
│   │   ├── SettingsModule.java
│   │   └── WorldModule.java
│   └── AppComponent.java
├── ecs
│   ├── components
│   │   ├── tags
│   │   │   ├── NPCComponent.java
│   │   │   └── PlayerComponent.java
│   │   ├── world
│   │   │   └── HexComponent.java
│   │   ├── PositionComponent.java
│   │   ├── RenderComponent.java
│   │   └── VelocityComponent.java
│   ├── systems
│   │   ├── world
│   │   │   └── WorldSimulationSystem.java
│   │   ├── CameraControlSystem.java
│   │   ├── MovementSystem.java
│   │   ├── NPCLogicSystem.java
│   │   ├── PlayerInputSystem.java
│   │   └── RenderingSystem.java
│   └── EntityFactory.java
├── input
│   ├── BattleInputProcessor.java
│   ├── InputManager.java
│   ├── InputMode.java
│   └── WorldMapInputProcessor.java
├── monitoring
│   ├── PerformanceLogger.java
│   └── PerformanceMonitor.java
├── renderer
│   └── HexMapRenderer.java
├── services
│   ├── AssetService.java
│   ├── CharacterEntityService.java
│   ├── InputService.java
│   └── WorldEntityService.java
├── settings
│   ├── impl
│   │   ├── AudioSettingsImpl.java
│   │   ├── BaseSettingsImpl.java
│   │   ├── CameraSettingsImpl.java
│   │   ├── GameplaySettingsImpl.java
│   │   ├── GraphicsSettingsImpl.java
│   │   └── SettingsFacade.java
│   ├── AudioSettings.java
│   ├── BaseSettings.java
│   ├── CameraSettings.java
│   ├── GameplaySettings.java
│   └── GraphicsSettings.java
├── ui
│   └── screens
│       ├── GameScreen.java
│       ├── LoadingScreen.java
│       ├── ScreenFactory.java
│       ├── ScreenFactoryImpl.java
│       └── ScreenSwitcher.java
├── utils
│   ├── validation
│   │   └── SettingsValidator.java
│   ├── GameSettingsConstants.java
│   ├── MemoryUtils.java
│   └── ResourceManager.java
└── MainGame.java
```



