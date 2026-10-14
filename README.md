# spring_transaction：注解 AOP 三通知 + XML 声明式事务（account 转账）

课堂练习14。Spring 整合 MyBatis 操作 account 表，用**注解方式（AspectJ）**为业务层添加前置/后置/环绕通知，用 **XML 声明式事务**（tx:advice + aop:advisor）管理转账方法。

## 结构
```
lwy.study.spring
├── aop/MyAnnotationAdvice.java   注解切面（@Before/@After/@Around + @Pointcut）
├── mapper/AccountMapper(.java/.xml)  selectAll / updateAccount / updateById
├── service/AccountService.java       接口：getAll / transfer(Account, Account)
├── service/impl/AccountServiceImpl   @Component，转账两笔 update
└── vo/Account.java                   id / name / Float money（包装类型！）
resources/applicationContext.xml   整合三件套 + aspectj-autoproxy + 事务配置
```

## 知识点 1：注解切面三件套
- `@Component` 交给 Spring 扫描 + `@Aspect` 声明是切面，缺一不可
- `@Pointcut("execution(* 包名.service.impl.*.*(..))")` 标在空方法上作为公共切点
- **引用公共切点的写法：`"全限定类名.方法名()"`**，如 `@After("lwy.study.spring.aop.MyAnnotationAdvice.pointcut()")`

## 知识点 2：三种通知与实测执行顺序
```
环绕通知—前置        ← @Around 里 proceed() 之前的代码
getAll               ← @Around 里 getSignature().getName() 打印的方法名
前置通知！            ← @Before（方法体执行前）
（方法体执行）
后置通知              ← @After（方法返回后，无论正常还是异常都走，类似 finally）
2                    ← @Around 里 proceed() 的返回值
环绕通知——后置        ← @Around 里 proceed() 之后的代码
```
**环绕是最外层的壳**：环绕前置最先、环绕后置最后，@Before/@After 夹在中间。
@Around 必须返回 `proceed()` 的结果，否则调用方拿不到返回值。

## 知识点 3：XML 声明式事务四步
```xml
<!-- 1. 事务管理器（spring-jdbc 包） -->
<bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>
<!-- 2. tx:advice 事务通知：name 支持通配符，REQUIRED=有事务加入没有就新建 -->
<tx:advice transaction-manager="txManager" id="txAdvice">
    <tx:attributes>
        <tx:method name="trans*" propagation="REQUIRED" isolation="DEFAULT"/>
        <tx:method name="add*" propagation="REQUIRED" isolation="DEFAULT"/>
        <tx:method name="modi*"/>
        <tx:method name="remo*"/>
    </tx:attributes>
</tx:advice>
<!-- 3+4. aop:config 用 aop:advisor 把事务通知绑到切点上 -->
<aop:config>
    <aop:pointcut id="txPointcut" expression="execution( * ...AccountServiceImpl.transfer(..))"/>
    <aop:advisor advice-ref="txAdvice" pointcut-ref="txPointcut"/>
</aop:config>
```
- **坑①：`<tx:advice>` 作为顶层标签必须写 id**，否则启动直接报
  `Id is required for element 'advice' when used as a top-level tag`（课堂实测翻车点）
- **坑②：`aop:advisor`（Spring 内置通知如事务）与 `aop:aspect`（自定义切面类）是两条路**，事务用 advisor
- XML 里记得声明 `xmlns:tx` 命名空间，否则 tx:advice 解析不了

## 知识点 4：事务回滚的两个条件（都是实测踩过的坑）
1. **必须抛出 RuntimeException**（或 Error）才会回滚。update 一个不存在的 id **影响 0 行但不报错**——
   所以"打印转账失败"≠"数据库回滚"，没有异常事务照样正常提交。
   想回滚就手动 `throw new RuntimeException(...)`。
2. **表引擎必须是 InnoDB，MyISAM 不支持事务！**
   建表没指定引擎时如果是 MyISAM，抛异常也照样落库，回滚完全是空气。
   检查：`show create table account;`　修复：`alter table account engine=InnoDB;`

## 知识点 5：代理类型
`<aop:aspectj-autoproxy proxy-target-class="false"/>`：false = JDK 动态代理（基于接口），true = CGLib（基于子类）。AccountServiceImpl 实现了接口所以用 JDK 代理。

## 测试
- `testGetAll`：查全表，通知链完整触发
- `testTransfer`：王五 →200 元→ 赵六（转出方传 **-200**，配 SQL 的 `money = money + #{money}` 正好是扣款），
  两笔 update 各影响 1 行，result=2 > 1 打印"转账成功！"
- 表里备了 `// int i = 1 / 0;` 注释，解开可演示异常回滚（钱原封不动退回）
