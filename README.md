# deal-stream-services
Deal stream services offers shoppers real-time price for an item. If price updates happen for an item, a shopper is always showed the best price available for the item.

It is a spring boot application that offers two APIs
    - The search endpoint is responsible for determining the best price of an item at the time it is called.
    - The update endpoint is called any time a new price is made available from one of the app's known retailers.

Once the endpoints are all hooked, it may be run by running the main method in DealStreamApp.

Technologies used
- Java
- Maven - to build the project and manage external Java libraries (the dependencies are all defined in pom.xml)
- Spring boot - Open source Java based framework popularly used for creating micro services.
