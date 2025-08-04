-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: agriwasteecommerceplatform
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `buyercropjunction`
--

DROP TABLE IF EXISTS `buyercropjunction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `buyercropjunction` (
  `buyerid` varchar(10) NOT NULL,
  `cropid` varchar(10) NOT NULL,
  PRIMARY KEY (`buyerid`,`cropid`),
  CONSTRAINT `buyercropjunction_buyerdetails_fk` FOREIGN KEY (`buyerid`) REFERENCES `buyerdetails` (`buyerid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buyercropjunction`
--

LOCK TABLES `buyercropjunction` WRITE;
/*!40000 ALTER TABLE `buyercropjunction` DISABLE KEYS */;
INSERT INTO `buyercropjunction` VALUES ('buyer1','ricehusk'),('buyer1','ricestraw'),('buyer1','wheatstraw');
/*!40000 ALTER TABLE `buyercropjunction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `buyerdetails`
--

DROP TABLE IF EXISTS `buyerdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `buyerdetails` (
  `buyerid` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `industryname` varchar(30) NOT NULL,
  `industryid` varchar(15) NOT NULL,
  `contactno` decimal(10,0) NOT NULL,
  `emailid` varchar(40) NOT NULL,
  `industryaddress` text NOT NULL,
  `industrytype` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`buyerid`),
  UNIQUE KEY `buyerdetails_unique` (`contactno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buyerdetails`
--

LOCK TABLES `buyerdetails` WRITE;
/*!40000 ALTER TABLE `buyerdetails` DISABLE KEYS */;
INSERT INTO `buyerdetails` VALUES ('buyer1','Tata Industries','tataandson@1922',456135,'tatagroup@gmail.com','Andhra Pradesh, India','biofuel');
/*!40000 ALTER TABLE `buyerdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `farmercropjunction`
--

