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
-- Table structure for table `Revisions`
--

DROP TABLE IF EXISTS `Revisions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Revisions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` bigint(20) NOT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;
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
  CONSTRAINT `FK_gwojgg9njjyq3vyb9il18pqx7` FOREIGN KEY (`id`) REFERENCES `analysis` (`id`)
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
  CONSTRAINT `FK_31wv7jv236i0rffaxw5p0qmw9` FOREIGN KEY (`id`) REFERENCES `analysis` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_fastqc`
--

DROP TABLE IF EXISTS `analysis_fastqc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_fastqc` (
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
  `id` bigint(20) NOT NULL,
  `fastQCReport_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_gpp3t5wivfxxl6kgnsaj4qo0h` (`fastQCReport_id`),
  CONSTRAINT `FK_elaiy1lmmhchvqyooawnlxpq4` FOREIGN KEY (`id`) REFERENCES `analysis` (`id`),
  CONSTRAINT `FK_gpp3t5wivfxxl6kgnsaj4qo0h` FOREIGN KEY (`fastQCReport_id`) REFERENCES `analysis_output_file` (`id`)
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
  UNIQUE KEY `UK_teo8cd4p4wacvj88xd2edy3gw` (`overrepresentedSequences_id`),
  CONSTRAINT `FK_i7qlp2tp9ljpgujgy487udg3i` FOREIGN KEY (`analysis_fastqc_id`) REFERENCES `analysis_fastqc` (`id`),
  CONSTRAINT `FK_teo8cd4p4wacvj88xd2edy3gw` FOREIGN KEY (`overrepresentedSequences_id`) REFERENCES `overrepresented_sequence` (`id`)
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
  UNIQUE KEY `UK_s80me5b3rqj4hp1bbwf35ykuc` (`tool_execution_id`),
  UNIQUE KEY `UK_o40c1y1nyudy27rppxc1baneb` (`file_path`),
  CONSTRAINT `FK_s80me5b3rqj4hp1bbwf35ykuc` FOREIGN KEY (`tool_execution_id`) REFERENCES `tool_execution` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
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
  UNIQUE KEY `UK_3wvr3pb5la0uylf25amusstrn` (`analysisOutputFilesMap_id`),
  CONSTRAINT `FK_3wvr3pb5la0uylf25amusstrn` FOREIGN KEY (`analysisOutputFilesMap_id`) REFERENCES `analysis_output_file` (`id`),
  CONSTRAINT `FK_qmrnr04aiy1bhy8d99ildcqmk` FOREIGN KEY (`analysis_id`) REFERENCES `analysis` (`id`)
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
  CONSTRAINT `FK_no3ddc22qewf8oq5gwyjtqaoj` FOREIGN KEY (`id`) REFERENCES `analysis` (`id`)
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
  CONSTRAINT `FK_t6delxnmuevmrhnkn0n375lfp` FOREIGN KEY (`analysis_id`) REFERENCES `analysis` (`id`)
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
  `analysis_cleaned_state` varchar(255) NOT NULL,
  `analysis_description` longtext,
  `analysis_state` varchar(255) NOT NULL,
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `remote_analysis_id` varchar(255) DEFAULT NULL,
  `remote_input_data_id` varchar(255) DEFAULT NULL,
  `remote_workflow_id` varchar(255) DEFAULT NULL,
  `workflow_id` varchar(255) NOT NULL,
  `analysis_id` bigint(20) DEFAULT NULL,
  `named_parameters_id` bigint(20) DEFAULT NULL,
  `reference_file_id` bigint(20) DEFAULT NULL,
  `submitter` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8hjsxsypoul5jhpkjorj76vqv` (`analysis_id`),
  KEY `FK_letqim8b139dhoooplswlq0g2` (`named_parameters_id`),
  KEY `FK_m4sepgpdr993kwcxvit226nw0` (`reference_file_id`),
  KEY `FK_45ncc3jc53xdki6tifk2wsbts` (`submitter`),
  CONSTRAINT `FK_45ncc3jc53xdki6tifk2wsbts` FOREIGN KEY (`submitter`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_8hjsxsypoul5jhpkjorj76vqv` FOREIGN KEY (`analysis_id`) REFERENCES `analysis` (`id`),
  CONSTRAINT `FK_letqim8b139dhoooplswlq0g2` FOREIGN KEY (`named_parameters_id`) REFERENCES `workflow_named_parameters` (`id`),
  CONSTRAINT `FK_m4sepgpdr993kwcxvit226nw0` FOREIGN KEY (`reference_file_id`) REFERENCES `reference_file` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_AUD`
--

DROP TABLE IF EXISTS `analysis_submission_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `analysis_cleaned_state` varchar(255) DEFAULT NULL,
  `analysis_description` longtext,
  `analysis_state` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `remote_analysis_id` varchar(255) DEFAULT NULL,
  `remote_input_data_id` varchar(255) DEFAULT NULL,
  `remote_workflow_id` varchar(255) DEFAULT NULL,
  `workflow_id` varchar(255) DEFAULT NULL,
  `reference_file_id` bigint(20) DEFAULT NULL,
  `submitter` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_iuwofgt85806e0li68kwlh31g` (`REV`),
  CONSTRAINT `FK_iuwofgt85806e0li68kwlh31g` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `value` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`,`name`),
  CONSTRAINT `FK_q4vy35idhlga8syk5vc4p8vkv` FOREIGN KEY (`id`) REFERENCES `analysis_submission` (`id`)
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
  `value` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`id`,`value`,`name`),
  CONSTRAINT `FK_oojqv9ciqoum63t93ejlhccdf` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_ikuqlacb3n66qlkdva2sehs2t` (`remote_file_pair_id`),
  CONSTRAINT `FK_ikuqlacb3n66qlkdva2sehs2t` FOREIGN KEY (`remote_file_pair_id`) REFERENCES `remote_sequence_file_pair` (`id`),
  CONSTRAINT `FK_ncmw190g4r6o5cgmtt966n2s` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_remote_file_pair_AUD`
--

DROP TABLE IF EXISTS `analysis_submission_remote_file_pair_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_remote_file_pair_AUD` (
  `REV` int(11) NOT NULL,
  `analysis_submission_id` bigint(20) NOT NULL,
  `remote_file_pair_id` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`analysis_submission_id`,`remote_file_pair_id`),
  CONSTRAINT `FK_c28w4vymglrx6dqq8xvooirf3` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_2wgi5c0bawbvc0tx8q9mn84xn` (`remote_file_id`),
  CONSTRAINT `FK_2wgi5c0bawbvc0tx8q9mn84xn` FOREIGN KEY (`remote_file_id`) REFERENCES `remote_sequence_file_single` (`id`),
  CONSTRAINT `FK_cl8sifvwgelyscbf1hr0utg3i` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_remote_file_single_AUD`
--

DROP TABLE IF EXISTS `analysis_submission_remote_file_single_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_remote_file_single_AUD` (
  `REV` int(11) NOT NULL,
  `analysis_submission_id` bigint(20) NOT NULL,
  `remote_file_id` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`analysis_submission_id`,`remote_file_id`),
  CONSTRAINT `FK_ac4pjky6epa4psiautrvr4605` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_qh6sq398xvuihij8uiodn6o3c` (`sequence_file_pair_id`),
  CONSTRAINT `FK_nmx271hk57rlhkgub4nkxukrs` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`),
  CONSTRAINT `FK_qh6sq398xvuihij8uiodn6o3c` FOREIGN KEY (`sequence_file_pair_id`) REFERENCES `sequence_file_pair` (`id`)
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
  CONSTRAINT `FK_rt014kludblhhdn59fbr5ih5o` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_skwybikfljgw4dnbja7742fvl` (`sequencing_object_id`),
  CONSTRAINT `FK_858nybn64yquq164we2tfqa8b` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`),
  CONSTRAINT `FK_skwybikfljgw4dnbja7742fvl` FOREIGN KEY (`sequencing_object_id`) REFERENCES `sequence_file_single_end` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `analysis_submission_sequence_file_single_end_AUD`
--

DROP TABLE IF EXISTS `analysis_submission_sequence_file_single_end_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_sequence_file_single_end_AUD` (
  `REV` int(11) NOT NULL,
  `analysis_submission_id` bigint(20) NOT NULL,
  `sequencing_object_id` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`analysis_submission_id`,`sequencing_object_id`),
  CONSTRAINT `FK_dkm7aaxsco2h4md3j6dnj6som` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_mi64vubux712v0xu59o524jam` (`created_by_id`),
  CONSTRAINT `FK_mi64vubux712v0xu59o524jam` FOREIGN KEY (`created_by_id`) REFERENCES `user` (`id`)
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
  KEY `FK_6jxc9h4ik57pbehhfllhjp1qc` (`REV`),
  CONSTRAINT `FK_6jxc9h4ik57pbehhfllhjp1qc` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  UNIQUE KEY `UK_k81fttne9l279txae83bemkyj` (`announcement_id`,`user_id`),
  KEY `FK_1qe32ej12x8084yf6dqlevkpt` (`user_id`),
  CONSTRAINT `FK_1qe32ej12x8084yf6dqlevkpt` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_rrbtebyr627cvdehay2yal66l` FOREIGN KEY (`announcement_id`) REFERENCES `announcement` (`id`)
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
  KEY `FK_7tqlv8j68isfgtuyco64vitux` (`REV`),
  CONSTRAINT `FK_7tqlv8j68isfgtuyco64vitux` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `clientId` varchar(255) NOT NULL,
  `clientSecret` varchar(255) NOT NULL,
  `createdDate` datetime NOT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `refresh_validity` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_CLIENT_DETAILS_CLIENT_ID` (`clientId`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
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
  `clientId` varchar(255) DEFAULT NULL,
  `clientSecret` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `refresh_validity` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_sdcuq6v141bq8bhbhrpiu76vt` (`REV`),
  CONSTRAINT `FK_sdcuq6v141bq8bhbhrpiu76vt` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `info_value` varchar(255) DEFAULT NULL,
  `info_key` varchar(255) NOT NULL,
  PRIMARY KEY (`client_details_id`,`info_key`),
  CONSTRAINT `FK_blkw0tmguhgk21gnyvwrsa2xa` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_additional_information_AUD`
--

DROP TABLE IF EXISTS `client_details_additional_information_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_additional_information_AUD` (
  `REV` int(11) NOT NULL,
  `client_details_id` bigint(20) NOT NULL,
  `info_value` varchar(255) NOT NULL,
  `info_key` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`client_details_id`,`info_value`,`info_key`),
  CONSTRAINT `FK_k709kji65jlvtklm30h28bgm2` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_s1giifvkswoboqighsu0f5nnd` (`authority_name`),
  KEY `FK_6wfo2paaje40fejbq418yxoe1` (`client_details_id`),
  CONSTRAINT `FK_6wfo2paaje40fejbq418yxoe1` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`),
  CONSTRAINT `FK_s1giifvkswoboqighsu0f5nnd` FOREIGN KEY (`authority_name`) REFERENCES `client_role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_authorities_AUD`
--

DROP TABLE IF EXISTS `client_details_authorities_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_authorities_AUD` (
  `REV` int(11) NOT NULL,
  `client_details_id` bigint(20) NOT NULL,
  `authority_name` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`client_details_id`,`authority_name`),
  CONSTRAINT `FK_extki80mr6ej10gere9hjjswi` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_h1lh6botdeuvo6pvyk8xr5lf0` (`client_details_id`),
  CONSTRAINT `FK_h1lh6botdeuvo6pvyk8xr5lf0` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_auto_approvable_scope_AUD`
--

DROP TABLE IF EXISTS `client_details_auto_approvable_scope_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_auto_approvable_scope_AUD` (
  `REV` int(11) NOT NULL,
  `client_details_id` bigint(20) NOT NULL,
  `auto_approvable_scope` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`client_details_id`,`auto_approvable_scope`),
  CONSTRAINT `FK_pwntmf3s04iuff0fbamxiy85f` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  CONSTRAINT `FK_maeueqm2q7lar95s2rg674mmf` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_grant_types_AUD`
--

DROP TABLE IF EXISTS `client_details_grant_types_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_grant_types_AUD` (
  `REV` int(11) NOT NULL,
  `client_details_id` bigint(20) NOT NULL,
  `grant_value` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`client_details_id`,`grant_value`),
  CONSTRAINT `FK_hodalj21vexg5kuc480bor5eo` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  CONSTRAINT `FK_del22txdrbrrg2q2edhvhn42` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_resource_ids_AUD`
--

DROP TABLE IF EXISTS `client_details_resource_ids_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_resource_ids_AUD` (
  `REV` int(11) NOT NULL,
  `client_details_id` bigint(20) NOT NULL,
  `resource_id` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`client_details_id`,`resource_id`),
  CONSTRAINT `FK_5onaq03uo64ajm47q8hp8wc8x` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  CONSTRAINT `FK_bjb5xnpevytarw5qm5ra8udc5` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `client_details_scope_AUD`
--

DROP TABLE IF EXISTS `client_details_scope_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_scope_AUD` (
  `REV` int(11) NOT NULL,
  `client_details_id` bigint(20) NOT NULL,
  `scope` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`client_details_id`,`scope`),
  CONSTRAINT `FK_kwxcrutfkp2b1d4e4fo2cr3ww` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_1o0vp9qxwlj6slqa20ejqnya9` (`REV`),
  CONSTRAINT `FK_1o0vp9qxwlj6slqa20ejqnya9` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `read_lengths` int(11) DEFAULT NULL,
  `workflow` varchar(255) NOT NULL,
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_64boc0fuebc1dj9x0dkhvn17n` FOREIGN KEY (`id`) REFERENCES `sequencing_run` (`id`)
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
  `read_lengths` int(11) DEFAULT NULL,
  `workflow` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  CONSTRAINT `FK_2n6s9j26uxficr3elovbris2u` FOREIGN KEY (`id`, `REV`) REFERENCES `sequencing_run_AUD` (`id`, `REV`)
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
  `accession` varchar(255) DEFAULT NULL,
  `bioSample` varchar(255) DEFAULT NULL,
  `instrument_model` varchar(255) DEFAULT NULL,
  `library_construction_protocol` varchar(255) DEFAULT NULL,
  `library_name` varchar(255) DEFAULT NULL,
  `library_selection` varchar(255) DEFAULT NULL,
  `library_source` varchar(255) DEFAULT NULL,
  `library_strategy` varchar(255) DEFAULT NULL,
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
  KEY `FK_ffsjed4wpry36uhnwu4ivnu66` (`pairs_id`),
  KEY `FK_fcmrvu3p57epoosfpewb55dvi` (`ncbi_export_biosample_id`),
  CONSTRAINT `FK_fcmrvu3p57epoosfpewb55dvi` FOREIGN KEY (`ncbi_export_biosample_id`) REFERENCES `ncbi_export_biosample` (`id`),
  CONSTRAINT `FK_ffsjed4wpry36uhnwu4ivnu66` FOREIGN KEY (`pairs_id`) REFERENCES `sequence_file_pair` (`id`)
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
  KEY `FK_5vlknaen2v7o9xnbrn9vl0a2a` (`files_id`),
  KEY `FK_dusjr2itdqulybcab06f00hgw` (`ncbi_export_biosample_id`),
  CONSTRAINT `FK_5vlknaen2v7o9xnbrn9vl0a2a` FOREIGN KEY (`files_id`) REFERENCES `sequence_file_single_end` (`id`),
  CONSTRAINT `FK_dusjr2itdqulybcab06f00hgw` FOREIGN KEY (`ncbi_export_biosample_id`) REFERENCES `ncbi_export_biosample` (`id`)
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
  `bio_project_id` varchar(255) NOT NULL,
  `created_date` datetime NOT NULL,
  `directory_path` varchar(255) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `namespace` varchar(255) NOT NULL,
  `organization` varchar(255) NOT NULL,
  `release_date` date DEFAULT NULL,
  `upload_state` varchar(255) NOT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `submitter` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_b15hyegwfp775uog1scntqfsv` (`project_id`),
  KEY `FK_cc97lkw458kowu4tjip9tpgy5` (`submitter`),
  CONSTRAINT `FK_b15hyegwfp775uog1scntqfsv` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_cc97lkw458kowu4tjip9tpgy5` FOREIGN KEY (`submitter`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
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
  UNIQUE KEY `UK_5ly7wlwrd1e8d2f70nwbnd5v8` (`bioSampleFiles_id`),
  KEY `FK_bxarldq7ma2pfofv35obaytfi` (`ncbi_export_submission_id`),
  CONSTRAINT `FK_5ly7wlwrd1e8d2f70nwbnd5v8` FOREIGN KEY (`bioSampleFiles_id`) REFERENCES `ncbi_export_biosample` (`id`),
  CONSTRAINT `FK_bxarldq7ma2pfofv35obaytfi` FOREIGN KEY (`ncbi_export_submission_id`) REFERENCES `ncbi_export_submission` (`id`)
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
  UNIQUE KEY `authentication_id` (`authentication_id`),
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
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=latin1;
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
  KEY `FK_a8wdbuprq1bigxs2mkb1ag367` (`user_id`),
  CONSTRAINT `FK_a8wdbuprq1bigxs2mkb1ag367` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
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
  `assemble_uploads` bit(1) NOT NULL,
  `createdDate` datetime NOT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `organism` varchar(255) DEFAULT NULL,
  `projectDescription` longtext,
  `remoteURL` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=latin1;
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
  `assemble_uploads` bit(1) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `organism` varchar(255) DEFAULT NULL,
  `projectDescription` longtext,
  `remoteURL` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_n2xwgs7hevwt0ha611ftf085v` (`REV`),
  CONSTRAINT `FK_n2xwgs7hevwt0ha611ftf085v` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_event`
--

DROP TABLE IF EXISTS `project_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_event` (
  `DTYPE` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  `project_id` bigint(20) NOT NULL,
  `user_group_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `sample_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_fpu63srkun9wb39y1akytcgma` (`project_id`),
  KEY `FK_5nmekb1clqnm4gqqnyb39aiv7` (`user_group_id`),
  KEY `FK_kek53ysyc6s9jvoawcgj80wew` (`user_id`),
  KEY `FK_njwveh9cgdmub1d6a0oscpxkw` (`sample_id`),
  CONSTRAINT `FK_5nmekb1clqnm4gqqnyb39aiv7` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`),
  CONSTRAINT `FK_fpu63srkun9wb39y1akytcgma` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_kek53ysyc6s9jvoawcgj80wew` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_njwveh9cgdmub1d6a0oscpxkw` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=latin1;
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
  UNIQUE KEY `UK_g0uvsk7lexikfhkj9b3o9fg2p` (`project_id`,`reference_file_id`),
  KEY `FK_6wmd2ao70dxjkjmywbevvu496` (`reference_file_id`),
  CONSTRAINT `FK_6wmd2ao70dxjkjmywbevvu496` FOREIGN KEY (`reference_file_id`) REFERENCES `reference_file` (`id`),
  CONSTRAINT `FK_956l93an1whvce1hp2krj0rya` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
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
  KEY `FK_45ihc58gnu30j3s0bbl30po7k` (`REV`),
  CONSTRAINT `FK_45ihc58gnu30j3s0bbl30po7k` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  UNIQUE KEY `UK_k0ksp81onbtvtlyh3rvlv1qb9` (`project_id`,`sample_id`),
  KEY `FK_hggs23w3t3ag0tx46v2fdoni0` (`sample_id`),
  CONSTRAINT `FK_hggs23w3t3ag0tx46v2fdoni0` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`),
  CONSTRAINT `FK_mhd5u60xudtj6f8th3kasoy5s` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=latin1;
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
  KEY `FK_arj1cjb0xk8d2ojpdr116nifl` (`REV`),
  CONSTRAINT `FK_arj1cjb0xk8d2ojpdr116nifl` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `email_subscription` bit(1) NOT NULL,
  `projectRole` varchar(255) NOT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jss9imprdxxjyalhommmsi495` (`project_id`,`user_id`),
  KEY `FK_d6kfrxuqknbxrlxhwmn66a3kg` (`user_id`),
  CONSTRAINT `FK_d6kfrxuqknbxrlxhwmn66a3kg` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_ptwhmsh2vocln8sffhyvr2ohm` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=latin1;
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
  `email_subscription` bit(1) DEFAULT NULL,
  `projectRole` varchar(255) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_u80eff5m4010t3wxeydf1h3l` (`REV`),
  CONSTRAINT `FK_u80eff5m4010t3wxeydf1h3l` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `fileLength` bigint(20) DEFAULT NULL,
  `fileRevisionNumber` bigint(20) DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fcmwd512cqtco5bynk6nxvn5s` (`filePath`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
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
  `fileLength` bigint(20) DEFAULT NULL,
  `fileRevisionNumber` bigint(20) DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_9v1imk4ejmggbehnbomus25au` (`REV`),
  CONSTRAINT `FK_9v1imk4ejmggbehnbomus25au` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `createdDate` datetime NOT NULL,
  `relatedProject_id` bigint(20) NOT NULL,
  `subject_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1nb21xwin9hfo1xv78p4i0vch` (`subject_id`,`relatedProject_id`),
  KEY `FK_p2pwx7ub81l71io1gvpy63kt1` (`relatedProject_id`),
  CONSTRAINT `FK_661ahh54g1kmoptu98sfw7avk` FOREIGN KEY (`subject_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_p2pwx7ub81l71io1gvpy63kt1` FOREIGN KEY (`relatedProject_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
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
  `createdDate` datetime DEFAULT NULL,
  `relatedProject_id` bigint(20) DEFAULT NULL,
  `subject_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_ipepiwd0brj3ip4pw2rr8t9ao` (`REV`),
  CONSTRAINT `FK_ipepiwd0brj3ip4pw2rr8t9ao` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `clientId` varchar(255) NOT NULL,
  `clientSecret` varchar(255) NOT NULL,
  `createdDate` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `serviceURI` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_REMOTE_API_SERVICEURI` (`serviceURI`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
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
  `clientId` varchar(255) DEFAULT NULL,
  `clientSecret` varchar(255) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `serviceURI` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_jij3314koi1ll9kihw8hhspem` (`REV`),
  CONSTRAINT `FK_jij3314koi1ll9kihw8hhspem` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `expiryDate` datetime NOT NULL,
  `tokenString` varchar(255) NOT NULL,
  `remote_api_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_remote_api_token_user` (`user_id`,`remote_api_id`),
  KEY `FK_kdbeqn00b1qbf0ay4b964tgiy` (`remote_api_id`),
  CONSTRAINT `FK_kdbeqn00b1qbf0ay4b964tgiy` FOREIGN KEY (`remote_api_id`) REFERENCES `remote_api` (`id`),
  CONSTRAINT `FK_nta7d8wew06apov4y5rk64k88` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
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
  `expiryDate` datetime DEFAULT NULL,
  `tokenString` varchar(255) DEFAULT NULL,
  `remote_api_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_7kw65y594o0hb84koivax0xt` (`REV`),
  CONSTRAINT `FK_7kw65y594o0hb84koivax0xt` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `created_date` datetime NOT NULL,
  `modified_date` datetime DEFAULT NULL,
  `remote_project_uri` varchar(255) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  `remote_api_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_REMOTE_RELATED_PROJECT` (`project_id`,`remote_api_id`,`remote_project_uri`),
  KEY `FK_ha4y5j2g6ns8aciwnwsw8iq72` (`remote_api_id`),
  CONSTRAINT `FK_ha4y5j2g6ns8aciwnwsw8iq72` FOREIGN KEY (`remote_api_id`) REFERENCES `remote_api` (`id`),
  CONSTRAINT `FK_p21dw6ydc7ej14483g1vfh3j2` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
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
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `remote_project_uri` varchar(255) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `remote_api_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_wam98bi5my507450rrhtnq92` (`REV`),
  CONSTRAINT `FK_wam98bi5my507450rrhtnq92` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  UNIQUE KEY `UK_r2kwoo9njq4e48r7b7346ugaj` (`file_path`)
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
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `file_revision_number` bigint(20) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `remote_uri` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_l3ejx8vwhilbcr0rei5nktjgy` (`REV`),
  CONSTRAINT `FK_l3ejx8vwhilbcr0rei5nktjgy` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `remote_uri` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_jdevc1nujveilakxfq16nxjks` (`REV`),
  CONSTRAINT `FK_jdevc1nujveilakxfq16nxjks` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  CONSTRAINT `FK_bru9ccxee21nfllgnay33ny8p` FOREIGN KEY (`files_id`) REFERENCES `remote_sequence_file` (`id`),
  CONSTRAINT `FK_l10fd0usljaghyqu7r0253rb7` FOREIGN KEY (`pair_id`) REFERENCES `remote_sequence_file_pair` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_pair_files_AUD`
--

DROP TABLE IF EXISTS `remote_sequence_file_pair_files_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_pair_files_AUD` (
  `REV` int(11) NOT NULL,
  `pair_id` bigint(20) NOT NULL,
  `files_id` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`pair_id`,`files_id`),
  CONSTRAINT `FK_13qg5nyf5y0fv7ivjco790ao7` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  CONSTRAINT `FK_ctdyh2abw4niaftflpkborl9x` FOREIGN KEY (`sequence_file_id`) REFERENCES `remote_sequence_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_properties_AUD`
--

DROP TABLE IF EXISTS `remote_sequence_file_properties_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_properties_AUD` (
  `REV` int(11) NOT NULL,
  `sequence_file_id` bigint(20) NOT NULL,
  `property_value` varchar(255) NOT NULL,
  `property_key` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`sequence_file_id`,`property_value`,`property_key`),
  CONSTRAINT `FK_fq4w9k565lf872lvqua4dwmjk` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_2sr4m80w44dh13ro33cx50m6h` (`file_id`),
  CONSTRAINT `FK_2sr4m80w44dh13ro33cx50m6h` FOREIGN KEY (`file_id`) REFERENCES `remote_sequence_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `remote_sequence_file_single_AUD`
--

DROP TABLE IF EXISTS `remote_sequence_file_single_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_single_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `remote_uri` varchar(255) DEFAULT NULL,
  `file_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_qrhd4yi5yr3rga90r82h25moc` (`REV`),
  CONSTRAINT `FK_qrhd4yi5yr3rga90r82h25moc` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `collectedBy` varchar(255) DEFAULT NULL,
  `collectionDate` date DEFAULT NULL,
  `createdDate` datetime NOT NULL,
  `description` longtext,
  `geographicLocationName` varchar(255) DEFAULT NULL,
  `isolate` varchar(255) DEFAULT NULL,
  `isolationSource` longtext,
  `latitude` varchar(255) DEFAULT NULL,
  `longitude` varchar(255) DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `organism` varchar(255) DEFAULT NULL,
  `sampleName` varchar(255) NOT NULL,
  `strain` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=latin1;
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
  `collectedBy` varchar(255) DEFAULT NULL,
  `collectionDate` date DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `description` longtext,
  `geographicLocationName` varchar(255) DEFAULT NULL,
  `isolate` varchar(255) DEFAULT NULL,
  `isolationSource` longtext,
  `latitude` varchar(255) DEFAULT NULL,
  `longitude` varchar(255) DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `organism` varchar(255) DEFAULT NULL,
  `sampleName` varchar(255) DEFAULT NULL,
  `strain` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_630kkpasqawvecp8p2imue78k` (`REV`),
  CONSTRAINT `FK_630kkpasqawvecp8p2imue78k` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_f1esw18e6463ongb4rqv9ff6p` (`sample_id`),
  CONSTRAINT `FK_f1esw18e6463ongb4rqv9ff6p` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`),
  CONSTRAINT `FK_n6645etqn5xjmlhre4ura0tbv` FOREIGN KEY (`sequencingobject_id`) REFERENCES `sequencing_object` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample_sequencingobject_AUD`
--

DROP TABLE IF EXISTS `sample_sequencingobject_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sample_sequencingobject_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `sample_id` bigint(20) DEFAULT NULL,
  `sequencingobject_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_hscyjvwnlprxgv76lui15khuu` (`REV`),
  CONSTRAINT `FK_hscyjvwnlprxgv76lui15khuu` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `file_path` varchar(255) NOT NULL,
  `file_revision_number` bigint(20) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `fastqc_analysis_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_if3k1bexmpk16pwoc2twvkthc` (`file_path`),
  KEY `FK_htthmlrlhioh47xm4h3t8prvt` (`fastqc_analysis_id`),
  CONSTRAINT `FK_htthmlrlhioh47xm4h3t8prvt` FOREIGN KEY (`fastqc_analysis_id`) REFERENCES `analysis_fastqc` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;
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
  `file_path` varchar(255) DEFAULT NULL,
  `file_revision_number` bigint(20) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_p455ngkyx9373deuhvtkr0bi2` (`REV`),
  CONSTRAINT `FK_p455ngkyx9373deuhvtkr0bi2` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  CONSTRAINT `FK_djcmkiq8anjr4dji4i2quavvs` FOREIGN KEY (`id`) REFERENCES `sequencing_object` (`id`)
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
  CONSTRAINT `FK_6rn4klasxv16xlf09y8gpokwl` FOREIGN KEY (`id`, `REV`) REFERENCES `sequencing_object_AUD` (`id`, `REV`)
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
  CONSTRAINT `FK_2g6dtlgh8ce6km8vvci8vue2j` FOREIGN KEY (`files_id`) REFERENCES `sequence_file` (`id`),
  CONSTRAINT `FK_ciqpdfdvu1f8icv6pygeyiss3` FOREIGN KEY (`pair_id`) REFERENCES `sequence_file_pair` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequence_file_pair_files_AUD`
--

DROP TABLE IF EXISTS `sequence_file_pair_files_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_pair_files_AUD` (
  `REV` int(11) NOT NULL,
  `pair_id` bigint(20) NOT NULL,
  `files_id` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`pair_id`,`files_id`),
  CONSTRAINT `FK_lwkvaib3rq868tkgo0aq0nyo1` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `property_value` varchar(255) NOT NULL,
  `property_key` varchar(255) NOT NULL,
  PRIMARY KEY (`sequence_file_id`,`property_key`),
  CONSTRAINT `FK_rrnu8xt3ssgwe9rc2ufs4ieu5` FOREIGN KEY (`sequence_file_id`) REFERENCES `sequence_file` (`id`)
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
  `sequence_file_id` bigint(20) NOT NULL,
  `property_value` varchar(255) NOT NULL,
  `property_key` varchar(255) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`sequence_file_id`,`property_value`,`property_key`),
  CONSTRAINT `FK_2j9pfy8i7f832s8ffxuh5krlv` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_b6ec69kjx6atb90bhogxbmon4` (`file_id`),
  CONSTRAINT `FK_b6ec69kjx6atb90bhogxbmon4` FOREIGN KEY (`file_id`) REFERENCES `sequence_file` (`id`),
  CONSTRAINT `FK_kitlftxt2ix40quewsfjpbgdj` FOREIGN KEY (`id`) REFERENCES `sequencing_object` (`id`)
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
  `REV` int(11) NOT NULL,
  `file_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  CONSTRAINT `FK_hc03ym71t2uxtd5fjobo9m4et` FOREIGN KEY (`id`, `REV`) REFERENCES `sequencing_object_AUD` (`id`, `REV`)
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
  `automated_assembly` bigint(20) DEFAULT NULL,
  `sequencing_run_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hsjmlgrwa0n0ogn5mohh82dop` (`automated_assembly`),
  KEY `FK_q6t6cw83bg4rufphv7v4mnuv2` (`sequencing_run_id`),
  CONSTRAINT `FK_hsjmlgrwa0n0ogn5mohh82dop` FOREIGN KEY (`automated_assembly`) REFERENCES `analysis_submission` (`id`),
  CONSTRAINT `FK_q6t6cw83bg4rufphv7v4mnuv2` FOREIGN KEY (`sequencing_run_id`) REFERENCES `sequencing_run` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sequencing_object_AUD`
--

DROP TABLE IF EXISTS `sequencing_object_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequencing_object_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `automated_assembly` bigint(20) DEFAULT NULL,
  `sequencing_run_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_l3ib73o42ae4879d9oaja0gqd` (`REV`),
  CONSTRAINT `FK_l3ib73o42ae4879d9oaja0gqd` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `createdDate` datetime NOT NULL,
  `description` longtext,
  `layout_type` varchar(255) NOT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `upload_status` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
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
  `createdDate` datetime DEFAULT NULL,
  `description` longtext,
  `layout_type` varchar(255) DEFAULT NULL,
  `modifiedDate` datetime DEFAULT NULL,
  `upload_status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_kts6ijtukxade9bnqrnf9ps6p` (`REV`),
  CONSTRAINT `FK_kts6ijtukxade9bnqrnf9ps6p` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_role`
--

DROP TABLE IF EXISTS `system_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_role` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
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
  `name` varchar(255) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`,`REV`),
  KEY `FK_q3neqt718i8h9x5dxdbb0aj40` (`REV`),
  CONSTRAINT `FK_q3neqt718i8h9x5dxdbb0aj40` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tool_execution_parameters`
--

DROP TABLE IF EXISTS `tool_execution_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tool_execution_parameters` (
  `tool_id` bigint(20) NOT NULL,
  `execution_parameter_value` varchar(255) NOT NULL,
  `execution_parameter_key` varchar(255) NOT NULL,
  PRIMARY KEY (`tool_id`,`execution_parameter_key`),
  CONSTRAINT `FK_mlken7w22u6doqy9quots97mx` FOREIGN KEY (`tool_id`) REFERENCES `tool_execution` (`id`)
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
  UNIQUE KEY `UK_6nn3rmr4dvioani1ohv3vbybs` (`tool_execution_prev_id`),
  CONSTRAINT `FK_6nn3rmr4dvioani1ohv3vbybs` FOREIGN KEY (`tool_execution_prev_id`) REFERENCES `tool_execution` (`id`),
  CONSTRAINT `FK_9ht8bhxvwg9f2aqsbt0q0m2qb` FOREIGN KEY (`tool_execution_id`) REFERENCES `tool_execution` (`id`)
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
  UNIQUE KEY `user_email_constraint` (`email`),
  UNIQUE KEY `user_username_constraint` (`username`),
  KEY `FK_dc875ks31x9kh1hjsekrcvxss` (`system_role`),
  CONSTRAINT `FK_dc875ks31x9kh1hjsekrcvxss` FOREIGN KEY (`system_role`) REFERENCES `system_role` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
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
  KEY `FK_ih31uc6fikcf93ek8i9xqfxxo` (`REV`),
  CONSTRAINT `FK_ih31uc6fikcf93ek8i9xqfxxo` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `description` longtext,
  `modified_date` datetime DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_kas9w8ead0ska5n3csefp2bpp` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
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
  `description` longtext,
  `modified_date` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_5la4692y450nl3sr6lxnnk59i` (`REV`),
  CONSTRAINT `FK_5la4692y450nl3sr6lxnnk59i` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  KEY `FK_jqj0jxwdfhx8vvpwic4s0fdsj` (`group_id`),
  KEY `FK_a4357qw3vxhtxdnk4i9tyy7yt` (`user_id`),
  CONSTRAINT `FK_a4357qw3vxhtxdnk4i9tyy7yt` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_jqj0jxwdfhx8vvpwic4s0fdsj` FOREIGN KEY (`group_id`) REFERENCES `user_group` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
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
  KEY `FK_ernc1av53w1j6hwsk6so1vphl` (`REV`),
  CONSTRAINT `FK_ernc1av53w1j6hwsk6so1vphl` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  UNIQUE KEY `UK_ltm4cg6b7fqm3ct91jjvbu9sf` (`project_id`,`user_group_id`),
  KEY `FK_df4ed7j9c3xqs7stofdgdn4ta` (`user_group_id`),
  CONSTRAINT `FK_a1hgc2iskmao7xdrnym41c0ic` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_df4ed7j9c3xqs7stofdgdn4ta` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
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
  KEY `FK_nmvi60s600fuy7r9k61ipymuu` (`REV`),
  CONSTRAINT `FK_nmvi60s600fuy7r9k61ipymuu` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
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
  `named_parameter_value` varchar(255) NOT NULL,
  `named_parameter_name` varchar(255) NOT NULL,
  PRIMARY KEY (`named_parameters_id`,`named_parameter_name`),
  CONSTRAINT `FK_cxut3il5p0icuf47hh3yo2c2k` FOREIGN KEY (`named_parameters_id`) REFERENCES `workflow_named_parameters` (`id`)
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-19  9:09:04
