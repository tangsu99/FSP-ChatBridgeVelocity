# FSP-ChatBridgeVelocity
### 一个 velocity 插件
* 跨服聊天与QQ群聊天互通

* 指令
```
/cbv            显示帮助
```
* QQ群聊天转发
* 使用[go-cqhttp](https://docs.go-cqhttp.org/guide/#go-cqhttp)，并配置正向 Websocket
* 或使用[mirai](https://docs.mirai.mamoe.net/)的[mirai-api-http](https://docs.mirai.mamoe.net/mirai-api-http/)，并配置 ws
* 使用`mirai-api-http`须在`mirai-api-http`配置文件中开启`verifyKey`，关闭`singleMode`
* 群内指令
```
!!help              显示帮助
!!mc <msg>          向服务器内玩家发送信息
!!chatSync on/off   消息同步
!!online            展示在线玩家
```
* 什么是聊天同步，开启后自动转发 mc/qq群 的聊天到 qq群/mc，群主与管理有权限开关。
* 服内指令
```
/cbv            显示帮助
!!qq <msg>      向QQ群内发送信息
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
  
  // 是否使用 goCQHttp，使用 miari-api-http 时须更改为 false
  "goCQHttp": true,
  // QQ群玩家加入信息格式化
  "QQJoinFormat": "{0} joined game.",

  // 机器人所登录的的QQ帐号
  "goCQHttp": "000000000",
  // 需要互通的群
  "QQGroup": "000000000",
  // 服务器地址
  "host": "127.0.0.1",
  // 服务器端口
  "port": "6700",
  // go-cqhttp 配置文件内设置的 access-token
  // 或者 miari-api-http 配置文件内设置的 verifyKey
  "token": "TOKEN"
}
```
* 聊天同步提示信息
```json5
{
  "noPermission": "没有权限开关聊天同步",
  "onState": "聊天同步已经是开启状态",
  "offState": "聊天同步已经是关闭状态",
  "on": "聊天同步开启成功",
  "off": "聊天同步关闭成功"
}
```
