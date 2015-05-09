## foursquare-sample.json

### PyTransforms
#### _photo_get_url_
From column: _response / venues / id_
>``` python
return photoGetRequest(getValue("id"))
```


### Semantic Types
| Column | Property | Class |
|  ----- | -------- | ----- |
| _address_ | `schema:streetAddress` | `schema:PostalAddress1`|
| _city_ | `schema:addressLocality` | `schema:PostalAddress1`|
| _country_ | `rdfs:label` | `schema:Country1`|
| _id_ | `uri` | `schema:Place1`|
| _lat_ | `schema:latitude` | `schema:GeoCoordinates1`|
| _lng_ | `schema:longitude` | `schema:GeoCoordinates1`|
| _name_ | `rdfs:label` | `schema:Place1`|
| _phone_ | `schema:telephone` | `schema:Place1`|
| _pluralName_ | `schema:description` | `schema:Place1`|
| _state_ | `schema:addressRegion` | `schema:PostalAddress1`|


### Links
| From | Property | To |
|  --- | -------- | ---|
| `schema:Place1` | `schema:address` | `schema:PostalAddress1`|
| `schema:Place1` | `schema:geo` | `schema:GeoCoordinates1`|
| `schema:PostalAddress1` | `schema:addressCountry` | `schema:Country1`|
