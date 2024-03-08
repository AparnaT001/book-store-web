# Bookstore App

## **Overview**

The Bookstore App is a web application designed to manage and organize a bookstore's inventory, 
provide the overall bookstore experience. It  allows customers and administrators to browse books, make purchases, and manage the bookstore's catalog.

## **Features**

**User registration** : User can register himself with his mail id being the unique attribute.

**Authentication** : Restricted access to customer only to view books and checkout whereas admin can perform all operations in the store

**Book Store**: Browse a comprehensive catalog of books, including details such as title, author, category, price and quantity.

**Shopping Cart**: Add books to a shopping cart for checkout.

**Technologies Used :**

    Java
    Spring Boot
    PostgreSQL

**Getting Started**

    1.Clone the Repository
        git clone https://github.com/AparnaT001/book-store-web.git

    2.Add the database details in application-dev.properties

    3.Run the schema.sql script in local postgres database.

    4.Build the gradle project
        ./gradlew build

    5.Run the application
        ./gradlew bootRun

    6.Access the application in swagger via below url.
        http://localhost:7777/book-store/api/swagger-ui/index.html

**Accessing the bookstore**

    1.Create a user registration as admin to add books.

    2.Add book categories and books under each category.

    3.You can also remove books from store.

    4.Also add/remove items to cart and then checkout.

    5.Create another user as customer and check if he only has the privileges of listing books by category and cart.

    

    
