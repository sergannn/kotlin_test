# Инструкция и объяснение проекта

## Что это за проект

Это маленькое Android-приложение на Kotlin + Jetpack Compose для тестового задания.

Идея приложения: показать список обезличенных объектов мониторинга, открыть карточку объекта и посмотреть график значений.

Это не настоящий продукт и не часть реального проекта. Это PoC, то есть proof of concept: небольшая проверка, что в проекте есть основные технические части.

## Что проверяет задание

В задании хотели увидеть не красивый дизайн, а понимание Android-архитектуры:

- экран входа;
- fake access token;
- защищенное хранение token;
- список объектов;
- поиск, фильтр, сортировка;
- карточку объекта;
- обработку loading / error / empty / content / partial states;
- Room-кэш;
- Canvas-график;
- разделение кода на слои.

## Как запустить

1. Открыть папку проекта в Android Studio.
2. Дождаться Gradle Sync.
3. Выбрать конфигурацию `app`.
4. Запустить на эмуляторе или Android-устройстве.

Тестовые данные для входа уже подставлены на экране:

```text
email: test@example.com
password: 1234
```

## Сборка APK

Команда для сборки debug APK:

```bash
./gradlew assembleDebug
```

Если нужно пересобрать APK в Android Studio:

1. Открыть проект.
2. Дождаться Gradle Sync.
3. Нажать `Build > Build Bundle(s) / APK(s) > Build APK(s)`.
4. APK появится примерно здесь:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Или через терминал:

```bash
./gradlew assembleDebug
```

## Простое объяснение архитектуры

Проект разделен на несколько частей, чтобы вся логика не лежала в Compose-экранах.

```text
FakeApiService -> MonitorRepository -> ViewModel -> Compose Screen
                     |
                    Room
```

### `FakeApiService`

Файл:

```text
app/src/main/java/com/example/monitorpoc/data/remote/FakeApiService.kt
```

Это моковый API. Реального backend нет. Класс сам генерирует объекты, детали и точки графика.

Он нужен, чтобы показать, что сетевой слой отделен от UI.

### `Room`

Файлы:

```text
data/local/AppDatabase.kt
data/local/AppDao.kt
data/local/Entities.kt
```

Room хранит локальный кэш:

- список объектов;
- детали объекта;
- параметры;
- точки графика.

Кэш нужен, чтобы при ошибке fake API можно было показать старые данные.

### `MonitorRepository`

Файл:

```text
data/repository/MonitorRepository.kt
```

Repository решает, откуда брать данные.

Логика такая:

1. Сначала пробуем получить данные из fake API.
2. Если получилось, сохраняем их в Room.
3. Если API упал, пробуем показать данные из Room.
4. Если кэша нет, показываем ошибку.

Это важно: Room здесь не главный источник истины, а именно кэш.

### `ViewModel`

Файл:

```text
ui/ViewModels.kt
```

ViewModel хранит состояние экрана и вызывает repository.

Compose-экран не делает запросы напрямую. Он только показывает состояние.

### `UiState`

Файл:

```text
ui/UiState.kt
```

Это sealed interface для состояний:

```kotlin
Loading
Empty
Error
Content
```

Так проще не смешивать загрузку, ошибку и реальные данные.

### `TokenStorage`

Файл:

```text
security/TokenStorage.kt
```

Fake token сохраняется через `EncryptedSharedPreferences`.

Это сделано потому, что по заданию нельзя хранить token в обычных `SharedPreferences`.

### Compose screens

Файлы:

```text
ui/screens/LoginScreen.kt
ui/screens/ListScreen.kt
ui/screens/DetailsScreen.kt
ui/screens/ChartScreen.kt
```

Это UI-экраны:

- `LoginScreen` - вход;
- `ListScreen` - список объектов;
- `DetailsScreen` - карточка объекта;
- `ChartScreen` - график.

В экранах нет прямой работы с Room или fake API.

## Как проверить кэш

1. Войти в приложение.
2. Дождаться списка объектов.
3. Нажать кнопку `API error`.
4. Fake API специально упадет.
5. Repository покажет данные из Room-кэша.
6. На экране появится сообщение, что данные взяты из кэша.

## Как проверить partial state

Открой разные объекты в списке.

У части объектов есть:

- `currentValue = missing`;
- параметры со значением `missing`;
- предупреждение `Partial data`.

Это показывает, что UI не подставляет ложные значения, если данных нет.

## Как проверить график

1. Открыть объект.
2. Нажать `Open chart`.
3. Откроется Canvas-график.

График рисуется вручную через Compose `Canvas`, без готовой chart-библиотеки.

У некоторых объектов графика нет. Тогда показывается empty state.

## Что специально упрощено

- Нет реального backend.
- Нет настоящей авторизации.
- Нет Hilt/Koin, используется простой ручной DI в `AppContainer`.
- Нет Navigation Compose, экраны переключаются через простой sealed state.
- Нет unit-тестов.
- UI простой, потому что задание проверяет архитектуру и состояния, а не дизайн.

## Что можно улучшить дальше

Если бы это был production MVP:

- добавить Retrofit или Ktor;
- добавить Hilt или Koin;
- добавить Navigation Compose;
- добавить unit-тесты для repository и ViewModel;
- сделать нормальные Room migrations;
- добавить стратегию устаревания кэша;
- добавить настоящую авторизацию.
