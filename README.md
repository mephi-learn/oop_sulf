**Приложение по управлению личными финансами.**
# Возможности приложения:
1. Во время работы все данные хранятся в памяти приложения.
1. Реализована функциональность для авторизации пользователей по логину и паролю. Приложение поддерживает несколько пользователей.
1. Разработана логика для добавления доходов и расходов. Пользователь имеет возможность создавать категории для планирования бюджета. Предусмотрена функциональность для установления бюджета на каждую категорию расходов.
1. Реализована возможность привязки кошелька к авторизованному пользователю. Кошелёк хранит информацию о текущих финансах и всех операциях (доходах и расходах). Также реализовано сохранение установленных бюджет по категориям.
1. Реализована возможность отображения общей суммы доходов и расходов по пользователю, по кошельку, а также данных по каждой категории. Выводится информацию о текущем состоянии бюджета для каждой категории, а также оставшийся лимит.
1. Разработаны методы, подсчитывающие общие расходы и доходы, по пользователю, по кошельку, а также по категориям.
1. Валидация пользовательского ввода и уведомление о некорректных данных.
1. Производится оповещение пользователя, если превышен лимит бюджета по категории или расходы превысили доходы.
1. При выходе из приложения данные кошелька пользователей сохраняются в файл, при запуске данные загружаются из файла.
1. Реализован цикл для постоянного чтения команд пользователя. Поддержка возможности выхода из приложения.
1. Реализована возможность переводов между кошельками пользователей. При переводе фиксируется расход у отправителя и доход у получателя, который идентифицируется по логину.
# Структура меню:
1. Управление пользователями
    1. Аутентификация по логину/паролю
    1. Список пользователей
    1. Регистрация
1. Управление кошельками
    1. Выбор кошелька
    1. Список кошельков
    1. Создание кошелька
    1. Редактирование кошелька
    1. Удаление кошелька
1. Управление категориями
    1. Создание категории
    1. Список категорий
    1. Редактирование категории
    1. Удаление категории
1. Управление платежами
    1. Создание платежа
    1. Список платежей
    1. Редактирование платежа
1. Перевод средств пользователям
    1. Создание перевода
1. Статистика
    1. Статистика по пользователю
    1. Статистика по кошельку

Основная работа ведётся в рамках выбранного кошелька.


## Описание пунктов меню.

**Управление пользователями.**

Данный пункт позволяет аутентифицироваться с помощью логина и пароля, а также завести пользователя. При заведении пользователя автоматически создаётся кошелёк default, в котором создаётся категория default. Данная категория необходима для перевода средств от других пользователей и не может быть изменена или удалена. Данный кошелёк автоматически становится кошельком по-умолчанию, а новый пользователь также автоматически проходит аутентификацию.

**Управление кошельками.**

Данный пункт меню позволяет создавать новые кошельки, выбирать какой-либо из созданных кошельков в качестве основного, редактировать или удалять их. Новосозданный кошелёк автоматически становится выбранным. Выбранный кошелёк автоматически выбирается при аутентификации пользователя.

При создании или редактировании кошелька необходимо указать его название и валюту. По-умолчанию выставлена валюта RUB, но допускается ввести любую строку, хоть клингонский тугрик. Кошелёк default позволяет изменить валюту, но не название. Не допускается создание кошельков с одним именем.

**Управление категориями.**

Пользователь может, в рамках кошелька, создавать категории, для каждой из который можно выставить бюджет. При превышении бюджета будет выводиться предупреждение. В одном кошельке создавать несколько категорий с одним именем запрещено. Допускается редактирование и удаление категории. При удалении будет выведено предупреждение, и, если в категории присутствуют платежи, будет выведено повторное предупреждение. При редактировании допускается изменение бюджета. Все текущие значения при редактировании становятся значениями по-умолчанию.

**Управление платежами.**

Пользователь может создавать платежи, при создании будет запрошена категория, в которую будет зачислен платёж. Также необходимо будет выбрать тип платежа, расход или доход. Обратите внимание, что именно это влияет на знак платежа, а не знак введённой суммы (он игнорируется). Также будет предложено ввести дату платежа, но если нажать ENTER, то будет автоматически использована текущая дата. Если категория, в которую вносится платёж, имеет бюджет, и при внесении данного платежа бюджет будет превышен, то будет выведено предупреждение. Также будет выведено предупреждение, если сумма расходов по данному кошельку превысит сумму доходов.

При редактировании допускается изменять все параметры платежа, исходные значения становятся значениями по-умолчанию.

**Перевод средств пользователям.**

Допускается переводить средства пользователям. Предлагается выбрать пользователя из списка и ввести сумму платежа. При переводе будет внесены две записи – расходная у пользователя, который отправил платёж и приходная – у пользователя, который этот платёж принял. При отправлении допускается указание категории, с которой будет списан платёж, приход платежа всегда приходится в каталог default кошелька default. Связано это с тем, что отправителю не нужно знать ни названия кошельков, ни названия категорий у принимающего пользователя, ибо это может его скомпроментировать.

**Статистика.**

При выводе статистики по кошелькам, она выводится следующим образом:


![](images/Aspose.Words.7a80e49e-ce5e-431e-80e4-51eb81019317.001.png)

<a name="_mon_1798231192"></a>Следует обратить внимание, что если у данной категории есть доход и расход, то он будет посчитан раздельно, в указанном примере категория «Зарплата» имеет два прихода, 20000 и 40000, а также один расход в 60000. Обе цифры видны в соответствующих расходах и доходах по категориям. Также выводятся категорий, чей баланс отличается от 0. Общий баланс также подсчитывается и выводится в заголовке соответствующего раздела, а также сразу под названием кошелька.

При выборе статистики по пользователю сначала будет выведена суммарная статистика по всем кошелькам пользователя:

![](images/Aspose.Words.7a80e49e-ce5e-431e-80e4-51eb81019317.002.png)

<a name="_mon_1798232296"></a>Следом будет выведена информация по каждому кошельку в отдельности в формате статистики кошелька, указанного выше.