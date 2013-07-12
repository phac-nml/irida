/*
 * Copyright 2013 Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.corefacility.bioinformatics.irida.repositories.sesame;

import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.result.Result;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
//public class SequenceFileSesameRepository extends GenericRepository<Identifier, SequenceFile> implements SequenceFileRepository{
public class SequenceFileSesameRepository extends GenericRepository<Identifier, SequenceFile>{
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SequenceFileSesameRepository.class);
    
    private ProjectSesameRepository projectRepo;
    private SampleSesameRepository sampleRepo;
    private final RdfPredicate hasFile = new RdfPredicate("irida", "hasFile");
    
    public SequenceFileSesameRepository(){}
    
    public SequenceFileSesameRepository(TripleStore store,AuditRepository auditRepo,RelationshipSesameRepository linksRepo) {
        super(store,SequenceFile.class,SequenceFile.PREFIX,SequenceFile.TYPE,auditRepo,linksRepo);
    }
    
    public void setProjectRepository(ProjectSesameRepository projectRepo){
        this.projectRepo = projectRepo;
    }
    
    public void setSampleRepository(SampleSesameRepository sampleRepo){
        this.sampleRepo = sampleRepo;
    }    
    
    public List<SequenceFile> getFilesForContainer(String uri,Class type){
        List<SequenceFile> files = new ArrayList<>();

        ObjectConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT ?s "
                    + "WHERE{ ?p a ?type . \n"
                    + "?p "+hasFile.getSparqlNotation()+" ?s . \n"
                    + "}";

            ObjectQuery query = con.prepareObjectQuery(QueryLanguage.SPARQL, qs);
            URI puri = con.getValueFactory().createURI(uri);
            query.setBinding("p", puri);
            query.setType("type", type);

            Result<SequenceFile> result = query.evaluate(SequenceFile.class);
            while (result.hasNext()) {
                SequenceFile o = result.next();
                URI u = con.getValueFactory().createURI(o.toString());

                SequenceFile ret = buildObjectFromResult(o, u, con);
                files.add(ret);
            }
            result.close();

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't list files for project");
        } finally {
            store.closeRepoConnection(con);
        }

        return files;        
    }
    
    public void addFileToContainer(String cID, SequenceFile file){
        ObjectConnection con = store.getRepoConnection();
        
        java.net.URI fNetUri = idGen.buildURIFromIdentifier(file.getIdentifier(),URI);
        
        try{
            ValueFactory fac = con.getValueFactory();
            URI puri = fac.createURI(cID);
            URI hasFile = fac.createURI(con.getNamespace("irida"),"hasFile");
            URI furi = fac.createURI(fNetUri.toString());
            Statement st = fac.createStatement(puri, hasFile, furi);
            con.add(st);
            
            file.getAuditInformation().setUpdated(new Date());
            
            auditRepo.audit(file.getAuditInformation(), fNetUri.toString(),null);
            
        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't add a file to project");        
        }finally{
            store.closeRepoConnection(con);           
        }
    }

    @Override
    public SequenceFile update(Identifier id, Map<String, Object> updatedFields) throws InvalidPropertyException {
        if(updatedFields.containsKey("file")){
            Object field = updatedFields.get("file");
            
            Method declaredMethod;
            try {
                declaredMethod = SequenceFile.class.getDeclaredMethod("getIoFile");
            } catch (    NoSuchMethodException | SecurityException ex) {
                logger.error("No field file exists.  Cannot update object.");
                throw new InvalidPropertyException("No field named file exists for this object type");            
            }

            Iri annotation = declaredMethod.getAnnotation(Iri.class);

            logger.trace("Updating file -- " + annotation.value());

            updateField(id, annotation.value(), field);
            
            updatedFields.remove("file");
        }
        
        return super.update(id, updatedFields);
    }
    
    /**
     * Set a predicate binding for a {@link Query} based on a map of predicates.
     * This override will use the method invocation "getIoFile" for "file" and otherwise call super
     * @param fieldName The name of the field to set a binding for
     * @param fieldPredicates A Map<String,String> of predicate URIs for the fields
     * @param bindingName The name of the binding to set the predicate for 
     * @param query The query to set the binding for
     * @param fac A ValueVactory to use to create the URI
     */    
    @Override
    protected void setListBinding(String fieldName, Map<String, String> fieldPredicates, String bindingName, Query query, ValueFactory fac){
        if(fieldName.equals("file")){
            String predStr = fieldPredicates.get("getIoFile");
            URI pred = fac.createURI(predStr);
            query.setBinding(bindingName, pred);
        }
        else{
            super.setListBinding(fieldName, fieldPredicates, bindingName, query, fac);
        }
    }
    
    /**
     * Convert to the given class type from a string.
     * This override will convert for Path, and call super otherwise
     * @param stringValue The string value to convert
     * @param pClass The class to convert to
     * @return A new instance of an object of type pClass
     */    
    @Override
    protected Object convertFromString(String stringValue, Class pClass){
        Object value;
        if(pClass.equals(Path.class)){
            value = Paths.get(stringValue);
        }
        else{
            value = super.convertFromString(stringValue, pClass);
        }
        
        return value;
    }    
    
    
    
    @Override
    protected Literal createLiteral(ValueFactory fac,String predicate,Object obj){
        
        String fileAnnotation = "";
        try {
            Method declaredMethod = SequenceFile.class.getDeclaredMethod("getIoFile");
            Iri annotation = declaredMethod.getAnnotation(Iri.class);
            fileAnnotation = annotation.value();

        } catch (SecurityException | NoSuchMethodException ex) {
            Logger.getLogger(SequenceFileSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
               
        if(fileAnnotation.compareTo(predicate) == 0){
            URI ioFile = fac.createURI("java:java.io.File");
            return fac.createLiteral(obj.toString(),ioFile);
        }
        else{
            return super.createLiteral(fac, predicate, obj);
        }
    }    
    
    public List<SequenceFile> getFilesForProject(Project project){
        String uri = project.getIdentifier().getUri().toString();
        return getFilesForContainer(uri,Project.class);
    }
    
    public List<Identifier> listFilesForProject(Project project){
        return linksRepo.listObjects(project.getIdentifier(), hasFile);
    }
    
    public List<SequenceFile> getFilesForSample(Sample sample){
        String uri = sample.getIdentifier().getUri().toString();
        return getFilesForContainer(uri,Sample.class);    
    }    
    
    public void addFileToProject(Project project, SequenceFile file){
        
        /*java.net.URI pNetUri = projectRepo.buildURI(project.getIdentifier().getIdentifier());
        
        addFileToContainer(pNetUri.toString(), file);
        project.getAuditInformation().setUpdated(new Date());

        auditRepo.audit(project.getAuditInformation(), pNetUri.toString());*/
        
        Relationship l = new Relationship();
        l.setSubject(project);
        l.setObject(file);
        //l.setPredicate(hasFile);
        linksRepo.create(l);
        
    }
    
    public void addFileToSample(Sample sample, SequenceFile file){
        
        /*java.net.URI pNetUri = sampleRepo.buildURI(sample.getIdentifier().getIdentifier());
        
        addFileToContainer(pNetUri.toString(), file);
        sample.getAuditInformation().setUpdated(new Date());

        auditRepo.audit(sample.getAuditInformation(), pNetUri.toString());*/
        
        Relationship l = new Relationship();
        l.setSubject(sample);
        l.setObject(file);
        //l.setPredicate(hasFile);
        linksRepo.create(l);        
    }    
    
}
