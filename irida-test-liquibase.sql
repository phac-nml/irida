-- MySQL dump 10.15  Distrib 10.0.24-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: irida_test
-- ------------------------------------------------------
-- Server version	10.0.24-MariaDB-7

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `DATABASECHANGELOG`
--

DROP TABLE IF EXISTS `DATABASECHANGELOG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DATABASECHANGELOG` (
  `ID` varchar(255) NOT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `FILENAME` varchar(255) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DATABASECHANGELOGLOCK`
--

DROP TABLE IF EXISTS `DATABASECHANGELOGLOCK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DATABASECHANGELOGLOCK` (
  `ID` int(11) NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Revisions`
--

DROP TABLE IF EXISTS `Revisions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Revisions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis`
--

DROP TABLE IF EXISTS `analysis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `description` longtext,
  `executionManagerAnalysisId` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_assemblyannotation`
--

DROP TABLE IF EXISTS `analysis_assemblyannotation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_assemblyannotation` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_ANALYSIS_ASSEMBLYANNOTATION_ID` FOREIGN KEY (`id`) REFERENCES `analysis` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_assemblyannotation_collection`
--

DROP TABLE IF EXISTS `analysis_assemblyannotation_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_assemblyannotation_collection` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_ANALYSIS_ASSEMBLYANNOTATIONCOLLECTION_ID` FOREIGN KEY (`id`) REFERENCES `analysis` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_fastqc`
--

DROP TABLE IF EXISTS `analysis_fastqc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_fastqc` (
  `id` bigint(20) NOT NULL,
  `duplicationLevelChart` longblob NOT NULL,
  `encoding` varchar(255) NOT NULL,
  `fileType` varchar(255) NOT NULL,
  `filteredSequences` int(11) NOT NULL,
  `gcContent` smallint(6) NOT NULL,
  `maxLength` int(11) NOT NULL,
  `minLength` int(11) NOT NULL,
  `perBaseQualityScoreChart` longblob NOT NULL,
  `perSequenceQualityScoreChart` longblob NOT NULL,
  `totalBases` bigint(20) NOT NULL,
  `totalSequences` int(11) NOT NULL,
  `fastQCReport_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ANALYSIS_FASTQC_REPORT` (`fastQCReport_id`),
  CONSTRAINT `FK_ANALYSIS_FASTQC_REPORT` FOREIGN KEY (`fastQCReport_id`) REFERENCES `analysis_output_file` (`id`),
  CONSTRAINT `FK_ANALYSIS_PARENT` FOREIGN KEY (`id`) REFERENCES `analysis` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_fastqc_overrepresented_sequence`
--

DROP TABLE IF EXISTS `analysis_fastqc_overrepresented_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_fastqc_overrepresented_sequence` (
  `analysis_fastqc_id` bigint(20) NOT NULL,
  `overrepresentedSequences_id` bigint(20) NOT NULL,
  PRIMARY KEY (`analysis_fastqc_id`,`overrepresentedSequences_id`),
  UNIQUE KEY `UK_FASTQC_OVERREPRESENTED_OVERREPRESENTED` (`overrepresentedSequences_id`),
  CONSTRAINT `FK_FASTQC_OVERREPRESENTED_FASTQC` FOREIGN KEY (`analysis_fastqc_id`) REFERENCES `analysis_fastqc` (`id`),
  CONSTRAINT `FK_FASTQC_OVERREPRESENTED_OVERREPRESENTED` FOREIGN KEY (`overrepresentedSequences_id`) REFERENCES `overrepresented_sequence` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_output_file`
--

DROP TABLE IF EXISTS `analysis_output_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_output_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `execution_manager_file_id` varchar(255) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `tool_execution_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ANALYSIS_OUTPUT_FILE_FILE_PATH` (`file_path`),
  UNIQUE KEY `tool_execution_id` (`tool_execution_id`),
  CONSTRAINT `FK_OUTPUT_FILE_TOOL_EXECUTION` FOREIGN KEY (`tool_execution_id`) REFERENCES `tool_execution` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_output_file_map`
--

DROP TABLE IF EXISTS `analysis_output_file_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_output_file_map` (
  `analysis_id` bigint(20) NOT NULL,
  `analysisOutputFilesMap_id` bigint(20) NOT NULL,
  `analysis_output_file_key` varchar(255) NOT NULL,
  PRIMARY KEY (`analysis_id`,`analysis_output_file_key`),
  UNIQUE KEY `analysisOutputFilesMap_id` (`analysisOutputFilesMap_id`),
  CONSTRAINT `FK_ANALYSIS_OUTPUT_FILE_MAP_ANALYSISOUTPUTFILESMAP_ID` FOREIGN KEY (`analysisOutputFilesMap_id`) REFERENCES `analysis_output_file` (`id`),
  CONSTRAINT `FK_ANALYSIS_OUTPUT_FILE_MAP_ANALYSIS_ID` FOREIGN KEY (`analysis_id`) REFERENCES `analysis` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_phylogenomicspipeline`
--

DROP TABLE IF EXISTS `analysis_phylogenomicspipeline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_phylogenomicspipeline` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_ANALYSIS_PHYLOGENOMICSPIPELINE_ANALYSIS` FOREIGN KEY (`id`) REFERENCES `analysis` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_properties`
--

DROP TABLE IF EXISTS `analysis_properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_properties` (
  `analysis_id` bigint(20) NOT NULL,
  `property_value` varchar(255) NOT NULL,
  `property_key` varchar(255) NOT NULL,
  PRIMARY KEY (`analysis_id`,`property_key`),
  CONSTRAINT `FK_ANALYSIS_PROPERTIES_ANALYSIS` FOREIGN KEY (`analysis_id`) REFERENCES `analysis` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission`
--

DROP TABLE IF EXISTS `analysis_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `remote_analysis_id` varchar(255) DEFAULT NULL,
  `remote_workflow_id` varchar(255) DEFAULT NULL,
  `workflow_id` varchar(255) NOT NULL,
  `analysis_state` varchar(255) NOT NULL,
  `analysis_id` bigint(20) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `reference_file_id` bigint(20) DEFAULT NULL,
  `submitter` bigint(20) NOT NULL,
  `remote_input_data_id` varchar(255) DEFAULT NULL,
  `named_parameters_id` bigint(20) DEFAULT NULL,
  `analysis_cleaned_state` varchar(255) NOT NULL DEFAULT 'NOT_CLEANED',
  `analysis_description` longtext,
  PRIMARY KEY (`id`),
  KEY `FK_ANALYSIS_SUBMISSION_ANALYSIS` (`analysis_id`),
  KEY `FK_ANALYSIS_SUBMISSION_REFERENCE_FILE_ID` (`reference_file_id`),
  KEY `FK_ANALYIS_SUBMISSION_SUBMITTER_ID` (`submitter`),
  KEY `FK_ANALYSIS_SUBMISSION_NAMED_PARAMETERS` (`named_parameters_id`),
  CONSTRAINT `FK_ANALYIS_SUBMISSION_SUBMITTER_ID` FOREIGN KEY (`submitter`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_ANALYSIS` FOREIGN KEY (`analysis_id`) REFERENCES `analysis` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_NAMED_PARAMETERS` FOREIGN KEY (`named_parameters_id`) REFERENCES `workflow_named_parameters` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REFERENCE_FILE_ID` FOREIGN KEY (`reference_file_id`) REFERENCES `reference_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_AUD`
--

DROP TABLE IF EXISTS `analysis_submission_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_AUD` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `remote_analysis_id` varchar(255) DEFAULT NULL,
  `remote_workflow_id` varchar(255) DEFAULT NULL,
  `workflow_id` varchar(255) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `analysis_state` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `reference_file_id` bigint(20) DEFAULT NULL,
  `submitter` bigint(20) DEFAULT NULL,
  `remote_input_data_id` varchar(255) DEFAULT NULL,
  `analysis_cleaned_state` varchar(255) DEFAULT 'NOT_CLEANED',
  `analysis_description` longtext,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_ANALYSIS_SUBMISSION_REVISION` (`REV`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_parameters`
--

DROP TABLE IF EXISTS `analysis_submission_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_parameters` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`name`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_PARAMETERS_ANALYSIS_SUBMISSION_ID` FOREIGN KEY (`id`) REFERENCES `analysis_submission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_parameters_AUD`
--

DROP TABLE IF EXISTS `analysis_submission_parameters_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_parameters_AUD` (
  `REV` int(11) NOT NULL,
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`id`,`name`,`value`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_PARAMETER_ID_REV` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_remote_file_pair`
--

DROP TABLE IF EXISTS `analysis_submission_remote_file_pair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_remote_file_pair` (
  `analysis_submission_id` bigint(20) NOT NULL,
  `remote_file_pair_id` bigint(20) NOT NULL,
  PRIMARY KEY (`analysis_submission_id`,`remote_file_pair_id`),
  KEY `FK_ANALYSIS_SUBMISSION_REMOTE_PAIR_FILE` (`remote_file_pair_id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REMOTE_PAIR_ANALYSIS` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REMOTE_PAIR_FILE` FOREIGN KEY (`remote_file_pair_id`) REFERENCES `remote_sequence_file_pair` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_remote_file_pair_AUD`
--

DROP TABLE IF EXISTS `analysis_submission_remote_file_pair_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_remote_file_pair_AUD` (
  `analysis_submission_id` bigint(20) NOT NULL,
  `remote_file_pair_id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`analysis_submission_id`,`remote_file_pair_id`,`REV`),
  KEY `FK_ANALYSIS_SUBMISSION_REMOTE_FILE_PAIR_AUD` (`REV`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REMOTE_FILE_PAIR_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_remote_file_single`
--

DROP TABLE IF EXISTS `analysis_submission_remote_file_single`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_remote_file_single` (
  `analysis_submission_id` bigint(20) NOT NULL,
  `remote_file_id` bigint(20) NOT NULL,
  PRIMARY KEY (`analysis_submission_id`,`remote_file_id`),
  KEY `FK_ANALYSIS_SUBMISSION_REMOTE_UNPAIRED_FILE` (`remote_file_id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REMOTE_SINGLE_ANALYSIS` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REMOTE_UNPAIRED_FILE` FOREIGN KEY (`remote_file_id`) REFERENCES `remote_sequence_file_single` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_remote_file_single_AUD`
--

DROP TABLE IF EXISTS `analysis_submission_remote_file_single_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_remote_file_single_AUD` (
  `analysis_submission_id` bigint(20) NOT NULL,
  `remote_file_id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`analysis_submission_id`,`remote_file_id`,`REV`),
  KEY `FK_ANALYSIS_SUBMISSION_REMOTE_FILE_SINGLE_AUD` (`REV`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REMOTE_FILE_SINGLE_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_sequence_file_pair`
--

DROP TABLE IF EXISTS `analysis_submission_sequence_file_pair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_sequence_file_pair` (
  `analysis_submission_id` bigint(20) NOT NULL,
  `sequence_file_pair_id` bigint(20) NOT NULL,
  PRIMARY KEY (`analysis_submission_id`,`sequence_file_pair_id`),
  KEY `FK_ANALYSIS_SUBMISSION_SEQUENCE_FILE_PAIR_FILE_ID` (`sequence_file_pair_id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_SEQUENCE_FILE_PAIR_FILE_ID` FOREIGN KEY (`sequence_file_pair_id`) REFERENCES `sequence_file_pair` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_SEQUENCE_FILE_PAIR_SUBMISSION_ID` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_sequence_file_pair_AUD`
--

DROP TABLE IF EXISTS `analysis_submission_sequence_file_pair_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_sequence_file_pair_AUD` (
  `REV` int(11) NOT NULL,
  `analysis_submission_id` bigint(20) NOT NULL,
  `sequence_file_pair_id` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`analysis_submission_id`,`sequence_file_pair_id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_SEQUENCE_FILE_PAIR_ID_REV` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_sequence_file_single_end`
--

DROP TABLE IF EXISTS `analysis_submission_sequence_file_single_end`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_sequence_file_single_end` (
  `analysis_submission_id` bigint(20) NOT NULL,
  `sequencing_object_id` bigint(20) NOT NULL,
  PRIMARY KEY (`analysis_submission_id`,`sequencing_object_id`),
  KEY `FK_ANALYSIS_SINGLE_FILE_FILE` (`sequencing_object_id`),
  CONSTRAINT `FK_ANALYSIS_SINGLE_FILE_ANALYSIS` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`),
  CONSTRAINT `FK_ANALYSIS_SINGLE_FILE_FILE` FOREIGN KEY (`sequencing_object_id`) REFERENCES `sequence_file_single_end` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_sequence_file_single_end_AUD`
--

DROP TABLE IF EXISTS `analysis_submission_sequence_file_single_end_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_sequence_file_single_end_AUD` (
  `analysis_submission_id` bigint(20) NOT NULL,
  `sequencing_object_id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`analysis_submission_id`,`sequencing_object_id`,`REV`),
  KEY `FK_ANALYSIS_SINGLE_FILE_AUD` (`REV`),
  CONSTRAINT `FK_ANALYSIS_SINGLE_FILE_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `announcement`
--

DROP TABLE IF EXISTS `announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `announcement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `message` longtext,
  `created_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_USER_ID` (`created_by_id`),
  CONSTRAINT `FK_USER_ID` FOREIGN KEY (`created_by_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `announcement_AUD`
--

DROP TABLE IF EXISTS `announcement_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `announcement_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `message` longtext,
  `created_by_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_ANNOUNCEMENT_AUD` (`REV`),
  CONSTRAINT `FK_ANNOUNCEMENT_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `announcement_user`
--

DROP TABLE IF EXISTS `announcement_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `announcement_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `announcement_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_USER_ID_ANNOUNCEMENT` (`user_id`),
  KEY `FK_ANNOUNCEMENT_ID` (`announcement_id`),
  CONSTRAINT `FK_ANNOUNCEMENT_ID` FOREIGN KEY (`announcement_id`) REFERENCES `announcement` (`id`),
  CONSTRAINT `FK_USER_ID_ANNOUNCEMENT` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `announcement_user_AUD`
--

DROP TABLE IF EXISTS `announcement_user_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `announcement_user_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `announcement_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_ANNOUNCEMENT_USER_AUD` (`REV`),
  CONSTRAINT `FK_ANNOUNCEMENT_USER_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details`
--

DROP TABLE IF EXISTS `client_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `token_validity` int(11) NOT NULL,
  `refresh_validity` int(11) DEFAULT NULL,
  `clientId` varchar(255) NOT NULL,
  `clientSecret` varchar(255) NOT NULL,
  `createdDate` datetime NOT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_CLIENT_DETAILS_CLIENT_ID` (`clientId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_AUD`
--

DROP TABLE IF EXISTS `client_details_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `token_validity` int(11) DEFAULT NULL,
  `refresh_validity` int(11) DEFAULT NULL,
  `clientId` varchar(255) DEFAULT NULL,
  `clientSecret` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_CLIENT_DETAILS_REVISION` (`REV`),
  CONSTRAINT `FK_CLIENT_DETAILS_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_additional_information`
--

DROP TABLE IF EXISTS `client_details_additional_information`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_additional_information` (
  `client_details_id` bigint(20) NOT NULL,
  `info_key` varchar(255) NOT NULL,
  `info_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`client_details_id`,`info_key`),
  CONSTRAINT `FK_CLIENT_DETAILS_INFO` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_additional_information_AUD`
--

DROP TABLE IF EXISTS `client_details_additional_information_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_additional_information_AUD` (
  `client_details_id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `info_key` varchar(255) NOT NULL,
  `info_value` varchar(255) NOT NULL,
  PRIMARY KEY (`client_details_id`,`REV`,`info_key`,`info_value`),
  KEY `FK_CLIENT_DETAILS_INFO_REVISION` (`REV`),
  CONSTRAINT `FK_CLIENT_DETAILS_INFO_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_authorities`
--

DROP TABLE IF EXISTS `client_details_authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_authorities` (
  `client_details_id` bigint(20) NOT NULL,
  `authority_name` varchar(255) NOT NULL,
  KEY `FK_CLIENT_DETAILS_AUTHORITIES` (`client_details_id`),
  KEY `FK_CLIENT_DETAILS_ROLE` (`authority_name`),
  CONSTRAINT `FK_CLIENT_DETAILS_AUTHORITIES` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`),
  CONSTRAINT `FK_CLIENT_DETAILS_ROLE` FOREIGN KEY (`authority_name`) REFERENCES `client_role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_authorities_AUD`
--

DROP TABLE IF EXISTS `client_details_authorities_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_authorities_AUD` (
  `client_details_id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `authority_name` varchar(255) NOT NULL,
  PRIMARY KEY (`client_details_id`,`REV`,`authority_name`),
  KEY `FK_CLIENT_DETAILS_AUTHORITIES_REVISION` (`REV`),
  CONSTRAINT `FK_CLIENT_DETAILS_AUTHORITIES_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_auto_approvable_scope`
--

DROP TABLE IF EXISTS `client_details_auto_approvable_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_auto_approvable_scope` (
  `client_details_id` bigint(20) NOT NULL,
  `auto_approvable_scope` varchar(255) DEFAULT NULL,
  KEY `FK_CLIENT_DETAILS_AUTO_APPROVABLE_SCOPE` (`client_details_id`),
  CONSTRAINT `FK_CLIENT_DETAILS_AUTO_APPROVABLE_SCOPE` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_auto_approvable_scope_AUD`
--

DROP TABLE IF EXISTS `client_details_auto_approvable_scope_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_auto_approvable_scope_AUD` (
  `client_details_id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `auto_approvable_scope` varchar(255) NOT NULL,
  PRIMARY KEY (`client_details_id`,`REV`,`auto_approvable_scope`),
  KEY `FK_CLIENT_DETAILS_AUTO_APPROVABLE_SCOPE_REVISION` (`REV`),
  CONSTRAINT `FK_CLIENT_DETAILS_AUTO_APPROVABLE_SCOPE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_grant_types`
--

DROP TABLE IF EXISTS `client_details_grant_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_grant_types` (
  `client_details_id` bigint(20) NOT NULL,
  `grant_value` varchar(255) NOT NULL,
  PRIMARY KEY (`client_details_id`,`grant_value`),
  CONSTRAINT `FK_CLIENT_DETAILS_GRANT_TYPES` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_grant_types_AUD`
--

DROP TABLE IF EXISTS `client_details_grant_types_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_grant_types_AUD` (
  `client_details_id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `grant_value` varchar(255) NOT NULL,
  PRIMARY KEY (`client_details_id`,`REV`,`grant_value`),
  KEY `FK_CLIENT_DETAILS_GRANT_TYPES_REVISION` (`REV`),
  CONSTRAINT `FK_CLIENT_DETAILS_GRANT_TYPES_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_resource_ids`
--

DROP TABLE IF EXISTS `client_details_resource_ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_resource_ids` (
  `client_details_id` bigint(20) NOT NULL,
  `resource_id` varchar(255) NOT NULL,
  PRIMARY KEY (`client_details_id`,`resource_id`),
  CONSTRAINT `FK_CLIENT_DETAILS_RESOURCE_IDS` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_resource_ids_AUD`
--

DROP TABLE IF EXISTS `client_details_resource_ids_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_resource_ids_AUD` (
  `client_details_id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `resource_id` varchar(255) NOT NULL,
  PRIMARY KEY (`client_details_id`,`REV`,`resource_id`),
  KEY `FK_CLIENT_DETAILS_RESOURCE_IDS_REVISION` (`REV`),
  CONSTRAINT `FK_CLIENT_DETAILS_RESOURCE_IDS_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_scope`
--

DROP TABLE IF EXISTS `client_details_scope`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_scope` (
  `client_details_id` bigint(20) NOT NULL,
  `scope` varchar(255) NOT NULL,
  PRIMARY KEY (`client_details_id`,`scope`),
  CONSTRAINT `FK_CLIENT_DETAILS_SCOPE` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_scope_AUD`
--

DROP TABLE IF EXISTS `client_details_scope_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_scope_AUD` (
  `client_details_id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `scope` varchar(255) NOT NULL,
  PRIMARY KEY (`client_details_id`,`REV`,`scope`),
  KEY `FK_CLIENT_DETAILS_SCOPE_REVISION` (`REV`),
  CONSTRAINT `FK_CLIENT_DETAILS_SCOPE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_role`
--

DROP TABLE IF EXISTS `client_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_role` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_role_AUD`
--

DROP TABLE IF EXISTS `client_role_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_role_AUD` (
  `name` varchar(255) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`,`REV`),
  KEY `FK_CLIENT_ROLE_REVISION` (`REV`),
  CONSTRAINT `FK_CLIENT_ROLE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `miseq_run`
--

DROP TABLE IF EXISTS `miseq_run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `miseq_run` (
  `application` varchar(255) DEFAULT NULL,
  `assay` varchar(255) DEFAULT NULL,
  `chemistry` varchar(255) DEFAULT NULL,
  `experimentName` varchar(255) DEFAULT NULL,
  `investigatorName` varchar(255) DEFAULT NULL,
  `projectName` varchar(255) DEFAULT NULL,
  `workflow` varchar(255) NOT NULL,
  `id` bigint(20) NOT NULL,
  `read_lengths` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_MISEQ_RUN_SEQUENCING_RUN` FOREIGN KEY (`id`) REFERENCES `sequencing_run` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `miseq_run_AUD`
--

DROP TABLE IF EXISTS `miseq_run_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `miseq_run_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `application` varchar(255) DEFAULT NULL,
  `assay` varchar(255) DEFAULT NULL,
  `chemistry` varchar(255) DEFAULT NULL,
  `experimentName` varchar(255) DEFAULT NULL,
  `investigatorName` varchar(255) DEFAULT NULL,
  `projectName` varchar(255) DEFAULT NULL,
  `workflow` varchar(255) DEFAULT NULL,
  `read_lengths` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  CONSTRAINT `FK_MISEQ_RUN_REVISION` FOREIGN KEY (`id`, `REV`) REFERENCES `sequencing_run_AUD` (`id`, `REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ncbi_export_biosample`
--

DROP TABLE IF EXISTS `ncbi_export_biosample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_export_biosample` (
  `id` varchar(255) NOT NULL,
  `bioSample` varchar(255) DEFAULT NULL,
  `instrument_model` varchar(255) DEFAULT NULL,
  `library_name` varchar(255) DEFAULT NULL,
  `library_selection` varchar(255) DEFAULT NULL,
  `library_source` varchar(255) DEFAULT NULL,
  `library_strategy` varchar(255) DEFAULT NULL,
  `library_construction_protocol` varchar(255) DEFAULT NULL,
  `accession` varchar(255) DEFAULT NULL,
  `submission_status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ncbi_export_biosample_sequence_file_pair`
--

DROP TABLE IF EXISTS `ncbi_export_biosample_sequence_file_pair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_export_biosample_sequence_file_pair` (
  `ncbi_export_biosample_id` varchar(255) NOT NULL,
  `pairs_id` bigint(20) NOT NULL,
  KEY `FK_NCBI_SUBMISSION_PAIR_FILE_SUBMISSION` (`ncbi_export_biosample_id`),
  KEY `FK_NCBI_SUBMISSION_PAIR_FILE_PAIR` (`pairs_id`),
  CONSTRAINT `FK_NCBI_SUBMISSION_PAIR_FILE_PAIR` FOREIGN KEY (`pairs_id`) REFERENCES `sequence_file_pair` (`id`),
  CONSTRAINT `FK_NCBI_SUBMISSION_PAIR_FILE_SUBMISSION` FOREIGN KEY (`ncbi_export_biosample_id`) REFERENCES `ncbi_export_biosample` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ncbi_export_biosample_sequence_file_single_end`
--

DROP TABLE IF EXISTS `ncbi_export_biosample_sequence_file_single_end`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_export_biosample_sequence_file_single_end` (
  `ncbi_export_biosample_id` varchar(255) NOT NULL,
  `files_id` bigint(20) NOT NULL,
  KEY `FK_NCBI_EXPORT_SINGLE_END_FILE_FILE` (`files_id`),
  KEY `FK_NCBI_EXPORT_SINGLE_END_FILE_SAMPLE` (`ncbi_export_biosample_id`),
  CONSTRAINT `FK_NCBI_EXPORT_SINGLE_END_FILE_FILE` FOREIGN KEY (`files_id`) REFERENCES `sequence_file_single_end` (`id`),
  CONSTRAINT `FK_NCBI_EXPORT_SINGLE_END_FILE_SAMPLE` FOREIGN KEY (`ncbi_export_biosample_id`) REFERENCES `ncbi_export_biosample` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ncbi_export_submission`
--

DROP TABLE IF EXISTS `ncbi_export_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_export_submission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `submitter` bigint(20) DEFAULT NULL,
  `upload_state` varchar(255) NOT NULL,
  `bio_project_id` varchar(255) DEFAULT NULL,
  `namespace` varchar(255) DEFAULT NULL,
  `release_date` date DEFAULT NULL,
  `directory_path` varchar(255) DEFAULT NULL,
  `organization` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_NCBI_SUBMISSION_PROJECT` (`project_id`),
  KEY `FK_NCBI_SUBMISSION_SUBMITTER` (`submitter`),
  CONSTRAINT `FK_NCBI_SUBMISSION_PROJECT` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_NCBI_SUBMISSION_SUBMITTER` FOREIGN KEY (`submitter`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ncbi_export_submission_biosample`
--

DROP TABLE IF EXISTS `ncbi_export_submission_biosample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_export_submission_biosample` (
  `ncbi_export_submission_id` bigint(20) NOT NULL,
  `bioSampleFiles_id` varchar(255) NOT NULL,
  PRIMARY KEY (`ncbi_export_submission_id`,`bioSampleFiles_id`),
  KEY `FK_NCBI_EXPORT_BIOSAMPLE_FILES` (`bioSampleFiles_id`),
  CONSTRAINT `FK_NCBI_EXPORT_BIOSAMPLE` FOREIGN KEY (`ncbi_export_submission_id`) REFERENCES `ncbi_export_submission` (`id`),
  CONSTRAINT `FK_NCBI_EXPORT_BIOSAMPLE_FILES` FOREIGN KEY (`bioSampleFiles_id`) REFERENCES `ncbi_export_biosample` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oauth_access_token`
--

DROP TABLE IF EXISTS `oauth_access_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_access_token` (
  `token_id` varchar(255) NOT NULL DEFAULT '',
  `token` longblob NOT NULL,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `client_id` varchar(255) NOT NULL,
  `authentication` longblob NOT NULL,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `UK_OAUTH_AUTHENTICATION` (`authentication_id`),
  KEY `FK_OAUTH_TOKEN_CLIENT_DETAILS` (`client_id`),
  CONSTRAINT `FK_OAUTH_TOKEN_CLIENT_DETAILS` FOREIGN KEY (`client_id`) REFERENCES `client_details` (`clientId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `overrepresented_sequence`
--

DROP TABLE IF EXISTS `overrepresented_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `overrepresented_sequence` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `overrepresentedSequenceCount` int(11) NOT NULL,
  `percentage` decimal(19,2) NOT NULL,
  `possibleSource` varchar(255) NOT NULL,
  `sequence` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `password_reset`
--

DROP TABLE IF EXISTS `password_reset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `password_reset` (
  `id` varchar(255) NOT NULL,
  `createdDate` datetime NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_PASSWORD_RESET_USER` (`user_id`),
  CONSTRAINT `FK_PASSWORD_RESET_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `projectDescription` longtext,
  `remoteURL` varchar(255) DEFAULT NULL,
  `organism` varchar(255) DEFAULT NULL,
  `assemble_uploads` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_AUD`
--

DROP TABLE IF EXISTS `project_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `projectDescription` longtext,
  `remoteURL` varchar(255) DEFAULT NULL,
  `organism` varchar(255) DEFAULT NULL,
  `assemble_uploads` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_PROJECT_REVISION` (`REV`),
  CONSTRAINT `FK_PROJECT_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_event`
--

DROP TABLE IF EXISTS `project_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `DTYPE` varchar(31) NOT NULL,
  `created_date` datetime NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  `project_id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `sample_id` bigint(20) DEFAULT NULL,
  `user_group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_PROJECT_EVENT_SAMPLE` (`sample_id`),
  KEY `FK_PROJECT_EVENT_PROJECT` (`project_id`),
  KEY `FK_PROJECT_EVENT_USER` (`user_id`),
  KEY `FK_USER_GROUP_PROJECT_EVENT` (`user_group_id`),
  CONSTRAINT `FK_PROJECT_EVENT_PROJECT` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_EVENT_SAMPLE` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`),
  CONSTRAINT `FK_PROJECT_EVENT_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_USER_GROUP_PROJECT_EVENT` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_referencefile`
--

DROP TABLE IF EXISTS `project_referencefile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_referencefile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `reference_file_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_PROJECT_REFERENCEFILE` (`project_id`,`reference_file_id`),
  KEY `FK_PROJECT_REFERENCEFILE_REFERENCEFILE` (`reference_file_id`),
  CONSTRAINT `FK_PROJECT_REFERENCEFILE_PROJECT` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_REFERENCEFILE_REFERENCEFILE` FOREIGN KEY (`reference_file_id`) REFERENCES `reference_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_referencefile_AUD`
--

DROP TABLE IF EXISTS `project_referencefile_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_referencefile_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `reference_file_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_PROJECT_REFERENCEFILE_REVISION` (`REV`),
  CONSTRAINT `FK_PROJECT_REFERENCEFILE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_sample`
--

DROP TABLE IF EXISTS `project_sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_sample` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `sample_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_PROJECT_SAMPLE` (`project_id`,`sample_id`),
  KEY `FK_PROJECT_SAMPLE_SAMPLE` (`sample_id`),
  CONSTRAINT `FK_PROJECT_SAMPLE_PROJECT` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_SAMPLE_SAMPLE` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_sample_AUD`
--

DROP TABLE IF EXISTS `project_sample_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_sample_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `sample_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_PROJECT_SAMPLE_REVISION` (`REV`),
  CONSTRAINT `FK_PROJECT_SAMPLE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_user`
--

DROP TABLE IF EXISTS `project_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `projectRole` varchar(255) NOT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `email_subscription` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_PROJECT_USER` (`project_id`,`user_id`),
  KEY `FK_PROJECT_USER_USER` (`user_id`),
  CONSTRAINT `FK_PROJECT_USER_PROJECT` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_USER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_user_AUD`
--

DROP TABLE IF EXISTS `project_user_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_user_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `projectRole` varchar(255) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `email_subscription` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_PROJECT_USER_REVISION` (`REV`),
  CONSTRAINT `FK_PROJECT_USER_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reference_file`
--

DROP TABLE IF EXISTS `reference_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reference_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `filePath` varchar(255) NOT NULL,
  `fileRevisionNumber` bigint(20) DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `fileLength` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_REFERENCE_FILE_FILE_PATH` (`filePath`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reference_file_AUD`
--

DROP TABLE IF EXISTS `reference_file_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reference_file_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  `fileRevisionNumber` bigint(20) DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `fileLength` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_REFERENCE_FILE_REVISION` (`REV`),
  CONSTRAINT `FK_REFERENCE_FILE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `related_project`
--

DROP TABLE IF EXISTS `related_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `related_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subject_id` bigint(20) NOT NULL,
  `relatedProject_id` bigint(20) NOT NULL,
  `createdDate` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_RELATED_PROJECT_SUBJECT_OBJECT` (`subject_id`,`relatedProject_id`),
  KEY `FK_RELATED_PROJECTS_OBJECT` (`relatedProject_id`),
  CONSTRAINT `FK_RELATED_PROJECTS_OBJECT` FOREIGN KEY (`relatedProject_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_RELATED_PROJECTS_SUBJECT` FOREIGN KEY (`subject_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `related_project_AUD`
--

DROP TABLE IF EXISTS `related_project_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `related_project_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `subject_id` bigint(20) DEFAULT NULL,
  `relatedProject_id` bigint(20) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_RELATED_PROJECT_REVISION` (`REV`),
  CONSTRAINT `FK_RELATED_PROJECT_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_api`
--

DROP TABLE IF EXISTS `remote_api`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_api` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `clientSecret` varchar(255) NOT NULL,
  `createdDate` datetime NOT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `serviceURI` varchar(255) NOT NULL,
  `clientId` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_REMOTE_API_SERVICEURI` (`serviceURI`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_api_AUD`
--

DROP TABLE IF EXISTS `remote_api_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_api_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `serviceURI` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `clientId` varchar(255) DEFAULT NULL,
  `clientSecret` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_REMOTE_API_REVISION` (`REV`),
  CONSTRAINT `FK_REMOTE_API_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_api_token`
--

DROP TABLE IF EXISTS `remote_api_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_api_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tokenString` varchar(255) NOT NULL,
  `expiryDate` datetime NOT NULL,
  `remote_api_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_remote_api_token_user` (`remote_api_id`,`user_id`),
  KEY `FK_REMOTE_API_TOKEN_USER` (`user_id`),
  CONSTRAINT `FK_REMOTE_API_TOKEN_REMOTE_API` FOREIGN KEY (`remote_api_id`) REFERENCES `remote_api` (`id`),
  CONSTRAINT `FK_REMOTE_API_TOKEN_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_api_token_AUD`
--

DROP TABLE IF EXISTS `remote_api_token_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_api_token_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `tokenString` varchar(255) DEFAULT NULL,
  `expiryDate` datetime DEFAULT NULL,
  `remote_api_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_REMOTE_API_TOKEN_REVISION` (`REV`),
  CONSTRAINT `FK_REMOTE_API_TOKEN_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_related_project`
--

DROP TABLE IF EXISTS `remote_related_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_related_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `remote_project_uri` varchar(255) NOT NULL,
  `remote_api_id` bigint(20) NOT NULL,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_REMOTE_RELATED_PROJECT` (`project_id`,`remote_api_id`,`remote_project_uri`),
  KEY `FK_REMOTE_API_REMOTE_PROJECT` (`remote_api_id`),
  CONSTRAINT `FK_PROJECT_REMOTE_PROJECT` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_REMOTE_API_REMOTE_PROJECT` FOREIGN KEY (`remote_api_id`) REFERENCES `remote_api` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_related_project_AUD`
--

DROP TABLE IF EXISTS `remote_related_project_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_related_project_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `remote_project_uri` varchar(255) DEFAULT NULL,
  `remote_api_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_REMOTE_RELATED_PROJECT_REVISION` (`REV`),
  CONSTRAINT `FK_REMOTE_RELATED_PROJECT_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file`
--

DROP TABLE IF EXISTS `remote_sequence_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `file_revision_number` bigint(20) NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `remote_uri` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_REMOTE_SEQUENCE_FILE_FILE` (`file_path`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_AUD`
--

DROP TABLE IF EXISTS `remote_sequence_file_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_AUD` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `file_revision_number` bigint(20) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `remote_uri` varchar(255) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_REMOTE_SEQUENCE_FILE_AUD` (`REV`),
  CONSTRAINT `FK_REMOTE_SEQUENCE_FILE_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_pair`
--

DROP TABLE IF EXISTS `remote_sequence_file_pair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_pair` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `remote_uri` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_pair_AUD`
--

DROP TABLE IF EXISTS `remote_sequence_file_pair_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_pair_AUD` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `remote_uri` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_REMOTE_SEQUENCE_FILE_PAIR_AUD` (`REV`),
  CONSTRAINT `FK_REMOTE_SEQUENCE_FILE_PAIR_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_pair_files`
--

DROP TABLE IF EXISTS `remote_sequence_file_pair_files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_pair_files` (
  `pair_id` bigint(20) NOT NULL,
  `files_id` bigint(20) NOT NULL,
  PRIMARY KEY (`pair_id`,`files_id`),
  UNIQUE KEY `UK_REMOTE_SEQUENCE_FILE_PAIR` (`files_id`),
  CONSTRAINT `FK_REMOTE_SEQUENCE_FILE_PAIR_FILES_FILE` FOREIGN KEY (`files_id`) REFERENCES `remote_sequence_file` (`id`),
  CONSTRAINT `FK_REMOTE_SEQUENCE_FILE_PAIR_FILES_PAIR` FOREIGN KEY (`pair_id`) REFERENCES `remote_sequence_file_pair` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_pair_files_AUD`
--

DROP TABLE IF EXISTS `remote_sequence_file_pair_files_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_pair_files_AUD` (
  `pair_id` bigint(20) NOT NULL,
  `files_id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`pair_id`,`files_id`,`REV`),
  KEY `FK_REMOTE_SEQUENCE_FILE_PAIR_FILES_AUD` (`REV`),
  CONSTRAINT `FK_REMOTE_SEQUENCE_FILE_PAIR_FILES_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_properties`
--

DROP TABLE IF EXISTS `remote_sequence_file_properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_properties` (
  `sequence_file_id` bigint(20) NOT NULL,
  `property_value` varchar(255) NOT NULL,
  `property_key` varchar(255) NOT NULL,
  PRIMARY KEY (`sequence_file_id`,`property_key`),
  CONSTRAINT `FK_REMOTE_SEQUENCE_FILE_PROPERTIES` FOREIGN KEY (`sequence_file_id`) REFERENCES `remote_sequence_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_properties_AUD`
--

DROP TABLE IF EXISTS `remote_sequence_file_properties_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_properties_AUD` (
  `sequence_file_id` bigint(20) NOT NULL,
  `property_value` varchar(255) NOT NULL,
  `property_key` varchar(255) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`sequence_file_id`,`property_value`,`property_key`,`REV`),
  KEY `FK_REMOTE_SEQUENCE_FILE_PROPERTIES_AUD` (`REV`),
  CONSTRAINT `FK_REMOTE_SEQUENCE_FILE_PROPERTIES_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_single`
--

DROP TABLE IF EXISTS `remote_sequence_file_single`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_single` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `remote_uri` varchar(255) NOT NULL,
  `file_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REMOTE_FILE_SINGLE_FILE` (`file_id`),
  CONSTRAINT `FK_REMOTE_FILE_SINGLE_FILE` FOREIGN KEY (`file_id`) REFERENCES `remote_sequence_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_single_AUD`
--

DROP TABLE IF EXISTS `remote_sequence_file_single_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_single_AUD` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `remote_uri` varchar(255) DEFAULT NULL,
  `file_id` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_REMOTE_SEQUENCE_FILE_SINGLE_AUD` (`REV`),
  CONSTRAINT `FK_REMOTE_SEQUENCE_FILE_SINGLE_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample`
--

DROP TABLE IF EXISTS `sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `description` longtext,
  `modifiedDate` datetime DEFAULT NULL,
  `sampleName` varchar(255) NOT NULL,
  `collectedBy` varchar(255) DEFAULT NULL,
  `geographicLocationName` varchar(255) DEFAULT NULL,
  `isolate` varchar(255) DEFAULT NULL,
  `isolationSource` longtext,
  `latitude` varchar(255) DEFAULT NULL,
  `longitude` varchar(255) DEFAULT NULL,
  `organism` varchar(255) DEFAULT NULL,
  `strain` varchar(255) DEFAULT NULL,
  `collectionDate` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample_AUD`
--

DROP TABLE IF EXISTS `sample_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `description` longtext,
  `modifiedDate` datetime DEFAULT NULL,
  `sampleName` varchar(255) DEFAULT NULL,
  `collectedBy` varchar(255) DEFAULT NULL,
  `geographicLocationName` varchar(255) DEFAULT NULL,
  `isolate` varchar(255) DEFAULT NULL,
  `isolationSource` longtext,
  `latitude` varchar(255) DEFAULT NULL,
  `longitude` varchar(255) DEFAULT NULL,
  `organism` varchar(255) DEFAULT NULL,
  `strain` varchar(255) DEFAULT NULL,
  `collectionDate` date DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SAMPLE_REVISION` (`REV`),
  CONSTRAINT `FK_SAMPLE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample_sequencingobject`
--

DROP TABLE IF EXISTS `sample_sequencingobject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample_sequencingobject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `sample_id` bigint(20) DEFAULT NULL,
  `sequencingobject_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_SEQUENCEOBJECT_SAMPLE_FILE` (`sequencingobject_id`),
  KEY `FK_SAMPLE_SEQUENCING_OBJECT_SAMPLE` (`sample_id`),
  CONSTRAINT `FK_SAMPLE_SEQUENCING_OBJECT_OBJECT` FOREIGN KEY (`sequencingobject_id`) REFERENCES `sequencing_object` (`id`),
  CONSTRAINT `FK_SAMPLE_SEQUENCING_OBJECT_SAMPLE` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample_sequencingobject_AUD`
--

DROP TABLE IF EXISTS `sample_sequencingobject_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample_sequencingobject_AUD` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `sample_id` bigint(20) DEFAULT NULL,
  `sequencingobject_id` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SAMPLE_SEQUENCING_OBJECT_AUD` (`REV`),
  CONSTRAINT `FK_SAMPLE_SEQUENCING_OBJECT_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_file`
--

DROP TABLE IF EXISTS `sequence_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `file_path` varchar(255) NOT NULL,
  `file_revision_number` bigint(20) DEFAULT NULL,
  `fastqc_analysis_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_SEQUENCE_FILE_FILEPATH` (`file_path`),
  KEY `FK_SEQUENCE_FILE_FASTQC` (`fastqc_analysis_id`),
  CONSTRAINT `FK_SEQUENCE_FILE_FASTQC` FOREIGN KEY (`fastqc_analysis_id`) REFERENCES `analysis_fastqc` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_file_AUD`
--

DROP TABLE IF EXISTS `sequence_file_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `file_revision_number` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SEQFILE_REVISION` (`REV`),
  CONSTRAINT `FK_SEQFILE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_file_pair`
--

DROP TABLE IF EXISTS `sequence_file_pair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_pair` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_SEQUENCE_FILE_PAIR_OBJECT` FOREIGN KEY (`id`) REFERENCES `sequencing_object` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_file_pair_AUD`
--

DROP TABLE IF EXISTS `sequence_file_pair_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_pair_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SEQUENCE_FILE_PAIR_AUD` (`REV`),
  CONSTRAINT `FK_SEQUENCE_FILE_PAIR_AUD` FOREIGN KEY (`id`, `REV`) REFERENCES `sequencing_object_AUD` (`id`, `REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_file_pair_files`
--

DROP TABLE IF EXISTS `sequence_file_pair_files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_pair_files` (
  `pair_id` bigint(20) NOT NULL,
  `files_id` bigint(20) NOT NULL,
  PRIMARY KEY (`pair_id`,`files_id`),
  UNIQUE KEY `UK_SEQUENCE_FILE_PAIR` (`files_id`),
  CONSTRAINT `FK_SEQUENCE_FILE_PAIR_FILES_File` FOREIGN KEY (`files_id`) REFERENCES `sequence_file` (`id`),
  CONSTRAINT `FK_SEQUENCE_FILE_PAIR_FILES_PAIR` FOREIGN KEY (`pair_id`) REFERENCES `sequence_file_pair` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_file_pair_files_AUD`
--

DROP TABLE IF EXISTS `sequence_file_pair_files_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_pair_files_AUD` (
  `pair_id` bigint(20) NOT NULL,
  `files_id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`pair_id`,`files_id`,`REV`),
  KEY `FK_SEQUENCE_FILE_PAIR_FILES_AUD` (`REV`),
  CONSTRAINT `FK_SEQUENCE_FILE_PAIR_FILES_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_file_properties`
--

DROP TABLE IF EXISTS `sequence_file_properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_properties` (
  `sequence_file_id` bigint(20) NOT NULL,
  `property_key` varchar(255) NOT NULL,
  `property_value` varchar(255) NOT NULL,
  PRIMARY KEY (`sequence_file_id`,`property_key`),
  CONSTRAINT `FK_SEQUENCE_PROPERTIES_SEQUENCE_FILE` FOREIGN KEY (`sequence_file_id`) REFERENCES `sequence_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_file_properties_AUD`
--

DROP TABLE IF EXISTS `sequence_file_properties_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_properties_AUD` (
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `sequence_file_id` bigint(20) NOT NULL,
  `property_key` varchar(255) NOT NULL,
  `property_value` varchar(255) NOT NULL,
  PRIMARY KEY (`REV`,`sequence_file_id`,`property_key`,`property_value`),
  CONSTRAINT `FK_SEQUENCE_FILE_PROPERTIES_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_file_single_end`
--

DROP TABLE IF EXISTS `sequence_file_single_end`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_single_end` (
  `id` bigint(20) NOT NULL,
  `file_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_SEQUENCE_FILE_SINGLE_FILE` (`file_id`),
  CONSTRAINT `FK_SEQUENCE_FILE_SINGLE_FILE` FOREIGN KEY (`file_id`) REFERENCES `sequence_file` (`id`),
  CONSTRAINT `FK_SEQUENCE_FILE_SINGLE_OBJECT` FOREIGN KEY (`id`) REFERENCES `sequencing_object` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_file_single_end_AUD`
--

DROP TABLE IF EXISTS `sequence_file_single_end_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_single_end_AUD` (
  `id` bigint(20) NOT NULL,
  `file_id` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  PRIMARY KEY (`id`,`REV`),
  CONSTRAINT `FK_SEQUENCE_FILE_SINGLE_AUD` FOREIGN KEY (`id`, `REV`) REFERENCES `sequencing_object_AUD` (`id`, `REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequencing_object`
--

DROP TABLE IF EXISTS `sequencing_object`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequencing_object` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `sequencing_run_id` bigint(20) DEFAULT NULL,
  `automated_assembly` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_SEQUENCING_OBJECT_RUN` (`sequencing_run_id`),
  KEY `FK_SEQUENCING_OBJECT_ASSEMBLY` (`automated_assembly`),
  CONSTRAINT `FK_SEQUENCING_OBJECT_ASSEMBLY` FOREIGN KEY (`automated_assembly`) REFERENCES `analysis_submission` (`id`),
  CONSTRAINT `FK_SEQUENCING_OBJECT_RUN` FOREIGN KEY (`sequencing_run_id`) REFERENCES `sequencing_run` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequencing_object_AUD`
--

DROP TABLE IF EXISTS `sequencing_object_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequencing_object_AUD` (
  `id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `sequencing_run_id` bigint(20) DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `automated_assembly` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SEQUENCING_OBJECT_AUD` (`REV`),
  CONSTRAINT `FK_SEQUENCING_OBJECT_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequencing_run`
--

DROP TABLE IF EXISTS `sequencing_run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequencing_run` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` longtext,
  `createdDate` datetime NOT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `layout_type` varchar(255) NOT NULL,
  `upload_status` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequencing_run_AUD`
--

DROP TABLE IF EXISTS `sequencing_run_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequencing_run_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `description` longtext,
  `createdDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `layout_type` varchar(255) DEFAULT NULL,
  `upload_status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SEQUENCING_RUN_REVISION` (`REV`),
  CONSTRAINT `FK_SEQUENCING_RUN_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_role`
--

DROP TABLE IF EXISTS `system_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_role` (
  `description` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_role_AUD`
--

DROP TABLE IF EXISTS `system_role_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_role_AUD` (
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`name`,`REV`),
  KEY `FK_SYSTEM_ROLE_REVISION` (`REV`),
  CONSTRAINT `FK_SYSTEM_ROLE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tool_execution`
--

DROP TABLE IF EXISTS `tool_execution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tool_execution` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `command_line` longtext NOT NULL,
  `created_date` datetime NOT NULL,
  `execution_manager_identifier` longtext NOT NULL,
  `tool_name` varchar(255) NOT NULL,
  `tool_version` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tool_execution_parameters`
--

DROP TABLE IF EXISTS `tool_execution_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tool_execution_parameters` (
  `tool_id` bigint(20) NOT NULL,
  `execution_parameter_key` varchar(255) NOT NULL,
  `execution_parameter_value` varchar(255) NOT NULL,
  PRIMARY KEY (`tool_id`,`execution_parameter_key`),
  CONSTRAINT `FK_TOOL_EXECUTION_PARAMETERS` FOREIGN KEY (`tool_id`) REFERENCES `tool_execution` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tool_execution_prev_steps`
--

DROP TABLE IF EXISTS `tool_execution_prev_steps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tool_execution_prev_steps` (
  `tool_execution_id` bigint(20) NOT NULL,
  `tool_execution_prev_id` bigint(20) NOT NULL,
  PRIMARY KEY (`tool_execution_id`,`tool_execution_prev_id`),
  UNIQUE KEY `tool_execution_prev_id` (`tool_execution_prev_id`),
  CONSTRAINT `FK_TOOL_EXECUTION_PREV_STEPS_OWNER_ID` FOREIGN KEY (`tool_execution_id`) REFERENCES `tool_execution` (`id`),
  CONSTRAINT `FK_TOOL_EXECUTION_PREV_STEPS_PREV_ID` FOREIGN KEY (`tool_execution_prev_id`) REFERENCES `tool_execution` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `credentialsNonExpired` bit(1) NOT NULL,
  `email` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `firstName` varchar(255) NOT NULL,
  `lastName` varchar(255) NOT NULL,
  `locale` varchar(255) DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `password` varchar(1024) NOT NULL,
  `phoneNumber` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `system_role` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_username_constraint` (`username`),
  UNIQUE KEY `user_email_constraint` (`email`),
  KEY `FK_USER_SYSTEM_ROLE` (`system_role`),
  CONSTRAINT `FK_USER_SYSTEM_ROLE` FOREIGN KEY (`system_role`) REFERENCES `system_role` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_AUD`
--

DROP TABLE IF EXISTS `user_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `credentialsNonExpired` bit(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `locale` varchar(255) DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `password` varchar(1024) DEFAULT NULL,
  `phoneNumber` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `system_role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_USER_REVISION` (`REV`),
  CONSTRAINT `FK_USER_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `description` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_USER_GROUP_NAME` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group_AUD`
--

DROP TABLE IF EXISTS `user_group_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` longtext,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_USER_GROUP_REVISION` (`REV`),
  CONSTRAINT `FK_USER_GROUP_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group_member`
--

DROP TABLE IF EXISTS `user_group_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `role` varchar(255) NOT NULL,
  `group_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_USER_GROUP_MEMBER_GROUP` (`group_id`),
  KEY `FK_USER_GROUP_MEMBER_USER` (`user_id`),
  CONSTRAINT `FK_USER_GROUP_MEMBER_GROUP` FOREIGN KEY (`group_id`) REFERENCES `user_group` (`id`),
  CONSTRAINT `FK_USER_GROUP_MEMBER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group_member_AUD`
--

DROP TABLE IF EXISTS `user_group_member_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group_member_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_USER_GROUP_MEMBER_REVISION` (`REV`),
  CONSTRAINT `FK_USER_GROUP_MEMBER_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group_project`
--

DROP TABLE IF EXISTS `user_group_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `project_role` varchar(255) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `user_group_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_USER_GROUP_PROJECT_PROJECT` (`project_id`),
  KEY `FK_USER_GROUP_PROJECT_USER_GROUP` (`user_group_id`),
  CONSTRAINT `FK_USER_GROUP_PROJECT_PROJECT` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_USER_GROUP_PROJECT_USER_GROUP` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_group_project_AUD`
--

DROP TABLE IF EXISTS `user_group_project_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group_project_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `project_role` varchar(255) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `user_group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_USER_GROUP_PROJECT_REVISION` (`REV`),
  CONSTRAINT `FK_USER_GROUP_PROJECT_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workflow_named_parameter_values`
--

DROP TABLE IF EXISTS `workflow_named_parameter_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `workflow_named_parameter_values` (
  `named_parameters_id` bigint(20) NOT NULL,
  `named_parameter_name` varchar(255) NOT NULL,
  `named_parameter_value` varchar(255) NOT NULL,
  PRIMARY KEY (`named_parameters_id`,`named_parameter_name`),
  CONSTRAINT `FK_NAMED_PARAMETER_VALUES` FOREIGN KEY (`named_parameters_id`) REFERENCES `workflow_named_parameters` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workflow_named_parameters`
--

DROP TABLE IF EXISTS `workflow_named_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `workflow_named_parameters` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `workflow_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-19  9:13:28
