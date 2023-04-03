# FoodDeliveryApplication
Implementation of a food delivery application using Kafka

Implementation a food delivery application. The application consists of a frontend that accepts requests from users and a backend that processes them. There are three types of users interacting with the service: (1) normal customers register, place orders, and check the status of orders; (2) admins can change the availability of items; (3) delivery men notify successful deliveries. The backend is decomposed in three services, following the microservices paradigm: (1) the users service manages personal data of registered users; (2) the orders service processes and validates orders; (3) the shipping service handles shipping of valid orders. Upon receiving a new order, the order service checks if all requested items are available and, if so, it sends the order to the shipping service. The shipping service checks the address of the user who placed the order and prepares the delivery. 

Assumptions and Guidelines 
- Services do not share state, but only communicate by exchanging messages/events over Kafka topics
- They adopt an event-driven architecture
- Services can crash at any time and lose their state
- Implementation of a fault recovery procedure to resume a valid state of the services
- Kafka topics assumed that cannot be lost
- TBD technology to implement the frontend (e.g., simple command-line application or basic REST/Web app)
