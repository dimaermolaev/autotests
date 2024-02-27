# language: ru
Функционал: Framework testing

  @link=https://wiki.digital-spirit.ru/display/DSTST/XML

  @test @xml
  Сценарий: Проверка функционала по работе с XML
    # Работа с XML

    # Запрос для использования метода 'Получить XML из переменной 'responseVar' и сохранить в переменной 'xmlVar''
    * Выполнить SQL запрос в базу 'qa' и сохранить результат в переменной 'responseVar'
    """
     select var_value as xml
     from properties
     where var_name = 'test_xml'
    """


    # Шаг для преобразования строки содержащей XML в объект org.w3c.dom.Document
    * Получить XML из переменной 'responseVar' и сохранить в переменной 'xmlVar'
    * Вывести на экран значение переменной 'xmlVar'



    * Выполнить SQL запрос в базу 'qa' и сохранить результат в переменной 'responseVar'
    """
    select *
    from properties
    where var_name = 'test_xml'
    """

    # Шаг для получения объекта XML org.w3c.dom.Document из SQL-ответа
    * Из переменной 'responseVar' получить XML и сохранить в переменной 'contentVar'
      | type  | xml       |
      | field | VAR_VALUE |

    * Вывести на экран значение переменной 'contentVar'

    # Шаг с проверкой значения в теге по XPath в документе org.w3c.dom.Document
    # Может принимать XPath к в формате с разделителем точкой '.', так и слеш '/'
    # Разделитель точка:
    * Проверить, что в переменной из XML-объекта 'contentVar' значения совпадают:
      | routing.routerInput.id    | 2313         |
      | routing.routerInput.pinfl | 124325235623 |

    # Разделитель слеш:
    * Проверить, что в переменной из XML-объекта 'contentVar' значения совпадают:
      | /routing/routerInput/id    | 2313         |
      | /routing/routerInput/pinfl | 124325235623 |

    # Шаг позволяет сохранить в памяти текстовой значение тега из объекта org.w3c.dom.Document по XPath
    * Из переменной 'contentVar' сохранить значение 'routing.routerInput.pinfl' в переменную 'pinflVar'
    * Вывести на экран значение переменной 'pinflVar'

    # Пример работы с XML
    * В переменной 'xmlVar' сохранено значение:
    """
    <routing>
      <routerInput>
        <id>2313</id>
        <pinfl>124325235623</pinfl>
      </routerInput>
    </routing>
    """
    * Из переменной 'xmlVar' получить документ и сохранить в переменной 'xmlVarDoc'
      | type | xml |

    * Проверить, что в переменной из XML-объекта 'xmlVarDoc' значения совпадают:
      | /routing/routerInput/id | 2313 |

    * Вывести на экран значение переменной 'xmlVarDoc'