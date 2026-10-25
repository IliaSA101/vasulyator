<img src="design/icon-512.png" width="96" alt="Иконка Васюлятра" align="right">

# Васюлятр

Калькулятор для ребёнка, который делит **с остатком**, как учат в школе:

```
10 : 3 = 3, остаток 1
Проверка: 3 × 3 + 1 = 10
```

Обычный калькулятор покажет `3,3333333`, а Васюлятр — неполное частное и остаток,
плюс строку-проверку, по которой ребёнок сам убедится, что ответ верный.
Сложение, вычитание и умножение тоже есть; все числа целые.

## Возможности

- Деление с остатком и подсказка-проверка (`частное × делитель + остаток = делимое`).
- Понятные сообщения вместо ошибок: «На ноль делить нельзя!», «Слишком большое число».
- Крупные кнопки, подписи для TalkBack, светлая и тёмная тема по настройке телефона.
- Состояние сохраняется при повороте экрана и после выгрузки приложения из памяти.
- Никаких разрешений, сети, рекламы и сбора данных.

## Архитектура

Один модуль, однонаправленный поток данных (UDF):

```
CalculatorScreen ──CalculatorKey──▶ CalculatorViewModel ──▶ CalculatorReducer ──▶ Arithmetic
       ▲                                   │
       └──────────── StateFlow<CalculatorState> ◀┘
```

| Пакет | Что внутри | Зависимости |
|---|---|---|
| `domain` | `Arithmetic` (чистая целочисленная арифметика), `CalculatorReducer` (состояние + нажатие → новое состояние) | только Kotlin |
| `ui` | `CalculatorViewModel` (StateFlow + SavedStateHandle), `CalculatorScreen` (stateless Compose), тема | AndroidX, Compose |

Вся логика — чистые функции в `domain`, поэтому покрывается быстрыми JVM-тестами
без эмулятора. Экран ничего не считает: только показывает состояние и отправляет нажатия.

## Сборка

Нужны JDK 17+ и Android SDK (проще всего — Android Studio).

```bash
./gradlew testDebugUnitTest   # юнит-тесты, меньше секунды
./gradlew lintDebug           # статический анализ
./gradlew assembleDebug       # app/build/outputs/apk/debug/app-debug.apk
```

### Установка на телефон

1. На телефоне: «Настройки → О телефоне» → 7 раз нажать на «Версия MIUI/HyperOS»,
   затем в «Для разработчиков» включить «Отладка по USB»
   (на Xiaomi также «Установка через USB»).
2. Подключить телефон кабелем и выполнить `./gradlew installDebug`
   либо нажать ▶ Run в Android Studio.

### Релизная сборка

`assembleRelease` собирает APK с R8 (минификация и удаление неиспользуемых ресурсов).
Ключ подписи хранится только локально: `*.jks`, `*.keystore` и `keystore.properties`
исключены через `.gitignore`.

## Иконка

Сгенерирована в Magnific (Nano Banana 2) по промту из [design/icon-prompt.md](design/icon-prompt.md),
фон убран через rembg, слои адаптивной иконки нарезаются скриптом
[design/make-icons.ps1](design/make-icons.ps1).

## Технологии

Kotlin 2.4 · Jetpack Compose (Material 3) · AndroidX Lifecycle · AGP 9.4 · Gradle 9.8 ·
minSdk 26 / targetSdk 37.

CI (GitHub Actions) на каждый push и PR проверяет `gradle-wrapper.jar`, гоняет тесты
и lint и собирает debug APK. Dependabot раз в месяц предлагает обновления зависимостей.

## Ветки

- `dev` — текущая работа, сюда идут все коммиты и PR Dependabot.
- `main` — стабильная история: изменения из `dev` попадают сюда через PR со squash merge.

## Лицензия

[MIT](LICENSE)
