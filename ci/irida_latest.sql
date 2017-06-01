-- MySQL dump 10.15  Distrib 10.0.29-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: localhost
-- ------------------------------------------------------
-- Server version	10.0.29-MariaDB-0ubuntu0.16.04.1

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
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  `CONTEXTS` varchar(255) DEFAULT NULL,
  `LABELS` varchar(255) DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DATABASECHANGELOG`
--

LOCK TABLES `DATABASECHANGELOG` WRITE;
/*!40000 ALTER TABLE `DATABASECHANGELOG` DISABLE KEYS */;
INSERT INTO `DATABASECHANGELOG` VALUES ('1','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/1.0/initialschema-v1.0.xml','2017-05-31 12:57:36',1,'EXECUTED','7:a2ee173b2f15108f56694acf8c064e59','createTable tableName=system_role; createTable tableName=user; createTable tableName=project; createTable tableName=project_user; createTable tableName=sample; createTable tableName=project_sample; createTable tableName=sequence_file; createTable ...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('initial-data-set-roles-and-admin','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/1.0/initialdata-v1.0.xml','2017-05-31 12:57:37',2,'EXECUTED','7:13f0fe666732635de5cc289e7399f866','insert tableName=system_role; insert tableName=system_role; insert tableName=system_role; insert tableName=system_role; insert tableName=user','',NULL,'3.5.1',NULL,NULL,'6253447650'),('2','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/1.1/updates-v1.1.xml','2017-05-31 12:57:48',3,'EXECUTED','7:0e92e8c480456b96ccf27a2525fc1460','dropTable tableName=sequencefile_project; dropTable tableName=sequencefile_project_AUD; renameTable newTableName=miseq_run, oldTableName=miseqRun; renameTable newTableName=miseq_run_AUD, oldTableName=miseqRun_AUD; dropColumn columnName=enabled, ta...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('3','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/1.1/rename-constraints.xml','2017-05-31 12:58:06',4,'EXECUTED','7:aec3a557f08cc292d0830b4ddc3a4153','dropUniqueConstraint constraintName=UK_3qbj4kdbey8f8wgabcel8i7io, tableName=system_role; addUniqueConstraint constraintName=UK_SYSTEM_ROLE_NAME, tableName=system_role; dropForeignKeyConstraint baseTableName=project_user, constraintName=FK_ptwhmsh2...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('add-sequencer-role','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/1.2/insert-sequencer-role.xml','2017-05-31 12:58:06',5,'EXECUTED','7:84b47370f1b22adb29d4275ada2e09b9','insert tableName=system_role','',NULL,'3.5.1',NULL,NULL,'6253447650'),('drop-client-role','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/1.3/drop-client-role.xml','2017-05-31 12:58:06',6,'EXECUTED','7:cd2ef4aca94030052b3922422b6b8df7','delete tableName=system_role','',NULL,'3.5.1',NULL,NULL,'6253447650'),('password_reset','josh','classpath:ca/corefacility/bioinformatics/irida/database/changesets/1.5/password_reset.xml','2017-05-31 12:58:07',7,'EXECUTED','7:95e3a556026d31b3c790f037b630cf53','createTable tableName=password_reset','',NULL,'3.5.1',NULL,NULL,'6253447650'),('data-model-cleanup','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/data-model-cleanup.xml','2017-05-31 12:58:11',8,'EXECUTED','7:51b7885c88e8e76ea0b2a82ca44b7e28','addForeignKeyConstraint baseTableName=password_reset, constraintName=FK_PASSWORD_RESET_USER, referencedTableName=user; addColumn tableName=sequence_file; addForeignKeyConstraint baseTableName=sequence_file, constraintName=FK_SEQUENCE_FILE_MISEQ_RU...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('remote-apis','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/remote-apis.xml','2017-05-31 12:58:13',9,'EXECUTED','7:fe2c43e14c532b436d69a4ee67911151','createTable tableName=remote_api; createTable tableName=remote_api_AUD; createTable tableName=remote_api_token; addUniqueConstraint constraintName=UK_remote_api_token_user, tableName=remote_api_token; createTable tableName=remote_api_token_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('user-groups','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/user-groups.xml','2017-05-31 12:58:14',10,'EXECUTED','7:11b8efeb2d94d44f18812e4c9286bc45','createTable tableName=logicalGroup; createTable tableName=logicalGroup_AUD; createTable tableName=user_group; createTable tableName=user_group_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('sequencer-agnostic-files','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/sequencer-agnostic-files.xml','2017-05-31 12:58:22',11,'EXECUTED','7:a89df3cd1e6ef073883a5eb6ec7486ce','createTable tableName=sequence_file_properties; addUniqueConstraint constraintName=UK_SEQUENCE_FILE_PROPERTY_KEY, tableName=sequence_file_properties; addForeignKeyConstraint baseTableName=sequence_file_properties, constraintName=FK_SEQUENCE_FILE_P...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('oauth-clientid-envers','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/oauth-clientid-envers.xml','2017-05-31 12:58:22',12,'EXECUTED','7:73a66cf755da9c722d00a630f4a1d2b1','addColumn tableName=Revisions','',NULL,'3.5.1',NULL,NULL,'6253447650'),('samples','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/samples.xml','2017-05-31 12:58:25',13,'EXECUTED','7:d38600b693479c8974757e62132901e3','renameColumn newColumnName=sequencerSampleId, oldColumnName=externalSampleId, tableName=sample; renameColumn newColumnName=sequencerSampleId, oldColumnName=externalSampleId, tableName=sample_AUD; addColumn tableName=sample; addColumn tableName=sam...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('client-details','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/client-details.xml','2017-05-31 12:58:31',14,'EXECUTED','7:b791c2470ff70894ac3902761fd21998','createTable tableName=client_role; createTable tableName=client_role_AUD; createTable tableName=client_details; createTable tableName=client_details_AUD; createTable tableName=client_details_additional_information; createTable tableName=client_det...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('add-not-null-constraints','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/add-not-null-constraints.xml','2017-05-31 12:58:39',15,'EXECUTED','7:beed1eb900c6f28de86b516b3259667f','addNotNullConstraint columnName=createdDate, tableName=miseq_run; addNotNullConstraint columnName=createdDate, tableName=project; addNotNullConstraint columnName=createdDate, tableName=project_sample; addNotNullConstraint columnName=createdDate, t...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('user-project-organization','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/organization.xml','2017-05-31 12:58:43',16,'EXECUTED','7:39af431dd7be2ee6371176ac2fd64334','createTable tableName=organization; addColumn tableName=project; addColumn tableName=sample; createTable tableName=organization_AUD; addColumn tableName=project_AUD; addColumn tableName=sample_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('add-organism-project','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/add-organism-project.xml','2017-05-31 12:58:44',17,'EXECUTED','7:d634f0d6c1e5064a25544495747cfd9e','addColumn tableName=project; addColumn tableName=project_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('sequencer-type-runs','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/sequencer-type-runs.xml','2017-05-31 12:58:51',18,'EXECUTED','7:494394aa222e4e07e764bc960c62839b','createTable tableName=sequencing_run; createTable tableName=sequencing_run_AUD; sql; sql; dropForeignKeyConstraint baseTableName=sequence_file, constraintName=FK_SEQUENCE_FILE_MISEQ_RUN; renameColumn newColumnName=sequencingRun_id, oldColumnName=m...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('related-projects','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/related-projects.xml','2017-05-31 12:58:52',19,'EXECUTED','7:dce5ad19c3fd2d8766093b87297c4650','createTable tableName=related_project; createTable tableName=related_project_AUD; addUniqueConstraint constraintName=UK_RELATED_PROJECT_SUBJECT_OBJECT, tableName=related_project','',NULL,'3.5.1',NULL,NULL,'6253447650'),('add-analysis-types','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/add-analysis-types.xml','2017-05-31 12:59:08',20,'EXECUTED','7:49cb060850a06d07da66ca7d2c59c865','createTable tableName=analysis; createTable tableName=analysis_properties; createTable tableName=analysis_AUD; createTable tableName=analysis_properties_AUD; createTable tableName=analysis_fastqc; createTable tableName=analysis_fastqc_AUD; addFore...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('drop-user-revision-fk','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/drop-user-revision-fk.xml','2017-05-31 12:59:08',21,'EXECUTED','7:9f4f5f9398a3bd0d9ceb6975b61d11f7','dropForeignKeyConstraint baseTableName=Revisions, constraintName=FK_REVISION_CREATED_BY; dropForeignKeyConstraint baseTableName=Revisions, constraintName=FK_REVISIONS_CLIENT_DETAILS','',NULL,'3.5.1',NULL,NULL,'6253447650'),('liquibase-cleanup','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/liquibase-cleanup.xml','2017-05-31 12:59:11',22,'EXECUTED','7:4b7d14dcfb9c613da6e0731c1e0917bb','addNotNullConstraint columnName=id, tableName=password_reset; addNotNullConstraint columnName=filePath, tableName=sequence_file; dropPrimaryKey tableName=system_role_AUD; dropColumn columnName=id, tableName=system_role_AUD; addPrimaryKey tableName...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('remoteapi-dates','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/remoteapi-dates.xml','2017-05-31 12:59:12',23,'EXECUTED','7:27f174534f370f72498b451e3bba1b3b','addColumn tableName=remote_api; addColumn tableName=remote_api_AUD; sql; addNotNullConstraint columnName=createdDate, tableName=remote_api','',NULL,'3.5.1',NULL,NULL,'6253447650'),('project-reference-file','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/project-reference-file.xml','2017-05-31 12:59:14',24,'EXECUTED','7:94fef29ca276d445e296e3f2167ed3d0','createTable tableName=reference_file; createTable tableName=reference_file_AUD; createTable tableName=project_referencefile; addUniqueConstraint constraintName=UK_PROJECT_REFERENCEFILE, tableName=project_referencefile; createTable tableName=projec...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('analysis-output-file','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/analysis-output-file.xml','2017-05-31 12:59:18',25,'EXECUTED','7:8ccddab60ec66dbeccc253df94b7feff','createTable tableName=analysis_output_file; createTable tableName=analysis_output_file_AUD; addColumn tableName=analysis_fastqc; addColumn tableName=analysis_fastqc_AUD; createTable tableName=analysis_phylogenomicspipeline; createTable tableName=a...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('oauth-token','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/oauth-token.xml','2017-05-31 12:59:19',26,'EXECUTED','7:5971e13a719f0a77946bc0a7f01bb089','createTable tableName=oauth_access_token; addForeignKeyConstraint baseTableName=oauth_access_token, constraintName=FK_OAUTH_TOKEN_CLIENT_DETAILS, referencedTableName=client_details','',NULL,'3.5.1',NULL,NULL,'6253447650'),('remote-api-name','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/remote-api-name.xml','2017-05-31 12:59:22',27,'EXECUTED','7:9a9d06f774574ec8d3dd18706e093c01','addColumn tableName=remote_api; addColumn tableName=remote_api_AUD; dropColumn columnName=serviceURI, tableName=remote_api; addColumn tableName=remote_api; addUniqueConstraint constraintName=UK_REMOTE_API_SERVICEURI, tableName=remote_api; dropColu...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('analysis-submission','aaron.petkau, tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/analysis-submission.xml','2017-05-31 12:59:28',28,'EXECUTED','7:ba9bfa8401244d6df56e4395f8e38a57','createTable tableName=remote_workflow; createTable tableName=remote_workflow_AUD; createTable tableName=remote_workflow_phylogenomics; createTable tableName=analysis_submission; createTable tableName=analysis_submission_AUD; createTable tableName=...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('samples-collection-date','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/samples-collection-date.xml','2017-05-31 12:59:30',29,'EXECUTED','7:4d841453516f9028b7812e84d2ac9d02','dropColumn columnName=collectionDate, tableName=sample; addColumn tableName=sample; dropColumn columnName=collectionDate, tableName=sample_AUD; addColumn tableName=sample_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('remote-related-projects','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/remote-related-projects.xml','2017-05-31 12:59:31',30,'EXECUTED','7:19c9f38751cb3eb48379cad63339454f','createTable tableName=remote_related_project; createTable tableName=remote_related_project_AUD; addUniqueConstraint constraintName=UK_REMOTE_RELATED_PROJECT, tableName=remote_related_project','',NULL,'3.5.1',NULL,NULL,'6253447650'),('snapshots','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/snapshots.xml','2017-05-31 12:59:35',31,'EXECUTED','7:57b9ad86facc711374219cadd7221576','createTable tableName=project_snapshot; createTable tableName=sample_snapshot; createTable tableName=sequence_file_snapshot; createTable tableName=snapshot_sequence_file_properties; createTable tableName=rest_links; createTable tableName=rest_link...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('project-events','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/2.0/project-events.xml','2017-05-31 12:59:36',32,'EXECUTED','7:ffcadaaa6e6d43e235474100c80d0493','createTable tableName=project_event','',NULL,'3.5.1',NULL,NULL,'6253447650'),('sequencing-run-update','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/sequencing-run-update.xml','2017-05-31 12:59:38',33,'EXECUTED','7:a5a4341adfdd3f72cd9f36c46c216e5d','addColumn tableName=sequencing_run; addNotNullConstraint columnName=layout_type, tableName=sequencing_run; addNotNullConstraint columnName=upload_status, tableName=sequencing_run; addColumn tableName=miseq_run; addColumn tableName=sequencing_run_A...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('update-analysis-workflows','apetkau,fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/replace-analysis-workflows.xml','2017-05-31 12:59:45',34,'EXECUTED','7:835de105c882abb9c260c6bf80476981','dropForeignKeyConstraint baseTableName=analysis_submission_phylogenomics_AUD, constraintName=FK_ANALYSIS_SUBMISSION_PHYLOGENOMICS_REVISION; dropTable tableName=analysis_submission_phylogenomics_AUD; dropForeignKeyConstraint baseTableName=analysis_...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('sequence-file-pairs','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/sequence-file-pairs.xml','2017-05-31 12:59:47',35,'EXECUTED','7:fd9dda495a3efadd0d0d6155b4457670','createTable tableName=sequence_file_pair; createTable tableName=sequence_file_pair_AUD; createTable tableName=sequence_file_pair_files; createTable tableName=sequence_file_pair_files_AUD; addUniqueConstraint constraintName=UK_SEQUENCE_FILE_PAIR, t...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('analysis-submission-updates','apetkau','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/analysis-submission-updates.xml','2017-05-31 12:59:52',36,'EXECUTED','7:9dd0d05e9567ff46ebb410b0868534e0','renameTable newTableName=analysis_submission_sequence_file_single, oldTableName=analysis_submission_sequence_file; renameTable newTableName=analysis_submission_sequence_file_single_AUD, oldTableName=analysis_submission_sequence_file_AUD; createTab...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('workflow-provenance','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/workflow-provenance.xml','2017-05-31 12:59:58',37,'EXECUTED','7:5c4d02cdeeea19745d144901eb72abef','createTable tableName=tool_execution; createTable tableName=tool_execution_parameters; createTable tableName=tool_execution_prev_steps; renameColumn newColumnName=created_date, oldColumnName=createdDate, tableName=analysis_output_file; addNotNullC...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('analysis-assembly-annotation','aaron','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/analysis-assembly-annotation.xml','2017-05-31 12:59:58',38,'EXECUTED','7:68d61a8bf191f9c172893fc9b33af681','createTable tableName=analysis_assemblyannotation','',NULL,'3.5.1',NULL,NULL,'6253447650'),('fix-project-referencefile-fk','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.0.0/fix-project-referencefile-fk.xml','2017-05-31 12:59:59',39,'EXECUTED','7:b707b26d4e1e4dd31dd4eb203724bae5','dropForeignKeyConstraint baseTableName=project_referencefile, constraintName=FK_PROJECT_REFERENCEFILE_REFERENCEFILE; renameColumn newColumnName=reference_file_id, oldColumnName=referenceFile_id, tableName=project_referencefile; renameColumn newCol...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('drop-analysis-audit-tables','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/drop-analysis-audit-tables.xml','2017-05-31 13:00:03',40,'EXECUTED','7:a849f49cc6d7ea0b94f95202d895baff','dropTable tableName=analysis_phylogenomicspipeline_AUD; dropTable tableName=analysis_fastqc_AUD; dropTable tableName=analysis_AUD; dropTable tableName=analysis_fastqc_overrepresented_sequence_AUD; dropTable tableName=analysis_output_file_AUD; drop...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('drop-analysis-sequence-files','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/drop-analysis-sequence-files.xml','2017-05-31 13:00:08',41,'EXECUTED','7:055984b97782b8160f88d833eb7ea065','renameColumn newColumnName=created_date, oldColumnName=createdDate, tableName=sequence_file; addNotNullConstraint columnName=created_date, tableName=sequence_file; renameColumn newColumnName=modified_date, oldColumnName=modifiedDate, tableName=seq...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('analysis-submission-cleanup','aaron','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/analysis-submission-cleanup.xml','2017-05-31 13:00:09',42,'EXECUTED','7:b454e91b1972f550d1a6316d0d32267e','addColumn tableName=analysis_submission; addColumn tableName=analysis_submission_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('unique-sequencefile-sample','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/unique-sequencefile-sample.xml','2017-05-31 13:00:10',43,'EXECUTED','7:60af0f3356df4887f40362348576f48e','addUniqueConstraint constraintName=UK_SEQUENCEFILE_SAMPLE_FILE, tableName=sequencefile_sample','',NULL,'3.5.1',NULL,NULL,'6253447650'),('oauth-auto-approvable-scopes.xml','joelt','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/oauth-auto-approvable-scopes.xml','2017-05-31 13:00:11',44,'EXECUTED','7:2e52e56959df663a3c4cc4c5a2115860','createTable tableName=client_details_auto_approvable_scope; createTable tableName=client_details_auto_approvable_scope_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('remove-snapshots','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.1.0/remove-snapshots.xml','2017-05-31 13:00:12',45,'EXECUTED','7:f8ccdf887e785c23b7d553c5ffb4fa39','dropTable tableName=remote_sequence_file_snapshot; dropTable tableName=remote_sample_snapshot; dropTable tableName=remote_project_snapshot; dropTable tableName=snapshot_sequence_file_properties; dropTable tableName=snapshot_sample_snapshot; dropTa...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('analysis-submission-cleanup','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.2.0/command-line-not-nullable.xml','2017-05-31 13:00:13',46,'EXECUTED','7:9630b5ffaeed9fcc0cef13bd1d347aad','addNotNullConstraint columnName=command_line, tableName=tool_execution','',NULL,'3.5.1',NULL,NULL,'6253447650'),('analysis-assembly-annotation-collection','aaron','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.2.0/assembly-annotation-collection.xml','2017-05-31 13:00:13',47,'EXECUTED','7:279e4254a8767a77e29ef75dd91dc0d9','createTable tableName=analysis_assemblyannotation_collection','',NULL,'3.5.1',NULL,NULL,'6253447650'),('remote-sequence-file','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.2.0/remote-sequence-file.xml','2017-05-31 13:00:18',48,'EXECUTED','7:cf68366870516e1ef18fb35737437eb2','createTable tableName=remote_sequence_file; addUniqueConstraint constraintName=UK_REMOTE_SEQUENCE_FILE_FILE, tableName=remote_sequence_file; createTable tableName=remote_sequence_file_AUD; createTable tableName=remote_sequence_file_properties; cre...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('assembled-genome','aaron','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.3.0/assembled-genome-analysis.xml','2017-05-31 13:00:20',49,'EXECUTED','7:b6c8921a1f35bab4086bd36f057ea0d1','createTable tableName=assembled_genome_analysis; addColumn tableName=sequence_file_pair; addColumn tableName=sequence_file_pair_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('update-project-modified-date','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.3.0/update-project-modified-date.xml','2017-05-31 13:00:21',50,'EXECUTED','7:4686cf6974916b3d8bd020c09253c0bf','dropColumn columnName=modifiedDate, tableName=project_AUD; sql','',NULL,'3.5.1',NULL,NULL,'6253447650'),('fix-workflow-id-column-type','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.3.0/fix-workflow-id-column-type.xml','2017-05-31 13:00:22',51,'EXECUTED','7:5672fc8508a7dc289a2ebb381dce0562','modifyDataType columnName=workflow_id, tableName=workflow_named_parameters; addNotNullConstraint columnName=workflow_id, tableName=workflow_named_parameters; sql','',NULL,'3.5.1',NULL,NULL,'6253447650'),('email-subscription','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.4.0/email-subscription.xml','2017-05-31 13:00:23',52,'EXECUTED','7:bc8a4d0ad7aafe682540c185cecb2a69','addColumn tableName=project_user; addColumn tableName=project_user_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('drop-sample-sequencer-id','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.4.0/drop-sample-sequencer-id.xml','2017-05-31 13:00:24',53,'EXECUTED','7:404cd46f14fef5ca16fbd2fe9f705eb9','dropColumn columnName=sequencerSampleId, tableName=sample; dropColumn columnName=sequencerSampleId, tableName=sample_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('oauth-unique-key','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.4.0/oauth-unique-key.xml','2017-05-31 13:00:24',54,'EXECUTED','7:b5740bb662ad8437ea3b5f20d32f9d13','delete tableName=oauth_access_token; addUniqueConstraint constraintName=UK_OAUTH_AUTHENTICATION, tableName=oauth_access_token','',NULL,'3.5.1',NULL,NULL,'6253447650'),('drop-group-host-organization','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/drop-group-host-organization.xml','2017-05-31 13:00:37',55,'EXECUTED','7:e2279a6805a875b4185b088fc9d3a596','dropTable tableName=user_group; dropTable tableName=user_group_AUD; dropTable tableName=logicalGroup; dropTable tableName=logicalGroup_AUD; dropColumn columnName=cultureCollection, tableName=sample; dropColumn columnName=genotype, tableName=sample...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('schema-inconsistencies-part-revisions-index-liquibase','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2017-05-31 13:00:37',56,'EXECUTED','7:47353a34583f68d134098812ddb9ecab','dropIndex indexName=FK_REVISION_CREATED_BY, tableName=Revisions','',NULL,'3.5.1',NULL,NULL,'6253447650'),('schema-inconsistencies-part-revisions-index-hibernate','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2017-05-31 13:00:37',57,'MARK_RAN','7:1c414137e437d3daff4583f073f07fb5','dropIndex indexName=FK_9h58753k8cr3e19i4buy9dboc, tableName=Revisions','',NULL,'3.5.1',NULL,NULL,'6253447650'),('schema-inconsistencies-part-miseq-run-aud-fk','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2017-05-31 13:00:37',58,'EXECUTED','7:83b90315f73d87a1279423f555218f07','dropForeignKeyConstraint baseTableName=miseq_run_AUD, constraintName=FK_MISEQ_RUN_REVISION','',NULL,'3.5.1',NULL,NULL,'6253447650'),('schema-inconsistencies-part-miseq-run-aud-liquibase','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2017-05-31 13:00:38',59,'EXECUTED','7:a5fe1685c20a0a93a555807e644d57d0','dropIndex indexName=FK_MISEQ_RUN_REVISION, tableName=miseq_run_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('schema-inconsistencies-part-miseq-run-aud-hibernate','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2017-05-31 13:00:38',60,'MARK_RAN','7:07c9ecf0bea89e5c093e048bf127ed62','dropIndex indexName=FK_oxvcne374i54prd0c89mud85b, tableName=miseq_run_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('schema-inconsistencies','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/schema-inconsistencies.xml','2017-05-31 13:00:52',61,'EXECUTED','7:29590a1337c6c6464c6c3eda9c5fc405','dropIndex indexName=FK_REVISIONS_CLIENT_DETAILS, tableName=Revisions; addForeignKeyConstraint baseTableName=miseq_run_AUD, constraintName=FK_MISEQ_RUN_REVISION, referencedTableName=sequencing_run_AUD; dropColumn columnName=modifiedDate, tableName=...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('ncbi-upload','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.5.0/ncbi-upload.xml','2017-05-31 13:00:54',62,'EXECUTED','7:9c543b3aaefe7847f9b8dfe94faafca4','createTable tableName=ncbi_export_submission; createTable tableName=ncbi_export_biosample; createTable tableName=ncbi_export_submission_biosample; createTable tableName=ncbi_export_biosample_sequence_file; createTable tableName=ncbi_export_biosamp...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('user-group','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.6.0/user-group.xml','2017-05-31 13:00:58',63,'EXECUTED','7:830eca98c251f6c1f2e506f425419c0a','createTable tableName=user_group; createTable tableName=user_group_AUD; createTable tableName=user_group_member; createTable tableName=user_group_member_AUD; createTable tableName=user_group_project; createTable tableName=user_group_project_AUD; a...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('sequencing-objects','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.7.0/sequencing-object.xml','2017-05-31 13:01:12',64,'EXECUTED','7:e42e5498ae82b57d5782d68ecd169b9a','createTable tableName=sequencing_object; createTable tableName=sequencing_object_AUD; sql; dropForeignKeyConstraint baseTableName=sequence_file_pair, constraintName=FK_SEQUENCE_FILE_PAIR_ASSEMBLED_GENOME; dropColumn columnName=created_date, tableN...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('sample-sequencingobject','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.7.0/sample-sequencingobject.xml','2017-05-31 13:01:14',65,'EXECUTED','7:dc5edf7e83bce7650a6a12d81048ef8f','createTable tableName=sample_sequencingobject; addUniqueConstraint constraintName=UK_SEQUENCEOBJECT_SAMPLE_FILE, tableName=sample_sequencingobject; createTable tableName=sample_sequencingobject_AUD; sql; sql; sql; sql; dropTable tableName=sequence...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('unpaired-snapshots','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.7.0/unpaired-snapshots.xml','2017-05-31 13:01:16',66,'EXECUTED','7:bd79e0e222bf5890cb910891bc366832','addColumn tableName=remote_sequence_file_pair; addColumn tableName=remote_sequence_file_pair_AUD; createTable tableName=remote_sequence_file_single; createTable tableName=remote_sequence_file_single_AUD; dropForeignKeyConstraint baseTableName=anal...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('analysis-description','john','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.7.0/analysis-description.xml','2017-05-31 13:01:17',67,'EXECUTED','7:51da3d79ad1ccae643b7aa3d00d38a00','addColumn tableName=analysis_submission; addColumn tableName=analysis_submission_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('automated-assembly','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.7.0/automated-assembly.xml','2017-05-31 13:01:21',68,'EXECUTED','7:54a624895bdcb8bda165b32094256b24','dropForeignKeyConstraint baseTableName=sequencing_object, constraintName=FK_SEQUENCING_OBJECT_ASSEMBLED_GENOME; dropColumn columnName=assembled_genome, tableName=sequencing_object; dropColumn columnName=assembled_genome, tableName=sequencing_objec...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('announcement','john','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.8.0/announcement.xml','2017-05-31 13:01:24',69,'EXECUTED','7:8a8da6bd4c61eaded652a13a4d8965ac','createTable tableName=announcement; createTable tableName=announcement_user; addUniqueConstraint tableName=announcement_user; addForeignKeyConstraint baseTableName=announcement_user, constraintName=FK_USER_ID_ANNOUNCEMENT, referencedTableName=user...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('sample-removal-event','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/3.8.0/sample-removal-event.xml','2017-05-31 13:01:25',70,'EXECUTED','7:45455446e23e1847c0d3cca4b48c9599','addColumn tableName=project_event','',NULL,'3.5.1',NULL,NULL,'6253447650'),('relative-paths','fbristow','classpath:ca/corefacility/bioinformatics/irida/database/changesets/0.12.0/relative-paths.xml','2017-05-31 13:01:25',71,'EXECUTED','7:2a6e5a8edce48842fdbfe696787ca4e7','customChange','',NULL,'3.5.1',NULL,NULL,'6253447650'),('remote-status','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/0.12.0/remote-status.xml','2017-05-31 13:01:33',72,'EXECUTED','7:644608d05877a675b3fdcbe879e5e08c','createTable tableName=remote_status; createTable tableName=remote_status_AUD; addColumn tableName=project; addColumn tableName=project_AUD; addColumn tableName=sample; addColumn tableName=sample_AUD; addColumn tableName=sequencing_object; addColum...','',NULL,'3.5.1',NULL,NULL,'6253447650'),('refresh-token','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/0.12.0/refresh-token.xml','2017-05-31 13:01:34',73,'EXECUTED','7:04177c06a2290e5e3bd92bcf7c43cfc7','createTable tableName=oauth_refresh_token; addColumn tableName=remote_api_token; addColumn tableName=remote_api_token_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('sequencing-run-by-user','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/0.14.0/sequencing-run-by-user.xml','2017-05-31 13:01:36',74,'EXECUTED','7:2a962c52fde3a162413255ca7573d04f','addColumn tableName=sequencing_run; addColumn tableName=sequencing_run_AUD; sql','',NULL,'3.5.1',NULL,NULL,'6253447650'),('analysis-project-share','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/0.14.0/analysis-project-share.xml','2017-05-31 13:01:37',75,'EXECUTED','7:5f874f54421e71a7bee5ba7cef91c153','createTable tableName=project_analysis_submission; addUniqueConstraint constraintName=UK_PROJECT_ANALYSIS, tableName=project_analysis_submission; createTable tableName=project_analysis_submission_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('file-checksum','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/0.15.0/file-checksum.xml','2017-05-31 13:01:38',76,'EXECUTED','7:662af6383adba5b9bf54bf417bc419f6','addColumn tableName=sequence_file; addColumn tableName=sequence_file_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650'),('qc-metrics','tom','classpath:ca/corefacility/bioinformatics/irida/database/changesets/0.15.0/qc-metrics.xml','2017-05-31 13:01:40',77,'EXECUTED','7:579fd4175e3814489b1ccd2a48bfbd64','createTable tableName=qc_entry; addColumn tableName=project; addColumn tableName=project_AUD; sql; sql; sql; sql','',NULL,'3.5.1',NULL,NULL,'6253447650'),('sistr','aaron','classpath:ca/corefacility/bioinformatics/irida/database/changesets/0.16.0/sistr.xml','2017-05-31 13:01:43',78,'EXECUTED','7:071153ff5ed45a482b0931e55338e2f7','createTable tableName=analysis_sistr_typing; addColumn tableName=sequencing_object; addColumn tableName=sequencing_object_AUD; addColumn tableName=project; addColumn tableName=project_AUD','',NULL,'3.5.1',NULL,NULL,'6253447650');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `analysis_properties`
--

LOCK TABLES `analysis_properties` WRITE;
/*!40000 ALTER TABLE `analysis_properties` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_properties` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `analysis_sistr_typing`
--

