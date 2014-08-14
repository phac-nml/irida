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

my @client_details = (
	{id=>1,clientId=>"sequencer", clientSecret=>"N9Ywc6GKWWZotzsJGutj3BZXJDRn65fXJqjrk29yTjI",token_validity=>43200},
	{id=>2,clientId=>"linker", clientSecret=>"ZG5K1AFVSycE25ooxgcBRGCWFzSTfDnJ1DxSkdEmEho",token_validity=>43200},
	{id=>3,clientId=>"pythonLinker", clientSecret=>"bySZBP5jNO9pSZTz3omFRtJs3XFAvshxGgvXIlZ2zjk",token_validity=>43200}
);

my @client_details_authorities =(
	{client_details_id=>1, authority_name=>"ROLE_CLIENT"},
	{client_details_id=>3, authority_name=>"ROLE_CLIENT"},
	{client_details_id=>2, authority_name=>"ROLE_CLIENT"}
);

my @client_details_scope =(
	{client_details_id=>1, scope=>"read"},
	{client_details_id=>1, scope=>"write"},
	{client_details_id=>2, scope=>"read"},
	{client_details_id=>3, scope=>"read"}
);

my @client_details_grant_types =(
	{client_details_id=>1, grant_value=>"password"},
	{client_details_id=>2, grant_value=>"password"},
	{client_details_id=>3, grant_value=>"password"}
);

my @client_details_resource_ids =(
	{client_details_id=>1, resource_id=>"NmlIrida"},
	{client_details_id=>2, resource_id=>"NmlIrida"},
	{client_details_id=>3, resource_id=>"NmlIrida"}
);

my $dbh = DBI->connect($dbstring,$username,$password, {RaiseError=>1,AutoCommit=>0}) or die "Cannot connect to database: $DBI::errstr";

my $sql = "INSERT INTO client_details (id,clientId,clientSecret,token_validity,createdDate) VALUES (?,?,?,?,now())";
my $sth = $dbh->prepare($sql);
foreach my $client(@client_details){
	my $rv = $sth->execute($client->{id},$client->{clientId},$client->{clientSecret},$client->{token_validity});
	if($rv){
		print "Added client '$client->{clientId}'\n";
	}
	else{
		die "Couldn't add client $client->{clientId}";
	}
}

$sql = "insert into client_details_authorities (client_details_id,authority_name) values (?,?)";
$sth = $dbh->prepare($sql);
foreach my $client(@client_details_authorities){
	my $rv = $sth->execute($client->{client_details_id},$client->{authority_name});
	if(!$rv){
		die "Couldn't add client authorities";
	}
}

$sql = "insert into client_details_scope (client_details_id,scope) values (?,?)";
$sth = $dbh->prepare($sql);
foreach my $client(@client_details_scope){
	my $rv = $sth->execute($client->{client_details_id},$client->{scope});
	if(!$rv){
		die "Couldn't add client scope";
	}
}

$sql = "insert into client_details_grant_types (client_details_id,grant_value) values (?,?)";
$sth = $dbh->prepare($sql);
foreach my $client(@client_details_grant_types){
	my $rv = $sth->execute($client->{client_details_id},$client->{grant_value});
	if(!$rv){
		die "Couldn't add client grant type";
	}
}

$sql = "insert into client_details_resource_ids (client_details_id,resource_id) values (?,?)";
$sth = $dbh->prepare($sql);
foreach my $client(@client_details_resource_ids){
	my $rv = $sth->execute($client->{client_details_id},$client->{resource_id});
	if(!$rv){
		die "Couldn't add client resource id";
	}
}

$dbh->commit();

$dbh->disconnect();
