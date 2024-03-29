# Многопользовательский time-tracker
```
Автор: Рихтер В., Элтекс, Новосибирск
Дата: 08.08.2022
```

## Вступление

Проект реализован через контейнеризацию, поэтому будет работать на любой платформе, где имеется docker-compose.
Тем не менее в документации будет упор на развертывание приложения на платформе Ubuntu 18.04.
Большая часть советов так будет актуальна и для Ubuntu 20.04.
Для того, чтобы развернуть приложение на macOS или Windows, возможно потребуется обращение к сторонним ресурсам.

## Подготовка к запуску приложения

### Скачивание и распаковка архива

Исходный код из репозитория можно получить в виде zip-архива нажав на кнопку `Code`, который можно удобно загрузить на сервер например так:
```
scp time-tracker-master.zip tester@192.168.1.2:/home/tester
``` 

Распаковать архив на сервере
```
unzip time-tracker-master.zip
```

### Установка docker и docker-compose

#### Установка docker

Установите несколько необходимых пакетов, которые позволяют apt использовать пакеты через HTTPS:
```
sudo apt install apt-transport-https ca-certificates curl software-properties-common
```

Добавьте ключ GPG для официального репозитория Docker в вашу систему:
```
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```

Добавьте репозиторий Docker в источники APT:

<span style="color:yellow">**Внимание!** Будьте внимательны при указании дистрибутива. В примере используется для Ubuntu 18.04. Смените при необходимости</span>
```
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable"
```

Обновляем список доступных нам пакетов:
```
sudo apt update
```

Убедитесь, что установка будет выполняться из репозитория Docker, а не из репозитория Ubuntu по умолчанию:

<span style="color:yellow">**Внимание!** Источник должен быть примерно таким: "https://download.docker.com/linux/ubuntu bionic/stable amd64 Packages"</span>
```
apt-cache policy docker-ce
```

Установите Docker:
```
sudo apt install docker-ce
```

Docker должен быть установлен, демон-процесс запущен, а для процесса активирован запуск при загрузке. Проверьте, что он запущен:
```
sudo systemctl status docker
```

<details>
  <summary>Пример вывода команды</summary>

  ```
  ● docker.service - Docker Application Container Engine
   Loaded: loaded (/lib/systemd/system/docker.service; enabled; vendor preset: enabled)
   Active: active (running) since Thu 2022-08-04 05:52:56 UTC; 20s ago
     Docs: https://docs.docker.com
 Main PID: 9674 (dockerd)
    Tasks: 9
   CGroup: /system.slice/docker.service
           └─9674 /usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
  ```
</details>

#### Настройка команды Docker без sudo (необязательно)

Если вы не хотите каждый раз вводить sudo при запуске команды docker, добавьте свое имя пользователя в группу docker:
```
sudo usermod -aG docker ${USER}
```

Чтобы применить добавление нового члена группы, выйдите и войдите на сервер или введите следующее:
```
su - ${USER}
```
Вы должны будете ввести пароль вашего пользователя, чтобы продолжить.

Проверьте, что ваш пользователь добавлен в группу docker, введя следующее:
```
id -nG
```

Если вам нужно добавить пользователя в группу docker, для которой вы не выполнили вход, объявите имя пользователя явно, используя следующую команду:
```
sudo usermod -aG docker username
```

В дальнейшем в статье подразумевается, что вы запускаете команду docker от имени пользователя в группе docker. В обратном случае вам необходимо добавлять к командам префикс sudo.

#### Установка docker-compose

После завершения предыдущих шагов можно сразу установить docker-compose на сервер:
```
sudo apt install docker-compose
```

Если что-то пошло не так во время установки docker или docker-compose, то можно обратиться к первоисточнику:
- https://docs.docker.com/get-docker/
- https://docs.docker.com/compose/install/


### Установка java и maven

Устанавливаем maven:
```
sudo apt install maven
```

Для сборки проекта используется java 11, чтобы посмотреть вашу текущую версию:
```
java -version
```
<span style="color:red">**Внимание!** Вам может выдать правильную версию jdk, но при сборке буду проблемы"</span>
Чтобы такого избежать, надо проверить действительно ли все инструменты java имеются в наличии в системе:

