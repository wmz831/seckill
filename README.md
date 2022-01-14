# seckill
> seckill from IDEA

Logback配置：https://logback.qos.ch/manual/configuration.html  
Spring配置：https://docs.spring.io/spring/docs/  
pdf下载：https://docs.spring.io/spring/docs/4.1.7.RELEASE/spring-framework-reference/pdf/spring-framework-reference.pdf  
Mybaits配置：http://www.mybatis.org/mybatis-3/zh/index.html  

命令  
mvn archetype:generate -DgroupId=wmz -DartifactId=seckill -DarchetypeArtifactId=maven-archetype-webapp

### Mybits特点：  
参数 + SQL = Entity/List  
Mybits和Hibernate最大的区别是sql完全是由你去写，可充分发挥sql技巧  

XML提供SQL  
注解提供SQL  

Mapper自动实现DAO接口（推荐）  
API编程方式实现DAO接口（比如像JDBC，开启一个connection，创建一个statement，拿到resultSet）  

整合目标  
更少的编码：只写接口，不写实现  
更少的配置：别名；配置扫描；Mybaits自动实现DAO接口，自动注入，减少spring配置  
足够的灵活性：自己定制sql + 自由传参(各种表达式) = 结果集自动赋值  

dto方便service返回的数据封装

https://docs.spring.io/spring/docs/4.1.7.RELEASE/spring-framework-reference/htmlsingle/

salt 加密盐  
uuid  

默认spring事务只在发生未被捕获的 runtimeexcetpion（运行期异常）时才回滚。  

### spring aop  异常捕获原理：  
被拦截的方法需显式抛出异常，并不能经任何处理，这样aop代理才能捕获到方法的异常，才能进行回滚，默认情况下aop只捕获runtimeexception的异常，但可以通过配置来捕获特定的异常并回滚，换句话说在service的方法中不使用try catch 或者在catch中最后加上throw new runtimeexcetpion（），这样程序异常时才能被aop捕获进而回滚。  

解决方案：  
在catch中加入TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();  
手动回滚或者抛出throw new runtimeexcetpion();  
异常。前者不需要在上层（controller层）做任何处理，后者需要在上层捕获这个异常。  

并发性上不去是因为当多个线程同时访问一行数据时，产生了事务，因此产生写锁，每当一个获取了事务的线程把锁释放，另一个排队线程才能拿到写锁，QPS和事务执行的时间有密切关系，事务执行时间越短，并发性越高，这也是要将费时的I/O操作移出事务的原因。  

关于同类中调用事务方法的时候有个坑，同学们需要注意下AOP切不到调用事务方法。事务不会生效，解决办法有几种，可以搜一下，找一下适合自己的方案。本质问题时类内部调用时AOP不会用代理调用内部方法。  

解决方案:  
1、如果是基于接口动态代理 是没有问题的，直接使用接口调用  
2、如果是基于class的动态代理 可以用 AopContext.currentProxy()  解决，注意剥离方法一定是public 修饰 ！！  

使用行级锁 必须创建索引 也就是必须使用老师创建的方法进行创建表,不然innoDB默认使用表锁  

### mq消息队列

（先说明下，好长时间没写java了，轻喷）我这是对楼下的评论不认同作出评论@ResponseBody不是直接输出到页面，而是输出到Body域，然后以符合要求的字符串形式输出，因为springmvc默认的return输出方式是页面（这一点你可以使用ajax进行调用查看success里面的参数值是html页面，而使用@ResponsBody就可以得到我们想要的字符串）  

核心架构的具体流程步骤如下：  
1、首先用户发送请求——>DispatcherServlet，前端控制器收到请求后自己不进行处理，而是委托给其他的解析器进行处理，作为统一访问点，进行全局的流程控制；  
2、DispatcherServlet——>HandlerMapping， HandlerMapping将会把请求映射为HandlerExecutionChain对象（包含一个Handler处理器（页面控制器）对象、多个HandlerInterceptor拦截器）对象，通过这种策略模式，很容易添加新的映射策略；  
3、DispatcherServlet——>HandlerAdapter，HandlerAdapter将会把处理器包装为适配器，从而支持多种类型的处理器，即适配器设计模式的应用，从而很容易支持很多类型的处理器；  
4、HandlerAdapter——>处理器功能处理方法的调用，HandlerAdapter将会根据适配的结果调用真正的处理器的功能处理方法，完成功能处理；并返回一个ModelAndView对象（包含模型数据、逻辑视图名）；  
5、ModelAndView的逻辑视图名——> ViewResolver， ViewResolver将把逻辑视图名解析为具体的View，通过这种策略模式，很容易更换其他视图技术；    
6、View——>渲染，View会根据传进来的Model模型数据进行渲染，此处的Model实际是一个Map数据结构，因此很容易支持其他视图技术；  
7、返回控制权给DispatcherServlet，由DispatcherServlet返回响应给用户，到此一个流程结束。  
HandlerMapping：根据请求的url来找到对应的Handler对象  
HandlerAdapter：适配到handler对象的具体某个处理方法上（controller中的方法），并返回ModelAndView  
ViewResolver：把ModelAndView解析为具体的视图，返回具体的View    

这里有个小问题, 使用annotation-driven, 在3.1之后, handler映射和适配Spring使用的是RequestMappingHandlerMapping和RequestMappingHandlerAdapter, 打断点走一遍DispatcherServlet#doDispatch可以看到, 而且异常解析器也使用了新的ExceptionHandlerExceptionResolver  

@RequestMapping(value="/getProduct",name="产品中心",method=RequestMethod.GET)  
https://segmentfault.com/q/1010000010879255  
Assign a name to this mapping.(给这个mapping分配一个名称，没有实质性的作用)  
SpringMVC 4.0开始支持基于name值来构建访问的url，需要通过配置AbstractHandlerMethodMapping.，具体接口是HandlerMethodMappingNamingStrategy。  
你可以自定义name属性，默认制是通过类的大写字符+#+方法名构建的，比如TestController的getUser方法，name值默认为TC#getUser。  
使用上，官方说明是主要可以通过使用Spring jsp tag包里面的mvcUrl，来生成jsp到controller的链接  

### 高并发优化
合理的利用CDN
CDN:（内容分发网络）加速用户获取数据的系统。可是静态资源（html、css、js），也可是动态资源（），也可是大流量资源（视频）  
部署在离用户最近的网络节点上。用户通过运营商 -> 城域网 -> 主干网络  
命中CDN不需要访问后端服务器  
互联网公司自己搭建或租用CDN  

获取系统事件不用优化：  

Java GC(Garbage Collection,垃圾收集,垃圾回收)机制  

存储过程可能是性价比比较高的一种简单粗暴的解决方案。正如老师所讲，大型网站和公司用的还是内存原子库存+可靠性消息的方案。不过这个实现的成本比存储过程要高的多。  

网络传输 和 GC影响 是硬伤， 解决了这两个，其他都好说。  
数据库：尽量单表查询，好处是：a、好扩展（分库分表）  b、缓存利用率高   c、易于维护  d ...  
分布式消息队列：a、起到请求缓冲作用  b、与分布式服务系统平滑过渡   c ...  
分布式缓存： 好处：没别的 扩展好， 在集群环境下 必须使用的方案   缺点：事务...   网络延迟...  必须考虑  
事务： 使用事务的基本原则是：只包含对DB的操作，尽可能缩短事务时间  

### 大型系统部署架构




