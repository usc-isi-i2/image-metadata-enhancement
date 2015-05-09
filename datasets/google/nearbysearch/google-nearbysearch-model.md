## nearbysearch-sample.json

### PyTransforms
#### _types_nice_
From column: _results / types / values_
>``` python
return glTypesNice(getValue("values"))
```


### Semantic Types
| Column | Property | Class |
|  ----- | -------- | ----- |
| _id_ | `uri` | `schema:Place1`|
| _lat_ | `schema:latitude` | `schema:GeoCoordinates1`|
| _lng_ | `schema:longitude` | `schema:GeoCoordinates1`|
| _name_ | `rdfs:label` | `schema:Place1`|
| _types_nice_ | `schema:description` | `schema:Place2`|


### Links
| From | Property | To |
|  --- | -------- | ---|
| `schema:Place1` | `schema:geo` | `schema:GeoCoordinates1`|
| `schema:Place2` | `schema:geo` | `schema:GeoCoordinates1`|