<details>
  <summary>Неправильный вывод</summary>

  ```
tester@rikhter-218802:~$ ll /usr/lib/jvm/java-11-openjdk-amd64/bin
total 188
drwxr-xr-x 2 root root   4096 Aug  7 11:45 ./
drwxr-xr-x 7 root root   4096 Aug  7 11:45 ../
-rwxr-xr-x 1 root root  10304 Jul 22 09:14 java*
-rwxr-xr-x 1 root root  10352 Jul 22 09:14 jjs*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 keytool*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 pack200*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 rmid*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 rmiregistry*
-rwxr-xr-x 1 root root 107408 Jul 22 09:14 unpack200*
  ```
</details>

<details>
  <summary>Правильный вывод</summary>

  ```
tester@rikhter-218802:~$ ll /usr/lib/jvm/java-11-openjdk-amd64/bin
total 500
drwxr-xr-x 2 root root   4096 Aug  7 11:50 ./
drwxr-xr-x 9 root root   4096 Aug  7 11:50 ../
-rwxr-xr-x 1 root root  10376 Jul 22 09:14 jaotc*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jar*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jarsigner*
-rwxr-xr-x 1 root root  10304 Jul 22 09:14 java*
-rwxr-xr-x 1 root root  10352 Jul 22 09:14 javac*
-rwxr-xr-x 1 root root  10352 Jul 22 09:14 javadoc*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 javap*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jcmd*
-rwxr-xr-x 1 root root  10368 Jul 22 09:14 jconsole*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jdb*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jdeprscan*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jdeps*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jfr*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jhsdb*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jimage*
-rwxr-xr-x 1 root root  10352 Jul 22 09:14 jinfo*
-rwxr-xr-x 1 root root  10352 Jul 22 09:14 jjs*
-rwxr-xr-x 1 root root  10352 Jul 22 09:14 jlink*
-rwxr-xr-x 1 root root  10352 Jul 22 09:14 jmap*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jmod*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jps*
-rwxr-xr-x 1 root root  10352 Jul 22 09:14 jrunscript*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jshell*
-rwxr-xr-x 1 root root  10352 Jul 22 09:14 jstack*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jstat*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 jstatd*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 keytool*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 pack200*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 rmic*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 rmid*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 rmiregistry*
-rwxr-xr-x 1 root root  10320 Jul 22 09:14 serialver*
-rwxr-xr-x 1 root root 107408 Jul 22 09:14 unpack200*

  ```
</details>

Правильный вывод можно добиться установкой пакета ниже.

Если у вас другая версия или её нет совсем, то нужно установить java 11:
```
sudo apt install openjdk-11-jdk
```

После этого её активировать:
```
sudo update-alternatives --config java
```

## Сборка проекта

Переходим в директорию `time-tracker-master/docker/java/app`, после чего выполняем следующие команды:
1. mvn compiler:compile - компилирует исходный код приложения в байткод понятный для jvm.
2. mvn compiler:testCompile - компилирует исходный код модульных тестов для приложения в байткод понятный для jvm.

После этого в текущей директории у вас появится новая директория с названием `target/` и структура её будет выглядить примерно так

<details>
  <summary>Вывод структуры target/</summary>

  ```
target/
├── classes
│   └── com
│       └── github
│           └── FurianMan
├── generated-sources
│   └── annotations
├── generated-test-sources
│   └── test-annotations
├── maven-status
│   └── maven-compiler-plugin
│       ├── compile
│       │   └── default-cli
│       └── testCompile
│           └── default-cli
└── test-classes
    └── com
        └── github
            └── FurianMan
  ```
</details>

3. mvn surefire:test - запуск модульных тестов.
4. mvn javadoc:javadoc - генерация документации java.
Чтобы посмотреть javadoc, достаточно открыть любой html файл в браузере. Например `time-tracker-master/docker/java/app/target/site/apidocs/index.html`.
Если вы не можете это сделать, например по причине того, что на хосте нет браузера, то можно скачать файл с документацией с сервера через scp 
```
scp -r tester@100.110.2.52:/home/tester/time-tracker-master/docker/java/app/target/site ./
```
Этой командой вы загрузите с удаленного хоста всю документацию в текущую директорию, откуда выполняете команду. Создастся директория `site` и в ней буду все файлы. 
5. mvn assembly:single - сбор проекта в один jar-архив со всеми зависимостями.

## Настройка приложения перед запуском

Главный конфигурационный файл находится по пути `time-tracker-master/docker/docker-compose.yml`

