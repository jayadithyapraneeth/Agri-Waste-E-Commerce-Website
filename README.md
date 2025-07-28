# 🌾 Agri-Waste E-Commerce Website

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/your-username/agri-waste-ecommerce/blob/main/LICENSE)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=java&logoColor=white)](https://www.java.com/)
[![MySQL](https://img.shields.io/badge/MySQL-005C84?style=flat&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat&logo=javascript&logoColor=black)](https://developer.mozilla.org/en-US/docs/Web/JavaScript)

## 📋 Project Overview

The **Agri-Waste E-Commerce Website** is an innovative online marketplace dedicated exclusively to trading agricultural crop waste between farmers and industrial buyers such as biofuel and compost producers. The platform supports easy listing, categorization, secure payments, and direct communication between users. By enabling efficient, transparent, and sustainable trade of crop waste, the project aims to create new income streams for farmers and support a circular economy in agriculture.

Notably, the platform increases the trading capacity of Agri-waste in India, making it easier for industries to source required biomass without additional effort or intervention. This enhanced, market-driven approach to waste management contributes to reducing pollution trends, as mandated by circular economy principles, by ensuring agricultural residues are traded and reused rather than openly burned or discarded.

## ✨ Key Features

### 🌱 For Sellers (Farmers)
- **Automatic Product Categorization**: Smart categorization system after product upload
- **Crop Waste Preservation Guidance**: Expert tips for maintaining waste quality
- **Price Freedom**: Complete control over pricing strategy
- **Platform-Managed Logistics**: Hassle-free order fulfillment and delivery
- **Customer Care Support**: Assisted product uploads through customer service
- **Rating & Review System**: Build reputation through buyer feedback

### 🏭 For Buyers (Industries)
- **Smart Product Search**: User-friendly search with advanced categorization
- **Advanced Filtering**: Filter by location, waste type, quantity, and price
- **Location-Based Discovery**: Find products within specific radius
- **Seamless Order Tracking**: Real-time transparency throughout the process
- **Flexible Quantity Orders**: Purchase exactly what you need
- **Intelligent Recommendations**: AI-driven suggestions based on preferences and seller ratings

### 🌍 Platform Features
- **Dedicated Crop Waste Trading**: Specialized platform for agricultural waste only
- **Cross-Platform Compatibility**: Optimized for both mobile and desktop
- **User-Friendly Interface**: Intuitive login, signup, and homepage design
- **Real-Time Messaging**: Direct communication between buyers and sellers
- **Secure Payment Gateway**: Safe and reliable transaction processing

## 🛠️ Technology Stack

### Backend
- **Java**: Core backend development with Servlets
- **Apache Tomcat**: Web server and servlet container
- **MySQL**: Primary database for data storage
- **JDBC**: Database connectivity
- **WebSocket API (JSR-356)**: Real-time messaging
- **Maven**: Dependency management

### Frontend
- **HTML5/CSS3**: Core web technologies
- **JavaScript**: Dynamic user interactions
- **Bootstrap**: Responsive design framework
- **AJAX**: Asynchronous server communication

### External Services
- **Payment Gateway**: Razorpay/PayPal integration
- **SMS API**: Notification services
- **Email Services**: User communications

## 🚀 Quick Start

### Prerequisites
- Java 8 or higher
- Apache Tomcat 9.0+
- MySQL 8.0+
- Maven 3.6+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/agri-waste-ecommerce.git
   cd agri-waste-ecommerce
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE agri_waste_db;
   USE agri_waste_db;
   SOURCE database/schema.sql;
   ```

3. **Configure Database Connection**
   ```bash
   # Update src/main/resources/db.properties
   db.url=jdbc:mysql://localhost:3306/agri_waste_db
   db.username=your_username
   db.password=your_password
   ```

4. **Build the Project**
   ```bash
   mvn clean compile
   mvn package
   ```

5. **Deploy to Tomcat**
   ```bash
   cp target/agri-waste-ecommerce.war $TOMCAT_HOME/webapps/
   $TOMCAT_HOME/bin/startup.sh
   ```

6. **Access the Application**
   ```
   http://localhost:8080/agri-waste-ecommerce
   ```

## 📁 Project Structure

```
agri-waste-ecommerce/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   └── agriswaste/
│   │   │   │       ├── servlets/
│   │   │   │       │   ├── UserServlet.java
│   │   │   │       │   ├── ProductServlet.java
│   │   │   │       │   └── OrderServlet.java
│   │   │   │       ├── models/
│   │   │   │       │   ├── User.java
│   │   │   │       │   ├── Product.java
│   │   │   │       │   └── Order.java
│   │   │   │       ├── dao/
│   │   │   │       │   ├── UserDAO.java
│   │   │   │       │   ├── ProductDAO.java
│   │   │   │       │   └── OrderDAO.java
│   │   │   │       ├── utils/
│   │   │   │       │   ├── DatabaseUtil.java
│   │   │   │       │   └── PaymentUtil.java
│   │   │   │       └── websocket/
│   │   │   │           └── ChatEndpoint.java
│   │   │   └── resources/
│   │   │       ├── db.properties
│   │   │       └── config.properties
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml
│   │       │   └── lib/
│   │       ├── css/
│   │       │   ├── style.css
│   │       │   └── responsive.css
│   │       ├── js/
│   │       │   ├── main.js
│   │       │   ├── chat.js
│   │       │   └── payment.js
│   │       ├── jsp/
│   │       │   ├── index.jsp
│   │       │   ├── login.jsp
│   │       │   ├── dashboard.jsp
│   │       │   └── product-listing.jsp
│   │       └── images/
├── database/
│   ├── schema.sql
│   └── sample-data.sql
├── docs/
│   ├── API-Documentation.md
│   └── User-Guide.md
├── pom.xml
└── README.md
```

## 🔌 API Endpoints

### User Management
- `POST /api/users/register` - User registration
- `POST /api/users/login` - User authentication
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update user profile

### Product Management
- `GET /api/products` - Get all products with filters
- `POST /api/products` - Create new product listing
- `GET /api/products/{id}` - Get specific product details
- `PUT /api/products/{id}` - Update product information
- `DELETE /api/products/{id}` - Remove product listing

### Order Management
- `POST /api/orders` - Create new order
- `GET /api/orders` - Get user orders
- `GET /api/orders/{id}` - Get specific order details
- `PUT /api/orders/{id}/status` - Update order status

### Payment
- `POST /api/payments/create` - Initiate payment
- `POST /api/payments/webhook` - Payment confirmation webhook

## 👥 Team

This project is developed by a dedicated team of 6 members:

- **Team Lead**: Project coordination and repository management
- **Frontend Developers (4)**: User interface, responsive design, JavaScript implementation
- **Backend Developer (1)**: Java servlets, business logic, API development
- **Database Expert (1)**: MySQL design, optimization, data management

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding conventions
- Write meaningful commit messages
- Test your changes before submitting
- Update documentation when necessary

## 🔒 Security

- All payment transactions are processed through secure gateways
- User passwords are hashed using bcrypt
- HTTPS encryption for all sensitive data
- Input validation and sanitization
- SQL injection prevention through prepared statements

## 🌍 Environmental Impact

This platform contributes to environmental sustainability by:
- **Reducing Agricultural Waste Burning**: Prevents open burning of crop residues
- **Promoting Circular Economy**: Turns waste into valuable resources
- **Supporting Renewable Energy**: Facilitates biofuel production
- **Reducing Landfill Waste**: Diverts agricultural waste from disposal sites

## 📊 Crop Waste Categories

The platform supports trading of various agricultural waste types:

- **Cereal Waste**: Rice straw, wheat straw, corn cobs
- **Pulse Waste**: Chickpea husks, pigeon pea stalks
- **Oilseed Waste**: Groundnut shells, mustard stalks
- **Sugar Crop Waste**: Sugarcane bagasse, trash
- **Fruit Waste**: Banana pseudostem, mango stones
- **Vegetable Waste**: Potato peelings, tomato residue

## 📈 Future Enhancements

- Mobile application development
- AI-powered price prediction
- Blockchain-based traceability
- Multi-language support
- Advanced analytics dashboard
- IoT integration for quality monitoring

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact

For questions, suggestions, or support, please reach out to:
- **Email**: team@agri-waste-ecommerce.com
- **GitHub Issues**: [Create an issue](https://github.com/your-username/agri-waste-ecommerce/issues)

## 🙏 Acknowledgments

- Thanks to all farmers and industry partners who provided valuable insights
- Special recognition to our academic advisor for project guidance
- Appreciation for open-source communities that made this project possible

---

**Made with ❤️ for sustainable agriculture and circular economy in India**