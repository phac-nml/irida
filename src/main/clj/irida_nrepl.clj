(ns irida-nrepl
  (:import (ca.corefacility.bioinformatics.irida.service.analysis.workspace AnalysisWorkspaceService)
           (com.github.jmchilton.blend4j.galaxy GalaxyInstance HistoriesClient WorkflowsClient WorkflowsClientImpl JobsClient JobsClientImpl ToolsClient)
           (ca.corefacility.bioinformatics.irida.service.workflow IridaWorkflowsService)
           (ca.corefacility.bioinformatics.irida.service.impl AnalysisExecutionScheduledTaskImpl ProjectServiceImpl)
           (ca.corefacility.bioinformatics.irida.service.analysis.execution AnalysisExecutionService)
           (ca.corefacility.bioinformatics.irida.model.workflow IridaWorkflow)
           (ca.corefacility.bioinformatics.irida.model.enums AnalysisState AnalysisType)
           (ca.corefacility.bioinformatics.irida.model.sequenceFile SequenceFilePair SequenceFile)
           (ca.corefacility.bioinformatics.irida.model.workflow.analysis ToolExecution Analysis)
           (ca.corefacility.bioinformatics.irida.repositories.analysis.submission AnalysisSubmissionRepository)
           (ca.corefacility.bioinformatics.irida.service SequencingObjectService CleanupAnalysisSubmissionCondition AnalysisExecutionScheduledTask)
           (ca.corefacility.bioinformatics.irida.model.workflow.submission AnalysisSubmission)
           (ca.corefacility.bioinformatics.irida.service.impl.user UserServiceImpl)
           (ca.corefacility.bioinformatics.irida.service.user UserService)
           (org.springframework.data.domain Sort Sort$Order)
           (com.github.jmchilton.blend4j.galaxy.beans HistoryContentsProvenance)
           (ca.corefacility.bioinformatics.irida.service.impl.analysis.submission AnalysisSubmissionServiceImpl)))