<details>
  <summary>Содержимое `docker-compose.yml` с пояснениями</summary>

  ```
version: "3"
services:
  database:
    image: mysql:8.0  # Собираем контейнер из образа
    container_name: database
    environment:
      MYSQL_ROOT_PASSWORD: root  # вы можете установить пароль, который устраивает вас
      LANG: C.UTF-8  # Установка кодировки в бд
      TZ: Asia/Novosibirsk  # Установка часового пояса в контейнере
    restart: always  # Включение перезапуска, если контейнер упал
    # ports:  # по-умолчанию выключено проксирование на порт хоста.
    #   - "3308:3306"
    volumes:
      - ./mysql/database.sql:/docker-entrypoint-initdb.d/database.sql  # Передача контейнеру стартового скрипта, который настроит бд
      - ./mysql/data:/var/lib/mysql  # Монтирование данных бд к хосту, чтобы после удаления контейнера не терялись данные из него
      - ./mysql/mycustom.cnf:/etc/mysql/conf.d/custom.cnf  # Дополнительные конфигурационные настройки для бд

  app:
    build: ./java/  # Собираем образ и потом уже контейнер
    image: time-tracker:1.0  
    container_name: time-tracker
    environment:
      APP_PORT: 6969  # Порт внутри контейнера, на котором запустится приложение. Если его меняете, то и не забудь отредактировать `ports:`
      TZ: Asia/Novosibirsk  # Установка часового пояса в контейнере
      CLEAR_DATA_TIME: "2022-08-03"  # Устанавливается дата, начиная с которой будут удаляться данные из mysql, в этом примере удаляются данные начиная с 2022-08-03 включительно.
    ports:
      - "6969:6969"  # Проксирование порта хоста на порт контейнера
    volumes:
      - ./java/app/logger:/logger  # Монтирование логгера для приложения. Нужно для того, чтобы менять настройки логов runtime
    depends_on:
      - database
    links:
      - "database:db"  # Связь между контейнерами
    restart: always

  ```
</details>

Таким образом из важных параметров вам следует настроить следующие:
1. TZ - ваш часовой пояс в двух контейнерах. Это очень важно, т.к. при создании задач время будет зависеть от времени в контейнере.
2. MYSQL_ROOT_PASSWORD - пароль, с которомы вы будете подключаться к mysql
3. CLEAR_DATA_TIME - дата, начиная с которой, информация в бд считает устаревшей и будет удалена. Удаление происходит самим приложением каждый день в 23:50

### Настройка логирования (log4j2)

Для настройки логирования нужно открыть файл по следующему пути `time-tracker-master/docker/java/app/logger/log4j2.xml`

<details>
  <summary>Содержимое `log4j2.xml` с пояснениями </summary>

  ```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" monitorInterval="30">
<Appenders>
    <Console name="LogToConsole" target="SYSTEM_OUT">
        <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%t] %c{1} - %msg%n"/>
    </Console>
    <!-- <File name="LogToFile" fileName="/logger/logs/time_tracker.log">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%t] %c - %msg%n"/>
    </File> -->
    <RollingFile name="LogToRollingFile" fileName="/logger/logs/time_tracker.log"  # Настройка записи логов в файл + ротация этих логов.
                    filePattern="/logger/logs/$${date:yyyy-MM}/time_tracker-%d{MM-dd-yyyy}-%i.log.gz">  # Как будет выполнена ротация
			<PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%t] %c{1} - %msg%n"/>  # Выражение, которое определяет как выглядит информация в логах
			<Policies>  # Политики ротации
				<TimeBasedTriggeringPolicy/>
				<SizeBasedTriggeringPolicy size="10 MB"/>
			</Policies>
		</RollingFile>
</Appenders>
<Loggers>
    <Root level="INFO" additivity="false">  # Уровень логов и запрет на дублирование
    <!--Possible value ALL, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, and OFF -->  # Возможные уровни логов
        <!-- <AppenderRef ref="LogToFile"/> --> 
        <AppenderRef ref="LogToConsole"/>  # Вывод логов в консоль контейнера
        <AppenderRef ref="LogToRollingFile"/>  # Вывод логов в файл
    </Root>
</Loggers>
</Configuration>
  ```
</details>

Из всех этих параметров нужно обратить внимание на:
1. monitorInterval - передается значение в секундах, каждый этот интервал будет перечитываться файл конфигурации сервисом. Если 30 секунд для вас много - измените на меньшее значение
2. Root level="INFO" - этот параметр отвечает за уровень логов, который будет отдавать сервис.
3. SizeBasedTriggeringPolicy size - максимальный размер файла при достижении которого будет выполнена ротация. Так же ротация выполняется по времени каждый день.

