# Task Manager

Веб-приложение для создания, просмотра, редактирования и удаления задач, а также изменения их статуса: `NEW`, `IN_PROGRESS`, `DONE`.

## Технологии

- **Backend:** Java 25, Spring Boot 4.1.1, Spring MVC, Spring Data JPA, Hibernate.
- **База данных:** PostgreSQL; миграции схемы выполняются Flyway.
- **Frontend:** HTML, CSS, Vanilla JavaScript.
- **Сборка:** Maven.
- **Развёртывание:** Docker, Kubernetes и Minikube.
- **Логи:** JSON-логирование, VictoriaLogs, Grafana.
- **Метрики:** Spring Boot Actuator, Micrometer, vmagent и VictoriaMetrics; метрики PostgreSQL собираются через `postgres-exporter`.

## API

| Метод | Путь | Назначение |
|---|---|---|
| `GET` | `/api/tasks` | Получить список задач |
| `GET` | `/api/tasks/{id}` | Получить задачу по ID |
| `POST` | `/api/tasks` | Создать задачу |
| `PUT` | `/api/tasks/{id}` | Обновить задачу |
| `DELETE` | `/api/tasks/{id}` | Удалить задачу |
| `GET` | `/actuator/health` | Проверить состояние приложения |

## Архитектура и мониторинг

Приложение работает в Kubernetes вместе с PostgreSQL. Доступ к приложению предоставляет `task-manager-service` типа `NodePort`, а база данных доступна внутри кластера через `postgres-service` типа `ClusterIP`. Приложение масштабировалось до трёх реплик; PostgreSQL работает в одной реплике.

Логи приложения выводятся в `stdout` в JSON-формате. `victoria-logs-collector` собирает логи Kubernetes и передаёт их в VictoriaLogs; для просмотра и анализа подключена Grafana. Для метрик `vmagent` обнаруживает Pod по аннотациям `prometheus.io/*` и отправляет данные в VictoriaMetrics. Собираются метрики JVM, бизнес-метрики Task Manager и PostgreSQL.

## Запуск локально

Требуются Java 25, Maven и доступный PostgreSQL. Создайте базу `taskdb` и пользователя `taskuser`, затем задайте параметры подключения через переменные окружения `DB_URL`, `DB_USER`, `DB_PASSWORD`. Для локального запуска URL базы обычно указывает на `localhost:5432`.

```bash
mvn clean package
java -jar target/task-manager-1.0.0.jar
```

После запуска:
- Интерфейс: <http://localhost:8080>
- API: <http://localhost:8080/api/tasks>
- Health: <http://localhost:8080/actuator/health>
- Метрики Prometheus: <http://localhost:8080/actuator/prometheus>

## Развёртывание в Minikube

Соберите образ и загрузите его в Minikube:

```bash
docker build -t task-manager:1.0 .
minikube image load task-manager:1.0
```

Примените манифесты приложения и PostgreSQL:

```bash
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/postgres-service.yaml
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml
```

Проверить ресурсы и открыть приложение:

```bash
kubectl get pods
kubectl get deployments
kubectl get services
minikube service task-manager-service
```

Масштабировать приложение до трёх реплик:

```bash
kubectl scale deployment task-manager --replicas=3
```

## Проверка логов и метрик

Посмотреть логи приложения:

```bash
kubectl logs deployment/task-manager --tail=10
```

Открыть VictoriaMetrics локально:

```bash
kubectl port-forward svc/vm-victoria-metrics-single-server -n metrics 8428:8428
```

Затем перейти на <http://localhost:8428/vmui/> и выполнить, например, запросы PromQL:

```promql
up
jvm_memory_used_bytes
pg_up
pg_stat_database_numbackends
{__name__=~"tasks_.*"}
```

Примеры LogsQL для VictoriaLogs/Grafana:

```text
_time:10m kubernetes.pod_name:task-manager*
_time:30m kubernetes.pod_name:task-manager* log.level:WARN
```
