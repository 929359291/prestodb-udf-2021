#### 一、前言
##### 网上教程版本较老
* 网上许多prestodb自定义函数开发的博客都是比较老的版本与最新的版本有差异
##### presto有两个版本, 这里总结的是prestodb
* facebook维护的prestodb，包名com.facebook.presto
* 另一个独立发展的版本prestoSQL，包名io.prestosql
#### 二、安装prestodb本地环境，连接mysql
* 安装教程不做赘述，参见这个博客总结 [prestodb本地环境安装](https://www.jianshu.com/p/1936d345abac)
#### 三、prestodb自定义函数插件开发
* sql中有聚合函数跟一般函数，这里做一般函数的demo，即RSA非对称加密函数开发
* prestodb自带函数库不支持对称加密和非对称加密，因此要进行二次开发
##### 1、代码开发
* 由于我安装的prestodb是最新版本的，所以参照github官方库的demo进行编写
```
https://github.com/prestodb/presto/blob/master/presto-teradata-functions/src/main/java/com/facebook/presto/teradata/functions/TeradataStringFunctions.java#L63
```
![](https://oscimg.oschina.net/oscnet/up-25ce93214e73e55d11b34fd8df71ff34637.png)
* 在resources下创建META-INF/services/com.facebook.presto.spi.Plugin，文本内容为插件导入实现类的路径org.example.presto.RSAEncryptFunctionsPlugin
  ![](https://oscimg.oschina.net/oscnet/up-5794ef32a88fc8c95d14cb4cfbf9463690f.png)
  ![](https://oscimg.oschina.net/oscnet/up-3bbbf69843fe1b706a91e1b3bd270a38ac6.png)
* 入参出参都需要用注解标识@ScalarFunction、@SqlType，String类型传输都需要用Slice类包装，否则使用时prestodb是无法接收输入的
  ![](https://oscimg.oschina.net/oscnet/up-d927d038e365a87c3f2c03b61a3c87c19eb.png)
##### 2、测试开发的插件
* 如果依赖了jdk包和prestodb包以外的第三方包的话，需要将依赖都打包进插件jar中，否则prestodb重启加载插件jar时会报错找不到对应的class
###### 如图所示，这样build出来的jar才会将依赖的第三方包一起打包进jar，无第三方包则忽略
![](https://oscimg.oschina.net/oscnet/up-46884b0551c7e67fedf8c418c0feb9f02fb.png)
![](https://oscimg.oschina.net/oscnet/up-3963b728a8f26d33e5928344f6a970c1eb8.png)
![](https://oscimg.oschina.net/oscnet/up-91aa6887b4f5707c544de6a44ea7194292f.png)
![](https://oscimg.oschina.net/oscnet/up-06308cf0643006d5002b73f97aadfb7c0fa.png)
###### 将插件jar拷贝到prestodb的插件目录的自定义目录下，
* /opt/presto-server-0.245.1/plugin/
* /opt/presto-server-0.245.1/plugin/prestodb-udf-2021/prestodb-udf-2021.jar
* 重启prestodb
* nohup ./bin/launcher run > run.log &
* cat run.log|grep 'prestodb-udf-2021'
  ![](https://oscimg.oschina.net/oscnet/up-ac56edb45d92f0b8d9b948bff0f352a8dee.png)
* 这个日志表示加载成功了
###### 启动客户端测试自定函数， cli包自行官网下载
* java -jar presto-cli-0.245.1-executable.jar --server localhost:8080 --catalog mysql --schema sys
* 1.生成key

![](https://oscimg.oschina.net/oscnet/up-02a3c0373706d80cd84330cf9b15ac84b41.png)
* 2.公钥加密，china no.1
* 公钥
```
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIdQMkoYhNj18xHuNqjoek+ZPztonY5F2Tw5QKmfM6oQawh81ut6ZpVgGqKK9HV7CKFx+AsjjalvcHkrQuzusmx2sHtjv6LPmpVZM60qgQ00rz5+uPtVe5E51Pz8CxBOdN296yfeNdcjsdw8sNPlVtVv97gXHCVVbf0mdskk1GxwIDAQAB
```
* 3.私钥解密

![](https://oscimg.oschina.net/oscnet/up-870ad22a75a0d9ac98d9117141195176f28.png)
* 私钥
```
MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMh1AyShiE2PXzEe42qOh6T5k/O2idjkXZPDlAqZ8zqhBrCHzW63pmlWAaoor0dXsIoXH4CyONqW9weStC7O6ybHawe2O/os+alVkzrSqBDTSvPn64+1V7kTnU/PwLEE503b3rJ9411yOx3Dyw0+VW1W/3uBccJVVt/SZ2ySTUbHAgMBAAECgYAhYrOiTzDnUgCVqsJPhet7g4GrrnQo5mfba1Blr5w94IG/tu51CSszEfr2/w2qQSWK6iz5/zTO2yPLZBiUTywEnu5K4Qj2VSO+zowVM7BNMLDu2G2iSk7qNLtyf4prZApHNgkfurCouVSZvVU71yORog7iV2ThTn8G5k3iQd040QJBAOQCJa2e+GWg4AblOgl+XuDl82Ur4lClxL4LevkkTErfFbG5TEkIRuU+SHjWc9VSn/YT7GnlDI0ycnL26TtKGA8CQQDhEPthM+Cuyef0bnLfCJq6RC1kpWRb6yWGhrV1C1sTikoHeSiq6fi9saNcYeQCLd58O10I4r8JjC2kACHG/m3JAkEAwlPSKRfEPVnPlhDoVg8Obj+OIjw0tueRtyext4XoQCfCJab4DYawZdGfkt2z+6xgCSsR428EM5M7WCB4x79uoQJBANGKVgBxpEYXhPV1v00XXbb9sqV5tq49h3pwAJEwOWM/5RyAGPTHUOaLdGSWXwoIWpOfUUbslJ/2+qct2ETErMECQCOwbHBPJCXvER2aO3gyVVwDhMsAplkk9pb7gmnGBFAd8K2IPCSsB0WhhVlaEdpeI3tjc4YwzBAIFjB8Q680G5I=
```
* 4. 语法
```sql
select rsa_private_decrypt(rsa_public_encrypt('china no.1', pubKey), priKey)
```
#### 本项目github链接
https://github.com/929359291/prestodb-udf-2021.git