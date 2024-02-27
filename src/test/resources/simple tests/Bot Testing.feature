# language: ru
Функционал: Telegram Bot Testing

  @link=https://wiki.digital-spirit.ru/pages/viewpage.action?pageId=183503292

  @tg_bot @test
  Сценарий: Тестирование телеграм-бота

    # Шаг, активирующий телеграм-бота
    * Активировать телеграм-бота

    * В переменной 'tg-bot-chat' сохранено значение '-4168934782'
    * В переменной 'tg-bot-text' сохранено значение 'demo from framework'

    # Для отправки сообщения через ТГ-бота, нужно передать ChatID группы или пользователя
    * Выполнить HTTP-запрос с параметрами, результат сохранить в переменной 'tg-bot-response':
      | type    | POST                                                                               |
      | uri     | https://api.telegram.org/bot#{tg-bot-token}/sendMessage                            |
      | headers | Content-Type=application/json                                                      |
      | body    | {"chat_id":#{tg-bot-chat}, "text": "#{tg-bot-text}", "disable_notification": true} |