####################################################################################################
# KARMA WEB SERVICE REQUESTS
####################################################################################################
Foursquare:
give the R2rml model uri and rest api http url
curl --request POST --data 'R2rmlURI=file:///Users/d3admin/Desktop/image-metadata-enhancement/datasets/foursquare/foursquare-model-new.ttl&ContentType=JSON&DataURL=https://api.foursquare.com/v2/venues/search?ll=40.7,-74&client_id=4QJ5FZZWNOGPLT3A25UO4CN0EK3FQPJS0PNRO3KC31RL15YR&client_secret=FIYPITFGMNWYGJ02EX4XQINXJETDTNTSJ4ICTVCDVETQOHE3&v=20150528' http://localhost:8080/rdf/r2rml/json

The http url needs to be encoded so the request would be

curl --request POST --data 'R2rmlURI=file:///Users/d3admin/Desktop/github_repos/image-metadata-enhancement/datasets/foursquare/foursquare-model-new.ttl&ContentType=JSON&DataURL=https%3A%2F%2Fapi.foursquare.com%2Fv2%2Fvenues%2Fsearch%3Fll%3D40.7%2C-74%26client_id%3D4QJ5FZZWNOGPLT3A25UO4CN0EK3FQPJS0PNRO3KC31RL15YR%26client_secret%3DFIYPITFGMNWYGJ02EX4XQINXJETDTNTSJ4ICTVCDVETQOHE3%26v%3D20150528%0A' http://localhost:9999/rdf/r2rml/json


Google nearby search

curl --request POST --data 'R2rmlURI=file:///Users/d3admin/Desktop/github_repos/image-metadata-enhancement/datasets/google/nearbysearch/google-nearbysearch-model-new.ttl&ContentType=JSON&DataURL=https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=33.86,-118.19&radius=500&key=AIzaSyCvuvRV_XM1u49nRik46VsE0_1amOexOrw' http://localhost:8080/rdf/r2rml/json

The http url needs to be encoded so the request would be

curl --request POST --data 'R2rmlURI=file:///Users/d3admin/Desktop/github_repos/image-metadata-enhancement/datasets/google/nearbysearch/google-nearbysearch-model-new.ttl&ContentType=JSON&DataURL=https%3A%2F%2Fmaps.googleapis.com%2Fmaps%2Fapi%2Fplace%2Fnearbysearch%2Fjson%3Flocation%3D33.86%2C-118.19%26radius%3D500%26key%3DAIzaSyCvuvRV_XM1u49nRik46VsE0_1amOexOrw' http://localhost:8080/rdf/r2rml/json

osm data

####################################################################################################
# ELASTIC SEARCH
####################################################################################################


#List all indices
curl 'localhost:9200/_cat/indices?v'

#Delete all indices
curl -XDELETE 'http://localhost:9200/osm_data'
curl -XDELETE 'http://localhost:9200/googlemaps_data'
curl -XDELETE 'http://localhost:9200/foursquare_data'

#CREATE all indices
curl -XPUT 'http://localhost:9200/osm_data'
curl -XPUT 'http://localhost:9200/googlemaps_data'
curl -XPUT 'http://localhost:9200/foursquare_data'

#To create foursquare mapping file:
curl -XPUT http://localhost:9200/foursquare_data -d '
{
"mappings": {
      "foursquare": {
        "properties": {
          "@context": {
            "type": "string"
          },
          "a": {
            "type": "string"
          },
          "address": {
            "properties": {
              "a": {
                "type": "string"
              },
              "addressCountry": {
                "properties": {
                  "a": {
                    "type": "string"
                  },
                  "label": {
                    "type": "string"
                  }
                }
              },
              "addressLocality": {
                "type": "string"
              },
              "addressRegion": {
                "type": "string"
              },
              "streetAddress": {
                "type": "string"
              }
            }
          },
          "geo": {
            "properties": {
              "a": {
                "type": "string"
              },
              "lat": {
                "type": "string"
              },
              "lon": {
                "type": "string"
              }
            }
          },
          "label": {
            "type": "string"
          },
          "description": {
            "type": "string"
          },
          "globalLocationNumber": {
            "type": "geo_point"
          },
          "telephone": {
            "type": "string"
          },
          "uri": {
            "type": "string"
          }
        }
      }
    }
}'


