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

NOTICE：

This software is licensed under the GNU Lesser General Public License (LGPL) version 3.0 or later. However, it is not permitted to use this software for commercial purposes without explicit permission from the copyright holder.
If the above restrictions are violated, all commercial profits generated during unauthorized commercial use shall belong to the copyright holder. 
The copyright holder reserves the right to pursue legal liability against infringers through legal means, including but not limited to demanding the cessation of infringement and compensation for losses suffered as a result of infringement.
本软件根据GNU较宽松通用公共许可证（LGPL）3.0或更高版本获得许可。但是，未经版权所有者明确许可，不得将本软件用于商业目的。
若违反上述限制，在未经授权的商业化使用过程中所产生的一切商业收益，均归版权所有者。
版权所有者保留通过法律途径追究侵权者法律责任的权利，包括但不限于要求停止侵权行为、赔偿因侵权行为所遭受的损失等。