DROP TABLE IF EXISTS `farmercropjunction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `farmercropjunction` (
  `cropid` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `farmerid` varchar(10) NOT NULL,
  `noofunitsavailable` int NOT NULL,
  `priceperunit` int NOT NULL,
  `totalworth` int GENERATED ALWAYS AS ((`priceperunit` * `noofunitsavailable`)) STORED,
  PRIMARY KEY (`cropid`,`farmerid`),
  KEY `farmercropjunction_farmerdetails_fk` (`farmerid`),
  CONSTRAINT `farmercropjunction_farmerdetails_fk` FOREIGN KEY (`farmerid`) REFERENCES `farmerdetails` (`farmerid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farmercropjunction`
--

LOCK TABLES `farmercropjunction` WRITE;
/*!40000 ALTER TABLE `farmercropjunction` DISABLE KEYS */;
INSERT INTO `farmercropjunction` (`cropid`, `farmerid`, `noofunitsavailable`, `priceperunit`) VALUES ('coffeehusk','ramu20',10,5),('ricehusk','ramu20',20,6),('ricehusk','suresh',20,10),('ricestraw','ramu20',20,10);
/*!40000 ALTER TABLE `farmercropjunction` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`jayadithyapraneeth`@`localhost`*/ /*!50003 TRIGGER `addremovecrops` AFTER INSERT ON `farmercropjunction` FOR EACH ROW begin
	update inventorydetails set unitsavailable = unitsavailable + new.noofunitsavailable where inventorydetails.cropid = new.cropid;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `farmerdetails`
--

DROP TABLE IF EXISTS `farmerdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `farmerdetails` (
  `farmerid` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `fullname` varchar(100) NOT NULL,
  `farmeraddress` text NOT NULL,
  `phoneno` decimal(10,0) NOT NULL,
  PRIMARY KEY (`farmerid`),
  UNIQUE KEY `phoneno` (`phoneno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `farmerdetails`
--

LOCK TABLES `farmerdetails` WRITE;
/*!40000 ALTER TABLE `farmerdetails` DISABLE KEYS */;
INSERT INTO `farmerdetails` VALUES ('ramu20','P Rama Krishna','D.No.:2-36/1, kalava road, Bhimavaram',8965316428),('suresh','N Suresh Babu','Hyderabad',9586743132);
/*!40000 ALTER TABLE `farmerdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventorydetails`
--

DROP TABLE IF EXISTS `inventorydetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventorydetails` (
  `cropid` varchar(30) NOT NULL,
  `categoryid` varchar(30) DEFAULT NULL,
  `unitsavailable` int NOT NULL,
  `unittype` varchar(20) NOT NULL,
  PRIMARY KEY (`cropid`),
  CONSTRAINT `inventorydetails_chk_1` CHECK ((`unitsavailable` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventorydetails`
--

LOCK TABLES `inventorydetails` WRITE;
/*!40000 ALTER TABLE `inventorydetails` DISABLE KEYS */;
INSERT INTO `inventorydetails` VALUES ('applepeel','fruitcrop',0,'kg'),('applepomace','fruitcrop',0,'kg'),('bananaleaf','fruitcrop',0,'kg'),('bananapeel','fruitcrop',0,'kg'),('bananapseudostem','fruitcrop',0,'kg'),('chickpeapodhusk','pulsewaste',0,'kg'),('chickpeastalk','pulsewaste',0,'kg'),('citruspeel','fruitcrop',0,'kg'),('citruspulp','fruitcrop',0,'kg'),('citrusseed','fruitcrop',0,'kg'),('coffeehusk','spices',10,'kg'),('coffeepulp','spices',0,'kg'),('coffeesilverskin','spices',0,'kg'),('corncob','cerealcrop',0,'kg'),('cornhusk','cerealcrop',0,'kg'),('cornstover','cerealcrop',0,'kg'),('cottonseedhull','fibercrop',0,'kg'),('cottonstalk','fibercrop',0,'kg'),('groundnuthaulms','oilseedcrop',0,'kg'),('groundnutshells','oilseedcrop',0,'kg'),('mangopeel','fruitcrop',0,'kg'),('mangoseed','fruitcrop',0,'kg'),('mustardseedcake','oilseedcrop',0,'kg'),('mustardstalk','oilseedcrop',0,'kg'),('onionouterskin','vegetablecrop',0,'kg'),('onionpeel','vegetablecrop',0,'kg'),('pigeonpeapodshells','pulsewaste',0,'kg'),('potatopeel','vegetablecrop',0,'kg'),('ricehusk','cerealcrop',40,'kg'),('ricestraw','cerealcrop',20,'kg'),('rottentomato','vegetablecrop',0,'kg'),('soybeancake','oilseedcrop',0,'kg'),('soybeanhulls','oilseedcrop',0,'kg'),('soybeanstraw','oilseedcrop',0,'kg'),('sugarcanebagasse','sugarcrop',0,'kg'),('sugarcaneleaves','sugarcrop',0,'kg'),('sugarcanetops','sugarcrop',0,'kg'),('sugarcanetrash','sugarcrop',0,'kg'),('teadust','spices',0,'kg'),('teapruning','spices',0,'kg'),('teastalk','spices',0,'kg'),('tomatoseed','vegetablecrop',0,'kg'),('tomatoskin','vegetablecrop',0,'kg'),('turmericleaves','spices',0,'kg'),('turmericrhizomepeel','spices',0,'kg'),('wheathusk','cerealcrop',0,'kg'),('wheatstraw','cerealcrop',0,'kg');
/*!40000 ALTER TABLE `inventorydetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logincredentials`
--

DROP TABLE IF EXISTS `logincredentials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logincredentials` (
  `userid` varchar(15) NOT NULL,
  `password` varchar(10) NOT NULL,
  `role` enum('seller','buyer') NOT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='both farmers asd buyers exist but differ with the column "role"';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logincredentials`
--

LOCK TABLES `logincredentials` WRITE;
/*!40000 ALTER TABLE `logincredentials` DISABLE KEYS */;
INSERT INTO `logincredentials` VALUES ('buyer1','none','buyer'),('ramu20','none','seller'),('suresh','none','seller');
/*!40000 ALTER TABLE `logincredentials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderhistory`
--

DROP TABLE IF EXISTS `orderhistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderhistory` (
  `orderid` varchar(10) NOT NULL,
  `sellerid` varchar(10) NOT NULL,
  `buyerid` varchar(10) NOT NULL,
  `cropid` varchar(10) NOT NULL,
  `noofunits` int NOT NULL,
  `fromaddress` text NOT NULL,
  `toaddress` text NOT NULL,
  `status` enum('accepted','rejected','placed','cancelled') DEFAULT NULL,
  PRIMARY KEY (`orderid`),
  KEY `orderhistory_inventorydetails_fk` (`cropid`),
  KEY `orderhistory_buyerdetails_fk` (`buyerid`),
  KEY `orderhistory_farmerdetails_fk` (`sellerid`),
  CONSTRAINT `orderhistory_buyerdetails_fk` FOREIGN KEY (`buyerid`) REFERENCES `buyerdetails` (`buyerid`),
  CONSTRAINT `orderhistory_farmerdetails_fk` FOREIGN KEY (`sellerid`) REFERENCES `farmerdetails` (`farmerid`),
  CONSTRAINT `orderhistory_inventorydetails_fk` FOREIGN KEY (`cropid`) REFERENCES `inventorydetails` (`cropid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderhistory`
--

LOCK TABLES `orderhistory` WRITE;
/*!40000 ALTER TABLE `orderhistory` DISABLE KEYS */;
INSERT INTO `orderhistory` VALUES ('cereal11','ramu20','buyer1','ricehusk',10,'D.No.:2-36/1, kalava road, Bhimavaram','India, Andhra Pradesh','cancelled');
/*!40000 ALTER TABLE `orderhistory` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`jayadithyapraneeth`@`localhost`*/ /*!50003 TRIGGER `updateinventoryunits1` AFTER INSERT ON `orderhistory` FOR EACH ROW begin
	if new.status = 'accepted' then
	update agriwasteecommerceplatform.inventorydetails set agriwasteecommerceplatform.inventorydetails.unitsavailable = agriwasteecommerceplatform.inventorydetails.unitsavailable - new.noofunits where agriwasteecommerceplatform.inventorydetails.cropid = new.cropid;
    update agriwasteecommerceplatform.farmercropjunction set agriwasteecommerceplatform.farmercropjunction.noofunitsavailable = agriwasteecommerceplatform.farmercropjunction.noofunitsavailable - new.noofunits where agriwasteecommerceplatform.farmercropjunction.cropid = new.cropid and agriwasteecommerceplatform.farmercropjunction.farmerid = new.sellerid;
    end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`jayadithyapraneeth`@`localhost`*/ /*!50003 TRIGGER `updateinventoryunits2` AFTER UPDATE ON `orderhistory` FOR EACH ROW begin
	if new.status = 'cancelled' then
	update agriwasteecommerceplatform.inventorydetails set agriwasteecommerceplatform.inventorydetails.unitsavailable = agriwasteecommerceplatform.inventorydetails.unitsavailable + old.noofunits where agriwasteecommerceplatform.inventorydetails.cropid = new.cropid;
    update agriwasteecommerceplatform.farmercropjunction set agriwasteecommerceplatform.farmercropjunction.noofunitsavailable = agriwasteecommerceplatform.farmercropjunction.noofunitsavailable + old.noofunits where agriwasteecommerceplatform.farmercropjunction.cropid = new.cropid and agriwasteecommerceplatform.farmercropjunction.farmerid = new.sellerid;
    end if;
end */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping routines for database 'agriwasteecommerceplatform'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-04 15:57:56
