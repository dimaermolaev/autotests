# language: ru
Функционал: Framework testing

  @link=https://wiki.digital-spirit.ru/display/DSTST/JSON

  @test @json
  Сценарий: Проверка функционала по работе с JSON

    # Работа с JSON
    * Выполнить SQL запрос в базу 'qa' и сохранить результат в переменной 'responseVar'
    """
    select *
    from properties
    where var_name = 'test_json'
    """

    # Шаг для получения объекта JSON JsonElement из SQL-ответа
    * Из переменной 'responseVar' получить JSON и сохранить в переменной 'contentVar'
      | type  | json      |
      | field | VAR_VALUE |
    * Вывести на экран значение переменной 'contentVar'

    # Шаг с проверкой значения в теге по JsonPath в объекте JsonElement
    * Проверить, что в переменной из JSON-объекта 'contentVar' значения совпадают:
      | tin   | 12345678 |
      | pinfl | 100500   |

    # Шаг позволяет сохранить в памяти текстовой значение тега из объекта JsonElement по jsonPath
    * Из переменной 'contentVar' сохранить значение 'cadNum' в переменную 'cadNumVar'
    * Вывести на экран значение переменной 'cadNumVar'

    # Шаг для получения объекта JsonElement из SQL-ответа
    * Из переменной 'responseVar' получить JSON и сохранить в переменной 'contentVar1'
      | type  | json      |
      | field | VAR_VALUE |

    * Из переменной 'responseVar' сохранить значение 'VAR_VALUE' в переменную 'savedJSON'
    * Вывести на экран значение переменной 'savedJSON'

    # Шаг для сравнения двух JSON сохраненных в переменных
    * Проверить, что json в переменной 'savedJSON' совпадает с 'contentVar1'