#To Create Google Maps Mapping
curl -XPUT http://localhost:9200/googlemaps_data -d '
{
"mappings": {
      “googlemaps”: {
        "properties": {
          "@context": {
            "type": "string"
          },
          "a": {
            "type": "string"
          },
          "address": {
            "properties": {
              "a": {
                "type": "string"
              },
              "addressCountry": {
                "properties": {
                  "a": {
                    "type": "string"
                  },
                  "label": {
                    "type": "string"
                  }
                }
              },
              "addressLocality": {
                "type": "string"
              },
              "addressRegion": {
                "type": "string"
              },
              "streetAddress": {
                "type": "string"
              }
            }
          },
          "geo": {
            "properties": {
              "a": {
                "type": "string"
              },
              "lat": {
                "type": "string"
              },
              "lon": {
                "type": "string"
              }
            }
          },
          "label": {
            "type": "string"
          },
          "description": {
            "type": "string"
          },
          "globalLocationNumber": {
            "type": "geo_point"
          },
          "telephone": {
            "type": "string"
          },
          "uri": {
            "type": "string"
          }
        }
      }
    }
}'


#OSM http Request
http://open.mapquestapi.com/xapi/api/0.6/map/*[bbox=11.54,48.14,11.543,48.145]?key=b8uBAGZowqebVTadpoak56zmANQtQyUA

#Xapi WIKI
http://wiki.openstreetmap.org/wiki/Xapi#Tag_Predicates

#ELASTIC SEARCH OSM MAPPING
PUT _mapping/osm-way
{
 "osm-way": {
            "properties": {
               "@context": {
                  "type": "string"
               },
               "a": {
                  "type": "string"
               },
               "geo": {
                  "properties": {
                     "a": {
                        "type": "string"
                     },
                     "line": {
                        "type": "string",
                         "index":"not_analyzed"
                     }
                  }
               },
               "geoJSONpin": {
                  "properties": {
                     "location": {
                         "type": "geo_shape"
                     }
                  }
               },
                "geoJSONpinStr": {
                  "type": "string",
                  "index":"not_analyzed"
               },
               "label": {
                  "type": "string"
               },
               "uri": {
                  "type": "string"
               }
            }
         }
}
PUT _mapping/osm-building
{
 "osm-building": {
            "properties": {
               "@context": {
                  "type": "string"
               },
               "a": {
                  "type": "string"
               },
               "geo": {
                  "properties": {
                     "a": {
                        "type": "string"
                     },
                     "polygon": {
                        "type": "string",
                         "index":"not_analyzed"
                     }
                  }
               },
               "geoJSONpin": {
                  "properties": {
                     "location": {
                         "type": "geo_shape"
                     }
                  }
               },
                "geoJSONpinStr": {
                  "type": "string",
                  "index":"not_analyzed"
               },
               "label": {
                  "type": "string"
               },
               "uri": {
                  "type": "string"
               }
            }
         }
}

#Sense requests:

GET _search
        {
            "query": {
                "match": {
                   "description": "Yoga"
                }
            }
        }

GET googlemaps_data/_search
        {
            "query": {
                "match": {
                   "description": "Health"
                }
            }
        }


#geo-spatial query:
GET foursquare_data/_search
        {
            "query": {
                "filtered": {
                   "query": {
                   "match_all": {}
                   },
                   "filter": {
                       "geo_distance": {
                          "distance": 100,
                          "distance_unit": "km",
                          "globalLocationNumber": "40.699657951708524, -73.99976911704667"
                       }
                   }
                }
            }
        }



#Geo Spatial Query - invoke script to re rank the results :
GET _search
{
      "sort" : [ {"_score" : "asc"} ],
"size":5,
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
                     "lat": 34.0212021,
                     "lon": -118.2872039
                  },
                  "script": "geoshape_search_nearest",
                  "lang": "native"
               }
            }
         ]
      }
   }
}

