Wearhacks
============

### Project description

Our Wearhacks project. Think outside the glass!

Running on Heroku at http://wearhacks38.herokuapp.com

### API

- GET /items

Status code: 200 OK

Response body:
```
[
{
  "id":"abc-123",
  "name":"Mona Lisa",
  "audioUrl":"http://www.example.com/audio.mp3",
  "cards": [
    {
      "imageUrl":"http://www.example.com/image1.jpg",
      "text": "some text",
      "time":0
    },
    {
      "imageUrl":"http://www.example.com/image2.jpg",
      "text": "some text",
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
  "audioUrl":"http://www.example.com/audio.mp3",
  "cards": [
    {
      "imageUrl":"http://www.example.com/image1.jpg",
      "text": "some text",
      "time":0
    },
    {
      "imageUrl":"http://www.example.com/image2.jpg",
      "text": "some text",
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
  "audioUrl":"http://www.example.com/audio.mp3",
  "cards": [
    {
      "imageUrl":"http://www.example.com/image1.jpg",
      "text": "some text",
      "time":0
    },
    {
      "imageUrl":"http://www.example.com/image2.jpg",
      "text": "some text",
      "time":500
    }]
}
```

- GET /items/:id

Status code: 200 OK

Response body:
```
{
  "id":"abc-123",
  "name":"Mona Lisa",
  "audioUrl":"http://www.example.com/audio.mp3",
  "cards": [
    {
      "imageUrl":"http://www.example.com/image1.jpg",
      "text": "some text",
      "time":0
    },
    {
      "imageUrl":"http://www.example.com/image2.jpg",
      "text": "some text",
      "time":500
    }]
}
```

- DELETE /items/:id

Status code: 200 OK

Response body: The deleted Item

- PUT /items/:id/images

Entity:
```
{
    "imageUrl":"http://www.example.com/image3.jpg",
    "text":"some text",
    "time":1000
}
```
Status code: 200 OK

Response body: The updated item
