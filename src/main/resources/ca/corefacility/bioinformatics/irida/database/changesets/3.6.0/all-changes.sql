-- MySQL dump 10.13  Distrib 5.6.28, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: irida_test
-- ------------------------------------------------------
-- Server version	5.6.28-0ubuntu0.15.10.1

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
-- Dumping data for table `DATABASECHANGELOG`
--

LOCK TABLES `DATABASECHANGELOG` WRITE;
/*!40000 ALTER TABLE `DATABASECHANGELOG` DISABLE KEYS */;
INSERT INTO `DATABASECHANGELOG` VALUES ('1','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/1.0/initialschema-v1.0.xml','2016-03-02 16:14:30',1,'EXECUTED','7:a2ee173b2f15108f56694acf8c064e59','createTable (x27)','',NULL,'3.2.2'),('initial-data-set-roles-and-admin','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/1.0/initialdata-v1.0.xml','2016-03-02 16:14:30',2,'EXECUTED','7:13f0fe666732635de5cc289e7399f866','insert (x5)','',NULL,'3.2.2'),('2','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/1.1/updates-v1.1.xml','2016-03-02 16:14:44',3,'EXECUTED','7:0e92e8c480456b96ccf27a2525fc1460','dropTable (x2), renameTable (x2), dropColumn (x10), addUniqueConstraint (x3), addColumn (x3), update (x4), dropForeignKeyConstraint, dropColumn, renameColumn, dropColumn, addPrimaryKey, addForeignKeyConstraint, addColumn, update (x4), dropColumn, ...','',NULL,'3.2.2'),('3','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/1.1/rename-constraints.xml','2016-03-02 16:15:03',4,'EXECUTED','7:aec3a557f08cc292d0830b4ddc3a4153','dropUniqueConstraint, addUniqueConstraint, dropForeignKeyConstraint, addForeignKeyConstraint, dropForeignKeyConstraint, addForeignKeyConstraint, dropForeignKeyConstraint, addForeignKeyConstraint, dropForeignKeyConstraint, addForeignKeyConstraint, ...','',NULL,'3.2.2'),('add-sequencer-role','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/1.2/insert-sequencer-role.xml','2016-03-02 16:15:03',5,'EXECUTED','7:84b47370f1b22adb29d4275ada2e09b9','insert','',NULL,'3.2.2'),('drop-client-role','tom','ca/corefacility/bioinformatics/irida/database/changesets/1.3/drop-client-role.xml','2016-03-02 16:15:03',6,'EXECUTED','7:cd2ef4aca94030052b3922422b6b8df7','delete','',NULL,'3.2.2'),('password_reset','josh','ca/corefacility/bioinformatics/irida/database/changesets/1.5/password_reset.xml','2016-03-02 16:15:04',7,'EXECUTED','7:95e3a556026d31b3c790f037b630cf53','createTable','',NULL,'3.2.2'),('data-model-cleanup','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/2.0/data-model-cleanup.xml','2016-03-02 16:15:09',8,'EXECUTED','7:51b7885c88e8e76ea0b2a82ca44b7e28','addForeignKeyConstraint, addColumn, addForeignKeyConstraint, addColumn, addUniqueConstraint, sql, dropTable (x2), addColumn, addForeignKeyConstraint, addColumn, sql, dropTable (x2)','',NULL,'3.2.2'),('remote-apis','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/remote-apis.xml','2016-03-02 16:15:11',9,'EXECUTED','7:fe2c43e14c532b436d69a4ee67911151','createTable (x3), addUniqueConstraint, createTable','',NULL,'3.2.2'),('user-groups','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/2.0/user-groups.xml','2016-03-02 16:15:13',10,'EXECUTED','7:11b8efeb2d94d44f18812e4c9286bc45','createTable (x4)','',NULL,'3.2.2'),('sequencer-agnostic-files','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/sequencer-agnostic-files.xml','2016-03-02 16:15:21',11,'EXECUTED','7:a89df3cd1e6ef073883a5eb6ec7486ce','createTable, addUniqueConstraint, addForeignKeyConstraint, createTable, sql (x6), dropColumn (x12)','',NULL,'3.2.2'),('oauth-clientid-envers','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/oauth-clientid-envers.xml','2016-03-02 16:15:22',12,'EXECUTED','7:73a66cf755da9c722d00a630f4a1d2b1','addColumn','',NULL,'3.2.2'),('samples','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/2.0/samples.xml','2016-03-02 16:15:44',13,'EXECUTED','7:d38600b693479c8974757e62132901e3','renameColumn (x2), addColumn (x2), createTable (x2), addForeignKeyConstraint','',NULL,'3.2.2'),('client-details','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/client-details.xml','2016-03-02 16:15:50',14,'EXECUTED','7:b791c2470ff70894ac3902761fd21998','createTable (x14), insert, addColumn, dropColumn','',NULL,'3.2.2'),('add-not-null-constraints','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/2.0/add-not-null-constraints.xml','2016-03-02 16:15:59',15,'EXECUTED','7:beed1eb900c6f28de86b516b3259667f','addNotNullConstraint (x15)','',NULL,'3.2.2'),('user-project-organization','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/2.0/organization.xml','2016-03-02 16:16:03',16,'EXECUTED','7:39af431dd7be2ee6371176ac2fd64334','createTable, addColumn (x2), createTable, addColumn (x2)','',NULL,'3.2.2'),('add-organism-project','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/2.0/add-organism-project.xml','2016-03-02 16:16:04',17,'EXECUTED','7:d634f0d6c1e5064a25544495747cfd9e','addColumn (x2)','',NULL,'3.2.2'),('sequencer-type-runs','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/sequencer-type-runs.xml','2016-03-02 16:16:12',18,'EXECUTED','7:494394aa222e4e07e764bc960c62839b','createTable (x2), sql (x2), dropForeignKeyConstraint, renameColumn (x2), addForeignKeyConstraint, dropColumn (x6), addNotNullConstraint, addColumn, sql, dropColumn, renameColumn, addPrimaryKey, dropColumn, addForeignKeyConstraint','',NULL,'3.2.2'),('related-projects','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/related-projects.xml','2016-03-02 16:16:14',19,'EXECUTED','7:dce5ad19c3fd2d8766093b87297c4650','createTable (x2), addUniqueConstraint','',NULL,'3.2.2'),('add-analysis-types','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/2.0/add-analysis-types.xml','2016-03-02 16:16:33',20,'EXECUTED','7:49cb060850a06d07da66ca7d2c59c865','createTable (x6), addForeignKeyConstraint, createTable (x2), sql (x2), dropColumn (x22), createTable (x2), sql, dropForeignKeyConstraint, dropColumn (x2)','',NULL,'3.2.2'),('drop-user-revision-fk','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/drop-user-revision-fk.xml','2016-03-02 16:16:33',21,'EXECUTED','7:9f4f5f9398a3bd0d9ceb6975b61d11f7','dropForeignKeyConstraint (x2)','',NULL,'3.2.2'),('liquibase-cleanup','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/liquibase-cleanup.xml','2016-03-02 16:16:36',22,'EXECUTED','7:4b7d14dcfb9c613da6e0731c1e0917bb','addNotNullConstraint (x2), dropPrimaryKey, dropColumn, addPrimaryKey, addUniqueConstraint','',NULL,'3.2.2'),('remoteapi-dates','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/remoteapi-dates.xml','2016-03-02 16:16:39',23,'EXECUTED','7:27f174534f370f72498b451e3bba1b3b','addColumn (x2), sql, addNotNullConstraint','',NULL,'3.2.2'),('project-reference-file','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/2.0/project-reference-file.xml','2016-03-02 16:16:41',24,'EXECUTED','7:94fef29ca276d445e296e3f2167ed3d0','createTable (x3), addUniqueConstraint, createTable','',NULL,'3.2.2'),('analysis-output-file','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/2.0/analysis-output-file.xml','2016-03-02 16:16:45',25,'EXECUTED','7:8ccddab60ec66dbeccc253df94b7feff','createTable (x2), addColumn (x2), createTable (x2), addForeignKeyConstraint','',NULL,'3.2.2'),('oauth-token','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/oauth-token.xml','2016-03-02 16:16:46',26,'EXECUTED','7:5971e13a719f0a77946bc0a7f01bb089','createTable, addForeignKeyConstraint','',NULL,'3.2.2'),('remote-api-name','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/remote-api-name.xml','2016-03-02 16:16:49',27,'EXECUTED','7:9a9d06f774574ec8d3dd18706e093c01','addColumn (x2), dropColumn, addColumn, addUniqueConstraint, dropColumn, addColumn','',NULL,'3.2.2'),('analysis-submission','aaron.petkau, tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/analysis-submission.xml','2016-03-02 16:16:56',28,'EXECUTED','7:ba9bfa8401244d6df56e4395f8e38a57','createTable (x7), addForeignKeyConstraint, createTable (x2), addForeignKeyConstraint, createTable (x2)','',NULL,'3.2.2'),('samples-collection-date','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/samples-collection-date.xml','2016-03-02 16:16:58',29,'EXECUTED','7:4d841453516f9028b7812e84d2ac9d02','dropColumn, addColumn, dropColumn, addColumn','',NULL,'3.2.2'),('remote-related-projects','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/remote-related-projects.xml','2016-03-02 16:17:00',30,'EXECUTED','7:19c9f38751cb3eb48379cad63339454f','createTable (x2), addUniqueConstraint','',NULL,'3.2.2'),('snapshots','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/snapshots.xml','2016-03-02 16:17:04',31,'EXECUTED','7:57b9ad86facc711374219cadd7221576','createTable (x13)','',NULL,'3.2.2'),('project-events','tom','ca/corefacility/bioinformatics/irida/database/changesets/2.0/project-events.xml','2016-03-02 16:17:05',32,'EXECUTED','7:ffcadaaa6e6d43e235474100c80d0493','createTable','',NULL,'3.2.2'),('sequencing-run-update','tom','ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/sequencing-run-update.xml','2016-03-02 16:17:09',33,'EXECUTED','7:a5a4341adfdd3f72cd9f36c46c216e5d','addColumn, addNotNullConstraint (x2), addColumn (x3)','',NULL,'3.2.2'),('update-analysis-workflows','apetkau,fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/replace-analysis-workflows.xml','2016-03-02 16:17:17',34,'EXECUTED','7:835de105c882abb9c260c6bf80476981','dropForeignKeyConstraint, dropTable, dropForeignKeyConstraint, dropTable, dropForeignKeyConstraint, dropTable, dropForeignKeyConstraint (x2), dropTable, dropForeignKeyConstraint, dropTable, dropForeignKeyConstraint, dropTable (x2), dropForeignKeyC...','',NULL,'3.2.2'),('sequence-file-pairs','tom','ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/sequence-file-pairs.xml','2016-03-02 16:17:18',35,'EXECUTED','7:fd9dda495a3efadd0d0d6155b4457670','createTable (x4), addUniqueConstraint','',NULL,'3.2.2'),('analysis-submission-updates','apetkau','ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/analysis-submission-updates.xml','2016-03-02 16:17:25',36,'EXECUTED','7:9dd0d05e9567ff46ebb410b0868534e0','renameTable (x2), createTable (x6), addColumn (x2)','',NULL,'3.2.2'),('workflow-provenance','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/workflow-provenance.xml','2016-03-02 16:17:32',37,'EXECUTED','7:5c4d02cdeeea19745d144901eb72abef','createTable (x3), renameColumn, addNotNullConstraint, renameColumn (x2), addNotNullConstraint, renameColumn (x2), addNotNullConstraint, renameColumn (x5), addColumn','',NULL,'3.2.2'),('analysis-assembly-annotation','aaron','ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/analysis-assembly-annotation.xml','2016-03-02 16:17:33',38,'EXECUTED','7:68d61a8bf191f9c172893fc9b33af681','createTable','',NULL,'3.2.2'),('fix-project-referencefile-fk','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/fix-project-referencefile-fk.xml','2016-03-02 16:17:34',39,'EXECUTED','7:b707b26d4e1e4dd31dd4eb203724bae5','dropForeignKeyConstraint, renameColumn (x2), addForeignKeyConstraint','',NULL,'3.2.2'),('drop-analysis-audit-tables','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/drop-analysis-audit-tables.xml','2016-03-02 16:17:39',40,'EXECUTED','7:a849f49cc6d7ea0b94f95202d895baff','dropTable (x8), dropColumn (x3), dropForeignKeyConstraint, dropColumn (x3)','',NULL,'3.2.2'),('drop-analysis-sequence-files','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/drop-analysis-sequence-files.xml','2016-03-02 16:17:45',41,'EXECUTED','7:055984b97782b8160f88d833eb7ea065','renameColumn, addNotNullConstraint, renameColumn (x2), addNotNullConstraint, renameColumn, dropForeignKeyConstraint, renameColumn, addForeignKeyConstraint, addColumn, sql, dropTable (x2), renameColumn (x5)','',NULL,'3.2.2'),('analysis-submission-cleanup','aaron','ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/analysis-submission-cleanup.xml','2016-03-02 16:17:46',42,'EXECUTED','7:b454e91b1972f550d1a6316d0d32267e','addColumn (x2)','',NULL,'3.2.2'),('unique-sequencefile-sample','tom','ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/unique-sequencefile-sample.xml','2016-03-02 16:17:46',43,'EXECUTED','7:60af0f3356df4887f40362348576f48e','addUniqueConstraint','',NULL,'3.2.2'),('oauth-auto-approvable-scopes.xml','joelt','ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/oauth-auto-approvable-scopes.xml','2016-03-02 16:17:47',44,'EXECUTED','7:2e52e56959df663a3c4cc4c5a2115860','createTable (x2)','',NULL,'3.2.2'),('remove-snapshots','tom','ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/remove-snapshots.xml','2016-03-02 16:17:49',45,'EXECUTED','7:f8ccdf887e785c23b7d553c5ffb4fa39','dropTable (x13)','',NULL,'3.2.2'),('analysis-submission-cleanup','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.2.0/command-line-not-nullable.xml','2016-03-02 16:17:49',46,'EXECUTED','7:9630b5ffaeed9fcc0cef13bd1d347aad','addNotNullConstraint','',NULL,'3.2.2'),('analysis-assembly-annotation-collection','aaron','ca/corefacility/bioinformatics/irida/database/changesets/3.2.0/assembly-annotation-collection.xml','2016-03-02 16:17:49',47,'EXECUTED','7:279e4254a8767a77e29ef75dd91dc0d9','createTable','',NULL,'3.2.2'),('remote-sequence-file','tom','ca/corefacility/bioinformatics/irida/database/changesets/3.2.0/remote-sequence-file.xml','2016-03-02 16:17:54',48,'EXECUTED','7:cf68366870516e1ef18fb35737437eb2','createTable, addUniqueConstraint, createTable (x6), addUniqueConstraint, createTable (x5)','',NULL,'3.2.2'),('assembled-genome','aaron','ca/corefacility/bioinformatics/irida/database/changesets/3.3.0/assembled-genome-analysis.xml','2016-03-02 16:17:57',49,'EXECUTED','7:b6c8921a1f35bab4086bd36f057ea0d1','createTable, addColumn (x2)','',NULL,'3.2.2'),('update-project-modified-date','tom','ca/corefacility/bioinformatics/irida/database/changesets/3.3.0/update-project-modified-date.xml','2016-03-02 16:17:57',50,'EXECUTED','7:4686cf6974916b3d8bd020c09253c0bf','dropColumn, sql','',NULL,'3.2.2'),('fix-workflow-id-column-type','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.3.0/fix-workflow-id-column-type.xml','2016-03-02 16:17:58',51,'EXECUTED','7:5672fc8508a7dc289a2ebb381dce0562','modifyDataType, addNotNullConstraint, sql','',NULL,'3.2.2'),('email-subscription','tom','ca/corefacility/bioinformatics/irida/database/changesets/3.4.0/email-subscription.xml','2016-03-02 16:18:00',52,'EXECUTED','7:bc8a4d0ad7aafe682540c185cecb2a69','addColumn (x2)','',NULL,'3.2.2'),('drop-sample-sequencer-id','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.4.0/drop-sample-sequencer-id.xml','2016-03-02 16:18:01',53,'EXECUTED','7:404cd46f14fef5ca16fbd2fe9f705eb9','dropColumn (x2)','',NULL,'3.2.2'),('oauth-unique-key','tom','ca/corefacility/bioinformatics/irida/database/changesets/3.4.0/oauth-unique-key.xml','2016-03-02 16:18:01',54,'EXECUTED','7:b5740bb662ad8437ea3b5f20d32f9d13','delete, addUniqueConstraint','',NULL,'3.2.2'),('drop-group-host-organization','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/drop-group-host-organization.xml','2016-03-02 16:18:18',55,'EXECUTED','7:e2279a6805a875b4185b088fc9d3a596','dropTable (x4), dropColumn (x8), dropForeignKeyConstraint, dropColumn (x2), dropForeignKeyConstraint, dropColumn (x12), dropForeignKeyConstraint, dropColumn (x2), dropTable (x4)','',NULL,'3.2.2'),('schema-inconsistencies-part-revisions-index-liquibase','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2016-03-02 16:18:18',56,'EXECUTED','7:47353a34583f68d134098812ddb9ecab','dropIndex','',NULL,'3.2.2'),('schema-inconsistencies-part-revisions-index-hibernate','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2016-03-02 16:18:19',57,'MARK_RAN','7:1c414137e437d3daff4583f073f07fb5','dropIndex','',NULL,'3.2.2'),('schema-inconsistencies-part-miseq-run-aud-fk','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2016-03-02 16:18:19',58,'EXECUTED','7:83b90315f73d87a1279423f555218f07','dropForeignKeyConstraint','',NULL,'3.2.2'),('schema-inconsistencies-part-miseq-run-aud-liquibase','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2016-03-02 16:18:19',59,'EXECUTED','7:a5fe1685c20a0a93a555807e644d57d0','dropIndex','',NULL,'3.2.2'),('schema-inconsistencies-part-miseq-run-aud-hibernate','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2016-03-02 16:18:20',60,'MARK_RAN','7:07c9ecf0bea89e5c093e048bf127ed62','dropIndex','',NULL,'3.2.2'),('schema-inconsistencies','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2016-03-02 16:18:37',61,'EXECUTED','7:29590a1337c6c6464c6c3eda9c5fc405','dropIndex, addForeignKeyConstraint, dropColumn (x2), sql, addNotNullConstraint (x3), dropDefaultValue, addDefaultValue, addNotNullConstraint (x2), dropDefaultValue, dropUniqueConstraint, dropForeignKeyConstraint, dropUniqueConstraint, dropForeignK...','',NULL,'3.2.2'),('ncbi-upload','tom','ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/ncbi-upload.xml','2016-03-02 16:18:39',62,'EXECUTED','7:9c543b3aaefe7847f9b8dfe94faafca4','createTable (x5)','',NULL,'3.2.2'),('user-group','fbristow','ca/corefacility/bioinformatics/irida/database/changesets/3.6.0/user-group.xml','2016-03-02 16:18:42',63,'EXECUTED','7:7a80f90e017093aa5dc7b8616f2e7287','createTable (x6)','',NULL,'3.2.2');
/*!40000 ALTER TABLE `DATABASECHANGELOG` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `DATABASECHANGELOGLOCK`
--

LOCK TABLES `DATABASECHANGELOGLOCK` WRITE;
/*!40000 ALTER TABLE `DATABASECHANGELOGLOCK` DISABLE KEYS */;
INSERT INTO `DATABASECHANGELOGLOCK` VALUES (1,'\0',NULL,NULL);
/*!40000 ALTER TABLE `DATABASECHANGELOGLOCK` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `Revisions`
--

LOCK TABLES `Revisions` WRITE;
/*!40000 ALTER TABLE `Revisions` DISABLE KEYS */;
/*!40000 ALTER TABLE `Revisions` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis`
--

LOCK TABLES `analysis` WRITE;
/*!40000 ALTER TABLE `analysis` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_assemblyannotation`
--

LOCK TABLES `analysis_assemblyannotation` WRITE;
/*!40000 ALTER TABLE `analysis_assemblyannotation` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_assemblyannotation` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_assemblyannotation_collection`
--

LOCK TABLES `analysis_assemblyannotation_collection` WRITE;
/*!40000 ALTER TABLE `analysis_assemblyannotation_collection` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_assemblyannotation_collection` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_fastqc`
--

LOCK TABLES `analysis_fastqc` WRITE;
/*!40000 ALTER TABLE `analysis_fastqc` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_fastqc` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_fastqc_overrepresented_sequence`
--

LOCK TABLES `analysis_fastqc_overrepresented_sequence` WRITE;
/*!40000 ALTER TABLE `analysis_fastqc_overrepresented_sequence` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_fastqc_overrepresented_sequence` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_output_file`
--

LOCK TABLES `analysis_output_file` WRITE;
/*!40000 ALTER TABLE `analysis_output_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_output_file` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_output_file_map`
--

LOCK TABLES `analysis_output_file_map` WRITE;
/*!40000 ALTER TABLE `analysis_output_file_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_output_file_map` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_phylogenomicspipeline`
--

LOCK TABLES `analysis_phylogenomicspipeline` WRITE;
/*!40000 ALTER TABLE `analysis_phylogenomicspipeline` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_phylogenomicspipeline` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_properties`
--

LOCK TABLES `analysis_properties` WRITE;
/*!40000 ALTER TABLE `analysis_properties` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_properties` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_submission`
--

LOCK TABLES `analysis_submission` WRITE;
/*!40000 ALTER TABLE `analysis_submission` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission` ENABLE KEYS */;
UNLOCK TABLES;

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
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_ANALYSIS_SUBMISSION_REVISION` (`REV`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `analysis_submission_AUD`
--

LOCK TABLES `analysis_submission_AUD` WRITE;
/*!40000 ALTER TABLE `analysis_submission_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_submission_parameters`
--

LOCK TABLES `analysis_submission_parameters` WRITE;
/*!40000 ALTER TABLE `analysis_submission_parameters` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_parameters` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_submission_parameters_AUD`
--

LOCK TABLES `analysis_submission_parameters_AUD` WRITE;
/*!40000 ALTER TABLE `analysis_submission_parameters_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_parameters_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_submission_remote_file_pair`
--

LOCK TABLES `analysis_submission_remote_file_pair` WRITE;
/*!40000 ALTER TABLE `analysis_submission_remote_file_pair` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_remote_file_pair` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_submission_remote_file_pair_AUD`
--

LOCK TABLES `analysis_submission_remote_file_pair_AUD` WRITE;
/*!40000 ALTER TABLE `analysis_submission_remote_file_pair_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_remote_file_pair_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
  KEY `FK_ANALYSIS_SUBMISSION_REMOTE_SINGLE_FILE` (`remote_file_id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REMOTE_SINGLE_ANALYSIS` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REMOTE_SINGLE_FILE` FOREIGN KEY (`remote_file_id`) REFERENCES `remote_sequence_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `analysis_submission_remote_file_single`
--

LOCK TABLES `analysis_submission_remote_file_single` WRITE;
/*!40000 ALTER TABLE `analysis_submission_remote_file_single` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_remote_file_single` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_submission_remote_file_single_AUD`
--

LOCK TABLES `analysis_submission_remote_file_single_AUD` WRITE;
/*!40000 ALTER TABLE `analysis_submission_remote_file_single_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_remote_file_single_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_submission_sequence_file_pair`
--

LOCK TABLES `analysis_submission_sequence_file_pair` WRITE;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_pair` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_pair` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `analysis_submission_sequence_file_pair_AUD`
--

LOCK TABLES `analysis_submission_sequence_file_pair_AUD` WRITE;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_pair_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_pair_AUD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `analysis_submission_sequence_file_single`
--

DROP TABLE IF EXISTS `analysis_submission_sequence_file_single`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_sequence_file_single` (
  `analysis_submission_id` bigint(20) NOT NULL,
  `sequence_file_id` bigint(20) NOT NULL,
  PRIMARY KEY (`analysis_submission_id`,`sequence_file_id`),
  KEY `FK_ANALYSIS_SUBMISSION_SEQUENCE_FILE_FILE_ID` (`sequence_file_id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_SEQUENCE_FILE_FILE_ID` FOREIGN KEY (`sequence_file_id`) REFERENCES `sequence_file` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_SEQUENCE_FILE_SUBMISSION_ID` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `analysis_submission_sequence_file_single`
--

LOCK TABLES `analysis_submission_sequence_file_single` WRITE;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_single` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_single` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `analysis_submission_sequence_file_single_AUD`
--

DROP TABLE IF EXISTS `analysis_submission_sequence_file_single_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_submission_sequence_file_single_AUD` (
  `REV` int(11) NOT NULL,
  `analysis_submission_id` bigint(20) NOT NULL,
  `sequence_file_id` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`analysis_submission_id`,`sequence_file_id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_SEQUENCE_FILE_ID_REV` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `analysis_submission_sequence_file_single_AUD`
--

LOCK TABLES `analysis_submission_sequence_file_single_AUD` WRITE;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_single_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_single_AUD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assembled_genome_analysis`
--

DROP TABLE IF EXISTS `assembled_genome_analysis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `assembled_genome_analysis` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `analysis` bigint(20) NOT NULL,
  `created_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `analysis` (`analysis`),
  CONSTRAINT `FK_ASSEMBLED_GENOME_ANALYSIS_ANALYSIS` FOREIGN KEY (`analysis`) REFERENCES `analysis_assemblyannotation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assembled_genome_analysis`
--

LOCK TABLES `assembled_genome_analysis` WRITE;
/*!40000 ALTER TABLE `assembled_genome_analysis` DISABLE KEYS */;
/*!40000 ALTER TABLE `assembled_genome_analysis` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details`
--

LOCK TABLES `client_details` WRITE;
/*!40000 ALTER TABLE `client_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_AUD`
--

LOCK TABLES `client_details_AUD` WRITE;
/*!40000 ALTER TABLE `client_details_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_additional_information`
--

LOCK TABLES `client_details_additional_information` WRITE;
/*!40000 ALTER TABLE `client_details_additional_information` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_additional_information` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_additional_information_AUD`
--

LOCK TABLES `client_details_additional_information_AUD` WRITE;
/*!40000 ALTER TABLE `client_details_additional_information_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_additional_information_AUD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_details_authorities`
--

DROP TABLE IF EXISTS `client_details_authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client_details_authorities` (
  `client_details_id` bigint(20) NOT NULL,
  `authority_name` varchar(255) NOT NULL,
  KEY `FK_CLIENT_DETAILS_ROLE` (`authority_name`),
  KEY `FK_CLIENT_DETAILS_AUTHORITIES` (`client_details_id`),
  CONSTRAINT `FK_CLIENT_DETAILS_AUTHORITIES` FOREIGN KEY (`client_details_id`) REFERENCES `client_details` (`id`),
  CONSTRAINT `FK_CLIENT_DETAILS_ROLE` FOREIGN KEY (`authority_name`) REFERENCES `client_role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_details_authorities`
--

LOCK TABLES `client_details_authorities` WRITE;
/*!40000 ALTER TABLE `client_details_authorities` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_authorities` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_authorities_AUD`
--

LOCK TABLES `client_details_authorities_AUD` WRITE;
/*!40000 ALTER TABLE `client_details_authorities_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_authorities_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_auto_approvable_scope`
--

LOCK TABLES `client_details_auto_approvable_scope` WRITE;
/*!40000 ALTER TABLE `client_details_auto_approvable_scope` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_auto_approvable_scope` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_auto_approvable_scope_AUD`
--

LOCK TABLES `client_details_auto_approvable_scope_AUD` WRITE;
/*!40000 ALTER TABLE `client_details_auto_approvable_scope_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_auto_approvable_scope_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_grant_types`
--

LOCK TABLES `client_details_grant_types` WRITE;
/*!40000 ALTER TABLE `client_details_grant_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_grant_types` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_grant_types_AUD`
--

LOCK TABLES `client_details_grant_types_AUD` WRITE;
/*!40000 ALTER TABLE `client_details_grant_types_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_grant_types_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_resource_ids`
--

LOCK TABLES `client_details_resource_ids` WRITE;
/*!40000 ALTER TABLE `client_details_resource_ids` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_resource_ids` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_resource_ids_AUD`
--

LOCK TABLES `client_details_resource_ids_AUD` WRITE;
/*!40000 ALTER TABLE `client_details_resource_ids_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_resource_ids_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_scope`
--

LOCK TABLES `client_details_scope` WRITE;
/*!40000 ALTER TABLE `client_details_scope` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_scope` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_details_scope_AUD`
--

LOCK TABLES `client_details_scope_AUD` WRITE;
/*!40000 ALTER TABLE `client_details_scope_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_details_scope_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_role`
--

LOCK TABLES `client_role` WRITE;
/*!40000 ALTER TABLE `client_role` DISABLE KEYS */;
INSERT INTO `client_role` VALUES ('ROLE_CLIENT','A basic IRIDA OAuth2 client');
/*!40000 ALTER TABLE `client_role` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `client_role_AUD`
--

LOCK TABLES `client_role_AUD` WRITE;
/*!40000 ALTER TABLE `client_role_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_role_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `miseq_run`
--

LOCK TABLES `miseq_run` WRITE;
/*!40000 ALTER TABLE `miseq_run` DISABLE KEYS */;
/*!40000 ALTER TABLE `miseq_run` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `miseq_run_AUD`
--

LOCK TABLES `miseq_run_AUD` WRITE;
/*!40000 ALTER TABLE `miseq_run_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `miseq_run_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `ncbi_export_biosample`
--

LOCK TABLES `ncbi_export_biosample` WRITE;
/*!40000 ALTER TABLE `ncbi_export_biosample` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbi_export_biosample` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ncbi_export_biosample_sequence_file`
--

DROP TABLE IF EXISTS `ncbi_export_biosample_sequence_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_export_biosample_sequence_file` (
  `ncbi_export_biosample_id` varchar(255) NOT NULL,
  `files_id` bigint(20) NOT NULL,
  KEY `FK_NCBI_SUBMISSION_SINGLE_FILE_SUBMISSION` (`ncbi_export_biosample_id`),
  KEY `FK_NCBI_SUBMISSION_SINGLE_FILE` (`files_id`),
  CONSTRAINT `FK_NCBI_SUBMISSION_SINGLE_FILE` FOREIGN KEY (`files_id`) REFERENCES `sequence_file` (`id`),
  CONSTRAINT `FK_NCBI_SUBMISSION_SINGLE_FILE_SUBMISSION` FOREIGN KEY (`ncbi_export_biosample_id`) REFERENCES `ncbi_export_biosample` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ncbi_export_biosample_sequence_file`
--

LOCK TABLES `ncbi_export_biosample_sequence_file` WRITE;
/*!40000 ALTER TABLE `ncbi_export_biosample_sequence_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbi_export_biosample_sequence_file` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `ncbi_export_biosample_sequence_file_pair`
--

LOCK TABLES `ncbi_export_biosample_sequence_file_pair` WRITE;
/*!40000 ALTER TABLE `ncbi_export_biosample_sequence_file_pair` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbi_export_biosample_sequence_file_pair` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `ncbi_export_submission`
--

LOCK TABLES `ncbi_export_submission` WRITE;
/*!40000 ALTER TABLE `ncbi_export_submission` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbi_export_submission` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `ncbi_export_submission_biosample`
--

LOCK TABLES `ncbi_export_submission_biosample` WRITE;
/*!40000 ALTER TABLE `ncbi_export_submission_biosample` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbi_export_submission_biosample` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `oauth_access_token`
--

LOCK TABLES `oauth_access_token` WRITE;
/*!40000 ALTER TABLE `oauth_access_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_access_token` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `overrepresented_sequence`
--

LOCK TABLES `overrepresented_sequence` WRITE;
/*!40000 ALTER TABLE `overrepresented_sequence` DISABLE KEYS */;
/*!40000 ALTER TABLE `overrepresented_sequence` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `password_reset`
--

LOCK TABLES `password_reset` WRITE;
/*!40000 ALTER TABLE `password_reset` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset` ENABLE KEYS */;
UNLOCK TABLES;

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

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
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_PROJECT_REVISION` (`REV`),
  CONSTRAINT `FK_PROJECT_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_AUD`
--

LOCK TABLES `project_AUD` WRITE;
/*!40000 ALTER TABLE `project_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
  PRIMARY KEY (`id`),
  KEY `FK_PROJECT_EVENT_PROJECT` (`project_id`),
  KEY `FK_PROJECT_EVENT_SAMPLE` (`sample_id`),
  KEY `FK_PROJECT_EVENT_USER` (`user_id`),
  CONSTRAINT `FK_PROJECT_EVENT_PROJECT` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_EVENT_SAMPLE` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`),
  CONSTRAINT `FK_PROJECT_EVENT_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_event`
