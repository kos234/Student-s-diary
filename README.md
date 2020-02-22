Student's diary 
=====================

#### A program for students from a student. This app can replace the student's diary function. 

#### Functions: Display notifications before the end or beginning of lessons and break time, record schedules, ratings, homework, annual assessments, teachers and the ability to create your own color themes of the application

#### Russian documentation is available

#### On development stage: release in playmarket, adding the ability to select the number of quarters

#### The current version can be found at this link: https://drive.google.com/file/d/1NKeulH7HjqqlSzkT4temiUFFXW0Vx0tX/view?usp=sharing


Дневник
=====================

#### Программа для школьников от школьника. Это приложение способно заменить полноценный дневник.

#### Функции: Показ уведомлений до конца или начала уроков и перемены, запись расписания, оценок, домашнего задания, годовых оценок, учителей и возможность создавать свои цветовые темы приложения.

#### В разработке: Релиз в PlayMarket, добавить возможность указать количество четвертей

#### Приложение можно скачать по этой ссылке: https://drive.google.com/file/d/1NKeulH7HjqqlSzkT4temiUFFXW0Vx0tX/view?usp=sharing

Документация
=====================

Расписание
-----------------------------------

#### Самый главный блок, по его данным работает функция отправки уведомлений и блоки “Дневник”, “Оценки”.
#### В макете лежит `ViewPager`(Слайдер) с помощь которого происходит переключение между фрагментами дней, которые были сгенерированы в коде. По умолчанию включена суббота, которую можно выключить в настройках.

