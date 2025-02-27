# ntfy-java-client

🚀 **ntfy-java-client** is a lightweight Java library for interacting with [ntfy.sh](https://ntfy.sh).  
It provides a simple API for **sending notifications** and **subscribing to topics**.

## Features
- ✅ **Send Notifications** to ntfy.sh with custom messages and parameters.
- 🔄 **Subscribe to Topics** and listen for real-time notifications.
- 🏗️ **Compatible with Java 8+**.

## Installation

Add the following dependency to your **Maven** project:

```xml
<dependency>
    <groupId>com.github.matheusverissimo</groupId>
    <artifactId>ntfy-java-client</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Usage

### Sending a notification

```java
Topic topic = new Topic("ntfy-java-client");

NotificationRequest request = new NotificationRequest()
    .title("My first notification!")
    .message("Hello, world!")
    .addAction(new ViewAction()
        .label("Ntfy")
		.url("https://ntfy.sh"));

//Synchronously
topic.notify(request);

//Asynchronously
topic.notifyAsync(request, notificationResponse -> {
    log.info("Notification id: {}", notificationResponse.getId());
});
```

Use method overloading for connect to a self-hosted nfty.sh server.
```java
Topic topic = new Topic("https://my-ntfysh.com/", "ntfy-java-client");
```

### Listening to a topic

```java
Topic topic = new Topic("ntfy-java-client");

topic.subscribe(notification -> {
    log.info("Message received: {}", notification.getMessage());
});

topic.listen();
```

## Contributing

Contributions are welcome!
Feel free to open an issue or submit a pull request.

## License
This project is licensed under the MIT License.