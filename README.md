# PigeonRPC
分布式服务框架

改造计划：先易后难
[ ] 删除繁杂、冗余二而且令人头晕的`Config`类，公共配置内容（如注册中心地址）由yaml文件配置，其余配置项全部在注解里面解决
  - 公共配置内容：
      - zookeeper地址及其余配置项
  - 个别配置内容：
    - 调用类class对象
    - 超时时间
[ ] 删除扩展机制ExtensionLoader
[ ] 删除Filter机制
[ ] 改正错误百出的Invocation机制
[ ] 坚决合并RPCRequest类与wrapper类
[ ] 优化RPCContext
[ ] 修改ServiceURL