### **Функционал родительского экрана:** 
#### 1) Кнопка открытия меню блоков
#### 2) Кнопка включение, выключение уведомлений в текущем дне
#### 3) Список дней, белая полоса указывает на текущий день, нажатие на определенный день откроет соответствующий день, так-же переключение работает путём слайда
#### 4) Блоки с информацией о уроке, содержит: время начала и конца, само название предмета и номер кабинета, аудитории; короткое нажатие откроет меню редактирование, долгое нажатие выведет окно удаление. Если нет записей, то на заднем фоне будет отображаться подсказка
#### 5) Кнопка добавление новой записи, короткое нажатие открывает меню добавление(оно же меню редактирования), долгое нажатие выведет окно о очистке, где будет выбор: либо удалить все записи, либо только в текущем дне
![screenshot of timetables](https://psv4.userapi.com/c856528/u388061716/docs/d17/c7d5b73dcd03/2020-02-17_18-22-02.png?extra=iTxyLTUnARj1hzFFj7wNr7v7XG-QO-qrxROOZNeq1JqjE57p3HFyVhyo5MrDNC3uhmxFV4oeW7-wLLr___cHQ0QYKW7Ja-Wh9m_11LILBlmD6iCUDV-lnDyfwksDhxb09bNlWRSa8XoCExeUn0oUdb2R)

### **Функционал экрана добавление, он же экран редактирования:**
#### 1) Поле начала урока, принимает значение вида “HH:MM”
#### 2) Блок выбора(Spinner) полудня, если у вас стоит 24 часовой формат этот блок будет скрыт и не будет отображаться в блоке с информацией
#### 3) Поле конца урока, принимает значение вида “HH:MM”
#### 4) Выбор место проведения, для школьников – кабинет, для студентов – аудитория
#### 5) Номер кабинета, аудитории
#### Если все поля были пусты (то есть вы ничего не вводили) запишутся данные по умолчанию, которые вы могли видеть в скрытом режиме
![screenshot of timetables_add](https://psv4.userapi.com/c856236/u388061716/docs/d4/08e0d939f228/2020-02-17_18-32-33.png?extra=0MrtH9C8ptKvnEONP-L1b4peerCJlC2hGlUFlbK9uneyruO4Yk4TbcryWhXACleKAEJ6i99BOVfcOXobkVIVUvY3MiHtnsmEXFSbY7_j8ftGvuRM1qyLUIv7mgp71W-ur7qEZw8pMIsx6CH7SJbxOj56)

### **Функционал с программной точки зрения:**
#### При добавлении новых записей записывает в файлах `Monday.txt`, `Tuesday.txt` и так далее, информацию по шаблону: `ЧАС:МИНУТА:AM-ЧАС:МИНУТА:AM=НАЗВАНИЕ ПРЕДМЕТА, КАБИНЕТ/АУДИТОРИЯ НОМЕР /n`, если используется 24 часовой формат: `ЧАС:МИНУТА-ЧАС:МИНУТА=НАЗВАНИЕ ПРЕДМЕТА, КАБИНЕТ/АУДИТОРИЯ НОМЕР /n`. Сами файлы находятся в корневом каталоге приложения

Дневник
-----------------------------------

#### Блок заменяющий основной функционал дневника
#### В макете также лежит `ViewPager`, с помощью которого происходит перемещение между днями недели, которые были тоже сгенерированы в коде

### **Функционал родительского экрана:**
#### 1) Кнопка открытия меню блоков
#### 2) Текстовое поле выводящие дату начала и конец недели(включая воскресение). Нажатие по нему откроет окно удаление, можно либо удалить записи домашнего задания, либо оценки, либо полностью очистить текущую неделю(расписание уроков будет сгенерировано заново)
#### 3) Кнопка открывающая прошлую неделю, если она ещё не была создана, то она сгенерируется
#### 4) Кнопка открывающая следующую неделю, если она ещё не была создана, то она сгенерируется
#### 5) Таблица дней недели, клик по строке с нужным вам уроком откроет меню редактирования(Изменение названия предмета невозможно! Это делается через блок “Расписание”)
![screenshot of timetables_add](https://psv4.userapi.com/c856320/u388061716/docs/d12/be639c8f934f/2020-02-17_18-51-58.png?extra=-aU1OxdcKJR_yAqLP1dFI9H_vL0UOhyTDJzyu0sPyd1qG6K0NsZrgucvspdlmsMkMzbdEUtdn9EMza8KoqTZpfMgSYFmCWtDZbFQjoDzKGJngLuwC6QQzyhYlepdwaqtZrHqGCM8tlFAFPCL_4YyHvcf)

### **Функционал экрана редактирования:**
#### 1) Название предмета, Не редактируется!
#### 2) Добавление домашнего задания
#### 3) Оценка за урок(если получили конечно)
![screenshot of timetables_add](https://psv4.userapi.com/c856532/u388061716/docs/d12/0f6238f111db/2020-02-17_19-02-11.png?extra=yG0AlQ9rI0OJSi9F1CNqxAHlesgkuOQuMNMxvQeLSyCsOykexQJjU2MksYR1yyjtflp8Q5cVJrKVbCg89DEDzVtbVaRqrRKQbAeyvA7add_fACfQMg3UOQU9BzUYWKJEMS99BK2yWYDV_Sn1-NlMLYuG)

### **Функционал с программной точки зрения:**
#### При старте генерируется неделя и записывается в 6 файлов с названием типа: `ДЕНЬ.МЕСЯЦ.СКОЛЬКО ЛЕТ ПРОШЛО ПОСЛЕ 1900`(17.2.120), эти файлы находятся в папке `Dnewnik`, она в свою очередь в корневом каталоге приложения. При генерации читаются файлы `Monday.txt`, `Tuesday.txt` и так далее, из них берется информация после знака равно, то есть: `НАЗВАНИЕ ПРЕДМЕТА, КАБИНЕТ/АУДИТОРИЯ НОМЕР`. Если файлы были не найдены или пусты неделя не генерируется, но в блоки дней выводится подсказка о добавлении расписания через блок “Расписание”. Неделя записывается по шаблону: `НАЗВАНИЕ ПРЕДМЕТА, КАБИНЕТ/АУДИТОРИЯ НОМЕР=ДОМАШНЕЕ ЗАДАНИЕ\`=ОЦЕНКА /n`.Символ “ ` ” необходим для записи домашнего задания в несколько строк, каждый такой символ указывает на один перенос строки.
При нажатии на кнопки 3, 4 принцип тот-же, берется информация о том какая неделя сейчас отображается и просто добавляется или убавляется 7 дней
