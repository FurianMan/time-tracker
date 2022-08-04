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

Если у вас другая версия, то нужно установить java 11:
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
4. mvn assembly:single - сбор проекта в один jar-архив со всеми зависимостями.

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
Выглядеть с ротацией это будет вот так 

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

## Интеграционное тестирование приложения

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

Запуск тестов происзводится из директории `time-tracker-master/pytests` следующей командой:
```
python3 -m pytest -s -v tests/*
```
Ключ -v позволяет включить verbose mode , а ключи -s отображает команду print

Продолжительность тестов около 12 минут, это обусловленно тем, что для тестирования получения статистики - эту статистику сначала надо создать.