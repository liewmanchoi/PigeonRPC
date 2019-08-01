# PigeonRPC
分布式服务框架

改造计划：先易后难
- [ ] 增加Provider包，修改Server和Provider实现，每个Provider类对象持有一个Server对象；一台服务器实体对应一个Server类
- [ ] 增加Consumer包
  - 修改Client和Consumer实现，每个Consumer类对象持有一个Client对象；一个服务器实体对应一个Client类；也就是说：一个JVM中最多只有一个Server对象和一个Client对象
  - 不同的调用方式应当使用专门的类进行解耦
  - 生成代理对象，将调用替换成代理类对象的调用
  - 负载均衡算法也在consumer侧实现
  - `callback`方法暂缓实现
