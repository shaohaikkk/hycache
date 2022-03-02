## HyCache

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