Таким образом когда вы настроите и запустите приложение, то логи будут находиться по пути `time-tracker-master/docker/java/app/logger/logs`
Выглядеть с ротацией это будет вот так:

<details>
  <summary>Пример логов с ротаций </summary>

  ```
logs/
├── 2022-07
│   ├── time_tracker-07-28-2022-1.log.gz
│   ├── time_tracker-07-29-2022-1.log.gz
│   ├── time_tracker-07-30-2022-1.log.gz
│   └── time_tracker-07-31-2022-1.log.gz
├── 2022-08
│   ├── time_tracker-08-02-2022-1.log.gz
│   └── time_tracker-08-03-2022-1.log.gz
└── time_tracker.log
  ```
</details>

## Запуск и управление приложением

Перейдем в директорию `time-tracker-master/docker`, именно здесь у нас имеется файл `docker-compose.yml`, запускаем наше приложение следующей командой:
```
docker-compose up --build -d
```

<span style="color:red">**Внимание!** Без этапа `Сборка проекта` при запуске вы получите ошибку</span>

Первый запуск или запуск после удаления настроек бд (об этом коснемся позже) старт приложения может занять некоторое время.
Понять, что приложение готово к работе можно по следующим логам:

<details>
  <summary>Пример успешного старта приложения</summary>

  ```
2022-08-04 16:04:05.444 [INFO ] [main] HttpServerMain - HTTP Server has been started successfully
2022-08-04 16:04:05.445 [INFO ] [main] CheckConnectionToDB - Connecting to database...
2022-08-04 16:04:05.508 [ERROR] [main] CheckConnectionToDB - Connection failed, new try starting...
2022-08-04 16:04:10.510 [ERROR] [main] CheckConnectionToDB - Connection failed, new try starting...
2022-08-04 16:04:15.668 [INFO ] [main] CheckConnectionToDB - Database connection has been installed successfully
  ```
</details>

Т.е. после успешного подключения к бд.

Логи можно посмотреть:
- по пути `time-tracker-master/docker/java/app/logger/logs/time_tracker.log`
- через команду `docker logs time-tracker`

Долгий старт обусловлен настройкой mysql, т.е. работает стартовый скрипт, который готовит базу данных для приложения, а также обсуловлен характеристиками хоста.

После успешного старта приложения можно отправить ему тестовый запрос:
```
curl http://localhost:6969/time-tracker/version
```

Дополнительные команды, которые могут быть полезны:
1. `docker container stop time-tracker` - остановка контейнера
2. `docker rm time-tracker` - удаление контейнера
3. `docker rmi time-tracker:1.0` - удаление образа из которого создается контейнер

При удалении контейнера database вы не удалите информацию, которую успели накопить. 
Вся эта информация хранится по пути `time-tracker-master/docker/mysql/data/`

Выглядит следующим образом:

<details>
  <summary>Директория data/</summary>
  ```
data/
├── auto.cnf
├── ib_buffer_pool
├── ibdata1
├── ibtmp1
├── mysql
├── mysql.ibd
├── mysql.sock -> /var/run/mysqld/mysqld.sock
├── performance_schema
├── private_key.pem
├── public_key.pem
├── server-cert.pem
├── server-key.pem
├── sys
├── time_tracker
...
  ```
</details>

Таким образом, если вы действительно хотите полностью избавиться от всей информации из mysql здесь и сейчас, то:
1. `docker container stop database`
2. `docker rm database`
3. `sudo rm -rf data/` - команда выполняется из директории `mysql/`

## Интеграционное тестирование приложения

<span style="color:red">**Внимание!** Без этапа `Запуск приложения` нет никакого смысла запускать тесты.</span>

### Подготовка к запуску тестов

Т.к. тестирование происходит на Ubuntu 18.04 , то лучшим способом будет запустить нужную версию python через виртуальное окружение.

<span style="color:yellow">**Внимание!** Запуск тестов тестировался на python3.8 и python3.9, в примере будет использоваться python3.9</span>

Посмотреть версию python можно через команду:
```
python3 --version
```

Установка python3.9 через репозиторий:

<span style="color:yellow">**Внимание!** Если по каким-то причинам вам не нравится этот способ установки, то вы можете найти альтернативный</span>
```
sudo add-apt-repository ppa:deadsnakes/ppa 
sudo apt update 
sudo apt install python3.9
```

