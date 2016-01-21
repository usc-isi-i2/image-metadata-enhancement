#Image Search on Metadata Similarity - ElasticSerach Plugin
## Installation
1. Package the source code into a jar archive.
```
mvn package
```
This will create the jar file in target/releases folder.

2. Install the plugin
```
<ES_HOME>/bin/plugin install vector-similarity-plugin -url file://<PATH TO JAR ARCHIVE>
```

## Creating an image type
After installing the plugin, you can create a type in an index that conatins the mapping for the "image" type.
```
curl -XPUT 'localhost:9200/<index_name>/<type_name>/_mapping' -d '{
    "<type_name>": {
        "properties": {
            "name": {
                "type": "string"
            },
            "image": {
                "type": "image",
                "feature": {
                    "CEDD": {
                        "hash": "LSH"
                    }
                }
            }
        }
    }
}'
```

## Indexing an image
```
{
	"name":"002_0002.jpg",
	"image":"...feature vector as a base 64 encoded string..."
}
```

## Querying an image
```
{
	"sort": [{
		"_score": "desc"
	}],
	"fields": ["name"],
	"query": {
		"image": {
			"image": {
				"image": "...feature vector as a base 64 encoded string....",
				"feature": "CEDD",
				"hash": "LSH"
			}
		}
	}
}
```

