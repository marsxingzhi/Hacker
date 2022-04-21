## method-invoke-optimize      

### TODO     
- [ ] 使用core api，实现简易的删除（需验证Java和Koltin）
- [ ] 使用core api，实现复杂的删除（同上）
- [ ] 使用tree api     

### 目的     
删除方法调用，减少包体积。

### 如何使用
使用方式：   
- 在根build.gradle文件中引入插件
```java
buildscript {
    dependencies {
        classpath "com.mars.infra:method-invoke-optimize-plugin:0.0.1"
    }
}
```

- 在build.gradle文件中apply插件，并配置extension
```java
plugins {
    id 'com.mars.infra.method.invoke.optimize.plugin'
}
methodInvokeOptimize {
    enable = true
    optimizeList = ["android/util/Log#e#(Ljava/lang/String;Ljava/lang/String;)I",
                    "com/mars/infra/hacker/Logger#ee#(Ljava/lang/String;Ljava/lang/String;)I"]
}
```

优化之前：
![method-invoke-optimize-before](./assets/method-invoke-optimize-before.png)

优化之后：
![method-invoke-optimize-after](./assets/method-invoke-optimize-after.png)

### 思路  
