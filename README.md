[TOC]  

# HyCache 
> HyCache 是一款轻量级的缓存框架，通过注解的方式，方便用户快速的使用本地cache 和redis 等缓存工具。

## 背景
> Spring cache提供了开箱即用的接入方式,只需要若干注解和缓存管理类即可接入,但spring cache存在以下问题：
 1. Spring cache 对于缓存自动过期支持不是很友好
 2. 本质上应用了AOP,调用的时候必须跨对象才能生效，不符合用户习惯
 
## 设计
HyCache 主要采用了以下技术和原理
1. 使用 javassist 进行字节码增强，直接在当前对象中增加创建增强方法，是当前对象内的调用也可以基于注解生效。
2. 基于 缓存性能之王 caffeine 进行本地缓存的实现，提供优秀的性能

## 集成&& 使用
### 1. import dependency
```
<dependency>
  <groupId>io.github.shaohaikkk</groupId>
  <artifactId>hycache-starter</artifactId>
  <version>0.0.2</version>
</dependency>
```

### 2. add @HyCache on the method
#### demo1
```
@RequestMapping(value = "demo1")
public UserBean demo1() {
  UserService userService = new UserService();
  return userService.getUser();
}


@Slf4j
public class UserService {

    @HyCache(key = "user1", expire = 10)
    public UserBean getUser(){
        log.info("cache penetration");
        return new UserBean("tom",18);
    }
}

```

#### demo2
```
@RequestMapping(value = "demo2")
    public UserBean demo2() {
        return this.getUser();
    }
    @HyCache(key = "user2",expire = 10)
    public UserBean getUser(){
        log.info("cache penetration");
        return new UserBean("tom",18);
    }
```

#### demo3
```
@RequestMapping(value = "demo2")
    public UserBean demo2(int userId) {
        return this.getUser(userId);
    }
    @HyCache(key = "user:#userId",expire = 10)
    public UserBean getUser(int userId){
        log.info("cache penetration");
        return new UserBean("tom",18);
    }
```
## 问题
1. 目前仅springboot 的集成
2. springboot test 单元测试中会存在问题
3. caffeine 的过期实现不够优雅，较为复杂
4. 只支持String类型，不支持基础类型

