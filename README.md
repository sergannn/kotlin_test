# Monitor PoC

Минимальный Android PoC на Kotlin + Jetpack Compose по тестовому заданию.

## Что сделано

- Login экран с email/password и fake access token.
- Token хранится через `EncryptedSharedPreferences`, а не в обычных `SharedPreferences`.
- Список из 80 моковых объектов: поиск, фильтр по статусу, сортировка по времени.
- Экран деталей объекта с параметрами и partial state, если часть данных отсутствует.
- Canvas-график на 240 точек без chart-библиотеки.
- Room-кэш для списка, карточки, параметров и точек графика.
- Loading / error / empty / content / cached состояния.
- Logout очищает token и Room-кэш.

## Как запустить

1. Открыть проект в Android Studio.
2. Дождаться Gradle Sync.
3. Запустить конфигурацию `app` на эмуляторе или устройстве.

Тестовый вход уже подставлен:

- email: `test@example.com`
- password: `1234`

## Архитектура простыми словами

Проект специально сделан без Hilt/Koin, чтобы было проще понять поток данных.

- `data/remote` - `FakeApiService`, моковый API. Он имитирует задержку, возвращает DTO и умеет один раз упасть по кнопке `API error`.
- `data/local` - Room: Entity, DAO, Database.
- `data/repository` - repository. Сначала пробует получить данные из fake API, сохраняет их в Room, а при ошибке показывает кэш, если он есть.
- `domain` - чистые модели приложения: `ObjectItem`, `ObjectDetails`, `Parameter`, `ChartPoint`.
- `ui` - ViewModel и явные UI-состояния.
- `ui/screens` - Compose-экраны. В них нет прямой работы с Room или API.
- `security` - защищенное хранение fake token.
- `di/AppContainer.kt` - ручная сборка зависимостей.

Главная цепочка:

```text
FakeApiService -> MonitorRepository -> ViewModel -> Compose Screen
                     |
                    Room
```

## Кэш

Room не считается главным источником истины. Repository сначала делает запрос в fake API. Если запрос успешен, данные сохраняются в Room. Если запрос упал, но в Room уже есть данные, UI показывает cached banner.

Чтобы проверить cached state:

1. Войти в приложение.
2. Дождаться загрузки списка.
3. Нажать `API error`.
4. Repository получит ошибку fake API и покажет список из Room-кэша.

## Что упрощено для PoC

- Нет реального backend и настоящей авторизации.
- Нет полноценной навигационной библиотеки, экраны переключаются простым sealed state в `MainActivity`.
- Нет unit-тестов, чтобы не усложнять учебный PoC.
- UI простой, без детального дизайна.
- Кэш очищается полностью при logout.

## Что улучшить для production MVP

- Добавить реальный API-клиент на Retrofit или Ktor.
- Перейти на Hilt/Koin для DI.
- Добавить Navigation Compose.
- Добавить миграции Room и более строгую стратегию устаревания кэша.
- Покрыть repository и ViewModel unit-тестами.
- Добавить retry/backoff, аналитику ошибок и нормальную авторизацию.
