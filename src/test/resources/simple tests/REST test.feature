# language: ru
Функционал: Framework testing

  @link=https://wiki.digital-spirit.ru/display/DSTST/REST

  @test @rest
  Сценарий: Проверка функционала по работе с HTTP-запросами

    # Работа с HTTTP-запросами
#    * В переменной 'digital-spirit' сохранено значение 'Digital Spirit - cooperation based on trust'

    # Простейший пример запроса
    * Выполнить HTTP-запрос с параметрами, результат сохранить в переменной 'restVar':
      | type | GET                        |
      | uri  | https://digital-spirit.ru/ |

    * Вывести на экран значение переменной 'restVar'

    # Шаг проверяющий, что в ответе запроса вернулись корректные значения
    # Headers имеет формат key=value
    #          где key - имя заголовка
    #              value - значение
    #
    # Body имеет формат type:path=value
    #          где type - формат содержимого (html, xml, json или text)
    #              path - путь до проверяемого значения (xpath для html и xml, jsonPath для json)
    #                      если type имеет значение text, то path не указывается
    #              value - значение для сравнения
    * Проверить, что в переменной из HTTP-ответа 'restVar' значения совпадают:
      | StatusCode  | 200                            |
      | StatusLine  | HTTP/1.1 200                   |
      | SessionID   | null                           |
      | ContentType | text/html                      |
      | Headers     | Server=ddos-guard              |
      | Headers     | Connection=keep-alive          |
      | Body        | html://title=#{digital-spirit} |
#      | Body        | text:Digital Spirit            |