# language: ru
Функционал: Пример BDD-теста

  @test @bdd @outline @blocker
  Структура сценария: BDD Структура сценария

    Дано В переменной 'tableName' сохранено значение 'autotests.properties'

    Когда Выполнить SQL запрос в базу 'qa' и сохранить результат в переменной 'sqlVar'
    """
    select *
    from #{tableName}
    where var_name = '<field>'
    """

    Если Проверить, что в переменной из SQL-запроса 'sqlVar' значения совпадают:
      | VAR_VALUE | <value> |

    Примеры:
      | field          | value                                                 |
      | test_parameter | test_test                                             |
      | test_cyrillic  | тест-кириллица                                        |
      | test_update    | string to update: update from qa_automation_framework |