--

LOCK TABLES `project_event` WRITE;
/*!40000 ALTER TABLE `project_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_event` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `project_referencefile`
--

LOCK TABLES `project_referencefile` WRITE;
/*!40000 ALTER TABLE `project_referencefile` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_referencefile` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `project_referencefile_AUD`
--

LOCK TABLES `project_referencefile_AUD` WRITE;
/*!40000 ALTER TABLE `project_referencefile_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_referencefile_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `project_sample`
--

LOCK TABLES `project_sample` WRITE;
/*!40000 ALTER TABLE `project_sample` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_sample` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `project_sample_AUD`
--

LOCK TABLES `project_sample_AUD` WRITE;
/*!40000 ALTER TABLE `project_sample_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_sample_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `project_user`
--

LOCK TABLES `project_user` WRITE;
/*!40000 ALTER TABLE `project_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_user` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `project_user_AUD`
--

LOCK TABLES `project_user_AUD` WRITE;
/*!40000 ALTER TABLE `project_user_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_user_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `reference_file`
--

LOCK TABLES `reference_file` WRITE;
/*!40000 ALTER TABLE `reference_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `reference_file` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `reference_file_AUD`
--

LOCK TABLES `reference_file_AUD` WRITE;
/*!40000 ALTER TABLE `reference_file_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `reference_file_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `related_project`
--

LOCK TABLES `related_project` WRITE;
/*!40000 ALTER TABLE `related_project` DISABLE KEYS */;
/*!40000 ALTER TABLE `related_project` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `related_project_AUD`
--

LOCK TABLES `related_project_AUD` WRITE;
/*!40000 ALTER TABLE `related_project_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `related_project_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_api`
--

LOCK TABLES `remote_api` WRITE;
/*!40000 ALTER TABLE `remote_api` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_api` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_api_AUD`
--

LOCK TABLES `remote_api_AUD` WRITE;
/*!40000 ALTER TABLE `remote_api_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_api_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_api_token`
--

LOCK TABLES `remote_api_token` WRITE;
/*!40000 ALTER TABLE `remote_api_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_api_token` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_api_token_AUD`
--

LOCK TABLES `remote_api_token_AUD` WRITE;
/*!40000 ALTER TABLE `remote_api_token_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_api_token_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_related_project`
--

LOCK TABLES `remote_related_project` WRITE;
/*!40000 ALTER TABLE `remote_related_project` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_related_project` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_related_project_AUD`
--

LOCK TABLES `remote_related_project_AUD` WRITE;
/*!40000 ALTER TABLE `remote_related_project_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_related_project_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_sequence_file`
--

LOCK TABLES `remote_sequence_file` WRITE;
/*!40000 ALTER TABLE `remote_sequence_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_sequence_file` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_sequence_file_AUD`
--

LOCK TABLES `remote_sequence_file_AUD` WRITE;
/*!40000 ALTER TABLE `remote_sequence_file_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_sequence_file_AUD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `remote_sequence_file_pair`
--

DROP TABLE IF EXISTS `remote_sequence_file_pair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_sequence_file_pair` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remote_sequence_file_pair`
--

LOCK TABLES `remote_sequence_file_pair` WRITE;
/*!40000 ALTER TABLE `remote_sequence_file_pair` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_sequence_file_pair` ENABLE KEYS */;
UNLOCK TABLES;

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
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_REMOTE_SEQUENCE_FILE_PAIR_AUD` (`REV`),
  CONSTRAINT `FK_REMOTE_SEQUENCE_FILE_PAIR_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remote_sequence_file_pair_AUD`
--

LOCK TABLES `remote_sequence_file_pair_AUD` WRITE;
/*!40000 ALTER TABLE `remote_sequence_file_pair_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_sequence_file_pair_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_sequence_file_pair_files`
--

LOCK TABLES `remote_sequence_file_pair_files` WRITE;
/*!40000 ALTER TABLE `remote_sequence_file_pair_files` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_sequence_file_pair_files` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_sequence_file_pair_files_AUD`
--

LOCK TABLES `remote_sequence_file_pair_files_AUD` WRITE;
/*!40000 ALTER TABLE `remote_sequence_file_pair_files_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_sequence_file_pair_files_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_sequence_file_properties`
--

LOCK TABLES `remote_sequence_file_properties` WRITE;
/*!40000 ALTER TABLE `remote_sequence_file_properties` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_sequence_file_properties` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `remote_sequence_file_properties_AUD`
--

