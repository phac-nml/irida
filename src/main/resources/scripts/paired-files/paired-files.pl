#!/usr/bin/perl

use DBI;
use Getopt::Long;
use File::Basename;
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

my $totalFiles = 0;    #total number of files found
my $totalPairs = 0;    #total number of paired files found

#connect to the DB
my $dbstring = "dbi:mysql:$db:$host";

my $dbh =
  DBI->connect( $dbstring, $username, $password,
    { RaiseError => 1, AutoCommit => 0 } )
  or die "Cannot connect to database: $DBI::errstr";

#get the sequence files joined with sequencing_run
my $sql = "SELECT r.id, f.id, f.filePath
FROM sequencing_run r INNER JOIN sequence_file f ON r.id=f.sequencingRun_id";
my $sth = $dbh->prepare($sql);
my $rv  = $sth->execute();

my %runs;    #the runs, file ids, and path names

#compile the runs, files, and path names
my ( $runId, $fileId, $filePath );
$sth->bind_columns( undef, \$runId, \$fileId, \$filePath );
while ( $sth->fetch ) {
    $runs{$runId}{$fileId} = basename($filePath);
}

#prepare the insertion query
my $pairQuery = "INSERT INTO sequence_file_pair (created_date) VALUES (now())";
my $filesQuery =
  "INSERT INTO sequence_file_pair_files (pair_id,files_id) VALUES (?,?)";
my $pairSth  = $dbh->prepare($pairQuery);
my $filesSth = $dbh->prepare($filesQuery);

#loop through each run and find the pairs for the files
for my $runId ( keys %runs ) {
    my $run = $runs{$runId};

    #find pair matches for the runs
    my $pairs = findPairs($run);

    #foreach pair
    foreach my $p ( keys $pairs ) {

        #inser to sequence_file_pair
        $pairSth->execute();

        #get the id inserted
        my $insertedId =
          $dbh->last_insert_id( "", "", "sequence_file_pair", "id" );

        #insert each file to sequence_file_pair_files
        $filesSth->execute( $insertedId, $p );
        $filesSth->execute( $insertedId, $pairs->{$p} );
    }

    #increment the totals
    $totalFiles += scalar keys $run;
    $totalPairs += ( scalar keys $pairs ) * 2;
}

$dbh->commit();

$dbh->disconnect();

print "Paired $totalPairs of $totalFiles files.\n";

#find pairs of files
sub findPairs {
    my $files = shift;

    my %pairs;
    my @fileIds = keys %$files;

    #for each file
    for ( my $i = 0 ; $i < @fileIds ; $i++ ) {

        #get the filename
        my $fname = $files->{ $fileIds[$i] };

        my $matched = 0;    #flag if matched

        #check if we've paired this file already
        if ( defined $pairs{ $fileIds[$i] } ) {
            next;
        }

        #parse the filename
        if ( $fname =~ /^(.+)_R(\d+)_\S+\.fastq.*$/ ) {
            my $base   = $1;    #the sample name and run info
            my $thisid = $2;    # the pair number

            #check what the number of the other file should be
            my $pairId = 0;
            if ( $thisid == 1 ) {
                $pairId = 2;
            }
            elsif ( $thisid == 2 ) {
                $pairId = 1;
            }

            #look ahead in the list to see if we can find the match
            for ( my $j = $i + 1 ; $j < @fileIds ; $j++ ) {

                #get the other filename
                my $innerFile = $files->{ $fileIds[$j] };

                #build a regular expression
                my $regex = $base . "_R" . $pairId;

                #test the other file name
                if ( $innerFile =~ /^$regex/ ) {

                    #if mastched, add to the hash
                    $pairs{ $fileIds[$j] } = $fileIds[$i];
                    $matched = 1;
                    last;
                }
            }

            if ( !$matched ) {
                print STDERR "Warning: $fileIds[$i] - $fname not matched\n";
            }
        }
    }
    return \%pairs;
}
