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

my $sql = "SELECT id,clientId from Revisions";
my $sth = $dbh->prepare($sql);

my $rv = $sth->execute();

my ($rid,$clientId);

$sth->bind_columns(undef,\$rid,\$clientId);
while($sth->fetch()){
	print "$rid,$clientId\n";
}

$dbh->disconnect();
