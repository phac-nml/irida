#!/usr/bin/perl

use DBI;
use Getopt::Long;
use Text::CSV;
use strict;
use warnings;

my $username = "test";
my $password = "test";
my $db       = "irida_test";
my $host     = "localhost";

GetOptions(
    'u|username=s' => \$username,
    'p|password=s' => \$password,
    'd|db=s'       => \$db,
    'h|host=s'     => \$host
);

my $csv = Text::CSV->new({binary => 1, eol => $/ })
    or die "Failed to create a CSV handle: $!";

my $fh = \*STDOUT;

#connect to the DB
my $dbstring = "dbi:mysql:$db:$host";

my $dbh =
  DBI->connect( $dbstring, $username, $password,
    { RaiseError => 1, AutoCommit => 0 } )
  or die "Cannot connect to database: $DBI::errstr";


#get the metadata_entry_AUD that dont have mappings to sample_metadata_entry_AUD
my $sql = "select m.id, m.value, r.id, r.user_id, s.id, s.sampleName from metadata_entry_AUD m LEFT JOIN sample_AUD s ON s.REV=m.REV LEFT JOIN Revisions r ON m.REV=r.id where m.id NOT IN (select metadata_id FROM sample_metadata_entry_AUD)";

my $sth = $dbh->prepare($sql);
my $rv  = $sth->execute();

my %entries;

my @arr = ("Metadata entry id", "Metadata value", "Database revision number", "Editing user", "Associated sample id", "Associated Sample Name");

$csv->print($fh, \@arr);

#compile the runs, files, and path names
my ( $mid, $mvalue, $rev, $user, $sid, $sname );
$sth->bind_columns( undef, \$mid, \$mvalue, \$rev, \$user, \$sid, \$sname );
while ( $sth->fetch ) {
	@arr = ($mid, $mvalue, $rev, $user, $sid, $sname);

	$csv->print($fh,\@arr);
}

$dbh->disconnect();
