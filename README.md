**Sunbase Coding Assignment**
-----------------------------
**What does the app do?**
- It's a simple customer management app. It fetches customer details from third party API, saves in local DB and presents the data on the client side.
- It generates an authorization token in order to fetch the data from the remote API. Authorization token is generated by the customer data provider (sunbase.com in this case).
- It provides basic functionalities to the user. Such as adding new customer to DB, updating and deleting a customer and searching on various parameters.

**How to Run the App**
- Once you have pulled the code, there are basically three things you need to do in order to run the app seamlessly.
- 1st, app uses MySQL databse so create a database named "sunbasedata" in your MySQL database.
- 2nd, configure the database settings in Application.properties file. Just replace the datasource username and password with your MySQL username and password.
- 3rd, Keep your port running on 8080.

You are good to run the app.