## foursquare.json

### PyTransforms
#### _boundingbox_
From column: _response / suggestedBounds / Glue_1 / lng_1_
>``` python
return boundingbox(getValue("lat"), getValue("lat_1"), getValue("lng"), getValue("lng_1"))
```

#### _id_
From column: _response / groups / items / venue / id_
>``` python
return "https://api.foursquare.com/v2/venues/" + getValue("id")
```

#### _name_
From column: _response / groups / items / venue / name_
>``` python
return trimData(getValue("name"))
```

#### _street_address_
From column: _response / groups / items / venue / location / formattedAddress_
>``` python
return getValueFromNestedColumnByIndex("formattedAddress","values",0)
```


### Semantic Types
| Column | Property | Class |
|  ----- | -------- | ----- |
| _boundingbox_ | `schema:box` | `schema:GeoShape1`|
| _city_ | `schema:addressLocality` | `schema:PostalAddress1`|
| _id_ | `uri` | `schema:Place1`|
| _lat_ | `schema:latitude` | `schema:GeoCoordinates1`|
| _lng_ | `schema:longitude` | `schema:GeoCoordinates1`|
| _name_ | `rdfs:label` | `schema:Place1`|
| _name_ | `km-dev:columnSubClassOfLink` | `schema:Place1`|
| _pluralName_ | `schema:description` | `schema:Place1`|
| _postalCode_ | `schema:postalCode` | `schema:PostalAddress1`|
| _state_ | `schema:addressRegion` | `schema:PostalAddress1`|
| _street_address_ | `schema:streetAddress` | `schema:PostalAddress1`|


### Links
| From | Property | To |
|  --- | -------- | ---|
| `schema:Place1` | `schema:address` | `schema:PostalAddress1`|
| `schema:Place1` | `schema:geo` | `schema:GeoCoordinates1`|
| `schema:Place1` | `schema:geo` | `schema:GeoShape1`|
