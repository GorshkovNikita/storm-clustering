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
-- Table structure for table `allTerms`
--

DROP TABLE IF EXISTS `allTerms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `allTerms` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `term` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `frequency` int(10) DEFAULT NULL,
  `clusterName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `allTerms`
--

LOCK TABLES `allTerms` WRITE;
/*!40000 ALTER TABLE `allTerms` DISABLE KEYS */;
/*!40000 ALTER TABLE `allTerms` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Table structure for table `macroClusteringTasks`
--

DROP TABLE IF EXISTS `macroClusteringTasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `macroClusteringTasks` (
  `taskId` int(11) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `numberOfTuples` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `macroClusteringTasks`
--

LOCK TABLES `macroClusteringTasks` WRITE;
/*!40000 ALTER TABLE `macroClusteringTasks` DISABLE KEYS */;
/*!40000 ALTER TABLE `macroClusteringTasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manualclustering`
--

DROP TABLE IF EXISTS `manualclustering`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `manualclustering` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `clusterId` int(10) unsigned DEFAULT NULL,
  `tweetId` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cluster_clustering_idx` (`clusterId`),
  CONSTRAINT `fk_cluster_clustering` FOREIGN KEY (`clusterId`) REFERENCES `manualclusters` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=159681 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manualclustering`
--

LOCK TABLES `manualclustering` WRITE;
/*!40000 ALTER TABLE `manualclustering` DISABLE KEYS */;
/*!40000 ALTER TABLE `manualclustering` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manualclusters`
--

DROP TABLE IF EXISTS `manualclusters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `manualclusters` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manualclusters`
--

LOCK TABLES `manualclusters` WRITE;
/*!40000 ALTER TABLE `manualclusters` DISABLE KEYS */;
INSERT INTO `manualclusters` VALUES (29,'123'),(30,'sada');
/*!40000 ALTER TABLE `manualclusters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pending`
--

DROP TABLE IF EXISTS `pending`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pending` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pendingMessages` bigint(10) unsigned DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `spout` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=454 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pending`
--

LOCK TABLES `pending` WRITE;
/*!40000 ALTER TABLE `pending` DISABLE KEYS */;
/*!40000 ALTER TABLE `pending` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `removedmicroclusters`
--

DROP TABLE IF EXISTS `removedmicroclusters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `removedmicroclusters` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `clusterId` int(10) DEFAULT NULL,
  `isPotential` tinyint(1) unsigned DEFAULT NULL,
  `topWords` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numberOfDocuments` int(10) unsigned DEFAULT NULL,
  `creationTime` timestamp NULL DEFAULT NULL,
  `lastUpdateTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=152 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
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
  `mostRelevantTweetId` varchar(255) DEFAULT NULL,
  `totalProcessedTweets` int(10) DEFAULT NULL,
  `rate` double(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3210 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistics`
--

LOCK TABLES `statistics` WRITE;
/*!40000 ALTER TABLE `statistics` DISABLE KEYS */;
/*!40000 ALTER TABLE `statistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `summary`
--

DROP TABLE IF EXISTS `summary`;
/*!50001 DROP VIEW IF EXISTS `summary`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `summary` (
  `id` tinyint NOT NULL,
  `clusterId` tinyint NOT NULL,
  `numberOfDocuments` tinyint NOT NULL,
  `timestamp` tinyint NOT NULL,
  `absorbedClusters` tinyint NOT NULL,
  `timeFactor` tinyint NOT NULL,
  `totalProcessedPerTimeUnit` tinyint NOT NULL,
  `mostRelevantTweetId` tinyint NOT NULL,
  `totalProcessedTweets` tinyint NOT NULL,
  `rate` tinyint NOT NULL,
  `statisticId` tinyint NOT NULL,
  `term` tinyint NOT NULL,
  `numberOfOccurrences` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

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
  `numberOfOccurrences` int(10) unsigned NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tweets`
--

LOCK TABLES `tweets` WRITE;
/*!40000 ALTER TABLE `tweets` DISABLE KEYS */;
/*!40000 ALTER TABLE `tweets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `summary`
--

/*!50001 DROP TABLE IF EXISTS `summary`*/;
/*!50001 DROP VIEW IF EXISTS `summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `summary` AS (select `stat`.`id` AS `id`,`stat`.`clusterId` AS `clusterId`,`stat`.`numberOfDocuments` AS `numberOfDocuments`,`stat`.`timestamp` AS `timestamp`,`stat`.`absorbedClusters` AS `absorbedClusters`,`stat`.`timeFactor` AS `timeFactor`,`stat`.`totalProcessedPerTimeUnit` AS `totalProcessedPerTimeUnit`,`stat`.`mostRelevantTweetId` AS `mostRelevantTweetId`,`stat`.`totalProcessedTweets` AS `totalProcessedTweets`,`stat`.`rate` AS `rate`,`term`.`statisticId` AS `statisticId`,`term`.`term` AS `term`,`term`.`numberOfOccurrences` AS `numberOfOccurrences` from (`statistics` `stat` join `topterms` `term` on((`stat`.`id` = `term`.`statisticId`))) order by `stat`.`timestamp` desc,`stat`.`numberOfDocuments` desc,`term`.`numberOfOccurrences` desc) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-05-10 12:16:01
