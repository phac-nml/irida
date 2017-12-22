(function (angular, page, notifications) {
  /**
   * Controller to download the analysis.
   * @constructor
   */
  function FileDownloadController() {
    var vm = this;

    vm.getZipFile = function (id) {
      var iFrameId = 'hiddenDownloader';
      var hiddenIFrame = document.getElementById(iFrameId);
      if (hiddenIFrame === null) {
        hiddenIFrame = document.createElement('iframe');
        hiddenIFrame.id = iFrameId;
        hiddenIFrame.style.display = 'none';
        document.body.appendChild(hiddenIFrame);
      }
      hiddenIFrame.src = page.URLS.download + id
    };
  }

  /**
   * Service for Analysis
   * @param $http
   * @param $interval
   * @returns {AnalysisService}
   * @constructor
   */
  function AnalysisService($http) {
    var svc = this;

    /**
     * Call the server to get the status for the current analysis.
     * 'page.URLS.status' is on the `_base.html` page for the analysis.
     * @returns {*}
     * @private
     */
    function _getState() {
      return $http.get(page.URLS.status).then(function (data) {
        return data.data;
      });
    }

    
    /**
     * Get the results from a SISTR analysis to preview in the browser.
     */
    svc.getSistrResults = function() {
      return $http.get(page.URLS.sistr).then(function(result) {
        return result.data;
      });
    };

    svc.getJobErrors = function() {
      return $http.get(page.URLS.job_errors).then(function(x) {
        return x.data;
      });
    };

    /**
     * Exported function to call the server for information about the current analysis.
     * @param fn Callback function with how to handle the results.
     */
    svc.getAnalysisState = function (fn) {
      _getState().then(function (data) {
        fn(data);
      });
    };

    /**
     * Call the server to update the shared status of the current analysis.
     */
    svc.updateProjectShare = function(project, shared) {
      var data = {project: project, shared: shared};
      return $http.post(page.URLS.share, data).then(function(response) {
        return response.data;
      });
    };

    /**
     * Call the server to get the shared status of project
     */
    svc.getSharedProjects = function() {
      return $http.get(page.URLS.share).then(function(response) {
        return response.data;
      });
    };

    return svc;
  }

  function ProjectShareController(AnalysisService) {
    var vm = this;

    vm.projects = {};

    function initialize() {
      AnalysisService.getSharedProjects().then(function(response){
        vm.projects = response;
        console.log(vm.projects);
      });
    }

    vm.updateShared = function(project) {
      AnalysisService.updateProjectShare(project.project.identifier, project.shared).then(function(response) {
        notifications.show({text: response.message});
      });
    };

    initialize();
  }

  /**
   * Controls the state for the current analysis.
   * @param AnalysisService
   * @constructor
   */
  function StateController(AnalysisService) {
    var vm = this;
    vm.percentage = 0;

    /**
     * Initializes the sate of the analysis.
     */
    function initialize() {
      return AnalysisService.getAnalysisState(function (data) {
        vm.state = data.state;
        vm.stateLang = data.stateLang;
        vm.percentage = Math.round(parseFloat(data.percentComplete));
        vm.stateClass = _createClass(vm.state);
      });
    }

    /**
     * Dynamically creates the border color for the top of the sidebar depending on the state of the analysis.
     * @param state
     * @returns {string}
     * @private
     */
    function _createClass(state) {
      return 'analysis__alert--' + state.toLowerCase();
    }

    initialize();
  }

  function PreviewController() {
    var vm = this;
    vm.newick = page.NEWICK;
  }

  function SistrController(analysisService) {
    var vm = this;

    analysisService.getSistrResults().then(function(result) {
      if (result['parse_results_error']) {
        vm.parse_results_error = true;
      } else {
        var sample_information = {};
        sample_information['name'] = result['sample_name'];
        sample_information['qc_status'] = result['qc_status'];
        sample_information['qc_messages'] = result['qc_messages'].split("|");
        sample_information['qc_pass'] = (result['qc_status'] == 'PASS');
        sample_information['qc_warning'] = (result['qc_status'] == 'WARNING');
        sample_information['qc_fail'] = (result['qc_status'] == 'FAIL');

        var cgMLST_predictions = {};
        cgMLST_predictions['matching_alleles'] = result['cgmlst_matching_alleles']+'/330';
        cgMLST_predictions['percent_matching'] = parseFloat((1 - result['cgmlst_distance'])*100).toFixed(1)+"%";
  
        var mash_predictions = {};
        mash_predictions['distance'] = result['mash_distance'];
  
	vm.result = result;
        vm.sample_information = sample_information;
        vm.cgMLST_predictions = cgMLST_predictions;
        vm.mash_predictions = mash_predictions;
        vm.parse_results_error = result['parse_results_error'];
      }
    });
  }

  function JobErrorsController(analysisService) {
    const vm = this;
    vm.has_job_errors = false;
    vm.job_errors = [];
    analysisService.getJobErrors().then(function(x) {
      if (typeof x === "undefined" || x === null) {
        return;
      }
      if (typeof x.job_errors === "undefined") {
        return;
      }
      if (x.job_errors.length === 0) {
        return;
      }
      vm.has_job_errors = true;
      vm.job_errors = x.job_errors;
    });
    // commandLine: "perl /shed_tools/toolshed.g2.bx.psu.edu/repos/nml/spades/35cb17bd8bf9/spades/spades.pl /export/galaxy-central/database/files/000/dataset_158.dat /export/galaxy-central/database/files/000/dataset_159.dat /export/galaxy-central/database/files/000/dataset_160.dat /export/galaxy-central/database/files/000/dataset_161.dat /export/galaxy-central/database/files/000/dataset_162.dat  fail spades.py --disable-gzip-output   --careful -t ${GALAXY_SLOTS:-16}       --pe1-fr --pe1-1 fastq:/home/CSCScience.ca/pkruczkiewicz/irida-data/sequence/3/1/test_file_1.fastq --pe1-2 fastq:/home/CSCScience.ca/pkruczkiewicz/irida-data/sequence/4/1/test_file_2.fastq --pe2-fr --pe2-s fastq:/export/galaxy-central/database/files/000/dataset_154.dat"
    // createdDate: 1513894397000
    // exitCode: 25
    // historyId: "df7a1f0c02a5b08e"
    // identifier: "1"
    // jobId: "72ad249754f05d26"
    // label: "toolshed.g2.bx.psu.edu/repos/nml/spades/spades/1.4-2017-12-21 16:22:24.0"
    // links: Array []
    // parameters: "{iontorrent=\"false\", __workflow_invocation_uuid__=\"9ebb9a22e69b11e794c10242ac110002\", libraries_1|files_0|unpaired_reads|__identifier__=\"fail\", cov={\"state\": \"off\", \"__current_case__\": 0}, libraries_1|files_0|unpaired_reads={id=d3a4d6a5256f2d9a, uuid=f651fee7-4bea-466a-a798-26b5b98e31c5}, trustedcontigs=[], dbkey=\"?\", libraries=[{\"lib_type\": \"paired_end\", \"__index__\": 0, \"orientation\": \"fr\", \"files\": [{\"file_type\": {\"fastq_collection\": {\"values\": [{\"src\": \"dce\", \"id\": 151}]}, \"type\": \"paired-collection\", \"__current_case__\": 3}, \"__index__\": 0}]}, {\"lib_type\": \"paired_end\", \"__index__\": 1, \"orientation\": \"fr\", \"files\": [{\"file_type\": {\"type\": \"unpaired\", \"unpaired_reads\": {\"values\": [{\"src\": \"hda\", \"id\": 154}]}, \"__current_case__\": 2}, \"__index__\": 0}]}], onlyassembler=\"false\", nanopore=[], pacbio=[], kmer_choice={\"auto_kmer_choice\": \"true\", \"__current_case__\": 1}, sc=\"false\", untrustedcontigs=[], sanger=[], careful=\"true\", chromInfo=\"/galaxy-central/tool-data/shared/ucsc/chrom/?.len\", libraries_0|files_0|fastq_collection2={id=8317ee2b0d0f62d9, uuid=389d114f-d653-486f-b58a-2d0841fcf476}, libraries_0|files_0|fastq_collection1={id=70eec96181a992f8, uuid=5e47872a-e3ec-47be-919a-e4bb4d76a929}}"
    // provenanceId: "1343954bc98f8001"
    // provenanceUUID: "d9b0c822-5b27-499a-b693-75577a96a2fd"
    // standardError: "Failed with code 256\nCommand spades.py --disable-gzip-output --careful -t 1 --pe1-fr --pe1-1 fastq:/home/CSCScience.ca/pkruczkiewicz/irida-data/sequence/3/1/test_file_1.fastq --pe1-2 fastq:/home/CSCScience.ca/pkruczkiewicz/irida-data/sequence/4/1/test_file_2.fastq --pe2-fr --pe2-s fastq:/export/galaxy-central/database/files/000/dataset_154.dat -o output_dir\nMessage: 256\n"
    // standardOutput: "Command line: /export/tool_deps/spades/3.9.0/nml/package_spades_3_9_0/d8c8c3dc8f9a/bin/spades.py\t--disable-gzip-output\t--careful\t-t\t1\t--pe1-fr\t--pe1-1\tfastq:/home/CSCScience.ca/pkruczkiewicz/irida-data/sequence/3/1/test_file_1.fastq\t--pe1-2\tfastq:/home/CSCScience.ca/pkruczkiewicz/irida-data/sequence/4/1/test_file_2.fastq\t--pe2-fr\t--pe2-s\tfastq:/export/galaxy-central/database/files/000/dataset_154.dat\t-o\t/export/galaxy-central/database/job_working_directory/000/47/working/output_dir\t\n\nSystem information:\n  SPAdes version: 3.9.0\n  Python version: 2.7.6\n  OS: Linux-4.4.0-98-generic-x86_64-with-Ubuntu-14.04-trusty\n\nOutput dir: /export/galaxy-central/database/job_working_directory/000/47/working/output_dir\nMode: read error correction and assembling\nDebug mode is turned OFF\n\nDataset parameters:\n  Multi-cell mode (you should set '--sc' flag if input data was obtained with MDA (single-cell) technology or --meta flag if processing metagenomic dataset)\n  Reads:\n    Library number: 1, library typ…"
    // toolDescription: "SPAdes genome assembler for regular and single-cell projects"
    // toolId: "toolshed.g2.bx.psu.edu/repos/nml/spades/spades/1.4"
    // toolName: "spades"
    // toolVersion: "1.4"
    // updatedDate: 1513894944000
  }

  angular.module('irida.analysis', ['ui.router', 'subnav', 'phylocanvas'])
    .config(['$stateProvider', function ($stateProvider) {
      $stateProvider
        .state("preview", {
          url        : "/preview",
          templateUrl: "preview.html"
        })
        .state("sistr", {
          url: "/sistr",
          templateUrl: "sistr.html",
          controllerAs: "sistrCtrl",
          controller: ["AnalysisService", SistrController]
        })
        .state("joberrors", {
          url: "/joberrors",
          templateUrl: "joberrors.html",
          controllerAs: "jobErrorsCtrl",
          controller: ["AnalysisService", JobErrorsController]
        })
        .state("inputs", {
          url        : "/inputs",
          templateUrl: "inputs.html"
        })
        .state("provenance", {
          url        : "/provenance",
          templateUrl: "provenance.html"
        })
        .state("share", {
          url        : "/share",
          templateUrl: "share.html"
        })
      ;
    }])
    .service('AnalysisService', ['$http', AnalysisService])
    .controller('FileDownloadController', [FileDownloadController])
    .controller('StateController', ['AnalysisService', StateController])
    .controller('PreviewController', [PreviewController])
    .controller('ProjectShareController', ['AnalysisService', ProjectShareController])
  ;
})(window.angular, window.PAGE, window.notifications);
