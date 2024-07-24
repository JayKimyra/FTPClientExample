# FTPClientExample
### Сборка проекта по средством следующих команд (FTPClientExample название директории с содержимым):
```
javac -d out FTPClientExample/*.java
jar cfm app.jar FTPClientExample/MANIFEST.MF -C out .
```
### Для запуска:
```
java -jar app.jar
```
### Перед запуском убедитесь что у вас локально запущен FTP сервер с настроенным пользователем, в котором есть файл с информацией в следующем виде:
![Screenshot 2024-07-24 064955](https://github.com/user-attachments/assets/744f1fd9-ff3b-40e2-959e-6d4639d56768)
