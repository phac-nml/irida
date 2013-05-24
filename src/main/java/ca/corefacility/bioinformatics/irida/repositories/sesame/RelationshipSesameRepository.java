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

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.StringIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.RelationshipRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.DefaultLinks;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class RelationshipSesameRepository extends SesameRepository implements RelationshipRepository{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RelationshipSesameRepository.class);
    
    public final String linkType = "http://corefacility.ca/irida/ResourceLink";
    AuditRepository auditRepo;
    DefaultLinks linkList;
    
    public RelationshipSesameRepository(TripleStore store,AuditRepository auditRepo) {
        super(store,"ResourceLink");
        this.auditRepo = auditRepo;
        linkList = new DefaultLinks();
    }
    
    public <S extends IridaThing,O extends IridaThing> void addRelationship(Class subject,RdfPredicate pred,Class object){
        linkList.addLink(subject, pred, object);
    }
    
    /**
     * Build an identifier object from a link binding set
     * 
     * @param bs The <type>BindingSet</type> to construct the identifier from
     * @param bindingName The binding name of the subject from this binding set
     * @return A <type>StringIdentifier</type> for this binding set
     */
    public StringIdentifier buildIdentiferFromBindingSet(BindingSet bs,String bindingName){
        StringIdentifier id = null;
        try {
            Value uri = bs.getValue(bindingName);
            Value ident = bs.getValue("identifier");
            Value label = bs.getValue("label");
            id = new StringIdentifier();
            id.setIdentifier(ident.stringValue());
            id.setUri(new java.net.URI(uri.stringValue()));
            id.setLabel(label.stringValue());
        } catch (URISyntaxException ex) {
            Logger.getLogger(RelationshipSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return id;
    }
    
    /**
     * Build a link identifier from a given URI and identifier string
     * @param uri The URI to build from
     * @param identifiedBy The unique string for this identifier
     * @return A new instance of an Identifier
     */
    public Identifier buildLinkIdentifier(URI uri,String identifiedBy) {
        Identifier objid = new Identifier();
        objid.setUri(java.net.URI.create(uri.toString()));
        objid.setIdentifier(identifiedBy);

        return objid;
    }
    
    /**
     * {@inheritDoc}
     */ 
    @Override
    public <SubjectType extends IridaThing,ObjectType extends IridaThing> Relationship create(SubjectType subject, ObjectType object){
        
        RdfPredicate pred = linkList.getLink(subject.getClass(), object.getClass());
        Relationship link = new Relationship();
        
        link.setSubject((Identifier) subject.getIdentifier());
        link.setPredicate(pred);
        link.setObject((Identifier) object.getIdentifier());
        
        return create(link);
    }
    
    /**
     * {@inheritDoc}
     */     
    @Override
    public Relationship create(Relationship link){
        Identifier subject = link.getSubject();
        Identifier object = link.getObject();
        RdfPredicate predicate = link.getPredicate();
        
        java.net.URI subNetURI = getUriFromIdentifier(subject);
        java.net.URI objNetURI = getUriFromIdentifier(object);
        
        ObjectConnection con = store.getRepoConnection();
        ValueFactory fac = con.getValueFactory();
        
        try {
            con.begin();
            URI subURI = fac.createURI(subNetURI.toString());
            URI objURI = fac.createURI(objNetURI.toString());            
            URI pred = fac.createURI(con.getNamespace(predicate.prefix), predicate.name);
            
            Identifier identifier = generateNewIdentifier();
            link.setIdentifier(identifier);
            
            java.net.URI netURI = identifier.getUri();
            URI linkURI = fac.createURI(netURI.toString());
            
            Statement st = fac.createStatement(subURI, pred, objURI);
            con.add(st);
            
            URI rdftype = fac.createURI(con.getNamespace("rdf"), "type");
            URI type = fac.createURI(linkType);
            setIdentifiedBy(con, linkURI, identifier.getIdentifier());
            
            URI linkSubject = fac.createURI(con.getNamespace("irida"), "linkSubject");
            URI linkPredicate = fac.createURI(con.getNamespace("irida"), "linkPredicate");
            URI linkObject = fac.createURI(con.getNamespace("irida"), "linkObject");
            
            con.add(fac.createStatement(linkURI, rdftype, type));
            con.add(fac.createStatement(linkURI, linkSubject, subURI));
            con.add(fac.createStatement(linkURI, linkPredicate, pred));
            con.add(fac.createStatement(linkURI, linkObject, objURI));
            
            con.commit();
            
            auditRepo.audit(link.getAuditInformation(), linkURI.toString());
            
        } catch (RepositoryException ex) {
            Logger.getLogger(RelationshipSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally{
            store.closeRepoConnection(con);
        }
        
        return link;
    }
    
    /**
     * Get an identifier object for the given URI
     * @param uri The URI to retrieve and build an identifier for
     * @return A new Identifier instance
     */
    private Identifier getIdentiferForURI(URI uri){
        Identifier id = null;
        ObjectConnection con = store.getRepoConnection();
        try{
            String qs = store.getPrefixes()
                    + "SELECT ?object ?identifier ?label "
                    + "WHERE{ ?object irida:identifier ?identifier ;"
                    + " rdfs:label ?label .\n"
                    + "}";
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);

            query.setBinding("object", uri);
            TupleQueryResult results = query.evaluate();
            BindingSet bs = results.next();
            id = buildIdentiferFromBindingSet(bs,"object");
        }
        catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(RelationshipSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        }        
        finally{
            store.closeRepoConnection(con);
        }
        
        return id;
    }
    
   
    /**
     * {@inheritDoc}
     */ 
    @Override
    public List<Identifier> listObjects(Identifier subjectId, RdfPredicate predicate){
        
        List<Identifier> ids = new ArrayList<>();
        java.net.URI subNetURI = getUriFromIdentifier(subjectId);
        
        ObjectConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT ?object ?identifier ?label "
                    + "WHERE{ ?subject ?predicate ?object .\n"
                    + "?object irida:identifier ?identifier ;"
                    + " rdfs:label ?label .\n"
                    + "}";
            ValueFactory fac = con.getValueFactory();
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            
            URI subURI = fac.createURI(subNetURI.toString());
            URI predURI = fac.createURI(con.getNamespace(predicate.prefix), predicate.name);
            query.setBinding("subject", subURI);
            query.setBinding("predicate", predURI);
            TupleQueryResult results = query.evaluate();
            while(results.hasNext()){
                BindingSet bs = results.next();
                ids.add(buildIdentiferFromBindingSet(bs,"object"));
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(RelationshipSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ids;
    }
    
    /**
     * {@inheritDoc}
     */ 
    @Override
    public List<Identifier> listSubjects(Identifier objectId, RdfPredicate predicate){
        
        List<Identifier> ids = new ArrayList<>();
        java.net.URI objNetUri = getUriFromIdentifier(objectId);
        
        ObjectConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT ?subject ?identifier ?label "
                    + "WHERE{ ?subject ?predicate ?object .\n"
                    + "?subject irida:identifier ?identifier ;"
                    + " rdfs:label ?label .\n"
                    + "}";
            ValueFactory fac = con.getValueFactory();
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            
            URI objURI = fac.createURI(objNetUri.toString());
            URI predURI = fac.createURI(con.getNamespace(predicate.prefix), predicate.name);
            query.setBinding("object", objURI);
            query.setBinding("predicate", predURI);
            TupleQueryResult results = query.evaluate();
            while(results.hasNext()){
                BindingSet bs = results.next();
                ids.add(buildIdentiferFromBindingSet(bs,"subject"));
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(RelationshipSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ids;
    }
    
    /**
     * {@inheritDoc}
     */ 
    @Override
    public List<Identifier> listLinks(Identifier id, Class subjectType,Class objectType){
        RdfPredicate pred = linkList.getLink(subjectType, objectType);
        
        return listObjects(id, pred);
    }    
    
    /**
     * {@inheritDoc}
     */ 
    @Override
    public List<Relationship> getLinks(Identifier subjectId, Class subjectType, Class objectType){
        RdfPredicate pred = linkList.getLink(subjectType, objectType);
        
        return getLinks(subjectId, pred);        
    }
    
    /**
     * {@inheritDoc}
     */ 
    @Override
    public List<Relationship> getLinks(Identifier subjectId, RdfPredicate predicate){
        List<Relationship> links = new ArrayList<>();
        
        java.net.URI subNetURI = getUriFromIdentifier(subjectId);
        
        ObjectConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes() +
                    "SELECT ?link ?sub ?pred ?obj " +
                    "WHERE{ " +
                    "?link a irida:ResourceLink ; " +
                    " irida:linkPredicate ?linkPred ; " +
                    " irida:linkElement ?sub ; " +
                    " irida:linkElement ?obj . " +
                    "?sub ?pred ?obj. " +
                    "OPTIONAL{ " +
                    "?linkPred owl:inverseOf ?inv " +
                    "} " +
                    "FILTER(?pred IN (?linkPred,?inv)). " +
                    "}";
            ValueFactory fac = con.getValueFactory();
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            
            URI subURI = fac.createURI(subNetURI.toString());
            query.setBinding("sub", subURI);
            URI predURI = fac.createURI(con.getNamespace(predicate.prefix), predicate.name);
            query.setBinding("pred", predURI);
            
            TupleQueryResult results = query.evaluate();
            while(results.hasNext()){
                BindingSet bs = results.next();
                
                String uristr = bs.getValue("link").stringValue();
                URI uri = fac.createURI(uristr);
                String identifiedBy = getIdentifiedBy(con, uri);
                
                Identifier linkId = buildLinkIdentifier(uri,identifiedBy);
                Relationship link = buildLinkfromBindingSet(bs, con);
                link.setIdentifier(linkId);
                Audit audit = auditRepo.getAudit(uri.toString());
                link.setAuditInformation(audit);
                
                links.add(link);
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(RelationshipSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return links;
    }
    
    @Override
    public List<Relationship> getLinks(Identifier subjectId, RdfPredicate predicate, Identifier objectId){
        if(subjectId==null && predicate==null && objectId==null){
            throw new IllegalArgumentException("subjectId, predicate, and objectId cannot all be null");
        }
        
        List<Relationship> links = new ArrayList<>();
        
        ObjectConnection con = store.getRepoConnection();
        try {
            /*String qs = store.getPrefixes()
                    + "SELECT ?link ?sub ?pred ?obj "
                    + "WHERE{ ?link a irida:ResourceLink ;\n"
                    + " irida:linkSubject ?sub ; \n"
                    + " irida:linkPredicate ?pred ;\n"
                    + " irida:linkObject ?obj ."
                    + "}";*/
            
            String qs = store.getPrefixes() + 
                    "SELECT ?link ?sub ?pred ?obj " +
                    "WHERE{ " +
                    "?link a irida:ResourceLink ; " +
                    " irida:linkPredicate ?linkPred ; " +
                    " irida:linkElement ?sub ; " +
                    " irida:linkElement ?obj . " +
                    "?sub ?pred ?obj. " +
                    "OPTIONAL{ " +
                    "?linkPred owl:inverseOf ?inv " +
                    "} " +
                    "FILTER(?pred IN (?linkPred,?inv)). " +
                    "}";
            ValueFactory fac = con.getValueFactory();
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            
            if(subjectId != null){
                java.net.URI subNetURI = getUriFromIdentifier(subjectId);
                URI subURI = fac.createURI(subNetURI.toString());
                query.setBinding("sub", subURI);
            }
            
            if(predicate != null){
                URI predURI = fac.createURI(con.getNamespace(predicate.prefix), predicate.name);
                query.setBinding("pred", predURI);                
            }
            
            if(objectId != null){
                java.net.URI objNetURI = getUriFromIdentifier(objectId);
                URI objURI = fac.createURI(objNetURI.toString());
                query.setBinding("obj", objURI);                
            }
            
            TupleQueryResult results = query.evaluate();
            while(results.hasNext()){
                BindingSet bs = results.next();
                
                String uristr = bs.getValue("link").stringValue();
                URI uri = fac.createURI(uristr);
                String identifiedBy = getIdentifiedBy(con, uri);
                
                Identifier linkId = buildLinkIdentifier(uri,identifiedBy);
                Relationship link = buildLinkfromBindingSet(bs, con);
                link.setIdentifier(linkId);
                Audit audit = auditRepo.getAudit(uri.toString());
                link.setAuditInformation(audit);
                
                links.add(link);
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(RelationshipSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return links;        
    }
    
    @Override
    public List<Relationship> getSubjectLinks(Identifier objectId, RdfPredicate predicate){
        List<Relationship> links = new ArrayList<>();
        
        java.net.URI subNetURI = getUriFromIdentifier(objectId);
        
        ObjectConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT ?link ?sub ?pred ?obj "
                    + "WHERE{ ?link a irida:ResourceLink ;\n"
                    + " irida:linkSubject ?sub ; \n"
                    + " irida:linkPredicate ?pred ;\n"
                    + " irida:linkObject ?obj ."
                    + "}";
            ValueFactory fac = con.getValueFactory();
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            
            URI subURI = fac.createURI(subNetURI.toString());
            query.setBinding("obj", subURI);
            URI predURI = fac.createURI(con.getNamespace(predicate.prefix), predicate.name);
            query.setBinding("pred", predURI);
            
            TupleQueryResult results = query.evaluate();
            while(results.hasNext()){
                BindingSet bs = results.next();
                
                String uristr = bs.getValue("link").stringValue();
                URI uri = fac.createURI(uristr);
                String identifiedBy = getIdentifiedBy(con, uri);
                
                Identifier linkId = buildLinkIdentifier(uri,identifiedBy);
                Relationship link = buildLinkfromBindingSet(bs, con);
                link.setIdentifier(linkId);
                Audit audit = auditRepo.getAudit(uri.toString());
                link.setAuditInformation(audit);
                
                links.add(link);
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(RelationshipSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return links;
    }  

    /**
     * {@inheritDoc}
     */ 
    @Override
    public List<Relationship> getLinks(Identifier subjectId){
        List<Relationship> links = new ArrayList<>();
        
        java.net.URI subNetURI = getUriFromIdentifier(subjectId);
        
        ObjectConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT ?link ?sub ?pred ?obj "
                    + "WHERE{ ?link a irida:ResourceLink ;\n"
                    + " irida:linkSubject ?sub ; \n"
                    + " irida:linkPredicate ?pred ;\n"
                    + " irida:linkObject ?obj ."
                    + "}";
            ValueFactory fac = con.getValueFactory();
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            
            URI subURI = fac.createURI(subNetURI.toString());
            query.setBinding("sub", subURI);
            
            TupleQueryResult results = query.evaluate();
            while(results.hasNext()){
                BindingSet bs = results.next();
                
                String uristr = bs.getValue("link").stringValue();
                URI uri = fac.createURI(uristr);
                String identifiedBy = getIdentifiedBy(con, uri);
                
                Identifier linkId = buildLinkIdentifier(uri,identifiedBy);
                Relationship link = buildLinkfromBindingSet(bs, con);
                link.setIdentifier(linkId);
                Audit audit = auditRepo.getAudit(uri.toString());
                link.setAuditInformation(audit);
                
                links.add(link);
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(RelationshipSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return links;
    }
    
    private Relationship buildLinkfromBindingSet(BindingSet bs, ObjectConnection con){
        ValueFactory fac = con.getValueFactory();
        
        String substr = bs.getValue("sub").stringValue();
        URI subURI = fac.createURI(substr);
        
        String objstr = bs.getValue("obj").stringValue();
        URI objURI = fac.createURI(objstr);
        
        String predstr = bs.getValue("pred").stringValue();
        URI predURI = fac.createURI(predstr);

        RdfPredicate pred = new RdfPredicate(predURI.getNamespace(), predURI.getLocalName());
        
        Identifier subId = getIdentiferForURI(subURI);
        Identifier objId = getIdentiferForURI(objURI);
        Relationship l = new Relationship();
        l.setSubject(subId);
        l.setObject(objId);
        
        l.setPredicate(pred);
        
        return l;
    }

    @Override
    public Relationship read(Identifier id) throws EntityNotFoundException {
        Relationship ret = null;
        
        java.net.URI linkNetURI = getUriFromIdentifier(id);
        
        ObjectConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT ?link ?sub ?pred ?obj "
                    + "WHERE{ ?link a irida:ResourceLink ;\n"
                    + " irida:linkSubject ?sub ; \n"
                    + " irida:linkPredicate ?pred ;\n"
                    + " irida:linkObject ?obj ."
                    + "}";
            ValueFactory fac = con.getValueFactory();
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            
            URI subURI = fac.createURI(linkNetURI.toString());
            query.setBinding("link", subURI);
            
            TupleQueryResult results = query.evaluate();
            if(results.hasNext()){
                BindingSet bs = results.next();
                
                String uristr = bs.getValue("link").stringValue();
                URI uri = fac.createURI(uristr);
                String identifiedBy = getIdentifiedBy(con, uri);
                
                Identifier linkId = buildLinkIdentifier(uri,identifiedBy);
                ret = buildLinkfromBindingSet(bs, con);
                ret.setIdentifier(linkId);
                Audit audit = auditRepo.getAudit(uri.toString());
                ret.setAuditInformation(audit);
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(RelationshipSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        return ret;
    }

    @Override
    public void delete(Identifier id) throws EntityNotFoundException {
        if (!exists(id)) {
            throw new EntityNotFoundException("Object does not exist in the database.");            
        }
        
        ObjectConnection con = store.getRepoConnection();

        java.net.URI netURI = buildURIFromIdentifier(id);
        String uri = netURI.toString();

        ValueFactory vf = con.getValueFactory();
        URI objecturi = vf.createURI(uri);

        try {
            con.remove(objecturi, null, null);
            con.close();

        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to remove object" + id);
        } finally {
            store.closeRepoConnection(con);
        }        
    }

    @Override
    public List<Relationship> list() {
        throw new UnsupportedOperationException("Listing links will not be supported.");
    }

    @Override
    public List<Relationship> list(int page, int size, String sortProperty, Order order) {
        throw new UnsupportedOperationException("Listing links will not be supported.");
    }

    @Override
    public Boolean exists(Identifier id) {
        throw new UnsupportedOperationException("Checking existance of a link will not be supported");
    }

    @Override
    public Integer count() {
        throw new UnsupportedOperationException("Counting links will not be supported.");
    }

    @Override
    public Relationship update(Identifier id, Map<String, Object> updatedFields) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<Relationship> readMultiple(Collection<Identifier> idents) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
       
}
