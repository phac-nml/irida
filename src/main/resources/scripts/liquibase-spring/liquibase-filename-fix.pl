#!/usr/bin/perl

use DBI;
use Getopt::Long;
use strict;
use warnings;

my $username = "test";
my $password = "test";
my $db = "irida_test";
my $host = "localhost";

GetOptions(
        'u|username=s'=>\$username,
        'p|password=s'=>\$password,
        'd|db=s'=>\$db,
        'h|host=s'=>\$host
);

my $dbstring = "dbi:mysql:$db:$host";

my $dbh = DBI->connect($dbstring,$username,$password, {RaiseError=>1,AutoCommit=>0}) or die "Cannot connect to database: $DBI::errstr";

#Get the existing ID and Filename for the liquibase updates
my $sql = "SELECT ID, FILENAME FROM DATABASECHANGELOG";
my $sth = $dbh->prepare($sql);
my $rv = $sth->execute();

my @results;

my ($id,$filename);

$sth->bind_columns(undef,\$id,\$filename);
while($sth->fetch()){
	push(@results,{id=>$id, file=>$filename});
}

#For each of the existing liquibase updates, remote the src/main/resources/ portion
$sql = "UPDATE DATABASECHANGELOG SET FILENAME=? WHERE ID=?";
$sth = $dbh->prepare($sql);
foreach my $entry(@results){
	my $id = $entry->{id};
	my $file = $entry->{file};
	if($file =~ s/^src\/main\/resources\///g)
	{
		my $rv = $sth->execute($file,$id);
		print $rv."\n";
	}
}

$dbh->commit();
$dbh->disconnect();