LOCK TABLES `remote_sequence_file_properties_AUD` WRITE;
/*!40000 ALTER TABLE `remote_sequence_file_properties_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_sequence_file_properties_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sample`
--

LOCK TABLES `sample` WRITE;
/*!40000 ALTER TABLE `sample` DISABLE KEYS */;
/*!40000 ALTER TABLE `sample` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sample_AUD`
--

LOCK TABLES `sample_AUD` WRITE;
/*!40000 ALTER TABLE `sample_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sample_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
  `sequencing_run_id` bigint(20) DEFAULT NULL,
  `fastqc_analysis_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_SEQUENCE_FILE_FILEPATH` (`file_path`),
  KEY `FK_SEQUENCE_FILE_SEQUENCING_RUN` (`sequencing_run_id`),
  KEY `FK_SEQUENCE_FILE_FASTQC` (`fastqc_analysis_id`),
  CONSTRAINT `FK_SEQUENCE_FILE_FASTQC` FOREIGN KEY (`fastqc_analysis_id`) REFERENCES `analysis_fastqc` (`id`),
  CONSTRAINT `FK_SEQUENCE_FILE_SEQUENCING_RUN` FOREIGN KEY (`sequencing_run_id`) REFERENCES `sequencing_run` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence_file`
--

LOCK TABLES `sequence_file` WRITE;
/*!40000 ALTER TABLE `sequence_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_file` ENABLE KEYS */;
UNLOCK TABLES;

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
  `sequencing_run_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SEQFILE_REVISION` (`REV`),
  CONSTRAINT `FK_SEQFILE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence_file_AUD`
