# Beyond the Five Horizons (Stage 1 — Technical Foundation)

Minecraft Java Edition **1.21.1** | **Fabric** | **Java 21**

## Реализовано на Этапе 1
- Конфигурация Gradle + Fabric Loom (`1.8-SNAPSHOT`), Yarn (`1.21.1+build.3`), Fabric Loader (`0.16.9`), Fabric API (`0.107.0+1.21.1`).
- Модульная архитектура (`registry`, `dimension`, `portal`, `progression`, `block`, `item`, `client`).
- Регистрация базовых блоков (`ModBlocks`), предметов (`ModItems`), вкладки творчества (`ModItemGroups`), звуков (`ModSounds`) и команд (`ModCommands`).
- Дата-ориентированная регистрация всех 5 измерений (`dimension`, `dimension_type`, стартовые `worldgen/biome`):
  - `beyondfivehorizons:elysium` (Элизиум)
  - `beyondfivehorizons:chronos` (Хронос)
  - `beyondfivehorizons:astralia` (Астралия)
  - `beyondfivehorizons:underroot` (Подкоренье)
  - `beyondfivehorizons:ashen_crown` (Пепельная Корона)
- Безопасная система межпространственной телепортации (`HorizonTeleportManager`) и блок-основа Обелиска (`ObeliskCoreBlock`).
- Серверное сохранение прогрессии игрока (`HorizonProgressionState` через `PersistentState`).
- Команда отладки `/horizons tp <измерение|overworld>` для мгновенной проверки всех 5 измерений.
- Workflow `.github/workflows/build.yml` для автоматической сборки JAR через GitHub Actions.

## Сборка JAR
### Локально (требуется JDK 21 и Gradle 8.10+)
```bash
gradle build
```
Готовый JAR появится в `build/libs/beyondfivehorizons-0.1.0-stage1.jar`.

### Через GitHub Actions
Загрузите содержимое архива в репозиторий GitHub — во вкладке **Actions** автоматически запустится сборка и сформируется артефакт с JAR-файлом.