DROP TABLE IF EXISTS `analysis_sistr_typing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `analysis_sistr_typing` (
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_ANALYSIS_SISTR_TYPING_ANALYSIS` FOREIGN KEY (`id`) REFERENCES `analysis` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `analysis_sistr_typing`
--

LOCK TABLES `analysis_sistr_typing` WRITE;
/*!40000 ALTER TABLE `analysis_sistr_typing` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_sistr_typing` ENABLE KEYS */;
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
  `analysis_description` longtext,
  PRIMARY KEY (`id`),
  KEY `FK_ANALYSIS_SUBMISSION_REFERENCE_FILE_ID` (`reference_file_id`),
  KEY `FK_ANALYSIS_SUBMISSION_ANALYSIS` (`analysis_id`),
  KEY `FK_ANALYIS_SUBMISSION_SUBMITTER_ID` (`submitter`),
  KEY `FK_ANALYSIS_SUBMISSION_NAMED_PARAMETERS` (`named_parameters_id`),
  CONSTRAINT `FK_ANALYIS_SUBMISSION_SUBMITTER_ID` FOREIGN KEY (`submitter`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_ANALYSIS` FOREIGN KEY (`analysis_id`) REFERENCES `analysis` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_NAMED_PARAMETERS` FOREIGN KEY (`named_parameters_id`) REFERENCES `workflow_named_parameters` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REFERENCE_FILE_ID` FOREIGN KEY (`reference_file_id`) REFERENCES `reference_file` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `analysis_description` longtext,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_ANALYSIS_SUBMISSION_REVISION` (`REV`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  KEY `FK_ANALYSIS_SUBMISSION_REMOTE_UNPAIRED_FILE` (`remote_file_id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REMOTE_SINGLE_ANALYSIS` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`),
  CONSTRAINT `FK_ANALYSIS_SUBMISSION_REMOTE_UNPAIRED_FILE` FOREIGN KEY (`remote_file_id`) REFERENCES `remote_sequence_file_single` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `analysis_submission_sequence_file_pair_AUD`
--

LOCK TABLES `analysis_submission_sequence_file_pair_AUD` WRITE;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_pair_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_pair_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `analysis_submission_sequence_file_single_end`
--

LOCK TABLES `analysis_submission_sequence_file_single_end` WRITE;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_single_end` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_single_end` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `analysis_submission_sequence_file_single_end_AUD`
--

LOCK TABLES `analysis_submission_sequence_file_single_end_AUD` WRITE;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_single_end_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_submission_sequence_file_single_end_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement`
--

LOCK TABLES `announcement` WRITE;
/*!40000 ALTER TABLE `announcement` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcement` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement_AUD`
--

LOCK TABLES `announcement_AUD` WRITE;
/*!40000 ALTER TABLE `announcement_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcement_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
  UNIQUE KEY `announcement_id` (`announcement_id`,`user_id`),
  KEY `FK_USER_ID_ANNOUNCEMENT` (`user_id`),
  CONSTRAINT `FK_ANNOUNCEMENT_ID_ANNOUNCEMENT` FOREIGN KEY (`announcement_id`) REFERENCES `announcement` (`id`),
  CONSTRAINT `FK_USER_ID_ANNOUNCEMENT` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement_user`
--

LOCK TABLES `announcement_user` WRITE;
/*!40000 ALTER TABLE `announcement_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcement_user` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `announcement_user_AUD`
--

LOCK TABLES `announcement_user_AUD` WRITE;
/*!40000 ALTER TABLE `announcement_user_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `announcement_user_AUD` ENABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ncbi_export_biosample`
--

LOCK TABLES `ncbi_export_biosample` WRITE;
/*!40000 ALTER TABLE `ncbi_export_biosample` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbi_export_biosample` ENABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ncbi_export_biosample_sequence_file_pair`
--

LOCK TABLES `ncbi_export_biosample_sequence_file_pair` WRITE;
/*!40000 ALTER TABLE `ncbi_export_biosample_sequence_file_pair` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbi_export_biosample_sequence_file_pair` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ncbi_export_biosample_sequence_file_single_end`
--

DROP TABLE IF EXISTS `ncbi_export_biosample_sequence_file_single_end`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ncbi_export_biosample_sequence_file_single_end` (
  `ncbi_export_biosample_id` varchar(255) NOT NULL,
  `files_id` bigint(20) NOT NULL,
  KEY `FK_NCBI_EXPORT_SINGLE_END_FILE_SAMPLE` (`ncbi_export_biosample_id`),
  KEY `FK_NCBI_EXPORT_SINGLE_END_FILE_FILE` (`files_id`),
  CONSTRAINT `FK_NCBI_EXPORT_SINGLE_END_FILE_FILE` FOREIGN KEY (`files_id`) REFERENCES `sequence_file_single_end` (`id`),
  CONSTRAINT `FK_NCBI_EXPORT_SINGLE_END_FILE_SAMPLE` FOREIGN KEY (`ncbi_export_biosample_id`) REFERENCES `ncbi_export_biosample` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ncbi_export_biosample_sequence_file_single_end`
--

LOCK TABLES `ncbi_export_biosample_sequence_file_single_end` WRITE;
/*!40000 ALTER TABLE `ncbi_export_biosample_sequence_file_single_end` DISABLE KEYS */;
/*!40000 ALTER TABLE `ncbi_export_biosample_sequence_file_single_end` ENABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_access_token`
--

LOCK TABLES `oauth_access_token` WRITE;
/*!40000 ALTER TABLE `oauth_access_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_access_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_refresh_token`
--

DROP TABLE IF EXISTS `oauth_refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_refresh_token` (
  `token_id` varchar(255) NOT NULL,
  `token` longblob NOT NULL,
  `authentication` longblob NOT NULL,
  PRIMARY KEY (`token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_refresh_token`
--

LOCK TABLES `oauth_refresh_token` WRITE;
/*!40000 ALTER TABLE `oauth_refresh_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_refresh_token` ENABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `assemble_uploads` bit(1) NOT NULL DEFAULT b'0',
  `remote_status` bigint(20) DEFAULT NULL,
  `sync_frequency` varchar(255) DEFAULT NULL,
  `genome_size` bigint(20) DEFAULT NULL,
  `required_coverage` int(11) DEFAULT NULL,
  `sistr_typing_uploads` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `FK_PROJECT_REMOTE_STATUS` (`remote_status`),
  CONSTRAINT `FK_PROJECT_REMOTE_STATUS` FOREIGN KEY (`remote_status`) REFERENCES `remote_status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `assemble_uploads` bit(1) DEFAULT NULL,
  `remote_status` bigint(20) DEFAULT NULL,
  `sync_frequency` varchar(255) DEFAULT NULL,
  `genome_size` bigint(20) DEFAULT NULL,
  `required_coverage` int(11) DEFAULT NULL,
  `sistr_typing_uploads` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_PROJECT_REVISION` (`REV`),
  CONSTRAINT `FK_PROJECT_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_AUD`
--

LOCK TABLES `project_AUD` WRITE;
/*!40000 ALTER TABLE `project_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_AUD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_analysis_submission`
--

DROP TABLE IF EXISTS `project_analysis_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_analysis_submission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `analysis_submission_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_PROJECT_ANALYSIS` (`project_id`,`analysis_submission_id`),
  KEY `FK_PROJECT_ANALYSIS_ANALYSIS` (`analysis_submission_id`),
  CONSTRAINT `FK_PROJECT_ANALYSIS_ANALYSIS` FOREIGN KEY (`analysis_submission_id`) REFERENCES `analysis_submission` (`id`),
  CONSTRAINT `FK_PROJECT_ANALYSIS_PROJECT` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_analysis_submission`
--

LOCK TABLES `project_analysis_submission` WRITE;
/*!40000 ALTER TABLE `project_analysis_submission` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_analysis_submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project_analysis_submission_AUD`
--

DROP TABLE IF EXISTS `project_analysis_submission_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_analysis_submission_AUD` (
  `id` bigint(20) NOT NULL,
  `analysis_submission_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_PROJECT_ANALYSIS_SUBMISSION_AUD` (`REV`),
  CONSTRAINT `FK_PROJECT_ANALYSIS_SUBMISSION_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_analysis_submission_AUD`
--

LOCK TABLES `project_analysis_submission_AUD` WRITE;
/*!40000 ALTER TABLE `project_analysis_submission_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_analysis_submission_AUD` ENABLE KEYS */;
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
  `user_group_id` bigint(20) DEFAULT NULL,
  `sample_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_PROJECT_EVENT_SAMPLE` (`sample_id`),
  KEY `FK_PROJECT_EVENT_PROJECT` (`project_id`),
  KEY `FK_PROJECT_EVENT_USER` (`user_id`),
  KEY `FK_USER_GROUP_PROJECT_EVENT` (`user_group_id`),
  CONSTRAINT `FK_PROJECT_EVENT_PROJECT` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK_PROJECT_EVENT_SAMPLE` FOREIGN KEY (`sample_id`) REFERENCES `sample` (`id`),
  CONSTRAINT `FK_PROJECT_EVENT_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_USER_GROUP_PROJECT_EVENT` FOREIGN KEY (`user_group_id`) REFERENCES `user_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project_user_AUD`
--

LOCK TABLES `project_user_AUD` WRITE;
/*!40000 ALTER TABLE `project_user_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `project_user_AUD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qc_entry`
--

DROP TABLE IF EXISTS `qc_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qc_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `DTYPE` varchar(31) NOT NULL,
  `sequencingObject_id` bigint(20) NOT NULL,
  `total_bases` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_SEQOBJECT_QC_ENTRY` (`sequencingObject_id`),
  CONSTRAINT `FK_SEQOBJECT_QC_ENTRY` FOREIGN KEY (`sequencingObject_id`) REFERENCES `sequencing_object` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qc_entry`
--

LOCK TABLES `qc_entry` WRITE;
/*!40000 ALTER TABLE `qc_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `qc_entry` ENABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_remote_api_token_user` (`remote_api_id`,`user_id`),
  KEY `FK_REMOTE_API_TOKEN_USER` (`user_id`),
  CONSTRAINT `FK_REMOTE_API_TOKEN_REMOTE_API` FOREIGN KEY (`remote_api_id`) REFERENCES `remote_api` (`id`),
  CONSTRAINT `FK_REMOTE_API_TOKEN_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_REMOTE_API_TOKEN_REVISION` (`REV`),
  CONSTRAINT `FK_REMOTE_API_TOKEN_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `remote_uri` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `remote_uri` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_REMOTE_SEQUENCE_FILE_PAIR_AUD` (`REV`),
  CONSTRAINT `FK_REMOTE_SEQUENCE_FILE_PAIR_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remote_sequence_file_properties_AUD`
--

LOCK TABLES `remote_sequence_file_properties_AUD` WRITE;
/*!40000 ALTER TABLE `remote_sequence_file_properties_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_sequence_file_properties_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remote_sequence_file_single`
--

LOCK TABLES `remote_sequence_file_single` WRITE;
/*!40000 ALTER TABLE `remote_sequence_file_single` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_sequence_file_single` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remote_sequence_file_single_AUD`
--

LOCK TABLES `remote_sequence_file_single_AUD` WRITE;
/*!40000 ALTER TABLE `remote_sequence_file_single_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_sequence_file_single_AUD` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `remote_status`
--

DROP TABLE IF EXISTS `remote_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sync_status` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `api_id` bigint(20) NOT NULL,
  `remote_hash_code` int(11) DEFAULT NULL,
  `read_by` bigint(20) DEFAULT NULL,
  `last_update` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_REMOTE_STATUS_API` (`api_id`),
  KEY `FK_REMOTE_OBJECT_READ_BY` (`read_by`),
  CONSTRAINT `FK_REMOTE_OBJECT_READ_BY` FOREIGN KEY (`read_by`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_REMOTE_STATUS_API` FOREIGN KEY (`api_id`) REFERENCES `remote_api` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remote_status`
--

LOCK TABLES `remote_status` WRITE;
/*!40000 ALTER TABLE `remote_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `remote_status_AUD`
--

DROP TABLE IF EXISTS `remote_status_AUD`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `remote_status_AUD` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `sync_status` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `api_id` bigint(20) DEFAULT NULL,
  `remote_hash_code` int(11) DEFAULT NULL,
  `read_by` bigint(20) DEFAULT NULL,
  `last_update` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_REMOTE_STATUS_AUD` (`REV`),
  CONSTRAINT `FK_REMOTE_STATUS_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `remote_status_AUD`
--

LOCK TABLES `remote_status_AUD` WRITE;
/*!40000 ALTER TABLE `remote_status_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `remote_status_AUD` ENABLE KEYS */;
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
  `remote_status` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_SAMPLE_REMOTE_STATUS` (`remote_status`),
  CONSTRAINT `FK_SAMPLE_REMOTE_STATUS` FOREIGN KEY (`remote_status`) REFERENCES `remote_status` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `remote_status` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SAMPLE_REVISION` (`REV`),
  CONSTRAINT `FK_SAMPLE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sample_AUD`
--

LOCK TABLES `sample_AUD` WRITE;
/*!40000 ALTER TABLE `sample_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sample_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sample_sequencingobject`
--

LOCK TABLES `sample_sequencingobject` WRITE;
/*!40000 ALTER TABLE `sample_sequencingobject` DISABLE KEYS */;
/*!40000 ALTER TABLE `sample_sequencingobject` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sample_sequencingobject_AUD`
--

LOCK TABLES `sample_sequencingobject_AUD` WRITE;
/*!40000 ALTER TABLE `sample_sequencingobject_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sample_sequencingobject_AUD` ENABLE KEYS */;
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
  `fastqc_analysis_id` bigint(20) DEFAULT NULL,
  `remote_status` bigint(20) DEFAULT NULL,
  `upload_sha256` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_SEQUENCE_FILE_FILEPATH` (`file_path`),
  KEY `FK_SEQUENCE_FILE_FASTQC` (`fastqc_analysis_id`),
  KEY `FK_SEQUENCEFILE_REMOTE_STATUS` (`remote_status`),
  CONSTRAINT `FK_SEQUENCEFILE_REMOTE_STATUS` FOREIGN KEY (`remote_status`) REFERENCES `remote_status` (`id`),
  CONSTRAINT `FK_SEQUENCE_FILE_FASTQC` FOREIGN KEY (`fastqc_analysis_id`) REFERENCES `analysis_fastqc` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `remote_status` bigint(20) DEFAULT NULL,
  `upload_sha256` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SEQFILE_REVISION` (`REV`),
  CONSTRAINT `FK_SEQFILE_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_SEQUENCE_FILE_PAIR_OBJECT` FOREIGN KEY (`id`) REFERENCES `sequencing_object` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SEQUENCE_FILE_PAIR_AUD` (`REV`),
  CONSTRAINT `FK_SEQUENCE_FILE_PAIR_AUD` FOREIGN KEY (`id`, `REV`) REFERENCES `sequencing_object_AUD` (`id`, `REV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence_file_properties_AUD`
--

LOCK TABLES `sequence_file_properties_AUD` WRITE;
/*!40000 ALTER TABLE `sequence_file_properties_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_file_properties_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence_file_single_end`
--

LOCK TABLES `sequence_file_single_end` WRITE;
/*!40000 ALTER TABLE `sequence_file_single_end` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_file_single_end` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence_file_single_end_AUD`
--

LOCK TABLES `sequence_file_single_end_AUD` WRITE;
/*!40000 ALTER TABLE `sequence_file_single_end_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_file_single_end_AUD` ENABLE KEYS */;
UNLOCK TABLES;

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
  `remote_status` bigint(20) DEFAULT NULL,
  `sistr_typing` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_SEQUENCING_OBJECT_RUN` (`sequencing_run_id`),
  KEY `FK_SEQUENCING_OBJECT_ASSEMBLY` (`automated_assembly`),
  KEY `FK_SEQOBJECT_REMOTE_STATUS` (`remote_status`),
  KEY `FK_SEQUENCING_OBJECT_SISTR` (`sistr_typing`),
  CONSTRAINT `FK_SEQOBJECT_REMOTE_STATUS` FOREIGN KEY (`remote_status`) REFERENCES `remote_status` (`id`),
  CONSTRAINT `FK_SEQUENCING_OBJECT_ASSEMBLY` FOREIGN KEY (`automated_assembly`) REFERENCES `analysis_submission` (`id`),
  CONSTRAINT `FK_SEQUENCING_OBJECT_RUN` FOREIGN KEY (`sequencing_run_id`) REFERENCES `sequencing_run` (`id`),
  CONSTRAINT `FK_SEQUENCING_OBJECT_SISTR` FOREIGN KEY (`sistr_typing`) REFERENCES `analysis_submission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequencing_object`
--

LOCK TABLES `sequencing_object` WRITE;
/*!40000 ALTER TABLE `sequencing_object` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequencing_object` ENABLE KEYS */;
UNLOCK TABLES;

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
  `remote_status` bigint(20) DEFAULT NULL,
  `sistr_typing` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SEQUENCING_OBJECT_AUD` (`REV`),
  CONSTRAINT `FK_SEQUENCING_OBJECT_AUD` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequencing_object_AUD`
--

LOCK TABLES `sequencing_object_AUD` WRITE;
/*!40000 ALTER TABLE `sequencing_object_AUD` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequencing_object_AUD` ENABLE KEYS */;
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
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_SEQUENCING_RUN_USER` (`user_id`),
  CONSTRAINT `FK_SEQUENCING_RUN_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK_SEQUENCING_RUN_REVISION` (`REV`),
  CONSTRAINT `FK_SEQUENCING_RUN_REVISION` FOREIGN KEY (`REV`) REFERENCES `Revisions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2017-05-31 12:57:37','\0','admin@example.org','','Administrator','Administrator','en','2017-05-31 12:57:37','$2a$10$yvzFLxWA9m2wNQmHpJtWT.MRZv8qV8Mo3EMB6HTkDnUbi9aBrbWWW','867-5309','admin','ROLE_ADMIN');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
  KEY `FK_USER_GROUP_MEMBER_GROUP` (`group_id`),
  KEY `FK_USER_GROUP_MEMBER_USER` (`user_id`),
  CONSTRAINT `FK_USER_GROUP_MEMBER_GROUP` FOREIGN KEY (`group_id`) REFERENCES `user_group` (`id`),
  CONSTRAINT `FK_USER_GROUP_MEMBER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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

-- Dump completed on 2017-05-31 13:05:15
