es7.6 API


GET /_cat/indices?v



#--
GET /push_msg/_search?from=10&size=100
GET /mytest_taximeterstatus/_search
GET /push_msg/_mapping
GET /mytest_taximeterstatus/_mapping


GET /product/_search?q=desc:erji
GET /product/_search?q=erji

GET /product/_doc/1
POST /product/_update/1
{
  "doc":{
    "name" : "xiaomi phone erji"
  }
}


GET /product/_search
{
  "query": {
    "bool":{
      "must": [
        {"match": { "name": "nfc"}}
      ],
      "should": [
        {"range": {
          "price": {"gt":1999}
        }},
         {"range": {
          "price": {"gt":3999}
        }}
      ],
      "minimum_should_match": 0
    }
  }
}




GET /product/_search
{
  "query": {
    "term": {
      "name": "nfc phone"
    }
  }
}

GET /product/_search
{
  "query": {
    "match_phrase": {
      "name": "nfc phone"
    }
  }
}


GET /_analyze
{
  "analyzer": "standard",
  "text":"xiaomi nfc zhineng phone"
}


GET /saas-2020.08*/_search
{
  "_source": {
    "includes": ["message","Datetime","APIName"]
  }, 
  
  "track_total_hits": true
}


GET /service_objs_join_vehicle/_search

GET /saastime-2020.08/_search
{
  "_source": {
     "includes": ["message","Datetime","APIName"]
  }, 
  "query": {
    "match_all": {}
  },
  "track_total_hits": true
}


GET /push_msg/_search
{
  "size": 0, 
  "aggs": {
    "by_id": {
      "terms": {
        "field": "rec_uid.keyword",
        "size": 100
      }
    }
  }
}

#1
GET /biz_alarm_msg/_search
{
  "from": 0,
  "size": 10,
  "_source": [
    "create_time",
    "alarm_type",
    "handle_status",
    "rec_uid",
    "obj_id"
  ],
  "query": {
    "bool": {
      "must": [
        {
          "terms": {
            "obj_id": ["18082117150715259","18061209415874994","18062912132930390","18062914000298718","18061110541928130","66f55f543904437e98bce96a96903702","677e464ec96d4cebb7903d902ae7186f","edb2748cf51f42a5accf83bded7d3ec3","1647b0c7b5ae43e781d2dc6234747c49","19031415052563108","22638356f9fb4468b5ed9c91bd442a9a","26805c2ce6e848a4969feaafdd2b2f4b","4e3b82c4db164eacb08322f249484704","5b134d658f8e4863b5c6f03c3645ed55","6ba3bc809a02499d9204ec334a87421d","a1b87f86758c41e6b05ae176aa684de2","18061110550768395","18061209445476031","e5d89ee8a0cd481ab57277ece65e1337","18090518224140332","aecdae7d7055476dac0a193729407923","882f572381914d57b1a62657ca9ae764","c6371ea71ba641aab74dcfed13c9499c","ff6eafbd52194c539c9d0924496b4e71","0385772f9123490eb624b9607357e3a3","165f0c86981547629ceb5a7bfa112cdf","18061209455676438","19052318241678680","19052318251978892","1b5e29819502466ea3a53b42fd0c58e6","1c985e7f56124d2bbf1f2baffa035ba4","20011412550798793","20011413100890083","2d8a2922868244f094d27050431f6cbb","2f2f407762324d66968e44fe5c8169bd","3204d4163c39418c84568c8d21c7d7da","4ae51c30712c4fe8ad7709bbe43a4d87","4f73fc4d968f4e7ebc7bb3dd0bae3e5f","57c978529848477ea782c9f4437bb4dc","718a0e99ec95459ba93b385032721757","766e53803697416bb3264c6f44917ee0","7991114af1bc479c87cd40c27a1b23cf","85a950ae03f8424aac237171e1721312","92fd976f83a048a7a6b0a8d4941b44ff","93b21abae55547169e46a23fb21b6174","98cde790ca6f4442b0352a3c54e1862c","a442cc46580f4d07ae5f35eb154a4b4b","a6b06be796ab47e0b37fde04c008aba0","ab2a4720cf924817be706e39fcb9325c","bc6cefefdeca4d9ba656a407b3b2da10","e596aa0aaa2f4bdfa6adf7078e3dbcfc","fdda47eb8ce7473489fe165618a2c7a3"]
          }
        },
        {
          "terms": {
            "alarm_type": [200,201,202,203]
          }
        },
        {
          "match": {
            "handle_status": "0"
          }
        }
      ]
    }
  },
  "sort": [
    {
      "alarm_time": {
        "order": "desc"
      }
    }
  ]
}

GET /push_msg/_search
#2
GET /push_msg/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "terms": {
            "msg_type": [
              "207"
            ]
          }
        },
        {
          "terms": {
            "received": [
              "0",
              "1",
              "2"
            ]
          }
        },
        {
          "term": {
            "app_name.keyword": {
              "value": "CarFleetMan"
            }
          }
        },
        {
          "term": {
            "push_mode": {
              "value": "0"
            }
          }
        },
        {
          "term": {
            "user_id": {
              "value": "9947db359d9843d595451fad0ce85b3e"
            }
          }
        }
      ],
      "must_not": [
        {
          "term": {
            "clean_flag": {
              "value": "1"
            }
          }
        }
      ]
    }
  }
}

#3
GET service_objs_join_vehicle/_search
{
  "query": {
    "bool": {
      "filter": [
        {
          "term": {
            "status": "1"
          }
        },
        {
          "term": {
            "rec_status": "1"
          }
        },
        {
          "prefix": {
            "license_plate_no.keyword": "苏A"
          }
        }
      ]
    }
  },
  "sort": [
    {
      "creat_time": {
        "order": "desc"
      }
    }
  ]
}

#4
GET device_status_vehicle_summary/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "exists": {"field": "corp_id"}
        }
      ]
      
    }
  }
}



POST /_bulk
{ "delete": { "_index": "product2",  "_id": "1" }}
{ "create": { "_index": "product2",  "_id": "1" }}
{ "name":    "_bulk create 2" }
{ "create": { "_index": "product2",  "_id": "12" }}
{ "name":    "_bulk create 12" }
{ "index":  { "_index": "product2",  "_id": "3" }}
{ "name":    "index product2 " }
{ "index":  { "_index": "product2",  "_id": "13" }}
{ "name":    "index product2" }
#{ "update": { "_index": "product2",  "_id": "4","retry_on_conflict" : "3"} }
#{ "doc" : {"test_field2" : "bulk test1"} }






