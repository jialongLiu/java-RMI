# RMI学习笔记

## 实验用例
```
输入命令：
add lpl 2020-01-01-16:00 2020-01-01-17:00 happy     //第一次添加

add ljl 2020-01-01-16:00 2020-01-01-17:00 sad       //自己跟自己
add lpl 2020-01-01-16:00 2020-01-01-17:00 sad       //会议时间冲突

add lpl 2020-01-01-18:00 2020-01-01-19:00 sad0      //第二次添加
add default0 2020-01-02-18:00 2020-01-02-19:00 sad1 //第三次添加
add default1 2020-01-03-18:00 2020-01-03-19:00 sad2 //第四次添加
query 2020-01-01-16:00 2020-01-01-17:00             //查询

add default1 2020-02-03-18:00 2020-02-03-19:00 sad21 //第五次添加二月
add default1 2020-02-04-18:00 2020-02-04-19:00 sad22 //第六次添加二月

query 2020-02-01-16:00 2020-02-28-17:00             //查询二月的会议
query 2020-01-01-16:00 2020-01-01-20:00             //查询两个
delete 1                                            //删除id=1的会议
clear                                               //清除该用户创建的会议
quit                                                //退出

自作主张小功能：
1. 端口、用户名、用户密码可以main参数，可以手动输.
2. 用户名重复可以自动使用默认用户名fault+i
```




## 一、理论原理学习

### 1. 概念

1. **远程方法调用（RMI）**顾名思义是一台机器上的程序调用另一台机器上的方法。
2. RMI的**目的**就是要使运行在不同的计算机中的对象之间的调用表现得像本地调用一样。
3. **RMI要解决的是**：是让客户端对远程方法的调用可以相当于对本地方法的调用而屏蔽其中关于远程通信的内容，即使在远程上，也和在本地上是一样的。

### 2. 过程

1. 当客户端调用远程对象方法时, **存根stub**负责把要调用的远程对象方法的**方法名及其参数**编组打包,并将该包向下经远程引用层、传输层转发给远程对象所在的服务器。
2. 通过 RMI 系统的 RMI 注册表实现的简单服务器**名字服务**, 可定位远程对象所在的服务器。
3. 该包到达服务器后, 向上经远程引用层, 被远程对象的 **Skeleton 接收**, 此 Skeleton 解析客户包中的方法名及编组的参数后, 在服务器端执行客户要调用的远程对象方法, 然**后将该方法的返回值**( 或产生的异常) 打包后通过相反路线返回给客户端,。
4. 客户端的 Stub 将**返回结果解析后传递给客户程序**。
5. 事实上, 不仅客户端程序可以通过存根调用服务器端的远程对象的方法, 而服务器端的程序亦可通过由客户端传递的远程接口回调客户端的远程对象方法。在分布式系统中, 所有的计算机可以是服务器, 同时又可以是客户机。

