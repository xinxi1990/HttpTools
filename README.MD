# 愿景
通过配置yaml文件就能进行简单的接口测试,降低编写代码的成本,让接口测试更加容易

# 用例组织
https://cn.httprunner.org/concept/testcase-structure/

# 更新
```
1.支持debug模式调试
2.支持测试用例文件夹参数
3.增加输入请求和响应日志
```


# jar包使用
```$xslt
把该工具打包成jar包,jar包在target目录下

打包命令:mvn package

java -jar HttpTools-1.0-SNAPSHOT.jar 
-f /Users/xinxi/get_temp.yaml 测试用例路径 或者 /Users/xinxi/ 测试用例文件夹
-r /Users/xinxi/Desktop 保存的文件目录
-v ALL 日志等级
```

# 模版格式
```$xslt
get请求

- info:
    name: "查询快递"
  given:
    queryParam:
      type: "shentong"
      postid: "111111"
    headers:
      Accept: "*/*"
    request: "get"
  when:
    url: "http://www.kuaidi100.com/query"
  then:
    statusCode: 200
    body:
    - eq : ["message", "ok"]
```

```$xslt
post请求

- info:
    name: "电子书详情页"
  given:
    body:
      bid: 3210
      userInfo:
        uid: "229461915"
    headers:
      Content-Type: "application/json"
    request: "post"
  when:
    url: "https://m.igetget.com/hybrid/api/v1/ebook/detail"
  then:
    statusCode: 200
    body:
     - eq : ["errCode", "0"]
```

# 断言
1.支持一个请求多次断言，断言失败可以继续后续断言<br>
2.支持等于、不等于、大于、小于方式的断言


# 报告
根据-r的入参数路径生成在该文件夹路径下,case失败会有具体的报错信息


# 响应解析
使用jsonpath语法解析响应


# 问题
```
io.restassured.path.json.exception.JsonPathException: Failed to parse the JSON document

https://testerhome.com/topics/6672
```
