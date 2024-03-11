# WEATHER REST ENDPOINTS

## INSERT DATA

## Insert location

```BASH
curl --location 'http://localhost:8080/v1/locations' \
--header 'Content-Type: application/json' \
--data '{
    "code":"SCL",
    "city_name":"Santiago",
    "region_name":"Region Metropolitana",
    "country_code":"CL",
    "country_name":"Republica de Chile",
    "enabled":true
}'
```
# Insert weather in hours

```bash
curl --location --request PUT 'http://localhost:8080/v1/hourly/SCL' \
--header 'Content-Type: application/json' \
--data '[
    {
        "temperature": 10,
        "precipitation": 50,
        "status": "Cloudy",
        "hour_of_day": 8
    },
    {
        "temperature": 15,
        "precipitation": 50,
        "status": "Cloudy",
        "hour_of_day": 9
    },
    {
        "temperature": 16,
        "precipitation": 40,
        "status": "Cloudy",
        "hour_of_day": 10
    }
]'
```
## Insert weather realtime

```bash
curl --location --request PUT 'http://localhost:8080/v1/realtime/SCL' \
--header 'Content-Type: application/json' \
--data '{
    "temperature":50,
    "humidity":20,
    "precipitation":25,
    "status":"Cloudy",
    "wind_speed":30
}'
```

## Get information

### Fetch realtime weather by ip address

```bash
curl --location 'http://localhost:8080/v1/realtime' \
--header 'X-FORWARED-FOR: 101.46.168.0'
```
### Fetch realtime weather by ip location

```bash
curl --location 'http://localhost:8080/v1/realtime/SCL'
```