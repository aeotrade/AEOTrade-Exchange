# 传输系统

1. 执行maven编译，系统依赖jdk11及以上，在target目录中获得程序包aeochainexchange.jar，放入你设计的程序执行目录

2. 在jar包的同级目录新建application.properties文件，并增加传输配置信息，例如：

    aeochain.exchange.rabbitmq-infos[0].user-id= 用户ID
    
    aeochain.exchange.rabbitmq-infos[0].receive-queue= 接收用户消息的队列名
    
    aeochain.exchange.rabbitmq-infos[0].send-queue= 向用户发送消息的队列名
    
    aeochain.exchange.rabbitmq-infos[0].rabbitmq.host= rabbitmq主机地址
    
    aeochain.exchange.rabbitmq-infos[0].rabbitmq.port= rabbitmq的端口
    
    aeochain.exchange.rabbitmq-infos[0].rabbitmq.username= rabbitmq的用户名
    
    aeochain.exchange.rabbitmq-infos[0].rabbitmq.password= rabbitmq的用户密码
    
    aeochain.exchange.rabbitmq-infos[0].rabbitmq.virtual-host= rabbitmq的虚拟主机
    
    //第二个用户，请将中括号中的数字加1，第三个用户依次再加1，不可重复
    
    aeochain.exchange.rabbitmq-infos[1].user-id=
    
    aeochain.exchange.rabbitmq-infos[1].receive-queue=
    
    aeochain.exchange.rabbitmq-infos[1].send-queue=
    
    aeochain.exchange.rabbitmq-infos[1].rabbitmq.host=
    
    aeochain.exchange.rabbitmq-infos[1].rabbitmq.port=
    
    aeochain.exchange.rabbitmq-infos[1].rabbitmq.username=
    
    aeochain.exchange.rabbitmq-infos[1].rabbitmq.password=
    
    aeochain.exchange.rabbitmq-infos[1].rabbitmq.virtual-host=

3. 系统运行依赖jdk11及以上版本，Windows采用java -jar aeochainexchange.jar启动程序，linux系统授予aeochainexchange.jar可执行权限，采用./aeochaincontract.jar启动程序。