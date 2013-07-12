## A simple neo4j unmanaged extension to find paths between two points

usage example:
  `@Path("/findpathslen/{id1}/{id2}/{len}/{count}")`

Executing "/findpaths/findpaths/findpathslen/102/289/3/2" gets me up to two directed paths of length 3 between the two node ids (102, 289).

```
[
   [
      {
         "id":39812,
         "relType":"f",
         "props":{

         }
      },
      {
         "id":36171,
         "relType":"f",
         "props":{

         }
      },
      {
         "id":56895,
         "relType":"f",
         "props":{

         }
      }
   ],
   [
      {
         "id":39812,
         "relType":"f",
         "props":{

         }
      },
      {
         "id":36171,
         "relType":"f",
         "props":{

         }
      },
      {
         "id":27120,
         "relType":"f",
         "props":{

         }
      }
   ]
]
``` 

### configuration

Add this line to the end of your neo4j-server.properties file:
`org.neo4j.server.thirdparty_jaxrs_classes=findpaths=/findpaths`

Copy `findpaths_2.10-0.1.jar`, `lift-json_2.10-2.5.jar` and `paranamer-2.4.1.jar` into the `lib/` folder in your neo4j server installation.