(do
  (use 'cl-java-introspector.spring)
  (use 'cl-java-introspector.core)
  (use 'clojure.reflect 'clojure.pprint 'clojure.java.javadoc)
  (use 'me.raynes.fs))

(defn sorted-beans
  ([]
   (->> (get-beans) sort))
  ([pattern]
   (->> (get-beans)
        sort
        (filter #(re-matches (re-pattern (str ".*" pattern ".*")) %)))))

(defn cfg-auth [^String role]
  (let [roles-array (into-array String [role])
        authorities (AuthorityUtils/createAuthorityList roles-array)
        auth (new UsernamePasswordAuthenticationToken "user" role authorities)
        ctx (SecurityContextHolder/getContext)]
    (.setAuthentication ctx auth)))
(defn as-admin [] (cfg-auth "ROLE_ADMIN"))
(def user-svc (get-bean "userServiceImpl"))
(.getUserByUsername user-svc "admin")
(sorted-beans "project")
(def proj-svc (get-bean "projectServiceImpl"))
(get-method-names proj-svc)
(.findAll proj-svc)

(import '(org.springframework.data.domain Sort Sort$Order))



(get-method-names Sort/DEFAULT_DIRECTION)
(get-method-names Sort$Order)


(as-admin)
(.findProjectsForUser proj-svc "" (int 1) (int 100) (Sort. [(Sort$Order. "id")]))
(def proj-page (.findAllProjects proj-svc "" (int 1) (int 100) (Sort. [(Sort$Order. "name")])))
(get-method-names proj-page)
(.hasContent proj-page)
(get-fields proj-page)



(sorted-beans)

(sorted-beans "Work")

(sorted-beans "user")



(get-method-names user-svc)



(.getUserByUsername user-svc "admin")


(def gi (get-bean "galaxyInstance"))

(identity gi)

(methods-info gi)

(.getGalaxyUrl gi)

(def hc (get-bean "historiesClient"))

(methods-info hc)


(def wfc (get-bean "workflowsClient"))

(methods-info wfc)


;IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowIdSingle);
;Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
;String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
;Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString);

(def iwfs (get-bean "iridaWorkflowsService"))

(methods-info iwfs)

(fields-info iwfs)

(.getRegisteredWorkflows iwfs)

(def all-wfs (:defaultWorkflowForAnalysis (into {} (get-fields iwfs))))

(def asm-wf (into {} (get-fields (->> all-wfs first))))

(identity asm-wf)

(def irida-wf-asm (.getIridaWorkflow iwfs (:value asm-wf)))

(defn recur-get-fields [x & {:keys [d max-d] :or {d 0 max-d 1}}]
  (let [m (into {} (get-fields x))
        ks (keys m)
        vs (vals m)]
    (prn d)
    (prn x)
    (prn m)
    (when-not (= d max-d)
      (map #(recur-get-fields % :d (inc d) :max-d max-d) vs))
    ))

(recur-get-fields irida-wf-asm)

(recur-get-fields irida-wf-asm :max-d 2)

(get-fields irida-wf-asm)

(.getWorkflows wfc)


(def jc   (.getJobsClient gi))

(methods-info jc)

(def h (first (.getHistories hc)))

(identity h)
(get-fields h)
(.getId h)
(def jobs (.getJobsForHistory jc (.getId h)))
(map get-fields jobs)
(def j (first (.getJobs jc)))
(get-fields j)
(methods-info j)
(.getCreated j)
(.getState j)
(.getState j)
(.getToolId j)
(get-fields j)
(.getId h)
(.getId j)
(def prov (.showProvenance hc (.getId h) (.getId j)))
(methods-info prov)
(.getStandardError prov)
(get-fields prov)
(.toString (.getParameters prov))


(type prov)


(def assi (get-bean "analysisSubmissionServiceImpl"))

(methods-info assi)

(get-method-names assi)

(as-admin)
(def anal-sub (.. assi findAll))
(def anal-sub (first anal-sub))

(identity anal-sub)
(methods-info anal-sub)
(get-fields anal-sub)

(defn filter-in [xs pattern]
  (let [re-p (re-pattern (str ".*" pattern ".*"))]
    (filter #(re-matches re-p %) xs)))

(filter-in (get-method-names anal-sub) "get")
(.getAnalysisDescription anal-sub)
(.getNamedParameters anal-sub)
(.getInputParameters anal-sub)

(.getAnalysisState anal-sub)
(.getWorkflowId anal-sub)
(def wf-id (.getRemoteWorkflowId anal-sub))
(def h-id (.getRemoteAnalysisId anal-sub))
(identity h-id)
(methods-info hc)
(get-fields (.showHistory hc h-id))

(def history (.showHistory hc h-id))
(identity history)
(def errors (get (.getStateIds history) "error"))
(map #(get-fields (.showDataset hc h-id %)) errors)

(methods-info jc)
(map get-fields
     (.getJobsForHistory jc h-id))
(def errored-jobs (filter #(= (.getState %) "error") (.getJobsForHistory jc h-id)))
(map get-fields errored-jobs)
(def errored-job (first errored-jobs))
(methods-info errored-job)
(get-fields errored-job)
(.getId errored-job)
(.getId history)
(def prov (.showProvenance hc (.getId history) (.getId errored-job)))

(get-fields history)
(filter-in (get-method-names history) "get")
(filter-in (get-method-names hc) "get")

(filter-in (get-beans) "galaxy")

(.showProvenance hc h-id "4")

; history_id, history content api id
(get-fields (.showProvenance hc "1cd8e2f6b131e891" "417e33144b294c21"))
(get-fields (.showProvenance hc "1cd8e2f6b131e891" "1cd8e2f6b131e891"))
(identity h-id)

(str (first (into #{} (map :parameters
                           (map #(into {}
                                       (get-fields (.showProvenance hc h-id %))) errors)))))

(map get-fields (.showHistoryContents hc h-id))

(filter-in (get-method-names (first (.showHistoryContents hc h-id))) "get")
(.getHid (first (.showHistoryContents hc h-id)))
(first (.showHistoryContents hc h-id))


(filter-in (get-method-names anal-sub) "get")
(.getCommandLine
  (.showJob jc (.getId errored-job)))
(get-fields errored-job)

(filter-in (get-method-names gi) "Tool")
(def tools-client (.getToolsClient gi))
(get-method-names tools-client)
(.getTools tools-client)

(get-fields errored-job)
(def tool (.showTool tools-client (.getToolId errored-job)))
(get-fields tool)

(.getInputParameters anal-sub)
(.getExecutionTimeParameters anal-sub)

(type prov)
(.getStandardError prov)
(.getStandardOutput prov)
(get-fields prov)
(methods-info gi)
(map #(.showHistory hc %) errors)

(methods-info gi)
(methods-info hc)
(get-fields (.showDataset hc h-id "c9468fdb6dc5c5f1"))
(def dsc ())



(.findAll assi)
(.listAllSubmissions assi)

(import '(net.matlux NreplServerSpring))

(def ctx (. NreplServerSpring/instance getApplicationContext))

(methods-info ctx)


(def user-svc (get-bean "userServiceImpl"))

(methods-info user-svc)

(def admin-user (.loadUserByUsername user-svc "admin"))

(get-fields admin-user)

(methods-info )

(get-fields ctx)

(get-fields NreplServerSpring/instance)

(methods-info NreplServerSpring/instance)

(import '(org.springframework.security.authentication UsernamePasswordAuthenticationToken))
(import '(org.springframework.security.core.authority AuthorityUtils))
(import '(org.springframework.security.core.context SecurityContextHolder))



(methods-info admin-user)

(.isEnabled admin-user)

(get-fields (first (.getAuthorities admin-user)))