--

LOCK TABLES `sequence_file_AUD` WRITE;
/*!40000 ALTER TABLE `sequence_file_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_file_AUD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequence_file_pair`
--

DROP TABLE IF EXISTS `sequence_file_pair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_pair` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `assembled_genome` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `assembled_genome` (`assembled_genome`),
  CONSTRAINT `FK_SEQUENCE_FILE_PAIR_ASSEMBLED_GENOME` FOREIGN KEY (`assembled_genome`) REFERENCES `assembled_genome_analysis` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence_file_pair`
--

LOCK TABLES `sequence_file_pair` WRITE;
/*!40000 ALTER TABLE `sequence_file_pair` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_file_pair` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequence_file_pair_AUD`
--

DROP TABLE IF EXISTS `sequence_file_pair_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_file_pair_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `assembled_genome` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SEQUENCE_FILE_PAIR_AUD` (`REV`),
  CONSTRAINT `FK_SEQUENCE_FILE_PAIR_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence_file_pair_AUD`
--

LOCK TABLES `sequence_file_pair_AUD` WRITE;
/*!40000 ALTER TABLE `sequence_file_pair_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_file_pair_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sequence_file_pair_files`
--

LOCK TABLES `sequence_file_pair_files` WRITE;
/*!40000 ALTER TABLE `sequence_file_pair_files` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_file_pair_files` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sequence_file_pair_files_AUD`
--

LOCK TABLES `sequence_file_pair_files_AUD` WRITE;
/*!40000 ALTER TABLE `sequence_file_pair_files_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_file_pair_files_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sequence_file_properties`
--

LOCK TABLES `sequence_file_properties` WRITE;
/*!40000 ALTER TABLE `sequence_file_properties` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_file_properties` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sequence_file_properties_AUD`
--

LOCK TABLES `sequence_file_properties_AUD` WRITE;
/*!40000 ALTER TABLE `sequence_file_properties_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_file_properties_AUD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequencefile_sample`
--

DROP TABLE IF EXISTS `sequencefile_sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequencefile_sample` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createdDate` datetime NOT NULL,
  `sample_id` bigint(20) DEFAULT NULL,
  `sequencefile_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_SEQUENCEFILE_SAMPLE_FILE` (`sequencefile_id`),
  KEY `FK_SEQFILE_SAMPLE_SAMPLE` (`sample_id`),
  CONSTRAINT `FK_SEQFILE_SAMPLE_SAMPLE` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`),
  CONSTRAINT `FK_SEQFILE_SAMPLE_SEQFILE` FOREIGN KEY (`sequencefile_id`) REFERENCES `sequence_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequencefile_sample`
--

LOCK TABLES `sequencefile_sample` WRITE;
/*!40000 ALTER TABLE `sequencefile_sample` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequencefile_sample` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequencefile_sample_AUD`
--

DROP TABLE IF EXISTS `sequencefile_sample_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequencefile_sample_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `createdDate` datetime DEFAULT NULL,
  `sample_id` bigint(20) DEFAULT NULL,
  `sequencefile_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SEQFILE_SAMPLE_REVISION` (`REV`),
  CONSTRAINT `FK_SEQFILE_SAMPLE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequencefile_sample_AUD`
--

LOCK TABLES `sequencefile_sample_AUD` WRITE;
/*!40000 ALTER TABLE `sequencefile_sample_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequencefile_sample_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sequencing_run`
--

LOCK TABLES `sequencing_run` WRITE;
/*!40000 ALTER TABLE `sequencing_run` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequencing_run` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `sequencing_run_AUD`
--

LOCK TABLES `sequencing_run_AUD` WRITE;
/*!40000 ALTER TABLE `sequencing_run_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequencing_run_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `system_role`
--

LOCK TABLES `system_role` WRITE;
/*!40000 ALTER TABLE `system_role` DISABLE KEYS */;
INSERT INTO `system_role` VALUES ('An administrative user in the system.','ROLE_ADMIN'),('A manager role in the system.','ROLE_MANAGER'),('A sequencer produces sequence data for storage in the archive.','ROLE_SEQUENCER'),('A basic user in the system.','ROLE_USER');
/*!40000 ALTER TABLE `system_role` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `system_role_AUD`
--

LOCK TABLES `system_role_AUD` WRITE;
/*!40000 ALTER TABLE `system_role_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_role_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `tool_execution`
--

LOCK TABLES `tool_execution` WRITE;
/*!40000 ALTER TABLE `tool_execution` DISABLE KEYS */;
/*!40000 ALTER TABLE `tool_execution` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `tool_execution_parameters`
--

LOCK TABLES `tool_execution_parameters` WRITE;
/*!40000 ALTER TABLE `tool_execution_parameters` DISABLE KEYS */;
/*!40000 ALTER TABLE `tool_execution_parameters` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `tool_execution_prev_steps`
--

LOCK TABLES `tool_execution_prev_steps` WRITE;
/*!40000 ALTER TABLE `tool_execution_prev_steps` DISABLE KEYS */;
/*!40000 ALTER TABLE `tool_execution_prev_steps` ENABLE KEYS */;
UNLOCK TABLES;

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
  KEY `FK_USER_SYSTEM_ROLE` (`system_role`),
  CONSTRAINT `FK_USER_SYSTEM_ROLE` FOREIGN KEY (`system_role`) REFERENCES `system_role` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2016-03-02 16:14:30','\0','admin@example.org','','Administrator','Administrator','en','2016-03-02 16:14:30','$2a$10$yvzFLxWA9m2wNQmHpJtWT.MRZv8qV8Mo3EMB6HTkDnUbi9aBrbWWW','867-5309','admin','ROLE_ADMIN');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user_AUD`
