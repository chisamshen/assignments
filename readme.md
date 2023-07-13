# Home Test
Build with gradle and run with java 11.

```shell
> gradle wrapper
> ./gradlew build
```

## Introduction
This project is to demonstrate the message flow between producer and consumer(s), you may find the different assumptions and corresponding test cases in the test package. As the messages produced by producer also within the project. So a simple object pool is implemented as well.
### Few points want to highlight
* Please note that the test cases in `TransmissionFlowTest` are those kick start producer/consumer threads. Others test cases are unit test mainly.

* How to handle overly productive producers? 
  * The overly productive producers problem means slow consumer problem (or back pressure as well). The producer will be blocked if the consumer consumes messages slowly. Hence there are two solutions to this situation:
    1. Increase the consumer's consuming speed, but it may not be possible in some cases. My simple solution is to disable the logging for the message in consumer implementation and this will highly improve the consuming speed and we can see from the test case that the slow consumer problem is solved.
    2. Slow down the producer's producing speed. In this project we throttle the producer by make the producer sleep for 5ms when it hits the threshold. The threshold is configurable and it is used to compare to how many messages that pending for consumer to consume.

* What if there are multiple consumers?
  * We need to take care of the threading issue if there are multiple consumers and using ThreadLocal variable helps out. Meanwhile with multiple consumers we can see the messages can be consumed more quickly. But there is a drawback I want to highlight - with multiple consumers, the message handling sequence might not be guaranteed.

### TODO
* For the interest of time I left a TODO keyword in the code for the follow-up of the residual messages issue.