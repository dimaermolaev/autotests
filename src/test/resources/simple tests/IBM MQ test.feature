# language: ru
Функционал: Framework testing

  @link=https://wiki.digital-spirit.ru/display/DSTST/MQ

  @test @ibmmq
  Сценарий: Проверка функционала по работе с IBM MQ-очередями
    # Работа с системами очередей


    * В переменной 'messageID' сохранено значение:
    """
    414d512044514d2020202020202020203695b76501110010
    """

    * В переменной 'mqXML' сохранено значение:
    """
    <s:ScoringResponseSuccessType xmlns:s="http://iABS.ru" xmlns:abs="http://iABS.ru" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="s:ScoringResponseSuccessType">
      <abs:code>0</abs:code>
      <abs:message>Failed</abs:message>
      <abs:object>
        <abs:application_no>4010#14</abs:application_no>
        <abs:passed>true</abs:passed>
        <abs:claim_id>NBU_SL4010#1</abs:claim_id>
        <abs:error>Кредиты с максимальным количеством дней непрерывных отсроченных платежей по текущим и истекшим договорам за последние 12 месяцев, превышают 30 календарных дней на дату заявки</abs:error>
      </abs:object>
    </s:ScoringResponseSuccessType>
    """

    # Шаг, который отправляет в очередь текстовое сообщение и сохраняет в переменной messageID
    * Отправить сообщение в очередь 'ibmmq' и сохранить messageID в переменной 'mqMessageID':
    """
    #{mqXML}
    """
    * Вывести на экран все сообщения из очереди 'ibmmq'

    # Швг, который находит сообщение в очереди по параметрам:
    #       messageID - по messageID
    #       xml - по значению в теге (xpath=value)
    #       json - по значению в поле (jsonPath=value)
    #       text - по совпадению в тексте сообщения
    * Сохранить сообщение из очереди 'ibmmq' в переменной 'mqvar':
      | xml       | ScoringResponseSuccessType.object.application_no=4010#12 |
#      | messageID | #{messageID}                                             |
#      | json      | json.path=fieldValue                                     |
#      | text      | #{textContent}                                           |


    * Сохранить сообщение из очереди 'ibmmq' в переменной 'mqvar1':
      | messageID | #{messageID} |

    * Вывести на экран значение переменной 'mqvar'
    * Вывести на экран значение переменной 'mqvar1'

    * Из переменной 'mqvar' получить документ и сохранить в переменной 'xml'
      | type | xml |

    * Проверить, что в переменной из XML-объекта 'xml' значения совпадают:
      | ScoringResponseSuccessType.object.application_no | 4010#12 |

    * Из переменной 'xml' сохранить значение 'ScoringResponseSuccessType.object.application_no' в переменную 'xpathContent'
    * Вывести на экран значение переменной 'xpathContent'

    # Пример поиска сообщения в очереди содержащего JSON
    * В переменной 'mqJSON' сохранено значение:
    """
    {
      "filter": {
        "system": "CRM"
      },
      "headers": {
        "authorization": "Basic SUJNX0lCX1VTRVI6QVNAS0FfMjAyMmQ="
      },
      "data": {
        "sourceSystemId": "Wings",
        "requestDateTime": "12-12-2016",
        "messageGuid": "1234-4567-6789-1123"
      },
      "method": "GET",
      "resourceUrl": "/api/sr-dictionary-v1/dictionary/category/"
    }
    """

    * Отправить сообщение в очередь 'ibmmq' и сохранить messageID в переменной 'mqJSONMessageID':
    """
    #{mqJSON}
    """

    * Вывести на экран все сообщения из очереди 'ibmmq'


    # Находим в очереди сообщение, которое имеет нужное значение по jsonPath
    * Сохранить сообщение из очереди 'ibmmq' в переменной 'mqJSONvar':
      | json | data.sourceSystemId=Wings |

    * Из переменной 'mqJSONvar' получить документ и сохранить в переменной 'json'
      | type | json |

    * Проверить, что в переменной из JSON-объекта 'json' значения совпадают:
      | filter.system | CRM |

    * Из переменной 'json' сохранить значение 'headers.authorization' в переменную 'jsonContent'

    * Вывести на экран значение переменной 'jsonContent'
