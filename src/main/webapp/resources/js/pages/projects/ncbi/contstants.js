/**
 * Values imported directly from https://www.ncbi.nlm.nih.gov/core/assets/sra/files/SRA_metadata_SP.xlsx
 */
export const PLATFORMS = [
  "LS454",
  "ILLUMINA",
  "HELICOS",
  "ABI_SOLID",
  "COMPLETE_GENOMICS",
  "PACBIO_SMRT",
  "ION_TORRENT",
  "CAPILLARY",
  "OXFORD_NANOPORE",
];

const LS454 = [
  "454 GS",
  "454 GS 20",
  "454 GS FLX",
  "454 GS FLX+",
  "454 GS FLX Titanium",
  "454 GS Junior",
];

const ILLUMINA = [
  "Illumina Genome Analyzer",
  "Illumina Genome Analyzer II",
  "Illumina Genome Analyzer IIx",
  "Illumina HiSeq 2500",
  "Illumina HiSeq 2000",
  "Illumina HiSeq 1000",
  "Illumina MiSeq",
  "Illumina HiScanSQ",
  "NextSeq 500",
  "HiSeq X Ten",
  "HiSeq X Five",
  "Illumina HiSeq 1500",
  "Illumina HiSeq 3000",
  "Illumina HiSeq 4000",
  "NextSeq 550",
];

const HELICOS = ["Helicos HeliScope"];

const ABI_SOLID = [
  "AB SOLiD System",
  "AB SOLiD System 2.0",
  "AB SOLiD System 3.0",
  "AB SOLiD 4 System",
  "AB SOLiD 4hq System",
  "AB SOLiD PI System",
  "AB 5500 Genetic Analyzer",
  "AB 5500xl Genetic Analyzer",
  "AB 5500x-Wl Genetic Analyzer",
  "AB SOLiD 3 Plus System",
];

const COMPLETE_GENOMICS = ["Complete Genomics"];

const PACBIO_SMRT = ["PacBio RS", "PacBio RS II"];

const ION_TORRENT = ["Ion Torrent PGM", "Ion Torrent Proton"];

const CAPILLARY = [
  "AB 3730xL Genetic Analyzer",
  "AB 3730 Genetic Analyzer",
  "AB 3500xL Genetic Analyzer",
  "AB 3500 Genetic Analyzer",
  "AB 3130xL Genetic Analyzer",
  "AB 3130 Genetic Analyzer",
  "AB 310 Genetic Analyzer",
];

const OXFORD_NANOPORE = ["GridION", "MinION"];
export const LIBRARY_SELECTION_OPTIONS = [
  "RANDOM",
  "PCR",
  "RANDOM PCR",
  "RT-PCR",
  "HMPR",
  "MF",
  "CF-S",
  "CF-M",
  "CF-H",
  "CF-T",
  "MDA",
  "MSLL",
  "cDNA",
  "ChIP",
  "MNase",
  "DNAse",
  "Hybrid Selection",
  "Reduced Representation",
  "Restriction Digest",
  "5-methylcytidine antibody",
  "MBD2 protein methyl-CpG binding domain",
  "CAGE",
  "RACE",
  "size fractionation",
  "Padlock probes capture method",
  "other",
  "unspecified",
];
export const LIBRARY_STRATEGY_OPTIONS = [
  "WGA",
  "WGS",
  "WXS",
  "RNA-Seq",
  "miRNA-Seq",
  "WCS",
  "CLONE",
  "POOLCLONE",
  "AMPLICON",
  "CLONEEND",
  "FINISHING",
  "ChIP-Seq",
  "MNase-Seq",
  "DNase-Hypersensitivity",
  "Bisulfite-Seq",
  "Tn-Seq",
  "EST",
  "FL-cDNA",
  "CTS",
  "MRE-Seq",
  "MeDIP-Seq",
  "MBD-Seq",
  "OTHER",
];
