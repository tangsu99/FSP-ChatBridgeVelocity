# FSP-ChatBridgeVelocity
### 一个 velocity 插件
* 跨服聊天与QQ群聊天互通

* 指令
```
/cbv            显示帮助
```
* QQ群聊天互通需要 [go-cqhttp](https://docs.go-cqhttp.org/guide/#go-cqhttp)，并配置正向 Websocket
* 群内指令
```
!!help          显示帮助
!!mc            <msg>向服务器内玩家发送信息
!!online        展示在线玩家
```
* 配置文件
```json5
{
  // 跨服聊天
  "chatForwardEnabled": true,
  
  // 指令前缀
  // 服务器内玩家向QQ群内发送
  "mcRespondPrefix": "!!qq",
  // QQ群内向服务器内玩家发送
  "QQRespondPrefix": "!!mc",
  
  // 服务器列表
  // 用于 !!online 指令获取服务器在线玩家
  // 却保服务器名称填写与 velocity.toml 内的 servers 相同
  // 填写顺序即为输出顺序
  // 没有玩家在线的服务器不会展示
  // Survival online:
  // - tangsu99
  // Creative online:
  // - BusyPacket
  "serverList": [
    // 如
    // [servers]
    //  Survival = "127.0.0.1:30066"
    //  Creative = "127.0.0.1:30067"
    //  Mirror = "127.0.0.1:30068"
    "Survival",
    "Creative",
    "Mirror"
  ],
  
  // 跨服聊天格式化
  "messageFormat": "§8[§b{0}§8]§8<§6{1}§8> §7{2}",
  // 加入信息格式化
  "joinFormat": "[{0}] {1} joined {0}",
  // 退出信息格式化
  "leftFormat": "[{0}] {1} left {0}",
  
  // QQ群聊天互通
  "QQChatEnabled": true,
  // QQ群聊天信息格式化
  "QQMessageFormat": "[{0}]<{1}> {2}",
  // 是否向QQ群转发玩家加入信息
  "QQJoinMessageEnabled": true,
  // QQ群玩家加入信息冷却时间，默认30秒，防止玩家反复进出服务器造成群内刷屏
  "CD": 30,
  // QQ群玩家加入信息格式化
  "QQJoinFormat": "{0} joined game.",
  
  // 需要互通的群号
  "QQGroup": "0000000000",
  // go-cqhttp Websocket 地址
  "host": "127.0.0.1",
  // go-cqhttp Websocket 端口
  "port": "6700",
  // go-cqhttp 配置文件内设置的 access-token
  "token": "TOKEN"
}
```

#### 像素仙缘 FSP-Fantasy story in pixel world
* 是一个 Minecraft Java 版的服务器
* 公益
* 正版验证
* 入服需审核
* [点我](https://space.bilibili.com/661916647)
