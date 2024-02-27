# language: ru
Функционал: Пример BDD-теста

  @test @bdd
  Сценарий: BDD Сценарий

    Дано В переменной 'tableName' сохранено значение 'autotests.properties'

    Когда Выполнить SQL запрос в базу 'qa' и сохранить результат в переменной 'sqlVar'
    """
    select *
    from #{tableName}
    where var_name = 'test_parameter'
    """

    Если Проверить, что в переменной из SQL-запроса 'sqlVar' значения совпадают:
      | VAR_NAME  | test_parameter |
      | VAR_VALUE | test_test      |

    Тогда Из переменной 'sqlVar' сохранить значение 'SCOPE' в переменную 'newVar'

    И  Выполнить UPDATE SQL запрос в базу 'qa'
    """
    UPDATE PROPERTIES
    SET VAR_VALUE = 'string to update: #{newVar}'
    WHERE VAR_NAME = 'test_update'
    """
