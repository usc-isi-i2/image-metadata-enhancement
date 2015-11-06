#Feature Vector Similarity Plugin
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

##Running the Script
The Script takes 3 parameters.
metric - The type of vector similarity to run on the features. Expected Value : "cosine"
vector - The target vector to perform similarity for each document. Expected Value : Array of double values.
field  - The field in your document you wish to compare.
Example Script:
```
GET _search
{
    "query": {
      "function_score": {
         "query": {
            "match_all": {
             
            }
           
         },
         "functions": [
            {
               "script_score": {
                  "params": {
                     "metric":"cosine",
                     "vector": [1.0,2.0,3.0],
                     "field":"tags"
                     
                  },
                  "script": "vector-similarity-plugin",
                  "lang": "native"
               }
            }
         ]
      }
   }
}
```
