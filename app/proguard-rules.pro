# Правила R8 для релиза. Базовые правила берутся из proguard-android-optimize.txt,
# AndroidX и Compose приносят свои consumer-правила сами.
#
# Рефлексии в приложении нет, имена классов нигде не сохраняются (SavedStateHandle
# хранит только строки и Boolean), поэтому дополнительные -keep не нужны.
# Если что-то сломается после минификации — правило добавляется сюда с комментарием «почему».

# Читаемые стектрейсы в отчётах о падениях: номера строк сохраняем, имена файлов скрываем.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
