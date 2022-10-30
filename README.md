# airports-search
Консольное приложение, позволяющее быстро искать данные в CSV файле по вводимому тексту. 
Текущий файл (airports.csv) - данные аэропортов 
# Запуск
```
mvn clean package
java -Xmx7m -jar target/airports-search.jar X
X - обязательный аргумент - номер столбца, отсчет с 1
```
#Завершение программы
``
Для того, чтобы завершить программу нужно ввести !quit на запрос ввода.
``
#Основные спецификации
```
Для строк используется лексикографическая сортировка
Для чисел числовая
Если в колонке, где в ячейках в основном идут числа встречается строка,
сначала выводятся строки, потом числа
```
