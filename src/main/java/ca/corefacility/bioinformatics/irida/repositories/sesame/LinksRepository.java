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
import ca.corefacility.bioinformatics.irida.model.Link;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.StringIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class LinksRepository extends SesameRepository implements CRUDRepository<Identifier, Link>{
    
    public final String linkType = "http://corefacility.ca/irida/ResourceLink";
    AuditRepository auditRepo;
    
    public LinksRepository(TripleStore store,AuditRepository auditRepo) {
        super(store,"ResourceLink");
        this.auditRepo = auditRepo;
    }
    
    public StringIdentifier buildIdentiferFromBindingSet(BindingSet bs){
        StringIdentifier id = null;
        try {
            Value uri = bs.getValue("object");
            Value ident = bs.getValue("identifier");
            Value label = bs.getValue("label");
            id = new StringIdentifier();
            id.setIdentifier(ident.stringValue());
            id.setUri(new java.net.URI(uri.stringValue()));
            id.setLabel(label.stringValue());
        } catch (URISyntaxException ex) {
            Logger.getLogger(LinksRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return id;
    }
    
    public Identifier buildLinkIdentifier(URI uri,String identifiedBy) {
        Identifier objid = new Identifier();
        objid.setUri(java.net.URI.create(uri.toString()));
        objid.setIdentifier(identifiedBy);

        return objid;
    }     
    
    public Link create(Link link){
        Identifier subject = link.getSubject();
        Identifier object = link.getObject();
        RdfPredicate predicate = link.getRelationship();
        
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

            auditRepo.audit(link.getAuditInformation(), linkURI.toString());
            
            con.commit();
            
        } catch (RepositoryException ex) {
            Logger.getLogger(LinksRepository.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally{
            store.closeRepoConnection(con);
        }
        
        return link;
    }
    
    public Identifier getIdentiferForURI(URI uri){
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
            id = buildIdentiferFromBindingSet(bs);
        }
        catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(LinksRepository.class.getName()).log(Level.SEVERE, null, ex);
        }        
        finally{
            store.closeRepoConnection(con);
        }
        
        return id;
    }
    
   
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
                ids.add(buildIdentiferFromBindingSet(bs));
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(LinksRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ids;
    }
    
    public List<Link> getLinks(Identifier subjectId){
        List<Link> links = new ArrayList<>();
        
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
                Link link = buildLinkfromBindingSet(bs, con);
                link.setIdentifier(linkId);
                Audit audit = auditRepo.getAudit(uri);
                link.setAuditInformation(audit);
                
                links.add(link);
            }

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(LinksRepository.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return links;
    }
    
    private Link buildLinkfromBindingSet(BindingSet bs, ObjectConnection con){
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
        Link l = new Link();
        l.setSubject(subId);
        l.setObject(objId);
        
        l.setRelationship(pred);
        
        return l;
    }

    @Override
    public Link read(Identifier id) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Link update(Link object) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Identifier id) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Link> list() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Link> list(int page, int size, String sortProperty, Order order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean exists(Identifier id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer count() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
       
}
