# iFood Backend Test - Score

Our goal is to be the best food marketplace in the world.

In order to build trust from our million users (restaurants and consumers), we need to fully understand 
how relevant the menu item/categories sold by our partners are. 
So, we can give insights to our restaurants and better offers to our consumers. 

## Tasks

Your task is to develop one (or more, feel free) RESTful service(s) to:
* Provide Score for a given Menu Item
* Provide Score for a given Category
* Retrieve Menu Item with Score above/below a parameter
* Retrieve Categories with Score above/below a parameter
* Update score of Menu Item and Categories according to our business rules

We also would like you to check our module `score-order-generator` and give us a overview about our choices regarding:
* Design Pattens
* Data Structures
* Code organization

Fork this repository and submit your code.

## Business Rules

* An **Order** is composed by id, Restaurant id, Customer id, Address id, confirmation date and list of **Order Items**.
* An **Order Item** is composed by **Menu Item** id, **Category**, unit price and quantity.
* The **Menu Item** has a **Relevance** in the Order, calculated using a heuristic, as number between 0 and 100. The same goes for **Category Relevance**. And the formula is:
```
Relevance = SQRT(IQ*IP*10000)

Whereas:
IQ = (Menu Item Quantity in Order)/(Total Items Quantity in Order)
IP = (Total Menu Item Price in Order)/(Total Order Price)
```
* The **Menu Item Score** is calculated by the mean of the **Menu Item Relevance** on each Order it appears.  The same goes for **Category Score**.
* An Order can affect Menu Item/Category Score in three situations:
    * Checkout: Each Menu Item/Category Relevance will *be taken in account to* the corresponding Menu Item/Category Score.
    * Cancellation: Menu Item/Category Score should stop taking the Menu Item/Category Relevance into account.
    * Expiration: Menu Item/Category Score should stop taking the Menu Item Dish/Category Relevance into account (same effect as Cancellation).
* And Order expires in 1 month after its confirmation date.
* Only non-cancelled Orders expire.

## Examples

Order Example:
```javascript
{
"uuid":"c3850c73-f4ca-4974-871c-6f99c6167f1f",
"customerUuid":"10359eaa-2292-4217-a7fe-45172be9b498",
"restaurantUuid":"836dc1c1-aec7-4272-ab1e-ba01a9842ede",
"addressUuid":"789224b0-5cee-48b8-89ec-13001d955391",
"confirmedAt": null,
"orderItems":
 [
    {
    "menuUuid":"dad0f8ac-9433-40fd-bd43-9ec0c12d5213",
    "category":"PIZZA",
    "unitPrice":26,
    "quantity":1
    },
    {
    "menuUuid":"6208e2fd-45c3-4013-a69a-5f54cb249be0",
    "category":"VEGAN",
    "unitPrice":3,
    "quantity":3
    },
    {
    "menuUuid":"bd2746ce-a975-4bf4-84dc-fedd14273a03",
    "category":"PIZZA",
    "unitPrice":23,
    "quantity":1
    }
 ]
}
```

So, from our example, the Relevance for the Menu Item `6208e2fd-45c3-4013-a69a-5f54cb249be0` is:
```
IQ = 3/(3+1+1) = 0,6
IP = (3*3)/(1*26+1*23+3*3) = 0.155172414
Relevance = 30.512857683
```
Similarly, the Relevance for the Category `Pizza` in our order is:
```
IQ = (1+1)/(1+3+1) = 0,4
IP = (1*26+1*23)/(1*26+1*23+3*3) = 0.844827586
Relevance = 58.13183589
```

So if we only have the example order, the `Pizza` Score index is `58.13183589`. However, if we had a 
second order with `Pizza` relevance equals to `13.8` the Score would be `35.965917945` 
(=`mean(58.13183589, 13.8)`).

## Non functional requirements

Your service(s) must be resilient, fault tolerant, responsive. You should prepare it/them to be highly scalable as possible.
The score update rate should be closest possible to "real-time", balancing your choices in order to achieve the expected
scalability.

## Constraints

* The project starter modules are given
* Order Checkout is providad by `ifood.score.order.OrderCheckoutMock.checkoutFakeOrder`
* Order Cancellation is provided by `ifood.score.order.OrderCheckoutMock.cancelFakeOrder`
