# ProjectGame

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
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

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.


```
io.github.game
│
├── core
│   ├── world              # Ядро модели мира
│   │   ├── hex            # Гексовая сетка, модели гексов
│   │   ├── region         # Понятие региона (может занимать N гексов)
│   │   └── time           # Модель игрового времени
│   │
│   ├── location           # Ядро модели локации
│   │   ├── graph          # Граф точек интереса и связей внутри локации
│   │   └── action         # Система действий, доступных на локации
│   │
│   └── gamecontext        # Контекст игры (состояние, доступ к мира/локации)
│
├── di                     # Внедрение зависимостей (остаётся, но расширяется)
│   ├── modules
│   │   ├── WorldModule.java
│   │   ├── LocationModule.java
│   │   └── ... 
│   └── AppComponent.java
│
├── ecs                    # Сущности и системы (сильно расширяется)
│   ├── components         # Компоненты
│   │   ├── world          # Для сущностей на глобальной карте (WorldEntity)
│   │   │   ├── HexPositionComponent.java   # Позиция на гексовой карте
│   │   │   └── MovementIntentComponent.java # Намерение переместиться
│   │   │
│   │   ├── battle         # Для сущностей в бою (BattleEntity)
│   │   │   ├── HealthComponent.java
│   │   │   ├── StatsComponent.java
│   │   │   └── TeamComponent.java
│   │   │
│   │   ├── common         # Общие для всех контекстов
│   │   │   ├── IdentityComponent.java
│   │   │   └── InventoryComponent.java
│   │   │
│   │   └── tags           # Тэги
│   │       ├── PlayerPartyComponent.java   # Группа игрока
│   │       └── ... 
│   │
│   ├── systems
│   │   ├── world          # Системы для глобальной карты
│   │   │   ├── WorldMovementSystem.java    # Перемещает группы по гексам
│   │   │   └── RegionSimulationSystem.java # Обновляет состояния регионов
│   │   │
│   │   ├── battle         # Системы для боя
│   │   │   ├── AutoBattleSystem.java       # Логика авто-боя
│   │   │   ├── BattleLogSystem.java        # Генерация лога
│   │   │   └── ...
│   │   │
│   │   └── common         # Общие системы (например, для локации)
│   │       └── ...
│   │
│   └── EntityFactory.java
│
├── view                   # Всё для отображения (заменяет старый ui)
│   ├── screens           # Экраны (GameScreen теперь не один)
│   │   ├── WorldMapScreen.java    # Отображение гексовой карты
│   │   ├── LocationScreen.java    # Текстовое меню/диалог локации
│   │   ├── BattleScreen.java      # Схематичное представление боя
│   │   └── ...
│   │
│   └── renderers          # Рендереры, отделенные от систем
│       ├── WorldMapRenderer.java  # Рендерит гексовую карту
│       ├── BattleRenderer.java    # Рендерит поле боя
│       └── ...
│
├── services               # Сервисы (расширяются)
│   ├── SaveLoadService.java       # Сервис сохранения/загрузки мира и прогресса
│   ├── LocationGenerator.java     # Генератор процедурных локаций
│   └── ...
│
└── utils                  # Утилиты (остаются)
    ├── GameSettings.java
    ├── ResourceManager.java
    └── ...
```



