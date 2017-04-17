CREATE DATABASE  IF NOT EXISTS `clustering` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `clustering`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: clustering
-- ------------------------------------------------------
-- Server version	5.6.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bigramFrequencies`
--

DROP TABLE IF EXISTS `bigramFrequencies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bigramFrequencies` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `bigram` varchar(255) NOT NULL,
  `frequency` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `bigram_UNIQUE` (`bigram`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bigramFrequencies`
--

LOCK TABLES `bigramFrequencies` WRITE;
/*!40000 ALTER TABLE `bigramFrequencies` DISABLE KEYS */;
/*!40000 ALTER TABLE `bigramFrequencies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `removedmicroclusters`
--

DROP TABLE IF EXISTS `removedmicroclusters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `removedmicroclusters` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `isPotential` tinyint(1) unsigned DEFAULT NULL,
  `topWords` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numberOfDocuments` int(10) unsigned DEFAULT NULL,
  `creationTime` timestamp NULL DEFAULT NULL,
  `lastUpdateTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=118181 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `removedmicroclusters`
--

LOCK TABLES `removedmicroclusters` WRITE;
/*!40000 ALTER TABLE `removedmicroclusters` DISABLE KEYS */;
/*!40000 ALTER TABLE `removedmicroclusters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statistics`
--

DROP TABLE IF EXISTS `statistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `statistics` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `clusterId` int(10) unsigned NOT NULL,
  `numberOfDocuments` int(10) unsigned NOT NULL,
  `timestamp` timestamp NOT NULL,
  `absorbedClusters` varchar(255) DEFAULT NULL,
  `timeFactor` int(10) unsigned DEFAULT NULL,
  `totalProcessedPerTimeUnit` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=158329 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistics`
--

LOCK TABLES `statistics` WRITE;
/*!40000 ALTER TABLE `statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `termFrequencies`
--

DROP TABLE IF EXISTS `termFrequencies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `termFrequencies` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `term` varchar(255) DEFAULT NULL,
  `frequency` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `term_UNIQUE` (`term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `termFrequencies`
--

LOCK TABLES `termFrequencies` WRITE;
/*!40000 ALTER TABLE `termFrequencies` DISABLE KEYS */;
/*!40000 ALTER TABLE `termFrequencies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `topterms`
--

DROP TABLE IF EXISTS `topterms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `topterms` (
  `statisticId` int(10) unsigned NOT NULL,
  `term` varchar(255) NOT NULL,
  `numberOfOccurences` int(10) unsigned NOT NULL,
  KEY `fk_topterms_cluster-statistic_idx` (`statisticId`),
  CONSTRAINT `fk_topterms_statistics` FOREIGN KEY (`statisticId`) REFERENCES `statistics` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `topterms`
--

LOCK TABLES `topterms` WRITE;
/*!40000 ALTER TABLE `topterms` DISABLE KEYS */;
/*!40000 ALTER TABLE `topterms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tweetBigrams`
--

DROP TABLE IF EXISTS `tweetBigrams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tweetBigrams` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tweetId` varchar(50) NOT NULL,
  `bigram` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_tweet-bigrams_tweets_idx` (`tweetId`),
  KEY `fk_tweet-bigrams_bigrams_idx` (`bigram`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tweetBigrams`
--

LOCK TABLES `tweetBigrams` WRITE;
/*!40000 ALTER TABLE `tweetBigrams` DISABLE KEYS */;
/*!40000 ALTER TABLE `tweetBigrams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tweetTerms`
--

DROP TABLE IF EXISTS `tweetTerms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tweetTerms` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tweetId` varchar(50) NOT NULL,
  `term` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_tweet-terms_tweet_idx` (`tweetId`),
  KEY `fk_tweet-terms_term_idx` (`term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tweetTerms`
--

LOCK TABLES `tweetTerms` WRITE;
/*!40000 ALTER TABLE `tweetTerms` DISABLE KEYS */;
/*!40000 ALTER TABLE `tweetTerms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tweets`
--

DROP TABLE IF EXISTS `tweets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tweets` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tweetId` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tweetText` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `creationTime` timestamp NULL DEFAULT NULL,
  `clusteredTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tweetId_UNIQUE` (`tweetId`)
) ENGINE=InnoDB AUTO_INCREMENT=29393 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tweets`
--

LOCK TABLES `tweets` WRITE;
/*!40000 ALTER TABLE `tweets` DISABLE KEYS */;
/*!40000 ALTER TABLE `tweets` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-04-17 10:45:12