Так же устанавливаем дополнительные пакеты для корректной работы
```
sudo apt install python3.9-distutils python3-pip python3.9-venv 
``` 
Теперь переходим в директорию с тестами `time-tracker-master/pytests` и создаем виртуальное окружение:
```
python3.9 -m venv .venv
```
Активируем его:
```
source .venv/bin/activate
```
<span style="color:yellow">**Внимание!** Деактивация происходит командой `deactivate`</span>

Далее обновляем pip3 и устанавливаем зависимости:
```
pip3 install --upgrade pip
pip3 install -r requirements.txt
```

### Запуск тестов

Запуск тестов производится из директории `time-tracker-master/pytests` следующей командой:
```
python3 -m pytest -s -v tests/*
```
Ключ -v позволяет включить verbose mode , а ключи -s отображает команду print

Продолжительность тестов около 12 минут, это обусловленно тем, что для тестирования получения статистики - эту статистику сначала надо создать.

## Взаимодействие с API

Описание API построена на спецификации openAPI.

В директории `time-tracker-master/openapiDocs` имеется файл с описанием интерфейса `time_tracker_openapi.yaml`
Его можно посмотреть двумя способами:
1. Поднять docker container и смотреть на хосте
2. Обратиться к стороннему сайту `https://editor.swagger.io/` и открыть файл там.

Подробнее про 1-ый вариант
Запустить UI приложение следующей командой:
```
docker run -d --name swagger -p 80:8080 -e SWAGGER_JSON=/foo/time_tracker_openapi.yaml -v ~/time-tracker-master/openapiDocs/:/foo swaggerapi/swagger-ui
```

После того как контейнер соберется у вас появится доступ на этом хосте к документации по ссылке:
```
http://localhost/
```

Так же доступ должен быть и с других ip вашего хоста.
Вы можете поменять порт 80 на необходимый вам.

Примечание:
- `~/time-tracker-master/openapiDocs/:/foo` здесь происходит монтирование файла на хосте к директории в контейнере, измените путь, если он у вас отличается
- после просмотра документации вы можете остановить контейнер, для самого приложения в нем нет необходимости.
- запросы исполняемые из документации никак не будут влиять на само приложение.

## Для самых любознательных

В этом разделе будут отмечены особенности работы приложения.

### Mysql

Разберем что из себя представляет база данных, для этого обратимся к файлу, который в этом проекте её создает `time-tracker-master/docker/mysql/database.sql`:

<details>
  <summary>Файл database.sql</summary>
  ```
CREATE DATABASE IF NOT EXISTS time_tracker;
CREATE TABLE IF NOT EXISTS time_tracker.users(
    user_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    surname VARCHAR(30) NOT NULL,
    patronymic VARCHAR(30),
    position VARCHAR(30) NOT NULL,
    birthday DATE NOT NULL,
    date_creating DATETIME,
    UNIQUE (name,surname,position,birthday)
);
CREATE TABLE IF NOT EXISTS time_tracker.tasks(
	task_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	task_num INT NOT NULL,
	start_time DATETIME,
	end_time DATETIME,
	user_id INT NOT NULL,
    UNIQUE (task_num,start_time,user_id),
	FOREIGN KEY (user_id) REFERENCES time_tracker.users (user_id) ON DELETE CASCADE
);
USE time_tracker;
DELIMITER //
CREATE TRIGGER insert_date_into_user
BEFORE INSERT
ON users
FOR EACH ROW
SET NEW.date_creating=NOW();//
DELIMITER ;
CREATE USER 'javauser'@'%' IDENTIFIED BY 'javapassword';
GRANT ALL PRIVILEGES ON time_tracker.* TO 'javauser'@'%';
...
  ```
</details>

Обсудим создание таблицы `users`:
1. единственное поле, которое может быть NULL - `patronymic`, т.е. отчество
2. уникальность записи определяется по полям `name`,`surname`,`position`,`birthday`. Отчество было убрано из учета, т.к. его возможное отсутствие создавало много проблем в архитектуре приложения. Эти проблемы можно было решить, но из-за временного ограничения было не целесообразно делать это прям сейчас.
Можно сделать вывод, что один и тот же человек не может занимать две одинаковые должности.

Перейдем к таблице `tasks`:
1. start_time - отмечено, что поле может быть пустым, но на деле время генерируется в приложении и вносится в таблицу, поэтому оно никогда не будет пустым.
2. уникальность записи состоит из полей `task_num`,`start_time`,`user_id`. Таким образом один пользователь не может создать две задачи в одно и тоже время, но на самом деле ни в одном запросе пользователь не может внести дату в базу данных, поэтому подобное поведение может встретиться лишь при одновременном запросе и будет пресечено. Забегая вперед скажу, что две открытые задачи по одному заданию у одного пользователя так же не могут быть открыты, придется закрыть предыдущую.
3. `FOREIGN KEY (user_id) REFERENCES time_tracker.users (user_id) ON DELETE CASCADE` здесь мы связали две таблицы по user_id и если к примеру пользователь будет удален из таблицы `users`, то и все связанные с ним записи пропадут из таблицы `tasks`

