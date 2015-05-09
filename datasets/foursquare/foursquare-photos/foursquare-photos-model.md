## foursquare-photos-sample.json

### PyTransforms
#### _createdAt_iso_
From column: _response / photos / items / createdAt_
>``` python
return iso8601date(getValue("createdAt"), "epoch")
```

#### _photo_url_
From column: _response / photos / items / suffix_
>``` python
return photoUrl(getValue("prefix"),getValue("suffix"))
```


### Semantic Types
| Column | Property | Class |
|  ----- | -------- | ----- |
| _createdAt_iso_ | `schema:dateCreated` | `schema:Photograph1`|
| _height_ | `schema:height` | `schema:Photograph1`|
| _id_ | `uri` | `schema:Photograph1`|
| _photo_url_ | `schema:url` | `schema:Photograph1`|
| _width_ | `schema:width` | `schema:Photograph1`|


### Links
| From | Property | To |
|  --- | -------- | ---|
