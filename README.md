# beigam-auth
Module for authorization

Для сборки используем
```
./gradlew buildFatJar
```

Затем делаем сборку
```
docker build -t beigam-auth .
```

И запускаем контейнер
```
docker run -p 8081:8081 beigam-auth
```