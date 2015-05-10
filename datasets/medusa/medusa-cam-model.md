## medusa-cam-sample.json

### PyTransforms
#### _long_
From column: _Location (long, lat, accuracy)_
>``` python
return md_getLong(getValue("Location (long, lat, accuracy)"))
```

#### _lat_
From column: _Location (long, lat, accuracy)_
>``` python
return md_getLat(getValue("Location (long, lat, accuracy)"))
```

#### _accuracy_
From column: _Location (long, lat, accuracy)_
>``` python
return md_getAccuracy(getValue("Location (long, lat, accuracy)"))
```

#### _photo_uri_
From column: _Image_
>``` python
return md_photo_uri(getValue("Image"))
```


### Semantic Types
| Column | Property | Class |
|  ----- | -------- | ----- |
| _photo_uri_ | `uri` | `schema:Photograph1`|


### Links
| From | Property | To |
|  --- | -------- | ---|