Из скрипта видно, что присутствуют не только таблицы, но и триггер.
Триггер активируется при внесении новой записи в таблицу `users`, т.е. при `INSERT`.
Задача этого триггера перед внесением этого запроса добавить текущую дату в поле `date_creating`.
Это единственное место, где именно база данных, а не приложение, генерирует время.

Важно отметить, что устаревшая информация как раз считается по времени создания пользователя. Почему же так?
Из-за специфики архитектуры пользователь не вносит ни одной даты, везде её генерирует приложение или база данных и самая первая дата - это дата создания пользователя. Без пользователя невозможно создать задание и получить статистику. Таким образом когда подходит время, мы удаляем только пользователя.

Если пользователь не закрыл трекинг своей задачи в течение дня, то в 23:59 приложение пройдется по всем заданиям, найдет те, у которых `end_time` IS NULL и проставит им текущую дату и время `23:59:59`. Т.к. задача активируется каждый день в 23:59, то это возможное уязвимое место, ведь если если сервер к примеру будет выключен и это время пройдет, то выполнение будет в день включения сервера в 23:59. Пока что это фича.

Еще одна задача по расписанию работает каждый день в 23:50 и проверяет наличие "устаревшую" информации. Здесь отключение сервера на некоторое время не создает проблем в эксплуатации. 

### API и его особенности 

Теперь более подробно остановимся на самом приложении.

Далеко не все полученные ошибки по API будут сразу понятны, например:

http-code: 500, message: "Cannot execute query `insertUser` in database"
Эта ошибка происходит при попытке создать пользователя. Она может означать, что пользователь уже существует, так же любое исключение произошедшее во время исполнения sql команды. Такие ошибки касаются любых методов, которые сопряжены с работой с бд. 


В таким случаях лучше всего обращаться к логам и там будет подробная информация какая именно ошибка произошла с бд.
Но в этом случае перед этим все же попробуйте запросить этого пользователя через GET запрос, возможно он просто был уже создан.

Любопытный факт:
Для получения статистики есть три мода: `oneline`, `sum`, `period`.
Если для пользователя нет статистики, то `sum` и `period` - вернут 404 ошибку, а `oneline` вернет время со значением 00:00.

В приложении используются регулярные выражения для валидации полей пользователя и дат, вот эти самые регулярные выражения и их назначение:
1. `(^[а-яА-ЯёЁ]*$)|(^[A-Za-z]*$)` русский алфавит или английский, но не вместе. Используется для полей `name`, `surname`, `position`, `patronymic`, `newName`, `newSurname`, `newPatronymic`, `newPosition` в запросах на создание пользователя и изменение данных.
2. `^(((2\d\d\d|19\d\d)|(1\d|2[0123]))-((0[0-9])|(1[012]))-((0[1-9])|([12][0-9])|(3[01])))$` дата формата 2023-12-31, другие разделители запрещены, не допустит 13-ый месяц или 32 число. Используется для полей `birthday`, `newBirthday`, т.е. при создании пользователей и изменении.
3. `^(((2\\d\\d\\d|1\\d\\d\\d)|(1\\d|2[0123]))-((0[0-9])|(1[012]))-((0[1-9])|([12][0-9])|(3[01]))) ([0-1]\\d|2[0-3])(:[0-5]\\d){2}$` 
в этом случае мы ждем дату и время формата `2022-08-02 23:59:59`, т.е. не пропустит 25-ый час или 60-ую минуту и тд. Даты проверяются как и во 2-ом пункте.
Используется для полей `start_time` и `end_time` в запросах на получение статистики. 

Из этого можно сделать вывод, что имя можно написать на русском, а фамилию на английском и это пройдет, команда по разработке из одного человека единогласно признало это фичей. 

Некоторые запросы не выдают сообщений в теле, лишь http-code `200 OK`, например:
1. Запросы на изменение
2. Запросы на удаление

В запросе на изменение данных пользователя вы можете менять несколько, все или одно поле. А так же можете менять ничего и запрос будет выполнен, уже не похоже на фичу.