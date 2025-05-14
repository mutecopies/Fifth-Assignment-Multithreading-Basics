# Theoretical Questions 📝

## 1. `start()` vs `run()`

### Code:
```java
public class StartVsRun {    
    static class MyRunnable implements Runnable {    
        public void run() {    
            System.out.println("Running in: " + Thread.currentThread().getName()); 
        }    
    }    
    public static void main(String[] args) throws InterruptedException {    
        Thread t1 = new Thread(new MyRunnable(), "Thread-1");    
        System.out.println("Calling run()");    
        t1.run();    
        Thread.sleep(100);    

        Thread t2 = new Thread(new MyRunnable(), "Thread-2");    
        System.out.println("Calling start()");    
        t2.start();    
    }  
}
```

### Output:
```
Calling run()
Running in: main
Calling start()
Running in: Thread-2
```

### Explanation:
- `t1.run()` just calls the `run()` method like a regular method, so it runs on the **main thread**.
- `t2.start()` starts a **new thread**, so the `run()` method executes in `Thread-2`.

### Difference:
| Method      | Behavior                                              |
|-------------|-------------------------------------------------------|
| `run()`     | Executes on the current thread like a normal method   |
| `start()`   | Spawns a new thread and executes `run()` asynchronously |

---

## 2. Daemon Threads

### Code:
```java
public class DaemonExample {    
    static class DaemonRunnable implements Runnable {    
        public void run() {    
            for(int i = 0; i < 20; i++) {    
                System.out.println("Daemon thread running...");    
                try {    
                    Thread.sleep(500);    
                } catch (InterruptedException e) {    
                    // Handle Exception
                }            
            }    
        }    
    }    
    public static void main(String[] args) {    
        Thread thread = new Thread(new DaemonRunnable());    
        thread.setDaemon(true);    
        thread.start();    
        System.out.println("Main thread ends.");    
    }  
}
```

### Output:
```
Main thread ends.
Daemon thread running...
(Execution may stop immediately after or during a few daemon messages)
```

### Explanation:
- The daemon thread may or may not complete its loop depending on how quickly the main thread exits.
- **Daemon threads are terminated** automatically when all non-daemon threads (like `main`) finish.

### What if `setDaemon(true)` is removed?
- The thread becomes a **non-daemon thread**, so it will **continue running even after the main thread ends**, until its task finishes.

### Real-life use cases for daemon threads:
- Garbage collection (Java GC thread)
- Background tasks like:
  - Logging
  - Auto-saving
  - Monitoring or heartbeats

---

## 3. A shorter way to create threads

### Code:
```java
public class ThreadDemo {  
    public static void main(String[] args) {  
        Thread thread = new Thread(() -> {  
            System.out.println("Thread is running using a ...!");  
        });  

        thread.start();  
    }  
}
```

### Output:
```
Thread is running using a ...!
```

### Explanation:
- This thread prints a message and runs independently.

### What is `() -> { ... }`?
- This is a **lambda expression** in Java, introduced in Java 8.
- It is a shorthand for writing instances of interfaces with a single abstract method (like `Runnable`).

### How is this different?
| Approach                        | Characteristics |
|--------------------------------|-----------------|
| Extending `Thread`             | Requires a new class, less reusable |
| Implementing `Runnable`       | More flexible, allows multiple inheritance |
| Using lambda `() -> {}`       | Concise, readable, used for simple tasks |