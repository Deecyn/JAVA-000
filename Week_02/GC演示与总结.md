## GCLogAnalysis.java 运行
### 一、-Xmx512m -Xms512m 的情况下
> java -Xmx512m -Xms512m `-XX:+UseSerialGC` -XX:+PrintGCDetails GCLogAnalysis

结果：执行结束!共生成对象次数: `7926`，执行FullGC 5次，年轻代GC10次 左右
> java -Xmx512m -Xms512m -XX:+PrintGCDetails GCLogAnalysis

结果：执行结束!共生成对象次数: `7320`， 执行FullGC 5次，YoungGC 21次左右

> java -Xmx512m -Xms512m `-XX:+UseConcMarkSweepGC` -XX:+PrintGC GCLogAnalysis

结果：执行结束!共生成对象次数:8543 执行FullGC 4次，YoungGC 12次左右

#### 在设置堆大小 512m 的情况下，CMS 能够生成的对象次数最多，串行 GC 比并行 GC 效果稍好。在堆内存较小的情况下，并行和串行的 GC 效果相差不大，并行也没有体现出多线程的优势，反而还有线程上下文切换

### 二、 -Xmx2g -Xms2g 的情况下，调整执行时间到 4s，由于时间段不会进行FullGC
> java -Xmx2g -Xms2g `-XX:+UseSerialGC` -XX:+PrintGCDetails GCLogAnalysis

串行结果：执行结束!共生成对象次数: `52170`，执行 FullGC 2 次
> java -Xmx2g -Xms2g -XX:+PrintGCDetails GCLogAnalysis

并行结果：执行结束!共生成对象次数: `50661`， 执行 FullGC 2 次
> java -Xmx2g -Xms2g `-XX:+UseConcMarkSweepGC` -XX:+PrintGC GCLogAnalysis

CMS结果：执行结束!共生成对象次数: `54954`，执行 FullGC 4次

#### 在增大堆大小的情况下，效率均有所提高。CMS 的 FullGC 次数最多，因为 CMS 会预留一些 buffer，所以会比串行 gc 提前进行 GC


### 二、 -Xmx4g -Xms4g 的情况下，调整程序执行时间到 8s
> java -Xmx4g -Xms4g `-XX:+UseSerialGC` -XX:+PrintGCDetails GCLogAnalysis

结果：执行结束!共生成对象次数: `119269` 执行 FullGC 1 次
> java -Xmx4g -Xms4g -XX:+PrintGCDetails GCLogAnalysis

结果：执行结束!共生成对象次数: `116429`， 执行 FullGC 1 次

> java -Xmx4g -Xms4g `-XX:+UseConcMarkSweepGC` -XX:+PrintGC GCLogAnalysis

结果：执行结束!共生成对象次数: `105430` 执行 FullGC 2 次

> java -Xmx4g -Xms4g `-XX:+UseG1GC` -XX:+PrintGC GCLogAnalysis

结果：G1 执行结束!共生成对象次数: `122601`

#### 在增大堆到 4g 之后，串行 gc 的劣势就体现出来了，一次要回收很大的 Edge 区，即使 1s 内只进行了一次 gc，但是执行的时间大概 0.16s，时间比较长了导致性能下降，而并行 GC性能也下降，在 1s 内执行了两次 youngGC，一次 0.1 秒左右，一次 0.14 秒左右，导致性能还不如串行 GC；G1 较稳定，应该是只执行 1s，G1 的自动学习能力还没有得到体现，应该并行 GC 也有这个问题，自适应调整还没有开始就结束了


## gateway-server-0.0.1-SNAPSHOT.jar 运行

### -Xmx2g -Xms2g 情况下
> wrk -t 20 -c 100 -d 40s http://localhost:8088/api/hello

`并行GC` 结果：Requests/sec:  16571.02

`串行GC` 结果：Requests/sec:  20218.27

`CMS` 结果：Requests/sec:  17649.59

`G1GC` 结果：Requests/sec:  23278.21

结果显示，在2g堆的情况下，并行GC反而是吞吐量最低的GC

### -Xmx4g -Xms4g 情况下
> wrk -t 20 -c 100 -d 40s http://localhost:8088/api/hello

`并行GC` 结果：Requests/sec:  21994.78

`串行GC` 结果：Requests/sec:  17703.30

`CMS` 结果：Requests/sec:  22474.61

`G1GC` 结果：Requests/sec:  23463.83

结果显示，在 4g 堆下，基本符合想像中的情况，串行 gc 在大内存的情况下比较吃力，并行 GC 和 CMS 在结果中看起来差不多，他们各种有各自的侧重点。
