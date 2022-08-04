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

span style="color:yellow">**Внимание!** Будьте внимательны при указании дистрибутива. В примере используется для Ubuntu 18.04. Смените при необходимости</span>
```
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable"
```

Обновляем список доступных нам пакетов:
```
sudo apt update
```

Убедитесь, что установка будет выполняться из репозитория Docker, а не из репозитория Ubuntu по умолчанию:

<span style="color:yellow">**Внимание!** Источник должен быть примерно таким "https://download.docker.com/linux/ubuntu bionic/stable amd64 Packages"</span>
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

Переходим в директорию `/time-tracker-master/docker/java/app`, после чего выполняем следующие команды:
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


## Интеграционное тестирование приложения

### Подготовка к запуску тестов

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
sudo apt install python3.9-distutils python3-pip
```

После этого устаналвиваем python3.9 для системы по-умолчанию:

```
sudo update-alternatives --install /usr/bin/python python /usr/bin/python3.9 2
sudo update-alternatives --install /usr/bin/python python /usr/bin/python3.6 1
sudo update-alternatives --config python
```

Т.к. в примере Ubuntu 18.04, то в ней присутствует только python3.6 изначально. 

Далее обновляем pip3:
```
pip3 install --upgrade pip
pip3 install --upgrade setuptools
```

Теперь переходим в директорию с тестами `/time-tracker-master/pytests` и устанавливаем зависимости:
```
pip3 install -r requirements.txt
```

### Запуск тестов

Запуск тестов происзводится из директории `/time-tracker-master/pytests` следующей командой:
```
python3 -m pytest -s -v tests/*
```
Ключ -v позволяет включить verbose mode , а ключи -s отображает команду print

Продолжительность тестов около 10 минут, это обусловленно тем, что для тестирования получения статистики - эту статистику сначала надо создать.