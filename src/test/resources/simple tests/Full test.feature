# language: ru
Функционал: Framework testing

  @test @full @tmslink=BBAT-T1 @trivial
  Сценарий: Полная проверка всех шагов
    # Сохранение и вывод переменных
    Дано В переменной 'digital-spirit' сохранено значение 'Digital Spirit - cooperation based on trust'
    * Вывести на экран значение переменной 'digital-spirit'

    # Работа с содрежимым файлов
    * Сохранить в переменной 'fileVar' содержимое файла 'src/test/resources/simple tests/Full test.feature'
    * Вывести на экран значение переменной 'fileVar'
    * Проверить, что переменная 'fileVar' содержит значения:
      | Функционал |
      | Сценарий   |
      | Дано       |

    # Ожидалки
    * Ожидать '1' сек.

    # Работа с HTTTP-запросами
    * Выполнить HTTP-запрос с параметрами, результат сохранить в переменной 'restVar':
      | type | GET                        |
      | uri  | https://digital-spirit.ru/ |
    * Вывести на экран значение переменной 'restVar'

    * Проверить, что в переменной из HTTP-ответа 'restVar' значения совпадают:
      | StatusCode  | 200                            |
      | StatusLine  | HTTP/1.1 200                   |
      | SessionID   | null                           |
      | ContentType | text/html                      |
      | Headers     | Server=ddos-guard              |
      | Headers     | Connection=keep-alive          |
      | Body        | html://title=#{digital-spirit} |

    # Работа с SQL-запросами
    * Выполнить SQL запрос в базу 'qa' и сохранить результат в переменной 'sqlVar'
    """
    select *
    from properties
    """
    * Вывести на экран значение переменной 'sqlVar'

    * Выполнить SQL запрос в базу 'qa' и сохранить результат в переменной 'sqlVar'
    """
    select *
    from properties
    where var_name = 'test_parameter'
    """
    * Вывести на экран значение переменной 'sqlVar'

    # Работа с сохраненными объектами
    * Проверить, что в переменной из SQL-запроса 'sqlVar' значения совпадают:
      | VAR_NAME  | test_parameter |
      | VAR_VALUE | test_test      |

    * Из переменной 'sqlVar' сохранить значение 'SCOPE' в переменную 'newVar'
    * Вывести на экран значение переменной 'newVar'

    # Работа с JSON
    * Выполнить SQL запрос в базу 'qa' и сохранить результат в переменной 'sqlJson'
    """
    select *
    from properties
    where var_name = 'test_json'
    """
    * Из переменной 'sqlJson' получить документ и сохранить в переменной 'json'
      | type  | json      |
      | field | VAR_VALUE |
    * Вывести на экран значение переменной 'json'
    * Проверить, что в переменной из JSON-объекта 'json' значения совпадают:
      | tin   | 12345678 |
      | pinfl | 100500   |

    * В переменной 'sql-update-var' сохранено значение 'update from qa_automation_framework'

    * Из переменной 'json' сохранить значение 'cadNum' в переменную 'cadNumVar'
    * Вывести на экран значение переменной 'cadNumVar'

    * Выполнить UPDATE SQL запрос в базу 'qa'
    """
    UPDATE PROPERTIES
    SET VAR_VALUE = 'string to update: #{sql-update-var}'
    WHERE VAR_NAME = 'test_update'
    """
    * Выполнить SQL запрос в базу 'qa' и сохранить результат в переменной 'sqlUpdate'
    """
    select var_value
    from properties
    where var_name = 'test_update'
    """
    * Вывести на экран значение переменной 'sqlUpdate'

    # Работа с XML
    * Выполнить SQL запрос в базу 'qa' и сохранить результат в переменной 'responseVar'
    """
    select *
    from properties
    where var_name = 'test_xml'
    """

    * Из переменной 'responseVar' получить документ и сохранить в переменной 'contentXML'
      | type  | xml       |
      | field | VAR_VALUE |

    * Вывести на экран значение переменной 'contentXML'

    * Проверить, что в переменной из XML-объекта 'contentXML' значения совпадают:
      | routing.routerInput.id    | 2313         |
      | routing.routerInput.pinfl | 124325235623 |

    * Проверить, что в переменной из XML-объекта 'contentXML' значения совпадают:
      | /routing/routerInput/id    | 2313         |
      | /routing/routerInput/pinfl | 124325235623 |

    # Сравнение данных в полях документов JSON и XML
    * Из переменной 'contentXML' сохранить значение 'routing.routerInput.pinfl' в переменную 'pinflVar'
    * Вывести на экран значение переменной 'pinflVar'

    * Выполнить SQL запрос в базу 'qa' и сохранить результат в переменной 'responseVar'
    """
    select *
    from properties
    where var_name = 'test_json_xml'
    """

    * Из переменной 'responseVar' получить документ и сохранить в переменной 'contentJSON'
      | type  | json      |
      | field | VAR_VALUE |

    * Вывести на экран значение переменной 'contentJSON'

    * Сформировать коллекцию полей для сравнения и сохранить в переменной 'fieldsMap'
      | filter.id     | routing.routerInput.id    |
      | filter.pinfl1 | routing.routerInput.pinfl |

    * Сравнить значения полей в переменных 'contentJSON' и 'contentXML' по таблице 'fieldsMap'

    * Убедиться, что в переменной 'contentJSON' содержатся все поля из таблицы 'fieldsMap' стоблец 1
    * Убедиться, что в переменной 'contentXML' содержатся все поля из таблицы 'fieldsMap' стоблец 2