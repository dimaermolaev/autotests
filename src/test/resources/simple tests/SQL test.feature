# language: ru
Функционал: Framework testing

  @link=https://wiki.digital-spirit.ru/display/DSTST/SQL

  @test @sql
  Сценарий: Проверка функционала по работе с SQL-запросами

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

    # Шаг для проверки значений в ответе SELECT-запроса

    # Для работы необходимо убедиться, что ответ возвращает только одну строку.
    # Если возвращено более одной строки, будет проверена только первая строка из ответа!
    * Проверить, что в переменной из SQL-запроса 'sqlVar' значения совпадают:
      | VAR_NAME  | test_parameter |
      | VAR_VALUE | test_test      |

    # Шаг для сохранения значения из поля ответа (в ответе должна быть одна строка)
    * Из переменной 'sqlVar' сохранить значение 'SCOPE' в переменную 'newVar'
    * Вывести на экран значение переменной 'newVar'

    # Пример работы метода, вносящего изменения в таблицу БД
    # Выведет в консоль кол-во измененных строк
    * В переменной 'sql-update-var' сохранено значение 'update from qa_automation_framework'

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
