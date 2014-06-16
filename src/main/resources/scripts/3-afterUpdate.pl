#!/usr/bin/perl

use DBI;
use Getopt::Long;
use strict;
use warnings;

my $username = "test";
my $password = "test";
my $db = "irida_test";
my $host = "localhost";
my $file;

GetOptions(
        'u|username=s'=>\$username,
        'p|password=s'=>\$password,
        'd|db=s'=>\$db,
        'h|host=s'=>\$host,
	'i|input=s'=>\$file
);

my $dbstring = "dbi:mysql:$db:$host";

open(FILE,$file) or die "Can't open data file";

my $dbh = DBI->connect($dbstring,$username,$password, {RaiseError=>1,AutoCommit=>0}) or die "Cannot connect to database: $DBI::errstr";

my $sql = "UPDATE Revisions SET client_id=(SELECT id FROM client_details WHERE clientId=?) WHERE id=?";
my $sth = $dbh->prepare($sql);

while(my $line = <FILE>){
	chomp $line;
	my ($rid,$clientId) = split(/,/,$line);
	my $rv = $sth->execute($clientId,$rid);
	if($rv){
		print "Updated revision $rid\n";
	}
	else{
		$dbh->rollback();
		die "ERROR: Couldn't update revision $rv";
	}
}

$dbh->commit();

close(FILE);

$dbh->disconnect();