--

LOCK TABLES `user_AUD` WRITE;
/*!40000 ALTER TABLE `user_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user_group`
--

LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user_group_AUD`
--

LOCK TABLES `user_group_AUD` WRITE;
/*!40000 ALTER TABLE `user_group_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
  KEY `FK_USER_GROUP_MEMBER_USER` (`user_id`),
  KEY `FK_USER_GROUP_MEMBER_GROUP` (`group_id`),
  CONSTRAINT `FK_USER_GROUP_MEMBER_GROUP` FOREIGN KEY (`group_id`) REFERENCES `user_group` (`id`),
  CONSTRAINT `FK_USER_GROUP_MEMBER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group_member`
--

LOCK TABLES `user_group_member` WRITE;
/*!40000 ALTER TABLE `user_group_member` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group_member` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user_group_member_AUD`
--

LOCK TABLES `user_group_member_AUD` WRITE;
/*!40000 ALTER TABLE `user_group_member_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group_member_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user_group_project`
--

LOCK TABLES `user_group_project` WRITE;
/*!40000 ALTER TABLE `user_group_project` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group_project` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user_group_project_AUD`
--

LOCK TABLES `user_group_project_AUD` WRITE;
/*!40000 ALTER TABLE `user_group_project_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group_project_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `workflow_named_parameter_values`
--

LOCK TABLES `workflow_named_parameter_values` WRITE;
/*!40000 ALTER TABLE `workflow_named_parameter_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `workflow_named_parameter_values` ENABLE KEYS */;
UNLOCK TABLES;

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

--
-- Dumping data for table `workflow_named_parameters`
--

LOCK TABLES `workflow_named_parameters` WRITE;
/*!40000 ALTER TABLE `workflow_named_parameters` DISABLE KEYS */;
/*!40000 ALTER TABLE `workflow_named_parameters` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-03-02 16:19:19
