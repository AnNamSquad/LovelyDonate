```
napthe-root/
├── napthe-common/          # Shared core logic
│   ├── src/main/java/com/napthe/
│   │   ├── common/         # Core interfaces and models
│   │   ├── provider/       # Payment provider interfaces
│   │   └── strategy/       # Base strategy definitions
│   └── build.gradle
│
├── napthe-paper/           # Minecraft Paper implementation
│   ├── src/main/java/com/napthe/paper/
│   │   ├── polling/        # Polling implementation
│   │   ├── command/        # In-game commands
│   │   └── listener/       # Bukkit/Paper listeners
│   └── build.gradle
│
├── napthe-web/             # Web service implementation
│   ├── src/main/java/com/napthe/web/
│   │   ├── webhook/        # Webhook implementation
│   │   ├── controller/     # REST endpoints
│   │   └── security/       # Webhook validation
│   └── build.gradle
│
├── settings.gradle
└── build.gradle
```

Note:

* Cross Tasks

- [ ] get order id cross server, use redis caching for cross server, on one server, use atomiclong

* Normal task

- [x] implement giving coins
- [x] implement server milestone / need async cache thinggy
- [x] implement cache for placeholder, get player current số tiền đã nạp / also async cache thing
- [ ] migrate to okhttp connection pool
- [ ] handle when server cant connect to the internet(get ddosed, dns issues) ->
  mark payment as failed, the payment on psp should expire on their own. For card transaction, they should be cached to
  db somehow (prio)
- [x] implementation database to save payment history
- [x] implement menu nạp thẻ
- [x] implement menu xem lịch sử nạp admin và player
- [x] implement menu nạp thẻ floodgate
- [x] fix lỗi chuyển trang của menu xem lịch sử nạp
- [x] find a way to generate unique order id for payos
- [x] implement payos
- [x] test thesieutoc
- [x] idea: always have a timer task that check on the queue and process the payment

* Nap Lan Dau

- [x] implement EAV for database to store data flexible
- [ ] testing

* Milestone

- Check on payment success -> get all existing milestone -> forEach ->
- current = player current donated player
- amount = new charge amount
- if (current >= this.amount && current - amount < this.amount) {

* milestone
  [ ] testing 