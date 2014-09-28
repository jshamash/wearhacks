Wearhacks
============

### Project description

Our Wearhacks project. Think outside the glass!

### API

- GET /items
Status code: 200 OK
Response body:
```
[
{
  "id":"abc-123",
  "name":"Mona Lisa",
  "audio":"http://www.example.com/audio.mp3",
  "images": [
    {
      "url":"http://www.example.com/image1.jpg",
      "time":0
    },
    {
      "url":"http://www.example.com/image2.jpg",
      "time":500
    }]
},
...
]
```

- POST /items
entity:
```
{
  "id":"abc-123",
  "name":"Mona Lisa",
  "audio":"http://www.example.com/audio.mp3",
  "images": [
    {
      "url":"http://www.example.com/image1.jpg",
      "time":0
    },
    {
      "url":"http://www.example.com/image2.jpg",
      "time":500
    }]
}
```
Status code: 201 Created
Response body:
```
{
  "id":"abc-123",
  "name":"Mona Lisa",
  "audio":"http://www.example.com/audio.mp3",
  "images": [
    {
      "url":"http://www.example.com/image1.jpg",
      "time":0
    },
    {
      "url":"http://www.example.com/image2.jpg",
      "time":500
    }]
}
```

- GET /items/<id>
Status code: 200 OK
Response body:
```
{
  "id":"<id>",
  "name":"Mona Lisa",
  "audio":"http://www.example.com/audio.mp3",
  "images": [
    {
      "url":"http://www.example.com/image1.jpg",
      "time":0
    },
    {
      "url":"http://www.example.com/image2.jpg",
      "time":500
    }]
}
```

- DELETE /items/<id>
Status code: 200 OK
Response body: The deleted Item

- PUT /items/<id>/images
Entity:
```
{
    "url":"http://www.example.com/image3.jpg",
    "time":1000
}
```
Status code: 200 OK
Response body: The updated item