![img](https://images2015.cnblogs.com/blog/735119/201603/735119-20160319210716896-1990636233.jpg)

### 3. 程序实现原理

1. 　Remote 接口用于标识其方法可以**从非本地虚拟机上调用的接口**。任何远程对象都必须直接或间接实现此接口。只有在“远程接口”（扩展 java.rmi.Remote 的接口）中**指定的这些方法**才可远程使用。 
2.  也就是说需要远程调用的方法必须在**扩展Remote接口的接口中声名并且要抛出RemoteException异常**才能被远程调用。
3. **远程对象**必须实现java.rmi.server.UniCastRemoteObject类，这样才能保证客户端访问获得远程对象时，该远程对象将会把自身的一个**拷贝序列化后**以Socket的形式**传输给客户端**，此时客户端所获得的这个**拷贝称为“存根”**，而服务器端本身**已存在的远程对象**则称之为**“骨架”。**
4. 其实此时的**存根**是**客户端的一个代理**，用于与服务器端的通信，而**骨架**也可认为是**服务器端的一个代理**，用于接收客户端的请求之后调用远程方法来响应客户端的请求。 远程对象的接口和实现必须在客户端和服务器端**同时存在并且保持一致**才行。
5. **实现原理**：客户端只与代表远程主机中对象的`Stub`对象进行通信，丝毫不知道`Server`的存在。客户端只是调用`Stub`对象中的本地方法，`Stub`对象是一个本地对象，它实现了远程对象向外暴露的接口。客户端认为它是调用远程对象的方法，实际上是调用`Stub`对象中的方法。**可以理解为`Stub`对象是远程对象在本地的一个代理**，当客户端调用方法的时候，`Stub`对象会将调用通过网络传递给远程对象。
6. 图中的stub和skeleton代理都是在服务端程序中由RMI系统动态生成，服务端程序只需要继承java.rmi.server.UnicastRemoteObject类。

![img](https://img-blog.csdn.net/20150517121935587?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGluZ2h1bl80/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)实现步骤

7. 要完成以上步骤需要有以下几个步骤： 
   1. 生成一个远程接口 
   2. 实现远程对象(服务器端程序)
   3. 生成占位程序和骨干网(服务器端程序)
   4. 编写服务器程序 
   5. 编写客户程序 
   6. 注册远程对象 
   7. 启动远程对象 

### 4. 程序实现步骤

1. 概述：可以说，RMI由**3个部分**构成，第一个是**RMIService**即JDK提供的一个可以独立运行的程序（bin目录下的rmiregistry），第二个是RMIServer即我们自己编写的一个java项目，这个项目对外提供服务。第三个是**RMIClient**即我们自己编写的另外一个java项目，这个项目远程使用RMIServer提供的服务。

2. 首先，RMIService必须先启动并开始监听对应的端口。

3. 其次，RMIServer将自己提供的服务的实现类注册到RMIService上，并指定一个访问的路径（或者说名称）供RMIClient使用。

4. 最后，RMIClient使用事先知道（或和RMIServer约定好）的路径（或名称）到RMIService上去寻找这个服务，并使用这个服务在本地的接口调用服务的具体方法。

5. 通俗的讲完了再稍微技术的讲下：

   1. 首先，在一个JVM中启动rmiregistry服务，启动时可以指定服务**监听**的端口，也可以使用默认的端口。
   2. 其次，RMIServer在本地先实例化一个提供服务的实现类，然后通过RMI提供的Naming，Context，Registry等类的bind或rebind方法将刚才实例化好的实现类注册到RMIService上并对外暴露一个名称。
   3. 最后，RMIClient通过本地的接口和一个已知的名称（即RMIServer暴露出的名称）再使用RMI提供的Naming，Context，Registry等类的lookup方法从RMIService那拿到实现类。这样虽然本地没有这个类的实现类，但所有的方法都在接口里了，想怎么调就怎么调吧。
   4. 值得注意的是理论上讲RMIService，RMIServer，RMIClient可以部署到3个不同的JVM中，这个时候的执行的顺序是RMIService---RMIServer—RMIClient。另外也可以由RMIServer来启动RMIService这时候执行的顺序是RMIServer—RMIService—RMIClient。

   ***\*实际应用中很少有单独提供一个RMIService服务器，开发的时候可以使用Registry类在RMIServer中启动RMIService。\****

## 二、实验学习阶段

### 1. 远程接口

1. 在Java中，只要一个类**extends了java.rmi.Remote接口**，即可成为存在于服务器端的远程对象，供客户端访问并提供一定的服务。

2. 何远程对象都**必须直接或间接实现此接口。**只有在“远程接口” （扩展 java.rmi.Remote 的接口）中指定的这些方法才可被远程调用。 

3. ```java
   import java.rmi.Remote;
   
   public interface IHello extends Remote {
   　　　　/* extends了Remote接口的类或者其他接口中的方法若是声明抛出了RemoteException异常，
    　　　　* 则表明该方法可被客户端远程访问调用。
    　　　　*/
   	public String sayHello(String name) throws java.rmi.RemoteException;
   }
   ```

### 2. 远程接口实现类

1. 远程对象**必须实现java.rmi.server.UniCastRemoteObject类**，这样才能保证客户端访问获得远程对象时，该远程对象将会把自身的一个拷贝**以Socket的形式传输给客户端**，此时客户端所获得的这个拷贝称为**“存根”**。

2. 而服务器端本身已存在的远程对象则称之**为“骨架”**。其实此时的存根是客户端的一个代理，用于与服务器端的通信，而骨架也可认为是服务器端的一个代理，用于接收客户端的请求之后调用远程方法来响应客户端的请求。

3. ```java
   /*远程接口实现类*/
   import java.rmi.RemoteException;
   import java.rmi.server.UnicastRemoteObject;
    
   /*
    * 远程对象必须实现java.rmi.server.UniCastRemoteObject类，这样才能保证客户端访问获得远程对象时，
    * 该远程对象将会把自身的一个拷贝以Socket的形式传输给客户端，此时客户端所获得的这个拷贝称为“存根”，
    * 而服务器端本身已存在的远程对象则称之为“骨架”。其实此时的存根是客户端的一个代理，用于与服务器端的通信，
    * 而骨架也可认为是服务器端的一个代理，用于接收客户端的请求之后调用远程方法来响应客户端的请求。
    */ 
    
   /* java.rmi.server.UnicastRemoteObject构造函数中将生成stub和skeleton */
   public class HelloImpl extends UnicastRemoteObject implements IHello {
       // 这个实现必须有一个显式的构造函数，并且要抛出一个RemoteException异常  
       protected HelloImpl() throws RemoteException {
           super();
       }
       
       private static final long serialVersionUID = 4077329331699640331L;
       public String sayHello(String name) throws RemoteException {
           return "Hello " + nae + " ^_^ ";
       }
   }
   ```

### 3. 服务端

1. 注册远程对象,向客户端**提供远程对象服务**
2. **远程对象是在远程服务上创建的**，你无法确切地知道远程服务器上的对象的名称
3. 但是，**将远程对象注册**到RMI Service之后，客户端就可以通过RMI Service请求到该远程服务对象的stub了，利用stub代理就可以访问远程服务对象了
4. **其实基本上就是为了new一个远程对象并且注册**

5. ```java
   /* HelloServer.java */
   package mytest;
   import java.rmi.registry.LocateRegistry;
   import java.rmi.registry.Registry;
    
   /* 注册远程对象,向客户端提供远程对象服务 
    * 远程对象是在远程服务上创建的，你无法确切地知道远程服务器上的对象的名称
    * 但是，将远程对象注册到RMI Service之后，客户端就可以通过RMI Service请求
    * 到该远程服务对象的stub了，利用stub代理就可以访问远程服务对象了
    */
    
   public class HelloServer {
       public static void main(String[] args) {
           try {
               IHello hello = new HelloImpl(); /* 生成stub和skeleton,并返回stub代理引用 */
               /* 本地创建并启动RMI Service，被创建的Registry服务将在指定的端口上侦听到来的请求 
                * 实际上，RMI Service本身也是一个RMI应用，我们也可以从远端获取Registry:
                *     public interface Registry extends Remote;
                *     public static Registry getRegistry(String host, int port) throws RemoteException;
                */
               LocateRegistry.createRegistry(1099);
               /* 将stub代理绑定到Registry服务的URL上 */
               java.rmi.Naming.rebind("rmi://localhost:1099/hello", hello);
               System.out.print("Ready");
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
   }
   ```

### 4. 客户端

1. 客户端向服务端请求远程对象服务

2. **从RMI Registry中请求stub** 如果RMI Service就在本地机器上，URL就是：rmi://localhost:1099/hello

3. 否则，URL就是：rmi://RMIService_IP:1099/hello

4. 必须得**强制类型转换**一下：IHello hello = (IHello) Naming.lookup("rmi://localhost:1099/hello");

5. ```java
   /* Hello_RMI_Client.java */
   package mytest;
   import java.rmi.Naming;
    
   /* 客户端向服务端请求远程对象服务 */
   public class Hello_RMI_Client {
       public static void main(String[] args) {
           try {
               /* 从RMI Registry中请求stub
                * 如果RMI Service就在本地机器上，URL就是：rmi://localhost:1099/hello
                * 否则，URL就是：rmi://RMIService_IP:1099/hello
                */
               IHello hello = (IHello) Naming.lookup("rmi://localhost:1099/hello");
               /* 通过stub调用远程接口实现 */
               System.out.println(hello.sayHello("zhangxianxin"));
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
   }
   ```

## 三、程序结构个人总结

#### 1. 远程接口类：是一个可以被远程调用的接口，用来被实现，实现他的类变成了一个真正要执行的java程序包括要被远程调用的方法。

#### 2. 远程接口实现类：是远程接口的一个实现，就是真正的java程序，就是为了被远程的程序调用它里面的方法。

#### 3. 服务器端：其实基本上就是为了new一个远程对象并且注册到RMIreqistry。

#### 4. 客户端：就是请求远程对象服务，在注册中心找远程方法。

#### 5. javaBean类：就是实体类



![img](https://img-blog.csdn.net/20150517173807668?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGluZ2h1bl80/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

## 四、实验实现阶段

### 1. JavaBean

1. JavaBean是描**述Java的软件组件模型，**有点类似于Microsoft的COM组件概念。在Java模型中，通过JavaBean可以无限扩充Java程序的功能，通过**JavaBean的组合**可以快速的生成新的应用程序。对于程序员来说，最好的一点就是JavaBean可以**实现代码的重复利用，**另外对于程序的易维护性等等也有很重大的意义
2. 举个栗子：比如说一个**购物车程序**，要实现购物车中添加一件商品这样的功能，就可以写一个**购物车操作的JavaBean，**建立一个public的AddItem成员方法，前台Jsp文件里面直接调用这个方法来实现。如果**后来又考虑添加**商品的时候需要判断库存是否有货物，没有货物不得购买，在这个时候我们就可以**直接修改JavaBean的AddItem方法**，加入处理语句来实现，这样就完全不用修改前台jsp程序了。
3. 在本实验中，bean就是**两个实体（用户和会议）。**
4. 方法基本上就是**操作这两个实体**的一些基本方法，比如get、set、tostring等
5. 与远程接口类和远程接口实现类的区别：bean里的方法是操作实体内的属性为目的（设置会议的id、获取会议的时间）。远程接口实现类里的方法是操作实体的对象整体（添加一个会议、清空所有会议等）。

## 本文参考：

1. https://blog.csdn.net/xinghun_4/article/details/45787549
2. https://www.cnblogs.com/handsomeye/p/6514026.html
3. https://blog.csdn.net/wubinghai/article/details/82951769







