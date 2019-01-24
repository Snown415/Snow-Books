# Snow-Books
A JavaFX client built to utilize a MVC (Model View Controller) architecture.

Snow Books is a mock business application for personal use. It started out as a very simple chart and table of information to keep track of my person finances as a sole proprietor. I then built a structured JavaFX application and merged the two into one application. Once the merge was complete, I built out a server to handle all of the data using serialization and at one point, MySQL. I removed the SQL to work more dynamically, serialization allows me to work from two different machines where as SQL forced me to run the server on my desktop where the databases were stored. 

###### Note: This application connections to a server, cloning the repository and trying to run the application won't work as expected. If you're an employer, recruiter, hiring manager, ect please contact me at Snown415@icloud.com.

### Key Features:
- Transactions: The ability to create, remove, and edit transactions with different types such as income types (services, contract, ect..) and expenses like business expenses and personal expenses. The plan is to expand transactions so users can easily add their own transaction types.

- Budgets: Budgets are used to build toward company goals. One will define a budget with a name, a target amount, and a description if applicable. Transactions can then be made and by choosing a budget and a saving percentage one can start to achieve their goals and watch their progress.

- Custom Architecture: The client has a custom architecture which allows the scene to be changed using only 1 line of code. Multiple controllers can be utilized at the same time and multiple of the same controller can be used at the same time as well. This leads to concise code and clean controllers.

- Visuals: Transactions and budgets both use visuals such as charts to show the user valuable information.

- Preferences: Stored locally is a preference file used when the client is ran. Users can opt to save their username and/or automatically login when the client is opened. This is planned to be expanded so multiple users can use the same machine but have seperate accounts.

- Security: Once an account is created the password used is encrypted using "AES" encryption. A security key and a vector key are generated randomly for every user and remain the same for every user once created. These keys are then used to check if the password offered upon login is correct and the original password isn't stored anywhere or viewed in anyway. The server contains a method to decrypt passwords but was only used for testing purposes and is now commented out and unused.

- Packets: The client and server work together using an object stream. This makes it very fast and efficient to send packets to and from the client. Each packet type is a subclass of packet allowing the packets to be handled seperately.

### My Experience:
When creating projects, I always have a vast imagination about where I see my application in the future. When I first started to build Snow Books it was the complete opposite. I was working with my first client at the time I was just trying to produce something that was usable. 

Here is a look at what v1 of Snow Books looked like...

![alt text](https://i.gyazo.com/15aa84024da2cc33de8af09b5d502702.png)

This ugly, simple application was such an inspiration. My first job was doing something I love and although my free-lancing experience was short it tought me how to follow through and code to the best of my abiltities. I only had 2 clients but I can safely say that they were happy with what I provided. Free-lancing was worth the money but I knew it wasn't what I wanted. I aspired to be a part of a team and work with others to accomplish things that would take me a long time by myself; hence, my portfolio exists. Snow Books v2 came to life on Decemeber 19, 2018 and one month and a few days later it is fully functioning. I have a published app on Google Play, yet this application is the one I am most proud of. It speaks a thousand words about who I am and have far I have come. A young man with a dream to do amazing things using software, my journey has just began.
